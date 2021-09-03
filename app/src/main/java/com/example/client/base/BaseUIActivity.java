package com.example.client.base;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.blankj.utilcode.util.LogUtils;
import com.example.client.common.dialog.DialogManager;
import com.example.client.common.dialog.LoadingSunflowerDialog;
import com.example.client.common.ui.dialog.DialogMaker;

import io.reactivex.disposables.Disposable;

public abstract class BaseUIActivity<VD extends ViewDataBinding> extends AppCompatActivity {

    Disposable disposable;
    private VD dataBinding;
    public static final String TAG = "baseUIActivity";

    protected abstract int getLayoutId();

    protected void preCreate(){}

    protected abstract void init();

    protected abstract void initEvent();

    protected abstract void initData();

    public VD getDataBinding(){
        return dataBinding;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        preCreate();
        super.onCreate(savedInstanceState);
        dataBinding = DataBindingUtil.setContentView(this, getLayoutId());
        init();
        initEvent();
        initData();
    }

    public void addDisposable(Disposable disposable){
        if (disposable != null && disposable.isDisposed()) {
            disposable.dispose();
        }
        this.disposable = disposable;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (disposable != null && disposable.isDisposed()) {
            disposable.isDisposed();
        }
    }

    public void showLoading(String content){
        LogUtils.d("showLoading");
        DialogManager.Companion.showByService(
                DialogManager.Companion.create(this, content,
                        new LoadingSunflowerDialog(this))
        );
    }

    public void dismissDialog(){
        DialogManager.Companion.dismiss();
    }

}
