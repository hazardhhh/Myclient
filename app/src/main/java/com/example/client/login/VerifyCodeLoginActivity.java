package com.example.client.login;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;

import androidx.core.content.ContextCompat;

import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.StringUtils;
import com.example.client.R;
import com.example.client.base.BaseUIActivity;
import com.example.client.bean.VerifyCodeResponse;
import com.example.client.databinding.VerifyCodeLoginActivityBinding;
import com.example.client.ui.activity.MainActivity;
import com.example.client.utils.GlobalToastUtils;
import com.example.client.utils.PreferencesUcStar;
import com.example.client.utils.ToastUtils;
import com.example.client.utils.UcstarUIKit;
import com.example.client.utils.retrofit.RetrofitClient;
import com.ucstar.android.SDKGlobal;
import com.ucstar.android.sdk.RequestCallback;
import com.ucstar.android.sdk.ResponseCode;
import com.ucstar.android.sdk.auth.LoginInfo;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class VerifyCodeLoginActivity extends BaseUIActivity<VerifyCodeLoginActivityBinding> {

    /**
     *  倒计时器
     */
    private CountDownTimer countDownTimer;

    private int millisInFuture = 60; //秒数
    private int countDownInterval = 1000; //间隔

    private TextWatcher textWatcher;

    public static void start(Context context) {
        context.startActivity(new Intent(context, VerifyCodeLoginActivity.class));
    }


    @Override
    protected int getLayoutId() {
        return R.layout.verify_code_login_activity;
    }

    @Override
    protected void init() {
        //初始化 GlobalToastUtils
        GlobalToastUtils.init(this);
    }

    @Override
    protected void initEvent() {

        getDataBinding().editLoginAccount.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                //StringUtils com.blankj.utilcode.util
                getDataBinding().tvGetVerifyCode.setEnabled(!StringUtils.isEmpty(s.toString()));
                getDataBinding().tvLoginBtn.setEnabled(!StringUtils.isEmpty(s.toString())
                        && !StringUtils.isEmpty(getDataBinding().editVerifyCode.getText().toString()));
            }
        });

        getDataBinding().editVerifyCode.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                getDataBinding().tvLoginBtn.setEnabled(!StringUtils.isEmpty(s.toString())
                        && !StringUtils.isEmpty(getDataBinding().editLoginAccount.getText().toString()));
            }
        });

        getDataBinding().flAccountLogin.setOnClickListener(v -> {
            LoginActivity.start(this);
            finish();
        });

        getDataBinding().flGetVerifyCode.setOnClickListener(v -> {
            String phone = getDataBinding().editLoginAccount.getText().toString();
            if (!PhoneFormatCheckUtils.isChinaPhoneLegal(getDataBinding().editLoginAccount.getText().toString())) {
                GlobalToastUtils.showNormalShort("请输入正确的手机号码");
            } else {
//                requestVerifyCode(phone);
            }
        });

        getDataBinding().flForgetPsw.setOnClickListener(v -> {
//            ForgetPasswordActivity.start(this, ForgetPasswordActivity.DEFAULT_URL);
        });

        getDataBinding().tvLoginBtn.setOnClickListener(v -> {
            String phone = getDataBinding().editLoginAccount.getText().toString().trim();
            String code = getDataBinding().editVerifyCode.getText().toString().trim();
            if (!StringUtils.isEmpty(phone) && !StringUtils.isEmpty(code)) {
//                loginByVerifyCode(phone, code);
            }
        });
    }


    @Override
    protected void initData() {

    }

    /**
     * 倒计时60秒，一次1秒
     */
    private void startCountDown() {
        stopCountDown();
        countDownTimer = new CountDownTimer(millisInFuture * 1000, countDownInterval) {
            @SuppressLint("SetTextI18n")
            @Override
            public void onTick(long second) {
                getDataBinding().tvGetVerifyCode.setText(second / 1000 + "重新发送");
                getDataBinding().tvGetVerifyCode.setTextColor(ContextCompat.getColor(VerifyCodeLoginActivity.this, R.color.color_text_B2B2B2));
                getDataBinding().flGetVerifyCode.setEnabled(false);
            }

            @Override
            public void onFinish() {
                getDataBinding().tvGetVerifyCode.setText(R.string.get_verify_code);
                getDataBinding().tvGetVerifyCode.setTextColor(ContextCompat.getColor(VerifyCodeLoginActivity.this, R.color.color_gray_666666));
                getDataBinding().flGetVerifyCode.setEnabled(true);
            }
        };
        countDownTimer.start();
    }

    private void stopCountDown() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    /**
     * 获取登录验证码
     *
     * @param phone
     */
    private void requestVerifyCode(String phone) {
        String serverIpToken = PreferencesUcStar.getServerIpToken();
        String serverPortToken = "30101";
        String baseUrl = "http://" + serverIpToken + ":" + serverPortToken + "/";
        Retrofit retrofit = RetrofitClient.retrofit(baseUrl);
        RetrofitService service = retrofit.create(RetrofitService.class);
        if (StringUtils.isEmpty(phone)){
            return;
        }
        service.getVerifyCode(phone).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {
                    LogUtils.d("requestVerifyCode", response.toString());
                    String body = response.body().string();
                    LogUtils.d("body : " + body);
                    if (body != null) {
                        VerifyCodeResponse rsp = GsonUtils.fromJson(body, VerifyCodeResponse.class);
                        if (rsp.isResult()) {
                            startCountDown();
                        } else {
                            GlobalToastUtils.showNormalShort(rsp.getMessage());
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    LogUtils.e(e);
                    GlobalToastUtils.showNormalShort("获取验证码 解析异常!");
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                LogUtils.d("requestVerifyCode fail!  throwable : " + t.getMessage());
                GlobalToastUtils.showNormalShort("获取验证码 失败!");
            }
        });
    }

    /**
     * token 登录
     *
     * @param phone      手机号
     * @param verifyCode 验证码
     */
    private void loginByVerifyCode(String phone, String verifyCode) {
        String serverIpToken = PreferencesUcStar.getServerIpToken();
        String serverPortToken = PreferencesUcStar.getServerPortToken();
        String baseUrl = "http://" + serverIpToken + ":" + serverPortToken + "/";
        UcstarUIKit.doLoginByVerifyCode(baseUrl, phone, verifyCode, new RequestCallback<LoginInfo>() {
            @Override
            public void onSuccess(LoginInfo param) {
                LogUtils.d("login success param : " + param);
                dismissDialog();
                // 进入主界面 记录当前登录密码 主页面判断是否是初始化密码
                MainActivity.start(VerifyCodeLoginActivity.this, null);
                finish();
            }

            @Override
            public void onFailed(int code) {
                dismissDialog();
                if (code == 4001) {//4001不存在 4002密码错误
                    GlobalToastUtils.showNormalShort(R.string.login_account_not);
                } else if (code == 4002) {
                    GlobalToastUtils.showNormalShort(R.string.verify_code_error);
                } else if (code == 4005) {
                    GlobalToastUtils.showNormalShort("登录失败, 无可用服务: " + code);
                } else if (code == 302 || code == 404) {
                    GlobalToastUtils.showNormalShort(R.string.login_failed_account_or_verify_code_error);
                } else if (code == ResponseCode.RES_FORBIDDEN) {
                    int errorcode = SDKGlobal.getErrorCode();
                    String errMsg = "登录失败";
                    if (errorcode == 500) {
                        errMsg = "您的许可证已经到期，请尽快联系管理员检查并且更换新的许可证！";
                    } else if (errorcode == 501) {
                        errMsg = "当前在线人数已超出限制，您被禁止登陆，请联系管理员处理！";
                    }
                    GlobalToastUtils.showNormalShort(errMsg);
                } else {
                    GlobalToastUtils.showNormalShort("登录失败,请检查地址和网络");
                }
            }

            @Override
            public void onException(Throwable throwable) {
                GlobalToastUtils.showNormalShort(R.string.login_exception);
                dismissDialog();
            }
        });
    }

}
