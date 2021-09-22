package com.example.client.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.RegexUtils;
import com.blankj.utilcode.util.StringUtils;
import com.google.gson.JsonObject;
//import com.shenhua.sdk.uikit.PreferencesUcStar;
//import com.shenhua.sdk.uikit.cache.UcUserInfoCache;
import com.example.client.base.UI;
import com.example.client.common.ui.dialog.DialogMaker;
import com.example.client.model.ToolBarOptions;
import com.example.client.R;
//import com.shenhua.zhihui.contact.helper.UserUpdateHelper;
//import com.shenhua.zhihui.login.ForgetPasswordActivity;
//import com.shenhua.zhihui.login.RetrofitService;
import com.example.client.login.SimpleTextWatcher;
//import com.shenhua.zhihui.retrofit.BaseResponse;
//import com.shenhua.zhihui.retrofit.Data;
//import com.shenhua.zhihui.utils.retrofit.RetrofitClient;
import com.example.client.utils.GlobalToastUtils;
import com.ucstar.android.sdk.RequestCallbackWrapper;
import com.ucstar.android.sdk.uinfo.constant.UserInfoFieldEnum;
import com.ucstar.android.sdk.uinfo.model.UcSTARUserInfo;

import java.util.ArrayList;
import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ChangePhoneNumActivity extends UI implements View.OnClickListener {

//    private static final String TAG = com.shenhua.zhihui.main.activity.ChangePhoneNumActivity.class.getSimpleName();

    //一次验证
    private View layoutFirstVerify;
    private TextView tvCurrentPhoneNum;
    private TextView tvCountDownSec;
    private TextView tvRetrieveAccount;

    //验证码
    private EditText etPreFirstPwd;
    private EditText etPreSecondPwd;
    private EditText etPreThirdPwd;
    private EditText etPreFourthPwd;
    private EditText[] preVerifyArray;
    private String verifyNumStr;

    //更换手机号码
    private View layoutAlterPhoneNum;
    private TextView tvCurrentNum;
    private EditText etPhoneNum;
    private Button btnConfirmAlter;

    //二次验证
    private View layoutSecondVerify;
    private TextView tvNewPhoneNum;
    private TextView tvGetVerifyAgain;
    private EditText etNewFirstPwd;
    private EditText etNewSecondPwd;
    private EditText etNewThirdPwd;
    private EditText etNewFourthPwd;
    private EditText[] newVerifyArray;
    private String newVerifyNumStr;

    private CountDownTimer countDownTimer; //计时器

    private int millisInFuture = 120; //秒数
    private int countDownInterval = 1000; //间隔

    private String mobile; //原手机号
    private String newMobile; //修改后手机号

    //0= 首次页面 1=输入新手机号页面  2=第二次输入验证码页面
    private int currentPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_phone_num);

