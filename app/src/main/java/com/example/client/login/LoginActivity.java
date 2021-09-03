package com.example.client.login;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blankj.utilcode.util.SPUtils;
import com.example.client.R;
import com.example.client.base.UI;
import com.example.client.common.ui.dialog.DialogMaker;
import com.example.client.common.ui.dialog.EasyAlertDialogHelper;
import com.example.client.config.Constant;
import com.example.client.contact.ContactHttpClient;
import com.example.client.permission.MPermission;
import com.example.client.share.SystemShareActivity;
import com.example.client.ui.activity.MainActivity;
import com.example.client.utils.GlobalToastUtils;
import com.example.client.utils.PreferencesUcStar;
import com.example.client.utils.ToastUtils;
import com.example.client.utils.UcstarUIKit;
import com.example.client.utils.log.LogUtil;
import com.example.client.utils.sys.NetworkUtil;
import com.example.client.utils.sys.ScreenUtil;
import com.example.client.widget.ClearableEditTextWithIcon;
import com.ucstar.android.SDKGlobal;
import com.ucstar.android.sdk.AbortableFuture;
import com.ucstar.android.sdk.Observer;
import com.ucstar.android.sdk.RequestCallback;
import com.ucstar.android.sdk.ResponseCode;
import com.ucstar.android.sdk.StatusCode;
import com.ucstar.android.sdk.UcSTARSDKClient;
import com.ucstar.android.sdk.auth.AuthService;
import com.ucstar.android.sdk.auth.ClientType;
import com.ucstar.android.sdk.auth.LoginInfo;

