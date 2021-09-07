package com.example.client.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.LogUtils;
import com.example.client.R;
import com.example.client.base.UI;
import com.example.client.bean.VerifyCodeResponse;
import com.example.client.common.dialog.DialogManager;
import com.example.client.common.ui.dialog.DialogMaker;
import com.example.client.utils.GlobalToastUtils;
import com.example.client.utils.PreferencesUcStar;
import com.example.client.utils.UcstarUIKit;
import com.example.client.utils.log.LogUtil;
import com.example.client.utils.retrofit.RetrofitClient;
import com.example.client.utils.string.StringUtil;
import com.google.gson.JsonObject;
import com.ucstar.android.SDKGlobal;
import com.ucstar.android.sdk.RequestCallback;
import com.ucstar.android.sdk.ResponseCode;
import com.ucstar.android.sdk.auth.LoginInfo;

import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 *  注册
 */
public class RegisterActivity extends UI implements View.OnClickListener, TextWatcher {

    private EditText edPhone, edCode, edName; //手机号，验证码，姓名编辑框
    private TextView tvGetVerifyCode; //获取验证码
    private TextView tvServiceAgreement, tvPrivacyAgreement; //底部服务协议, 隐私政策
    private CheckBox rbAgreeAgreement; //同意复选框
    private CardView buttonRegister; //注册
    private boolean isCanRegistrer = false;
    private boolean isCanGetCode = false;

    private boolean isCountDownTimer = false;
    private CountDownTimer countDownTimer; //倒计时器
    private int millisInFuture = 60; //秒数
    private int countDownInterval = 1000; //间隔

    private String phone, code, name;

