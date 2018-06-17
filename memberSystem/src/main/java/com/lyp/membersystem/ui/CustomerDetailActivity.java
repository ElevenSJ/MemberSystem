package com.lyp.membersystem.ui;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.lyp.membersystem.R;
import com.lyp.membersystem.adapter.SpecDateAdapter;
import com.lyp.membersystem.base.BaseActivity;
import com.lyp.membersystem.bean.SpecDateBean;
import com.lyp.membersystem.database.CustomerDao;
import com.lyp.membersystem.log.LogUtils;
import com.lyp.membersystem.manager.ImageManager;
import com.lyp.membersystem.net.Errors;
import com.lyp.membersystem.utils.Constant;
import com.lyp.membersystem.utils.SystemStatusManager;
import com.lyp.membersystem.utils.ToastUtil;
import com.lyp.membersystem.view.CircleImageView;
import com.lyp.membersystem.view.CustomPopupWindow;
import com.lyp.membersystem.view.MyListView;
import com.lyp.membersystem.view.contactsort.ContactSortModel;
import com.lyp.membersystem.view.dialog.WaitDialog;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

public class CustomerDetailActivity extends BaseActivity {

	private final static int MODIFY_CUSTOMER_REQUEST_CODE = 0x1315;
	private WaitDialog mWaitDialog;
	private CustomerDao mCustomerDao;
	private TextView tv_title;
	private CircleImageView customer_avater;
	private TextView tv_name;
	private TextView tv_gener;
	private TextView tv_birthday;
	private TextView tv_marry;
	private TextView tv_child;
	private TextView tv_address;
	private TextView tv_phone;
	private TextView tv_cemail;
	private TextView tv_district;
	private TextView tv_policy_no;
	private TextView tv_profession;
	private TextView tv_tags;
	private String id;
	private CustomPopupWindow mPopWin;
	private boolean isUpdate = false;
	private MyListView specListView;
	private SpecDateAdapter specDateAdapter;
	private List<SpecDateBean> specDateList;
	private TextView tv_nickname;

