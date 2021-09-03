package com.example.client.utils.cache;

import android.text.TextUtils;
import android.util.Log;

import com.example.client.utils.PreferencesUcStar;
import com.example.client.utils.UIKitLogTag;
import com.example.client.utils.UcstarUIKit;
import com.example.client.utils.log.LogUtil;
import com.ucstar.android.SDKGlobal;
import com.ucstar.android.log.LogWrapper;
import com.ucstar.android.message.SenderNickCache;
import com.ucstar.android.sdk.Observer;
import com.ucstar.android.sdk.RequestCallback;
import com.ucstar.android.sdk.RequestCallbackWrapper;
import com.ucstar.android.sdk.ResponseCode;
import com.ucstar.android.sdk.UcSTARSDKClient;
import com.ucstar.android.sdk.friend.model.Friend;
import com.ucstar.android.sdk.uinfo.UserService;
import com.ucstar.android.sdk.uinfo.UserServiceObserve;
import com.ucstar.android.sdk.uinfo.model.UcSTARUserInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用户资料数据缓存，适用于用户体系使用UcSTAR用户资料托管
 * 注册缓存变更通知，请使用UserInfoHelper的registerObserver方法
 * Created by ucstar on 2015/8/20.
 */
public class UcUserInfoCache {

    public static com.example.client.utils.cache.UcUserInfoCache getInstance() {
        return InstanceHolder.instance;
    }

    private Map<String, UcSTARUserInfo> account2UserMap = new ConcurrentHashMap<>();

    private Map<String, List<RequestCallback<UcSTARUserInfo>>> requestUserInfoMap = new ConcurrentHashMap<>(); // 重复请求处理

    /**
     * 构建缓存与清理
     */
    public void buildCache() {
        List<UcSTARUserInfo> users = UcSTARSDKClient.getService(UserService.class).getAllUserInfo();
        addOrUpdateUsers(users, false);
        LogUtil.i(UIKitLogTag.USER_CACHE, "build NimUserInfoCache completed, users count = " + account2UserMap.size());
        if (!TextUtils.isEmpty(SDKGlobal.currAccount())) {
            List<String> userLst = new ArrayList<>();
            userLst.add(SDKGlobal.currAccount());
            UcSTARSDKClient.getService(UserService.class).fetchUserInfo(userLst);
        }
    }

    public void clear() {
        clearUserCache();
    }

    /**
     * 从UcSTAR服务器获取用户信息（重复请求处理）[异步]
     */
    public void getUserInfoFromRemote(final String account, final RequestCallback<UcSTARUserInfo> callback) {
        if (TextUtils.isEmpty(account)) {
            return;
        }

        if (requestUserInfoMap.containsKey(account)) {
            if (callback != null) {
                requestUserInfoMap.get(account).add(callback);
            }
            return; // 已经在请求中，不要重复请求
        } else {
            List<RequestCallback<UcSTARUserInfo>> cbs = new ArrayList<>();
            if (callback != null) {
                cbs.add(callback);
            }
            requestUserInfoMap.put(account, cbs);
        }

        List<String> accounts = new ArrayList<>(1);
        accounts.add(account);

        UcSTARSDKClient.getService(UserService.class).fetchUserInfo(accounts).setCallback(new RequestCallbackWrapper<List<UcSTARUserInfo>>() {

            @Override
            public void onResult(int code, List<UcSTARUserInfo> users, Throwable exception) {
                if (exception != null) {
                    if (callback == null){
                        LogWrapper.err("NimUserInfoCache","callback is null !!");
                        return;
                    }
                    callback.onException(exception);
                    return;
                }

                UcSTARUserInfo user = null;
                boolean hasCallback = requestUserInfoMap.get(account).size() > 0;
                if (code == ResponseCode.RES_SUCCESS && users != null && !users.isEmpty()) {
                    user = users.get(0);
                    // 这里不需要更新缓存，由监听用户资料变更（添加）来更新缓存
                }

                // 处理回调
                if (hasCallback) {
                    List<RequestCallback<UcSTARUserInfo>> cbs = requestUserInfoMap.get(account);
                    for (RequestCallback<UcSTARUserInfo> cb : cbs) {
                        if (code == ResponseCode.RES_SUCCESS) {
                            cb.onSuccess(user);
                        } else {
                            cb.onFailed(code);
                        }
                    }
                }

                requestUserInfoMap.remove(account);
            }
        });
    }

