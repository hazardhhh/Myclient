package com.example.client.utils.cache;

import android.text.TextUtils;

import com.example.client.utils.PreferencesUcStar;
import com.example.client.utils.UIKitLogTag;
import com.ucstar.android.sdk.Observer;
import com.ucstar.android.sdk.UcSTARSDKClient;
import com.ucstar.android.sdk.friend.FriendService;
import com.ucstar.android.sdk.friend.FriendServiceObserve;
import com.ucstar.android.sdk.friend.model.BlackListChangedNotify;
import com.ucstar.android.sdk.friend.model.Friend;
import com.ucstar.android.sdk.friend.model.FriendChangedNotify;
import com.ucstar.android.sdk.msg.MsgService;
import com.ucstar.android.sdk.msg.constant.SessionTypeEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;


/**
 * 好友关系缓存
 * 注意：获取通讯录列表即是根据Friend列表帐号，去取对应的UserInfo
 * <p/>
 * Created by ucstar on 2015/9/14.
 */
public class FriendDataCache {

    public static com.example.client.utils.cache.FriendDataCache getInstance() {
        return InstanceHolder.instance;
    }

    /**
     * 属性
     */
    private Set<String> friendAccountSet = new CopyOnWriteArraySet<>();

    private Map<String, Friend> friendMap = new ConcurrentHashMap<>();

    private List<FriendDataChangedObserver> friendObservers = new ArrayList<>();

    /**
     * 初始化&清理
     */

    public void clear() {
        clearFriendCache();
    }

    public void buildCache() {
        // 获取我所有的好友关系
        List<Friend> friends = UcSTARSDKClient.getService(FriendService.class).getFriends();
        if(friends!=null) {
            for (Friend f : friends) {
                friendMap.put(f.getAccount(), f);
            }
        }

        // 获取我所有好友的帐号
        List<String> accounts = UcSTARSDKClient.getService(FriendService.class).getFriendAccounts();
        if (accounts == null || accounts.isEmpty()) {
            UcSTARSDKClient.getService(FriendService.class).fetchFriends();
            return;
        }

        // 排除黑名单
        List<String> blacks = UcSTARSDKClient.getService(FriendService.class).getBlackList();
        accounts.removeAll(blacks);

        // 排除掉自己
        accounts.remove(PreferencesUcStar.getUserAccount());

        // 确定缓存
        friendAccountSet.addAll(accounts);

//        LogUtil.i(UIKitLogTag.FRIEND_CACHE, "build FriendDataCache completed, friends count = " + friendAccountSet.size());
    }

    private void clearFriendCache() {
        friendAccountSet.clear();
        friendMap.clear();
    }

    /**
     * ****************************** 好友查询接口 ******************************
     */

    public List<String> getMyFriendAccounts() {
        List<String> accounts = new ArrayList<>(friendAccountSet.size());
        accounts.addAll(friendAccountSet);

        return accounts;
    }

    public int getMyFriendCounts() {
        return friendAccountSet.size();
    }

    public Friend getFriendByAccount(String account) {
        if (TextUtils.isEmpty(account)) {
            return null;
        }

        return friendMap.get(account);
    }

    public boolean isMyFriend(String account) {
        return friendAccountSet.contains(account);
    }

    /**
     * ****************************** 缓存好友关系变更监听&通知 ******************************
     */

    /**
     * 缓存监听SDK
     */
    public void registerObservers(boolean register) {
        UcSTARSDKClient.getService(FriendServiceObserve.class).observeFriendChangedNotify(friendChangedNotifyObserver, register);
        UcSTARSDKClient.getService(FriendServiceObserve.class).observeBlackListChangedNotify(blackListChangedNotifyObserver, register);
    }

    /**
     * APP监听缓存
     */
    public void registerFriendDataChangedObserver(FriendDataChangedObserver o, boolean register) {
        if (o == null) {
            return;
        }

        if (register) {
            if (!friendObservers.contains(o)) {
                friendObservers.add(o);
            }
        } else {
            friendObservers.remove(o);
        }
    }

    public interface FriendDataChangedObserver {
        void onAddedOrUpdatedFriends(List<String> accounts);

        void onDeletedFriends(List<String> accounts);

