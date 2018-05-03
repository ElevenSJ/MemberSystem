package com.sj.activity.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.sj.activity.base.FragmentBase;

import java.util.List;

/**
 * <p>Fragments适配器 </p>
 *
 * @author 张华洋 2017/9/27 10:14
 * @version V1.1
 * @name ResourcePagerAdapter
 */
public class FragmentAdapter extends FragmentStatePagerAdapter {
    private List<FragmentBase> mFragments;

    public FragmentAdapter(FragmentManager fm, List<FragmentBase> mFragments) {
        super(fm);
        this.mFragments = mFragments;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments != null ? mFragments.size() : 0;
    }

    @Override
    public int getItemPosition(Object object) {
        return android.support.v4.view.PagerAdapter.POSITION_NONE;
    }
}
