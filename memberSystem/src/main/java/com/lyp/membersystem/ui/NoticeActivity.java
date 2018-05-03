package com.lyp.membersystem.ui;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.lyp.membersystem.R;
import com.lyp.membersystem.adapter.NoticeAdapter;
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
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class NoticeActivity extends BaseActivity {

	private SwipeMenuListView lv_notice;
	private WaitDialog mWaitDialog;
	private CustomPopupWindow mPopWin;
	private SharedPreferences mSharedPreferences;
	private List<NoticeBean> noticeList;
	private NoticeAdapter mNoticeAdapter;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MessageContants.MSG_GET_NOTICE_LIST: {
				parseNoticeList((String) msg.obj);
				break;
			}
			case MessageContants.MSG_DELETE_NOTICE: {
				parseDeleteNotice((String) msg.obj);
				break;
			}
			case MessageContants.MSG_GET_RULE_NOTICE_LIST: {
				parseRuleNoticeList((String) msg.obj);
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
		setContentView(R.layout.notice_layout);
		initView();
		// initData();
	}

	@Override
	protected void onResume() {
		super.onResume();
		initData();
	}

	private void initView() {
		lv_notice = (SwipeMenuListView) findViewById(R.id.lv_notice);
		// initSwipeMenuListView();
		noticeList = new ArrayList<NoticeBean>();
		mNoticeAdapter = new NoticeAdapter(noticeList, this);
		lv_notice.setAdapter(mNoticeAdapter);
		lv_notice.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long arg3) {
				if ("1".equals(noticeList.get(position).getState())) {
					ToastUtil.showMessage("已经处理！");
					return;
				}
				
				if (System.currentTimeMillis() > noticeList.get(position).getInvalidTime()) {
					ToastUtil.showMessage("处理时间已经失效！");
					return;
				}
				Intent intent = new Intent();
				intent.setClass(NoticeActivity.this, ChooseCustomerActivity.class);
				// intent.putExtra("id", noticeList.get(position));
				intent.putExtra("select_notice", noticeList.get(position));
				startActivity(intent);
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
		// NetProxyManager.getInstance().toGetNoticeList(mHandler, tokenid,
		// saleId);
		// NetProxyManager.getInstance().toGetRuleNoticeList(mHandler, tokenid,
		// "0", null);
		NetProxyManager.getInstance().toGetRuleNoticeList(mHandler, tokenid, null, null);
	}

	public void onBack(View view) {
		finish();
	}

	public void ruleList(View view) {
		Intent intent = new Intent();
		intent.setClass(NoticeActivity.this, RuleActivity.class);
		startActivity(intent);
	}

	private void parseNoticeList(String result) {
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
			noticeList.clear();
			List<NoticeBean> handleList = new ArrayList<NoticeBean>();
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

				if (jsonObject.has("cardId")) {
					JSONArray cardImgArray = jsonObject.getJSONArray("cardImage");
					noticeBean.setCard(jsonObject.getString("cardId") + "," + jsonObject.getString("cardPrice") + ","
							+ cardImgArray.getString(0));
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
						goods.add(str);
					}
					noticeBean.setGoodlist(goods);
				}

				if ("1".equals(noticeBean.getState())) {
					handleList.add(noticeBean);
				} else {
					noticeList.add(noticeBean);
				}
			}
			noticeList.addAll(handleList);
			mNoticeAdapter.notifyDataSetChanged();
			if (mWaitDialog != null && mWaitDialog.isShowing()) {
				mWaitDialog.dismiss();
			}
		} catch (Exception ex) {
			LogUtils.e(ex.getMessage());
			return;
		}
	}

	private void parseRuleNoticeList(String result) {
		if (Errors.ERROR_NET.equals(result) || Errors.ERROR_SERVER.equals(result)) {
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
			JSONArray jsonArray = json.getJSONArray("object");
			noticeList.clear();
			List<NoticeBean> handleList = new ArrayList<NoticeBean>();
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject job = jsonArray.getJSONObject(i);
				JSONObject obj = new JSONObject(job.getString("noticeInfo"));
				NoticeBean noticeBean = new NoticeBean();
				
				
//				if (obj.has("recordStatus")) {
//					noticeBean.setReadStatus(obj.getString("recordStatus"));
//				}
				noticeBean.setRuleId(obj.getString("ruleId"));
				if (obj.has("ruleName")) {
					noticeBean.setTitle(obj.getString("ruleName"));
				}
				if (obj.has("ruleType")) {
					noticeBean.setRuleType(obj.getString("ruleType"));
				}
				if (obj.has("gender")) {
					noticeBean.setGender(obj.getString("gender"));
				}
//				if (obj.has("createTime")) {
//					noticeBean.setDate(DateUtil.stringToStr(obj.getString("createTime")));
//				}
//				if (obj.has("remindTime")) {
//					noticeBean.setRemindTime(obj.getString("remindTime"));
//				}

				if (obj.has("condition")) {
					noticeBean.setCondition(obj.getString("condition"));
				}

				if (obj.has("haveChildren")) {
					noticeBean.setHaveChildren(obj.getString("haveChildren"));
				}

				if (obj.has("maritalStatus")) {
					noticeBean.setMaritalStatus(obj.getString("maritalStatus"));
				}

				if (obj.has("ageMax")) {
					noticeBean.setAgeMax(obj.getString("ageMax"));
				}

				if (obj.has("ageMin")) {
					noticeBean.setAgeMin(obj.getString("ageMin"));
				}
				
				
				if (job.has("invalidTime")) {
					noticeBean.setInvalidTime(job.getLong("invalidTime"));
				}
				noticeBean.setId(job.getString("id"));
				noticeBean.setState(job.getString("status"));
				noticeBean.setTitle(job.getString("title"));
				if (job.has("remindTime")) {
					noticeBean.setRemindTime(job.getString("remindTime"));
				}
				if (obj.has("product")) {
					JSONArray array = obj.getJSONArray("product");
					List<String> goods = new ArrayList<String>();
					for (int j = 0; j < array.length(); j++) {
						JSONObject jb = array.getJSONObject(j);
						String str = jb.getString("id") + "," + jb.getString("price") + "," + jb.getString("pic") + ","
								+ jb.getString("name");
						goods.add(str);
					}
					noticeBean.setGoodlist(goods);
				}
//				String ruleType = obj.getString("ruleType");
//				noticeBean.setRuleType(ruleType);
				if (obj.has("card")) {
					JSONObject object = obj.getJSONObject("card");
					if (object.has("custom1")) {
						String cardInfo = null;
						if (!TextUtils.isEmpty(object.getString("custom1"))) {
							cardInfo = object.getString("custom1");
						}
						
						if (object.has("custom2") && !TextUtils.isEmpty(object.getString("custom2"))) {
							cardInfo = cardInfo + "_" + object.getString("custom2");
							
							if (object.has("custom3") && !TextUtils.isEmpty(object.getString("custom3"))) {
								cardInfo = cardInfo + "_" + object.getString("custom3");
							}
						}
						
					    noticeBean.setCardTexts(cardInfo);
					}
					String cardPrice = object.getString("price");
					String name = object.getString("name");
					String cardId = object.getString("id");
					String cardImage = null;
					if (object.has("cardImage") && object.getJSONArray("cardImage").length() > 0) {
						cardImage = object.getJSONArray("cardImage").getString(0);
						LogUtils.d("lyp", "cardImage：" + cardImage);
					}
					noticeBean.setCard(cardId + "," + cardPrice + "," + cardImage);
				}
				if ("1".equals(noticeBean.getState())) {
					handleList.add(noticeBean);
				} else {
					noticeList.add(noticeBean);
				}
			}
			noticeList.addAll(handleList);
			mNoticeAdapter.notifyDataSetChanged();
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
		lv_notice.setMenuCreator(creator);

		// step 2. listener item click event
		lv_notice.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
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
		lv_notice.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {

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

}
