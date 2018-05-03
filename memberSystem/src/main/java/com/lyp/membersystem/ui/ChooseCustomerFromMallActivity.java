package com.lyp.membersystem.ui;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.json.JSONObject;

import com.lyp.membersystem.R;
import com.lyp.membersystem.adapter.ChooseCustomerAdapter;
import com.lyp.membersystem.base.BaseActivity;
import com.lyp.membersystem.base.BaseApplication;
import com.lyp.membersystem.database.CustomerDao;
import com.lyp.membersystem.log.LogUtils;
import com.lyp.membersystem.net.Errors;
import com.lyp.membersystem.net.MessageContants;
import com.lyp.membersystem.net.NetProxyManager;
import com.lyp.membersystem.pay.PayActivity;
import com.lyp.membersystem.utils.Constant;
import com.lyp.membersystem.utils.SystemStatusManager;
import com.lyp.membersystem.utils.ToastUtil;
import com.lyp.membersystem.view.CustomPopupWindow;
import com.lyp.membersystem.view.contactsort.ContactSortModel;
import com.lyp.membersystem.view.contactsort.PinyinComparator;
import com.lyp.membersystem.view.contactsort.PinyinUtils;
import com.lyp.membersystem.view.contactsort.SideBar;
import com.lyp.membersystem.view.dialog.WaitDialog;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

public class ChooseCustomerFromMallActivity extends BaseActivity {

	private ListView sortListView;
	private SideBar sideBar;
	private TextView dialog, mTvTitle;
	private ChooseCustomerAdapter adapter;
	// private EditTextWithDel mEtSearchName;
	private List<ContactSortModel> mCustomerList;
	private CheckBox customer_select_all;
	private WaitDialog mWaitDialog;
	private SharedPreferences mSharedPreferences;
	private CustomerDao mCustomerDao;
	private TextView choose_customer_tv;
	private TextView pay_money_tv;
	private String good_info;
	private String card_info;
	private String package_info = null;
	private double goodPrice;
	private double cardPrice = 0d;
	private double packagePrice;
	private String card_content;
	private String productSkuIds;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MessageContants.MSG_ADD_ORDER: {
				parseAddOrder((String) msg.obj);
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
		BaseApplication.getInstance().addActivity(this);
		setTranslucentStatus();
		setContentView(R.layout.choose_customer_from_mall_layout);
		productSkuIds = getIntent().getStringExtra("productSkuIds");
		good_info = getIntent().getStringExtra("good_info");
		goodPrice = Double.valueOf(good_info.split("_")[1]);
		card_info = getIntent().getStringExtra("card_info");
		cardPrice = Double.valueOf(card_info.split("_")[1]);
//		package_info = getIntent().getStringExtra("package_info");
//		packagePrice = Double.valueOf(package_info.split("_")[1]);
		card_content = getIntent().getStringExtra("card_content");
		initViews();
	}

	private void initViews() {
		// mEtSearchName = (EditTextWithDel) findViewById(R.id.et_search);
		sideBar = (SideBar) findViewById(R.id.sidrbar);
		dialog = (TextView) findViewById(R.id.dialog);
		mTvTitle = (TextView) findViewById(R.id.tv_title);
		sortListView = (ListView) findViewById(R.id.lv_contact);
		mTvTitle.setText(R.string.my_customer);
		customer_select_all = (CheckBox) findViewById(R.id.customer_select_all);
		initDatas();
		initEvents();
		adapter = new ChooseCustomerAdapter(this, mCustomerList);
		sortListView.setAdapter(adapter);
		pay_money_tv = (TextView) findViewById(R.id.pay_money_tv);
		pay_money_tv.setText("实付款：￥" + (goodPrice + packagePrice + cardPrice) * 0);
		choose_customer_tv = (TextView) findViewById(R.id.choose_customer_tv);
		choose_customer_tv.setText("已选择客户：" + 0 + "人");
	}

	public void backAdvanceStep(View view) {
		back();
	}
	
	public void nextStep(View view) {
		List<ContactSortModel> selectContact = adapter.getSelectContact();
		String customerIds = "";
		for (int i = 0; i < selectContact.size(); i++) {
			if (i == 0) {
				customerIds = selectContact.get(i).getId();
			} else {
				customerIds = customerIds + "," + selectContact.get(i).getId();
			}
		}
		if (customerIds.length() <= 0) {
			ToastUtil.showMessage("请先选择客户！");
			return;
		}
		Intent i = new Intent();
		i.setClass(this, ChooseAddressFromMallActivity.class);
		i.putExtra("good_info", good_info);
		i.putExtra("productSkuIds", productSkuIds);
		i.putExtra("card_info", card_info);
		i.putExtra("customerIds", customerIds);
		i.putExtra("card_content", card_content);
		startActivity(i);
	}

