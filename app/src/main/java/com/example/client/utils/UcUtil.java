package com.example.client.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;

/**
 * @author: qingqi
 * @date: 2019/11/21
 */
public class UcUtil {

    public static void copyBoard(Context context,String content,int textToastId){
        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData mClipData = ClipData.newPlainText("content",content);
        cm.setPrimaryClip(mClipData);
        Toast.makeText(context,textToastId, Toast.LENGTH_SHORT).show();
    }

    public static String getStringMD5(String pStr) {
        if(pStr != null && pStr.trim().length() > 0) {
            try {
                return getMD5(pStr.getBytes("UTF-8"));
            } catch (UnsupportedEncodingException v1UEExc) {
                throw new RuntimeException(v1UEExc.getMessage(), v1UEExc);
            }
        }
        return null;
    }

    private static String getMD5(byte[] data) {
        try {
            return HexUtil.bytes2Hex(MessageDigest.getInstance("MD5").digest(data));
        } catch (Exception v1Exc) {
            throw new RuntimeException(v1Exc.getMessage(), v1Exc);
        }
    }
}