	private Handler mainHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0: {
				// if (mIsPause) {
				// return;
				// }
				// if (mWaitDialog == null) {
				// mWaitDialog = new WaitDialog(MainActivity.this,
				// R.string.loading_data);
				// }
				// mWaitDialog.show();
				parseGetProductType((String) msg.obj);
				break;
			}
			default:
				break;
			}
		};
	};
	private ContactSortModel contact;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTranslucentStatus();
		setContentView(R.layout.customer_detail_layout);
		id = getIntent().getStringExtra("id");
		initView();
		initData();
	}

	private void initView() {
		tv_title = (TextView) findViewById(R.id.tv_title);
		customer_avater = (CircleImageView) findViewById(R.id.customer_avater);
		tv_name = (TextView) findViewById(R.id.tv_name);
		tv_gener = (TextView) findViewById(R.id.tv_gener);
		tv_phone = (TextView) findViewById(R.id.tv_phone);
		tv_cemail = (TextView) findViewById(R.id.tv_cemail);
		tv_birthday = (TextView) findViewById(R.id.tv_birthday);
		tv_marry = (TextView) findViewById(R.id.tv_marry);
		tv_child = (TextView) findViewById(R.id.tv_child);
		tv_nickname = (TextView) findViewById(R.id.tv_nickname);
		tv_address = (TextView) findViewById(R.id.tv_address);
		tv_district = (TextView) findViewById(R.id.tv_district);
		tv_policy_no = (TextView) findViewById(R.id.tv_policy_no);
		tv_profession = (TextView) findViewById(R.id.tv_profession);
		tv_tags= (TextView) findViewById(R.id.tv_tag);
		specListView = (MyListView)findViewById(R.id.spec_list);
		specDateList = new ArrayList<SpecDateBean>();
//		specDateAdapter = new SpecDateAdapter(this, specDateList);
//		specListView.setAdapter(specDateAdapter);
	}

	private void initData() {
		mCustomerDao = new CustomerDao(this.getApplicationContext());
		contact = mCustomerDao.getContact(id);
		if (contact.getName() != null) {
			tv_name.setText(contact.getName());
		}

		if (contact.getAvater() != null) {
			ImageManager.loadImage(contact.getAvater(), customer_avater);
		}

		if (contact.getGender() != null) {
			String gender = contact.getGender();
			if (gender.equals("0")) {
				tv_gener.setText("女");
			} else if (gender.equals("1")) {
				tv_gener.setText("男");
			} else if (gender.equals("2")) {
				tv_gener.setText("全部");
			} 
			
		}
		if (contact.getTags() != null) {
			tv_tags.setText(contact.getTags());
		}
		if (contact.getCphone() != null) {
			tv_phone.setText(contact.getCphone());
		}

		if (contact.getBirthday() != null) {
			// tv_birthday.setText(DateUtil.stringToStr6(contact.getBirthday()));
			tv_birthday.setText(contact.getBirthday());
		}

		if (contact.getMarry() != null) {
			String marry = contact.getMarry();
			String[] marryArray = getResources().getStringArray(R.array.marry);
			if (marry.equals("0")) {
				tv_marry.setText(marryArray[0]);
			} else if (marry.equals("1")) {
				tv_marry.setText(marryArray[1]);
			}
			
//			if (marry.equals("0")) {
//				tv_marry.setText("未婚");
//			} else if (marry.equals("1")) {
//				tv_marry.setText("已婚");
//			} else if (marry.equals("2")) {
//				tv_marry.setText("离异");
//			} else if (marry.equals("3")) {
//				tv_marry.setText("丧偶");
//			}

		}
		if (contact.getPolicyNo() != null) {
			tv_policy_no.setText(contact.getPolicyNo());
		}
		if (contact.getProfession() != null) {
			tv_profession.setText(contact.getProfession());
		}
		
		if (contact.getCemail() != null) {
			tv_cemail.setText(contact.getCemail());
		}
		
		if (contact.getHaveChildren() != null) {
			tv_child.setText(contact.getHaveChildren().equals("0") ? "无子女" : "有子女");
		}
		if (contact.getNickname() != null) {
			tv_nickname.setText(contact.getNickname());
		}
		
		if (contact.getDistrict() != null) {
			tv_district.setText(contact.getDistrict());
		}
		if (contact.getCaddress() != null) {
			tv_address.setText(contact.getCaddress());
		}
		specDateList.clear();
		if (contact.getSpecialday() != null) {
			String specialday = contact.getSpecialday();
			if (specialday.contains(",")) {
				String[] specialdayAry = specialday.split(",");
				for (int i = 0; i < specialdayAry.length; i++) {
					String specStr = specialdayAry[i];
					getSpec(specStr);
				}
			} else {
				getSpec(specialday);
			}
//			specDateAdapter.notifyDataSetChanged();
		}
		specDateAdapter = new SpecDateAdapter(this, specDateList);
		specListView.setAdapter(specDateAdapter);
	}
	
	private void getSpec(String spec) {
		if (spec.contains(":")) {
			String[] specAry = spec.split(":");
			SpecDateBean specDateBean = new SpecDateBean();
			if(specAry.length>0){
				specDateBean.setDate(specAry[0]);
			}
			if(specAry.length>1){
				specDateBean.setRemark(specAry[1]);
			}
			specDateList.add(specDateBean);
		}
	}

	public void modifyCustomer(View view) {
		Intent intent = new Intent();
		intent.setClass(this, ModifyCustomerActivity.class);
		intent.putExtra("id", id);
		startActivityForResult(intent, MODIFY_CUSTOMER_REQUEST_CODE);
	}

	public void callOrSMS(View view) {
		View inflate = getLayoutInflater().inflate(R.layout.customer_info_popupwindow, null);
		mPopWin = new CustomPopupWindow(inflate, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
				CustomerDetailActivity.this);
		inflate.findViewById(R.id.dial_phone).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_DIAL);
				Uri uri = Uri.parse("tel:" + contact.getCphone());
				intent.setData(uri);
				// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
				mPopWin.dismiss();
			}
		});
		inflate.findViewById(R.id.send_sms).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Uri uri = Uri.parse("smsto:" + contact.getCphone());
				Intent intentMessage = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(intentMessage);
				mPopWin.dismiss();
			}
		});
		inflate.findViewById(R.id.delet_line).setVisibility(View.GONE);
		inflate.findViewById(R.id.delet_customer).setVisibility(View.GONE);
		
		mPopWin.showAtLocation(view, Gravity.CENTER, 0, 0);
	}

	/**
	 * to parse login infomations from network
	 */
	private void parseGetProductType(String result) {
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
			JSONArray object = json.getJSONArray("object");
			// ToastUtil.showShort(this, "ok--" + object);
		} catch (Exception ex) {
			LogUtils.e(ex.getMessage());
			return;
		}
	}

	public void onBack(View view) {
		if (isUpdate) {
			setResult(RESULT_OK);
		}
		finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
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
		if (resultCode == RESULT_OK && requestCode == MODIFY_CUSTOMER_REQUEST_CODE) {
			isUpdate = true;
			initData();
			return;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
