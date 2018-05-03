package com.lyp.membersystem.ui;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.lyp.membersystem.R;
import com.lyp.membersystem.base.BaseActivity;
import com.lyp.membersystem.database.CustomerDao;
import com.lyp.membersystem.imagepicker.ChooseImageActivity;
import com.lyp.membersystem.log.LogUtils;
import com.lyp.membersystem.manager.ImageManager;
import com.lyp.membersystem.net.Errors;
import com.lyp.membersystem.net.MessageContants;
import com.lyp.membersystem.net.NetProxyManager;
import com.lyp.membersystem.ui.permission.PermissionsActivity;
import com.lyp.membersystem.utils.CityDialog;
import com.lyp.membersystem.utils.CityDialog.InputListener;
import com.lyp.membersystem.utils.Constant;
import com.lyp.membersystem.utils.DateUtil;
import com.lyp.membersystem.utils.PermissionsChecker;
import com.lyp.membersystem.utils.PhotoUtil;
import com.lyp.membersystem.utils.SystemStatusManager;
import com.lyp.membersystem.utils.ToastUtil;
import com.lyp.membersystem.utils.ValidateUtils;
import com.lyp.membersystem.view.CircleImageView;
import com.lyp.membersystem.view.CustomPopupWindow;
import com.lyp.membersystem.view.contactsort.ContactSortModel;
import com.lyp.membersystem.view.dialog.WaitDialog;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

public class ModifyCustomerActivity extends BaseActivity {

