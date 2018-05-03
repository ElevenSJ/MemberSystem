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
import com.lyp.membersystem.adapter.GoodsFromNoticeAdapter;
import com.lyp.membersystem.base.BaseActivity;
import com.lyp.membersystem.base.BaseApplication;
import com.lyp.membersystem.bean.GoodBean;
import com.lyp.membersystem.log.LogUtils;
import com.lyp.membersystem.net.Errors;
import com.lyp.membersystem.net.MessageContants;
import com.lyp.membersystem.net.NetProxyManager;
import com.lyp.membersystem.service.ShoppingCartService;
import com.lyp.membersystem.utils.Constant;
import com.lyp.membersystem.utils.DisplayUtil;
import com.lyp.membersystem.utils.SystemStatusManager;
import com.lyp.membersystem.utils.ToastUtil;
import com.lyp.membersystem.view.dialog.WaitDialog;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class GoodsFromNoticeActivity extends BaseActivity implements OnRefreshListener2<ListView> {
	/** 资源对象 */
	public Resources res;
	private WaitDialog mWaitDialog;
	private PullToRefreshListView mPullRefreshListView;
	private String id;
	private String pId;
	private String customerIds;
	private int mPage = 1;
	private int mRow = 10;
	private int mTotal;
	private List<GoodBean> goodsList;
	private ListView mListView;
	private GoodsFromNoticeAdapter mAdapter;
	private SharedPreferences mSharedPreferences;

	private Handler mainHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MessageContants.MSG_PRODUCT_LIST: {
				// if (mIsPause) {
				// return;
				// }
				// if (mWaitDialog == null) {
				// mWaitDialog = new WaitDialog(MainActivity.this,
				// R.string.loading_data);
				// }
				// mWaitDialog.show();
				parseGetProductList((String) msg.obj);
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
		Bundle extras = getIntent().getExtras();
		id = extras.getString("id");
		pId = extras.getString("pId");
		customerIds = extras.getString("customerIds");
		setContentView(R.layout.goods_layout);
		initView();
		getData();

		// startActivity(new Intent().setClass(this,
		// ViewPageFragmentActivity.class));
	}

	private void initView() {
		mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_listview);
		mListView = mPullRefreshListView.getRefreshableView();
		mListView.setDividerHeight(new DisplayUtil(this).dipToPx(1));
		mListView.setSelector(android.R.color.transparent);
		mPullRefreshListView.setMode(Mode.DISABLED);
		mPullRefreshListView.setOnRefreshListener(this);
		goodsList = new ArrayList<GoodBean>();
		mAdapter = new GoodsFromNoticeAdapter(goodsList, this, customerIds);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Intent intent = new Intent();
				GoodBean goodBean = goodsList.get(arg2 - 1);
				intent.putExtra("id", goodBean.getId());
				intent.putExtra("customTexts", goodBean.getCustomTexts());
				intent.putExtra("customerIds", customerIds);
				intent.setClass(GoodsFromNoticeActivity.this, GoodDetailFromNoticeActivity.class);
				startActivity(intent);
			}
		});
	}

	private void getData() {
		mSharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCE, MODE_PRIVATE);
		String tokenid = mSharedPreferences.getString(Constant.TOKEN_ID, "");
		String saleId = mSharedPreferences.getString(Constant.ID, "");
		NetProxyManager.getInstance().toGetProductList(mainHandler, tokenid, mPage + (goodsList.size() / mRow), mRow,
				null, id);
	}

	@Override
	protected void onResume() {
		super.onResume();
//		if (Build.VERSION.SDK_INT < 23 || Settings.canDrawOverlays(this)) {
//			Intent intentService = new Intent(this, ShoppingCartService.class);
//			startService(intentService);
//		}
	}

	@Override
	protected void onPause() {
		super.onPause();
//		if (Build.VERSION.SDK_INT < 23 || Settings.canDrawOverlays(this)) {
//			Intent intent = new Intent(this, ShoppingCartService.class);
//			stopService(intent);
//		}
	}

	/**
	 * to parse product infomations from network
	 */
	private void parseGetProductList(String result) {
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
			JSONObject obj = json.getJSONObject("object");
			mTotal = obj.getInt("totalCount");
			JSONArray jsonArray = obj.getJSONArray("infoList");
			for (int i = 0; i < jsonArray.length(); i++) {
				GoodBean goodBean = new GoodBean();
				JSONObject childObj = jsonArray.getJSONObject(i);
				goodBean.setId(childObj.getString("id"));
				goodBean.setPname(childObj.getString("pname"));
				goodBean.setPsaleprice(childObj.getString("psaleprice"));
				if (childObj.has("smallPicUrl")) {
					goodBean.setSmallPicUrl(childObj.getString("smallPicUrl"));
				}
				if (childObj.has("piprice")) {
					goodBean.setPiprice(childObj.getString("piprice"));
				}
				if (childObj.has("customTexts") && !childObj.isNull("customTexts")) {
					String customTexts = null;
					JSONArray array = childObj.getJSONArray("customTexts");
					for (int j = 0; j < array.length(); j++) {
						if (j == 0) {
							customTexts = array.getString(j);
						} else {
							customTexts = customTexts + "_" + array.getString(j);
						}
					}
					goodBean.setCustomTexts(customTexts);
				}
				goodsList.add(goodBean);
			}
			mAdapter.notifyDataSetChanged();
			mPullRefreshListView.onRefreshComplete();
			if (mTotal <= 0) {
				ToastUtil.showShort(this, R.string.not_data);
			} else if (mTotal > mRow) {
				mPullRefreshListView.setMode(Mode.PULL_UP_TO_REFRESH);
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
		mainHandler.postDelayed(mRefresCompleteRunnable, 1000);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		if (mTotal == goodsList.size()) {
			ToastUtil.showShort(this, R.string.no_more_data_loading);
			mainHandler.postDelayed(mRefresCompleteRunnable, 1000);
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
	protected void onDestroy() {
		BaseApplication.getInstance().removeActivity(this);
		super.onDestroy();
	};
}