//        ToolBarOptions options = new ToolBarOptions();
//        options.titleString = "  ";
//        setToolBar(R.id.toolbar, options);

        initView();
        initPhoneNum();
        initVerifyNum();
        initEditPhoneNum();
        initVerifyNumSecond();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startCountDown();
    }

    private void initView() {
        currentPosition = 0;
        //一次验证
        layoutFirstVerify = findViewById(R.id.layout_first_verify);
        tvCurrentPhoneNum = findViewById(R.id.tv_phone_num);
        tvCountDownSec = findViewById(R.id.tv_countdown_sec);
        tvRetrieveAccount = findViewById(R.id.tv_retrieve);

        //验证码
        etPreFirstPwd = findViewById(R.id.et_first_num);
        etPreSecondPwd = findViewById(R.id.et_second_num);
        etPreThirdPwd = findViewById(R.id.et_third_num);
        etPreFourthPwd = findViewById(R.id.et_fourth_num);

        //更换手机号码
        layoutAlterPhoneNum = findViewById(R.id.layout_alter_phone);
        tvCurrentNum = findViewById(R.id.tv_phone_num_second);
        etPhoneNum = findViewById(R.id.et_phone_num);
        btnConfirmAlter = findViewById(R.id.btn_confirm_alert);

        //二次验证
        layoutSecondVerify = findViewById(R.id.layout_verify_again);
        tvNewPhoneNum = findViewById(R.id.tv_phone_num_third);
        tvGetVerifyAgain = findViewById(R.id.tv_get_verify);
        etNewFirstPwd = findViewById(R.id.et_new_first_num);
        etNewSecondPwd = findViewById(R.id.et_new_second_num);
        etNewThirdPwd = findViewById(R.id.et_new_third_num);
        etNewFourthPwd = findViewById(R.id.et_new_fourth_num);

        tvGetVerifyAgain.setOnClickListener(this);
        tvRetrieveAccount.setOnClickListener(this);

        layoutAlterPhoneNum.setVisibility(View.GONE);
        layoutSecondVerify.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_retrieve:
//                startActivity(new Intent(this, ForgetPasswordActivity.class));
                break;
            case R.id.tv_get_verify:
                //重新获取验证码
//                requestVerifyCode(newMobile);
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (currentPosition == 0) {
            super.onBackPressed();
        } else if (currentPosition == 1) {
            currentPosition = 0;
            layoutFirstVerify.setVisibility(View.VISIBLE);
            layoutSecondVerify.setVisibility(View.GONE);
            layoutAlterPhoneNum.setVisibility(View.GONE);
        } else if (currentPosition == 2) {
            currentPosition = 1;
            layoutFirstVerify.setVisibility(View.GONE);
            layoutSecondVerify.setVisibility(View.GONE);
            layoutAlterPhoneNum.setVisibility(View.VISIBLE);
        } else {
            this.finish();
        }
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if(keyCode ==KeyEvent.KEYCODE_BACK){
//            return  true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }

    /**
     * 当前手机号
     */
    private void initPhoneNum() {
//        UcSTARUserInfo userInfo = UcUserInfoCache.getInstance().getUserInfo(PreferencesUcStar.getUserAccount());
//        tvCurrentPhoneNum.setText(userInfo != null && !TextUtils.isEmpty(userInfo.getMobile()) ? "+86" + userInfo.getMobile() : "未绑定");
//        tvCurrentNum.setText(userInfo != null && !TextUtils.isEmpty(userInfo.getMobile()) ? "+86" + userInfo.getMobile() : "未绑定");
//        mobile = userInfo.getMobile();
    }


    @SuppressLint("ClickableViewAccessibility")
    private void initVerifyNum() {
        preVerifyArray = new EditText[]{etPreFirstPwd, etPreSecondPwd, etPreThirdPwd, etPreFourthPwd};
        for (int i = 0; i < preVerifyArray.length; i++) {
            final int j = i;
            preVerifyArray[j].addTextChangedListener(new TextWatcher() {
                private CharSequence temp;

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    temp = s;
                }

                @Override
                public void afterTextChanged(Editable s) {
                    //自动跳到下一个输入框
                    if (temp.length() == 1 && j >= 0 && j < preVerifyArray.length - 1) {
                        preVerifyArray[j + 1].setFocusable(true);
                        preVerifyArray[j + 1].setFocusableInTouchMode(true);
                        preVerifyArray[j + 1].requestFocus();
                    }
                    //删除回退上一个输入框
                    if (temp.length() == 0) {
                        if (j >= 1) {
                            preVerifyArray[j - 1].setFocusable(true);
                            preVerifyArray[j - 1].setFocusableInTouchMode(true);
                            preVerifyArray[j - 1].requestFocus();
                        }
                    }
                    //输入结束隐藏键盘
                    if (temp.length() == 3 || j == preVerifyArray.length - 1) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (!TextUtils.isEmpty(s.toString())) {
                            imm.hideSoftInputFromWindow(preVerifyArray[preVerifyArray.length - 1].getWindowToken(), 0);
                        }
                    }
                    if (!TextUtils.isEmpty(etPreFirstPwd.getText().toString().trim()) && !TextUtils.isEmpty(etPreSecondPwd.getText().toString().trim()) &&
                            !TextUtils.isEmpty(etPreThirdPwd.getText().toString().trim()) && !TextUtils.isEmpty(etPreFourthPwd.getText().toString().trim())) {
                        //校验验证码第一次
//                        checkoutVerifyCode();
                    }
                }
            });
        }

        //隐藏光标
        etPreSecondPwd.setCursorVisible(false);
        etPreThirdPwd.setCursorVisible(false);
        etPreFourthPwd.setCursorVisible(false);
        etPreSecondPwd.setOnTouchListener((v, event) -> {
            if (MotionEvent.ACTION_DOWN == event.getAction()) {
                etPreSecondPwd.setCursorVisible(true);
            }
            return false;
        });
        etPreThirdPwd.setOnTouchListener((v, event) -> {
            if (MotionEvent.ACTION_DOWN == event.getAction()) {
                etPreThirdPwd.setCursorVisible(true);
            }
            return false;
        });
        etPreFourthPwd.setOnTouchListener((v, event) -> {
            if (MotionEvent.ACTION_DOWN == event.getAction()) {
                etPreFourthPwd.setCursorVisible(true);
            }
            return false;
        });
    }

    //校验验证码第一次
