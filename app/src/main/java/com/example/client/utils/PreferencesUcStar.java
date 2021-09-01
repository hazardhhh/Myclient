package com.example.client.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.example.client.R;
import com.ucstar.android.SDKGlobal;
import com.ucstar.android.util.AESEncryptorUtil;


public class PreferencesUcStar {
    public static final String KEY_SERVER_IP_TOKEN = "serverIpToken";
    public static final String KEY_SERVER_PORT_TOKEN = "serverPortToken";
    private static final String KEY_USER_ACCOUNT = "account";
    private static final String KEY_UPDATE = "mobileUpdate";
    private static final String KEY_FIRST_LOAD="first_load";
    private static final String KEY_HEAD_URL="key_head_url";


    public static void saveFirstLoad(boolean isFirst){
        saveBoolean(KEY_FIRST_LOAD,isFirst);
    }
    public static boolean getFirstLoad(){
        return getBoolean(KEY_FIRST_LOAD);
    }


    public static void saveHeadUrl(String account) {
        saveString(KEY_HEAD_URL, account);
    }
    public static String getHeadUrl() {
        return getString(KEY_HEAD_URL);
    }


    public static void saveUserAccount(String account) {
        saveString(KEY_USER_ACCOUNT, AESEncryptorUtil.encrypt(account));
    }

    public static void saveUpdateUrl(String updateUrl) {
        saveString(KEY_UPDATE, updateUrl);
    }

    public static String getServerIpToken() {
        return getSharedPreferences().getString(KEY_SERVER_IP_TOKEN, SDKGlobal.getContext().getString(R.string.server_ip_qqtech));
    }

    public static String getServerPortToken() {
        return getSharedPreferences().getString(KEY_SERVER_PORT_TOKEN, SDKGlobal.getContext().getString(R.string.server_port_qqtech));
    }

    public static void saveServerIpToken(String ip) {
        saveString(KEY_SERVER_IP_TOKEN, ip);
    }
    public static void saveServerPortToken(String port) {
        saveString(KEY_SERVER_PORT_TOKEN, port);
    }


    public static String getUpdateUrl() {
        return getSharedPreferences().getString(KEY_UPDATE, "");
    }

    public static String getUserAccount() {
        String account=getString(KEY_USER_ACCOUNT);
        if (TextUtils.isEmpty(account)) {
            return account;
        }
        return AESEncryptorUtil.decrypt(getString(KEY_USER_ACCOUNT));
    }

    private static void saveString(String key, String value) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(key, value);
        editor.commit();
    }

    private static String getString(String key) {
        return getSharedPreferences().getString(key, null);
    }

    private static void saveLong(String key,long value){
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putLong(key, value);
        editor.commit();
    }

    private static long getLong(String key){
       return  getSharedPreferences().getLong(key,-1);
    }
    private static boolean getBoolean(String key){
        return  getSharedPreferences().getBoolean(key,false);
    }
    private static void saveBoolean(String key,boolean isFirst){
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putBoolean(key, isFirst);
        editor.commit();
    }


    //设置草稿内容
    public static void putDraft(String keyId,String content) {
        SharedPreferences sharedPreferences = getSharedPreferencesWithAccount();
        SharedPreferences.Editor v3SPEditor = sharedPreferences.edit().putString(keyId, content);
        v3SPEditor.commit();
    }
    //获取草稿内容
    public static String getDraft(String keyId) {
        SharedPreferences sharedPreferences = getSharedPreferencesWithAccount();
        return sharedPreferences.getString(keyId,"");
    }
    static SharedPreferences getSharedPreferences() {
        return SDKGlobal.getContext().getSharedPreferences("UcSTAR_SP", Context.MODE_PRIVATE);
    }

    static SharedPreferences getSharedPreferencesWithAccount() {
        return SDKGlobal.getContext().getSharedPreferences("UcSTAR_SP_"+ SDKGlobal.currAccount(), Context.MODE_PRIVATE);
    }
}
