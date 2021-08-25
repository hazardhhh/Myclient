package com.example.client.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 *  存储一个叫uid的String类型的值：
 *  SharedPrefUtil.putString(mContext, "uid", "要赋值为xxxxxx"); // 最后一个是要赋的值
 *
 *  取出一个叫uid的String类型的值：
 *  String uid = SharedPrefUtil.getString(mContext, "uid", ""); // 最后一个""是如果没有取到uid的值时,要赋的默认值
 *
 */
public class SharedPrefUtil {
    private static SharedPreferences mSp;

    private static SharedPreferences getSharedPref(Context context) {
        if (mSp == null) {
            mSp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        return mSp;
    }

    public static void putBoolean(Context context, String key, boolean value) {
        getSharedPref(context).edit().putBoolean(key, value).commit();
    }

    public static boolean getBoolean(Context context, String key, boolean defValue) {
        return getSharedPref(context).getBoolean(key, defValue);
    }

    public static void putString(Context context, String key, String value) {
        getSharedPref(context).edit().putString(key, value).commit();
    }

    public static String getString(Context context, String key, String defValue) {
        return getSharedPref(context).getString(key, defValue);
    }

    public static void removeString(Context context, String key) {
        getSharedPref(context).edit().remove(key).commit();
    }

    public static void putInt(Context context, String key, int value) {
        getSharedPref(context).edit().putInt(key, value).commit();
    }

    public static int getInt(Context context, String key, int defValue) {
        return getSharedPref(context).getInt(key, defValue);
    }

}