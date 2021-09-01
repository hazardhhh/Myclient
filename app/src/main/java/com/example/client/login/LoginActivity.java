package com.example.client.login;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.method.TransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.client.R;
import com.example.client.base.UI;
import com.example.client.common.ui.dialog.EasyAlertDialogHelper;
import com.example.client.permission.MPermission;
import com.example.client.utils.PreferencesUcStar;
import com.example.client.utils.sys.ScreenUtil;
import com.example.client.widget.ClearableEditTextWithIcon;
import com.ucstar.android.sdk.AbortableFuture;
import com.ucstar.android.sdk.Observer;
import com.ucstar.android.sdk.StatusCode;
import com.ucstar.android.sdk.UcSTARSDKClient;
import com.ucstar.android.sdk.auth.AuthService;
import com.ucstar.android.sdk.auth.ClientType;
import com.ucstar.android.sdk.auth.LoginInfo;

/**
 *  登录/注册界面
 */
public class LoginActivity extends UI implements View.OnKeyListener {

    private static final String TAG=LoginActivity.class.getSimpleName();
    private static final String KICK_OUT="KICK_OUT";
    private static final String REQUEST_CODE_LOGIN="request_code_login";
    private final int BASE_PERMISSION_REQUEST_CODE=110;

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

    private TextView rightTopBtn; //ActionBar右上角按钮
    private TextView switchModeBtn; //登录/注册切换
    private FrameLayout flPhoneLogin; //短信验证码登录
    private FrameLayout flForgetPsw; //忘记密码？
    private ImageView ivShowPsw; //密码是否可见
    private ClearableEditTextWithIcon loginAccountEdit; //账号登录编辑框
    private ClearableEditTextWithIcon loginPasswordEdit; //账号密码编辑框
    private ClearableEditTextWithIcon registerAccountEdit; //账号注册编辑框
    private ClearableEditTextWithIcon registerNickNameEdit; //账号姓名注册编辑框
    private ClearableEditTextWithIcon registerPasswordEdit; //账号密码注册编辑框
    private View loginLayout; //登录框视图
    private View registerLayout; //注册框视图
    private AbortableFuture<LoginInfo> loginRequest;
    private boolean registerMode = false; //注册模式
    private boolean registerPanelInited = false; //注册面板是否初始化
    private int requestCode; //请求码
    private int clickCount;
    private long currentClickTime; //现在点击时间

