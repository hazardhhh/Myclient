package com.example.client.ui.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.client.R;
import com.example.client.base.BaseFragment;
import com.example.client.model.event.Event;
import com.example.client.ui.activity.AchieveDataActivity;
import com.example.client.ui.activity.CostQueryActivity;
import com.example.client.ui.activity.PersonIntroduce;
import com.example.client.utils.ImageUtils;
import com.example.client.utils.SharedPrefUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MineFragment extends BaseFragment implements View.OnClickListener{

    private TextView achieve_data;
    private LinearLayout person_introduce_show;
    private LinearLayout cost_query_show;
    private ImageView iv_mine_default_icon;

    private Bitmap bitmap;

    private static final String STORE_KEY = "key_one";

    @Override
    protected int getLayoutId() {
        return R.layout.mine_fragment;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        //注册 EventBus
        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }
    }

    //使用EventBus传递数据
    public String saveIcon;
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(Event event) {
        //event就是拿到接收的数据
        saveIcon=event.getAchieveDataPhone();
        SharedPrefUtil.putString(mContext,"sIcon",saveIcon);
        if(event.getAchieveDataPhone() != "") {
            //将字符串转为图片
            byte[] decode = Base64.decode(event.getAchieveDataPhone().getBytes(), 1);
            bitmap = BitmapFactory.decodeByteArray(decode, 0, decode.length);
            //将图片转为圆形
            bitmap = ImageUtils.toRoundBitmap(bitmap);
            iv_mine_default_icon.setImageBitmap(bitmap);
        }
    }

    @Override
    protected void initWidget(Bundle savedInstanceState) {
        super.initWidget(savedInstanceState);
        achieve_data = (TextView) getViewById(R.id.tv_achieve_data);
        person_introduce_show=(LinearLayout)getViewById(R.id.person_introduce_show);
        cost_query_show=(LinearLayout)getViewById(R.id.cost_query_show);
        iv_mine_default_icon=(ImageView)getViewById(R.id.iv_mine_default_icon);

    }

    @Override
    protected void initClick() {
        super.initClick();
        achieve_data.setOnClickListener(this);
        person_introduce_show.setOnClickListener(this);
        cost_query_show.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_achieve_data:
                Intent intent_mine_achieve_data=new Intent(getActivity(), AchieveDataActivity.class);
                getActivity().startActivity(intent_mine_achieve_data);
                break;

            case R.id.person_introduce_show:
                Intent intent_mine_person_introduce=new Intent(getActivity(), PersonIntroduce.class);
                getActivity().startActivity(intent_mine_person_introduce);
                break;

            case R.id.cost_query_show:
                Intent intent_cost_query_show=new Intent(getActivity(), CostQueryActivity.class);
                getActivity().startActivity(intent_cost_query_show);
                break;

            default:
                break;
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        String sIcon=SharedPrefUtil.getString(mContext,"sIcon","");
        if(sIcon != "") {
            //将字符串转为图片
            byte[] decode = Base64.decode(sIcon.getBytes(), 1);
            bitmap = BitmapFactory.decodeByteArray(decode, 0, decode.length);
            //将图片转为圆形
            bitmap = ImageUtils.toRoundBitmap(bitmap);
            iv_mine_default_icon.setImageBitmap(bitmap);
        }
    }

    @Override
    protected void beforeDestroy() {
        EventBus.getDefault().unregister(this);
    }
}
