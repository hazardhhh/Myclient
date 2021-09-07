package com.example.client.utils;

import android.content.Context;
import android.text.TextUtils;

import com.example.client.utils.PreferencesUcStar;
import com.example.client.utils.cache.DataCacheManager;
import com.example.client.utils.uinfo.UserInfoHelper;
import com.ucstar.android.SDKGlobal;
import com.ucstar.android.SDKSharedPreferences;
import com.ucstar.android.log.LogWrapper;
import com.ucstar.android.p64m.SDKTimeManager;
import com.ucstar.android.p64m.p70c.p72b.UcSTARClusterAgent;
import com.ucstar.android.sdk.AbortableFuture;
import com.ucstar.android.sdk.RequestCallback;
import com.ucstar.android.sdk.UcSTARSDKClient;
import com.ucstar.android.sdk.auth.AuthService;
import com.ucstar.android.sdk.auth.LoginInfo;
import com.ucstar.android.sdk.uinfo.UserInfoProvider;
import com.ucstar.android.util.JWTUtil;

import org.json.JSONObject;

import java.util.List;

/**
 * UIKit能力输出类。
 */
public class UcstarUIKit {

    // context
    private static Context context;

    // 用户信息提供者
    private static UserInfoProvider userInfoProvider;

    // 图片加载、缓存与管理组件
    private static ImageLoaderKit imageLoaderKit;

    public static Context getContext() {
        return context;
    }

    public static void initBiz() {
        DataCacheManager.buildDataCacheAsync(); // build data cache on auto login
    }

    /**
     * 手动登陆，由于手动登陆完成之后，UIKit 需要设置账号、构建缓存等，使用此方法登陆 UIKit 会将这部分逻辑处理好，开发者只需要处理自己的逻辑即可
     *
     * @param callback  登陆结果回调
     */
    public static AbortableFuture<LoginInfo> doLoginByTokenInfo(String serverUrl, String account, String tokenInfo, final RequestCallback<LoginInfo> callback) {
        try {
            JSONObject jsonObject = new JSONObject(tokenInfo);
            String access_token = jsonObject.getString("access_token");
            String refresh_token = jsonObject.getString("refresh_token");
            String accessStr = jsonObject.getString("access_url");
            JSONObject commonObj = new JSONObject(accessStr).getJSONObject("common");
            String link = commonObj.getString("link");
            String[] linkSplit = link.split(",");
            String linkStr = "";
            if (linkSplit != null && linkSplit.length > 0 && linkSplit[0].length() >= 2) {
                linkStr = linkSplit[0].substring(2, link.length() - 2);
            }
            if (TextUtils.isEmpty(linkStr)) {
                callback.onFailed(4005);
            }
            //邮箱登录需要解析token
            String decode_user = JWTUtil.getId(access_token);
            if (decode_user == null || decode_user.isEmpty()) {
                decode_user = account;
            }

            LoginInfo li = LoginInfo.createWithToken(decode_user, access_token, UcUtil.getStringMD5(serverUrl));

            String[] address = linkStr.split(":");
            li.setServer(address[0]);
            li.setPort(Integer.valueOf(address[1]));
            li.setVersion(SDKGlobal.getSDKOption().version);
            SDKSharedPreferences.getInstance().saveServerIp(address[0]);
            SDKSharedPreferences.getInstance().saveServerPort(address[1]);
            SDKSharedPreferences.getInstance().saveAuthServerUrl(serverUrl);
            SDKSharedPreferences.getInstance().saveRefreshToken(refresh_token);
            SDKSharedPreferences.getInstance().saveAccessToken(access_token);
            SDKSharedPreferences.getInstance().saveTokenRefreshTime(SDKTimeManager.getInstance().getCurrentTime());
            SDKGlobal.setAppKey(li.getAppKey());
            if (!TextUtils.isEmpty(accessStr)) {
                LogWrapper.infoUI(accessStr);
                UcSTARClusterAgent.get().parseucstarcluster(accessStr);
            }
            SDKGlobal.setLoginInfo(li);
            SDKTimeManager.getInstance().setBeginTime(0);
            AbortableFuture<LoginInfo> loginRequest = UcSTARSDKClient.getService(AuthService.class).login(li);
            loginRequest.setCallback(new RequestCallback<LoginInfo>() {
                @Override
                public void onSuccess(LoginInfo loginInfo) {
                    PreferencesUcStar.saveUserAccount(loginInfo.getAccount());
                    initBiz();
                    callback.onSuccess(loginInfo);
                }

                @Override
                public void onFailed(int code) {
                    callback.onFailed(code);
                }

                @Override
                public void onException(Throwable exception) {
                    callback.onException(exception);
                }
            });
            return loginRequest;
        } catch (Exception e) {
            callback.onFailed(4000);
            return null;
        }
    }

    public static AbortableFuture<LoginInfo> doLogin(String serverUrl, String account, String password, final RequestCallback<LoginInfo> callback) {

        AbortableFuture<LoginInfo> loginRequest = UcSTARSDKClient.getService(AuthService.class).login2(serverUrl, account, password);
        loginRequest.setCallback(new RequestCallback<LoginInfo>() {
            @Override
            public void onSuccess(LoginInfo loginInfo) {

                PreferencesUcStar.saveUserAccount(loginInfo.getAccount());
                initBiz();
                callback.onSuccess(loginInfo);
            }

            @Override
            public void onFailed(int code) {
                callback.onFailed(code);
            }

            @Override
            public void onException(Throwable exception) {
                callback.onException(exception);
            }
        });
        return loginRequest;
    }

    /**
     * 验证码登录
     *
     * @param serverUrl  当前服务器地址 ip + port
     * @param account    手机号
     * @param verifyCode 验证码
     * @param callback   登录结果回调
     * @return
     */
    public static AbortableFuture<LoginInfo> doLoginByVerifyCode(String serverUrl, String account, String verifyCode, final RequestCallback<LoginInfo> callback) {

        AbortableFuture<LoginInfo> loginRequest = UcSTARSDKClient.getService(AuthService.class).login3(serverUrl, account, verifyCode, "{\"type\":5}");
        loginRequest.setCallback(new RequestCallback<LoginInfo>() {
            @Override
            public void onSuccess(LoginInfo loginInfo) {

                PreferencesUcStar.saveUserAccount(loginInfo.getAccount());
                initBiz();
                callback.onSuccess(loginInfo);
            }

            @Override
            public void onFailed(int code) {
                callback.onFailed(code);
            }

            @Override
            public void onException(Throwable exception) {
                callback.onException(exception);
            }
        });
        return loginRequest;
    }

    public static ImageLoaderKit getImageLoaderKit() {
        return imageLoaderKit;
    }

    public static UserInfoProvider getUserInfoProvider() {
        return userInfoProvider;
    }

    /**
     * 当用户资料发生改动时，请调用此接口，通知更新UI
     *
     * @param accounts 有用户信息改动的帐号列表
     */
    public static void notifyUserInfoChanged(List<String> accounts) {
        UserInfoHelper.notifyChanged(accounts);
    }
}
