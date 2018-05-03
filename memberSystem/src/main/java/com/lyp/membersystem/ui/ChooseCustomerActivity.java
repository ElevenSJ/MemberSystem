package com.lyp.membersystem.ui;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.lyp.membersystem.R;
import com.lyp.membersystem.adapter.ChooseCustomerAdapter;
import com.lyp.membersystem.base.BaseActivity;
import com.lyp.membersystem.base.BaseApplication;
import com.lyp.membersystem.bean.NoticeBean;
import com.lyp.membersystem.bean.QueryContactBean;
import com.lyp.membersystem.database.CustomerDao;
import com.lyp.membersystem.log.LogUtils;
import com.lyp.membersystem.net.Errors;
import com.lyp.membersystem.net.MessageContants;
import com.lyp.membersystem.net.NetProxyManager;
import com.lyp.membersystem.utils.Constant;
import com.lyp.membersystem.utils.DateUtil;
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

public class ChooseCustomerActivity extends BaseActivity {

	private final static int ADD_CUSTOMER_REQUEST_CODE = 0x1314;
	private ListView sortListView;
	private SideBar sideBar;
	private TextView dialog, mTvTitle;
	private ChooseCustomerAdapter adapter;
	// private EditTextWithDel mEtSearchName;
	private List<ContactSortModel> mCustomerList;
	private CheckBox customer_select_all;
	private WaitDialog mWaitDialog;
	private CustomPopupWindow mPopWin;
	private CustomerDao mCustomerDao;
	private NoticeBean mNoticeBean;
	private SharedPreferences mSharedPreferences;
	
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MessageContants.MSG_GET_CUSTOMER_LIST: {
				parseCustomerList((String) msg.obj);
				break;
			}
			case MessageContants.MSG_GET_CHOOSE_CUSTOMER_LIST: {
				parseCustomerList((String) msg.obj);
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
		mNoticeBean = getIntent().getParcelableExtra("select_notice");
		setContentView(R.layout.choose_customer_layout);
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
		sideBar.setTextView(dialog);
//		initDatas();
		initEvents();
		mCustomerList = new ArrayList<ContactSortModel>();
		adapter = new ChooseCustomerAdapter(this, mCustomerList);
		sortListView.setAdapter(adapter);
		getServiceDatas();
	}

	public void backMyCustomer(View view) {
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
		Intent intent = new Intent();
		String condition = mNoticeBean.getCondition();
		List<String> goodlist = mNoticeBean.getGoodlist();
		if (goodlist.size() >= 1) {
			intent.setClass(this, ChooseGoodActivity.class);
			intent.putExtra("select_notice", mNoticeBean);
		} else {
			intent.setClass(this, MallFragmentFromNoticeActivity.class);
		}
		intent.putExtra("customerIds", customerIds);
		LogUtils.d("lyp", "customerIds: " + customerIds);
		startActivity(intent);
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
		adapter.updateListView(mCustomerList);
	    adapter.notifyDataSetChanged();
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
				adapter.notifyDataSetChanged();
			}
		});

		sortListView.setOnItemLongClickListener(new Onlongclick());

	}
	
	private void getServiceDatas() {
		if (mWaitDialog == null) {
			mWaitDialog = new WaitDialog(this, R.string.loading_data);
		}
		if (!mWaitDialog.isShowing()) {
		    mWaitDialog.show();
		}
		mSharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCE, MODE_PRIVATE);
		String tokenid = mSharedPreferences.getString(Constant.TOKEN_ID, "");
		String saleId = mSharedPreferences.getString(Constant.ID, "");
		LogUtils.d("lyp", "ruleId: " + mNoticeBean.getRuleId());
		NetProxyManager.getInstance().toGetChooseCustomerList(mHandler, tokenid, saleId, 
				null, null, 0, mNoticeBean.getRuleId());
