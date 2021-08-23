package com.example.client.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.client.R;
import com.example.client.base.BaseActivity;
import com.example.client.utils.ToastUtils;

public class MessageTopDialogActivity extends Activity implements View.OnClickListener {
    private LinearLayout message_dialog;
    private LinearLayout layout_group_chat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_top_dialog);
        //设置对话框activity的宽度等于屏幕宽度，一定要设置，不然对话框会显示不全
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);//需要添加的语句

        message_dialog=(LinearLayout)findViewById(R.id.message_dialog);
        layout_group_chat=(LinearLayout)findViewById(R.id.layout_group_chat) ;
        message_dialog.setOnClickListener(this);
        layout_group_chat.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.layout_group_chat:
                ToastUtils.show(this,"发起群聊");
                break;

            default:
                break;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        finish();
        return true;
    }

}

//public class MessageTopDialogActivity extends BaseActivity implements View.OnClickListener {
//    private LinearLayout message_dialog;
//    private LinearLayout layout_group_chat;
//
//    @Override
//    protected int getLayoutId() {
//        return R.layout.message_top_dialog;
//    }
//
//    @Override
//    protected void initData(Bundle savedInstanceState) {
//        super.initData(savedInstanceState);
////        //设置对话框activity的宽度等于屏幕宽度，一定要设置，不然对话框会显示不全
////        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);//需要添加的语句
//    }
//
//    @Override
//    protected void initWidget() {
//        super.initWidget();
//        message_dialog=(LinearLayout)findViewById(R.id.message_dialog);
//        layout_group_chat=(LinearLayout)findViewById(R.id.layout_group_chat) ;
//    }
//
//    @Override
//    protected void initClick() {
//        super.initClick();
//        message_dialog.setOnClickListener(this);
//        layout_group_chat.setOnClickListener(this);
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()){
//            case R.id.layout_group_chat:
//                ToastUtils.show(this,"发起群聊");
//                break;
//
//            default:
//                break;
//        }
//    }
//
//    @Override
//    public boolean onTouchEvent(MotionEvent event){
//        finish();
//        return true;
//    }
//
//}