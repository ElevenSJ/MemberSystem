package com.lyp.membersystem.ui;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.handmark.pulltorefresh.library.PullToRefresSwipeMenuhListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.lyp.membersystem.R;
import com.lyp.membersystem.adapter.ServiceCustomerAdapter;
import com.lyp.membersystem.base.BaseActivity;
import com.lyp.membersystem.bean.GoodBean;
import com.lyp.membersystem.bean.TeamMemberBean;
import com.lyp.membersystem.log.LogUtils;
import com.lyp.membersystem.net.Errors;
import com.lyp.membersystem.net.MessageContants;
import com.lyp.membersystem.net.NetProxyManager;
import com.lyp.membersystem.utils.Constant;
import com.lyp.membersystem.utils.DisplayUtil;
import com.lyp.membersystem.utils.SystemStatusManager;
import com.lyp.membersystem.utils.ToastUtil;
import com.lyp.membersystem.view.CustomPopupWindow;
import com.lyp.membersystem.view.dialog.WaitDialog;

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
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class ServiceCustomerActivity extends BaseActivity implements OnRefreshListener2<SwipeMenuListView> {

	private final static int ADD_CUSTOMER_REQUEST_CODE = 0x13;
	private final static int UPDATE_CUSTOMER_REQUEST_CODE = 0x14;
	private PullToRefresSwipeMenuhListView mPullToRefresSwipeMenuhListView;
	private SwipeMenuListView lv_customer;
	private WaitDialog mWaitDialog;
	private CustomPopupWindow mPopWin;
	private SharedPreferences mSharedPreferences;
	private TextView tv_title;
	private List<TeamMemberBean> list;
	private ServiceCustomerAdapter adapter;
	private int mPage = 1;
	private int mRow = 10;
	private int mTotal;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MessageContants.MSG_GET_SALEMAN_MEMBER_LIST: {
				parseCustomerList((String) msg.obj);
				break;
			}
			case MessageContants.MSG_DELETE_SALEMAN_MEMBER: {
				parseDeleteCustomer((String) msg.obj);
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
		setContentView(R.layout.service_customer_layout);
		initView();
		ToastUtil.showLong(this, "向左滑动条目可以删除成员！");
		initData();
	}

	private void initView() {
		mPullToRefresSwipeMenuhListView = (PullToRefresSwipeMenuhListView) findViewById(
				R.id.pull_refresh_swipemenulistview);
		lv_customer = (SwipeMenuListView) mPullToRefresSwipeMenuhListView.getRefreshableView();
		lv_customer.setDividerHeight(new DisplayUtil(this).dipToPx(1));
		lv_customer.setSelector(android.R.color.transparent);
		mPullToRefresSwipeMenuhListView.setMode(Mode.DISABLED);
		// mPullToRefresSwipeMenuhListView.setOnRefreshListener(this);
		initSwipeMenuListView();
		tv_title = (TextView) findViewById(R.id.tv_title);
		list = new ArrayList<TeamMemberBean>();
		adapter = new ServiceCustomerAdapter(this, list);
		lv_customer.setAdapter(adapter);
		lv_customer.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Intent intent = new Intent();
				TeamMemberBean bean = list.get(arg2 - 1);
				intent.putExtra("id", bean.getId());
				intent.putExtra("name", bean.getName());
				intent.putExtra("avatar", bean.getAvatar());
				intent.putExtra("gengerStr", bean.getGenger());
				intent.putExtra("birthday", bean.getBirthday());
				intent.putExtra("marryStr", bean.getMarryStatus());
				intent.putExtra("areaStr", bean.getArea());
				intent.putExtra("address", bean.getAddress());
				intent.setClass(ServiceCustomerActivity.this, AddServiceCustomerActivity.class);
				startActivityForResult(intent, UPDATE_CUSTOMER_REQUEST_CODE);
			}
		});

	}

	private void initData() {
		if (mWaitDialog == null) {
			mWaitDialog = new WaitDialog(this, R.string.loading_data);
		}
		mWaitDialog.show();
		mSharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCE, MODE_PRIVATE);
		String tokenid = mSharedPreferences.getString(Constant.TOKEN_ID, "");
		String saleId = mSharedPreferences.getString(Constant.ID, "");
		// NetProxyManager.getInstance().toGetSalemanMemberList(mHandler,
		// tokenid, mPage + (list.size() / mRow), mRow);
		NetProxyManager.getInstance().toGetSalemanMemberList(mHandler, tokenid, 1, 10000);
	}

	public void initSwipeMenuListView() {
		// step 1. create a MenuCreator
		SwipeMenuCreator creator = new SwipeMenuCreator() {
			@Override
			public void create(SwipeMenu menu) {
				// create "delete" item
				SwipeMenuItem deleteItem = new SwipeMenuItem(getApplicationContext());
				// set item background
				deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)));
				// set item width
				deleteItem.setWidth(dp2px(90));
				// set a icon
				deleteItem.setIcon(R.drawable.ic_delete);
				// add to menu
				menu.addMenuItem(deleteItem);
			}
		};
		// set creator
		lv_customer.setMenuCreator(creator);

		// step 2. listener item click event
		lv_customer.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
				switch (index) {
				case 0:
					// delete
					if (mWaitDialog == null) {
						mWaitDialog = new WaitDialog(ServiceCustomerActivity.this, R.string.loading_data);
					}
					mWaitDialog.show();
					mSharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCE, MODE_PRIVATE);
					String tokenid = mSharedPreferences.getString(Constant.TOKEN_ID, "");
					NetProxyManager.getInstance().toDeleteSaleManMember(mHandler, tokenid, list.get(position).getId());
					break;
				}
				return true;
			}
		});

		// set SwipeListener
		lv_customer.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {

			@Override
			public void onSwipeStart(int position) {
				// swipe start
			}

			@Override
			public void onSwipeEnd(int position) {
				// swipe end
			}
		});
	}

	private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
	}

	public void onBack(View view) {
		finish();
	}

	public void addCustomer(View view) {
		Intent intent = new Intent();
		intent.setClass(this, AddServiceCustomerActivity.class);
		startActivityForResult(intent, ADD_CUSTOMER_REQUEST_CODE);

	}

	private void parseCustomerList(String result) {
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
			list.clear();
			JSONObject job = json.getJSONObject("object");
			JSONArray jsonArray = job.getJSONArray("infoList");
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				TeamMemberBean bean = new TeamMemberBean();
				bean.setId(jsonObject.getString("id"));
				if (jsonObject.has("name")) {
					bean.setName(jsonObject.getString("name"));
				}
				if (jsonObject.has("avatar")) {
					bean.setAvatar(jsonObject.getString("avatar"));
				}
				if (jsonObject.has("gender")) {
					bean.setGenger(jsonObject.getString("gender"));
				}
				if (jsonObject.has("birthday")) {
					bean.setBirthday(jsonObject.getString("birthday"));
				}
				if (jsonObject.has("maritalStatus")) {
					bean.setMarryStatus(jsonObject.getString("maritalStatus"));
				}
				if (jsonObject.has("district")) {
					bean.setArea(jsonObject.getString("district"));
				}
				if (jsonObject.has("address")) {
					bean.setAddress(jsonObject.getString("address"));
				}
				list.add(bean);
			}
			tv_title.setText("团队成员(" + list.size() + ")");
			adapter.notifyDataSetChanged();
		} catch (Exception ex) {
			LogUtils.e(ex.getMessage());
			return;
		} finally {
			if (mWaitDialog != null && mWaitDialog.isShowing()) {
				mWaitDialog.dismiss();
			}
		}
	}

	private void parseDeleteCustomer(String result) {
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
				return;
			}
			initData();
		} catch (Exception ex) {
			LogUtils.e(ex.getMessage());
			return;
		} finally {
			if (mWaitDialog != null && mWaitDialog.isShowing()) {
				mWaitDialog.dismiss();
			}
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == ADD_CUSTOMER_REQUEST_CODE || requestCode == UPDATE_CUSTOMER_REQUEST_CODE) {
				initData();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<SwipeMenuListView> refreshView) {
		mHandler.postDelayed(mRefresCompleteRunnable, 1000);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<SwipeMenuListView> refreshView) {
		if (mTotal == list.size()) {
			ToastUtil.showShort(this, R.string.no_more_data_loading);
			mHandler.postDelayed(mRefresCompleteRunnable, 1000);
		} else {
			initData();
		}

	}

	private Runnable mRefresCompleteRunnable = new Runnable() {

		@Override
		public void run() {
			mPullToRefresSwipeMenuhListView.onRefreshComplete();
		}
	};
}