//		NetProxyManager.getInstance().toGetCustomerList(mHandler, tokenid, saleId, null, null, 0);
	}

	private void initDatas() {
		mCustomerDao = new CustomerDao(this.getApplicationContext());
		String condition = mNoticeBean.getCondition();
		if (condition.equals("1")) {
			QueryContactBean queryContactBean = new QueryContactBean();
			queryContactBean.setGender(mNoticeBean.getGender());
			queryContactBean.setMaritalStatus(mNoticeBean.getMaritalStatus());
			queryContactBean.setHaveChildren(mNoticeBean.getHaveChildren());
			queryContactBean.setAgeMax(mNoticeBean.getAgeMax());
			queryContactBean.setAgeMin(mNoticeBean.getAgeMin());
			mCustomerList = mCustomerDao.getContactList(queryContactBean);
		} else if (condition.equals("2")) {
			String feteDay = DateUtil.stringToStr5(mNoticeBean.getRemindTime());
			mCustomerList = mCustomerDao.getContactList(feteDay);
		} else {
			mCustomerList = mCustomerDao.getContactList();
		}
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
			JSONObject job = json.getJSONObject("object");
			JSONArray jsonArray = job.getJSONArray("infoList");
			mCustomerList.clear();
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				ContactSortModel contactSortModel = new ContactSortModel();
				contactSortModel.setId(jsonObject.getString("id"));
				if (jsonObject.has("birthday")) {
					String birthday = jsonObject.getString("birthday");
					contactSortModel.setBirthday(birthday);
					contactSortModel.setFeteDay(DateUtil.stringToStr5(birthday));
					LogUtils.d("lyp", "fete day: " + DateUtil.stringToStr5(birthday));
					LogUtils.d("lyp", "age: " + DateUtil.getAgeNum(birthday));
					contactSortModel.setAge(DateUtil.getAgeNum(birthday));
				}
				if (jsonObject.has("specialday")) {
					contactSortModel.setSpecialday(jsonObject.getString("specialday"));
				}
				if (jsonObject.has("cname")) {
					contactSortModel.setName(jsonObject.getString("cname"));
				}
				if (jsonObject.has("nickname")) {
					contactSortModel.setNickname(jsonObject.getString("nickname"));
				}
				if (jsonObject.has("gender")) {
					contactSortModel.setGender(jsonObject.getString("gender"));
				}
				if (jsonObject.has("profiles")) {
					contactSortModel.setProfiles(jsonObject.getString("profiles"));
				}
				if (jsonObject.has("cphone")) {
					contactSortModel.setCphone(jsonObject.getString("cphone"));
				}
				if (jsonObject.has("caddress")) {
					contactSortModel.setCaddress(jsonObject.getString("caddress"));
				}
				if (jsonObject.has("cemail")) {
					contactSortModel.setCemail(jsonObject.getString("cemail"));
				}
				if (jsonObject.has("maritalStatus")) {
					contactSortModel.setMarry(jsonObject.getString("maritalStatus"));
				}
				if (jsonObject.has("district")) {
					contactSortModel.setDistrict(jsonObject.getString("district"));
				}
				if (jsonObject.has("haveChildren")) {
					contactSortModel.setHaveChildren(jsonObject.getString("haveChildren"));
				}
				if (jsonObject.has("avatar")) {
					contactSortModel.setAvater(jsonObject.getString("avatar"));
				}
//				if (jsonObject.has("age")) {
//					contactSortModel.setAge(jsonObject.getInt("age"));
//				}
				if (jsonObject.has("policyNo")) {
					contactSortModel.setPolicyNo(jsonObject.getString("policyNo"));
				}
				if (jsonObject.has("profession")) {
					contactSortModel.setProfession(jsonObject.getString("profession"));
				}
				mCustomerList.add(contactSortModel);
			}
			if (mCustomerList.size() <= 0) {
				ToastUtil.showLongMessage("很遗憾，没有符合条件的金主！");
			}
			contactsSort();
		} catch (Exception ex) {
			LogUtils.e(ex.getMessage());
			return;
		} finally {
			if (mWaitDialog != null && mWaitDialog.isShowing()) {
				mWaitDialog.dismiss();
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && requestCode == ADD_CUSTOMER_REQUEST_CODE) {
			return;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onDestroy() {
		BaseApplication.getInstance().removeActivity(this);
		super.onDestroy();
//		if (mWaitDialog != null && mWaitDialog.isShowing()) {
//		    mWaitDialog.dismiss();
//		}
	};
}
