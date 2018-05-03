package com.nodeprogress.nodeprogress;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.lyp.membersystem.R;
import com.lyp.membersystem.base.BaseActivity;
import com.lyp.membersystem.log.LogUtils;
import com.lyp.membersystem.net.Errors;
import com.lyp.membersystem.net.MessageContants;
import com.lyp.membersystem.net.NetProxyManager;
import com.lyp.membersystem.utils.Constant;
import com.lyp.membersystem.utils.SystemStatusManager;
import com.lyp.membersystem.utils.ToastUtil;
import com.lyp.membersystem.view.dialog.WaitDialog;
import com.nodeprogress.nodeprogress.view.LogisticsData;
import com.nodeprogress.nodeprogress.view.NodeProgressAdapter;
import com.nodeprogress.nodeprogress.view.NodeProgressView;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;

public class ExpressActivity extends BaseActivity {
	List<LogisticsData> logisticsDatas;
	private WaitDialog mWaitDialog;
	private SharedPreferences mSharedPreferences;
	private String logisticsno;
	private NodeProgressView nodeProgressView;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MessageContants.MSG_GET_EXPRESS_INFO: {
				parseExpressInfo((String) msg.obj);
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
		logisticsno = getIntent().getStringExtra("logisticsNo");
//		logisticsno = "887687908103353479";
		setContentView(R.layout.express_layout);

		nodeProgressView = (NodeProgressView) findViewById(R.id.npv_NodeProgressView);

		if (mWaitDialog == null) {
			mWaitDialog = new WaitDialog(this, R.string.loading_data);
		}
		mWaitDialog.show();
		mSharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCE, MODE_PRIVATE);
		String tokenid = mSharedPreferences.getString(Constant.TOKEN_ID, "");
		String saleId = mSharedPreferences.getString(Constant.ID, "");
		NetProxyManager.getInstance().toGetExpressInfo(mHandler, tokenid, logisticsno);
		logisticsDatas = new ArrayList<>();
		// logisticsDatas.add(new LogisticsData().setTime("2016-6-28
		// 15:13:02").setContext("快件在【相城中转仓】装车,正发往【无锡分拨中心】已签收,签收人是【王漾】,签收网点是【忻州原平】"));
		// logisticsDatas.add(new LogisticsData().setTime("2016-6-28
		// 15:13:02").setContext("快件在【相城中转仓】装车,正发往【无锡分拨中心】"));
		// logisticsDatas.add(new LogisticsData().setTime("2016-6-28
		// 15:13:02").setContext("【北京鸿运良乡站】的【010058.269】正在派件"));
		// logisticsDatas.add(new LogisticsData().setTime("2016-6-28
		// 15:13:02").setContext("快件到达【潍坊市中转部】,上一站是【】"));
		// logisticsDatas.add(new LogisticsData().setTime("2016-6-28
		// 15:13:02").setContext("快件在【潍坊市中转部】装车,正发往【潍坊奎文代派】"));
		// logisticsDatas.add(new LogisticsData().setTime("2016-6-28
		// 15:13:02").setContext("快件到达【潍坊】,上一站是【潍坊市中转部】"));
		// logisticsDatas.add(new LogisticsData().setTime("2016-6-28
		// 15:13:02").setContext("快件在【武汉分拨中心】装车,正发往【晋江分拨中心】"));
		// logisticsDatas.add(new LogisticsData().setTime("2016-6-28
		// 15:13:02").setContext("【北京鸿运良乡站】的【010058.269】正在派件"));
		// logisticsDatas.add(new LogisticsData().setTime("2016-6-28
		// 15:13:02").setContext("【北京鸿运良乡站】的【010058.269】正在派件"));
		// logisticsDatas.add(new LogisticsData().setTime("2016-6-28
		// 15:13:02").setContext("【北京鸿运良乡站】的【010058.269】正在派件"));
		// logisticsDatas.add(new LogisticsData().setTime("2016-6-28
		// 15:13:02").setContext("【北京鸿运良乡站】的【010058.269】正在派件"));