        void onAddUserToBlackList(List<String> account);

        void onRemoveUserFromBlackList(List<String> account);
    }

    /**
     * 监听好友关系变化
     */
    private Observer<FriendChangedNotify> friendChangedNotifyObserver = new Observer<FriendChangedNotify>() {
        @Override
        public void onEvent(FriendChangedNotify friendChangedNotify) {
            List<Friend> addedOrUpdatedFriends = friendChangedNotify.getAddedOrUpdatedFriends();
            List<String> myFriendAccounts = new ArrayList<>(addedOrUpdatedFriends.size());
            List<String> friendAccounts = new ArrayList<>(addedOrUpdatedFriends.size());
            List<String> deletedFriendAccounts = friendChangedNotify.getDeletedFriends();

            // 如果在黑名单中，那么不加到好友列表中
            String account;
            for (Friend f : addedOrUpdatedFriends) {
                account = f.getAccount();
                friendMap.put(account, f);
                friendAccounts.add(account);

                if (UcSTARSDKClient.getService(FriendService.class).isInBlackList(account)) {
                    continue;
                }

                myFriendAccounts.add(account);
            }

            // 更新我的好友关系
            if (!myFriendAccounts.isEmpty()) {
                // update cache
                friendAccountSet.addAll(myFriendAccounts);

                // log
                DataCacheManager.Log(myFriendAccounts, "on add friends", UIKitLogTag.FRIEND_CACHE);
            }

            // 通知好友关系更新
            if (!friendAccounts.isEmpty()) {
                for (FriendDataChangedObserver o : friendObservers) {
                    o.onAddedOrUpdatedFriends(friendAccounts);
                }
            }

            // 处理被删除的好友关系
            if (!deletedFriendAccounts.isEmpty()) {
                // update cache
                friendAccountSet.removeAll(deletedFriendAccounts);

                for (String a : deletedFriendAccounts) {
                    friendMap.remove(a);
                }

                // log
                DataCacheManager.Log(deletedFriendAccounts, "on delete friends", UIKitLogTag.FRIEND_CACHE);

                // notify
                for (FriendDataChangedObserver o : friendObservers) {
                    o.onDeletedFriends(deletedFriendAccounts);
                }
            }
        }
    };

    /**
     * 监听黑名单变化(决定是否加入或者移出好友列表)
     */
    private Observer<BlackListChangedNotify> blackListChangedNotifyObserver = new Observer<BlackListChangedNotify>() {
        @Override
        public void onEvent(BlackListChangedNotify blackListChangedNotify) {
            List<String> addedAccounts = blackListChangedNotify.getAddedAccounts();
            List<String> removedAccounts = blackListChangedNotify.getRemovedAccounts();

            if (!addedAccounts.isEmpty()) {
                // 拉黑，即从好友名单中移除
                friendAccountSet.removeAll(addedAccounts);

                // log
                DataCacheManager.Log(addedAccounts, "on add users to black list", UIKitLogTag.FRIEND_CACHE);

                // notify
                for (FriendDataChangedObserver o : friendObservers) {
                    o.onAddUserToBlackList(addedAccounts);
                }

                // 拉黑，要从最近联系人列表中删除该好友
                for (String account : addedAccounts) {
                    UcSTARSDKClient.getService(MsgService.class).deleteRecentContact2(account, SessionTypeEnum.P2P);
                }
            }

            if (!removedAccounts.isEmpty()) {
                // 移出黑名单，判断是否加入好友名单
                for (String account : removedAccounts) {
                    if (UcSTARSDKClient.getService(FriendService.class).isMyFriend(account)) {
                        friendAccountSet.add(account);
                    }
                }

                // log
                DataCacheManager.Log(removedAccounts, "on remove users from black list", UIKitLogTag.FRIEND_CACHE);

                // 通知观察者
                for (FriendDataChangedObserver o : friendObservers) {
                    o.onRemoveUserFromBlackList(removedAccounts);
                }
            }
        }
    };

    /**
     * ************************************ 单例 **********************************************
     */

    static class InstanceHolder {
        final static com.example.client.utils.cache.FriendDataCache instance = new com.example.client.utils.cache.FriendDataCache();
    }
}
