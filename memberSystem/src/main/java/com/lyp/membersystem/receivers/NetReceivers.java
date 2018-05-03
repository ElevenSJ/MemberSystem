package com.lyp.membersystem.receivers;

import com.lyp.membersystem.log.LogUtils;
import com.lyp.membersystem.utils.NetUtils;
import com.lyp.membersystem.view.dialog.CustomerAlertDialog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.view.View;
import android.view.View.OnClickListener;

public class NetReceivers extends BroadcastReceiver {
	public void onReceive(final Context context, Intent intent) {
		String action = intent.getAction();
		if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
			boolean isConnected = NetUtils.isNetworkConnected(context);
			LogUtils.d("NetReceivers", "网络状态：" + isConnected);
			LogUtils.d("NetReceivers", "wifi状态：" + NetUtils.isWifiConnected(context));
			LogUtils.d("NetReceivers", "移动网络状态：" + NetUtils.isMobileConnected(context));
			LogUtils.d("NetReceivers", "网络连接类型：" + NetUtils.getConnectedType(context));
			if (isConnected) {
			} else {
				new CustomerAlertDialog(context).builder().setTitle("提示").setMsg("检测到无网络连接")
				.setPositiveButton("设置", new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = null;
						// 判断手机系统的版本 即API大于10 就是3.0或以上版本
						intent = new Intent(android.provider.Settings.ACTION_SETTINGS);
						context.startActivity(intent);
					}
				}).setNegativeButton("取消", new OnClickListener() {
					@Override
					public void onClick(View v) {

					}
				}).show();
			}
		}
	}

}
