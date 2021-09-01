package com.example.client.login;


import android.view.KeyEvent;
import android.view.View;

import com.example.client.base.UI;
import com.ucstar.android.sdk.Observer;
import com.ucstar.android.sdk.StatusCode;

/**
 *  登录/注册界面
 */
public class LoginActivity extends UI implements View.OnKeyListener {

    private static final String TAG=LoginActivity.class.getSimpleName();
    private static final String REQUEST_CODE_LOGIN="request_code_login";
    private final int BASE_PERMISSION_REQUEST_CODE=110;

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        return false;
    }

    /**
     *  用户状态变化
     */
    Observer<StatusCode> userStatusObserver=new Observer<StatusCode>() {
        @Override
        public void onEvent(StatusCode code) {
            if(code.wontAutoLogin())
                return;
        }
    };

}
