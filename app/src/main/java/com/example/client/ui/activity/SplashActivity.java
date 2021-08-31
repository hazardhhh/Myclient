package com.example.client.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.client.R;
import com.example.client.base.BaseActivity;
import com.example.client.login.LoginActivity;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class SplashActivity extends BaseActivity {
    @Override
    protected int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        //延迟一秒后跳转页面
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                /*页面跳转到主要页面*/
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                SplashActivity.this.finish(); //结束当前activity
            }
        }, 1000);

    }

//    @Override
//    protected int getLayoutId() {
//        return R.layout.activity_splash;
//    }
//
//    @Override
//    protected void initData(Bundle savedInstanceState) {
//        super.initData(savedInstanceState);
//        addDisposable(
//                Observable.just(1500)
//                        .delay(0, TimeUnit.MILLISECONDS)
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe(integer -> {
//                            startActivity(new Intent(SplashActivity.this,MainActivity.class));
//                            finish();
//                        })
//        );
//    }
//
//    @Override
//    protected void initWidget() {
//        super.initWidget();
//    }
}