	private WaitDialog mWaitDialog;
	private SharedPreferences mSharedPreferences;
	private DatePickerDialog mDatePickerDialog;
	private CircleImageView customer_avater;
	private TextView tv_name;
	private TextView tv_birthday;
	private TextView tv_area;
	private String areaStr;
	private EditText et_address;
	private EditText tv_phone;
	private EditText tv_cemail;
	private EditText tv_policy_no;
	private EditText tv_profession;
	private Spinner genger = null;
	private Spinner marry = null;
	private Spinner rule_haschild;
	private TextView tv_haschild;
	private CustomPopupWindow rule_haschild_pop;
	private String marryStr = "0";
	private String gengerStr = "0";
	private String haveChildren = "2";
	private ArrayAdapter<String> generAdapter;
	private CityDialog mCityDialog;
	private String id;
	private CustomerDao mCustomerDao;
	private ContactSortModel contact;
	private CustomPopupWindow mPopWin;
	private String imageLoadStr;
	private Uri imageUri;
	private String cropImageUri;
	private PermissionsChecker mPermissionsChecker; // 权限检测器
	private static final int REQUEST_PERMISSION = 1006; // 权限请求
	static final String[] PERMISSIONS = new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE,
			Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA };
	private PhotoUtil photoUtil;
	private static final int TAKE_PHOTO = 1000;
	private static final int SELECT_PHOTO = 1001;
	private static final int CROP_PHOTO = 1002;
	private static final int CHOOSE_IMAGE = 1003;
	private static final int SPEC_REQUEST = 1004;
	private String birthday = null;
	private ScrollView scroll_view;
	// 装在所有动态添加的Item的LinearLayout容器
	private LinearLayout addHotelNameView;
	private List<String> specList;
	private EditText tv_nickname;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MessageContants.MSG_UPDATE_CUSTOMER: {
				parseUpdateCustomer((String) msg.obj);
				break;
			}
			case MessageContants.MSG_UPLOAD_AVATER: {
				parseUploadInfo((String) msg.obj);
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
		setContentView(R.layout.add_or_modify_customer_layout);
		id = getIntent().getStringExtra("id");
		initView();
		initData();
	}

	private void initView() {
		mPermissionsChecker = new PermissionsChecker(this);
		photoUtil = new PhotoUtil(this);
		customer_avater = (CircleImageView) findViewById(R.id.customer_avater);
		customer_avater.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				uploadAvater(customer_avater);
			}
		});
		tv_name = (TextView) findViewById(R.id.tv_name);
		tv_birthday = (TextView) findViewById(R.id.tv_birthday);
		et_address = (EditText) findViewById(R.id.et_address);
		tv_area = (TextView) findViewById(R.id.tv_area);
		tv_phone = (EditText) findViewById(R.id.tv_phone);
		tv_cemail = (EditText) findViewById(R.id.tv_cemail);
		tv_policy_no = (EditText) findViewById(R.id.tv_policy_no);
		tv_profession = (EditText) findViewById(R.id.tv_profession);
		genger = (Spinner) findViewById(R.id.tv_gener);
		genger.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View view, int position, long arg3) {
				gengerStr = String.valueOf(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});
		marry = (Spinner) findViewById(R.id.tv_marry);
		ArrayAdapter<String> marryAdapter = new ArrayAdapter<String>(this, R.layout.activity_tipsprice_spinner,
				getResources().getStringArray(R.array.marry));
		marryAdapter.setDropDownViewResource(R.layout.activity_tipsprice_spinner);
		marry.setAdapter(marryAdapter);
		marry.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View view, int position, long arg3) {
				marryStr = String.valueOf(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});
		generAdapter = new ArrayAdapter<String>(this, R.layout.activity_tipsprice_spinner,
				getResources().getStringArray(R.array.gener));
		generAdapter.setDropDownViewResource(R.layout.activity_tipsprice_spinner);
		genger.setAdapter(generAdapter);
		rule_haschild = (Spinner) findViewById(R.id.rule_haschild);
		tv_haschild = (TextView) findViewById(R.id.tv_haschild);
		ArrayAdapter<String> childAdapter = new ArrayAdapter<String>(this, R.layout.activity_tipsprice_spinner,
				getResources().getStringArray(R.array.child));
		childAdapter.setDropDownViewResource(R.layout.activity_tipsprice_spinner);
		rule_haschild.setAdapter(childAdapter);
		rule_haschild.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View view, int position, long arg3) {
				haveChildren = String.valueOf(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});
		tv_nickname = (EditText) findViewById(R.id.tv_nickname);
		scroll_view = (ScrollView) findViewById(R.id.scroll_view);
		addHotelNameView = (LinearLayout) findViewById(R.id.ll_addView);
		specList = new ArrayList<String>();
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
			gengerStr = contact.getGender();
			genger.setSelection(Integer.valueOf(gengerStr));
		}

		if (contact.getCphone() != null) {
			tv_phone.setText(contact.getCphone());
		}

		if (contact.getBirthday() != null) {
			birthday = contact.getBirthday();
			// tv_birthday.setText(DateUtil.stringToStr6(birthday));
			tv_birthday.setText(birthday);
		}

		if (contact.getMarry() != null) {
			marryStr = contact.getMarry();
			if (marryStr.equals("0")) {
				marry.setSelection(0);
			} else if (marryStr.equals("1")) {
				marry.setSelection(1);
			} else {
			    marry.setSelection(0);
			}
			
		}

		if (contact.getHaveChildren() != null) {
//			haveChildren = contact.getHaveChildren();
//			rule_haschild.setSelection(Integer.valueOf(haveChildren));
			String[] childArray = getResources().getStringArray(R.array.child);
			tv_haschild.setText(childArray[Integer.valueOf(contact.getHaveChildren())]);
		}

		if (contact.getNickname() != null) {
			tv_nickname.setText(contact.getNickname());
		}
		
		if (contact.getPolicyNo() != null) {
			tv_policy_no.setText(contact.getPolicyNo());
		}

		if (contact.getProfession() != null) {
			tv_profession.setText(contact.getProfession());
		}

		if (contact.getDistrict() != null) {
			areaStr = contact.getDistrict();
			tv_area.setText(areaStr);
		}

		if (contact.getCaddress() != null) {
			et_address.setText(contact.getCaddress());
		}
		if (contact.getSpecialday() != null) {
			String specialday = contact.getSpecialday();
			if (specialday.contains(",")) {
				String[] specialdayAry = specialday.split(",");
				for (int i = 0; i < specialdayAry.length; i++) {
					specList.add(specialdayAry[i]);
				}
			} else {
				specList.add(specialday);
			}
		}
		addViewItem();
	}
	
	public void childPop(View v) {
		View inflate = getLayoutInflater().inflate(R.layout.child_popupwindow, null);
		rule_haschild_pop = new CustomPopupWindow(inflate, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, this);
		inflate.findViewById(R.id.no_child).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				tv_haschild.setText("无子女");
				tv_haschild.setTag("0");
				rule_haschild_pop.dismiss();
			}
		});
		inflate.findViewById(R.id.has_child).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				tv_haschild.setText("有子女");
				tv_haschild.setTag("1");
				rule_haschild_pop.dismiss();
			}
		});
		rule_haschild_pop.showAsDropDown(v);
	}

	// 添加ViewItem
	private void addViewItem(View view) {
		View hotelEvaluateView = View.inflate(this, R.layout.specially_layout_item, null);
		addHotelNameView.addView(hotelEvaluateView);
		specList.add("");
		sortSpecViewItem();
	}

	// 添加ViewItem
	private void addViewItem() {
		if (specList.size() == 0) {
			addViewItem(null);
			return;
		}
		for (int i = 0; i < specList.size(); i++) {
			String specStr = specList.get(i);
			if (!specStr.contains(":")) {
				continue;
			}
			String[] specAry = specStr.split(":");
			View hotelEvaluateView = View.inflate(this, R.layout.specially_layout_item, null);
			TextView spec_tv = (TextView) hotelEvaluateView.findViewById(R.id.spec_tv);
			spec_tv.setText(specAry[1]);
			TextView spec_date = (TextView) hotelEvaluateView.findViewById(R.id.spec_date);
			spec_date.setText(specAry[0]);
			addHotelNameView.addView(hotelEvaluateView);
		}
		sortSpecViewItem();
	}

	/**
	 * Item排序
	 */
	private void sortSpecViewItem() {
		int childCount = addHotelNameView.getChildCount();
		if (childCount <= 0) {
			addViewItem(null);
			return;
		}
		// 获取LinearLayout里面所有的view
		for (int i = 0; i < childCount; i++) {
			final View childAt = addHotelNameView.getChildAt(i);
			final ImageView add_spec = (ImageView) childAt.findViewById(R.id.add_spec);
			final ImageView delete_spec = (ImageView) childAt.findViewById(R.id.delete_spec);
			final ImageView edit_spec = (ImageView) childAt.findViewById(R.id.edit_spec);
			final TextView spec_tv = (TextView) childAt.findViewById(R.id.spec_tv);
			final View spec_edit = childAt.findViewById(R.id.spec_edit);
			final int index = i;
			spec_edit.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					Intent intent = new Intent();
					intent.setClass(ModifyCustomerActivity.this, SetSpecInfo.class);
					intent.putExtra("item", index);
					if (specList.size() > index) {
						String specStr = specList.get(index);
						if (specStr.contains(":")) {
							String[] specAry = specStr.split(":");
							intent.putExtra("remark", specAry[1]);
							intent.putExtra("date", specAry[0]);
						} else {
							intent.putExtra("remark", "");
							intent.putExtra("date", "");
						}
					} else {
						intent.putExtra("remark", "");
						intent.putExtra("date", "");
					}
					startActivityForResult(intent, SPEC_REQUEST);
				}
			});
			delete_spec.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					addHotelNameView.removeView(childAt);
					if (specList.size() > index) {
					    specList.remove(index);
					}
					sortSpecViewItem();
				}
			});
			// 如果是最后一个ViewItem，就设置为添加
			if (i == (addHotelNameView.getChildCount() - 1)) {
				add_spec.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						addViewItem(v);
					}
				});
			} else {
				add_spec.setVisibility(View.INVISIBLE);
			}
		}
	}

	private void setSpecView(int index, String remark, String date) {
		View childAt = addHotelNameView.getChildAt(index);
		TextView spec_tv = (TextView) childAt.findViewById(R.id.spec_tv);
		spec_tv.setText(remark);
		TextView spec_date = (TextView) childAt.findViewById(R.id.spec_date);
		spec_date.setText(date);
	}

	private void uploadAvater(View view) {
		View inflate = getLayoutInflater().inflate(R.layout.set_avater_popupwindow, null);
		mPopWin = new CustomPopupWindow(inflate, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, this);
		mPopWin.showAtLocation(view, Gravity.CENTER, 0, 0);
		inflate.findViewById(R.id.invoke_camera).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mPopWin.dismiss();
				// 检查权限(6.0以上做权限判断)
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
					if (mPermissionsChecker.lacksPermissions(PERMISSIONS)) {
						startPermissionsActivity();
					} else {
						// 打开相机
						imageUri = photoUtil.takePhoto(TAKE_PHOTO);
					}
				} else {
					imageUri = photoUtil.takePhoto(TAKE_PHOTO);
				}
			}
		});

		inflate.findViewById(R.id.select_from_gallery).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mPopWin.dismiss();
				Intent intent = new Intent(ModifyCustomerActivity.this, ChooseImageActivity.class);
				intent.putExtra("radio", true);
				startActivityForResult(intent, CHOOSE_IMAGE);
			}
		});
	}

	private void startPermissionsActivity() {
		PermissionsActivity.startActivityForResult(this, REQUEST_PERMISSION, PERMISSIONS);
	}

	public void addOrModifyCustomer(View view) {
		updateCustomer();
	}

	private void updateCustomer() {
		CharSequence nameText = tv_name.getText();
		if (nameText == null || nameText.toString().trim().length() == 0) {
			ToastUtil.showLong(this, "名字不能为空");
			return;
		}
		CharSequence phoneText = tv_phone.getText();
		if (phoneText == null || phoneText.toString().trim().length() == 0) {
			ToastUtil.showLong(this, "号码不能为空");
			return;
		}
		if (areaStr == null) {
			ToastUtil.showLong(this, "地区不能为空");
			return;
		}
		Editable addressET = et_address.getText();
		if (addressET == null || addressET.toString().length() <= 0) {
			ToastUtil.showLong(this, "地址不能为空");
			return;
		}
		if (birthday == null) {
			ToastUtil.showLong(this, "生日不能为空");
			return;
		}
		contact.setBirthday(birthday);
		contact.setFeteDay(DateUtil.stringToStr5(birthday));
		contact.setAge(DateUtil.getAgeNum(birthday));
		contact.setName(nameText.toString());
		contact.setCphone(phoneText.toString());
		contact.setGender(gengerStr);
		contact.setMarry(marryStr);
//		if (!haveChildren.equals("2")) {
//		    contact.setHaveChildren(haveChildren);
//		}
		if (tv_haschild.getTag() != null) {
			contact.setHaveChildren((String)tv_haschild.getTag());
		} else {
			ToastUtil.showLongMessage("请填入子女信息！");
			return;
		}
		contact.setDistrict(areaStr);
		contact.setCaddress(addressET.toString());
		Editable policyNoET = tv_policy_no.getText();
		if (policyNoET != null) {
			contact.setPolicyNo(policyNoET.toString());
		} else {
			contact.setPolicyNo("0");
		}
		Editable professinET = tv_profession.getText();
		if (professinET != null) {
			contact.setProfession(professinET.toString());
		}
		Editable cemail = tv_cemail.getText();
		if (cemail != null && cemail.toString().length() > 0) {
			if (!ValidateUtils.isEmail(cemail.toString())){
				ToastUtil.showLongMessage("请填入合法的邮箱！");
				return;
			}
			contact.setCemail(cemail.toString());
		}
		if (imageLoadStr != null) {
			contact.setAvater(imageLoadStr);
		}
		Editable text = tv_nickname.getText();
		if (text != null) {
			contact.setNickname(text.toString());
		}
		String spec = null;
		for (int i = 0; i < specList.size(); i++) {
			String specStr = specList.get(i);
			if (specStr == null || specStr.length() == 0) {
				continue;
			}
			if (spec == null) {
				spec = specStr;
			} else {
				spec = spec + "," + specStr;
			}
		}
		if (spec != null) {
			contact.setSpecialday(spec);
		}
		mSharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCE, MODE_PRIVATE);
		String tokenid = mSharedPreferences.getString(Constant.TOKEN_ID, "");
		String salemanId = mSharedPreferences.getString(Constant.ID, "");
		if (mWaitDialog == null) {
			mWaitDialog = new WaitDialog(this, R.string.loading_press);
		}
		mWaitDialog.show();
		NetProxyManager.getInstance().toUpdateCustomer(mHandler, tokenid, salemanId, contact);
	}

	public void setAddress(View view) {
		InputListener listener = new InputListener() {

			@Override
			public void getText(String str) {
				tv_area.setText(str);
				areaStr = str;
			}
		};
		mCityDialog = new CityDialog(ModifyCustomerActivity.this, listener);
		mCityDialog.setTitle(R.string.district);
		mCityDialog.show();
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
		tintManager.setStatusBarTintResource(R.color.main_bg_color);// 状态栏无背景
		getWindow().getDecorView().setFitsSystemWindows(true);
	}

	public void onBack(View view) {
		finish();
	}

	public void setBirthday(View view) {
		if (mDatePickerDialog == null) {
			Calendar calendar = Calendar.getInstance();
			mDatePickerDialog = new DatePickerDialog(this, DatePickerDialog.THEME_HOLO_LIGHT, new OnDateSetListener() {

				@Override
				public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
					birthday = year + "-" + DateUtil.addZeroStr(String.valueOf(monthOfYear + 1)) + "-"
							+ DateUtil.addZeroStr(String.valueOf(dayOfMonth));
					// tv_birthday.setText(DateUtil.stringToStr6(birthday));
					tv_birthday.setText(birthday);
				}
			}, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
		}

		if (!mDatePickerDialog.isShowing()) {
			mDatePickerDialog.show();
		}
	}

	private void parseUpdateCustomer(String result) {
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
			LogUtils.d("" + json);
			if (!success) {
				String message = json.getString("message");
				ToastUtil.showShort(this, message);
				return;
			}
			mCustomerDao.saveContact(contact);
			setResult(RESULT_OK);
			finish();
		} catch (Exception ex) {
			LogUtils.e(ex.getMessage());
			return;
		}
	}

	/**
	 * to parse upload avater from network
	 */
	private void parseUploadInfo(String result) {
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
			if (mWaitDialog != null && mWaitDialog.isShowing()) {
				mWaitDialog.dismiss();
			}
			LogUtils.d("Json: " + json);
			boolean success = json.getBoolean("success");
			if (!success) {
				String message = json.getString("message");
				ToastUtil.showShort(this, message);
				if (json.getString("resCode").equals(Constant.RELOGIN)) {
					backLogin();
				}
				return;
			}
			JSONArray jsonArray = json.getJSONArray("object");
			if (jsonArray.length() > 0) {
				imageLoadStr = jsonArray.getString(0);
				ImageManager.loadImage(imageLoadStr, customer_avater, R.drawable.personal);
			}
		} catch (Exception ex) {
			LogUtils.e(ex.getMessage());
			return;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// if (resultCode != RESULT_OK) {
		// return;
		// }
		switch (requestCode) {
		case TAKE_PHOTO:
			if (imageUri != null) {
				cropImageUri = photoUtil.startPhotoZoom(CROP_PHOTO, imageUri);
			}
			break;
		case SELECT_PHOTO:
			String imgPath = photoUtil.getCallPhoto(data);
			if (!TextUtils.isEmpty(imgPath)) {
				imageUri = Uri.parse(imgPath);
				cropImageUri = photoUtil.startPhotoZoom(CROP_PHOTO, imageUri);
			} else {
				ToastUtil.showShort(this, R.string.set_avater_fail);
			}
			break;
		case CHOOSE_IMAGE:
			if (resultCode != RESULT_OK) {
				return;
			}
			ArrayList<String> images = data.getStringArrayListExtra("choose_images");
			if (images.size() <= 0) {
				ToastUtil.showShort(this, R.string.set_avater_fail);
				return;
			}
			LogUtils.d("chooseImage: " + images.get(0));
			cropImageUri = photoUtil.startPhotoZoom(CROP_PHOTO, images.get(0));
			break;
		case CROP_PHOTO:
			if (cropImageUri == null) {
				return;
			}
			mSharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCE, MODE_PRIVATE);
			String tokenid = mSharedPreferences.getString(Constant.TOKEN_ID, "");
			String saleId = mSharedPreferences.getString(Constant.ID, "");
			if (mWaitDialog == null) {
				mWaitDialog = new WaitDialog(this, R.string.loading_press);
			}
			mWaitDialog.show();
			NetProxyManager.getInstance().toUploadAvater(mHandler, tokenid, cropImageUri);
			break;
		case SPEC_REQUEST:
			if (resultCode != RESULT_OK) {
				return;
			}
			int index = data.getIntExtra("item", -1);
			if (index == -1) {
				return;
			}
			String remark = data.getStringExtra("remark");
			String date = data.getStringExtra("date");
			if (specList.get(index) != null) {
			    specList.set(index, date + ":" + remark);
			} else {
			    specList.add(date + ":" + remark);
			}
			setSpecView(index, remark, date);
			break;
		default:
			break;
		}
	}
}
