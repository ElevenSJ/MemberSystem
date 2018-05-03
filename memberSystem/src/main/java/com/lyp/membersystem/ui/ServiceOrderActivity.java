package com.lyp.membersystem.ui;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lyp.membersystem.R;
import com.lyp.membersystem.adapter.ServiceOrderAdapter;
import com.lyp.membersystem.base.BaseActivity;
import com.lyp.membersystem.bean.OrderBean;
import com.lyp.membersystem.bean.ServiceOrderBean;
import com.lyp.membersystem.log.LogUtils;
import com.lyp.membersystem.net.Errors;
import com.lyp.membersystem.net.MessageContants;
import com.lyp.membersystem.net.NetProxyManager;
import com.lyp.membersystem.utils.Constant;
import com.lyp.membersystem.utils.DateUtil;
import com.lyp.membersystem.utils.DisplayUtil;
import com.lyp.membersystem.utils.SystemStatusManager;
import com.lyp.membersystem.utils.ToastUtil;
import com.lyp.membersystem.view.dialog.WaitDialog;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class ServiceOrderActivity extends BaseActivity implements OnRefreshListener2<ListView> {
	/** 资源对象 */
	public Resources res;
	private WaitDialog mWaitDialog;
	private SharedPreferences mSharedPreferences;
	private PullToRefreshListView mPullRefreshListView;
	private int mPage = 1;
	private int mRow = 10;
	private int mTotal;
	private ArrayList<ServiceOrderBean> serviceOrderList;
	private ListView lv_order;
	private ServiceOrderAdapter adapter;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MessageContants.MSG_GET_SERVICE_ORDER_LIST: {
				parseGetServiceOrderList((String) msg.obj);
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
		setContentView(R.layout.service_order_layout);
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
		serviceOrderList = new ArrayList<ServiceOrderBean>();
		adapter = new ServiceOrderAdapter(serviceOrderList, this);
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
		NetProxyManager.getInstance().toGetServiceOrderList(mHandler, tokenid, mPage + (serviceOrderList.size() / mRow),
				mRow);
	}

	public void newWedding(View view) {

	}


	/**
	 * to parse login infomations from network
	 */
	private void parseGetServiceOrderList(String result) {
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
				ServiceOrderBean serviceOrderBean = new ServiceOrderBean();
				serviceOrderBean.setOrderId(jb.getString("orderId"));
				serviceOrderBean.setOrderDate(DateUtil.stringToStr(jb.getString("createTime")));
				serviceOrderBean.setOrderPrice(jb.getDouble("orderPrice"));
				if (jb.has("smallPicUrl")) {
				    serviceOrderBean.setIcon(jb.getString("smallPicUrl"));
				}
				serviceOrderBean.setSalemanName(jb.getString("salemanName"));
				serviceOrderBean.setProductName(jb.getString("productName"));
				serviceOrderBean.setOrderState(jb.getString("orderStatus"));
//				serviceOrderBean.setExpiryDate(jb.getString(""));
				serviceOrderList.add(serviceOrderBean);
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
		if (mTotal == serviceOrderList.size()) {
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