//    private void checkoutVerifyCode() {
//        DialogMaker.showProgressDialog(this, "");
//        String serverIpToken = PreferencesUcStar.getServerIpToken();
//        String serverPortToken = PreferencesUcStar.getServerHttpGatewayPort();
//        String baseUrl = "http://" + serverIpToken + ":" + serverPortToken + "/";
//        Retrofit retrofit = RetrofitClient.retrofit(baseUrl);
//        RetrofitService service = retrofit.create(RetrofitService.class);
//
//        verifyNumStr = etPreFirstPwd.getText().toString().trim() + etPreSecondPwd.getText().toString().trim() +
//                etPreThirdPwd.getText().toString().trim() + etPreFourthPwd.getText().toString().trim();
//        JsonObject param = new JsonObject();
//        param.addProperty("code", verifyNumStr);
//        param.addProperty("mobile", mobile);
//
//        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), param.toString());
//        service.proofVerifyCode(body)
//                .enqueue(new Callback<BaseResponse<Object>>() {
//                    @Override
//                    public void onResponse(Call<BaseResponse<Object>> call, Response<BaseResponse<Object>> response) {
//                        DialogMaker.dismissProgressDialog();
//                        BaseResponse<Object> data = response.body();
//                        if (data == null) {
//                            GlobalToastUtils.showErrorShort(R.string.abnormal_data);
//                            return;
//                        }
//                        if (data.getCode() == Data.CODE_SUCCESS) {
//                            currentPosition = 1;
//                            //加载更改手机号码视图
//                            stopCountDown();
//                            layoutFirstVerify.setVisibility(View.GONE);
//                            layoutAlterPhoneNum.setVisibility(View.VISIBLE);
//                            layoutSecondVerify.setVisibility(View.GONE);
//                            showKeyboardDelayed(etPhoneNum);
//                        } else {
//                            GlobalToastUtils.showNormalShort(data.getMessage());
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<BaseResponse<Object>> call, Throwable t) {
//                        DialogMaker.dismissProgressDialog();
//                        LogUtils.d("proofVerifyCode fail!  throwable : " + t.getMessage());
//                        GlobalToastUtils.showNormalShort("验证码校验失败!");
//                    }
//                });
//    }


    @SuppressLint("ClickableViewAccessibility")
    private void initEditPhoneNum() {
        etPhoneNum.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                btnConfirmAlter.setEnabled(false);
                if (s.length() == 11) {
                    //输入结束隐藏键盘
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (!TextUtils.isEmpty(s.toString())) {
                        imm.hideSoftInputFromWindow(etPhoneNum.getWindowToken(), 0);
                    }
                    //输入结束隐藏光标
                    etPhoneNum.setCursorVisible(false);
                    etPhoneNum.setOnTouchListener((v, event) -> {
                        if (MotionEvent.ACTION_DOWN == event.getAction()) {
                            etPhoneNum.setCursorVisible(true);
                        }
                        return false;
                    });
//                    UcSTARUserInfo userInfo = UcUserInfoCache.getInstance().getUserInfo(PreferencesUcStar.getUserAccount());
//                    String phoneNum = s.toString();
//                    if (TextUtils.equals(userInfo.getMobile(), phoneNum)) {
//                        GlobalToastUtils.showNormalShort("不支持变更为原手机号码");
//                    }
//                    btnConfirmAlter.setEnabled(RegexUtils.isMobileSimple(phoneNum) && !TextUtils.equals(userInfo.getMobile(), phoneNum));
                }
            }
        });

        btnConfirmAlter.setOnClickListener(v -> {
            switch (v.getId()) {
                case R.id.btn_confirm_alert:
                    //给绑定新手机号码发送验证码
                    newMobile = etPhoneNum.getText().toString().trim();
//                    requestVerifyCode(newMobile);
                    break;
                default:
                    break;
            }
        });
    }

    //给绑定新手机号码发送验证码
