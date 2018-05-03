package com.lyp.membersystem.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.lyp.membersystem.R;
import com.lyp.membersystem.adapter.ImageAdapter;
import com.lyp.membersystem.base.BaseActivity;
import com.lyp.membersystem.base.BaseApplication;
import com.lyp.membersystem.bean.ImagePageBean;
import com.lyp.membersystem.bean.TagBean;
import com.lyp.membersystem.log.LogUtils;
import com.lyp.membersystem.net.API;
import com.lyp.membersystem.net.Errors;
import com.lyp.membersystem.net.MessageContants;
import com.lyp.membersystem.net.NetProxyManager;
import com.lyp.membersystem.pay.PayActivity;
import com.lyp.membersystem.utils.Constant;
import com.lyp.membersystem.utils.SystemStatusManager;
import com.lyp.membersystem.utils.ToastUtil;
import com.lyp.membersystem.view.dialog.GoodStyleDialog;
import com.lyp.membersystem.view.dialog.GoodStyleDialog.OnButtonClickListener;
import com.lyp.membersystem.view.dialog.WaitDialog;

import android.app.Activity;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GoodDetailFromNoticeActivity extends BaseActivity {
	private static final int DELAY_TIME = 4000;
	private String id;
	private WaitDialog mWaitDialog;
	private SharedPreferences mSharedPreferences;
	private ImageAdapter mImageAdapter;
	private TextView good_name;
	private String name;
	private TextView good_price;
	private String price;
	private Gallery img_gallery;
	private ImageView mGuideOne;
	private ImageView mGuideTwo;
	private ImageView mGuideThree;
	private int mIndicatorSelected = R.drawable.page_indicator_focused;
	private int mIndicatorUnSelected = R.drawable.page_indicator_unfocused;
	private WebView webview;
	private String isService = "0";
	private String tags;
	private LinearLayout tagLayout;
	private TextView tag;
	private String selectTag;
	private Map<String, List<TagBean>> tagList;
	private String customTexts;
	private String customerIds;

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
		isService = getIntent().getStringExtra("isService");
		customTexts = getIntent().getStringExtra("customTexts");
		customerIds = getIntent().getStringExtra("customerIds");
		setContentView(R.layout.good_deatil_layout);
		initView();
		initData();
	}

	private void initView() {
		good_name = (TextView) findViewById(R.id.good_name);
		good_price = (TextView) findViewById(R.id.good_price);
		img_gallery = (Gallery) findViewById(R.id.img_gallery);
		mImageAdapter = new ImageAdapter(this);
		img_gallery.setAdapter(mImageAdapter);
		mGuideOne = (ImageView) findViewById(R.id.guide_one);
		mGuideTwo = (ImageView) findViewById(R.id.guide_two);
		mGuideThree = (ImageView) findViewById(R.id.guide_three);
		img_gallery.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				setGuide(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});
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
		tagLayout = (LinearLayout) findViewById(R.id.tagLayout);
		tag = (TextView) findViewById(R.id.tag);
	}

	private void initData() {
		tagList = new HashMap<String, List<TagBean>>();
		mSharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCE, MODE_PRIVATE);
		String tokenid = mSharedPreferences.getString(Constant.TOKEN_ID, "");
		String saleId = mSharedPreferences.getString(Constant.ID, "");
		if (mWaitDialog == null) {
			mWaitDialog = new WaitDialog(this, R.string.loading_data);
		}
		mWaitDialog.show();
		NetProxyManager.getInstance().toGetGoodInfo(mHandler, tokenid, id);
		String uri = API.API_GET_PRODUCT_INTRODUCE + "?token_id=" + tokenid + "&id=" + id;
		webview.loadUrl(uri);
	}

	public void addShopCart(View view) {
		ToastUtil.showLong(this, name + "已经加入购物车");
	}

	public void buyBtn(View view) {
		if (isService != null && isService.equals("1")) {
			mSharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCE, Activity.MODE_PRIVATE);
			String tokenid = mSharedPreferences.getString(Constant.TOKEN_ID, "");
			// String saleId = mSharedPreferences.getString(Constant.ID, "");
			NetProxyManager.getInstance().toAddOrderByService(mHandler, tokenid, id);
		} else {
			Intent i = new Intent();
			i.setClass(this, ChooseCardFromNoticeActivity.class);
			i.putExtra("good_info", id + "_" + price + "_1");
			i.putExtra("productSkuIds", selectTag);
			i.putExtra("customTexts", customTexts);
			i.putExtra("customerIds", customerIds);
			startActivity(i);
		}
	}

	private void setGuide(int index) {
		img_gallery.setSelection(index);
		mGuideOne.setImageResource(index == 0 ? mIndicatorSelected : mIndicatorUnSelected);
		mGuideTwo.setImageResource(index == 1 ? mIndicatorSelected : mIndicatorUnSelected);
		mGuideThree.setImageResource(index == 2 ? mIndicatorSelected : mIndicatorUnSelected);
	}

	public void setTags(View view) {
//		if (tags == null || tags.length() <= 0 || !tags.contains(",")) {
//
//		}
		GoodStyleDialog builder = new GoodStyleDialog(this).builder();
		builder.setTitle("请选择规格");
//		builder.setCancelable(false);
//		builder.setCanceledOnTouchOutside(false);
		builder.setOkButton(new OnButtonClickListener() {

			@Override
			public void onClick(String productSkuIds, String tagStr) {
				selectTag = productSkuIds;
				tag.setText(tagStr);
			}
		});
		for (String pName : tagList.keySet()) {
			builder.addStyleItem(pName, tagList.get(pName));
		}
//		List<TagBean> colorList = new ArrayList<TagBean>();
//		TagBean tagBean = new TagBean();
//		tagBean.setId("0");
//		tagBean.setTagName("红色");
//		colorList.add(tagBean);
//		TagBean tagBean1 = new TagBean();
//		tagBean1.setId("1");
//		tagBean1.setTagName("黑色");
//		colorList.add(tagBean1);
//		TagBean tagBean2 = new TagBean();
//		tagBean2.setId("2");
//		tagBean2.setTagName("白色");
//		colorList.add(tagBean2);
//		TagBean tagBean3 = new TagBean();
//		tagBean3.setId("3");
//		tagBean3.setTagName("蓝色");
//		colorList.add(tagBean3);
//		TagBean tagBean4 = new TagBean();
//		tagBean4.setId("4");
//		tagBean4.setTagName("粉色");
//		colorList.add(tagBean4);
//		builder.addStyleItem("颜色", colorList);
		builder.show();
	}

	@Override
	public void onResume() {
		super.onResume();
		webview.resumeTimers();
		webview.onResume();
		mHandler.postDelayed(mRunnable, DELAY_TIME);
	}

	@Override
	public void onPause() {
		webview.onPause();
		webview.pauseTimers();
//		webview.reload();
		super.onPause();
		mHandler.removeCallbacks(mRunnable);
	}
	
	@Override
	protected void onDestroy() {
		webview.destroy();
		webview = null;
		super.onDestroy();
	}

	private Runnable mRunnable = new Runnable() {

		@Override
		public void run() {
			if (img_gallery.getCount() > 0) {
				int index = (img_gallery.getSelectedItemPosition() + 1) % img_gallery.getCount();
				img_gallery.setSelection(index);
				setGuide(index);
			}
			mHandler.postDelayed(mRunnable, DELAY_TIME);
		}
	};

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
			name = job.getString("pname");
			good_name.setText(name);
			price = job.getString("psaleprice");
			good_price.setText("￥" + price);
			List<ImagePageBean> mImagePageList = mImageAdapter.getmImagePageList();
			mImagePageList.clear();
			String picUrlStr = job.getString("picUrls");
			String[] picUrls = picUrlStr.split(",");
			for (int i = 0; i < picUrls.length; i++) {
				ImagePageBean imagePageBean = new ImagePageBean();
				imagePageBean.setUri(picUrls[i]);
				mImagePageList.add(imagePageBean);
			}
			mImageAdapter.notifyDataSetChanged();
			if (job.has("tagIds")) {
				tags = job.getString("tagIds");
//				if (tags != null && !tags.contains("null") && !tags.contains(",")) {
//					tag.setText(tags);
//				}
			}
			if (job.has("customTexts") && !job.isNull("customTexts")) {
				JSONArray array = job.getJSONArray("customTexts");
				for (int i = 0; i < array.length(); i++) {
					if (i == 0) {
						customTexts = array.getString(i);
					} else {
						customTexts = customTexts + "_" + array.getString(i);
					}
				}
			}
			if (job.has("tagPropeties") && !job.isNull("tagPropeties")
					&& job.getString("tagPropeties").trim().length() > 0) {
				JSONArray jsonArray = job.getJSONArray("tagPropeties");
				String tagStr = "选择";
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject obj = jsonArray.getJSONObject(i);
					String pName = obj.getString("name");
					if (i == 0) {
					    tagStr = tagStr + pName;
					} else {
						tagStr = tagStr + "\\" + pName;
					}
					String pId = obj.getString("id");
					JSONArray array = obj.getJSONArray("skuInfo");
					List<TagBean> tagBeanList = new ArrayList<TagBean>();
					for (int j = 0; j < array.length(); j++) {
						JSONObject jsonObject = array.getJSONObject(j);
						String skuId = jsonObject.getString("skuId");
						String tag = jsonObject.getString("tag");
						TagBean tagBean = new TagBean();
						tagBean.setId(skuId);
						tagBean.setTagName(tag);
						tagBean.setpId(pId);
						tagBean.setpName(pName);
						tagBeanList.add(tagBean);
					}
					tagList.put(pName, tagBeanList);
				}
				if (tagList.size() <= 0) {
					tagLayout.setVisibility(View.GONE);
					tag.setText("无标签");
				} else {
					tagLayout.setVisibility(View.VISIBLE);
					tag.setText(tagStr);
				}
			} else {
				tagLayout.setVisibility(View.GONE);
			}
			// ToastUtil.showShort(this, "ok--" + object);
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
