package com.sj.activity;

import org.json.JSONObject;

import com.lyp.membersystem.LoginActivity;
import com.lyp.membersystem.R;
import com.lyp.membersystem.log.LogUtils;
import com.lyp.membersystem.net.Errors;
import com.lyp.membersystem.net.MessageContants;
import com.lyp.membersystem.net.NetProxyManager;
import com.lyp.membersystem.utils.Constant;
import com.lyp.membersystem.utils.SystemStatusManager;
import com.lyp.membersystem.utils.ToastUtil;
import com.sj.activity.base.ActivityBase;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

public class ActivityRegister extends ActivityBase {

	
	private EditText phone_number_et;
	private EditText verify_code_et;
	private String phone;
	private SharedPreferences mSharedPreferences;
	

	private Handler mainHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MessageContants.MSG_LOGIN: {
				
				if (!isPause) {
				    showProgress();
				    parseLoginInfo((String) msg.obj);
				}
				break;
			}
			case MessageContants.MSG_GET_SMS_AUTH_CODE: {
				parseSMSAuthCodeInfo((String) msg.obj);
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
	}
	@Override
	public int getContentLayout() {
		// TODO Auto-generated method stub
		return R.layout.login_layout;
	}
	
	@Override
	public void initView() {
		mSharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCE, MODE_PRIVATE);
		phone_number_et = (EditText) findViewById(R.id.phone_number_et);
		verify_code_et = (EditText) findViewById(R.id.verify_code_et);
	}

	public void getVerifyCode(View view) {
		Editable phoneET = phone_number_et.getText();
		if (phoneET == null) {
			ToastUtil.showLong(ActivityRegister.this, "号码不能为空");
			return;
		}
		phone = phoneET.toString();
		if (phone.length() <= 0) {
			ToastUtil.showLong(ActivityRegister.this, "号码不能为空");
			return;
		}
		NetProxyManager.getInstance().toGetSMSAuthCode(mainHandler, phone);
	}

	public void login(View view) {
//		loginSuccess();
		
		Editable verifyCodeET = verify_code_et.getText();
		if (verifyCodeET == null) {
			ToastUtil.showLong(ActivityRegister.this, "验证码不能为空");
			return;
		}
		Editable phoneET = phone_number_et.getText();
		if (phoneET == null) {
			ToastUtil.showLong(ActivityRegister.this, "号码不能为空");
			return;
		}
		phone = phoneET.toString();
		if (phone.length() <= 0) {
			ToastUtil.showLong(ActivityRegister.this, "号码不能为空");
			return;
		}
		NetProxyManager.getInstance().toLogin(mainHandler, phone, verifyCodeET.toString());
	}

	private void loginSuccess() {
		Intent intent = new Intent();
		intent.setClass(ActivityRegister.this, LoginActivity.class);
		startActivity(intent);
		finish();
	}

	/**
	 * 设置状态栏背景状态
	 */
	private void setTranslucentStatus() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			// Window win = getWindow();
			// WindowManager.LayoutParams winParams = win.getAttributes();
			// final int bits =
			// WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
			// winParams.flags |= bits;
			// win.setAttributes(winParams);

			// 透明状态栏
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			// 透明导航栏
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		}
		SystemStatusManager tintManager = new SystemStatusManager(this);
		tintManager.setStatusBarTintEnabled(true);
		tintManager.setStatusBarTintResource(android.R.color.white);// 状态栏无背景
		getWindow().getDecorView().setFitsSystemWindows(true);
	}

	/**
	 * to parse get sms auth code
	 */
	private void parseSMSAuthCodeInfo(String result) {
		if (Errors.ERROR_NET.equals(result) || Errors.ERROR_SERVER.equals(result)) {
			ToastUtil.showLong(this, R.string.network_error);
			hideProgress();
			return;
		}
		// to parser json data
		try {
			JSONObject json = new JSONObject(result);
			boolean success = json.getBoolean("success");
			hideProgress();
			LogUtils.d("" + json);
			if (!success) {
				String message = json.getString("message");
				ToastUtil.showShort(this, message);
				return;
			}
			ToastUtil.showLong(ActivityRegister.this, "发送成功，请等待验证码。");
		} catch (Exception ex) {
			LogUtils.e(ex.getMessage());
			return;
		}
	}

	/**
	 * to parse login infomations from network
	 */
	private void parseLoginInfo(String result) {
		if (Errors.ERROR_NET.equals(result) || Errors.ERROR_SERVER.equals(result)) {
			ToastUtil.showLong(this, R.string.network_error);
			hideProgress();
			return;
		}
		// to parser json data
		try {
			JSONObject json = new JSONObject(result);
			boolean success = json.getBoolean("success");
			hideProgress();
			if (!success) {
				String message = json.getString("message");
				ToastUtil.showShort(this, message);
				return;
			}
			JSONObject jot = json.getJSONObject("object");
			String id = jot.getString("id");
			String name = jot.getString("name");
			String token = jot.getString("token_id");
			Editor editor = mSharedPreferences.edit();
			editor.putBoolean(Constant.IS_FIRST, false);
			editor.putString(Constant.TOKEN_ID, token);
			editor.putString(Constant.ID, id);
			editor.putString(Constant.USER_NAME, name);
			editor.commit();
			loginSuccess();
		} catch (Exception ex) {
			LogUtils.e(ex.getMessage());
			return;
		}
	}
}
