package com.example.client.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.StringUtils;
//import com.shenhua.sdk.uikit.PreferencesUcStar;
//import com.shenhua.sdk.uikit.cache.UcUserInfoCache;
import com.example.client.base.UI;
import com.example.client.common.ui.dialog.DialogMaker;
import com.example.client.model.ToolBarOptions;
import com.example.client.R;
import com.example.client.login.RetrofitService;
//import com.example.client.retrofit.BaseResponse;
//import com.example.client.retrofit.Data;
//import com.example.client.retrofit.RetrofitClient;
import com.example.client.utils.GlobalToastUtils;
import com.ucstar.android.sdk.uinfo.model.UcSTARUserInfo;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class BindPhoneNumActivity extends UI implements View.OnClickListener {

    private static final int REQUEST_CHANGE_PHONE_CODE = 330;

    private TextView tvCurrentPhone;
    private Button btnBindPhone;
    private String mobileStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_bind_phone_activity);

//        ToolBarOptions options = new ToolBarOptions();
//        options.titleString = "  ";
//        setToolBar(R.id.toolbar, options);

        initView();

        initPhoneNum();
    }

    private void initView() {
        tvCurrentPhone = findViewById(R.id.tv_current_phone_number);
        btnBindPhone = findViewById(R.id.btn_bind_phone);
        btnBindPhone.setOnClickListener(this);
    }

    /**
     * 当前手机号
     */
    private void initPhoneNum() {
//        UcSTARUserInfo userInfo = UcUserInfoCache.getInstance().getUserInfo(PreferencesUcStar.getUserAccount());
//        tvCurrentPhone.setText(userInfo != null && !TextUtils.isEmpty(userInfo.getMobile()) ? userInfo.getMobile() : "未绑定");
//        mobileStr = userInfo.getMobile();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_bind_phone:
//                requestVerifyCode(mobileStr);
                break;
            default:
                break;
        }
    }

    //获取修改手机号码验证码
//    private void requestVerifyCode(String mobile) {
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
//                LogUtils.d("requestVerifyCode", response.toString());
//                GlobalToastUtils.showNormalShort(data.getMessage());
//                if (data.getCode() == Data.CODE_SUCCESS) {
//                    Intent intent = new Intent(com.shenhua.zhihui.main.activity.BindPhoneNumActivity.this, ChangePhoneNumActivity.class);
//                    startActivityForResult(intent, REQUEST_CHANGE_PHONE_CODE);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<BaseResponse<Object>> call, Throwable t) {
//                DialogMaker.dismissProgressDialog();
//                LogUtils.d("requestVerifyCode fail!  throwable : " + t.getMessage());
//                GlobalToastUtils.showNormalShort(R.string.net_error);
//            }
//        });
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHANGE_PHONE_CODE && resultCode == Activity.RESULT_OK) {
            setResult(Activity.RESULT_OK);
            BindPhoneNumActivity.this.finish();
        }
    }
}
