package com.example.client.login;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 注销帮助类
 * Created by ucstar on 2015/10/8.
 */
public class LogoutHelper {

    private static List<LogoutCallback> callbacks = new CopyOnWriteArrayList<>();

    public interface LogoutCallback {
        void onLogout(int flag);
        void onLogin(int flag);
    }

    public static void onlogin(int flag) {
        Iterator<LogoutCallback> itor = callbacks.iterator();
        while (itor.hasNext()) {
            itor.next().onLogin(flag);
        }
    }
}
