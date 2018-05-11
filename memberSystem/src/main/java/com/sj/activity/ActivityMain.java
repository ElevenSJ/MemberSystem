package com.sj.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.view.MenuItem;

import com.lyp.membersystem.R;
import com.lyp.membersystem.utils.Constant;
import com.sj.activity.adapter.FragmentAdapter;
import com.sj.activity.base.ActivityBase;
import com.sj.activity.fragment.FragmentMain;
import com.sj.activity.fragment.FragmentMyNew;
import com.sj.activity.fragment.FragmentOrder;
import com.sj.service.JPushAliasService;
import com.sj.widgets.NoScrollViewPager;

import java.util.ArrayList;
import java.util.List;

public class ActivityMain extends ActivityBase {

    NoScrollViewPager mPager;
    BottomNavigationView navigation;
    private List<Fragment> mFragments = new ArrayList<>(3);
    private FragmentAdapter mAdapter;
    private int index = 0;

    private String phone;

    @Override
    public int getContentLayout() {
        // TODO Auto-generated method stub
        return R.layout.activity_main;
    }

    @Override
    public void initView() {
        navigation = findViewById(R.id.navigation);
        mPager = findViewById(R.id.container_pager);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        initViewPager();
        setAlias();
    }

    private void setAlias() {
        SharedPreferences mSharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCE, MODE_PRIVATE);
        phone = mSharedPreferences.getString(Constant.USER_ACCOUNT, "");
        boolean setAlias = mSharedPreferences.getBoolean(Constant.IS_SET_ALIAS, false);
        if (setAlias) {
            Intent intent = new Intent();
            intent.putExtra("alias",phone);
            intent.setClass(this, JPushAliasService.class);
            startService(intent);
        }
    }


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            int i = item.getItemId();
            if (i == R.id.navigation_home) {
                index = 0;
                mPager.setCurrentItem(0);
                return true;
            } else if (i == R.id.navigation_dashboard) {
                index = 1;
                mPager.setCurrentItem(1);
                return true;
            } else if (i == R.id.navigation_notifications) {
                index = 2;
                mPager.setCurrentItem(2);
                return true;
            }
            return false;
        }

    };

    private void initViewPager() {
        mFragments.add(FragmentMain.newInstance());
        mFragments.add(FragmentOrder.newInstance());
        mFragments.add(FragmentMyNew.newInstance());
        mAdapter = new FragmentAdapter(getSupportFragmentManager(), mFragments);
        mPager.setOffscreenPageLimit(2);
        mPager.setPagerEnabled(false);
        mPager.setAdapter(mAdapter);
        mPager.setCurrentItem(index);
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //互斥登录，退出当前设备
        if (intent != null) {
            if (intent.getBooleanExtra("LoginOut", false)) {
                backLogin();
            }
        }
    }
}
