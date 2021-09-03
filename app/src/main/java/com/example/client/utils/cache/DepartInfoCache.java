package com.example.client.utils.cache;

import android.text.TextUtils;

import com.ucstar.android.p64m.SDKTimeManager;
import com.ucstar.android.sdk.Observer;
import com.ucstar.android.sdk.UcSTARSDKClient;
import com.ucstar.android.sdk.depart.DepartAndUserWraper;
import com.ucstar.android.sdk.depart.DepartService;
import com.ucstar.android.sdk.depart.DepartServiceObserver;
import com.ucstar.android.sdk.depart.model.UcSTARDepartInfo;
import com.ucstar.android.sdk.uinfo.UserService;
import com.ucstar.android.sdk.uinfo.model.UcSTARUserInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by zhua on 2017/9/30.
 */

public class DepartInfoCache {
    private Map<String, UcSTARDepartInfo> id2DepartMap = new ConcurrentHashMap<>();
    private Map<String, String> Id2DepartPathMap = new ConcurrentHashMap<>();
    private static com.example.client.utils.cache.DepartInfoCache instance;

    private Map<String, Long> Id2TimeMap = new ConcurrentHashMap<>();

    public static synchronized com.example.client.utils.cache.DepartInfoCache getInstance() {
        if (instance == null) {
            instance = new com.example.client.utils.cache.DepartInfoCache();
        }

        return instance;
    }

    public void buildCache() {
        DepartAndUserWraper dataWraper = UcSTARSDKClient.getService(DepartService.class).getDepartAndUserList("0");
        if (dataWraper == null || dataWraper.departs.size() == 0) {
            UcSTARSDKClient.getService(DepartService.class).fetchDepartAndUserList("0");
        } else {
            for (UcSTARDepartInfo departInfo : dataWraper.departs) {
                id2DepartMap.put(departInfo.getId(), departInfo);
            }
        }
    }

    public void clear() {
        clearDepartCache();
    }

    private void clearDepartCache() {
        Id2TimeMap.clear();
    }

    public void addOrgUpdateDepart(UcSTARDepartInfo info) {

    }

    public DepartAndUserWraper getDepartAnduserList(String pid) {
        // UcSTARSDKClient.getService(DepartService.class).deleteUsersByDepartId(pid);
        DepartAndUserWraper dataWraper = UcSTARSDKClient.getService(DepartService.class).getDepartAndUserList(pid);
        if (dataWraper!=null&&dataWraper.departs!=null&&dataWraper.departs.size() == 0 && dataWraper.users.size() == 0) {
            UcSTARSDKClient.getService(DepartService.class).fetchDepartAndUserList(pid);
        }
        // qmfei 控制获取部门和人员的频率，不能每次点击都去服务器去取
        long time = 0;
        if (!Id2TimeMap.isEmpty() && Id2TimeMap.containsKey(pid)) {
            time = Id2TimeMap.get(pid);
        }
        long now = SDKTimeManager.getInstance().getCurrentTime();
        if (now - time > 60 * 10 * 1000) {
            UcSTARSDKClient.getService(DepartService.class).deleteUsersByDepartId(pid);
            UcSTARSDKClient.getService(DepartService.class).fetchDepartAndUserList(pid);
            Id2TimeMap.put(pid, now);
        }
        return dataWraper;
    }


    public List<UcSTARUserInfo> getUserList(String pattern) {
        return UcSTARSDKClient.getService(DepartService.class).getUserList(pattern);
    }

    public List<UcSTARDepartInfo> getDepartList(String pattern) {
        return UcSTARSDKClient.getService(DepartService.class).getDepartList(pattern);
    }

    public UcSTARDepartInfo getDepartInfo(String id) {
        UcSTARDepartInfo departInfo = id2DepartMap.get(id);
        if (departInfo == null) {
            return UcSTARSDKClient.getService(DepartService.class).getDepartInfo(id);
        }
        return departInfo;
    }

