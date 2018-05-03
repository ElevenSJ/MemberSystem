package com.sj.activity;

import android.support.annotation.NonNull;
import android.view.MenuItem;

import com.lyp.membersystem.R;
import com.sj.activity.adapter.FragmentAdapter;
import com.sj.activity.base.ActivityBase;
import com.sj.activity.base.FragmentBase;
import com.sj.widgets.NoScrollViewPager;

import java.util.ArrayList;
import java.util.List;

public class ActivityMain extends ActivityBase{

	private NoScrollViewPager mPager;
	private List<FragmentBase> mFragments = new ArrayList<>(3);
	private FragmentAdapter mAdapter;

	@Override
	public int getContentLayout() {
		// TODO Auto-generated method stub
		return R.layout.activity_main;
	}

	@Override
	public void initView() {
		BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
		navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
		initViewPager();
	}



	private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
			= new BottomNavigationView.OnNavigationItemSelectedListener() {

		@Override
		public boolean onNavigationItemSelected(@NonNull MenuItem item) {
			int i = item.getItemId();
			if (i == R.id.navigation_home) {
				mPager.setCurrentItem(0);
				return true;
			} else if (i == R.id.navigation_dashboard) {
				mPager.setCurrentItem(1);
				return true;
			} else if (i == R.id.navigation_notifications) {
				mPager.setCurrentItem(2);
				return true;
			}
			return false;
		}

	};

	private void initViewPager() {
//		mFragments.add(newsFragment);
		mPager = (NoScrollViewPager) findViewById(R.id.container_pager);
		mAdapter = new FragmentAdapter(getSupportFragmentManager(), mFragments);
		mPager.setPagerEnabled(false);
		mPager.setAdapter(mAdapter);
	}


}