    /**
     * 从UcSTAR服务器获取批量用户信息[异步]
     */
    public void getUserInfoFromRemote(List<String> accounts, final RequestCallback<List<UcSTARUserInfo>> callback) {
        UcSTARSDKClient.getService(UserService.class).fetchUserInfo(accounts).setCallback(new RequestCallback<List<UcSTARUserInfo>>() {
            @Override
            public void onSuccess(List<UcSTARUserInfo> users) {
                Log.i(UIKitLogTag.USER_CACHE, "fetch userInfo completed, add users size =" + users.size());
                // 这里不需要更新缓存，由监听用户资料变更（添加）来更新缓存
                if (callback != null) {
                    callback.onSuccess(users);
                }
            }

            @Override
            public void onFailed(int code) {
                if (callback != null) {
                    callback.onFailed(code);
                }
            }

            @Override
            public void onException(Throwable exception) {
                if (callback != null) {
                    callback.onException(exception);
                }
            }
        });
    }

    /**
     * ******************************* 业务接口（获取缓存的用户信息） *********************************
     */

    public List<UcSTARUserInfo> getAllUsersOfMyFriend() {
        List<String> accounts = FriendDataCache.getInstance().getMyFriendAccounts();
        List<UcSTARUserInfo> users = new ArrayList<>();
        List<String> unknownAccounts = new ArrayList<>();
        for (String account : accounts) {
            if (hasUser(account)) {
                users.add(getUserInfo(account));
            } else {
                unknownAccounts.add(account);
            }
        }

        // fetch unknown userInfo，根本不会发生，再次仅作测试校验，可以删去
        if (!unknownAccounts.isEmpty()) {
            DataCacheManager.Log(unknownAccounts, "lack friend userInfo", UIKitLogTag.USER_CACHE);
            getUserInfoFromRemote(unknownAccounts, null);
        }

        return users;
    }

    public UcSTARUserInfo getUserInfo(String account) {
        if (TextUtils.isEmpty(account) || account2UserMap == null) {
            LogUtil.e(UIKitLogTag.USER_CACHE, "getUserInfo null, account=" + account + ", account2UserMap=" + account2UserMap);
            return null;
        }

        return account2UserMap.get(account);
    }

    public boolean hasUser(String account) {
        if (TextUtils.isEmpty(account) || account2UserMap == null) {
            LogUtil.e(UIKitLogTag.USER_CACHE, "hasUser null, account=" + account + ", account2UserMap=" + account2UserMap);
            return false;
        }

        return account2UserMap.containsKey(account);
    }

    /**
     * 获取用户显示名称。
     * 若设置了备注名，则显示备注名。
     * 若没有设置备注名，用户有昵称则显示昵称，用户没有昵称则显示帐号。
     *
     * @param account 用户帐号
     * @return
     */
    public String getUserDisplayName(String account) {
        String alias = getAlias(account);
        if (!TextUtils.isEmpty(alias)) {
            return alias;
        }
        alias = SenderNickCache.get().getNick(account);
        if (!TextUtils.isEmpty(alias)) {
            return alias;
        }
        return getUserName(account);
    }


    public String getUserDisplayNameWithoutAccount(String account) {
        String alias = getAlias(account);
        if (!TextUtils.isEmpty(alias)) {
            return alias;
        }
        alias = SenderNickCache.get().getNick(account);
        if (!TextUtils.isEmpty(alias)) {
            return alias;
        }
        UcSTARUserInfo userInfo = getUserInfo(account);
        if (userInfo != null && !TextUtils.isEmpty(userInfo.getName())) {
            return userInfo.getName();
        }
        return null;
    }