    /**
     *  文本监听接口
     */
    private TextWatcher textWatcher=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            //更新右上角按钮状态
            if (!registerMode) {
                //登录模式
                boolean isEnable = loginAccountEdit.getText().length() > 0
                        && loginPasswordEdit.getText().length() > 0;
                updateRightTopBtn(LoginActivity.this, rightTopBtn, isEnable);
            }
        }
    };

    public static void startShareLogin(Context context, int requestCode) {
        start(context, false, requestCode);
    }

    public static void start(Context context) {
        start(context, false);
    }

    public static void start(Context context, boolean kickOut) {
        Intent intent=new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(KICK_OUT, kickOut);
        context.startActivity(intent);
    }

    public static void start(Context context, boolean kickOut, int requestCode) {
        Intent intent=new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(KICK_OUT, kickOut);
        intent.putExtra(REQUEST_CODE_LOGIN, requestCode);
        ((Activity) context).startActivityForResult(intent, requestCode);
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        requestCode = getIntent().getIntExtra(REQUEST_CODE_LOGIN, -1);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        requestBasicPermission();

        onParseIntent(); //登录端口类型
        initEvent(); //验证码登录，忘记密码，密码图标显示与隐藏
        initRightTopBtn(); //ActionBar 右上角按钮
        setupLoginPanel(); //设置登录面板
        setupRegisterPanel(); //设置注册面板

        //服务器设置
        TextView setting = (TextView) findViewById(R.id.tv_login_setting_ip);
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showIPSettingList();
            }
        });

        findViewById(R.id.iv_app_logo).setOnClickListener(v -> {
            //TODO:blankj.utilcode包
//            LogUtils.d("clickCount : " + clickCount);
            if (clickCount == 0) {
                clickCount++;
                currentClickTime = System.currentTimeMillis();
                return;
            }
            if (System.currentTimeMillis() - currentClickTime <= 600) {
                clickCount++;
                if (clickCount >= 5) {
                    clickCount = 0;
                    showIPSettingList();
                }
            } else {
                currentClickTime = System.currentTimeMillis();
                if (clickCount - 1 < 0) {
                    clickCount -= 1;
                }
            }
        });
    }

    private void showIPSettingList() {
        //TODO:服务器设置
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     *  基本权限管理
     */
    private void requestBasicPermission() {
        MPermission.with(LoginActivity.this)
                .addRequestCode(BASE_PERMISSION_REQUEST_CODE)
                .permissions(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                )
                .request();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     *  登录端口类型
     */
    private void onParseIntent() {
        requestCode = getIntent().getIntExtra(REQUEST_CODE_LOGIN, -1);
        if (getIntent().getBooleanExtra(KICK_OUT, false)) {
            int type = UcSTARSDKClient.getService(AuthService.class).getKickedClientType();
            String client;
            switch (type) {
                case ClientType.Web:
                    client = "网页端";
                    break;

                case ClientType.Windows:
                    client = "电脑端";
                    break;

                case ClientType.REST:
                    client = "服务端";
                    break;

                default:
                    client = "移动端";
                    break;
            }
            EasyAlertDialogHelper.showOneButtonDiolag(LoginActivity.this, getString(R.string.kickout_notify),
                    String.format(getString(R.string.kickout_content), client), getString(R.string.ok), true, null);
        }
    }

    /**
     *  ActionBar 右上角按钮
     */
    private void initRightTopBtn() {
        rightTopBtn = addRegisterRightTopBtn(this, R.string.login);
        rightTopBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (registerMode) {
                    register();
                } else {
                    login();
                }
            }
        });
    }

    /**
     *  验证码登录，忘记密码，密码图标显示与隐藏
     */
    private void initEvent() {
        flPhoneLogin = findViewById(R.id.fl_phone_login);
        flPhoneLogin.setOnClickListener(v -> {
            //TODO:vertifycodelogin
        });
        flForgetPsw = findViewById(R.id.fl_forget_psw);
        flForgetPsw.setOnClickListener(v -> {
            //TODO:forgetpsw
        });
        ivShowPsw = findViewById(R.id.iv_show_psw);
        ivShowPsw.setOnClickListener(v -> {
            if (loginPasswordEdit.getTransformationMethod() == PasswordTransformationMethod.getInstance()) {
                //显示密码
                loginPasswordEdit.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                ivShowPsw.setImageResource(R.drawable.ic_login_show_psw);
            } else {
                //隐藏密码
                loginPasswordEdit.setTransformationMethod(PasswordTransformationMethod.getInstance());
                ivShowPsw.setImageResource(R.drawable.ic_login_hide_psw);
            }
        });
    }


    /**
     *  登录面板
     */
    private void setupLoginPanel() {
        loginAccountEdit = findView(R.id.edit_login_account);
        loginPasswordEdit = findView(R.id.edit_login_password);

        //设置账号，密码图标资源
        loginAccountEdit.setIconResource(R.drawable.user_account_icon);
        loginPasswordEdit.setIconResource(R.drawable.user_pwd_lock_icon);

        //setFilters()限制最大输入符
        loginAccountEdit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(32)});
        loginPasswordEdit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(32)});
        //文本监听事件
        loginAccountEdit.addTextChangedListener(textWatcher);
        loginPasswordEdit.addTextChangedListener(textWatcher);

        loginPasswordEdit.setOnKeyListener(this);
//        String account = PreferencesUcStar.getUserAccount();
//        loginAccountEdit.setText(account);
    }

    /**
     *  注册面板
     */
    private void setupRegisterPanel() {
        loginLayout = findViewById(R.id.login_layout);
        registerLayout = findViewById(R.id.register_layout);
    }

    /**
     * 更新右上角按钮,激活按钮，设置内边距
     *
     * @param context
     * @param rightTopBtn 按钮
     * @param isEnable 是否激活
     */
    private void updateRightTopBtn(Context context, TextView rightTopBtn, boolean isEnable) {
        rightTopBtn.setEnabled(isEnable);
        rightTopBtn.setPadding(ScreenUtil.dip2px(10),0,ScreenUtil.dip2px(10),0);
    }

    /**
     * ***************************************** 登录 **************************************
     */

    private void login() {
    }

    /**
     * ***************************************** 注册 **************************************
     */

    private void register() {
    }

    /**
     *  ActionBar 添加右上角按钮方法
     */
    private TextView addRegisterRightTopBtn(UI activity, int strResId) {
        String text = activity.getResources().getString(strResId);
        TextView textView = findViewById(R.id.tv_login_btn);
        textView.setText(text);
        if (text != null) {
            textView.setPadding(ScreenUtil.dip2px(10), 0, ScreenUtil.dip2px(10), 0);
        }
        return textView;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_CANCELED);
    }
}
