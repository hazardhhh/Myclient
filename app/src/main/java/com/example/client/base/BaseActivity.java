package com.example.client.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.client.R;

import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public abstract class BaseActivity extends AppCompatActivity {

    protected static String TAG;

    protected Activity mActivity;
    protected Context mContext;

    protected View rootView;

    /**
     *  管理订阅事件 disposable
     */
    protected CompositeDisposable mDisposable;

    protected Toolbar mToolbar;

    /**
     *  abstract
     */
    protected abstract int getLayoutId();

    /**
     *  初始化
     */
    protected void addDisposable(Disposable disposable){
        if(mDisposable == null){
            mDisposable = new CompositeDisposable();
        }
        mDisposable.add(disposable);
    }

    /**
     *  配置 Toolbar
     */
    protected void setUpToolbar(Toolbar toolbar){

    }

    /**
     * 初始化数据
     *
     * @param savedInstanceState
     */
    protected void initData(Bundle savedInstanceState){

    }

    /**
     *  初始化零件
     */
    protected void initWidget(){

    }

    /**
     *  初始化事件
     */
    protected void initEvent(){

    }

    /**
     *  初始化点击事件
     */
    protected void initClick(){

    }

    /**
     *  执行逻辑
     */
    protected void processLogic(){}

    protected void beforeDestroy(){

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootView = LayoutInflater.from(this).inflate(getLayoutId(),null);
        setContentView(rootView);
        ButterKnife.bind(this);

        mContext=this;
        mActivity=this;

        TAG=this.getClass().getSimpleName();

        initData(savedInstanceState);
        initToolbar();
        initWidget();
        initEvent();
        initClick();
        processLogic();

    }

    protected void initToolbar() {
        mToolbar = findViewById(R.id.app_toolbar);
            if (mToolbar != null){
                supportActionBar(mToolbar);
                setUpToolbar(mToolbar);
            }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beforeDestroy();
        if(mDisposable != null){
            mDisposable.dispose();
        }
    }

    /**
     * method
     * @param toolbar
     */
    protected ActionBar supportActionBar(Toolbar toolbar){
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        return actionBar;
    }
}
