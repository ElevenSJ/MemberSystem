package com.lyp.membersystem.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.lyp.membersystem.LoginActivity;
import com.lyp.membersystem.R;
import com.lyp.membersystem.base.BaseActivity;
import com.lyp.membersystem.base.BaseApplication;
import com.lyp.membersystem.imagepicker.ChooseImageActivity;
import com.lyp.membersystem.log.LogUtils;
import com.lyp.membersystem.net.Errors;
import com.lyp.membersystem.net.MessageContants;
import com.lyp.membersystem.net.NetProxyManager;
import com.lyp.membersystem.ui.permission.PermissionsActivity;
import com.lyp.membersystem.utils.Constant;
import com.lyp.membersystem.utils.PermissionsChecker;
import com.lyp.membersystem.utils.PhotoUtil;
import com.lyp.membersystem.utils.SystemStatusManager;
import com.lyp.membersystem.utils.ToastUtil;
import com.lyp.membersystem.view.CustomPopupWindow;
import com.lyp.membersystem.view.dialog.WaitDialog;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

public class SettingsActivity extends BaseActivity {
	
	private static final int TAKE_PHOTO_BUSINESS_CARD = 1004;
	private static final int CHOOSE_IMAGE_BUSINESS_CARD = 1005;
	private static final int CROP_PHOTO_BUSINESS_CARD = 1006;
	private static final int TAKE_PHOTO_SIGN = 1007;
	private static final int CHOOSE_IMAGE_SIGN = 1008;
	private static final int CROP_PHOTO_SIGN = 1009;
	private static final int CHOOSE_IMAGE_Q = 1010;
	private static final String AVATER_PATH = Environment.getExternalStorageDirectory() + "/membersystem/";
	private WaitDialog mWaitDialog;
	private SharedPreferences mSharedPreferences;
	private String id;
	private PermissionsChecker mPermissionsChecker; // 权限检测器
	private static final int REQUEST_PERMISSION = 1006; // 权限请求
	static final String[] PERMISSIONS = new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE,
			Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA };
	private PhotoUtil photoUtil;
	private CustomPopupWindow mPopWin;
	private Bitmap avaterBitmap = null;
	private Uri imageUri;
	private String cropImageUri;

	private Handler mainHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MessageContants.MSG_UPLOAD_SIGN_BASELINE: {
				parseUploadSignMap((String) msg.obj);
				break;
			}
			case MessageContants.MSG_UPLOAD_Q_BASE_URL: {
				parseUploadSignInfo((String) msg.obj);
				break;
			}
			case MessageContants.MSG_UPLOAD_Q_BASELINE: {
				parseUploadQMap((String) msg.obj);
				break;
			}
			case MessageContants.MSG_UPLOAD_Qgraph_BASE_URL: {
				parseUploadQInfo((String) msg.obj);
				break;
			}
			case MessageContants.MSG_UPLOAD_BUSINESS_CARD: {
				parseUploadBusinessCardMap((String) msg.obj);
				break;
			}
			case MessageContants.MSG_UPLOAD_BUSINESS_CARD_URL: {
				parseUploadBusinessCardInfo((String) msg.obj);
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
		setContentView(R.layout.setting_layout);
		initView();
//		initData();
	}

	private void initView() {
		mPermissionsChecker = new PermissionsChecker(this);
		photoUtil = new PhotoUtil(this);
		mSharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCE, MODE_PRIVATE);
	}

	private void initData() {
//		String tokenid = mSharedPreferences.getString(Constant.TOKEN_ID, "");
		String saleId = mSharedPreferences.getString(Constant.ID, "");
		if (mWaitDialog == null) {
			mWaitDialog = new WaitDialog(this, R.string.loading_data);
		}
		mWaitDialog.show();
//		NetProxyManager.getInstance().toGetPersonInfo(mainHandler, saleId);
	}


	public void onBack(View view) {
		finish();
	}

	public void exitApp(View view) {
		BaseApplication.getInstance().finishAllActivity();
		mSharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCE, MODE_PRIVATE);
		Editor editor = mSharedPreferences.edit();
		editor.clear().commit();
		Intent i = new Intent();
		i.setClass(this, LoginActivity.class);
		startActivity(i);
		finish();
	}
	
	public void upLoadSignBasemap(View view) {
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
				Intent intent = new Intent(SettingsActivity.this, ChooseImageActivity.class);
				intent.putExtra("radio", true);
				startActivityForResult(intent, CHOOSE_IMAGE_SIGN);
			}
		});
	}
	
	public void upLoadQBasemap(View view) {
		Intent intent = new Intent(SettingsActivity.this, ChooseImageActivity.class);
		intent.putExtra("radio", true);
		startActivityForResult(intent, CHOOSE_IMAGE_Q);
	}
	
	public void upLoadBusinessCard(View view) {
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
						imageUri = photoUtil.takePhoto(TAKE_PHOTO_BUSINESS_CARD);
					}
				} else {
					imageUri = photoUtil.takePhoto(TAKE_PHOTO_BUSINESS_CARD);
				}
			}
		});

		inflate.findViewById(R.id.select_from_gallery).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mPopWin.dismiss();
				Intent intent = new Intent(SettingsActivity.this, ChooseImageActivity.class);
				intent.putExtra("radio", true);
				startActivityForResult(intent, CHOOSE_IMAGE_BUSINESS_CARD);
			}
		});
	}
	
	
	private void startPermissionsActivity() {
		PermissionsActivity.startActivityForResult(this, REQUEST_PERMISSION, PERMISSIONS);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
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
				ToastUtil.showShort(this, "设置签名底图失败");
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
				NetProxyManager.getInstance().toUploadSignBaseline(mainHandler, tokenid, cropImageUri);
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
				NetProxyManager.getInstance().toUploadSignBaseline(mainHandler, tokenid, AVATER_PATH + "userSignBase.png");
			} else {
				ToastUtil.showShort(this, "设置签名失败");
			}
			break;
		case CHOOSE_IMAGE_Q:
			if (resultCode != RESULT_OK) {
				return;
			}
			ArrayList<String> qImages = data.getStringArrayListExtra("choose_images");
			if (qImages.size() <= 0) {
				ToastUtil.showShort(this, "设置Q底图失败！");
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
			NetProxyManager.getInstance().toUploadQBaseline(mainHandler, token_id, q_path);
			break;
			
		case TAKE_PHOTO_BUSINESS_CARD:
			if (imageUri != null) {
				cropImageUri = photoUtil.startBusinessCardPhotoZoom(CROP_PHOTO_BUSINESS_CARD, imageUri);
			}
			break;
		case CHOOSE_IMAGE_BUSINESS_CARD:
			if (resultCode != RESULT_OK) {
				return;
			}
			ArrayList<String> imagesBC = data.getStringArrayListExtra("choose_images");
			if (imagesBC.size() <= 0) {
				ToastUtil.showShort(this, "设置签名底图失败");
				return;
			}
			LogUtils.d("chooseImage: " + imagesBC.get(0));
			cropImageUri = photoUtil.startBusinessCardPhotoZoom(CROP_PHOTO_BUSINESS_CARD, imagesBC.get(0));
			break;
		case CROP_PHOTO_BUSINESS_CARD:
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
				NetProxyManager.getInstance().toUploadBusinessCard(mainHandler, tokenid, cropImageUri);
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
				NetProxyManager.getInstance().toUploadSignBaseline(mainHandler, tokenid, AVATER_PATH + "userSignBase.png");
			} else {
				ToastUtil.showShort(this, "设置名片失败");
			}
			break;
			default :
				break;
		}
	}
	
	private boolean saveSignBitmap(Bitmap bitmap) {
		File f = new File(AVATER_PATH, "userSignBase.png");
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
	
	private void parseUploadSignMap(String result) {
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
				NetProxyManager.getInstance().toUpdateQBaseMap(mainHandler, tokenid,
						jsonArray.getString(0));
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
	
	private void parseUploadQMap(String result) {
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
				NetProxyManager.getInstance().toUpdateQgraphBaseMap(mainHandler, tokenid,
						jsonArray.getString(0));
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
	
	private void parseUploadBusinessCardMap(String result) {
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
				NetProxyManager.getInstance().toUpdateBusinessCard(mainHandler, tokenid,
						jsonArray.getString(0));
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
			ToastUtil.showMessage("上传签名底图成功！");
			finish();
		} catch (Exception ex) {
			LogUtils.e(ex.getMessage());
			return;
		} finally {
			if (mWaitDialog != null && mWaitDialog.isShowing()) {
				mWaitDialog.dismiss();
			}
		}
	}
	
	private void parseUploadQInfo(String result) {
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
			ToastUtil.showMessage("上传q底图成功！");
			finish();
		} catch (Exception ex) {
			LogUtils.e(ex.getMessage());
			return;
		} finally {
			if (mWaitDialog != null && mWaitDialog.isShowing()) {
				mWaitDialog.dismiss();
			}
		}
	}
	
	private void parseUploadBusinessCardInfo(String result) {
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
			ToastUtil.showMessage("上传名片成功！");
			finish();
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
	}
}
