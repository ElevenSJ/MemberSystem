package com.lyp.membersystem.ui;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.lyp.membersystem.R;
import com.lyp.membersystem.adapter.ChooseCardAdapter;
import com.lyp.membersystem.base.BaseActivity;
import com.lyp.membersystem.base.BaseApplication;
import com.lyp.membersystem.bean.CardEnvelopBean;
import com.lyp.membersystem.log.LogUtils;
import com.lyp.membersystem.net.Errors;
import com.lyp.membersystem.net.MessageContants;
import com.lyp.membersystem.net.NetProxyManager;
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
import android.widget.ListView;

public class ChooseCardActivity extends BaseActivity {

	private WaitDialog mWaitDialog;
	private ListView lv_card;
	private SharedPreferences mSharedPreferences;
	private ChooseCardAdapter mChooseCardAdapter;
	private List<CardEnvelopBean> list;
	private String good_info;
	private String productSkuIds;
	private String customTexts;

	private Handler mainHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MessageContants.MSG_GET_CARD_LIST: {
				parseGetCardEnvelopList((String) msg.obj);
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
		setContentView(R.layout.choose_card_layout);
		good_info = getIntent().getStringExtra("good_info");
		productSkuIds = getIntent().getStringExtra("productSkuIds");
		customTexts = getIntent().getStringExtra("customTexts");
		initView();
		initData();
	}

	private void initView() {
		lv_card = (ListView) findViewById(R.id.lv_card);
		list = new ArrayList<CardEnvelopBean>();
		mChooseCardAdapter = new ChooseCardAdapter(this, list);
		lv_card.setAdapter(mChooseCardAdapter);
	}

	private void initData() {
		mSharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCE, MODE_PRIVATE);
		String tokenid = mSharedPreferences.getString(Constant.TOKEN_ID, "");
		String saleId = mSharedPreferences.getString(Constant.ID, "");
		if (mWaitDialog == null) {
			mWaitDialog = new WaitDialog(this, R.string.loading_data);
		}
		mWaitDialog.show();
		NetProxyManager.getInstance().toGetCardEnvelopList(mainHandler, tokenid, saleId);
	}

	public void nextStep(View view) {
		Intent i = new Intent();
		i.setClass(this, EditCardActivity.class);
		CardEnvelopBean cardEnvelopBean = list.get(mChooseCardAdapter.getSelectCard());
		i.putExtra("good_info", good_info);
		i.putExtra("productSkuIds", productSkuIds);
		i.putExtra("card_info", cardEnvelopBean.getId() + "_" + cardEnvelopBean.getPrice());
		i.putExtra("card_icon", cardEnvelopBean.getCardFrontFile());
		i.putExtra("customTexts", customTexts);
		startActivity(i);
	}

	/**
	 * to parse login infomations from network
	 */
	private void parseGetCardEnvelopList(String result) {
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
			JSONObject obj = json.getJSONObject("object");
			JSONArray jsonArray = obj.getJSONArray("infoList");
			list.clear();
			for (int i = 0; i < jsonArray.length(); i++) {
				CardEnvelopBean cardEnvelopBean = new CardEnvelopBean();
				JSONObject childObj = jsonArray.getJSONObject(i);
				cardEnvelopBean.setId(childObj.getString("id"));
				cardEnvelopBean.setName(childObj.getString("name"));
				cardEnvelopBean.setPrice(childObj.getString("price"));
				if (childObj.has("cardEnvelopUrls")) {
					String urlStr = childObj.getString("cardEnvelopUrls");
					String[] urls = urlStr.split(",");
					if (urls.length > 1) {
					    cardEnvelopBean.setCardFrontFile(urls[0]);
					    cardEnvelopBean.setCardVersoFile(urls[1]);
					}
				}
				
				if (childObj.has("cardFrontFile")) {
					JSONObject job = childObj.getJSONObject("cardFrontFile");
					if (job.has("fileUrl")) {
						cardEnvelopBean.setCardFrontFile(job.getString("fileUrl"));
					}
				}
				
				if (childObj.has("cardVersoFile")) {
					JSONObject job = childObj.getJSONObject("cardVersoFile");
					if (job.has("fileUrl")) {
						cardEnvelopBean.setCardVersoFile(job.getString("fileUrl"));
					}
				}
				list.add(cardEnvelopBean);
			}
			mChooseCardAdapter.updateListView(list);
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
	
	@Override
	protected void onDestroy() {
		BaseApplication.getInstance().removeActivity(this);
		super.onDestroy();
	};
}
