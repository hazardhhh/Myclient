package com.example.client.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public abstract class BaseFragment extends Fragment {
    protected String TAG;

    protected Activity mActivity;
    protected Context mContext;

    /**
     *  管理订阅事件 disposable
     */
    protected CompositeDisposable mDisposable;

    protected View root=null;

    /**
     *  ButterKnife
     */
    private Unbinder unbinder;

    @Override
    public void onAttach(@NonNull Context context) {
        mActivity = (Activity) context;
        mContext = context;
        super.onAttach(context);
    }

    /**
     *  abstract
     */
    protected abstract int getLayoutId();

    /**
     *  初始化
     */
    protected void addDisposable(Disposable d){
        if(mDisposable == null){
            mDisposable = new CompositeDisposable();
        }
        mDisposable.add(d);
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
    protected void initWidget(Bundle savedInstanceState){
    }

    /**
     *  初始化点击事件
     */
    protected void initClick(){
    }

    /**
     *  执行逻辑
     */
    protected void processLogic(){
    }

    protected void beforeDestroy(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        int resId = getLayoutId();
        setHasOptionsMenu(true);
        root = inflater.inflate(resId,container,false);
        unbinder = ButterKnife.bind(this,root);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view,savedInstanceState);

        initData(savedInstanceState);
        TAG = getName();
        initWidget(savedInstanceState);
        initClick();
        processLogic();

    }

    @Override
    public void onDetach() {
        super.onDetach();
        beforeDestroy();
        if(mDisposable != null){
            mDisposable.clear();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public String getName(){
        return getClass().getName();
    }

    protected <VT> VT getViewById(int id){
        if (root == null){
            return null;
        }
        return (VT) root.findViewById(id);
    }

}
