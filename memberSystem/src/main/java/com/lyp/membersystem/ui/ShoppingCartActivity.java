package com.lyp.membersystem.ui;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import com.lyp.membersystem.R;
import com.lyp.membersystem.adapter.ShopCartAdapter;
import com.lyp.membersystem.base.BaseActivity;
import com.lyp.membersystem.bean.ShopCartBean;
import com.lyp.membersystem.log.LogUtils;
import com.lyp.membersystem.net.Errors;
import com.lyp.membersystem.net.MessageContants;
import com.lyp.membersystem.utils.Constant;
import com.lyp.membersystem.utils.SystemStatusManager;
import com.lyp.membersystem.utils.ToastUtil;
import com.lyp.membersystem.view.dialog.WaitDialog;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

public class ShoppingCartActivity extends BaseActivity {

	private ListView lv_shop_cart;
	private ShopCartAdapter adapter;
	// private EditTextWithDel mEtSearchName;
	private List<ShopCartBean> mShopCartList;
	private CheckBox customer_select_all;
	private WaitDialog mWaitDialog;
	private SharedPreferences mSharedPreferences;
	private TextView pay_money_tv;
	

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
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
		setContentView(R.layout.shopping_cart_layout);
		initViews();
	}

	private void initViews() {
		lv_shop_cart = (ListView) findViewById(R.id.lv_shop_cart);
		customer_select_all = (CheckBox) findViewById(R.id.customer_select_all);
		initDatas();
		initEvents();
		adapter = new ShopCartAdapter(this, mShopCartList);
		lv_shop_cart.setAdapter(adapter);
		pay_money_tv = (TextView) findViewById(R.id.pay_money_tv);
		pay_money_tv.setText("共计：￥");
	}

	public void backAdvanceStep(View view) {
		back();
	}

	public void submitOrderBtn(View view) {
		mSharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCE, MODE_PRIVATE);
		String tokenid = mSharedPreferences.getString(Constant.TOKEN_ID, "");
		String saleId = mSharedPreferences.getString(Constant.ID, "");
		List<ShopCartBean> selectContact = adapter.getSelectContact();
		String customerIds = "";
		for (int i = 0; i < selectContact.size(); i++) {
			if (i == 0) {
				customerIds = selectContact.get(i).getId();
			} else {
				customerIds = customerIds + "," + selectContact.get(i).getId();
			}
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
			LogUtils.d("" + json);
			if (!success) {
				String message = json.getString("message");
				ToastUtil.showShort(this, message);
				return;
			}
			ToastUtil.showLong(this, "下单成功！");
		} catch (Exception ex) {
			LogUtils.e(ex.getMessage());
			return;
		}
	}
	
	private void back() {
		finish();
	}

	private void initEvents() {

		// ListView的点击事件
		lv_shop_cart.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				CheckBox checkBox = (CheckBox) view.findViewById(R.id.customer_select);
				if (checkBox.isChecked()) {
					customer_select_all.setChecked(false);
					checkBox.setChecked(false);
					adapter.ischeck.put(position, false);
					adapter.isCheckNum--;
					adapter.getSelectContact().remove(mShopCartList.get(position));
				} else {
					checkBox.setChecked(true);
					adapter.ischeck.put(position, true);
					adapter.isCheckNum++;
					if (adapter.isCheckNum == adapter.getCount()) {
						customer_select_all.setChecked(true);
					}
					adapter.getSelectContact().add(mShopCartList.get(position));
				}
				pay_money_tv.setText("共计：￥");
				adapter.notifyDataSetChanged();
			}
		});

	}

	private void initDatas() {
		mShopCartList = new ArrayList<ShopCartBean>();
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

	public void selectAll(View view) {
		CheckBox checkbox = (CheckBox) view;
		if (checkbox.isChecked()) {
			for (int i = 0; i < mShopCartList.size(); i++) {
				adapter.ischeck.put(i, true);
			}
			adapter.isCheckNum = adapter.getCount();
		} else {
			for (int i = 0; i < mShopCartList.size(); i++) {
				adapter.ischeck.put(i, false);
			}
			adapter.isCheckNum = 0;
		}
		pay_money_tv.setText("共计：￥");
		adapter.notifyDataSetChanged();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			back();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void backLogin() {
		super.backLogin();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}
}
