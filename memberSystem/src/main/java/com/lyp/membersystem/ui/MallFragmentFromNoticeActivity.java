package com.lyp.membersystem.ui;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import com.lyp.membersystem.LoginActivity;
import com.lyp.membersystem.R;
import com.lyp.membersystem.base.BaseApplication;
import com.lyp.membersystem.bean.ProductTypeBean;
import com.lyp.membersystem.log.LogUtils;
import com.lyp.membersystem.net.Errors;
import com.lyp.membersystem.net.MessageContants;
import com.lyp.membersystem.net.NetProxyManager;
import com.lyp.membersystem.service.ShoppingCartService;
import com.lyp.membersystem.ui.fragment.AAFrag;
import com.lyp.membersystem.ui.fragment.BBFrag;
import com.lyp.membersystem.ui.fragment.BFrag;
import com.lyp.membersystem.ui.fragment.CCFrag;
import com.lyp.membersystem.ui.fragment.CFrag;
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
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

public class MallFragmentFromNoticeActivity extends FragmentActivity {

	private static final int ALERT_WINDOW_PERMISSION_CODE = 0x521;
	/** 资源对象 */
	public Resources res;
	PagerSlidingTabStrip tabs;
	ViewPager pager;
	DisplayMetrics dm;
	TextView tv_title;
	AAFrag afrag;
	BBFrag bfrag;
	CCFrag cfrag;
	String[] titles = { "用途", "节庆", "对象" };
	private WaitDialog mWaitDialog;
	private SharedPreferences mSharedPreferences;
	private HashMap<String, ArrayList<ProductTypeBean>> productTypeMap;
	private String customerIds;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MessageContants.MSG_PRODUCT_TYPE: {
				parseGetProductType((String) msg.obj);
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
		BaseApplication.getInstance().addActivity(this);
		BaseApplication.getInstance().addActivityToAll(this);
//		if (Build.VERSION.SDK_INT >= 23) {
//			if (!Settings.canDrawOverlays(this)) {
//				Toast.makeText(this, "当前无权限使用悬浮窗，请授权！", Toast.LENGTH_SHORT).show();
//				Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
//						Uri.parse("package:" + getPackageName()));
//				startActivityForResult(intent, ALERT_WINDOW_PERMISSION_CODE);
//			}
//		}
		initView();
		getData();
		// setContentView(R.layout.mall_layout);
		// startActivity(new Intent().setClass(this,
		// ViewPageFragmentActivity.class));
	}

	@Override
	protected void onResume() {
		super.onResume();
//		if (Build.VERSION.SDK_INT < 23 || Settings.canDrawOverlays(this)) {
//			Intent intentService = new Intent(this, ShoppingCartService.class);
//			startService(intentService);
//		}
	}

	@Override
	protected void onPause() {
		super.onPause();
//		if (Build.VERSION.SDK_INT < 23 || Settings.canDrawOverlays(this)) {
//			Intent intent = new Intent(this, ShoppingCartService.class);
//			stopService(intent);
//		}
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
		setContentView(R.layout.activity_slidingtab);
		tv_title = (TextView) findViewById(R.id.tv_title);
		dm = getResources().getDisplayMetrics();
		pager = (ViewPager) findViewById(R.id.pager);
		tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
		// pager.setAdapter(new MyAdapter(getSupportFragmentManager(), titles));
		// tabs.setViewPager(pager);
	}

	private void getData() {
		customerIds = getIntent().getStringExtra("customerIds");
		productTypeMap = new HashMap<String, ArrayList<ProductTypeBean>>();
		mSharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCE, MODE_PRIVATE);
		String tokenid = mSharedPreferences.getString(Constant.TOKEN_ID, "");
		String saleId = mSharedPreferences.getString(Constant.ID, "");
		NetProxyManager.getInstance().toGetProductType(mHandler, tokenid);
	}

	/**
	 * to parse login infomations from network
	 */
	private void parseGetProductType(String result) {
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
			JSONArray array = json.getJSONArray("object");
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = array.getJSONObject(i);
				String id = obj.getString("id");
				if (obj.has("name")) {
				    titles[i] = obj.getString("name");
				}
				JSONArray childArray = obj.getJSONArray("childrens");
				ArrayList<ProductTypeBean> productTypes = new ArrayList<ProductTypeBean>();
				for (int j = 0; j < childArray.length(); j++) {
					JSONObject childObj = childArray.getJSONObject(j);
					ProductTypeBean productTypeBean = new ProductTypeBean();
					productTypeBean.setId(childObj.getString("id"));
					productTypeBean.setName(childObj.getString("name"));
					LogUtils.d(childObj.getString("name"));
					productTypeBean.setpId(childObj.getString("pId"));
				    productTypeBean.setIcon(childObj.getString("icon"));
					productTypes.add(productTypeBean);
				}
				productTypeMap.put(String.valueOf(i + 1), productTypes);
			}
			pager.setAdapter(new MyAdapter(getSupportFragmentManager(), titles));
			tabs.setViewPager(pager);
			// ToastUtil.showShort(this, "ok--" + object);
		} catch (Exception ex) {
			LogUtils.e(ex.getMessage());
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
				if (afrag == null) {
					afrag = new AAFrag();
					Bundle bundle = new Bundle();
					bundle.putParcelableArrayList("afrag", productTypeMap.get("1"));
					bundle.putString("customerIds", customerIds);
					afrag.setArguments(bundle);
				}
				return afrag;
			case 1:
				if (bfrag == null) {
					bfrag = new BBFrag();
					Bundle bundle = new Bundle();
					bundle.putParcelableArrayList("bfrag", productTypeMap.get("2"));
					bundle.putString("customerIds", customerIds);
					bfrag.setArguments(bundle);
				}
				return bfrag;
			case 2:
				if (cfrag == null) {
					cfrag = new CCFrag();
					Bundle bundle = new Bundle();
					bundle.putParcelableArrayList("cfrag", productTypeMap.get("3"));
					bundle.putString("customerIds", customerIds);
					cfrag.setArguments(bundle);
				}
				return cfrag;
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

	/**
	 * 用户返回
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == ALERT_WINDOW_PERMISSION_CODE) {
			if (!Settings.canDrawOverlays(this)) {
				Toast.makeText(this, "权限授予失败，无法开启悬浮窗", Toast.LENGTH_SHORT).show();
			} else {
				Intent intentService = new Intent(this, ShoppingCartService.class);
				startService(intentService);
			}

		}
	}
	
	@Override
	protected void onDestroy() {
		BaseApplication.getInstance().removeActivity(this);
		BaseApplication.getInstance().removeActivityFromAll(this);
		super.onDestroy();
	};
}
