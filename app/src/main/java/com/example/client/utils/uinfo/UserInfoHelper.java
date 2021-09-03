package com.example.client.utils.uinfo;

import android.text.TextUtils;

import com.example.client.utils.PreferencesUcStar;
import com.example.client.utils.UcstarUIKit;
import com.example.client.utils.cache.TeamDataCache;
import com.example.client.utils.cache.UcUserInfoCache;
import com.ucstar.android.message.SenderNickCache;
import com.ucstar.android.sdk.msg.constant.SessionTypeEnum;

import java.util.List;

public class UserInfoHelper {

    private static UserInfoObservable userInfoObservable;

    // 获取用户显示在标题栏和最近联系人中的名字
    public static String getUserTitleName(String id, SessionTypeEnum sessionType) {
        if (sessionType == SessionTypeEnum.P2P) {
            if (id.equals(PreferencesUcStar.getUserAccount())) {
                return "文件传输助手";
            } else {
                String userNick = SenderNickCache.get().getNick(id);
                if (!TextUtils.isEmpty(userNick) && !id.equals(userNick)) {
                    return userNick;
                }
                return UcUserInfoCache.getInstance().getUserDisplayName(id);
            }
        } else if (sessionType == SessionTypeEnum.Team) {
            return TeamDataCache.getInstance().getTeamName(id);
        } else if (sessionType == SessionTypeEnum.Broadcast) {
//            return UcstarUIKit.getContext().getString(com.qqtech.sdk.uikit.R.string.msg_broadcast);
            if ("notice_broadcast".equals(id)) {
                return UcstarUIKit.getContext().getString(com.example.client.R.string.notice_broadcast);
            } else {
                String userNick = SenderNickCache.get().getNick(id);
                if (!TextUtils.isEmpty(userNick) && !id.equals(userNick)) {
                    return userNick;
                }
                return UcstarUIKit.getContext().getString(com.example.client.R.string.msg_broadcast);
            }
        } else if (sessionType == SessionTypeEnum.System) {
            return "系统消息";
        } else if (sessionType == SessionTypeEnum.ServiceOnline) {
            return SenderNickCache.get().getNick(id);
        }

        return id;
    }

    /**
     * 注册用户资料变化观察者。<br>
     * 注意：不再观察时(如Activity destroy后)，要unregister，否则会造成资源泄露
     *
     * @param observer 观察者
     */
    public static void registerObserver(UserInfoObservable.UserInfoObserver observer) {
        if (userInfoObservable == null) {
            userInfoObservable = new UserInfoObservable(UcstarUIKit.getContext());
        }
        userInfoObservable.registerObserver(observer);
    }

    /**
     * 注销用户资料变化观察者。
     *
     * @param observer 观察者
     */
    public static void unregisterObserver(UserInfoObservable.UserInfoObserver observer) {
        if (userInfoObservable != null) {
            userInfoObservable.unregisterObserver(observer);
        }
    }

    /**
     * 当用户资料发生改动时，请调用此接口，通知更新UI
     *
     * @param accounts 有用户信息改动的帐号列表
     */
    public static void notifyChanged(List<String> accounts) {
        if (userInfoObservable != null) {
            userInfoObservable.notifyObservers(accounts);
        }
    }
}
