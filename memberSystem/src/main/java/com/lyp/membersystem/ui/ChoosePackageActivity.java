package com.lyp.membersystem.ui;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.lyp.membersystem.R;
import com.lyp.membersystem.adapter.ChooseCardAdapter;
import com.lyp.membersystem.adapter.ChoosePackageAdapter;
import com.lyp.membersystem.base.BaseActivity;
import com.lyp.membersystem.base.BaseApplication;
import com.lyp.membersystem.bean.CardEnvelopBean;
import com.lyp.membersystem.bean.PackageBean;
import com.lyp.membersystem.log.LogUtils;
import com.lyp.membersystem.net.Errors;
import com.lyp.membersystem.net.MessageContants;
import com.lyp.membersystem.net.NetProxyManager;
import com.lyp.membersystem.utils.Constant;
import com.lyp.membersystem.utils.SystemStatusManager;
import com.lyp.membersystem.utils.ToastUtil;
import com.lyp.membersystem.view.dialog.WaitDialog;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;

public class ChoosePackageActivity extends BaseActivity {
	
	private WaitDialog mWaitDialog;
	private ListView lv_package;
	private SharedPreferences mSharedPreferences;
	private ChoosePackageAdapter mChoosePackageAdapter;
	private List<PackageBean> list;
	private String good_info;
	private String card_info;
	private String card_content;

	private Handler mainHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MessageContants.MSG_GET_PACKAGE_LIST: {
				parseGetPackageList((String) msg.obj);
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
		BaseApplication.getInstance().addActivity(this);
		setTranslucentStatus();
		setContentView(R.layout.choose_package_layout);
		good_info = getIntent().getStringExtra("good_info");
		card_info = getIntent().getStringExtra("card_info");
		card_content = getIntent().getStringExtra("card_content");
		initView();
		initData();
	}

	private void initView() {
		lv_package = (ListView) findViewById(R.id.lv_package);
		list = new ArrayList<PackageBean>();
		mChoosePackageAdapter = new ChoosePackageAdapter(this, list);
		lv_package.setAdapter(mChoosePackageAdapter);
	}
	
	private void initData() {
		mSharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCE, MODE_PRIVATE);
		String tokenid = mSharedPreferences.getString(Constant.TOKEN_ID, "");
		String saleId = mSharedPreferences.getString(Constant.ID, "");
		if (mWaitDialog == null) {
			mWaitDialog = new WaitDialog(this, R.string.loading_data);
		}
		mWaitDialog.show();
		NetProxyManager.getInstance().toGetPackageList(mainHandler, tokenid, saleId);
	}
	
	public void nextStep(View view) {
		Intent i = new Intent();
		i.setClass(this, ChooseCustomerFromMallActivity.class);
		PackageBean packageBean = list.get(mChoosePackageAdapter.getSelectPackage());
		i.putExtra("good_info", good_info);
		i.putExtra("card_info", card_info);
		i.putExtra("card_content", card_content);
		i.putExtra("package_info", packageBean.getId() + "_" + packageBean.getPrice());
		startActivity(i);
	}

	/**
	 * to parse login infomations from network
	 */
	private void parseGetPackageList(String result) {
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
			JSONObject obj = json.getJSONObject("object");
			JSONArray jsonArray = obj.getJSONArray("infoList");
			list.clear();
			for (int i = 0; i < jsonArray.length(); i++) {
				PackageBean packageBean = new PackageBean();
				JSONObject childObj = jsonArray.getJSONObject(i);
				packageBean.setId(childObj.getString("id"));
				packageBean.setName(childObj.getString("name"));
				packageBean.setPrice(childObj.getString("price"));
				if (childObj.has("picUrl")) {
					packageBean.setIcon(childObj.getString("picUrl"));
				}
				list.add(packageBean);
			}
			mChoosePackageAdapter.updateListView(list);
		} catch (Exception ex) {
			LogUtils.e(ex.getMessage());
			return;
		}
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
	
	@Override
	protected void onDestroy() {
		BaseApplication.getInstance().removeActivity(this);
		super.onDestroy();
	};
}
