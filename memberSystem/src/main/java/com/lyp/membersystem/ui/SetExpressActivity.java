package com.lyp.membersystem.ui;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.lyp.membersystem.R;
import com.lyp.membersystem.adapter.ExpressAddressAdapter;
import com.lyp.membersystem.base.BaseActivity;
import com.lyp.membersystem.base.BaseApplication;
import com.lyp.membersystem.bean.ExpressAddressBean;
import com.lyp.membersystem.log.LogUtils;
import com.lyp.membersystem.net.Errors;
import com.lyp.membersystem.net.MessageContants;
import com.lyp.membersystem.net.NetProxyManager;
import com.lyp.membersystem.utils.Constant;
import com.lyp.membersystem.utils.SystemStatusManager;
import com.lyp.membersystem.utils.ToastUtil;
import com.lyp.membersystem.view.dialog.WaitDialog;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;

public class SetExpressActivity extends BaseActivity {

	private final static int ADD_EXPRESS_ADDRESS = 0x1314;
	private SharedPreferences mSharedPreferences;
	private WaitDialog mWaitDialog;
	private ListView address_list;
	private List<ExpressAddressBean> list;
	private ExpressAddressAdapter adapter;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MessageContants.MSG_GET_MEMBER_ADDRESS_LIST: {
				parseGetMemberAddressList((String) msg.obj);
				break;
			}
			case MessageContants.MSG_DELETE_MEMBER_ADDRESS: {
				parseDeleteMemberAddress((String) msg.obj);
				break;
			}
			case MessageContants.MSG_UPDATE_MEMBER_DEFAULT_ADDRESS: {
				parseUpdateDefaultMemberAddress((String) msg.obj);
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
		setContentView(R.layout.express_address_layout);
		initView();
		initData();
	}

	private void initView() {
		address_list = (ListView) findViewById(R.id.address_list);
		list = new ArrayList<ExpressAddressBean>();
		adapter = new ExpressAddressAdapter(list, this, mHandler, false);
		address_list.setAdapter(adapter);
	}

	private void initData() {
		mSharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCE, Activity.MODE_PRIVATE);
		String tokenid = mSharedPreferences.getString(Constant.TOKEN_ID, "");
		// String saleId = mSharedPreferences.getString(Constant.ID,
		// "");
		if (mWaitDialog == null) {
			mWaitDialog = new WaitDialog(this, R.string.loading_data);
		}
		mWaitDialog.show();
		NetProxyManager.getInstance().toGetMemberAddressList(mHandler, tokenid, 1, 100);
	}

	public void addNewAddress(View view) {
		Intent i = new Intent();
		i.setClass(this, AddExpressActivity.class);
		startActivityForResult(i, ADD_EXPRESS_ADDRESS);
	}

	public void onBack(View view) {
		finish();
	}

	private void parseGetMemberAddressList(String result) {
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
			LogUtils.d("lyp" + json);
			if (!success) {
				String message = json.getString("message");
				ToastUtil.showShort(this, message);
				if (json.getString("resCode").equals(Constant.RELOGIN)) {
					backLogin();
				}
				return;
			}

			JSONObject obj = json.getJSONObject("object");
			JSONArray array = obj.getJSONArray("infoList");
			list.clear();
			for (int i = 0; i < array.length(); i++) {
				JSONObject job = array.getJSONObject(i);
				ExpressAddressBean bean = new ExpressAddressBean();
				bean.setId(job.getString("id"));
				bean.setTagName(job.getString("aliasName"));
				bean.setName(job.getString("name"));
				bean.setPhoneNumber(job.getString("phone"));
				bean.setArea(job.getString("district"));
				bean.setAddress(job.getString("address"));
//				if (job.has("isDefault")) {
				    bean.setFlag(job.getString("isDefault"));
//				}
				list.add(bean);
			}
			adapter.notifyDataSetChanged();
		} catch (Exception ex) {
			LogUtils.e(ex.getMessage());
			return;
		}
	}

	private void parseDeleteMemberAddress(String result) {
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
			LogUtils.d("lyp" + json);
			if (!success) {
				String message = json.getString("message");
				ToastUtil.showShort(this, message);
				if (json.getString("resCode").equals(Constant.RELOGIN)) {
					backLogin();
				}
				return;
			}
//			ExpressAddressBean deleteBean = adapter.getDeleteBean();
//			if (deleteBean != null) {
//				list.remove(deleteBean);
//				adapter.setDeleteBean(null);
//				adapter.notifyDataSetChanged();
//				ToastUtil.showLong(this, "刪除收货成功！");
//			}
			ToastUtil.showLong(this, "刪除收货成功！");
			initData();
		} catch (Exception ex) {
			LogUtils.e(ex.getMessage());
			return;
		}
	}
	
	private void parseUpdateDefaultMemberAddress(String result) {
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
			LogUtils.d("lyp" + json);
			if (!success) {
				String message = json.getString("message");
				ToastUtil.showShort(this, message);
				if (json.getString("resCode").equals(Constant.RELOGIN)) {
					backLogin();
				}
				return;
			}
			ToastUtil.showLong(this, "设置默认收货成功！");
			initData();
		} catch (Exception ex) {
			LogUtils.e(ex.getMessage());
			return;
		}
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK && requestCode == ADD_EXPRESS_ADDRESS) {
			initData();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	};
}
