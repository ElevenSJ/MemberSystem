package com.lyp.membersystem.ui;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.lyp.membersystem.LoginActivity;
import com.lyp.membersystem.R;
import com.lyp.membersystem.base.BaseApplication;
import com.lyp.membersystem.bean.MemberCardBean;
import com.lyp.membersystem.log.LogUtils;
import com.lyp.membersystem.net.Errors;
import com.lyp.membersystem.net.MessageContants;
import com.lyp.membersystem.net.NetProxyManager;
import com.lyp.membersystem.ui.fragment.CashCardFrag;
import com.lyp.membersystem.ui.fragment.ServiceCardFrag;
import com.lyp.membersystem.utils.Constant;
import com.lyp.membersystem.utils.SystemStatusManager;
import com.lyp.membersystem.utils.ToastUtil;
import com.lyp.membersystem.view.dialog.WaitDialog;
import com.lyp.membersystem.view.tabmenu.PagerSlidingTabStrip;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

public class MemberCardFragmentActivity extends FragmentActivity {

	/** 资源对象 */
	public Resources res;
	PagerSlidingTabStrip tabs;
	ViewPager pager;
	DisplayMetrics dm;
	CashCardFrag cashCardFrag;
	ServiceCardFrag serviceCardFrag;
//	String[] titles = new String[2];
	String[] titles = new String[1];
	private WaitDialog mWaitDialog;
	private SharedPreferences mSharedPreferences;
	private ArrayList<MemberCardBean> cashCardList = new ArrayList<MemberCardBean>();
	private ArrayList<MemberCardBean> serviceCardList = new ArrayList<MemberCardBean>();

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MessageContants.MSG_GET_MEMBER_CARD_LIST: {
				parseGetMemberCardList((String) msg.obj);
				break;
			}
			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTranslucentStatus();
		initView();
		BaseApplication.getInstance().addActivityToAll(this);
		getData();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}
	
	@Override
	protected void onDestroy() {
		BaseApplication.getInstance().removeActivityFromAll(this);
		super.onDestroy();
	}

	public void onBack(View view) {
		finish();
	}

	/**
	 * 设置状态栏背景状态
	 */
	private void setTranslucentStatus() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

			// 透明状态栏
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			// 透明导航栏
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		}
		SystemStatusManager tintManager = new SystemStatusManager(this);
		tintManager.setStatusBarTintEnabled(true);
		tintManager.setStatusBarTintResource(R.color.main_bg_color);// 状态栏背景
		getWindow().getDecorView().setFitsSystemWindows(true);
	}

	private void initView() {
		setContentView(R.layout.member_card_slidingtab);
		dm = getResources().getDisplayMetrics();
		pager = (ViewPager) findViewById(R.id.pager);
		tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
		// pager.setAdapter(new MyAdapter(getSupportFragmentManager(), titles));
		// tabs.setViewPager(pager);
	}

	private void getData() {
		mSharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCE, MODE_PRIVATE);
		String tokenid = mSharedPreferences.getString(Constant.TOKEN_ID, "");
		String saleId = mSharedPreferences.getString(Constant.ID, "");
		NetProxyManager.getInstance().toGetMemeberCardList(mHandler, tokenid, null);
	}

	/**
	 * to parse login infomations from network
	 */
	private void parseGetMemberCardList(String result) {
		if (Errors.ERROR_NET.equals(result) || Errors.ERROR_SERVER.equals(result)) {
			ToastUtil.showLong(this, R.string.network_error);
			if (mWaitDialog != null && mWaitDialog.isShowing()) {
				mWaitDialog.dismiss();
			}
			return;
		}
		// to parser json data
		try {
			JSONObject json = new JSONObject(result);
			LogUtils.d("" + json);
			boolean success = json.getBoolean("success");
			if (mWaitDialog != null && mWaitDialog.isShowing()) {
				mWaitDialog.dismiss();
			}
			if (!success) {
				String message = json.getString("message");
				ToastUtil.showShort(this, message);
				if (json.getString("resCode").equals(Constant.RELOGIN)) {
				    backLogin();
				}
				return;
			}
			JSONObject jsonObject = json.getJSONObject("object");
			JSONArray array = jsonObject.getJSONArray("infoList");
			cashCardList.clear();
			serviceCardList.clear();
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = array.getJSONObject(i);
				MemberCardBean memberCardBean = new MemberCardBean();
				memberCardBean.setId(obj.getString("id"));
				memberCardBean.setMoney(obj.getString("money"));
				memberCardBean.setBalance(obj.getString("balance"));
				memberCardBean.setName(obj.getString("name"));
				memberCardBean.setExpiryDate(obj.getString("indate"));
				memberCardBean.setRecordStatus(obj.getString("recordStatus"));
				String type = obj.getString("type");
				memberCardBean.setType(type);
				if (type.equals("1")) {
					cashCardList.add(memberCardBean);
				} else {
					serviceCardList.add(memberCardBean);
				}
			}
			titles[0] = "现金卡(" + cashCardList.size() + ")";
//			titles[1] = "服务卡(" + serviceCardList.size() + ")"; 
			pager.setAdapter(new MyAdapter(getSupportFragmentManager(), titles));
			tabs.setViewPager(pager);
			// ToastUtil.showShort(this, "ok--" + object);
		} catch (Exception ex) {
			LogUtils.e(ex.getMessage());
			ToastUtil.showLongMessage("请检查服务器返回是否正确！");
			return;
		}
	}

	public class MyAdapter extends FragmentPagerAdapter {
		String[] _titles;

		public MyAdapter(FragmentManager fm, String[] titles) {
			super(fm);
			_titles = titles;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return _titles[position];
		}

		@Override
		public int getCount() {
			return _titles.length;
		}

		@Override
		public Fragment getItem(int position) {
			switch (position) {
			case 0:
				if (cashCardFrag == null) {
					cashCardFrag = new CashCardFrag();
					Bundle bundle = new Bundle();
					bundle.putParcelableArrayList("cashcardfrage", cashCardList);
					cashCardFrag.setArguments(bundle);
				}
				return cashCardFrag;
			case 1:
				if (serviceCardFrag == null) {
					serviceCardFrag = new ServiceCardFrag();
					Bundle bundle = new Bundle();
					bundle.putParcelableArrayList("servicecardfrag", serviceCardList);
					serviceCardFrag.setArguments(bundle);
				}
				return serviceCardFrag;
			default:
				return null;
			}
		}
	}

	protected void backLogin() {
		BaseApplication.getInstance().finishAllActivity();
		SharedPreferences mSharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCE, MODE_PRIVATE);
		Editor editor = mSharedPreferences.edit();
		editor.clear().commit();
		Intent i = new Intent();
		i.setClass(this, LoginActivity.class);
		startActivity(i);
		finish();
	}
	
}
