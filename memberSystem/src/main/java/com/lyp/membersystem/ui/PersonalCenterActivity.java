package com.lyp.membersystem.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

import com.lyp.membersystem.LoginActivity;
import com.lyp.membersystem.R;
import com.lyp.membersystem.base.BaseActivity;
import com.lyp.membersystem.base.BaseApplication;
import com.lyp.membersystem.imagepicker.ChooseImageActivity;
import com.lyp.membersystem.log.LogUtils;
import com.lyp.membersystem.manager.ImageManager;
import com.lyp.membersystem.net.Errors;
import com.lyp.membersystem.net.MessageContants;
import com.lyp.membersystem.net.NetProxyManager;
import com.lyp.membersystem.ui.permission.PermissionsActivity;
import com.lyp.membersystem.utils.Constant;
import com.lyp.membersystem.utils.DateUtil;
import com.lyp.membersystem.utils.PermissionsChecker;
import com.lyp.membersystem.utils.PhotoUtil;
import com.lyp.membersystem.utils.SystemStatusManager;
import com.lyp.membersystem.utils.ToastUtil;
import com.lyp.membersystem.view.CircleImageView;
import com.lyp.membersystem.view.CustomPopupWindow;
import com.lyp.membersystem.view.dialog.WaitDialog;
import com.yuntongxun.ecdemo.common.utils.LogUtil;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

public class PersonalCenterActivity extends BaseActivity {

