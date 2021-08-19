package com.example.client.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.client.R;
import com.example.client.utils.TimeSPUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 刷新控制 view
 *
 */
public class NormalRefreshView extends RefreshBaseView {

    //下拉刷新相关布局
    private LinearLayout ll_ok;
    private LinearLayout ll_refresh;
    private ImageView iv_refresh, iv_ok;
    private TextView tv_tip, tv_time, tv_ok;
    private ProgressBar pb_refresh;

    public NormalRefreshView(Context context) {
        super(context);
    }

    public NormalRefreshView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected View getRefreshHeaderView() {
        View refreshView = LayoutInflater.from(mContext).inflate(R.layout.layout_refresh_header, null);
        ll_ok = (LinearLayout) refreshView.findViewById(R.id.ll_ok);
        ll_refresh = (LinearLayout) refreshView.findViewById(R.id.ll_refresh);
        iv_refresh = (ImageView) refreshView.findViewById(R.id.iv_refresh);
        iv_ok = (ImageView) refreshView.findViewById(R.id.iv_ok);
        tv_tip = (TextView) refreshView.findViewById(R.id.tv_tip);
        tv_time = (TextView) refreshView.findViewById(R.id.tv_time);
        tv_ok = (TextView) refreshView.findViewById(R.id.tv_ok);
        pb_refresh = (ProgressBar) refreshView.findViewById(R.id.pb_refresh);
        return refreshView;
    }

    @Override
    protected void doMovement(LinearLayout.LayoutParams lp, int lastTop) {
        lp.topMargin = lastTop;
        if (lastTop < 0) {//下拉刷新状态
            if (refreshState != REFRESH_BY_PULLDOWN) {
                pullDownToRefresh();
            }
        } else {//松开刷新状态
            if (refreshState != REFRESH_BY_RELEASE) {
                pullUpToRefresh();
            }
        }
    }

    @Override
    protected void doMoveUp(LinearLayout.LayoutParams lp) {
        //未拉到触发可刷新事件的状态，则直接收回
        if (refreshState == REFRESH_BY_PULLDOWN) {
            animRefreshView(500, TAKEBACK_ALL);
        } else {//松开刷新
            animRefreshView(300, TAKEBACK_REFRESH);
            refreshing();
            if (refreshListener != null) {
                refreshListener.onRefresh();
                setRefreshState(REFRESHING);
            }
        }
    }

    /**
     * 下拉刷新状态
     */
    @Override
    public void pullDownToRefresh() {
        setRefreshState(REFRESH_BY_PULLDOWN);
        ll_refresh.setVisibility(View.VISIBLE);
        ll_ok.setVisibility(View.GONE);
        tv_tip.setText("下拉刷新");
        getRefreshTime();
        //x轴的值，0.5f表明是以自身这个控件的一半长度为x轴
        RotateAnimation anim1 = new RotateAnimation(0, 180,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        //设置动画持续时间
        anim1.setDuration(300);
        //设置重复次数
//        anim1.setRepeatMode(-1);
        //动画终止时停留在最后一帧
        anim1.setFillAfter(true);
//        anim1.setInterpolator(new LinearInterpolator());
        iv_refresh.setVisibility(View.VISIBLE);
        iv_refresh.clearAnimation();
//        iv_refresh.setAnimation(anim1);
        iv_refresh.startAnimation(anim1);
        pb_refresh.setVisibility(View.GONE);

    }

    /**
     * 松开刷新状态
     */
    @Override
    public void pullUpToRefresh() {
        setRefreshState(REFRESH_BY_RELEASE);
        ll_refresh.setVisibility(View.VISIBLE);
        ll_ok.setVisibility(View.GONE);
        tv_tip.setText("松开刷新");
        getRefreshTime();
        iv_refresh.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.pull_up));
        RotateAnimation anim1 = new RotateAnimation(180, 0,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        anim1.setDuration(300);
//        anim1.setRepeatMode(-1);
        anim1.setFillAfter(true);
        iv_refresh.clearAnimation();
        iv_refresh.startAnimation(anim1);
        pb_refresh.setVisibility(View.GONE);
        iv_refresh.setVisibility(View.VISIBLE);
    }

    /**
     * 正在刷新状态
     */
    @Override
    public void refreshing() {
        setRefreshState(REFRESHING);
        ll_refresh.setVisibility(View.VISIBLE);
        ll_ok.setVisibility(View.GONE);
        tv_tip.setText("正在刷新...");
        getRefreshTime();
        TimeSPUtil.getInstance(mContext).setRefreshTime("MyMobile", "" +
                getDate( "MM-dd HH:mm", System.currentTimeMillis()));
        iv_refresh.clearAnimation();
        iv_refresh.setVisibility(View.GONE);
        pb_refresh.setVisibility(View.VISIBLE);
        RotateAnimation anim1 = new RotateAnimation(0, 360,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        anim1.setDuration(300);
        anim1.setRepeatMode(-1);
        anim1.setFillAfter(true);
        pb_refresh.clearAnimation();
        pb_refresh.startAnimation(anim1);

    }

    /**
     * 刷新成功状态
     */
    @Override
    public void refreshOK() {
        setRefreshState(REFRESHING_SUCCESS);
        ll_refresh.setVisibility(View.GONE);
        ll_ok.setVisibility(View.VISIBLE);
        tv_ok.setText("刷新成功");
        iv_ok.setImageDrawable(getResources().getDrawable(R.mipmap.pull_ok));
    }

    /**
     * 刷新失败状态
     */
    @Override
    public void refreshFailed() {
        setRefreshState(REFRESHING_FAILED);
        ll_refresh.setVisibility(View.GONE);
        ll_ok.setVisibility(View.VISIBLE);
        tv_ok.setText("刷新失败");
        iv_ok.setImageDrawable(getResources().getDrawable(R.mipmap.pull_failure));
    }


    public void getRefreshTime() {
        String time = TimeSPUtil.getInstance(mContext).getRefreshTime("MyMobile");
        if (time == null || "".equals(time)) {
            tv_time.setVisibility(View.GONE);
        } else {
            tv_time.setVisibility(View.VISIBLE);
            tv_time.setText(String.format("上次刷新:%s", time));
        }
    }

    public static String getDate(String formatStr, long times) {
        Date date = new Date(times);// 获取当前日期对象
        SimpleDateFormat format = new SimpleDateFormat(formatStr);// 设置格式
        return format.format(date);
    }

}
