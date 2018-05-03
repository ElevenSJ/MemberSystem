package com.lyp.membersystem.ui;

import org.json.JSONObject;

import com.lyp.membersystem.R;
import com.lyp.membersystem.base.BaseActivity;
import com.lyp.membersystem.base.BaseApplication;
import com.lyp.membersystem.log.LogUtils;
import com.lyp.membersystem.manager.ImageManager;
import com.lyp.membersystem.net.API;
import com.lyp.membersystem.net.Errors;
import com.lyp.membersystem.net.MessageContants;
import com.lyp.membersystem.pay.PayActivity;
import com.lyp.membersystem.utils.Constant;
import com.lyp.membersystem.utils.SystemStatusManager;
import com.lyp.membersystem.utils.ToastUtil;
import com.lyp.membersystem.view.dialog.WaitDialog;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

public class ServiceDetailActivity extends BaseActivity {

	private static final int DELAY_TIME = 4000;
	private String id;
	private WaitDialog mWaitDialog;
	private SharedPreferences mSharedPreferences;
	private TextView service_name;
	private ImageView img_iv;
	private String name;
	private String icon;
	private WebView webview;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MessageContants.MSG_GET_GOOD_INFO: {
				parseGetGoodInfo((String) msg.obj);
				break;
			}
			case MessageContants.MSG_ADD_SERVICE_ORDER: {
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
		setTranslucentStatus();
		id = getIntent().getStringExtra("id");
		name = getIntent().getStringExtra("name");
		icon = getIntent().getStringExtra("icon");
		setContentView(R.layout.service_deatil_layout);
		initView();
		initData();
	}

	private void initView() {
		service_name = (TextView) findViewById(R.id.service_name);
		service_name.setText(name);
		img_iv = (ImageView) findViewById(R.id.img_iv);
		ImageManager.loadImage(icon, img_iv, R.drawable.default_image);
		webview = (WebView) findViewById(R.id.webview);
		WebSettings webSettings = webview.getSettings();  
		webSettings.setJavaScriptEnabled(true);
        webSettings.setUseWideViewPort(true); // 关键点
        webSettings.setAllowFileAccess(true); // 允许访问文件
        webSettings.setSupportZoom(true); // 支持缩放
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setPluginState(WebSettings.PluginState.ON);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE); // 不加载缓存内容
      //设置默认的字符编码
        webSettings.setDefaultTextEncodingName("utf-8");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        WebChromeClient wvcc = new WebChromeClient();
        webview.setWebChromeClient(wvcc);
        WebViewClient wvc = new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
            	webview.loadUrl(url);
                return true;
            }
        };
        webview.setWebViewClient(wvc);
        
	}

	private void initData() {
		mSharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCE, MODE_PRIVATE);
		String tokenid = mSharedPreferences.getString(Constant.TOKEN_ID, "");
		String saleId = mSharedPreferences.getString(Constant.ID, "");
//		if (mWaitDialog == null) {
//			mWaitDialog = new WaitDialog(this, R.string.loading_data);
//		}
//		mWaitDialog.show();
//		NetProxyManager.getInstance().toGetGoodInfo(mHandler, tokenid, id);
		String uri = API.API_GET_SERVICE_INTRODUCE + "?token_id=" + tokenid + "&id=" + id;
		webview.loadUrl(uri);
	}


	public void applyBtn(View view) {
		Intent i = new Intent();
		i.setClass(this, ServiceResultActivity.class);
		i.putExtra("id", id);
		i.putExtra("name", name);
		startActivity(i);
	}

	@Override
	public void onResume() {
		super.onResume();
		webview.resumeTimers();
		webview.onResume();
	}

	@Override
	public void onPause() {
		webview.onPause();
		webview.pauseTimers();
//		webview.reload();
		super.onPause();
	}
	
	@Override
	protected void onDestroy() {
		webview.destroy();
		webview = null;
		super.onDestroy();
	}

	/**
	 * to parse login infomations from network
	 */
	private void parseGetGoodInfo(String result) {
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
			JSONObject job = json.getJSONObject("object");
		} catch (Exception ex) {
			LogUtils.e(ex.getMessage());
			return;
		}
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
			LogUtils.d("" + result);
			if (result == null) {
				ToastUtil.showShort(this, "服务器出错啦！");
			}
			JSONObject json = new JSONObject(result);
			boolean success = json.getBoolean("success");
			if (mWaitDialog != null && mWaitDialog.isShowing()) {
				mWaitDialog.dismiss();
			}
			LogUtils.d("" + json);
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
			i.putExtra("isService", true);
			startActivity(i);
			BaseApplication.getInstance().finishSpecialPathActivity();
		} catch (Exception ex) {
			LogUtils.e(ex.getMessage());
			return;
		}
	}

	public void onBack(View view) {
		finish();
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
}