//    private void requestVerifyCode(String mobile) {
//        String serverIpToken = PreferencesUcStar.getServerIpToken();
//        String serverPortToken = PreferencesUcStar.getServerHttpGatewayPort();
//        String baseUrl = "http://" + serverIpToken + ":" + serverPortToken + "/";
//        Retrofit retrofit = RetrofitClient.retrofit(baseUrl);
//        RetrofitService service = retrofit.create(RetrofitService.class);
//        if (StringUtils.isEmpty(mobile)) {
//            return;
//        }
//        if (!RegexUtils.isMobileSimple(mobile)) {
//            GlobalToastUtils.showNormalShort("请输入正确的手机号码");
//            return;
//        }
//
//        DialogMaker.showProgressDialog(this, "");
//        service.getChangePhoneVerify(mobile).enqueue(new Callback<BaseResponse<Object>>() {
//            @Override
//            public void onResponse(Call<BaseResponse<Object>> call, Response<BaseResponse<Object>> response) {
//                DialogMaker.dismissProgressDialog();
//                BaseResponse<Object> data = response.body();
//                if (data == null) {
//                    GlobalToastUtils.showErrorShort(R.string.abnormal_data);
//                    return;
//                }
//                LogUtils.d("requestVerifyCode", response.toString());
//                if (data.getCode() == Data.CODE_SUCCESS) {
//                    //第二次加载验证码视图
//                    initVerifySecond();
//                } else {
//                    GlobalToastUtils.showNormalShort(data.getMessage());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<BaseResponse<Object>> call, Throwable t) {
//                DialogMaker.dismissProgressDialog();
//                LogUtils.d("requestVerifyCode fail!  throwable : " + t.getMessage());
//                GlobalToastUtils.showNormalShort("获取验证码 失败!");
//            }
//        });
//    }

    //第二次加载验证码视图
    private void initVerifySecond() {
        currentPosition = 2;
        layoutFirstVerify.setVisibility(View.GONE);
        layoutAlterPhoneNum.setVisibility(View.GONE);
        layoutSecondVerify.setVisibility(View.VISIBLE);
        newMobile = etPhoneNum.getText().toString().trim();
        tvNewPhoneNum.setText("+86" + newMobile);
        showKeyboardDelayed(etNewFirstPwd);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initVerifyNumSecond() {
        newVerifyArray = new EditText[]{etNewFirstPwd, etNewSecondPwd, etNewThirdPwd, etNewFourthPwd};
        for (int i = 0; i < newVerifyArray.length; i++) {
            final int k = i;
            newVerifyArray[k].addTextChangedListener(new TextWatcher() {
                private CharSequence mTemp;

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    mTemp = s;
                }

                @Override
                public void afterTextChanged(Editable s) {
                    //自动跳到下一个输入框
                    if (mTemp.length() == 1 && k >= 0 && k < newVerifyArray.length - 1) {
                        newVerifyArray[k + 1].setFocusable(true);
                        newVerifyArray[k + 1].setFocusableInTouchMode(true);
                        newVerifyArray[k + 1].requestFocus();
                    }
                    //删除回退上一个输入框
                    if (mTemp.length() == 0) {
                        if (k >= 1) {
                            newVerifyArray[k - 1].setFocusable(true);
                            newVerifyArray[k - 1].setFocusableInTouchMode(true);
                            newVerifyArray[k - 1].requestFocus();
                        }
                    }
                    //输入结束隐藏键盘
                    if (mTemp.length() == 3 || k == newVerifyArray.length - 1) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (!TextUtils.isEmpty(s.toString())) {
                            imm.hideSoftInputFromWindow(newVerifyArray[newVerifyArray.length - 1].getWindowToken(), 0);
                        }
                    }
                    if (!TextUtils.isEmpty(etNewFirstPwd.getText().toString()) && !TextUtils.isEmpty(etNewSecondPwd.getText().toString()) &&
                            !TextUtils.isEmpty(etNewThirdPwd.getText().toString()) && !TextUtils.isEmpty(etNewFourthPwd.getText().toString())) {
                        //校验验证码第二次
//                        checkoutVerifyNewCode();
                    }
                }
            });
        }

        //隐藏光标
        etNewSecondPwd.setCursorVisible(false);
        etNewThirdPwd.setCursorVisible(false);
        etNewFourthPwd.setCursorVisible(false);
        etNewSecondPwd.setOnTouchListener((v, event) -> {
            if (MotionEvent.ACTION_DOWN == event.getAction()) {
                etNewSecondPwd.setCursorVisible(true);
            }
            return false;
        });
        etNewThirdPwd.setOnTouchListener((v, event) -> {
            if (MotionEvent.ACTION_DOWN == event.getAction()) {
                etNewThirdPwd.setCursorVisible(true);
            }
            return false;
        });
        etNewFourthPwd.setOnTouchListener((v, event) -> {
            if (MotionEvent.ACTION_DOWN == event.getAction()) {
                etNewFourthPwd.setCursorVisible(true);
            }
            return false;
        });
    }

    //校验验证码第二次
