package com.lyp.membersystem.ui;

import org.json.JSONArray;
import org.json.JSONObject;

import com.lyp.membersystem.R;
import com.lyp.membersystem.base.BaseActivity;
import com.lyp.membersystem.log.LogUtils;
import com.lyp.membersystem.net.API;
import com.lyp.membersystem.net.Errors;
import com.lyp.membersystem.net.MessageContants;
import com.lyp.membersystem.utils.Constant;
import com.lyp.membersystem.utils.SystemStatusManager;
import com.lyp.membersystem.utils.ToastUtil;
import com.lyp.membersystem.view.dialog.WaitDialog;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class UserAgreementActivity extends BaseActivity {
	/** 资源对象 */
	public Resources res;
	private WaitDialog mWaitDialog;
	private WebView webView;
	private Handler mainHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MessageContants.MSG_GET_USER_AGREEMENT: {
				parseUserAgreement((String) msg.obj);
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
		setContentView(R.layout.user_agreement_layout);
		webView = (WebView) findViewById(R.id.webView);
		WebSettings webSettings = webView.getSettings();  
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
        webView.setWebChromeClient(wvcc);
        WebViewClient wvc = new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
            	webView.loadUrl(url);
                return true;
            }
        };
        webView.setWebViewClient(wvc);
//		if (mWaitDialog == null) {
//			mWaitDialog = new WaitDialog(this, R.string.loading_data);
//		}
//		mWaitDialog.show();
//		NetProxyManager.getInstance().toGetUserAgreement(mainHandler, tokenid);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		SharedPreferences mSharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCE, MODE_PRIVATE);
		String tokenid = mSharedPreferences.getString(Constant.TOKEN_ID, "");
		String url = API.API_GET_USER_AGREEMENT + "?token_id=" + tokenid;
//		String url = "http://forvips.cn/api/app/v1/getUserAgreement" + "?token_id=" + tokenid;
//		webView.getSettings().setDomStorageEnabled(true);
//		webView.getSettings().setJavaScriptEnabled(true);
//		webView.addJavascriptInterface(new JsObject(), "injectedObject");  
//		webView.loadData("", "text/html", null);
//		webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
//		webView.setWebViewClient(new WebViewClient());
		webView.loadUrl(url);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		webView.getSettings().setJavaScriptEnabled(false);
		webView.clearCache(true);
	}

	public void onBack(View view) {
		finish();
	}
	
	/**
	 * to parse login infomations from network
	 */
	private void parseUserAgreement(String result) {
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
			
//			webView.loadUrl(url);
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

}
