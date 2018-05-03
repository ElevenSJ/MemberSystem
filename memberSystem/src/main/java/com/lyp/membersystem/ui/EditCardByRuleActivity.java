package com.lyp.membersystem.ui;

import java.util.ArrayList;

import org.json.JSONObject;

import com.lyp.membersystem.R;
import com.lyp.membersystem.base.BaseActivity;
import com.lyp.membersystem.base.BaseApplication;
import com.lyp.membersystem.bean.PackageBean;
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
import com.lyp.membersystem.view.dialog.WaitDialog;
import com.yuntongxun.ecdemo.common.utils.LogUtil;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class EditCardByRuleActivity extends BaseActivity {

	private String card_icon;
	private ImageView card_img;
	private EditText card_content;
	private String good_info;
	private String card_info;
	private String customerIds;
	private TextView pay_money_tv;
	private double goodPrice;
	private double cardPrice;
	private int customerNumber = 1;
	private SharedPreferences mSharedPreferences;
	private WaitDialog mWaitDialog;
	private RadioGroup group_temo;
	private RadioButton checkRadioButton;
	private int checkId;
	private String productSkuIds;
	private String customTexts;
	private ArrayList<SelectGoodBean> mSelectGoodlist;
	private String customers_goods;
	private String cardText;
	private String id;
	
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
		BaseApplication.getInstance().addActivity(this);
		setContentView(R.layout.edit_card_by_rule_layout);
		mSelectGoodlist = getIntent().getParcelableArrayListExtra("select_good");
		cardText = getIntent().getStringExtra("cardText");
		id = getIntent().getStringExtra("id");
		initView();
//		initData();
	}

	private void initView() {
		card_img = (ImageView) findViewById(R.id.card_img);
		String card = mSelectGoodlist.get(0).getCard();
		card_icon = card.split(",")[2];
		LogUtil.d("lyp", card.split(",")[2]);
		ImageManager.loadImage(card_icon, card_img);
		card_info = card.split(",")[0];
		card_content = (EditText) findViewById(R.id.card_content);
		card_content.setEnabled(false);
		group_temo = (RadioGroup) findViewById(R.id.radioGroup1);
		// 改变默认选项
		checkId = R.id.radio0;
		group_temo.check(checkId);
		if (cardText != null) {
			if (cardText.contains("_")) {
				LogUtils.d("lyp", "cardText: " + cardText);
				String[] split = cardText.split("_");
				((RadioButton) group_temo.findViewById(R.id.radio0)).setText(split[0]);
				((RadioButton) group_temo.findViewById(R.id.radio1)).setText(split[1]);
				if (split.length >= 3) {
					((RadioButton) group_temo.findViewById(R.id.radio2)).setText(split[2]);
				} 
			} else {
				((RadioButton) group_temo.findViewById(R.id.radio0)).setText(cardText);
			}
			
		}
		checkRadioButton = (RadioButton) group_temo.findViewById(R.id.radio0);  
		group_temo.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// 点击事件获取的选择对象
				checkId = checkedId;
				checkRadioButton = (RadioButton) group_temo.findViewById(checkedId);
				if (checkedId == R.id.radio3) {
					card_content.setEnabled(true);
				} else {
					card_content.setEnabled(false);
				}
			}
		});
//		for (int i = 0; i < mSelectGoodlist.size(); i++) {
//			SelectGoodBean selectGoodBean = mSelectGoodlist.get(i);
//			if (i == 0) {
//				customers_goods = selectGoodBean.getCid() + "-" + selectGoodBean.getPid();
//			} else {
//				customers_goods = customers_goods + "," + selectGoodBean.getCid() + "-" + selectGoodBean.getPid();
//			}
//		}
	}

	private void initData() {
		ImageManager.loadImage(card_icon, card_img);
	}

	public void nextStep(View view) {
		String content = null;
		if (checkId == R.id.radio3) {
			CharSequence text = card_content.getText();
			if (text == null || text.toString().trim().length() <= 0) {
				ToastUtil.showLong(this, "请写下点内容");
				return;
			}
			content = text.toString();
		} else {
			content = checkRadioButton.getText().toString();
		}
		Intent i = new Intent();
		i.setClass(this, ChooseAddressFromRuleActivity.class);
//		i.putExtra("customers_goods", customers_goods);
		i.putParcelableArrayListExtra("select_good", mSelectGoodlist);
		i.putExtra("id", id);
		i.putExtra("card_info", card_info);
		i.putExtra("card_content", content);
		startActivity(i);
	}

	public void onBack(View view) {
		finish();
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
			if (json.has("object")) {
				Intent i = new Intent();
				i.setClass(this, PayActivity.class);
				i.putExtra("order_id", json.getString("object"));
				startActivity(i);
			}
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
