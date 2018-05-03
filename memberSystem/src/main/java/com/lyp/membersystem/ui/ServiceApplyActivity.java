package com.lyp.membersystem.ui;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lyp.membersystem.R;
import com.lyp.membersystem.adapter.ServicesApplayAdapter;
import com.lyp.membersystem.base.BaseActivity;
import com.lyp.membersystem.bean.ServiceBean;
import com.lyp.membersystem.log.LogUtils;
import com.lyp.membersystem.net.Errors;
import com.lyp.membersystem.net.MessageContants;
import com.lyp.membersystem.net.NetProxyManager;
import com.lyp.membersystem.utils.Constant;
import com.lyp.membersystem.utils.DisplayUtil;
import com.lyp.membersystem.utils.SystemStatusManager;
import com.lyp.membersystem.utils.ToastUtil;
import com.lyp.membersystem.view.dialog.WaitDialog;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class ServiceApplyActivity extends BaseActivity implements OnRefreshListener2<ListView> {
	private WaitDialog mWaitDialog;
	private SharedPreferences mSharedPreferences;
	private PullToRefreshListView mPullRefreshListView;
	private int mPage = 1;
	private int mRow = 100;
	private int mTotal;
	private ArrayList<ServiceBean> serviceList;
	private ListView lv_order;
	private ServicesApplayAdapter adapter;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MessageContants.MSG_GET_MY_SERVICE_LIST: {
				parseGetServiceList((String) msg.obj);
				break;
			}
			case MessageContants.MSG_DELETE_SERVICE: {
				parseDeleteService((String) msg.obj);
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
		setContentView(R.layout.services_applay_layout);
		initView();
		initData();
	}

	private void initView() {
		mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_listview);
		lv_order = mPullRefreshListView.getRefreshableView();
		lv_order.setDividerHeight(new DisplayUtil(this).dipToPx(1));
		lv_order.setSelector(android.R.color.transparent);
		mPullRefreshListView.setMode(Mode.DISABLED);
		mPullRefreshListView.setOnRefreshListener(this);
		serviceList = new ArrayList<ServiceBean>();
		adapter = new ServicesApplayAdapter(mHandler, serviceList, this);
		lv_order.setAdapter(adapter);
		lv_order.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long arg3) {
				// Intent intent = new Intent();
				// intent.setClass(OrderActivity.this,
				// OrderDetailActivity.class);
				// // intent.putExtra("id", contactSortModel.getId());
				// startActivity(intent);
			}
		});
	}

	private void initData() {
		if (mWaitDialog == null) {
			mWaitDialog = new WaitDialog(this, R.string.loading_data);
		}
		mWaitDialog.show();
		getData();
	}

	private void getData() {
		mSharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCE, MODE_PRIVATE);
		String tokenid = mSharedPreferences.getString(Constant.TOKEN_ID, "");
		String saleId = mSharedPreferences.getString(Constant.ID, "");
		NetProxyManager.getInstance().toGetMyServiceList(mHandler, tokenid, mPage + (serviceList.size() / mRow), mRow,
				null);
	}

	/**
	 * to parse login infomations from network
	 */
	private void parseGetServiceList(String result) {
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
			if (!success) {
				String message = json.getString("message");
				ToastUtil.showShort(this, message);
				if (json.getString("resCode").equals(Constant.RELOGIN)) {
					backLogin();
				}
				return;
			}
			JSONObject job = json.getJSONObject("object");
			JSONArray jsonArray = job.getJSONArray("infoList");
			mTotal = job.getInt("totalCount");
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jb = jsonArray.getJSONObject(i);
				ServiceBean serviceBean = new ServiceBean();
				if (jb.has("id")) {
					serviceBean.setServiceId(jb.getString("id"));
				}
				if (jb.has("serialNo")) {
					serviceBean.setApplyId(jb.getString("serialNo"));
				}
				serviceBean.setName(jb.getString("servicename"));
				serviceBean.setIcon(jb.getString("picUrls"));
				serviceBean.setReserveTime(jb.getString("reserveTime"));
				if (jb.has("status")) {
					serviceBean.setServiceStatus(jb.getString("status"));
				}
				serviceList.add(serviceBean);
			}
			mPullRefreshListView.onRefreshComplete();
			adapter.notifyDataSetChanged();
			if (mTotal <= 0) {
				ToastUtil.showShort(this, R.string.not_data);
			} else if (mTotal > mRow) {
				mPullRefreshListView.setMode(Mode.PULL_UP_TO_REFRESH);
			}
			if (mWaitDialog != null && mWaitDialog.isShowing()) {
				mWaitDialog.dismiss();
			}
		} catch (Exception ex) {
			LogUtils.e(ex.getMessage());
			return;
		}
	}

	private void parseDeleteService(String result) {
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
			LogUtils.d("" + json);
			if (!success) {
				String message = json.getString("message");
				ToastUtil.showShort(this, message);
				if (json.getString("resCode").equals(Constant.RELOGIN)) {
					backLogin();
				}
				return;
			}
//			serviceList.clear();
			adapter.notifyDataSetChanged();
//			getData();
		} catch (Exception ex) {
			LogUtils.e(ex.getMessage());
			return;
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

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		mHandler.postDelayed(mRefresCompleteRunnable, 1000);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		if (mTotal == serviceList.size()) {
			ToastUtil.showShort(this, R.string.no_more_data_loading);
			mHandler.postDelayed(mRefresCompleteRunnable, 1000);
		} else {
			getData();
		}
	}

	private Runnable mRefresCompleteRunnable = new Runnable() {

		@Override
		public void run() {
			mPullRefreshListView.onRefreshComplete();
		}
	};
}
