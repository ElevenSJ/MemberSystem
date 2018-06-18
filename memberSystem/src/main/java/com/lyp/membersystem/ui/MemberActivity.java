package com.lyp.membersystem.ui;

import org.json.JSONArray;
import org.json.JSONObject;

import com.lyp.membersystem.R;
import com.lyp.membersystem.base.BaseActivity;
import com.lyp.membersystem.base.BaseApplication;
import com.lyp.membersystem.log.LogUtils;
import com.lyp.membersystem.net.Errors;
import com.lyp.membersystem.net.MessageContants;
import com.lyp.membersystem.net.NetProxyManager;
import com.lyp.membersystem.pay.PayActivity;
import com.lyp.membersystem.utils.Constant;
import com.lyp.membersystem.utils.SystemStatusManager;
import com.lyp.membersystem.utils.ToastUtil;
import com.lyp.membersystem.view.dialog.WaitDialog;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class MemberActivity extends BaseActivity {

	private WaitDialog mWaitDialog;
	private SharedPreferences mSharedPreferences;
	private CheckBox user_agreement_check;
	private TextView user_agreement;
	private TextView member_type;
	private TextView expiry_date;
	private RadioGroup group_temo;
	private RadioButton radio0;
	private RadioButton radio1;
	private RadioButton timeRadio1;
	private int checkId;
	private TextView price1;
	private TextView valid_endtime;
	private String typeId;
	private String money1;
	private String money2;
	private String id1;
	private String id2;

	private Handler mainHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MessageContants.MSG_GET_RENEWAL_FEE_INFO: {
				parseGetRenewalInfo((String) msg.obj);
				break;
			}
			case MessageContants.MSG_RENEWAL_FEE: {
				parseAddOrder((String) msg.obj);
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
		setContentView(R.layout.member_layout);
		initView();
		initData();
	}

	private void initView() {

		user_agreement_check = (CheckBox) findViewById(R.id.user_agreement_check);
		user_agreement = (TextView) findViewById(R.id.user_agreement);
		user_agreement.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);// 下划线
		member_type = (TextView) findViewById(R.id.member_type);
		expiry_date = (TextView) findViewById(R.id.expiry_date);
		group_temo = (RadioGroup) findViewById(R.id.radioGroup1);
		// 改变默认选项
		checkId = R.id.radio1;
		group_temo.check(checkId);
		radio0 = (RadioButton) group_temo.findViewById(R.id.radio0);
		radio0.setVisibility(View.GONE);
		radio1 = (RadioButton) group_temo.findViewById(R.id.radio1);
		group_temo.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// 点击事件获取的选择对象
				checkId = checkedId;
				if (checkedId == R.id.radio0) {
					price1.setText(money1);
				} else {
					price1.setText(money2);
				}
			}
		});
		timeRadio1 = (RadioButton) findViewById(R.id.timeRadio1);
		price1 = (TextView) findViewById(R.id.price1);
		valid_endtime = (TextView) findViewById(R.id.valid_endtime);
	}

	private void initData() {
		mSharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCE, MODE_PRIVATE);
		String tokenid = mSharedPreferences.getString(Constant.TOKEN_ID, "");
		String saleId = mSharedPreferences.getString(Constant.ID, "");
		if (mWaitDialog == null) {
			mWaitDialog = new WaitDialog(this, R.string.loading_data);
		}
		mWaitDialog.show();
		NetProxyManager.getInstance().toGetRenewalInfo(mainHandler, tokenid);
	}

	public void userAgreement(View view) {
		Intent intent = new Intent();
		intent.setClass(this, UserAgreementActivity.class);
		startActivity(intent);
	}

	public void buyMember(View view) {
		if (!user_agreement_check.isChecked()) {
			ToastUtil.showMessage("请先阅读并同意用户使用协议！");
			return;
		}
		mSharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCE, MODE_PRIVATE);
		String tokenid = mSharedPreferences.getString(Constant.TOKEN_ID, "");
		String saleId = mSharedPreferences.getString(Constant.ID, "");
		if (mWaitDialog == null) {
			mWaitDialog = new WaitDialog(this, R.string.loading_data);
		}
		mWaitDialog.show();
		String productid = "1";//1咨询顾问 2团队领袖
		if (checkId == R.id.radio0) {
			productid = "1";
		} else {
			productid = "2";
		}
		NetProxyManager.getInstance().toBuyMember(mainHandler, tokenid, productid);
	}
	
	public void buyList(View view) {
		Intent intent = new Intent();
		intent.setClass(this, MemberBuyListActivity.class);
		startActivity(intent);
	}

	/**
	 * to parse login infomations from network
	 */
	private void parseGetRenewalInfo(String result) {
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
			member_type.setText(obj.getString("name"));
//			member_type.setText(obj.getString("typeName"));
//			expiry_date.setText(obj.getString("validEndTime"));
//			typeId = obj.getString("typeId");
//			JSONArray array = obj.getJSONArray("memeberType");
//			for (int i = 0; i < array.length(); i++) {
//				JSONObject job = array.getJSONObject(i);
				String id = obj.getString("id");
				if (id.equals("1")) {
					id1 = obj.getString("id");
					money1 = obj.getString("price");
					radio0.setText(obj.getString("name"));
					radio1.setText(obj.getString("name"));
					timeRadio1.setText(obj.getString("continueTime"));
					valid_endtime.setText(obj.getString("preValidEndTime"));
					checkId = R.id.radio0;
					price1.setText(money1);
					group_temo.check(checkId);
					radio1.setEnabled(false);
					radio1.setVisibility(View.GONE);
				} else if (id.equals("2")) {
					id2 = obj.getString("id");
					money2 = obj.getString("price");
					radio0.setText(obj.getString("name"));
					radio1.setText(obj.getString("name"));
					timeRadio1.setText(obj.getString("continueTime"));
					valid_endtime.setText(obj.getString("preValidEndTime"));
					checkId = R.id.radio1;
					price1.setText(money2);
					group_temo.check(checkId);
					radio0.setEnabled(false);
					radio0.setVisibility(View.GONE);
				}
//			}
		} catch (Exception ex) {
			ToastUtil.showLongMessage("服务器数据异常！");
			LogUtils.e(ex.getMessage());
			return;
		}
	}
	
	private void parseAddOrder(String result) {
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
			LogUtils.d("lyp", "" + json);
			if (!success) {
				String message = json.getString("message");
				ToastUtil.showShort(this, message);
				if (json.getString("resCode").equals(Constant.RELOGIN)) {
					backLogin();
				}
				return;
			}
			JSONObject job = json.getJSONObject("object");
			Intent i = new Intent();
			i.setClass(this, PayActivity.class);
			i.putExtra("order_id", job.getString("orderId"));
			i.putExtra("type", "member");
			i.putExtra("order_amount", job.getString("orderPrice"));
			startActivity(i);
			finish();
		} catch (Exception ex) {
			ToastUtil.showLongMessage("服务器数据异常！");
			if (mWaitDialog != null && mWaitDialog.isShowing()) {
				mWaitDialog.dismiss();
			}
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

}