    public static void startRegisterActivity(Context context) {
        context.startActivity(new Intent(context, RegisterActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        GlobalToastUtils.init(this);
        initView();
    }

    private void initView() {
        edPhone = findViewById(R.id.ed_phone);
        edCode = findViewById(R.id.ed_code);
        edName = findViewById(R.id.ed_name);
        tvGetVerifyCode = findViewById(R.id.tv_get_verify_code);
        tvServiceAgreement = findViewById(R.id.tv_service_agreement);
        tvPrivacyAgreement = findViewById(R.id.tv_privacy_agreement);
        rbAgreeAgreement = findViewById(R.id.rb_agree_agreement);
        buttonRegister = findViewById(R.id.button_register);
        buttonRegister.setEnabled(true);
        edPhone.addTextChangedListener(this);
        edCode.addTextChangedListener(this);
        edName.addTextChangedListener(this);

        tvGetVerifyCode.setOnClickListener(this);
        tvServiceAgreement.setOnClickListener(this);
        tvPrivacyAgreement.setOnClickListener(this);
        buttonRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_get_verify_code:
//                requestVerifyCode();
                break;
            case R.id.tv_service_agreement:
                //TODO: web service_agreement
//                WebBrowserActivity.start(this, JsBridgeHandler.webviewUrlBase + "/my/serviceClause?loadFirst=1");
                break;
            case R.id.tv_privacy_agreement:
                //TODO: web privacy_agreement
//                WebBrowserActivity.start(this, JsBridgeHandler.webviewUrlBase + "/my/privacyProtocol?loadFirst=1");
                break;
            case R.id.button_register:
//                register();
                break;
            default:
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        phone = edPhone.getText().toString();
        code = edCode.getText().toString();
        name = edName.getText().toString();

        isCanGetCode = !StringUtil.isEmpty(phone) && phone.length() ==11 && !isCountDownTimer;

        isCanRegistrer = !StringUtil.isEmpty(phone) && !StringUtil.isEmpty(code) && !StringUtil.isEmpty(name);
        buttonRegister.setEnabled(isCanRegistrer);
        buttonRegister.setAlpha(isCanRegistrer ? 1.0f : 0.5f);
        tvGetVerifyCode.setEnabled(isCanGetCode);
        tvGetVerifyCode.setAlpha(isCanGetCode ? 1.0f : 0.5f);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    /**
     *  获取登录验证码
     */
    private void requestVerifyCode() {
        if (StringUtil.isEmpty(phone)) {
            GlobalToastUtils.showNormalShort("请输入手机号码");
            return;
        }
        DialogMaker.showProgressDialog(this, "获取验证码");
        String serverIpToken = PreferencesUcStar.getServerIpToken();
        String serverPortToken = "31010";
        String baseUrl = "http://" + serverIpToken + ":" + serverPortToken + "/";
        Retrofit retrofit = RetrofitClient.retrofit(baseUrl);
        RetrofitService service = retrofit.create(RetrofitService.class);
        service.sendRegisterSms(phone, "1").enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                DialogMaker.dismissProgressDialog();

                try {
                    LogUtils.d("requestVerifyCode", response.toString());
                    String body = response.body().string();
                    LogUtils.d("body", body);
                    if (body != null) {
                        VerifyCodeResponse rsp = GsonUtils.fromJson(body, VerifyCodeResponse.class);
                        if (rsp.isResult()) {
                            startCountDown();
                        } else {
                            GlobalToastUtils.showNormalShort(rsp.getMessage());
                        }
                    }
                } catch (Exception e){
                    e.printStackTrace();
                    LogUtils.e(e);
                    GlobalToastUtils.showNormalShort("获取验证码 解析异常！");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                DialogMaker.dismissProgressDialog();
                LogUtils.d("requestVerifyCode fail! throwable : " + t.getMessage());
                GlobalToastUtils.showNormalShort("注册 失败！");
            }
        });
    }

    /**
     *  用户注册
     */
    private void register() {
        if (StringUtil.isEmpty(phone)) {
            GlobalToastUtils.showNormalShort("请输入手机号码");
            return;
        }
        if (StringUtil.isEmpty(code)) {
            GlobalToastUtils.showNormalShort("请输入收到的验证码");
            return;
        }
        if (StringUtil.isEmpty(name)) {
            GlobalToastUtils.showNormalShort("请输入真实姓名");
            return;
        }
        if (!rbAgreeAgreement.isChecked()) {
            GlobalToastUtils.showNormalShort("请先勾选上方服务协议和隐私协议");
            return;
        }
        DialogMaker.showProgressDialog(this, "注册中...");
        String serverIpToken = PreferencesUcStar.getServerIpToken();
        String serverPortToken = "30101";
        String baseUrl = "http://" + serverIpToken + ":" + serverPortToken + "/";
        Retrofit retrofit = RetrofitClient.retrofit(baseUrl);
        RetrofitService service = retrofit.create(RetrofitService.class);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("phone", phone);
        jsonObject.addProperty("name", name);
        jsonObject.addProperty("code", code);
        jsonObject.addProperty("source", 1);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());
        service.register(body).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                DialogMaker.dismissProgressDialog();

                try {
                    LogUtils.d("requestVerifyCode", response.toString());
                    String body = response.body().string();
                    LogUtils.d("body : " + body);
                    if (body != null) {
                        //RegisterResponse rsp = GsonUtils.fromJson(body, RegisterResponse.class);
                        JSONObject jsonObject = new JSONObject(body);
                        if (jsonObject.getInt("code") == 200) {
                            //LogoutHelper.onlogin(0);
                            String serverIpToken = PreferencesUcStar.getServerIpToken();
                            String serverUrl = "http://" + serverIpToken + ":" + PreferencesUcStar.getServerPortToken() + "/";
                            UcstarUIKit.doLoginByTokenInfo(serverUrl, phone, jsonObject.getString("result"), new RequestCallback<LoginInfo>() {
                                @Override
                                public void onException(Throwable throwable) {
                                    GlobalToastUtils.showErrorLong(R.string.login_exception);
                                }

                                @Override
                                public void onFailed(int code) {
                                    if (code == 4001) {//4001不存在 4002密码错误
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
                                public void onSuccess(LoginInfo loginInfo) {
                                    LogoutHelper.onlogin(0);
                                }
                            });
                            finish();
                        } else {
                            GlobalToastUtils.showNormalShort(jsonObject.getString("message"));
                        }
                        GlobalToastUtils.showNormalShort(jsonObject.getString("message"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtils.e(e);
                    GlobalToastUtils.showNormalShort("注册 解析异常!");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                DialogMaker.dismissProgressDialog();
                LogUtils.d("requestVerifyCode fail!  throwable : " + t.getMessage());
                GlobalToastUtils.showNormalShort("注册 失败!");
            }
        });
    }

    /**
     *  倒计时60秒,一次一秒
     */
    private void startCountDown() {
        stopCountDown();
        if (countDownTimer == null) {
            countDownTimer = new CountDownTimer(millisInFuture * 1000, countDownInterval) {
                @Override
                public void onTick(long second) {
                    tvGetVerifyCode.setText(second / 1000 + "秒后重新发送");
                    tvGetVerifyCode.setAlpha(0.5f);
                    tvGetVerifyCode.setEnabled(false);
                }

                @Override
                public void onFinish() {
                    isCountDownTimer = false;
                    tvGetVerifyCode.setText(R.string.get_verify_code);
                    tvGetVerifyCode.setAlpha(1.0f);
                    tvGetVerifyCode.setEnabled(true);
                }
            };
        }
        isCountDownTimer = true;
        countDownTimer.start();
    }

    private void stopCountDown() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}
