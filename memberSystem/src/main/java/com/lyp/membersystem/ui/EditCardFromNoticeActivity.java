package com.lyp.membersystem.ui;

import org.json.JSONObject;

import com.lyp.membersystem.R;
import com.lyp.membersystem.base.BaseActivity;
import com.lyp.membersystem.base.BaseApplication;
import com.lyp.membersystem.bean.PackageBean;
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

public class EditCardFromNoticeActivity extends BaseActivity {

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
	private LinearLayout lLayout_content;
	private RadioButton cusButton;
	private String productSkuIds;
	private String customTexts;
	
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
		setContentView(R.layout.edit_card_layout_from_notice);
		good_info = getIntent().getStringExtra("good_info");
		goodPrice = Double.valueOf(good_info.split("_")[1]);
		card_info = getIntent().getStringExtra("card_info");
		cardPrice = Double.valueOf(card_info.split("_")[1]);
		card_icon = getIntent().getStringExtra("card_icon");
		customerIds = getIntent().getStringExtra("customerIds");
		customerNumber = customerIds.split(",").length;
		productSkuIds = getIntent().getStringExtra("productSkuIds");
		customTexts = getIntent().getStringExtra("customTexts");
		initView();
		initData();
	}

	private void initView() {
		card_img = (ImageView) findViewById(R.id.card_img);
		card_content = (EditText) findViewById(R.id.card_content);
		card_content.setEnabled(false);
		lLayout_content = (LinearLayout) findViewById(R.id.lLayout_content);
		group_temo = (RadioGroup) LayoutInflater.from(this)
				.inflate(R.layout.customer_text_radio_group_layout, null);
		float scale = getResources().getDisplayMetrics().density;
		int buttonHeight = (int) (50 * scale + 0.5f);
//		LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, buttonHeight);
		LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		int lineHeight = (int) (1 * scale + 0.5f);
		LayoutParams lineParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, lineHeight);
		if (customTexts != null && customTexts.length() > 0) {
			if (!customTexts.contains("_")) {
				RadioButton button = (RadioButton) LayoutInflater.from(this)
					    .inflate(R.layout.customer_text_radio_button_layout, null);
				button.setText(customTexts);
				button.setLayoutParams(layoutParams);
				checkRadioButton = button;
//				checkId = button.getId();
				group_temo.addView(button);
				View line = LayoutInflater.from(this)
					    .inflate(R.layout.customer_text_radio_button_line, null);
				line.setLayoutParams(lineParams);
				group_temo.addView(line);
			} else {
				String[] customTextsList = customTexts.split("_");
				for (int i = 0; i < customTextsList.length; i++) {
					RadioButton button = (RadioButton) LayoutInflater.from(this)
						    .inflate(R.layout.customer_text_radio_button_layout, null);
					if (i == 0) {
//						checkId = button.getId();
						checkRadioButton = button;
					}
					button.setText(customTextsList[i]);
					button.setLayoutParams(layoutParams);
					group_temo.addView(button);
					View line = LayoutInflater.from(this)
						    .inflate(R.layout.customer_text_radio_button_line, null);
					line.setLayoutParams(lineParams);
					group_temo.addView(line);
				}
			}
		}
		cusButton = (RadioButton) LayoutInflater.from(this)
			    .inflate(R.layout.customer_text_radio_button_layout, null);
		cusButton.setLayoutParams(layoutParams);
		if (group_temo.getChildCount() == 0) {
//		    checkId = cusButton.getId();
		    card_content.setEnabled(true);
		    checkRadioButton = cusButton;
		}
		group_temo.addView(cusButton);
//		group_temo.check(checkId);
		checkRadioButton.setChecked(true);
		group_temo.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// 点击事件获取的选择对象
//				checkId = checkedId;
				checkRadioButton = (RadioButton) group_temo.findViewById(checkedId);
				if (checkedId == cusButton.getId()) {
					card_content.setEnabled(true);
				} else {
					card_content.setEnabled(false);
				}
			}
		});
		lLayout_content.addView(group_temo);
		pay_money_tv = (TextView) findViewById(R.id.pay_money_tv);
		pay_money_tv.setText("实付款：￥" + 
				(goodPrice + cardPrice) * customerNumber);
	}

	private void initData() {
		ImageManager.loadImage(card_icon, card_img);
	}

	public void nextStep(View view) {
		String content = null;
		if (checkRadioButton == cusButton) {
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
		i.setClass(this, ChooseAddressFromMallActivity.class);
		i.putExtra("good_info", good_info);
		i.putExtra("productSkuIds", productSkuIds);
		i.putExtra("card_info", card_info);
		i.putExtra("customerIds", customerIds);
		i.putExtra("card_content", content);
		startActivity(i);
	}

	public void payBtn(View view) {
		String content = null;
		if (checkRadioButton == cusButton) {
			CharSequence text = card_content.getText();
			if (text == null || text.toString().trim().length() <= 0) {
				ToastUtil.showLong(this, "请写下点内容");
				return;
			}
			content = text.toString();
		} else {
			content = checkRadioButton.getText().toString();
		}
//		mSharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCE, MODE_PRIVATE);
//		String tokenid = mSharedPreferences.getString(Constant.TOKEN_ID, "");
//		String saleId = mSharedPreferences.getString(Constant.ID, "");
//		NetProxyManager.getInstance().toAddOrder(mHandler, tokenid, saleId, customerIds, good_info.split("_")[0], card_info.split("_")[0], null,
//				content, "1", null);
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
