package com.lyp.membersystem.ui;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.lyp.membersystem.R;
import com.lyp.membersystem.adapter.ConfirmChooseOrderAdapter;
import com.lyp.membersystem.base.BaseActivity;
import com.lyp.membersystem.base.BaseApplication;
import com.lyp.membersystem.bean.GoodBean;
import com.lyp.membersystem.bean.OrderDetailBean;
import com.lyp.membersystem.bean.SelectGoodBean;
import com.lyp.membersystem.log.LogUtils;
import com.lyp.membersystem.manager.ImageManager;
import com.lyp.membersystem.net.Errors;
import com.lyp.membersystem.net.MessageContants;
import com.lyp.membersystem.net.NetProxyManager;
import com.lyp.membersystem.pay.PayActivity;
import com.lyp.membersystem.utils.Constant;
import com.lyp.membersystem.utils.SystemStatusManager;
import com.lyp.membersystem.utils.ToastUtil;
import com.lyp.membersystem.view.contactsort.ContactSortModel;
import com.lyp.membersystem.view.dialog.WaitDialog;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ConfirmChooseOrderActivity extends BaseActivity {

	private WaitDialog mWaitDialog;
	private ListView lv_select_choose_order;
	private TextView pay_money_tv;
	private TextView num;
	private ImageView card_iv;
	private ImageView package_iv;
	private SharedPreferences mSharedPreferences;
	private ConfirmChooseOrderAdapter mconfrimOrderAdapter;
	private ArrayList<SelectGoodBean> mSelectGoodlist;
	private String customers_goods;
	private String card_info;
	private String pack_info;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MessageContants.MSG_ADD_RULE_ORDER: {
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
		mSelectGoodlist = getIntent().getParcelableArrayListExtra("select_good");
		setContentView(R.layout.confirm_order_layout);
		initView();
		initData();
	}

	private void initView() {
		lv_select_choose_order = (ListView) findViewById(R.id.lv_select_choose_order);
		pay_money_tv = (TextView) findViewById(R.id.pay_money_tv);
		card_iv = (ImageView) findViewById(R.id.card_iv);
		package_iv = (ImageView) findViewById(R.id.package_iv);
		num = (TextView) findViewById(R.id.num);
		mconfrimOrderAdapter = new ConfirmChooseOrderAdapter(mSelectGoodlist, this, null);
		lv_select_choose_order.setAdapter(mconfrimOrderAdapter);
		lv_select_choose_order.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long arg3) {
			}
		});
	}

	private void initData() {
		if (mWaitDialog == null) {
			mWaitDialog = new WaitDialog(this, R.string.loading_data);
		}
		mWaitDialog.show();
		String card = mSelectGoodlist.get(0).getCard();
		ImageManager.loadImage(card.split(",")[2], card_iv);
		card_info = card.split(",")[0] + "_" + card.split(",")[1];
//		String pack = mSelectGoodlist.get(0).getPack();
//		pack_info = pack.split(",")[0] + "_" + pack.split(",")[1];
//		ImageManager.loadImage(pack.split(",")[2], package_iv);
//		double orderPrice = (Double.parseDouble(card.split(",")[1]) + Double.parseDouble(pack.split(",")[1]))
//				* mSelectGoodlist.size();
		double orderPrice = (Double.parseDouble(card.split(",")[1]))
				* mSelectGoodlist.size();
		for (int i = 0; i < mSelectGoodlist.size(); i++) {
			SelectGoodBean selectGoodBean = mSelectGoodlist.get(i);
			if (i == 0) {
				customers_goods = selectGoodBean.getCid() + "-" + selectGoodBean.getPid();
			} else {
				customers_goods = customers_goods + "," + selectGoodBean.getCid() + "-" + selectGoodBean.getPid();
			}
			orderPrice = orderPrice + Double.parseDouble(selectGoodBean.getPprice());
		}
		num.setText("" + mSelectGoodlist.size());
		pay_money_tv.setText("实付款：￥" + orderPrice);

		if (mWaitDialog != null && mWaitDialog.isShowing()) {
			mWaitDialog.dismiss();
		}
	}

	public void onBack(View view) {
		finish();
	}

	public void payBtn(View view) {
		mSharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCE, MODE_PRIVATE);
		String tokenid = mSharedPreferences.getString(Constant.TOKEN_ID, "");
		String saleId = mSharedPreferences.getString(Constant.ID, "");
		LogUtils.d("customers_goods: " + customers_goods);
		LogUtils.d("card_info: " + card_info);
		LogUtils.d("pack_info: " + pack_info);
//		NetProxyManager.getInstance().toAddOrderByRule(mHandler, tokenid, saleId, customers_goods, card_info, pack_info,
//				"神助手祝你幸福，快乐！", "1", null);
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
			Intent i = new Intent();
			i.setClass(this, PayActivity.class);
			i.putExtra("order_id", json.getString("object"));
			startActivity(i);
			BaseApplication.getInstance().finishSpecialPathActivity();
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
