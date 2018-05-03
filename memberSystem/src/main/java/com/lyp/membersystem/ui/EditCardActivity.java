package com.lyp.membersystem.ui;

import com.lyp.membersystem.R;
import com.lyp.membersystem.base.BaseActivity;
import com.lyp.membersystem.base.BaseApplication;
import com.lyp.membersystem.log.LogUtils;
import com.lyp.membersystem.manager.ImageManager;
import com.lyp.membersystem.utils.SystemStatusManager;
import com.lyp.membersystem.utils.ToastUtil;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class EditCardActivity extends BaseActivity {

	private String card_icon;
	private ImageView card_img;
	private EditText card_content;
	private String good_info;
	private String card_info;
	private LinearLayout lLayout_content;
	private String productSkuIds;
	private String customTexts;
	private RadioGroup group_temo;
	private RadioButton checkRadioButton;
	private RadioButton cusButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		BaseApplication.getInstance().addActivity(this);
		setTranslucentStatus();
		setContentView(R.layout.edit_card_layout);
		good_info = getIntent().getStringExtra("good_info");
		productSkuIds = getIntent().getStringExtra("productSkuIds");
		card_info = getIntent().getStringExtra("card_info");
		card_icon = getIntent().getStringExtra("card_icon");
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
	}

	private void initData() {
		ImageManager.loadImage(card_icon, card_img);
	}

	public void nextStep(View view) {
		String content = null;
		LogUtils.d("lyp", checkRadioButton  + "   " + cusButton);
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
		LogUtils.d("lyp", content);
		Intent i = new Intent();
		// i.setClass(this, ChoosePackageActivity.class);
		i.setClass(this, ChooseCustomerFromMallActivity.class);
		i.putExtra("good_info", good_info);
		i.putExtra("productSkuIds", productSkuIds);
		i.putExtra("card_info", card_info);
		i.putExtra("card_content", content);
		startActivity(i);
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
