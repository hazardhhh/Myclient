package com.example.client.utils.cache;

import android.content.Context;
import android.os.Handler;

import com.example.client.common.framework.UcSingleThreadExecutor;
import com.example.client.utils.PreferencesUcStar;
import com.example.client.utils.UcstarUIKit;
import com.example.client.utils.log.LogUtil;
import com.ucstar.android.sdk.Observer;

import java.util.ArrayList;
import java.util.List;

/**
 * UIKit缓存数据管理类
 * <p/>
 * Created by ucstar on 2015/10/19.
 */
public class DataCacheManager {

    private static final String TAG = DataCacheManager.class.getSimpleName();

    /**
     * 本地缓存构建(异步)
     */
    public static void buildDataCacheAsync() {
        buildDataCacheAsync(null, null);
    }

    /**
     * 本地缓存构建(异步)
     */
    public static void buildDataCacheAsync(final Context context, final Observer<Void> buildCompletedObserver) {
        UcSingleThreadExecutor.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                buildDataCache();

                // callback
                if (context != null && buildCompletedObserver != null) {
                    new Handler(context.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            buildCompletedObserver.onEvent(null);
                        }
                    });
                }

                LogUtil.i(TAG, "build data cache completed");
            }
        });
    }

    /**
     * 本地缓存构建（同步）
     */
    public static void buildDataCache() {
        // clear
        clearDataCache();
        ConfigureCache.getInstance().buildCache();
        DepartInfoCache.getInstance().buildCache();
        // build user/friend/team data cache
        FriendDataCache.getInstance().buildCache();
        UcUserInfoCache.getInstance().buildCache();
        TeamDataCache.getInstance().buildCache();

        // build self avatar cache
        List<String> accounts = new ArrayList<>(1);
        accounts.add(PreferencesUcStar.getUserAccount());
        UcstarUIKit.getImageLoaderKit().buildAvatarCache(accounts);
    }

    /**
     * 清空缓存（同步）
     */
    public static void clearDataCache() {
        // clear user/friend/team data cache
        DepartInfoCache.getInstance().clear();
        FriendDataCache.getInstance().clear();
        UcUserInfoCache.getInstance().clear();
        TeamDataCache.getInstance().clear();

        // clear avatar cache
        UcstarUIKit.getImageLoaderKit().clear();
    }

    /**
     * 输出缓存数据变更日志
     */
    public static void Log(List<String> accounts, String event, String logTag) {
        StringBuilder sb = new StringBuilder();
        sb.append(event);
        sb.append(" : ");
        for (String account : accounts) {
            sb.append(account);
            sb.append(" ");
        }
        sb.append(", total size=" + accounts.size());

        LogUtil.i(logTag, sb.toString());
    }
}
