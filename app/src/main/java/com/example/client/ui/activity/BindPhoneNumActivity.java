package com.example.client.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

//import com.shenhua.sdk.uikit.PreferencesUcStar;
//import com.shenhua.sdk.uikit.cache.UcUserInfoCache;
import com.example.client.base.UI;
import com.example.client.R;
import com.example.client.model.ToolBarOptions;
import com.ucstar.android.sdk.uinfo.model.UcSTARUserInfo;

public class BindPhoneNumActivity extends UI implements View.OnClickListener {

    private TextView currentPhoneNum;
    private Button changePhoneNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_bind_phone_activity);
        
        ToolBarOptions options = new ToolBarOptions();
        options.titleString = "  ";
//        setToolBar(R.id.toolbar, options);

        initView();
    }

    private void initView() {
        currentPhoneNum = findViewById(R.id.tv_current_phone_number);
        changePhoneNum = findViewById(R.id.btn_bind_phone);
        changePhoneNum.setOnClickListener(this);
        
        initPhoneNum();
    }

    /**
     *  当前手机号
     */
    private void initPhoneNum() {
//        UcSTARUserInfo userInfo = UcUserInfoCache.getInstance().getUserInfo(PreferencesUcStar.getUserAccount());
//        currentPhoneNum.setText(userInfo != null && !TextUtils.isEmpty(userInfo.getMobile()) ? userInfo.getMobile() : "未绑定");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_bind_phone:
                startActivity(new Intent(BindPhoneNumActivity.this, ChangePhoneNumActivity.class));
                break;
            default:
                break;
        }
    }
}
