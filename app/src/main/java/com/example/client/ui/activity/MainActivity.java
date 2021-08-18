package com.example.client.ui.activity;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.client.R;
import com.example.client.base.BaseActivity;
import com.example.client.ui.adapter.FragmentAdapter;
import com.example.client.ui.fragment.ContactsFragment;
import com.example.client.ui.fragment.FindFragment;
import com.example.client.ui.fragment.MessageFragment;
import com.example.client.ui.fragment.MineFragment;
import com.example.client.widget.NormalRefreshView;
import com.example.client.widget.QQRefreshView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class MainActivity extends BaseActivity {

//    private MessageFragment mMessageFragment = HomeFragmentFactory.getInstance().getMessageFragment();
//    private ContactsFragment mContactsFragment = HomeFragmentFactory.getInstance().getContactsFragment();
//    private FindFragment mFindFragment = HomeFragmentFactory.getInstance().getFindFragment();
//    private MineFragment mMineFragment = HomeFragmentFactory.getInstance().getMineFragment();

    private List<Fragment> mFragments = new ArrayList<>();

    @BindView(R.id.bottomNavigationView)
    BottomNavigationView mNavigationView;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.message)
    TextView text_message;

    private RelativeLayout layout_message;
    private RelativeLayout layout_contacts;
    private RelativeLayout layout_find;
    private RelativeLayout layout_mine;

    //仿QQ下拉刷新
//    QQRefreshView refreshableView;

    //正常刷新
    NormalRefreshView normalRefreshView;

    final int SUCCESS = 1;
    final int FAILED = 0;

    /**
     * 退出时间
     */
    private long exitTime;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        mFragments.add(new MessageFragment());
        mFragments.add(new ContactsFragment());
        mFragments.add(new FindFragment());
        mFragments.add(new MineFragment());

//        mNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                switchFragment(item.getItemId());
//                return true;
//            }
//        });
//        switchFragment(R.id.menu_message);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        layout_message=findViewById(R.id.layout_message);
        layout_contacts=findViewById(R.id.layout_contacts);
        layout_find=findViewById(R.id.layout_find);
        layout_mine=findViewById(R.id.layout_mine);

//        //仿QQ刷新
//        refreshableView = (QQRefreshView) findViewById(R.id.refreshableView1);
////        layout = (LinearLayout) findViewById(R.id.ll_layout);
//        refreshableView.setRefreshEnabled(true);
//
//        refreshableView.setRefreshListener(new QQRefreshView.RefreshListener() {
//
//            @Override
//            public void onRefresh() {
//                //定时器
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        handler.sendEmptyMessage(SUCCESS);
//
//                    }
//                }, 500);
//            }
//        });

        //正常刷新
        normalRefreshView=(NormalRefreshView)findViewById(R.id.refreshableView1);
        normalRefreshView.setRefreshEnabled(true);
        normalRefreshView.setRefreshListener(new NormalRefreshView.RefreshListener(){

            @Override
            public void onRefresh() {
                //定时器
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        handler.sendEmptyMessage(SUCCESS);

                    }
                }, 500);
            }
        });

        // 初始化页卡
        initPager();
        //设置当前界面
//        viewPager.setCurrentItem(2);
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case SUCCESS:
//                    refreshableView.finishRefresh(true);
                    normalRefreshView.finishRefresh(true);
                    text_message.setText("信息");
                    break;
                case FAILED:
//                    refreshableView.finishRefresh(false);
                    normalRefreshView.finishRefresh(false);
                    break;
                default:
                    break;
            }
        };
    };

    private void initPager() {
        FragmentAdapter adapter = new FragmentAdapter(mFragments, getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        //BottomNavigationView 点击事件监听
        mNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int menuId = menuItem.getItemId();
                // 跳转指定页面：Fragment
                switch (menuId) {
                    case R.id.menu_message:
                        viewPager.setCurrentItem(0,false);
                        break;
                    case R.id.menu_contacts:
                        viewPager.setCurrentItem(1,false);
                        break;
                    case R.id.menu_find:
                        viewPager.setCurrentItem(2,false);
                        break;
                    case R.id.menu_mine:
                        viewPager.setCurrentItem(3,false);
                        break;
                }
                return false;
            }
        });

        // ViewPager 滑动事件监听
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //将滑动到的页面对应的 menu 设置为选中状态
                mNavigationView.getMenu().getItem(position).setChecked(true);
                if(position==0){
                    layout_message.setVisibility(View.VISIBLE);
                    layout_contacts.setVisibility(View.GONE);
                    layout_find.setVisibility(View.GONE);
                    layout_mine.setVisibility(View.GONE);
                }if(position==1){
                    layout_message.setVisibility(View.GONE);
                    layout_contacts.setVisibility(View.VISIBLE);
                    layout_find.setVisibility(View.GONE);
                    layout_mine.setVisibility(View.GONE);
                } if(position==2){
                    layout_message.setVisibility(View.GONE);
                    layout_contacts.setVisibility(View.GONE);
                    layout_find.setVisibility(View.VISIBLE);
                    layout_mine.setVisibility(View.GONE);
                }if(position==3) {
                    layout_message.setVisibility(View.GONE);
                    layout_contacts.setVisibility(View.GONE);
                    layout_find.setVisibility(View.GONE);
                    layout_mine.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int position) {

            }
        });

    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN
                && event.getRepeatCount() == 0) {
            // 重写键盘事件分发，onKeyDown方法某些情况下捕获不到，只能在这里写
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Snackbar snackbar = Snackbar.make(viewPager, "再按一次退出程序", Snackbar.LENGTH_SHORT);
                snackbar.getView().setBackgroundResource(R.color.colorPrimary);
                snackbar.show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

//    /**
//     * 切换fragment
//     *
//     * @param id
//     * @return
//     */
//    private void switchFragment(int id) {
//        Fragment fragment = null;
//        switch (id) {
//            case R.id.menu_message:
//                fragment = mFragments.get(0);
//                break;
//
//            case R.id.menu_contacts:
//                fragment = mFragments.get(1);
//                break;
//
//            case R.id.menu_find:
//                fragment = mFragments.get(2);
//                break;
//
//            case R.id.menu_mine:
//                fragment = mFragments.get(3);
//                break;
//
//            default:
//                break;
//        }
//        if (fragment != null) {
//            getSupportFragmentManager().beginTransaction().replace(R.id.fl_content,fragment).commit();
//        }
//    }
}