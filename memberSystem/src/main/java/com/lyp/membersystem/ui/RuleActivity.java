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
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.lyp.membersystem.R;
import com.lyp.membersystem.adapter.RuleAdapter;
import com.lyp.membersystem.base.BaseActivity;
import com.lyp.membersystem.bean.NoticeBean;
import com.lyp.membersystem.log.LogUtils;
import com.lyp.membersystem.net.Errors;
import com.lyp.membersystem.net.MessageContants;
import com.lyp.membersystem.net.NetProxyManager;
import com.lyp.membersystem.utils.Constant;
import com.lyp.membersystem.utils.DateUtil;
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
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class RuleActivity extends BaseActivity implements OnRefreshListener2<SwipeMenuListView> {

	private PullToRefresSwipeMenuhListView mPullToRefresSwipeMenuhListView;
	private SwipeMenuListView lv_rule;
	private WaitDialog mWaitDialog;
	private CustomPopupWindow mPopWin;
	private SharedPreferences mSharedPreferences;
	private List<NoticeBean> ruleList;
	private RuleAdapter mNoticeAdapter;
	private int mPage = 1;
	private int mRow = 10;
	private int mTotal;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MessageContants.MSG_GET_RULE_LIST: {
				parseRuleList((String) msg.obj);
				break;
			}
			case MessageContants.MSG_DELETE_NOTICE: {
				parseDeleteNotice((String) msg.obj);
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
		setContentView(R.layout.rule_layout);
		initView();
		initData();
	}

	private void initView() {
		mPullToRefresSwipeMenuhListView = (PullToRefresSwipeMenuhListView) findViewById(
				R.id.pull_refresh_swipemenulistview);
		lv_rule = (SwipeMenuListView) mPullToRefresSwipeMenuhListView.getRefreshableView();
		// initSwipeMenuListView();
		ruleList = new ArrayList<NoticeBean>();
		mNoticeAdapter = new RuleAdapter(ruleList, this);
		lv_rule.setAdapter(mNoticeAdapter);
		lv_rule.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long arg3) {
				NoticeBean noticeBean = ruleList.get(position - 1);
				Intent intent = new Intent();
				intent.setClass(RuleActivity.this, CreateRuleActivity.class);
				intent.putExtra("rule_id", noticeBean.getId());
				intent.putExtra("rule", noticeBean);
				startActivityForResult(intent, 0x1314);
			}
		});
	}

	private void initData() {
		getData();
	}

	private void getData() {
		if (mWaitDialog == null) {
			mWaitDialog = new WaitDialog(this, R.string.loading_data);
		}
		mWaitDialog.show();
		mSharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCE, MODE_PRIVATE);
		String tokenid = mSharedPreferences.getString(Constant.TOKEN_ID, "");
		String saleId = mSharedPreferences.getString(Constant.ID, "");
		NetProxyManager.getInstance().toGetRuleList(mHandler, tokenid, -1, -1);
		// NetProxyManager.getInstance().toGetRuleList(mHandler, tokenid, mPage
		// + (ruleList.size() / mRow),
		// mRow);
	}

	public void onBack(View view) {
		finish();
	}

	public void newCreate(View view) {
		Intent intent = new Intent();
		intent.setClass(RuleActivity.this, CreateRuleActivity.class);
		startActivityForResult(intent, 0x1314);
	}

	private void parseRuleList(String result) {
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
			JSONObject job = json.getJSONObject("object");
			JSONArray jsonArray = job.getJSONArray("infoList");
			ruleList.clear();
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				NoticeBean noticeBean = new NoticeBean();
				noticeBean.setId(jsonObject.getString("ruleId"));
				if (jsonObject.has("recordStatus")) {
					noticeBean.setReadStatus(jsonObject.getString("recordStatus"));
				}
				if (jsonObject.has("ruleName")) {
					noticeBean.setTitle(jsonObject.getString("ruleName"));
				}
				if (jsonObject.has("ruleType")) {
					noticeBean.setRuleType(jsonObject.getString("ruleType"));
				}
				if (jsonObject.has("gender")) {
					noticeBean.setGender(jsonObject.getString("gender"));
				}
				if (jsonObject.has("createTime")) {
					noticeBean.setDate(DateUtil.stringToStr(jsonObject.getString("createTime")));
				}
				if (jsonObject.has("remindTime")) {
					noticeBean.setRemindTime(jsonObject.getString("remindTime"));
				}
				if (jsonObject.has("condition")) {
					noticeBean.setCondition(jsonObject.getString("condition"));
				}
				if (jsonObject.has("haveChildren")) {
					noticeBean.setHaveChildren(jsonObject.getString("haveChildren"));
				}

				if (jsonObject.has("maritalStatus")) {
					noticeBean.setMaritalStatus(jsonObject.getString("maritalStatus"));
				}

				if (jsonObject.has("ageMax")) {
					noticeBean.setAgeMax(jsonObject.getString("ageMax"));
				}

				if (jsonObject.has("ageMin")) {
					noticeBean.setAgeMin(jsonObject.getString("ageMin"));
				}

				if (jsonObject.has("cardId") & jsonObject.has("cardImage")) {
					if (jsonObject.getJSONArray("cardImage").length() > 0) {
						noticeBean.setCard(
								jsonObject.getString("cardId") + "," + jsonObject.getJSONArray("cardImage").get(0));
					}
				}
				// if (jsonObject.has("packId")) {
				// noticeBean.setPack(jsonObject.getString("packId") + "," +
				// jsonObject.getString("packPrice") + ","
				// + jsonObject.getString("packImage"));
				// }
				if (jsonObject.has("productList")) {
					JSONArray jsonArray2 = jsonObject.getJSONArray("productList");
					List<String> goods = new ArrayList<String>();
					for (int j = 0; j < jsonArray2.length(); j++) {
						JSONObject jb = jsonArray2.getJSONObject(j);
						String str = jb.getString("pId") + "," + jb.getString("price") + "," + jb.getString("picUrl")
								+ "," + jb.getString("pname");
						// LogUtils.d("lyp", "str: " + str);
						goods.add(str);
					}
					noticeBean.setGoodlist(goods);
				}
				ruleList.add(noticeBean);
				mNoticeAdapter.notifyDataSetChanged();
			}
			if (mWaitDialog != null && mWaitDialog.isShowing()) {
				mWaitDialog.dismiss();
			}
		} catch (Exception ex) {
			LogUtils.e(ex.getMessage());
			return;
		}
	}

	private void parseDeleteNotice(String result) {
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
		} catch (Exception ex) {
			LogUtils.e(ex.getMessage());
			return;
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
		lv_rule.setMenuCreator(creator);

		// step 2. listener item click event
		lv_rule.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
				switch (index) {
				case 0:
					// delete
					break;
				}
				return true;
			}
		});

		// set SwipeListener
		lv_rule.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {

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

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<SwipeMenuListView> refreshView) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<SwipeMenuListView> refreshView) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK && requestCode == 0x1314) {

		}
		getData();
	}
}
