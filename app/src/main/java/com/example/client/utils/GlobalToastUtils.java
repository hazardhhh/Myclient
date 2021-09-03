package com.example.client.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;

import com.blankj.utilcode.util.ToastUtils;
import com.example.client.R;
import com.example.client.databinding.ToastBinding;


public class GlobalToastUtils {

    private static ToastBinding toastBinding;
    private static Context context;

    public static void init(Context c) {
        context = c;
        if (toastBinding == null) {
            toastBinding = DataBindingUtil.bind(LayoutInflater.from(context).inflate(R.layout.toast, null));
        }
    }


    public static void makeText(int imgResourceId, String text, int duration) {
        toastBinding.ivIcon.setImageResource(imgResourceId);
        toastBinding.tvContent.setText(text);
        ToastUtils.setGravity(Gravity.CENTER, 0, 0);
        if (duration == Toast.LENGTH_SHORT) {
            ToastUtils.showCustomShort(toastBinding.getRoot());
        } else {
            ToastUtils.showCustomLong(toastBinding.getRoot());
        }
    }

    public static void makeText(String text, int duration) {
        toastBinding.ivIcon.setVisibility(View.GONE);
        toastBinding.tvContent.setText(text);
        if (duration == Toast.LENGTH_SHORT) {
            ToastUtils.showCustomShort(toastBinding.getRoot());
        } else {
            ToastUtils.showCustomLong(toastBinding.getRoot());
        }
    }

    public static void makeText(int imgResourceId, int textId, int duration) {
        makeText(imgResourceId, context.getString(textId), duration);
    }

    public static void makeText(int textId, int duration) {
        makeText(context.getString(textId), duration);
    }

    public static void showSuccessShort(String text) {
        makeText(R.drawable.ic_toast_success, text, Toast.LENGTH_SHORT);
    }

    public static void showSuccessShort(int textId) {
        makeText(R.drawable.ic_toast_success, textId, Toast.LENGTH_SHORT);
    }

    public static void showSuccessLong(String text) {
        makeText(R.drawable.ic_toast_success, text, Toast.LENGTH_LONG);
    }

    public static void showSuccessLong(int textId) {
        makeText(R.drawable.ic_toast_success, textId, Toast.LENGTH_LONG);
    }

    public static void showErrorShort(String text) {
        makeText(R.drawable.ic_toast_error, text, Toast.LENGTH_SHORT);
    }

    public static void showErrorShort(int textId) {
        makeText(R.drawable.ic_toast_error, textId, Toast.LENGTH_SHORT);
    }


    public static void showErrorLong(String text) {
        makeText(R.drawable.ic_toast_error, text, Toast.LENGTH_LONG);
    }

    public static void showErrorLong(int textId) {
        makeText(R.drawable.ic_toast_error, textId, Toast.LENGTH_LONG);
    }


    public static void showHintShort(int textId) {
        makeText(R.drawable.ic_toast_exclamatory_mark, textId, Toast.LENGTH_SHORT);
    }

    public static void showHintShort(String text) {
        makeText(R.drawable.ic_toast_exclamatory_mark, text, Toast.LENGTH_SHORT);
    }

    public static void showHintLong(int textId) {
        makeText(textId, Toast.LENGTH_LONG);
    }

    public static void showHintLong(String text) {
        makeText(text, Toast.LENGTH_LONG);
    }

    public static void showNormalShort(String text) {
        makeText(text, Toast.LENGTH_SHORT);
    }

    public static void showNormalShort(int textId) {
        makeText(textId, Toast.LENGTH_SHORT);
    }

    public static void showNormalLong(String text) {
        makeText(text, Toast.LENGTH_LONG);
    }

    public static void showNormalLong(int textId) {
        makeText(textId, Toast.LENGTH_LONG);
    }
}
