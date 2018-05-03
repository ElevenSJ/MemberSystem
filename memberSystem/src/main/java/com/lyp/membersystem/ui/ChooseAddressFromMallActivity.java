package com.lyp.membersystem.ui;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.lyp.membersystem.R;
import com.lyp.membersystem.adapter.ChooseExpressAddressAdapter;
import com.lyp.membersystem.base.BaseActivity;
import com.lyp.membersystem.base.BaseApplication;
import com.lyp.membersystem.bean.ExpressAddressBean;
import com.lyp.membersystem.log.LogUtils;
import com.lyp.membersystem.net.Errors;
import com.lyp.membersystem.net.MessageContants;
import com.lyp.membersystem.net.NetProxyManager;
import com.lyp.membersystem.pay.PayActivity;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class ChooseAddressFromMallActivity extends BaseActivity {

	private SharedPreferences mSharedPreferences;
	private WaitDialog mWaitDialog;
	private ListView address_list;
	private List<ExpressAddressBean> list;
	private ChooseExpressAddressAdapter adapter;
	private String good_info;
	private String card_content;
	private String card_info;
	private double goodPrice;
	private double cardPrice = 0d;
	private String customerIds;
	private RadioGroup group_temo;
	private RadioButton checkRadioButton;
	private int checkId;
	private LinearLayout select_member_address;
	private String productSkuIds;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MessageContants.MSG_GET_MEMBER_ADDRESS_LIST: {
				parseGetMemberAddressList((String) msg.obj);
				break;
			}
			case MessageContants.MSG_ADD_ORDER: {
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
		BaseApplication.getInstance().addActivity(this);
		good_info = getIntent().getStringExtra("good_info");
		goodPrice = Double.valueOf(good_info.split("_")[1]);
		card_info = getIntent().getStringExtra("card_info");
		cardPrice = Double.valueOf(card_info.split("_")[1]);
		card_content = getIntent().getStringExtra("card_content");
		customerIds = getIntent().getStringExtra("customerIds");
		productSkuIds = getIntent().getStringExtra("productSkuIds");
		setContentView(R.layout.choose_address_layout_from_mall);
		initView();
		initData();
	}

	private void initView() {
		group_temo = (RadioGroup) findViewById(R.id.radioGroup1);
		// 改变默认选项
		checkId = R.id.radio1;
		group_temo.check(checkId);
		checkRadioButton = (RadioButton) group_temo.findViewById(R.id.radio1);
		group_temo.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// 点击事件获取的选择对象
				checkId = checkedId;
				checkRadioButton = (RadioButton) group_temo.findViewById(checkedId);
				if (checkedId == R.id.radio1) {
					select_member_address.setVisibility(View.VISIBLE);
				} else {
					select_member_address.setVisibility(View.GONE);
				}
			}
		});
		select_member_address = (LinearLayout) findViewById(R.id.select_member_address);
		address_list = (ListView) findViewById(R.id.address_list);
		list = new ArrayList<ExpressAddressBean>();
		adapter = new ChooseExpressAddressAdapter(list, this, mHandler, true);
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

	public void payBtn(View view) {
		mSharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCE, MODE_PRIVATE);
		String tokenid = mSharedPreferences.getString(Constant.TOKEN_ID, "");
		String saleId = mSharedPreferences.getString(Constant.ID, "");
		if (checkId == R.id.radio0) {
			NetProxyManager.getInstance().toAddOrder(mHandler, tokenid, saleId, customerIds, good_info.split("_")[0], card_info.split("_")[0], productSkuIds,
					card_content, "1", null);
		} else {
			int selectIndex = adapter.getSelectIndex();
			ExpressAddressBean expressAddressBean = list.get(selectIndex);
			NetProxyManager.getInstance().toAddOrder(mHandler, tokenid, saleId, customerIds, good_info.split("_")[0], card_info.split("_")[0], productSkuIds,
					card_content, "2", expressAddressBean.getId());
		}
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
				if (job.has("isDefault")) {
					bean.setFlag(job.getString("isDefault"));
				}
				list.add(bean);
			}
			adapter.updateListView(list);
		} catch (Exception ex) {
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
			ToastUtil.showLong(this, "下单成功！");
			JSONObject job = json.getJSONObject("object");
			Intent i = new Intent();
			i.setClass(this, PayActivity.class);
			i.putExtra("order_id", job.getString("orderId"));
//			i.putExtra("type", job.getString("normal"));
			String amount = "";
			if (job.has("amount")) {
				amount = job.getString("amount");
			}
			
			if (job.has("unpaid")) {
				amount = job.getString("unpaid");
			}
			i.putExtra("order_amount", amount);
			startActivity(i);
//			BaseApplication.getInstance().finishSpecialPathActivity();
		} catch (Exception ex) {
			LogUtils.e(ex.getMessage());
			ToastUtil.showMessage("服务器返回有参数缺失");
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
