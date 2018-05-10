package com.sj.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.lyp.membersystem.R;
import com.lyp.membersystem.ui.fragment.CashCardFrag;
import com.lyp.membersystem.view.tabmenu.PagerSlidingTabStrip;
import com.sj.activity.base.ActivityBase;
import com.sj.activity.fragment.CardFragment;

import java.util.ArrayList;
import java.util.List;

public class ActivityCardBag extends ActivityBase {

    PagerSlidingTabStrip tabs;
    ViewPager pager;
    String[] titles = new String[2];
    private SharedPreferences mSharedPreferences;

    List<Fragment> fragmentList = new ArrayList<>(2);

    MyAdapter pageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initView() {
        pager = (ViewPager) findViewById(R.id.pager);
        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        titles[0] = "我的卡券";
        titles[1] = "已使用卡券";
        fragmentList.add(CardFragment.newInstance(0));
        fragmentList.add(CardFragment.newInstance(1));
        pageAdapter = new MyAdapter(getSupportFragmentManager());
        pager.setAdapter(pageAdapter);
        tabs.setViewPager(pager);
        tabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                pager.setCurrentItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void setCardNum(int index, int count, int sideCount) {
        Log.d("setCardNum", "setCardNum: index = "+index);
        if (index == 0) {
            titles[0] = "我的卡券(" + count + ")";
            titles[1] = "已使用卡券(" + sideCount + ")";
            tabs.setViewPager(pager);
        }
    }


    public class MyAdapter extends FragmentPagerAdapter {

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

    }

    @Override
    public int getContentLayout() {
        return R.layout.activity_card_bag;
    }

}
