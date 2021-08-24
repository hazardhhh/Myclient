package com.example.client.ui.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.client.R;
import com.example.client.base.BaseActivity;
import com.google.android.material.tabs.TabLayout;


import butterknife.BindView;

public class CostQueryActivity extends BaseActivity {

    @BindView(R.id.mine_tab_layout)
    TabLayout mineTabLayout;
    @BindView(R.id.mine_view_pager)
    ViewPager mineViewPager;

    /**
     * 缴费查询标题
     */
    private final int[] TAB_TITLES = new int[]{R.string.mine_cost_query_days, R.string.mine_cost_query_month,
            R.string.mine_cost_query_months, R.string.mine_cost_query_year};

    /**
     * 缴费查询页卡适配器
     */
    private PagerAdapter mineCostQueryAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.mine_costquery;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);

        setTabs(mineTabLayout, getLayoutInflater(), TAB_TITLES);

        // 初始化页卡
        initPager();
    }


    @Override
    protected void initWidget() {
        super.initWidget();

    }
    /**
     * 设置tab显示效果
     * @param tabLayout
     * @param inflater
     * @param tabTitles
     */
    private void setTabs(TabLayout tabLayout, LayoutInflater inflater, int[] tabTitles) {
        for (int i = 0; i < tabTitles.length; i++) {
            TabLayout.Tab tab = tabLayout.newTab();
            View view = inflater.inflate(R.layout.mine_item_top_menu, null);
            // 使用自定义视图，目的是为了便于修改，也可使用自带的视图
            tab.setCustomView(view);
            TextView tvTitle = (TextView) view.findViewById(R.id.mine_text_tab);
            tvTitle.setText(tabTitles[i]);
            tabLayout.addTab(tab);
        }
    }

    private void initPager() {
//        mineCostQueryAdapter = new MainFragmentAdapter(getSupportFragmentManager());
        mineViewPager.setAdapter(mineCostQueryAdapter);

        // 关联切换
        mineViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mineTabLayout));
        mineTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // 取消平滑切换
                mineViewPager.setCurrentItem(tab.getPosition(), false);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}
