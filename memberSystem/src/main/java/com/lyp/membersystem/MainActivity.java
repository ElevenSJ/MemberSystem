package com.lyp.membersystem;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.lyp.membersystem.base.BaseActivity;
import com.lyp.membersystem.database.CustomerDao;
import com.lyp.membersystem.log.LogUtils;
import com.lyp.membersystem.net.Errors;
import com.lyp.membersystem.net.MessageContants;
import com.lyp.membersystem.net.NetProxyManager;
import com.lyp.membersystem.service.NoticeMgrService;
import com.lyp.membersystem.ui.MallFragmentActivity;
import com.lyp.membersystem.ui.MyActivityActivity;
import com.lyp.membersystem.ui.MyCustomerActivity;
import com.lyp.membersystem.ui.NoticeActivity;
import com.lyp.membersystem.ui.OrderActivity;
import com.lyp.membersystem.ui.PersonalCenterActivity;
import com.lyp.membersystem.ui.ServiceActivity;
import com.lyp.membersystem.ui.ServiceCustomerActivity;
import com.lyp.membersystem.ui.permission.PermissionsActivity;
import com.lyp.membersystem.utils.Constant;
import com.lyp.membersystem.utils.DateUtil;
import com.lyp.membersystem.utils.PermissionsChecker;
import com.lyp.membersystem.utils.SystemStatusManager;
import com.lyp.membersystem.utils.ToastUtil;
import com.lyp.membersystem.view.contactsort.ContactSortModel;
import com.lyp.membersystem.view.contactsort.PinyinComparator;
import com.lyp.membersystem.view.contactsort.PinyinUtils;
import com.lyp.membersystem.view.dialog.WaitDialog;
import com.yuntongxun.ecdemo.common.CCPAppManager;
import com.yuntongxun.ecdemo.common.ECContentObservers;
import com.yuntongxun.ecdemo.common.dialog.ECProgressDialog;
import com.yuntongxun.ecdemo.common.utils.FileAccessor;
import com.yuntongxun.ecdemo.common.utils.PermissionUtils;
import com.yuntongxun.ecdemo.core.ClientUser;
import com.yuntongxun.ecdemo.ui.SDKCoreHelper;
import com.yuntongxun.ecdemo.ui.chatting.ChattingActivity;
import com.yuntongxun.ecdemo.ui.chatting.ChattingFragment;
import com.yuntongxun.ecsdk.ECDevice;
import com.yuntongxun.ecsdk.ECInitParams;
import com.yuntongxun.ecsdk.SdkErrorCode;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

public class MainActivity extends BaseActivity {

	private SharedPreferences mSharedPreferences;
	private List<ContactSortModel> mCustomerList;
	private CustomerDao mCustomerDao;
	private WaitDialog mWaitDialog;
	private ECProgressDialog mPostingdialog;
	private InternalReceiver internalReceiver;
	private PermissionsChecker mPermissionsChecker; // 权限检测器
	private static final int REQUEST_PERMISSION = 1006; // 权限请求
	private String kefuId;//客服ID
	private RelativeLayout new_guide_rl;
	

