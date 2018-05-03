package com.lyp.membersystem.ui;

import java.util.ArrayList;
import java.util.List;

import com.lyp.membersystem.R;
import com.lyp.membersystem.adapter.ChooseGoodAdapter;
import com.lyp.membersystem.base.BaseActivity;
import com.lyp.membersystem.base.BaseApplication;
import com.lyp.membersystem.bean.NoticeBean;
import com.lyp.membersystem.bean.OrderDetailBean;
import com.lyp.membersystem.bean.SelectGoodBean;
import com.lyp.membersystem.database.CustomerDao;
import com.lyp.membersystem.log.LogUtils;
import com.lyp.membersystem.utils.SystemStatusManager;
import com.lyp.membersystem.view.contactsort.ContactSortModel;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;

public class ChooseGoodActivity extends BaseActivity {

	private ListView lv_choose_good;
	private List<ContactSortModel> contactList;
	private ChooseGoodAdapter mChooseGoodAdapter;
	private String customerIds;
	private CustomerDao mCustomerDao;
	private NoticeBean mNoticeBean;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTranslucentStatus();
		BaseApplication.getInstance().addActivity(this);
		setContentView(R.layout.choose_good_layout);
		customerIds = getIntent().getStringExtra("customerIds");
		mNoticeBean = getIntent().getParcelableExtra("select_notice");
		initView();
		initData();
	}

	private void initView() {
		lv_choose_good = (ListView) findViewById(R.id.lv_choose_good);
	}

	private void initData() {
		mCustomerDao = new CustomerDao(this);
		contactList = new ArrayList<ContactSortModel>();
		String[] customers = customerIds.split(",");
		for (int i = 0; i < customers.length; i++) {
			contactList.add(mCustomerDao.getContact(customers[i]));
		}
		mChooseGoodAdapter = new ChooseGoodAdapter(contactList, this, mNoticeBean);
		lv_choose_good.setAdapter(mChooseGoodAdapter);
	}

	public void onBack(View view) {
		finish();
	}

	public void nextStep(View view) {
		ArrayList<SelectGoodBean> mSelectGoodlist = mChooseGoodAdapter.getmSelectGoodlist();
		LogUtils.d("lyp", "mSelectGoodlist.size: " + mSelectGoodlist.size());
		Intent intent = new Intent();
		intent.putParcelableArrayListExtra("select_good", mSelectGoodlist);
		intent.putExtra("id", mNoticeBean.getId());
		intent.putExtra("cardText", mNoticeBean.getCardTexts());
		intent.setClass(this, EditCardByRuleActivity.class);
//		intent.setClass(this, ConfirmChooseOrderActivity.class);
		startActivity(intent);
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
	protected void onDestroy() {
		BaseApplication.getInstance().removeActivity(this);
		super.onDestroy();
	};
}
