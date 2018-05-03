package com.lyp.membersystem.ui;

import com.lyp.membersystem.R;
import com.lyp.membersystem.base.BaseActivity;
import com.lyp.membersystem.utils.SystemStatusManager;
import com.lyp.membersystem.utils.ToastUtil;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

public class MyActivityActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTranslucentStatus();
		setContentView(R.layout.my_activity_layout);
	}

	public void onBack(View view) {
		finish();
	}

	public void orderService(View view) {
		Intent i = new Intent();
		i.setClass(this, ServiceActivity.class);
		startActivity(i);
	}

	public void managerMember(View view) {
		Intent i = new Intent();
		i.setClass(this, ServiceCustomerActivity.class);
		startActivity(i);
	}
	
	public void queryApplyService(View view) {
		Intent i = new Intent();
		i.setClass(this, ServiceApplyActivity.class);
		startActivity(i);
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
