package com.example.client.ui.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import com.example.client.R;
import com.example.client.base.BaseActivity;
import com.example.client.utils.ToastUtils;

import butterknife.BindView;

public class PersonIntroduce extends BaseActivity {

    //动态改变文字
    @BindView(R.id.text_edit_Status)
    TextView textEditStatus;
    @BindView(R.id.text_edit_Status_honour)
    TextView textEditStatusHonour;
    //输入框
    @BindView(R.id.edit_person_introduce)
    EditText edit_person_introduce;
    @BindView(R.id.edit_person_honour)
    EditText edit_person_honour;

    @Override
    protected int getLayoutId() {
        return R.layout.mine_person_introduce;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
    }

    @Override
    protected void initWidget() {
        super.initWidget();

        edit_person_introduce.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                textEditStatus.setText(String.valueOf(s.length())+"/200");
                if (s.length()>=200){
                    ToastUtils.show(mContext,R.string.show_person_introduce_honour);
                }
            }
        });

        edit_person_honour.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                textEditStatusHonour.setText(String.valueOf(s.length())+"/200");
                if (s.length()>=200){
                    ToastUtils.show(mContext,R.string.show_person_introduce_honour);
                }
            }
        });

    }




}