//    private void checkoutVerifyNewCode() {
//        DialogMaker.showProgressDialog(this, "");
//        String serverIpToken = PreferencesUcStar.getServerIpToken();
//        String serverPortToken = PreferencesUcStar.getServerHttpGatewayPort();
//        String baseUrl = "http://" + serverIpToken + ":" + serverPortToken + "/";
//        Retrofit retrofit = RetrofitClient.retrofit(baseUrl);
//        RetrofitService service = retrofit.create(RetrofitService.class);
//
//        newVerifyNumStr = etNewFirstPwd.getText().toString().trim() + etNewSecondPwd.getText().toString().trim() +
//                etNewThirdPwd.getText().toString().trim() + etNewFourthPwd.getText().toString().trim();
//        newMobile = etPhoneNum.getText().toString().trim();
//        JsonObject param = new JsonObject();
//        param.addProperty("code", newVerifyNumStr);
//        param.addProperty("mobile", newMobile);
//
//        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), param.toString());
//        service.proofVerifyNewCode(body)
//                .enqueue(new Callback<BaseResponse<Object>>() {
//                    @Override
//                    public void onResponse(Call<BaseResponse<Object>> call, Response<BaseResponse<Object>> response) {
//                        DialogMaker.dismissProgressDialog();
//                        BaseResponse<Object> data = response.body();
//                        if (data == null) {
//                            GlobalToastUtils.showErrorShort(R.string.net_error);
//                            return;
//                        }
//                        if (data.getCode() == Data.CODE_SUCCESS) {
//                            UserUpdateHelper.update(UserInfoFieldEnum.MOBILE, newMobile, new RequestCallbackWrapper<Void>() {
//                                @Override
//                                public void onResult(int i, Void aVoid, Throwable throwable) {
//                                    UcSTARUserInfo userInfo = UcUserInfoCache.getInstance().getUserInfo(PreferencesUcStar.getUserAccount());
//                                    List<UcSTARUserInfo> data = new ArrayList<>();
//                                    data.add(userInfo);
//                                    UcUserInfoCache.getInstance().addOrUpdateUsers(data, true);
//                                    setResult(Activity.RESULT_OK);
//                                    com.shenhua.zhihui.main.activity.ChangePhoneNumActivity.this.finish();
//                                }
//                            });
//                        } else {
//                            GlobalToastUtils.showNormalShort(data.getMessage());
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<BaseResponse<Object>> call, Throwable t) {
//                        DialogMaker.dismissProgressDialog();
//                        LogUtils.d("proofVerifyCode fail!  throwable : " + t.getMessage());
//                        GlobalToastUtils.showNormalShort("验证码校验失败!");
//                    }
//                });
//    }
//
//    //获取修改原手机号码验证码
//    private void requestOldVerifyCode(String mobile) {
//        DialogMaker.showProgressDialog(this, "");
//        String serverIpToken = PreferencesUcStar.getServerIpToken();
//        String serverPortToken = PreferencesUcStar.getServerHttpGatewayPort();
//        String baseUrl = "http://" + serverIpToken + ":" + serverPortToken + "/";
//        Retrofit retrofit = RetrofitClient.retrofit(baseUrl);
//        RetrofitService service = retrofit.create(RetrofitService.class);
//        if (StringUtils.isEmpty(mobile)) {
//            return;
//        }
//        service.getAlterPhoneVerify(mobile).enqueue(new Callback<BaseResponse<Object>>() {
//            @Override
//            public void onResponse(Call<BaseResponse<Object>> call, Response<BaseResponse<Object>> response) {
//                DialogMaker.dismissProgressDialog();
//                BaseResponse<Object> data = response.body();
//                if (data == null) {
//                    GlobalToastUtils.showErrorShort(R.string.net_error);
//                    return;
//                }
//                GlobalToastUtils.showNormalShort(data.getMessage());
//                LogUtils.d("requestVerifyCode", response.toString());
//            }
//
//            @Override
//            public void onFailure(Call<BaseResponse<Object>> call, Throwable t) {
//                DialogMaker.dismissProgressDialog();
//                LogUtils.d("requestVerifyCode fail!  throwable : " + t.getMessage());
//                GlobalToastUtils.showNormalShort("获取验证码失败!");
//            }
//        });
//    }

    /**
     * 倒计时60秒，间隔一秒
     */
    private void startCountDown() {
        stopCountDown();
        countDownTimer = new CountDownTimer(millisInFuture * 1000, countDownInterval) {
            @Override
            public void onTick(long second) {
                tvCountDownSec.setText(String.valueOf(second / 1000));
            }

            @Override
            public void onFinish() {
                startCountDown();
                etPreFirstPwd.setFocusable(true);
                etPreFirstPwd.setFocusableInTouchMode(true);
                etPreFirstPwd.requestFocus();
                showKeyboardDelayed(etPreFirstPwd);
//                requestOldVerifyCode(mobile);
            }
        };
        countDownTimer.start();
    }

    private void stopCountDown() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopCountDown();
    }
}
