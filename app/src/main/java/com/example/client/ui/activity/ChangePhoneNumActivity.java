package com.example.client.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.client.base.UI;
import com.example.client.R;
import com.example.client.login.SimpleTextWatcher;
import com.example.client.model.ToolBarOptions;
//import com.shenhua.sdk.uikit.PreferencesUcStar;
//import com.shenhua.sdk.uikit.cache.UcUserInfoCache;
import com.example.client.utils.GlobalToastUtils;
import com.ucstar.android.sdk.uinfo.model.UcSTARUserInfo;

public class ChangePhoneNumActivity extends UI implements View.OnClickListener{

    private static final String TAG= ChangePhoneNumActivity.class.getSimpleName();

    private LinearLayout layoutChangePhone;
    private TextView currentPhoneNum;

    private LinearLayout layoutCountDown;
    private TextView countDownSec;

    private LinearLayout layoutRetrieve;
    private TextView retrievePhone;

    //验证码
    private EditText etFirst;
    private EditText etSecond;
    private EditText etThird;
    private EditText etFourth;
//    private String verifyNum;
    private EditText[] verifyArray;

    private CountDownTimer countDownTimer; //计时器

    private int millisInFuture = 60; //秒数
    private int countDownInterval = 1000; //间隔

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_identity_verify_activity);
        GlobalToastUtils.init(this);

        ToolBarOptions options = new ToolBarOptions();
        options.titleString = "  ";
//        setToolBar(R.id.toolbar, options);

        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startCountDown();
    }

    private void initView() {
        layoutChangePhone = findViewById(R.id.layout_change_phone_hint_first);
        currentPhoneNum = findViewById(R.id.tv_phone_num);
        layoutCountDown = findViewById(R.id.layout_countdown);
        countDownSec = findViewById(R.id.tv_countdown_sec);
        layoutRetrieve = findViewById(R.id.layout_retrieve);
        retrievePhone = findViewById(R.id.tv_retrieve);

        //验证码
        etFirst = findViewById(R.id.et_first_num);
        etSecond = findViewById(R.id.et_second_num);
        etThird = findViewById(R.id.et_third_num);
        etFourth = findViewById(R.id.et_fourth_num);

        retrievePhone.setOnClickListener(this);

        layoutChangePhone.setVisibility(View.VISIBLE);
        layoutCountDown.setVisibility(View.VISIBLE);
        layoutRetrieve.setVisibility(View.VISIBLE);

        initPhoneNum();
        initVerifyNum();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_retrieve:
                //TODO: retrieve phone
                break;
            default:
                break;
        }
    }

    /**
     *  当前手机号
     */
    private void initPhoneNum() {
//        UcSTARUserInfo userInfo = UcUserInfoCache.getInstance().getUserInfo(PreferencesUcStar.getUserAccount());
//        currentPhoneNum.setText(userInfo != null && !TextUtils.isEmpty(userInfo.getMobile()) ? userInfo.getMobile() : "未绑定");
    }

    private void initVerifyNum() {
        verifyArray = new EditText[]{etFirst, etSecond, etThird, etFourth};
        //自动跳到下一个输入框
        for (int i = 0; i < verifyArray.length; i++) {
            final int j = i;
            verifyArray[j].addTextChangedListener(new TextWatcher() {
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
                    if (temp.length() == 1 && j >= 0 && j < verifyArray.length - 1) {
                        verifyArray[j+1].setFocusable(true);
                        verifyArray[j+1].setFocusableInTouchMode(true);
                        verifyArray[j+1].requestFocus();
                    }
                    if (temp.length() == 0) {
                        if (j >= 1) {
                            verifyArray[j - 1].setFocusable(true);
                            verifyArray[j - 1].setFocusableInTouchMode(true);
                            verifyArray[j - 1].requestFocus();
                        }
                    }
                    //text
                    if (!TextUtils.isEmpty(etFirst.getText().toString().trim()) && !TextUtils.isEmpty(etSecond.getText().toString().trim()) &&
                            !TextUtils.isEmpty(etThird.getText().toString().trim()) && !TextUtils.isEmpty(etFourth.getText().toString().trim())) {
                        GlobalToastUtils.showNormalShort(etFirst.getText().toString().trim() + etSecond.getText().toString().trim() +
                                etThird.getText().toString().trim() + etFourth.getText().toString().trim());
                    }
                }
            });
        }

        //隐藏光标
        etFirst.setCursorVisible(false);
        etSecond.setCursorVisible(false);
        etThird.setCursorVisible(false);
        etFourth.setCursorVisible(false);
        etFirst.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_DOWN == event.getAction()) {
                    etFirst.setCursorVisible(true);
                }
                return false;
            }
        });
        etSecond.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_DOWN == event.getAction()) {
                    etFirst.setCursorVisible(true);
                }
                return false;
            }
        });
        etThird.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_DOWN == event.getAction()) {
                    etFirst.setCursorVisible(true);
                }
                return false;
            }
        });
        etFourth.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_DOWN == event.getAction()) {
                    etFirst.setCursorVisible(true);
                }
                return false;
            }
        });

        //输入结束隐藏键盘
        etFourth.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                if (!TextUtils.isEmpty(s.toString())) {
                    imm.hideSoftInputFromWindow(etFourth.getWindowToken(), 0);
                }
            }
        });
    }

    /**
     *  倒计时60秒，间隔一秒
     */
    private void startCountDown() {
        stopCountDown();
        countDownTimer = new CountDownTimer(millisInFuture * 1000, countDownInterval) {
            @Override
            public void onTick(long second) {
                countDownSec.setText(String.valueOf(second / 1000));
            }

            @Override
            public void onFinish() {

            }
        };
        countDownTimer.start();
    }

    private void stopCountDown() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

}