import butterknife.internal.Constants;

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

    private String dAccount = "111111"; //默认账号
    private String dPassword = "111111"; //默认密码

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

    /**
     * 若果另外一个activity启动该activity(该activity已经存在)想给该activity传递数据,那么就用到了onNewIntent()方法
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        requestCode = getIntent().getIntExtra(REQUEST_CODE_LOGIN, -1);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        requestBasicPermission(); //基本权限管理

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

        //app logo
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
            switchMode();
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


        //demo text
        TextView textView = findViewById(R.id.tv_login_btn);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mAccount = loginAccountEdit.getText().toString();
                String mPassword = loginPasswordEdit.getText().toString();

                if (! mAccount.equals(dAccount) || ! mPassword.equals(dPassword)){
                    ToastUtils.show(LoginActivity.this, "账号无效");
                } else {
                    Intent intent=new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        });

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
        DialogMaker.showProgressDialog(this, null, getString(R.string.logining), true, new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (loginRequest != null) {
                    loginRequest.abort();
                    onLoginDone();
                }
            }
        }).setCanceledOnTouchOutside(false); //点击Dialog以外的区域时Dialog消失(true)

        final String account = loginAccountEdit.getEditableText().toString().toLowerCase().trim();
        String password = loginPasswordEdit.getEditableText().toString();
        String serverIpToken = PreferencesUcStar.getServerIpToken();
        String serverPortToken = PreferencesUcStar.getServerPortToken();
        String baseUrl = "http://" + serverIpToken + ":" + serverPortToken + "/";

        //登录
        loginRequest = UcstarUIKit.doLogin(baseUrl, account, password, new RequestCallback<LoginInfo>() {

            @Override
            public void onSuccess(LoginInfo param) {
                LogUtil.i(TAG, "login success");
                onLoginDone();
                if (requestCode == SystemShareActivity.REQUEST_CODE_LOGIN) {
                    setResult(RESULT_OK);
                } else {
                    // 进入主界面 记录当前登录密码 主页面判断是否是初始化密码
                    //blankj:utilcode SPUtils SP 相关
                    SPUtils.getInstance().put(Constant.KEY_PASSWORD, password);
                    //注销
                    LogoutHelper.onlogin(0);
                }
                finish();
            }

            @Override
            public void onFailed(int code) {
                onLoginDone();
                if (code == 4001) { //4001不存在 4002密码错误
                    GlobalToastUtils.showErrorShort(R.string.login_account_not);
                } else if (code == 4002) {
                    GlobalToastUtils.showErrorShort(R.string.login_password_error);
                } else if (code == 4005) {
                    GlobalToastUtils.showErrorShort("登录失败, 无可用服务: " + code);
                } else if (code == 302 || code == 404) {
                    GlobalToastUtils.showErrorShort(R.string.login_failed);
                } else if (code == ResponseCode.RES_FORBIDDEN) {
                    int errorcode = SDKGlobal.getErrorCode();
                    String errMsg = "登录失败";
                    if (errorcode == 500) {
                        errMsg = "您的许可证已经到期，请尽快联系管理员检查并且更换新的许可证！";
                    } else if (errorcode == 501) {
                        errMsg = "当前在线人数已超出限制，您被禁止登陆，请联系管理员处理！";
                    }
                    GlobalToastUtils.showErrorShort(errMsg);
                } else {
                    GlobalToastUtils.showErrorShort("登录失败,请检查地址和网络");
                }

            }

            @Override
            public void onException(Throwable throwable) {
                GlobalToastUtils.showErrorLong(R.string.login_exception);
                onLoginDone();
            }
        });

    }

    private void onLoginDone() {
        loginRequest = null;
        DialogMaker.dismissProgressDialog();
    }

    /**
     * ***************************************** 注册 **************************************
     */

    private void register() {
        if (!registerMode || !registerPanelInited) {
            return;
        }

        if (!checkRegisterContentValid()) {
            return;
        }

        if (!NetworkUtil.isNetAvailable(LoginActivity.this)) {
            GlobalToastUtils.showNormalShort(R.string.network_is_not_available);
            return;
        }

        DialogMaker.showProgressDialog(this, getString(R.string.registering), false);

        // 注册流程
        final String account = registerAccountEdit.getText().toString();
        final String nickName = registerNickNameEdit.getText().toString();
        final String password = registerPasswordEdit.getText().toString();

        ContactHttpClient.getInstance().register(account, nickName, password, new ContactHttpClient.ContactHttpCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                GlobalToastUtils.showNormalShort(R.string.register_success);

                switchMode();  // 切换回登录
                loginAccountEdit.setText(account);
                loginPasswordEdit.setText(password);

                registerAccountEdit.setText("");
                registerNickNameEdit.setText("");
                registerPasswordEdit.setText("");

                DialogMaker.dismissProgressDialog();
            }

            @Override
            public void onFailed(int code, String errorMsg) {
                GlobalToastUtils.showErrorShort(getString(R.string.register_failed, String.valueOf(code), errorMsg));
                DialogMaker.dismissProgressDialog();
            }
        });
    }

    private boolean checkRegisterContentValid() {
        if (!registerMode || !registerPanelInited) {
            return false;
        }

        // 帐号检查
        String account = registerAccountEdit.getText().toString().trim();
        if (account.length() <= 0 || account.length() > 20) {
            Toast.makeText(this, R.string.register_account_tip, Toast.LENGTH_SHORT);

            return false;
        }

        // 昵称检查
        String nick = registerNickNameEdit.getText().toString().trim();
        if (nick.length() <= 0 || nick.length() > 10) {
            Toast.makeText(this, R.string.register_nick_name_tip, Toast.LENGTH_SHORT);

            return false;
        }

        // 密码检查
        String password = registerPasswordEdit.getText().toString().trim();
        if (password.length() < 6 || password.length() > 20) {
            Toast.makeText(this, R.string.register_password_tip, Toast.LENGTH_SHORT);

            return false;
        }

        return true;
    }

    /**
     * ***************************************** 注册/登录切换 **************************************
     */
    private void switchMode() {
        registerMode = !registerMode;

        if (registerMode && !registerPanelInited) {
            registerAccountEdit = findView(R.id.edit_register_account);
            registerNickNameEdit = findView(R.id.edit_register_nickname);
            registerPasswordEdit = findView(R.id.edit_register_password);

            registerAccountEdit.setIconResource(R.drawable.user_account_icon);
            registerNickNameEdit.setIconResource(R.drawable.user_account_icon);
            registerPasswordEdit.setIconResource(R.drawable.user_pwd_lock_icon);

            registerAccountEdit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
            registerNickNameEdit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
            registerPasswordEdit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});

            registerAccountEdit.addTextChangedListener(textWatcher);
            registerNickNameEdit.addTextChangedListener(textWatcher);
            registerPasswordEdit.addTextChangedListener(textWatcher);

            registerPanelInited = true;
        }

        setTitle(registerMode ? R.string.register : R.string.login);
        loginLayout.setVisibility(registerMode ? View.GONE : View.VISIBLE);
        rightTopBtn.setText(registerMode ? R.string.done : R.string.login);
        registerLayout.setVisibility(registerMode ? View.VISIBLE : View.GONE);
//        switchModeBtn.setText(registerMode ? R.string.login_has_account : R.string.register);
        if (registerMode) {
            rightTopBtn.setEnabled(true);
        } else {
            boolean isEnable = loginAccountEdit.getText().length() > 0
                    && loginPasswordEdit.getText().length() > 0;
            rightTopBtn.setEnabled(isEnable);
        }
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
