/*
 *  Copyright (c) 2013 The CCP project authors. All Rights Reserved.
 *
 *  Use of this source code is governed by a Beijing Speedtong Information Technology Co.,Ltd license
 *  that can be found in the LICENSE file in the root of the web site.
 *
 *   http://www.yuntongxun.com
 *
 *  An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */
package com.yuntongxun.ecdemo.ui.chatting;

import com.lyp.membersystem.R;
import com.lyp.membersystem.utils.SystemStatusManager;
import com.yuntongxun.ecdemo.common.CCPAppManager;
import com.yuntongxun.ecdemo.common.utils.CrashHandler;
import com.yuntongxun.ecdemo.common.utils.LogUtil;
import com.yuntongxun.ecdemo.ui.ECFragmentActivity;
import com.yuntongxun.ecdemo.ui.SDKCoreHelper;
import com.yuntongxun.ecdemo.ui.chatting.view.CCPChattingFooter2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.WindowManager;

/**
 * @author 容联•云通讯
 * @date 2014-12-9
 * @version 4.0
 */
public class ChattingActivity extends ECFragmentActivity implements
		ChattingFragment.OnChattingAttachListener {

	private static final String TAG = "ECSDK_Demo.ChattingActivity";
	public ChattingFragment mChattingFragment;
	private MyReceiver myReceiver;

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		LogUtil.d(TAG, "chatting ui dispatch key event :" + event);
		if (mChattingFragment != null
				&& mChattingFragment.onKeyDown(event.getKeyCode(), event)) {
			return true;
		}

		return super.dispatchKeyEvent(event);
	}

	@Override
	protected void onResume() {
		super.onResume();
		CrashHandler.getInstance().setContext(this);

		IntentFilter filter1 = new IntentFilter();

		filter1.addAction("com.lyp.membersystem.Removemember");
		filter1.addAction(SDKCoreHelper.ACTION_KICK_OFF);

		registerReceiver(myReceiver, filter1);
		
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		unregisterReceiver(myReceiver);
	}

	public class MyReceiver extends BroadcastReceiver
	{
		// 可用Intent的getAction()区分接收到的不同广播
		@Override
		public void onReceive(Context arg0, Intent intent)
		{
			if(intent==null){
				return;
			}
			if (intent.getAction().equals(SDKCoreHelper.ACTION_KICK_OFF)
					|| intent.getAction().equals(
							"com.lyp.membersystem.Removemember"))
				finish();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		LogUtil.d(TAG, "onCreate");
		super.onCreate(null);
		setTranslucentStatus();
		getWindow().setFormat(PixelFormat.TRANSPARENT);

		myReceiver = new MyReceiver();
		String recipients = getIntent().getStringExtra(
				ChattingFragment.RECIPIENTS);
		if (recipients == null) {
			finish();
			LogUtil.e(TAG, "recipients is null !!");
			return;
		}
		setContentView(R.layout.chattingui_activity_container);
		mChattingFragment = new ChattingFragment();
		Bundle bundle = getIntent().getExtras();
		bundle.putBoolean(ChattingFragment.FROM_CHATTING_ACTIVITY, true);
		mChattingFragment.setArguments(bundle);
		getSupportFragmentManager().beginTransaction()
				.add(R.id.ccp_root_view, mChattingFragment).commit();
		onActivityCreate();

		if (isChatToSelf(recipients) || isPeerChat(recipients)) {
			AppPanelControl.setShowVoipCall(false);
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		LogUtil.d(TAG, "chatting ui on key down, " + keyCode + ", " + event);
		
//		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0&&CCPChattingFooter2.isRecodering) {
//			// do nothing.
//			return true;
//		}
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			// do nothing.
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	
	
	
	

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		LogUtil.d(TAG, "chatting ui on key up");
		return super.onKeyUp(keyCode, event);
	}

	public boolean isPeerChat() {
		if (mChattingFragment != null) {
			return mChattingFragment.isPeerChat();
		}
		return false;
	}

	public boolean isChatToSelf(String sessionId) {

		String userId = CCPAppManager.getUserId();
		return sessionId != null
				&& (sessionId.equalsIgnoreCase(userId) ? true : false);
	}

	public boolean isPeerChat(String sessionId) {

		return sessionId.toLowerCase().startsWith("g");
	}

	@Override
	public void onChattingAttach() {
		LogUtil.d(TAG, "onChattingAttach");
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
		tintManager.setStatusBarTintResource(R.color.main_bg_color);// 状态栏无背景
		getWindow().getDecorView().setFitsSystemWindows(true);
	}

}