    public UcSTARDepartInfo getDepartInfoByUser(String account) {
        List<String> departIds = UcSTARSDKClient.getService(DepartService.class).getDepartIdByUser(account);
        if (departIds.size() == 0) {
            return null;
        }
        UcSTARDepartInfo departInfo = id2DepartMap.get(departIds.get(0));
        if (departInfo == null) {
            return UcSTARSDKClient.getService(DepartService.class).getDepartInfo(departIds.get(0));
        }
        return departInfo;
    }

    public String getDepartPath(String id) {
        if (TextUtils.isEmpty(id)) {
            return "";
        }
        String departPath = Id2DepartPathMap.get(id);
        if (TextUtils.isEmpty(departPath)) {
            UcSTARDepartInfo info = getDepartInfo(id);
            if (info != null && "0".equals(info.getId())) {
                Id2DepartPathMap.put(id, info.getName());
                return info.getName();
            }
            StringBuilder build = new StringBuilder();
            ArrayList<String> paths = new ArrayList<String>();
            while (info != null && info.getId() != "0") {
                paths.add(info.getName());
                info = getDepartInfo(info.getPid());
            }

            for (int index = paths.size() - 1; index >= 0; index--) {
                build.append(paths.get(index));
                if (index != 0) {
                    build.append("-");
                }
            }

            return build.toString();
        }
        return departPath;
    }

    public String getDepartPathByUser(String account) {

        String path = UcSTARSDKClient.getService(UserService.class).getDepartPathByUserId(account);
        if (!TextUtils.isEmpty(path)) {
            if (path.startsWith("/")) {
                path = path.substring(1);
            }
            path = path.replaceAll("/", "-");
            return path;
        }
        List<String> departIds = UcSTARSDKClient.getService(DepartService.class).getDepartIdByUser(account);
        if (departIds.size() == 0) {
            UcSTARUserInfo info = UcUserInfoCache.getInstance().getUserInfo(account);
            if (info != null) {
                return getDepartPath(info.getPid());
            }
            return null;
        }
        return getDepartPath(departIds.get(0));
    }

    public interface DepartAndUserChangedObserver {
        void onDepartAndUserChange(DepartAndUserWraper dataWraper);
    }

    private List<DepartAndUserChangedObserver> departAndUserObservers = new ArrayList<>();

    public void registerDepartDataChangedObserver(DepartAndUserChangedObserver o) {
        if (departAndUserObservers.contains(o)) {
            return;
        }

        departAndUserObservers.add(o);
    }

    public void unregisterDepartDataChangedObserver(DepartAndUserChangedObserver o) {
        departAndUserObservers.remove(o);
    }

    private void notifyonDepartAndUserChange(DepartAndUserWraper event) {
        for (DepartAndUserChangedObserver o : departAndUserObservers) {
            o.onDepartAndUserChange(event);
        }
    }

    public void registerObservers(boolean register) {
        UcSTARSDKClient.getService(DepartServiceObserver.class).observeDepartAndUserInfoUpdate(departAndUserInfoObserver, register);
    }

    private Observer<DepartAndUserWraper> departAndUserInfoObserver = new Observer<DepartAndUserWraper>() {

        @Override
        public void onEvent(DepartAndUserWraper event) {
            if (event.departs != null) {
                for (UcSTARDepartInfo departInfo : event.departs) {
                    id2DepartMap.put(departInfo.getId(), departInfo);
                }
            }
            notifyonDepartAndUserChange(event);
            String path = getDepartPath(event.pid);
            // 获取状态
            if (event.users != null && event.users.size() > 0) {
                List<String> accounts = new ArrayList<String>(event.users.size());
                for (int i = 0; i < event.users.size(); i++) {
                    UcSTARUserInfo user = event.users.get(i);
                    accounts.add(user.getAccount());
                    UcUserInfoCache.getInstance().putUser(user);
                    if (path != null && !"".equals(path)) {// 保存部门路径
                        UcSTARSDKClient.getService(UserService.class).saveUserPath(user.getAccount(), path);
                    }
                }
//                UcSTARSDKClient.getService(PresenceService.class).subscribePresences(accounts);
            }
        }
    };
}