	private static final String PHOTO_DATE_FORMAT = "'AVATER'_yyyyMMdd_HHmmss";
	private static final String AVATER_PATH = Environment.getExternalStorageDirectory() + "/membersystem/";
	private static final int TAKE_PHOTO = 1000;
	private static final int SELECT_PHOTO = 1001;
	private static final int CROP_PHOTO = 1002;
	private static final int CHOOSE_IMAGE = 1003;
	private static final int REQUEST_CAPTURE = 1004;
	private static final int REQUEST_PICTURE = 1005;
	private static final int CHOOSE_Q_IMAGE = 1006;
	private static final int TAKE_PHOTO_SIGN = 1007;
	private static final int CHOOSE_IMAGE_SIGN = 1008;
	private static final int CROP_PHOTO_SIGN = 1009;
	private PermissionsChecker mPermissionsChecker; // 权限检测器
	private static final int REQUEST_PERMISSION = 1006; // 权限请求
	static final String[] PERMISSIONS = new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE,
			Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA };
	private PhotoUtil photoUtil;
	private CustomPopupWindow mPopWin;
	private Uri imageUri;
	private String cropImageUri;
	private Bitmap avaterBitmap = null;
	private WaitDialog mWaitDialog;
	private SharedPreferences mSharedPreferences;
	private TextView tv_username;
	private TextView tv_phone_call;
	private TextView tv_genger;
	private TextView tv_mail;
	private TextView tv_profession;
	private TextView tv_office;
	private TextView tv_company_address;
	private TextView tv_renewal;
	private CircleImageView user_avater;
	private String id;
	private TextView tv_company;
	private ImageView q_iv;
	private String userName = "";
	private String phoneCall = "";
	private String mail = "";
	private String company = "";
	private String profession = "";
	private String office = "";
	private String company_address = "";
	private DatePickerDialog mDatePickerDialog;
	private TextView tv_age;
	private String age = "";
	private ImageView iv_signature;
	private TextView tv_card;
	private ImageView iv_signature_base;

	private Handler mainHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MessageContants.MSG_GET_PERSON_INFO: {
				parseGetPersonInfo((String) msg.obj);
				break;
			}
			case MessageContants.MSG_UPLOAD_AVATER: {
				parseUploadInfo((String) msg.obj);
				break;
			}
			case MessageContants.MSG_UPDATE_PERSON_INFO: {
				parseUpdatePersionInfo((String) msg.obj);
				break;
			}
			case MessageContants.MSG_UPLOAD_Q_IMG: {
				parseUploadQGraphInfo((String) msg.obj);
				break;
			}
			case MessageContants.MSG_UPLOAD_SIGN: {
				parseUploadSignInfo((String) msg.obj);
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
		setContentView(R.layout.personal_center_layout);
		initView();
		initData();
	}

	private void initView() {
		iv_signature = (ImageView) findViewById(R.id.iv_signature);
		iv_signature_base = (ImageView) findViewById(R.id.iv_signature_base);
		iv_signature_base.setVisibility(View.GONE);
		tv_username = (TextView) findViewById(R.id.tv_username);
		tv_phone_call = (TextView) findViewById(R.id.tv_phone_call);
		tv_genger = (TextView) findViewById(R.id.tv_genger);
		tv_mail = (TextView) findViewById(R.id.tv_mail);
		tv_age = (TextView) findViewById(R.id.tv_age);
		user_avater = (CircleImageView) findViewById(R.id.user_avater);
		q_iv = (ImageView) findViewById(R.id.q_iv);
		tv_company = (TextView) findViewById(R.id.tv_company);
		tv_profession = (TextView) findViewById(R.id.tv_profession);
		tv_office = (TextView) findViewById(R.id.tv_office);
		tv_company_address = (TextView) findViewById(R.id.tv_company_address);
		tv_renewal = (TextView) findViewById(R.id.tv_renewal);
		tv_card = (TextView) findViewById(R.id.tv_card);
		tv_card.setVisibility(View.GONE);
		mPermissionsChecker = new PermissionsChecker(this);
		photoUtil = new PhotoUtil(this);
	}

	private void initData() {
		mSharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCE, MODE_PRIVATE);
		String tokenid = mSharedPreferences.getString(Constant.TOKEN_ID, "");
		String saleId = mSharedPreferences.getString(Constant.ID, "");
		if (mWaitDialog == null) {
			mWaitDialog = new WaitDialog(this, R.string.loading_data);
		}
		mWaitDialog.show();
		NetProxyManager.getInstance().toGetPersonInfo(mainHandler, tokenid, saleId);
	}

	public void editText(View view) {
		Intent intent = new Intent();
		intent.setClass(this, SetPersonInfo.class);
		switch (view.getId()) {
		case R.id.tv_username:
			intent.putExtra("name", "用户名：");
			intent.putExtra("content", userName);
			break;
		case R.id.tv_phone_call:
			intent.putExtra("name", "手机号：");
			intent.putExtra("content", phoneCall);
			break;
		case R.id.tv_mail:
			intent.putExtra("name", "邮箱：");
			intent.putExtra("content", mail);
			break;
		case R.id.tv_profession:
			intent.putExtra("name", "职称：");
			intent.putExtra("content", profession);
			break;
		case R.id.tv_company:
			intent.putExtra("name", "公司：");
			intent.putExtra("content", company);
			break;
		case R.id.tv_office:
			intent.putExtra("name", "营业处：");
			intent.putExtra("content", office);
			break;
		case R.id.tv_company_address:
			intent.putExtra("name", "公司地址：");
			intent.putExtra("content", company_address);
			break;
		}
		startActivityForResult(intent, view.getId());
	}
	
	public void signature(View view) {
		View inflate = getLayoutInflater().inflate(R.layout.set_avater_popupwindow, null);
		mPopWin = new CustomPopupWindow(inflate, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, this);
		mPopWin.showAsDropDown(iv_signature);
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
						imageUri = photoUtil.takePhoto(TAKE_PHOTO_SIGN);
					}
				} else {
					imageUri = photoUtil.takePhoto(TAKE_PHOTO_SIGN);
				}
			}
		});

		inflate.findViewById(R.id.select_from_gallery).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mPopWin.dismiss();
				Intent intent = new Intent(PersonalCenterActivity.this, ChooseImageActivity.class);
				intent.putExtra("radio", true);
				startActivityForResult(intent, CHOOSE_IMAGE_SIGN);
			}
		});
	}

	public void setBirthday(View view) {
		if (mDatePickerDialog == null) {
			Calendar calendar = Calendar.getInstance();
			mDatePickerDialog = new DatePickerDialog(this, DatePickerDialog.THEME_HOLO_LIGHT, new OnDateSetListener() {

				@Override
				public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
					String birthday = year + "-" + DateUtil.addZeroStr(String.valueOf(monthOfYear + 1)) + "-"
							+ DateUtil.addZeroStr(String.valueOf(dayOfMonth));
					// tv_birthday.setText(DateUtil.stringToStr6(birthday));
					// age = DateUtil.getAge(birthday);
					age = birthday;
					tv_age.setText(age);
					updatePersonInfo(null, null, null, null, age, null, null, null);
				}
			}, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
		}

		if (!mDatePickerDialog.isShowing()) {
			mDatePickerDialog.show();
		}
	}

	public void onBack(View view) {
		finish();
	}

	public void uploadAvater(View view) {
		View inflate = getLayoutInflater().inflate(R.layout.set_avater_popupwindow, null);
		mPopWin = new CustomPopupWindow(inflate, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, this);
		mPopWin.showAsDropDown(user_avater);
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
				// Intent intentFromGallery = new Intent();
				// intentFromGallery.setType("image/*");
				// intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
				// startActivityForResult(intentFromGallery, SELECT_PHOTO);

				// if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				// if (mPermissionsChecker.lacksPermissions(PERMISSIONS)) {
				// startPermissionsActivity();
				// } else {
				// // 打开相册
				// photoUtil.callPhoto(SELECT_PHOTO);
				// }
				// } else {
				// photoUtil.callPhoto(SELECT_PHOTO);
				// }

				Intent intent = new Intent(PersonalCenterActivity.this, ChooseImageActivity.class);
				intent.putExtra("radio", true);
				startActivityForResult(intent, CHOOSE_IMAGE);
			}
		});
	}

	private void startPermissionsActivity() {
		PermissionsActivity.startActivityForResult(this, REQUEST_PERMISSION, PERMISSIONS);
	}

	public String generateTempPhotoFileName() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat(PHOTO_DATE_FORMAT);
		return dateFormat.format(date) + ".jpg";
	}

	public void memberBuy(View view) {
		Intent intent = new Intent();
		intent.setClass(this, MemberActivity.class);
		startActivity(intent);
	}

	public void serviceBtn(View view) {
		Intent intent = new Intent();
		intent.setClass(this, ServiceOrderActivity.class);
		startActivity(intent);
	}

	public void cardPackBtn(View view) {
		Intent intent = new Intent();
		intent.setClass(this, MemberCardFragmentActivity.class);
		startActivity(intent);
	}
	
	public void expressAddressBtn(View view) {
		Intent intent = new Intent();
		intent.setClass(this, SetExpressActivity.class);
		startActivity(intent);
	}
	
	public void queryOrder(View view) {
		Intent intent = new Intent();
		intent.setClass(this, OrderActivity.class);
		startActivity(intent);
	}
	
	public void queryApplyService(View view) {
		Intent i = new Intent();
		i.setClass(this, ServiceApplyActivity.class);
		startActivity(i);
	}

	public void exitApp(View view) {
		BaseApplication.getInstance().finishAllActivity();
		Editor editor = mSharedPreferences.edit();
		editor.clear().commit();
		Intent i = new Intent();
		i.setClass(this, LoginActivity.class);
		startActivity(i);
		finish();
	}
	
	public void settings(View view){
		Intent intent = new Intent();
		intent.setClass(this, SettingsActivity.class);
		startActivity(intent);
	}

	public void setCropImageIntent(Uri data) {
		File file = new File(AVATER_PATH);
		if (!file.exists()) {
			file.mkdirs();
		}
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(data, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 118);
		intent.putExtra("outputY", 118);
		intent.putExtra("scale", true);
		intent.putExtra("scaleUpIfNeeded", true);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, CROP_PHOTO);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		LogUtil.d("lyp",
				"onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode + ", data=" + data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case R.id.tv_username:
				userName = data.getStringExtra("content");
				updatePersonInfo(null, null, userName, null, null, null, null, null);
				return;
			case R.id.tv_phone_call:
				phoneCall = data.getStringExtra("content");
				updatePersonInfo(phoneCall, null, null, null, null, null, null, null);
				return;
			case R.id.tv_mail:
				mail = data.getStringExtra("content");
				updatePersonInfo(null, mail, null, null, null, null, null, null);
				return;
			case R.id.tv_profession:
				profession = data.getStringExtra("content");
				updatePersonInfo(null, null, null, null, null, profession, null, null);
				return;
			case R.id.tv_office:
				office = data.getStringExtra("content");
				updatePersonInfo(null, null, null, null, null, null, office, null);
				return;
			case R.id.tv_company_address:
				company_address = data.getStringExtra("content");
				updatePersonInfo(null, null, null, null, null, null, null, company_address);
				return;
			case R.id.tv_company:
				company = data.getStringExtra("content");
				updatePersonInfo(null, null, null, company, null, null, null, null);
				return;
			}
		}
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
		case CHOOSE_Q_IMAGE:
			if (resultCode != RESULT_OK) {
				return;
			}
			ArrayList<String> qImages = data.getStringArrayListExtra("choose_images");
			if (qImages.size() <= 0) {
				ToastUtil.showShort(this, "设置Q图失败！");
				return;
			}
			LogUtils.d("chooseImage: " + qImages.get(0));
			String token_id = mSharedPreferences.getString(Constant.TOKEN_ID, "");
			if (mWaitDialog == null) {
				mWaitDialog = new WaitDialog(this, R.string.loading_data);
			}
			mWaitDialog.show();
			String q_path = PhotoUtil.compressPicture(qImages.get(0));
			if (q_path == null) {
				// mWaitDialog.dismiss();
				q_path = qImages.get(0);
			}
			NetProxyManager.getInstance().toUploadQImg(mainHandler, token_id, q_path);
			break;
		case CROP_PHOTO:
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				if (cropImageUri == null) {
					return;
				}
				String tokenid = mSharedPreferences.getString(Constant.TOKEN_ID, "");
				String saleId = mSharedPreferences.getString(Constant.ID, "");
				if (mWaitDialog == null) {
					mWaitDialog = new WaitDialog(this, R.string.loading_data);
				}
				mWaitDialog.show();
				NetProxyManager.getInstance().toUploadAvater(mainHandler, tokenid, cropImageUri);
				return;
			}
			if (data == null) {
				return;
			}
			avaterBitmap = data.getParcelableExtra("data");
			if (saveBitmap(avaterBitmap)) {
				// mSharedPreferences =
				// getSharedPreferences(Constant.SHARED_PREFERENCE,
				// MODE_PRIVATE);
				String tokenid = mSharedPreferences.getString(Constant.TOKEN_ID, "");
				String saleId = mSharedPreferences.getString(Constant.ID, "");
				if (mWaitDialog == null) {
					mWaitDialog = new WaitDialog(this, R.string.loading_data);
				}
				mWaitDialog.show();
				NetProxyManager.getInstance().toUploadAvater(mainHandler, tokenid, AVATER_PATH + "userAvater.png");
				// user_avater.setImageBitmap(avaterBitmap);
			} else {
				ToastUtil.showShort(this, R.string.set_avater_fail);
			}
			break;
		case TAKE_PHOTO_SIGN:
			if (imageUri != null) {
				cropImageUri = photoUtil.startSignPhotoZoom(CROP_PHOTO_SIGN, imageUri);
			}
			break;
		case CHOOSE_IMAGE_SIGN:
			if (resultCode != RESULT_OK) {
				return;
			}
			ArrayList<String> imagesSign = data.getStringArrayListExtra("choose_images");
			if (imagesSign.size() <= 0) {
				ToastUtil.showShort(this, "设置签名失败");
				return;
			}
			LogUtils.d("chooseImage: " + imagesSign.get(0));
			cropImageUri = photoUtil.startSignPhotoZoom(CROP_PHOTO_SIGN, imagesSign.get(0));
			break;
		case CROP_PHOTO_SIGN:
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				if (cropImageUri == null) {
					return;
				}
				String tokenid = mSharedPreferences.getString(Constant.TOKEN_ID, "");
				String saleId = mSharedPreferences.getString(Constant.ID, "");
				if (mWaitDialog == null) {
					mWaitDialog = new WaitDialog(this, R.string.loading_data);
				}
				mWaitDialog.show();
				NetProxyManager.getInstance().toUploadSign(mainHandler, tokenid, cropImageUri);
				return;
			}
			if (data == null) {
				return;
			}
			avaterBitmap = data.getParcelableExtra("data");
			if (saveSignBitmap(avaterBitmap)) {
				String tokenid = mSharedPreferences.getString(Constant.TOKEN_ID, "");
				String saleId = mSharedPreferences.getString(Constant.ID, "");
				if (mWaitDialog == null) {
					mWaitDialog = new WaitDialog(this, R.string.loading_data);
				}
				mWaitDialog.show();
				NetProxyManager.getInstance().toUploadSign(mainHandler, tokenid, AVATER_PATH + "userSign.png");
			} else {
				ToastUtil.showShort(this, "设置签名失败");
			}
			break;
		default:
			break;
		}
	}

	private void updatePersonInfo(String phone, String email, String name, String cpmpanyStr, String birthday,
			String job, String businessAddress, String companyAddress) {
		String tokenid = mSharedPreferences.getString(Constant.TOKEN_ID, "");
		String saleId = mSharedPreferences.getString(Constant.ID, "");
		mWaitDialog = new WaitDialog(this, R.string.loading_press);
		mWaitDialog.show();
		NetProxyManager.getInstance().toUpdatePersonInfo(mainHandler, tokenid, null, id, phone, email, name, cpmpanyStr,
				birthday, job, businessAddress, companyAddress, null, null);
	}

	private void updatePersonInfo() {
		updatePersonInfo(phoneCall, mail, userName, company, age, profession, office, company_address);
	}

	private boolean saveBitmap(Bitmap bitmap) {
		File f = new File(AVATER_PATH, "userAvater.png");
		if (f.exists()) {
			f.delete();
		}
		try {
			FileOutputStream out = new FileOutputStream(f);
			bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	private boolean saveSignBitmap(Bitmap bitmap) {
		File f = new File(AVATER_PATH, "userSign.png");
		if (f.exists()) {
			f.delete();
		}
		try {
			FileOutputStream out = new FileOutputStream(f);
			bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * to parse person infomations from network
	 */
	private void parseGetPersonInfo(String result) {
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
			LogUtils.d("Json: " + json);
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
			id = obj.getString("id");
			userName = obj.getString("name");
			tv_username.setText(userName);
			if (obj.has("phone")) {
				phoneCall = obj.getString("phone");
				tv_phone_call.setText(phoneCall);
			}
			if (obj.has("gender")) {
				tv_genger.setText(obj.getString("gender").equals("0") ? "女" : "男");
			}
			if (obj.has("email")) {
				mail = obj.getString("email");
				tv_mail.setText(mail);
			}
			if (obj.has("company")) {
				company = obj.getString("company");
				tv_company.setText(company);
			}
			// if (obj.has("age")) {
			// age = obj.getString("age");
			// tv_age.setText(age);
			// }
			if (obj.has("birthday")) {
				age = obj.getString("birthday");
				tv_age.setText(age);
			}
			if (obj.has("job")) {
				profession = obj.getString("job");
				tv_profession.setText(profession);
			}
			if (obj.has("businessAddress")) {
				office = obj.getString("businessAddress");
				tv_office.setText(office);
			}
			if (obj.has("companyAddress")) {
				company_address = obj.getString("companyAddress");
				tv_company_address.setText(company_address);
			}
			if (obj.has("validEndTime") && obj.getString("validEndTime") != null) {
				tv_renewal.setText("有效期至" + DateUtil.formatDateStr(obj.getString("validEndTime")));
			} else {
				if (obj.has("validPeriod") && obj.getString("validPeriod") != null) {
					tv_renewal.setText("有效期至" + DateUtil.stringToStr(obj.getString("validPeriod")));
				}
			}
			if (obj.has("avatar")) {
				ImageManager.loadImage(obj.getString("avatar"), user_avater, R.drawable.avater_login);
			}
			if (obj.has("qgraph")) {
				ImageManager.loadImage(obj.getString("qgraph"), q_iv, R.drawable.default_q_icon);
			}
			if (obj.has("signature")) {
				ImageManager.loadDefaultImage(obj.getString("signature"), iv_signature);
			}
			if (obj.has("signaturebasemap")) {
				ImageManager.loadDefaultImage(obj.getString("signaturebasemap"), iv_signature_base	);
			}
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
				String tokenid = mSharedPreferences.getString(Constant.TOKEN_ID, "");
				String saleId = mSharedPreferences.getString(Constant.ID, "");
				NetProxyManager.getInstance().toUpdatePersonInfo(mainHandler, tokenid, null, id, null, null, null, null,
						null, null, null, null, jsonArray.getString(0), null);
				ImageManager.loadImage(jsonArray.getString(0), user_avater, R.drawable.avater_login);
			}
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
	 * to parse upload avater from network
	 */
	private void parseUpdatePersionInfo(String result) {
		if (Errors.ERROR_NET.equals(result) || Errors.ERROR_SERVER.equals(result)) {
			ToastUtil.showLong(this, R.string.network_error);
			if (mWaitDialog != null && mWaitDialog.isShowing()) {
				mWaitDialog.dismiss();
			}
			return;
		}
		// to parser json data
		try {
			LogUtils.d("result: " + result);
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
			tv_username.setText(userName);
			tv_phone_call.setText(phoneCall);
			tv_mail.setText(mail);
			tv_company.setText(company);
			tv_age.setText(age);
			tv_profession.setText(profession);
			tv_office.setText(office);
			tv_company_address.setText(company_address);
		} catch (Exception ex) {
			LogUtils.e(ex.getMessage());
			return;
		}
	}

	public void qGraphSelect(View view) {
		Intent intent = new Intent(PersonalCenterActivity.this, ChooseImageActivity.class);
		intent.putExtra("radio", true);
		startActivityForResult(intent, CHOOSE_Q_IMAGE);
	}

	/**
	 * to parse upload avater from network
	 */
	private void parseUploadQGraphInfo(String result) {
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
				String tokenid = mSharedPreferences.getString(Constant.TOKEN_ID, "");
				String saleId = mSharedPreferences.getString(Constant.ID, "");
				NetProxyManager.getInstance().toUpdatePersonInfo(mainHandler, tokenid, null, id, null, null, null, null,
						null, null, null, null, null, jsonArray.getString(0));
				ImageManager.loadImage(jsonArray.getString(0), q_iv, R.drawable.default_q_icon);
			}
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
	 * to parse upload avater from network
	 */
	private void parseUploadSignInfo(String result) {
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
				String tokenid = mSharedPreferences.getString(Constant.TOKEN_ID, "");
				String saleId = mSharedPreferences.getString(Constant.ID, "");
				NetProxyManager.getInstance().toUpdatePersonSignInfo(mainHandler, tokenid, null, 
						id, jsonArray.getString(0));
				ImageManager.loadDefaultImage(jsonArray.getString(0), iv_signature);
			}
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
	protected void onResume() {
		super.onResume();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (mPermissionsChecker.lacksPermissions(PERMISSIONS)) {
				startPermissionsActivity();
			}
		}
	}
}