	// Internal calss.
	private class InternalReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent == null || intent.getAction() == null) {
				return;
			}
			handleReceiver(context, intent);
		}
	}

	protected void handleReceiver(Context context, Intent intent) {
		// super.handleReceiver(context, intent);
		int error = intent.getIntExtra("error", -1);
		if (SDKCoreHelper.ACTION_SDK_CONNECT.equals(intent.getAction())) {
			// 初始注册结果，成功或者失败
			if (SDKCoreHelper.getConnectState() == ECDevice.ECConnectState.CONNECT_SUCCESS
					&& error == SdkErrorCode.REQUEST_SUCCESS) {

//				dismissPostingDialog();
//				try {
//					saveAccount();
//				} catch (InvalidClassException e) {
//					e.printStackTrace();
//				}
//				ContactsCache.getInstance().load();
				if (TextUtils.isEmpty(kefuId)) {
				    getKefuId();
				} else {
					openKefu(kefuId);
				}
				return;
			}
			if (intent.hasExtra("error")) {
				if (SdkErrorCode.CONNECTTING == error) {
					return;
				}
				if (error == -1) {
					ToastUtil.showMessage("请检查登陆参数是否正确[" + error + "]");
				} else {
					dismissPostingDialog();
				}
				ToastUtil.showMessage("登录失败，请稍后重试[" + error + "]");
			}
			dismissPostingDialog();
		}

	}

	protected final void registerReceiver(String[] actionArray) {
		if (actionArray == null) {
			return;
		}
		IntentFilter intentfilter = new IntentFilter();
		intentfilter.addAction(SDKCoreHelper.ACTION_KICK_OFF);
		for (String action : actionArray) {
			intentfilter.addAction(action);
		}
		if (internalReceiver == null) {
			internalReceiver = new InternalReceiver();
		}
		registerReceiver(internalReceiver, intentfilter);
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MessageContants.MSG_GET_CUSTOMER_LIST: {
				parseCustomerList((String) msg.obj);
				break;
			}
			case MessageContants.MSG_GET_CURRENT_AGNET: {
				parseKefuId((String) msg.obj);
				break;
			}
			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Intent ecInitIntent =new Intent();
		ecInitIntent.setAction("com.yuntongxun.ecdemo.inited");
		sendBroadcast(ecInitIntent);
		super.onCreate(savedInstanceState);
		kefuId = getIntent().getStringExtra("Main_Session");
		setTranslucentStatus();
		setContentView(R.layout.main_layout);
		mSharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCE, MODE_PRIVATE);
		boolean IS_GUIDE = mSharedPreferences.getBoolean(Constant.IS_GUIDE, true);
		new_guide_rl = (RelativeLayout)findViewById(R.id.new_guide_rl);
		if (IS_GUIDE) {
			new_guide_rl.setVisibility(View.VISIBLE);
		} else {
			new_guide_rl.setVisibility(View.GONE);
		}
		mPermissionsChecker = new PermissionsChecker(this);
		mCustomerDao = new CustomerDao(this.getApplicationContext());
		mCustomerList = mCustomerDao.getContactList();
		if (mCustomerList == null)
			mCustomerList = new ArrayList<ContactSortModel>();
		mSharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCE, MODE_PRIVATE);
		String tokenid = mSharedPreferences.getString(Constant.TOKEN_ID, "");
		String saleId = mSharedPreferences.getString(Constant.ID, "");
		if (mWaitDialog == null) {
			mWaitDialog = new WaitDialog(this, R.string.loading_data);
		}
		if (TextUtils.isEmpty(kefuId)) {
		    mWaitDialog.show();
	    }
		NetProxyManager.getInstance().toGetCustomerList(mHandler, tokenid, saleId, null, null, 0);
		Intent intentService = new Intent(this, NoticeMgrService.class);
		startService(intentService);
		PermissionUtils.requestMultiPermissions(this, mPermissionGrant);
		registerReceiver(new String[] { SDKCoreHelper.ACTION_SDK_CONNECT, SDKCoreHelper.ACTION_KICK_OFF });
		ECContentObservers.getInstance().initContentObserver();
		if (!TextUtils.isEmpty(kefuId)) {
			connectKefu();
		}
	}

	public void searchContent(View view) {
		ToastUtil.showShort(this, "searchContent");
	}

	public void myCustomer(View view) {
		Intent intent = new Intent();
		intent.setClass(this, MyCustomerActivity.class);
		startActivity(intent);
	}

	public void notice(View view) {
		Intent intent = new Intent();
		intent.setClass(this, NoticeActivity.class);
		startActivity(intent);
	}

	public void orderService(View view) {
		Intent i = new Intent();
		i.setClass(this, ServiceActivity.class);
		startActivity(i);
	}
	
	public void myActivity(View view) {
		Intent intent = new Intent();
//		intent.setClass(this, MyActivityActivity.class);
		intent.setClass(this, ServiceCustomerActivity.class);
		startActivity(intent);
	}

	public void mall(View view) {
		Intent intent = new Intent();
		intent.setClass(this, MallFragmentActivity.class);
		startActivity(intent);
	}

//	public void queryOrder(View view) {
//		Intent intent = new Intent();
//		intent.setClass(this, OrderActivity.class);
//		startActivity(intent);
//	}

	public void customerServer(View view) {
		connectKefu();
	}
	
	private void connectKefu() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (mPermissionsChecker.lacksPermissions(PermissionUtils.requestPermissions)) {
				startPermissionsActivity();
				return;
			}
		}