	public void payBtn(View view) {
		mSharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCE, MODE_PRIVATE);
		String tokenid = mSharedPreferences.getString(Constant.TOKEN_ID, "");
		String saleId = mSharedPreferences.getString(Constant.ID, "");
		List<ContactSortModel> selectContact = adapter.getSelectContact();
		String customerIds = "";
		for (int i = 0; i < selectContact.size(); i++) {
			if (i == 0) {
				customerIds = selectContact.get(i).getId();
			} else {
				customerIds = customerIds + "," + selectContact.get(i).getId();
			}
		}
//		NetProxyManager.getInstance().toAddOrder(mHandler, tokenid, saleId, customerIds, good_info, card_info, package_info,
//				card_content, "1", null);
	}

	private void parseAddOrder(String result) {
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
			LogUtils.d("lyp", "" + json);
			if (!success) {
				String message = json.getString("message");
				ToastUtil.showShort(this, message);
				if (json.getString("resCode").equals(Constant.RELOGIN)) {
				    backLogin();
				}
				return;
			}
			ToastUtil.showLong(this, "下单成功！");
			Intent i = new Intent();
			i.setClass(this, PayActivity.class);
			i.putExtra("order_id", json.getString("object"));
			startActivity(i);
			BaseApplication.getInstance().finishSpecialPathActivity();
		} catch (Exception ex) {
			LogUtils.e(ex.getMessage());
			return;
		}
	}
	
	private void back() {
		finish();
	}

	private void contactsSort() {
		ArrayList<String> indexString = new ArrayList<String>();

		for (int i = 0; i < mCustomerList.size(); i++) {
			ContactSortModel sortModel = mCustomerList.get(i);
			String name = sortModel.getName();
			if (Character.isDigit(name.charAt(0))) {
				sortModel.setSortLetters("#");
				if (!indexString.contains("#")) {
					indexString.add("#");
				}
				continue;
			}
			String pinyin = PinyinUtils.getPingYin(name);
			String sortString = pinyin.substring(0, 1).toUpperCase();
			LogUtils.d("Sort contacts: " + sortString);
			if (sortString.matches("[A-Z]")) {
				sortModel.setSortLetters(sortString.toUpperCase());
				LogUtils.d("contacts Letters: " + sortString.toUpperCase());
				if (!indexString.contains(sortString)) {
					indexString.add(sortString);
				}
			}
		}
		Comparator<Object> com = Collator.getInstance(java.util.Locale.CHINA);
		Collections.sort(mCustomerList, new PinyinComparator());
		Collections.sort(indexString, com);
		// sideBar.setIndexText(indexString);
		// adapter.notifyDataSetChanged();
	}

	private void initEvents() {
		// 设置右侧触摸监听
		sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
			@Override
			public void onTouchingLetterChanged(String s) {
				// 该字母首次出现的位置
				int position = adapter.getPositionForSection(s.charAt(0));
				if (position != -1) {
					sortListView.setSelection(position + 1);
				}
			}
		});

		// ListView的点击事件
		sortListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				CheckBox checkBox = (CheckBox) view.findViewById(R.id.customer_select);
				if (checkBox.isChecked()) {
					customer_select_all.setChecked(false);
					checkBox.setChecked(false);
					adapter.ischeck.put(position, false);
					adapter.isCheckNum--;
					adapter.getSelectContact().remove(mCustomerList.get(position));
				} else {
					checkBox.setChecked(true);
					adapter.ischeck.put(position, true);
					adapter.isCheckNum++;
					if (adapter.isCheckNum == adapter.getCount()) {
						customer_select_all.setChecked(true);
					}
					adapter.getSelectContact().add(mCustomerList.get(position));
				}
				choose_customer_tv.setText("已选择客户：" + adapter.isCheckNum + "人");
				pay_money_tv.setText("实付款：￥" + (goodPrice + packagePrice + cardPrice) * adapter.isCheckNum);
				adapter.notifyDataSetChanged();
			}
		});

		sortListView.setOnItemLongClickListener(new Onlongclick());

	}

	private void initDatas() {
		sideBar.setTextView(dialog);
		mCustomerDao = new CustomerDao(this.getApplicationContext());
		mCustomerList = mCustomerDao.getContactList();
		if (mCustomerList == null)
			mCustomerList = new ArrayList<ContactSortModel>();
		else
			contactsSort();
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

	class Onlongclick implements AdapterView.OnItemLongClickListener {

		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			// if (adapter.isMulChoice()) {
			// return false;
			// }
			// mTvTitle.setText(R.string.select_customer);
			// adapter.setMulChoice(true);
			// // adapter.getSelectContact().clear();
			// select_mode.setVisibility(View.VISIBLE);
			// customer_select_all.setChecked(false);
			// for (int i = 0; i < mCustomerList.size(); i++) {
			// adapter.visiblecheck.put(i, CheckBox.VISIBLE);
			// // adapter.ischeck.put(i, false);
			// }
			// adapter.notifyDataSetChanged();
			// ToastUtil.showShort(MyCustomerActivity.this, "onItemLongClick");
			return true;
		}
	}

	public void selectAll(View view) {
		CheckBox checkbox = (CheckBox) view;
		if (checkbox.isChecked()) {
			for (int i = 0; i < mCustomerList.size(); i++) {
				adapter.ischeck.put(i, true);
			}
			adapter.getSelectContact().addAll(mCustomerList);
			adapter.isCheckNum = adapter.getCount();
		} else {
			for (int i = 0; i < mCustomerList.size(); i++) {
				adapter.ischeck.put(i, false);
			}
			adapter.getSelectContact().removeAll(mCustomerList);
			adapter.isCheckNum = 0;
		}
		choose_customer_tv.setText("已选择客户：" + adapter.isCheckNum + "人");
		pay_money_tv.setText("实付款：￥" + (goodPrice + packagePrice + cardPrice) * adapter.isCheckNum);
		adapter.notifyDataSetChanged();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			back();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void backLogin() {
		super.backLogin();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	protected void onDestroy() {
		BaseApplication.getInstance().removeActivity(this);
		super.onDestroy();
	};
}