    public String getAlias(String account) {
        Friend friend = FriendDataCache.getInstance().getFriendByAccount(account);
        if (friend != null && !TextUtils.isEmpty(friend.getAlias())) {
            return friend.getAlias();
        }
        return null;
    }

    // 获取用户原本的昵称
    public String getUserName(String account) {
        UcSTARUserInfo userInfo = getUserInfo(account);
        if (userInfo != null && !TextUtils.isEmpty(userInfo.getName())) {
            return userInfo.getName();
        } else {
            return account;
        }
    }

    public String getUserDisplayNameEx(String account) {
        if (account.equals(PreferencesUcStar.getUserAccount())) {
            return "我";
        }

        return getUserDisplayName(account);
    }

    public String getUserDisplayNameYou(String account) {
        if (account.equals(PreferencesUcStar.getUserAccount())) {
            return "你";  // 若为用户自己，显示“你”
        }

        return getUserDisplayName(account);
    }


    public String getUserDisplayNameOnlyMoreTwoName(String account) {

       String showName= getUserDisplayName(account);
       if(!TextUtils.isEmpty(showName)&&showName.equals(account)){
           return showName;
       }
       if(!TextUtils.isEmpty(showName)){
           if(showName.length()>=3){
               showName=showName.substring(showName.length()-2);
           }
       }
       return  showName;
    }


    public String getUserDisplayNameOnlyMoreTwoName2(String account) {

        String showName= getUserDisplayName(account);
        if(!TextUtils.isEmpty(showName)){
            if(showName.length()>=3){
                showName=showName.substring(showName.length()-2);
            }
        }
        return  showName;
    }



    private void clearUserCache() {
        account2UserMap.clear();
    }

    /**
     * ************************************ 用户资料变更监听(监听SDK) *****************************************
     */

    /**
     * 在Application的onCreate中向SDK注册用户资料变更观察者
     */
    public void registerObservers(boolean register) {
        UcSTARSDKClient.getService(UserServiceObserve.class).observeUserInfoUpdate(userInfoUpdateObserver, register);
    }

    private Observer<List<UcSTARUserInfo>> userInfoUpdateObserver = new Observer<List<UcSTARUserInfo>>() {
        @Override
        public void onEvent(List<UcSTARUserInfo> users) {
            if (users == null || users.isEmpty()) {
                return;
            }
            addOrUpdateUsers(users, true);
        }
    };

    /**
     * *************************************** User缓存管理与变更通知 ********************************************
     */

    private void addOrUpdateUsers(final List<UcSTARUserInfo> users, boolean notify) {
        if (users == null || users.isEmpty()) {
            return;
        }


        // update cache
        for (UcSTARUserInfo u : users) {
            account2UserMap.put(u.getAccount(), u);
        }

        // log
        List<String> accounts = getAccounts(users);
        DataCacheManager.Log(accounts, "on userInfo changed", UIKitLogTag.USER_CACHE);

        // 通知变更
        if (notify && accounts != null && !accounts.isEmpty()) {
            UcstarUIKit.notifyUserInfoChanged(accounts); // 通知到UI组件
        }
    }

    private List<String> getAccounts(List<UcSTARUserInfo> users) {
        if (users == null || users.isEmpty()) {
            return null;
        }

        List<String> accounts = new ArrayList<>(users.size());
        for (UcSTARUserInfo user : users) {
            accounts.add(user.getAccount());
        }

        return accounts;
    }

    /**
     * ************************************ 单例 **********************************************
     */

    static class InstanceHolder {
        final static com.example.client.utils.cache.UcUserInfoCache instance = new com.example.client.utils.cache.UcUserInfoCache();
    }

    public void putUser(UcSTARUserInfo userInfo) {
        account2UserMap.put(userInfo.getAccount(), userInfo);
    }
}