//		 Intent intent = new Intent();
//		 intent.setClass(this, LauncherActivity.class);
//		 startActivity(intent);

		String appkey = FileAccessor.getAppKey();
		String token = FileAccessor.getAppToken();
		LogUtils.d("lyp", "appkey: " + appkey);
		LogUtils.d("lyp", "token: " + token);
		mSharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCE, MODE_PRIVATE);
		String tokenid = mSharedPreferences.getString(Constant.TOKEN_ID, "");
		String saleId = mSharedPreferences.getString(Constant.ID, "");
		LogUtils.d("lyp", "saleId: " + saleId);
		ClientUser clientUser = new ClientUser(saleId);
		clientUser.setAppKey(appkey);
		clientUser.setAppToken(token);
		clientUser.setLoginAuthType(ECInitParams.LoginAuthType.NORMAL_AUTH);
		// clientUser.setPassword(pass);
		CCPAppManager.setClientUser(clientUser);

		// if(!PatternUtils.isShuZiYing(mobile)){
		// ToastUtil.showMessage("输入的账号不合法");
		// return;
		// }
		mPostingdialog = new ECProgressDialog(this, R.string.opening_kf);
		mPostingdialog.show();

		SDKCoreHelper.init(this, ECInitParams.LoginMode.FORCE_LOGIN);
	}

	public void personalCenter(View view) {
		Intent intent = new Intent();
		intent.setClass(this, PersonalCenterActivity.class);
		startActivity(intent);
	}
	
	private void startPermissionsActivity() {
		PermissionsActivity.startActivityForResult(this, REQUEST_PERMISSION, PermissionUtils.requestPermissions);
	}
	
	private void getKefuId() {
		mSharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCE, MODE_PRIVATE);
		String tokenid = mSharedPreferences.getString(Constant.TOKEN_ID, "");
		NetProxyManager.getInstance().toGetCurrentAgent(mHandler, tokenid);
	}
	
	private void openKefu(String id) {
		try {
			Intent intent = new Intent(this , ChattingActivity.class);
            intent.putExtra(ChattingFragment.RECIPIENTS, id);
            LogUtils.d("lyp", "id: " + id);
            intent.putExtra(ChattingFragment.CONTACT_USER, "客服");
//			intent.putExtra(ChattingFragment.RECIPIENTS, ContactLogic.CUSTOM_SERVICE);
            startActivity(intent);
            dismissPostingDialog();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void knowNewGuide(View view){
		new_guide_rl.setVisibility(View.GONE);
		Editor editor = mSharedPreferences.edit();
		editor.putBoolean(Constant.IS_GUIDE, false);
		editor.commit();
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
		tintManager.setStatusBarTintResource(android.R.color.white);// 状态栏无背景
		getWindow().getDecorView().setFitsSystemWindows(true);
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
			contactsSort();
			mCustomerDao.saveContactList(mCustomerList);
		} catch (Exception ex) {
			LogUtils.e(ex.getMessage());
			ToastUtil.showMessage("请检查服务器数据是否正确！");
			return;
		} finally {
			if (mWaitDialog != null && mWaitDialog.isShowing()) {
				mWaitDialog.dismiss();
			}
		}
	}
	
	private void parseKefuId(String result) {
		if (Errors.ERROR_NET.equals(result) || Errors.ERROR_SERVER.equals(result)) {
			ToastUtil.showLong(this, R.string.network_error);
			dismissPostingDialog();
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
			JSONArray jsonArray = json.getJSONArray("object");
			if (jsonArray.length() > 0) {
				openKefu(jsonArray.getString(0));
			}
		} catch (Exception ex) {
			LogUtils.e(ex.getMessage());
			return;
		}
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
			LogUtils.d("lyp", "Sort contacts: " + sortString);
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
	}

	@Override
	protected void onResume() {
		super.onResume();
//		Intent ecInitIntent =new Intent();
//		ecInitIntent.setAction("com.yuntongxun.ecdemo.inited");
//		sendBroadcast(ecInitIntent);
		// if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
		// // 判断是否有WRITE_SETTINGS权限if(!Settings.System.canWrite(this)) {
		// // 申请WRITE_SETTINGS权限
		// Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS,
		// Uri.parse("package:" + getPackageName()));
		// startActivityForResult(intent, 0x1314);
		// }

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// if (requestCode == 0x1314) {
		// if (!Settings.System.canWrite(this)) {
		// Toast.makeText(this, "权限授予失败，", Toast.LENGTH_SHORT).show();
		// }
		// }
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
	}
	
	private PermissionUtils.PermissionGrant mPermissionGrant = new PermissionUtils.PermissionGrant() {
		@Override
		public void onPermissionGranted(int requestCode) {
			switch (requestCode) {
				case PermissionUtils.CODE_MULTI_PERMISSION:
					break;
				default:
					break;
			}
		}
	};
	
	/**
	 * 关闭对话框
	 */
	private void dismissPostingDialog() {
		if (mPostingdialog == null || !mPostingdialog.isShowing()) {
			return;
		}
		mPostingdialog.dismiss();
		mPostingdialog = null;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			unregisterReceiver(internalReceiver);
		} catch (Exception e) {
		}
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		kefuId = getIntent().getStringExtra("Main_Session");
		if (!TextUtils.isEmpty(kefuId)) {
			connectKefu();
		}
	}
}
