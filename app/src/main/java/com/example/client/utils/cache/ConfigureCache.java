package com.example.client.utils.cache;

import android.text.TextUtils;

//import com.blankj.utilcode.util.LogUtils;
import com.example.client.utils.PreferencesUcStar;
import com.ucstar.android.sdk.RequestCallback;
import com.ucstar.android.sdk.UcSTARSDKClient;
import com.ucstar.android.sdk.settings.SettingsService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConfigureCache {
    private static com.example.client.utils.cache.ConfigureCache instance = new com.example.client.utils.cache.ConfigureCache();
    private Map<String, String> configMap = new ConcurrentHashMap<>();

    private ConfigureCache() {
        // TODO Auto-generated constructor stub
    }

    public static com.example.client.utils.cache.ConfigureCache getInstance() {
        return instance;
    }

    public void buildCache() {
        final List<String> keys = new ArrayList<String>();
        keys.add("urlsynchead");
        keys.add("v1register");
        keys.add("urluploadhead");
        keys.add("ucweb");
        keys.add("chjhurl");
        keys.add("mobileupdate");
        keys.add("groupvoteurl");
        //工单标签,评分标签
        keys.add("dispatchlist_h5");
        keys.add("score_h5");
        keys.add("cloud_doc");
        keys.add("receiptstatics");
        keys.add("msgreadstatistic");
        keys.add("freeswitchaddr");
        keys.add("workbench_h5");
        keys.add("uc6-banner-todo");
        keys.add("uc6-banner-daka");
        keys.add("uc6-chat-filepreview");
        keys.add("duty");
        keys.add("uc6-dingmsg");
        keys.add("uc6-organization-manage");
        keys.add("uc6-discover");
        //文档地址
        try {

            UcSTARSDKClient.getService(SettingsService.class).fetchConfigure(keys).setCallback(new RequestCallback<Map<String, String>>() {
                @Override
                public void onException(Throwable throwable) {

                }

                @Override
                public void onFailed(int i) {

                }

                @Override
                public void onSuccess(Map<String, String> result) {
                    if (result != null) {
                        for (Map.Entry<String, String> config : result.entrySet()) {
                            configMap.put(config.getKey(), config.getValue());
//                            LogUtils.d("config.getKey() : " + config.getKey() + "   config.getValue() : " + config.getValue());
                            if (config.getKey().equals("urlsynchead")) {
                                PreferencesUcStar.saveHeadUrl(config.getValue());
                            }
                        }
                    }

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //统计已读未读数目
    public String getMsgReadStatisticUrl() {
        return getConfig("msgreadstatistic");
    }

    //统计回执的url
    public String getReceiptstaticsUrl() {
        return getConfig("receiptstatics");
    }

    public String getDispatchlist() {
        return getConfig("dispatchlist_h5");
    }

    public String getScore() {
        return getConfig("score_h5");
    }

    public String getFileWord() {
        return getConfig("cloud_doc");
    }

    public boolean checkCache() {
        return configMap.size() > 0;
    }

    public void setConfig(String key, String value) {
        configMap.put(key, value);
    }

    public String getConfig(String key) {
        return configMap.get(key);
    }

    public String getWorkbenchDomainName() {
        return configMap.get("workbench_h5");
    }

    public String getFindWebUrl(){
        return configMap.get("uc6-discover");
    }

    public String getAvatarUrl() {
        return TextUtils.isEmpty(getConfig("urlsynchead")) ? PreferencesUcStar.getHeadUrl() : getConfig("urlsynchead");
    }

    public String getPushRegisterUrl() {
        return getConfig("v1register");
    }

    public String getUploadAvatarUrl() {
        return getConfig("urluploadhead");
    }

    public String getFreeSwitchAddr() {
        return getConfig("freeswitchaddr");
    }

    public String getTodoUrl(){
        return getConfig("uc6-banner-todo");
    }

    public String getDaka(){
        return getConfig("uc6-banner-daka");
    }

    public String getFilePreviewUrl(){
        return getConfig("uc6-chat-filepreview");
    }

    public String getDuty(){
        return getConfig("duty");
    }

    public String getDingMsgBaseUrl (){
        return getConfig("uc6-dingmsg");
    }

    public String getTeamManagerUrl(){
        return getConfig("uc6-organization-manage");
    }


    public String getConfig(String key, String groupid, String userid, String departid) {
        String urlbase = configMap.get(key);
        if (urlbase == null || urlbase.isEmpty()) {
            return "";
        }
        if (groupid != null && !groupid.isEmpty()) {
            // urlbase = urlbase.replace("GROUP_ID", "group_" + groupid +
            // "@conference.qqtech");
            urlbase = urlbase.replace("GROUP_ID", groupid);
        }
        if (userid != null && !userid.isEmpty()) {
            urlbase = urlbase.replace("USER_ID", userid);
        }
        if (departid != null && !departid.isEmpty()) {
            urlbase = urlbase.replace("DEPART_ID", departid);
        }
        return urlbase;
    }
}