		// nodeProgressView.setNodeProgressAdapter(new NodeProgressAdapter() {
		//
		// @Override
		// public int getCount() {
		// return logisticsDatas.size();
		// }
		//
		// @Override
		// public List<LogisticsData> getData() {
		// return logisticsDatas;
		// }
		// });
	}

	private void parseExpressInfo(String result) {
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
			// boolean success = json.getBoolean("success");
			// LogUtils.d("" + json);
			// if (!success) {
			// String message = json.getString("message");
			// ToastUtil.showShort(this, message);
			// if (json.getString("resCode").equals(Constant.RELOGIN)) {
			// backLogin();
			// }
			// return;
			// }

			/*
			 * {"status":"0","msg":"ok","result":{"number":"887687908103353479",
			 * "type":"yto","list":[{"time":"2017-12-23 20:26:09","status":
			 * "客户 签收人: 快递已转入圆通公司仓库周一正常派送 已签收  感谢使用圆通速递，期待再次为您服务"},{"time":
			 * "2017-12-23 15:11:52","status":
			 * "上海市浦东新区张江公司(点击查询电话)周** 派件中 派件员电话13732519228&nbsp;&nbsp;"
			 * },{"time":"2017-12-23 13:22:52","status":"上海市浦东新区张江公司 已收入"
			 * },{"time":"2017-12-23 09:44:00","status":
			 * "浦东转运中心 已发出,下一站 上海市浦东新区张江"},{"time":"2017-12-23 09:38:08"
			 * ,"status":"浦东转运中心 已收入"},{"time":"2017-12-23 07:02:41","status":
			 * "浦东转运中心 已收入"},{"time":"2017-12-23 05:39:12","status":
			 * "上海转运中心 已发出,下一站"},{"time":"2017-12-23 05:01:42","status":
			 * "上海转运中心 已发出,下一站 浦东转运中心"},{"time":"2017-12-23 01:45:44","status":
			 * "上海转运中心 已收入"},{"time":"2017-12-21 19:56:59","status":"上海转运中心 已收入"
			 * },{"time":"2017-12-21 19:56:28","status":"上海转运中心 已收入"},{"time":
			 * "2017-12-18 23:35:36","status":"新疆乌鲁木齐市公司 已发出,下一站"},{"time":
			 * "2017-12-18 01:02:28","status":"新疆乌鲁木齐市公司 已发出,下一站"},{"time":
			 * "2017-12-17 20:01:07","status":"新疆乌鲁木齐市公司 已发出,下一站 上海转运中心"
			 * },{"time":"2017-12-17 16:17:04","status":"新疆乌鲁木齐市公司 已打包"
			 * },{"time":"2017-12-17 15:17:11","status":"新疆乌鲁木齐市公司 已收入"
			 * },{"time":"2017-12-15 20:54:37","status":
			 * "新疆阿克苏市公司(点击查询电话) 已揽收&nbsp;&nbsp;"},{"time":"2017-12-15 17:01:25"
			 * ,"status":"新疆阿克苏市公司 已发出,下一站 新疆乌鲁木齐市"
			 * }],"deliverystatus":"3","issign":"1"}}
			 */
			if (!json.getString("status").equals("0")) {
				ToastUtil.showShort(this, json.getString("msg"));
				return;
			}
			JSONObject resultObj = json.getJSONObject("result");
			JSONArray list = resultObj.getJSONArray("list");
			for (int i = 0; i < list.length(); i++) {
				JSONObject job = list.getJSONObject(i);
				logisticsDatas.add(new LogisticsData().setTime(job.getString("time"))
						.setContext(job.getString("status")));
			}

			nodeProgressView.setNodeProgressAdapter(new NodeProgressAdapter() {

				@Override
				public int getCount() {
					return logisticsDatas.size();
				}

				@Override
				public List<LogisticsData> getData() {
					return logisticsDatas;
				}
			});
			nodeProgressView.requestLayout();
			nodeProgressView.invalidate();

		} catch (Exception ex) {
			LogUtils.e(ex.getMessage());
			return;
		} finally {
			if (mWaitDialog != null && mWaitDialog.isShowing()) {
				mWaitDialog.dismiss();
			}
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
