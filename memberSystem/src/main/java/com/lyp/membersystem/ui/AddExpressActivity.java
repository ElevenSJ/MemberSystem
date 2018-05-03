package com.lyp.membersystem.ui;

import org.json.JSONObject;

import com.lyp.membersystem.R;
import com.lyp.membersystem.base.BaseActivity;
import com.lyp.membersystem.base.BaseApplication;
import com.lyp.membersystem.log.LogUtils;
import com.lyp.membersystem.net.Errors;
import com.lyp.membersystem.net.MessageContants;
import com.lyp.membersystem.net.NetProxyManager;
import com.lyp.membersystem.utils.CityDialog;
import com.lyp.membersystem.utils.CityDialog.InputListener;
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
import android.text.Editable;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

public class AddExpressActivity extends BaseActivity {

	private CityDialog mCityDialog;
	private SharedPreferences mSharedPreferences;
	private WaitDialog mWaitDialog;
	private EditText tv_name;
	private EditText tv_tag_name;
	private TextView tv_area;
	private String areaStr = null;
	private EditText et_address;
	private EditText tv_phone;
	private boolean isUpdate = false;
	private String id;
	private String flag = null;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MessageContants.MSG_ADD_MEMBER_ADDRESS: {
				parseAddMemberAddress((String) msg.obj);
				break;
			}
			case MessageContants.MSG_UPDATE_MEMBER_ADDRESS: {
				parseUpdateMemberAddress((String) msg.obj);
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
		setContentView(R.layout.add_express_address_layout);
		initView();
	}

	private void initView() {
		tv_name = (EditText) findViewById(R.id.tv_name);
		tv_tag_name = (EditText) findViewById(R.id.tv_tag_name);
		et_address = (EditText) findViewById(R.id.et_address);
		tv_area = (TextView) findViewById(R.id.tv_area);
		tv_phone = (EditText) findViewById(R.id.tv_phone);
		Intent intent = getIntent();
		isUpdate = intent.getBooleanExtra("update", false);
		if (isUpdate) {
			id = intent.getStringExtra("id");
			tv_name.setText(intent.getStringExtra("name"));
			tv_tag_name.setText(intent.getStringExtra("tag"));
			tv_phone.setText(intent.getStringExtra("phone"));
			areaStr  = intent.getStringExtra("area");
			tv_area.setText(areaStr);
			et_address.setText(intent.getStringExtra("address"));
			flag = intent.getStringExtra("flag");
		}
	}

	public void save(View view) {
		CharSequence nameText = tv_name.getText();
		if (nameText == null || nameText.toString().trim().length() == 0) {
			ToastUtil.showLong(this, "名字不能为空");
			return;
		}
		CharSequence phoneText = tv_phone.getText();
		if (phoneText == null || phoneText.toString().trim().length() == 0) {
			ToastUtil.showLong(this, "号码不能为空");
			return;
		}
		if (areaStr == null) {
			ToastUtil.showLong(this, "地区不能为空");
			return;
		}
		Editable addressET = et_address.getText();
		if (addressET == null || addressET.toString().length() <= 0) {
			ToastUtil.showLong(this, "地址不能为空");
			return;
		}
		Editable tv_tag_nameET = tv_tag_name.getText();
		if (tv_tag_nameET == null || tv_tag_nameET.toString().length() <= 0) {
			ToastUtil.showLong(this, "别名不能为空");
			return;
		}
		mSharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCE, Activity.MODE_PRIVATE);
		String tokenid = mSharedPreferences.getString(Constant.TOKEN_ID, "");
		// String saleId = mSharedPreferences.getString(Constant.ID,
		// "");
		if (mWaitDialog == null) {
			mWaitDialog = new WaitDialog(this, R.string.loading_data);
		}
		mWaitDialog.show();
		if (isUpdate) {
			NetProxyManager.getInstance().toUpdateMemberAddress(mHandler, tokenid, id, tv_tag_nameET.toString(),
					nameText.toString(), phoneText.toString(), areaStr, addressET.toString(), flag);
		} else {
			NetProxyManager.getInstance().toAddMemberAddress(mHandler, tokenid, tv_tag_nameET.toString(),
					nameText.toString(), phoneText.toString(), areaStr, addressET.toString(), null);
		}
	}

	public void setAddress(View view) {
		InputListener listener = new InputListener() {

			@Override
			public void getText(String str) {
				tv_area.setText(str);
				areaStr = str;
			}
		};
		mCityDialog = new CityDialog(AddExpressActivity.this, listener);
		mCityDialog.setTitle(R.string.district);
		mCityDialog.show();
	}

	public void onBack(View view) {
		finish();
	}

	private void parseAddMemberAddress(String result) {
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
			ToastUtil.showLong(this, "新增收货地址成功！");
			setResult(RESULT_OK);
			finish();
		} catch (Exception ex) {
			LogUtils.e(ex.getMessage());
			return;
		}
	}
	
	private void parseUpdateMemberAddress(String result) {
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
			ToastUtil.showLong(this, "更新收货地址成功！");
			setResult(RESULT_OK);
			finish();
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
	protected void onDestroy() {
		BaseApplication.getInstance().removeActivity(this);
		super.onDestroy();
	};
}
