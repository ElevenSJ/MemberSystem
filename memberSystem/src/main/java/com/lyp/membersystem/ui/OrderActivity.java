package com.lyp.membersystem.ui;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lyp.membersystem.R;
import com.lyp.membersystem.adapter.OrderAdapter;
import com.lyp.membersystem.base.BaseActivity;
import com.lyp.membersystem.bean.CardEnvelopBean;
import com.lyp.membersystem.bean.GoodBean;
import com.lyp.membersystem.bean.NoticeBean;
import com.lyp.membersystem.log.LogUtils;
import com.lyp.membersystem.net.Errors;
import com.lyp.membersystem.net.MessageContants;
import com.lyp.membersystem.net.NetProxyManager;
import com.lyp.membersystem.utils.Constant;
import com.lyp.membersystem.utils.DateUtil;
import com.lyp.membersystem.utils.DisplayUtil;
import com.lyp.membersystem.utils.SystemStatusManager;
import com.lyp.membersystem.utils.ToastUtil;
import com.lyp.membersystem.view.CustomPopupWindow;
import com.lyp.membersystem.view.contactsort.ContactSortModel;
import com.lyp.membersystem.view.dialog.WaitDialog;
import com.sj.activity.bean.OrderBean;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class OrderActivity extends BaseActivity implements OnRefreshListener2<ListView> {

	private PullToRefreshListView mPullRefreshListView;
	private ListView lv_order;
	private WaitDialog mWaitDialog;
	private CustomPopupWindow mPopWin;
	private SharedPreferences mSharedPreferences;
	private List<OrderBean> orderList;
	private OrderAdapter mOrderAdapter;
	private int mPage = 1;
	private int mRow = 10;
	private int mTotal;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MessageContants.MSG_GET_ORDER_LIST: {
//				parseOrderList((String) msg.obj);
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
		setContentView(R.layout.order_layout);
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
		orderList = new ArrayList<OrderBean>();
		mOrderAdapter = new OrderAdapter(orderList, this);
		lv_order.setAdapter(mOrderAdapter);
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
		NetProxyManager.getInstance().toGetOrderList(mHandler, tokenid, saleId, mPage + (orderList.size() / mRow),
				mRow);
	}

	public void onBack(View view) {
		finish();
	}

//	private void parseOrderList(String result) {
//		if (Errors.ERROR_NET.equals(result) || Errors.ERROR_SERVER.equals(result)) {
//			ToastUtil.showLong(this, R.string.network_error);
//			if (mWaitDialog != null && mWaitDialog.isShowing()) {
//				mWaitDialog.dismiss();
//			}
//			return;
//		}
//		// to parser json data
//		try {
//			JSONObject json = new JSONObject(result);
//			boolean success = json.getBoolean("success");
//			LogUtils.d("-----" + json);
//			if (!success) {
//				String message = json.getString("message");
//				ToastUtil.showShort(this, message);
//				if (json.getString("resCode").equals(Constant.RELOGIN)) {
//					backLogin();
//				}
//				return;
//			}
//			JSONObject job = json.getJSONObject("object");
//			JSONArray jsonArray = job.getJSONArray("infoList");
//			mTotal = job.getInt("totalCount");
//			// orderList.clear();
//			for (int i = 0; i < jsonArray.length(); i++) {
//				double orderPrice = 0d;
//				JSONObject jb = jsonArray.getJSONObject(i);
//				OrderBean orderBean = new OrderBean();
//				orderBean.setOrderId(jb.getString("orderId"));
//				String receiveType = "";
//				if (jb.has("receiveType")) {
//					receiveType = jb.getString("receiveType");
//					orderBean.setReceiveType(receiveType);
//				}
//				if (receiveType.equals("2")) {
//					orderBean.setLogisticsno(jb.getString("logisticsNo"));
//				}
//				orderBean.setOrderDate(DateUtil.stringToStr(jb.getString("orderTime")));
//				orderPrice = jb.getDouble("cardPrice");
//				// orderPrice = orderPrice + jb.getDouble("packPrice");
//
//				JSONArray jsonArray3 = jb.getJSONArray("customerList");
//				List<ContactSortModel> contactList = new ArrayList<ContactSortModel>();
//				List<GoodBean> goodlist = new ArrayList<GoodBean>();
//				List<String> goodIds = new ArrayList<String>();
//				for (int j = 0; j < jsonArray3.length(); j++) {
//					JSONObject contactJB = jsonArray3.getJSONObject(j);
//					ContactSortModel contactSortModel = new ContactSortModel();
//					if (contactJB.has("avatar") && !contactJB.isNull("avatar")) {
//						contactSortModel.setAvater(contactJB.getString("avatar"));
//					}
//					if (contactJB.has("customerAvatar") && !contactJB.isNull("customerAvatar")) {
//						contactSortModel.setAvater(contactJB.getString("customerAvatar"));
//					}
//					// contactSortModel.setId(contactJB.getString("customerId"));
//					contactList.add(contactSortModel);
//
//					if (receiveType.equals("1")) {
//						orderBean.setLogisticsno(contactJB.getString("logisticsNo"));
//					}
//
//					JSONArray jsonArray2 = contactJB.getJSONArray("productList");
//					for (int k = 0; k < jsonArray2.length(); k++) {
//						JSONObject goodJB = jsonArray2.getJSONObject(k);
//						String pId = goodJB.getString("id");
//						if (goodIds.contains(pId)) {
//							continue;
//						}
//						goodIds.add(pId);
//						GoodBean goodBean = new GoodBean();
//						goodBean.setId(pId);
//						String goodPrice = goodJB.getString("price");
//						orderPrice = orderPrice + Double.valueOf(goodPrice);
//						goodBean.setPiprice(goodPrice);
//						if (goodJB.has("smallPicUrl")) {
//							String picUrls = goodJB.getString("smallPicUrl");
//							if (picUrls.contains(",")) {
//								goodBean.setPicUrls(picUrls.split(",")[0]);
//							} else if (picUrls.trim().length() > 2) {
//								goodBean.setPicUrls(picUrls);
//							}
//						}
//						if (goodJB.has("isService") && !goodJB.isNull("isService")) {
//							goodBean.setIsService(goodJB.getString("isService"));
//						}
//						goodBean.setPname(goodJB.getString("name"));
//						goodlist.add(goodBean);
//					}
//				}
//				orderBean.setGoodlist(goodlist);
//				orderPrice = orderPrice * jsonArray3.length();
//				orderBean.setCustomerList(contactList);
//				orderBean.setOrderPrice(jb.getDouble("totalPrice"));
//				if (jb.has("orderStatus"))
//					orderBean.setOrderState(jb.getString("orderStatus"));
//				orderList.add(orderBean);
//			}
//			mPullRefreshListView.onRefreshComplete();
//			mOrderAdapter.notifyDataSetChanged();
//			if (mTotal <= 0) {
//				ToastUtil.showShort(this, R.string.not_data);
//			} else if (mTotal > mRow) {
//				mPullRefreshListView.setMode(Mode.PULL_UP_TO_REFRESH);
//			}
//			if (mWaitDialog != null && mWaitDialog.isShowing()) {
//				mWaitDialog.dismiss();
//			}
//		} catch (Exception ex) {
//			LogUtils.e(ex.getMessage());
//			if (mWaitDialog != null && mWaitDialog.isShowing()) {
//				mWaitDialog.dismiss();
//			}
//			return;
//		}
//	}

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
		if (mTotal == orderList.size()) {
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && requestCode == 0x1314) {
			getData();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
