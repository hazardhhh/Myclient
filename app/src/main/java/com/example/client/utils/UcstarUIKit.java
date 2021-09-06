package com.example.client.utils;

import android.content.Context;

import com.example.client.utils.PreferencesUcStar;
import com.example.client.utils.cache.DataCacheManager;
import com.example.client.utils.uinfo.UserInfoHelper;
import com.ucstar.android.sdk.AbortableFuture;
import com.ucstar.android.sdk.RequestCallback;
import com.ucstar.android.sdk.UcSTARSDKClient;
import com.ucstar.android.sdk.auth.AuthService;
import com.ucstar.android.sdk.auth.LoginInfo;
import com.ucstar.android.sdk.uinfo.UserInfoProvider;

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
