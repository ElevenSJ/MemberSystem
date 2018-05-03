package com.lyp.membersystem.ui;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.lyp.membersystem.R;
import com.lyp.membersystem.base.BaseActivity;
import com.lyp.membersystem.bean.NoticeBean;
import com.lyp.membersystem.log.LogUtils;
import com.lyp.membersystem.manager.ImageManager;
import com.lyp.membersystem.net.Errors;
import com.lyp.membersystem.net.MessageContants;
import com.lyp.membersystem.net.NetProxyManager;
import com.lyp.membersystem.utils.Constant;
import com.lyp.membersystem.utils.DateUtil;
import com.lyp.membersystem.utils.SystemStatusManager;
import com.lyp.membersystem.utils.ToastUtil;
import com.lyp.membersystem.view.dialog.WaitDialog;
import com.yuntongxun.ecdemo.common.utils.LogUtil;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

public class CreateRuleActivity extends BaseActivity implements OnClickListener, OnItemSelectedListener {
	private SharedPreferences mSharedPreferences;
	private WaitDialog mWaitDialog;
	private NoticeBean mNoticeBean;
	private TextView tv_title;
	private EditText rule_name;
	private TextView rule_date;
	private Spinner rule_gener;
	private Spinner rule_min_age;
	private Spinner rule_max_age;
	private Spinner rule_marry;
	private Spinner rule_haschild;
	private ImageView good_iv1;
	private ImageView good_iv2;
	private ImageView good_iv3;
	private ImageView card_iv;
	private ImageView package_iv;
	private DatePickerDialog mDatePickerDialog;
	private String date;
	private String id;
	private String marryStr;
	private String gengerStr;
	private String haveChildren;
	private String minAgeStr;
	private String maxAgeStr;
	List<String> ageArray = new ArrayList<String>();
	private String good_id1;
	private String good_id2;
	private String good_id3;
	private String card_id;
	private String pack_id;

	private Handler mainHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MessageContants.MSG_ADD_PERSON_RULE: {
				parseAddPersonRule((String) msg.obj);
				break;
			}
			case MessageContants.MSG_UPDATE_PERSON_RULE: {
				parseUpdatePersonRule((String) msg.obj);
				break;
			}
			case MessageContants.MSG_DELETE_PERSON_RULE: {
				parseDeletePersonRule((String) msg.obj);
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
		id = getIntent().getStringExtra("rule_id");
		if (id != null) {
			mNoticeBean = getIntent().getParcelableExtra("rule");
		}
		setContentView(R.layout.create_rule_layout);
		initView();
		initData();
	}

	private void initView() {
		tv_title = (TextView) findViewById(R.id.tv_title);
		if (id != null) {
			tv_title.setText("更新规则");
		} else {
			tv_title.setText("新建规则");
		}
		rule_name = (EditText) findViewById(R.id.rule_name);
		rule_date = (TextView) findViewById(R.id.rule_date);
		rule_date.setOnClickListener(this);
		rule_gener = (Spinner) findViewById(R.id.rule_gener);
		rule_gener.setSelection(2);
		rule_gener.setOnItemSelectedListener(this);
		rule_min_age = (Spinner) findViewById(R.id.rule_min_age);
		rule_min_age.setOnItemSelectedListener(this);
		for (int i = 0; i < 150; i++) {
			if (i == 0) {
				ageArray.add("   ");
			} else {
				ageArray.add(String.valueOf(i));
			}
		}
		ArrayAdapter<String> adapte = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ageArray);
		adapte.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		rule_min_age.setAdapter(adapte);
		rule_max_age = (Spinner) findViewById(R.id.rule_max_age);
		rule_max_age.setOnItemSelectedListener(this);
		rule_max_age.setAdapter(adapte);
		rule_marry = (Spinner) findViewById(R.id.rule_marry);
		rule_marry.setSelection(4);
		rule_marry.setOnItemSelectedListener(this);
		rule_haschild = (Spinner) findViewById(R.id.rule_haschild);
		rule_haschild.setSelection(2);
		rule_haschild.setOnItemSelectedListener(this);
		good_iv1 = (ImageView) findViewById(R.id.good_iv1);
		good_iv1.setOnClickListener(this);
		good_iv2 = (ImageView) findViewById(R.id.good_iv2);
		good_iv2.setOnClickListener(this);
		good_iv3 = (ImageView) findViewById(R.id.good_iv3);
		good_iv3.setOnClickListener(this);
		card_iv = (ImageView) findViewById(R.id.card_iv);
		card_iv.setOnClickListener(this);
		package_iv = (ImageView) findViewById(R.id.package_iv);
		package_iv.setOnClickListener(this);
	}
	
	private void initData() {
		if (id == null) {
			return;
		}
		
		rule_name.setText(mNoticeBean.getTitle());
		date = mNoticeBean.getRemindTime();
		rule_date.setText(date);
		marryStr = mNoticeBean.getMaritalStatus();
		if (marryStr != null) {
		    rule_marry.setSelection(Integer.valueOf(marryStr));
		}
		gengerStr = mNoticeBean.getGender();
		if (gengerStr != null) {
		    rule_gener.setSelection(Integer.valueOf(gengerStr));
		}
		haveChildren = mNoticeBean.getHaveChildren();
		if (haveChildren != null) {
		    rule_haschild.setSelection(Integer.valueOf(haveChildren));
		}
		minAgeStr = mNoticeBean.getAgeMin();
		maxAgeStr = mNoticeBean.getAgeMax();
		if (minAgeStr != null) {
			Integer minAge = Integer.valueOf(minAgeStr);
			if (minAge > 150 || minAge < 0) {
				rule_min_age.setSelection(0);
			} else {
				rule_min_age.setSelection(minAge);
			}
		}
		if (maxAgeStr != null) {
			Integer maxAge = Integer.valueOf(maxAgeStr);
			if (maxAge > 150 || maxAge < 0) {
				rule_max_age.setSelection(150);
			} else {
				rule_max_age.setSelection(maxAge);
			}
		}
		List<String> goodlist = mNoticeBean.getGoodlist();
		if (goodlist != null) {
			for (int i = 0; i < goodlist.size(); i++) {
				switch(i) {
				case 0:
					String[] good1 = goodlist.get(0).split(",");
					good_id1 = good1[0];
					ImageManager.loadImage(good1[2], good_iv1);
					break;
                case 1:
                	String[] good2 = goodlist.get(1).split(",");
        			good_id2 = good2[0];
        			ImageManager.loadImage(good2[2], good_iv2);
					break;	
                case 2:
                	String[] good3 = goodlist.get(2).split(",");
        			good_id3 = good3[0];
        			ImageManager.loadImage(good3[2], good_iv3);
					break;	
				}
			}
		}
		if (mNoticeBean.getCard() != null) {
			String[] card = mNoticeBean.getCard().split(",");
			card_id = card[0];
			ImageManager.loadImage(card[1], card_iv);
		}
//		String[] pack = mNoticeBean.getPack().split(",");
//		pack_id = pack[0];
//		ImageManager.loadImage(pack[2], package_iv);
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.rule_date:
			setDate();
			break;
		case R.id.good_iv1:
		case R.id.good_iv2:
		case R.id.good_iv3:
			intent.setClass(this, MallFragmentFromRuleActivity.class);
			startActivityForResult(intent, v.getId());
			break;
		case R.id.card_iv:
			intent.setClass(this, ChooseCardFromRuleActivity.class);
			startActivityForResult(intent, v.getId());
			break;
		case R.id.package_iv:
			intent.setClass(this, ChoosePackageFromRuleActivity.class);
			startActivityForResult(intent, v.getId());
			break;
		default:
			break;
		}

	}

	@Override
	public void onItemSelected(AdapterView<?> spinnerView, View view, int position, long arg3) {
		switch (spinnerView.getId()) {
		case R.id.rule_gener:
			if (position == 2) {
				gengerStr = null;
			} else {
				gengerStr = String.valueOf(position);
			}
			break;
		case R.id.rule_min_age:
			if (position == 0) {
				minAgeStr = null;
			} else {
				minAgeStr = String.valueOf(position);
			}
			break;
		case R.id.rule_max_age:
			if (position == 0) {
				maxAgeStr = null;
			} else {
				maxAgeStr = String.valueOf(position);
			}
			break;
		case R.id.rule_marry:
			if (position == 2) {
				marryStr = null;
			} else {
				marryStr = String.valueOf(position);
			}
			break;
		case R.id.rule_haschild:
			if (position == 2) {
				haveChildren = null;
			} else {
				haveChildren = String.valueOf(position);
			}
			break;
		default:
			break;
		}

	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

	public void delete(View view) {
		if (id == null) {
			finish();
			return;
		}
		mSharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCE, MODE_PRIVATE);
		String tokenid = mSharedPreferences.getString(Constant.TOKEN_ID, "");
		String saleId = mSharedPreferences.getString(Constant.ID, "");
		if (mWaitDialog == null) {
			mWaitDialog = new WaitDialog(this, R.string.loading_data);
		}
		mWaitDialog.show();
		NetProxyManager.getInstance().toDeletePersonRule(mainHandler, tokenid, id);
	}

	public void saveRule(View view) {
		NoticeBean noticeBean = new NoticeBean();
		Editable text = rule_name.getText();
		if (text == null || text.toString().length() <= 0) {
			ToastUtil.showLongMessage("规则名称不能为空");
			return;
		}
		noticeBean.setTitle(text.toString());
		if (date == null) {
			ToastUtil.showLongMessage("日期不能为空");
			return;
		}
		noticeBean.setRemindTime(date);
		
		if (card_id == null) {
			ToastUtil.showLongMessage("卡片不能为空");
			return;
		}
		noticeBean.setCard(card_id);
//		if (pack_id == null) {
//			ToastUtil.showLongMessage("包装不能为空");
//			return;
//		}
//		noticeBean.setPack(pack_id);
		noticeBean.setMaritalStatus(marryStr);
		noticeBean.setGender(gengerStr);
		noticeBean.setHaveChildren(haveChildren);
		noticeBean.setAgeMax(maxAgeStr);
		noticeBean.setAgeMin(minAgeStr);
		LogUtils.d("lyp", marryStr + gengerStr + haveChildren + maxAgeStr + minAgeStr);
		List<String> goodlist = new ArrayList<String>();
		if (good_id1 != null) {
			goodlist.add(good_id1);
		}
		if (good_id2 != null) {
			goodlist.add(good_id2);
		}
		if (good_id3 != null) {
			goodlist.add(good_id3);
		}
		noticeBean.setGoodlist(goodlist);
		//i.	个人规则不支持生日规则，只支持特定日期
		if (marryStr == null && gengerStr == null && haveChildren == null && maxAgeStr == null && minAgeStr == null) {
			noticeBean.setCondition("0");
		} else {
			noticeBean.setCondition("1");
		}
		mSharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCE, MODE_PRIVATE);
		String tokenid = mSharedPreferences.getString(Constant.TOKEN_ID, "");
		String saleId = mSharedPreferences.getString(Constant.ID, "");
		if (mWaitDialog == null) {
			mWaitDialog = new WaitDialog(this, R.string.loading_data);
		}
		mWaitDialog.show();
		if (id == null) {
			NetProxyManager.getInstance().toAddPersonRule(mainHandler, tokenid, saleId, noticeBean);
		} else {
			noticeBean.setId(id);
			NetProxyManager.getInstance().toUpdatePersonRule(mainHandler, tokenid, saleId, noticeBean);
		}
	}

	public void setDate() {
		if (mDatePickerDialog == null) {
			Calendar calendar = Calendar.getInstance();
			mDatePickerDialog = new DatePickerDialog(this, DatePickerDialog.THEME_HOLO_LIGHT, new OnDateSetListener() {

				@Override
				public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
					date = year + "-" + DateUtil.addZeroStr(String.valueOf(monthOfYear + 1)) + "-" +  DateUtil.addZeroStr(String.valueOf(dayOfMonth));
					// tv_birthday.setText(DateUtil.stringToStr6(birthday));
					rule_date.setText(date);
				}
			}, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
		}

		if (!mDatePickerDialog.isShowing()) {
			mDatePickerDialog.show();
		}
	}

	/**
	 * to parse login infomations from network
	 */
	private void parseAddPersonRule(String result) {
		if (Errors.ERROR_NET.equals(result) || Errors.ERROR_SERVER.equals(result)) {
			ToastUtil.showLong(this, R.string.network_error);
			if (mWaitDialog != null && mWaitDialog.isShowing()) {
				mWaitDialog.dismiss();
			}
			return;
		}
		// to parser json data
		try {
			LogUtil.d("lyp", result);
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
			setResult(RESULT_OK);
			finish();
		} catch (Exception ex) {
			LogUtils.e(ex.getMessage());
			return;
		}
	}
	
	/**
	 * to parse login infomations from network
	 */
	private void parseUpdatePersonRule(String result) {
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
			setResult(RESULT_OK);
			finish();
		} catch (Exception ex) {
			LogUtils.e(ex.getMessage());
			return;
		}
	}
	
	/**
	 * to parse login infomations from network
	 */
	private void parseDeletePersonRule(String result) {
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
			setResult(RESULT_OK);
			finish();
		} catch (Exception ex) {
			LogUtils.e(ex.getMessage());
			return;
		}
	}

	public void onBack(View view) {
		finish();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case R.id.good_iv1:
				good_id1 = data.getStringExtra("good_id");
				String good_pic = data.getStringExtra("good_pic");
				String good_price = data.getStringExtra("good_price");
				LogUtils.d("lyp", good_id1 + "-----" + good_pic);
				ImageManager.loadImage(good_pic, good_iv1);
				break;
			case R.id.good_iv2:
				good_id2 = data.getStringExtra("good_id");
				String good_pic2 = data.getStringExtra("good_pic");
				String good_price2 = data.getStringExtra("good_price");
				ImageManager.loadImage(good_pic2, good_iv2);
				break;
			case R.id.good_iv3:
				good_id3 = data.getStringExtra("good_id");
				String good_pic3 = data.getStringExtra("good_pic");
				String good_price3 = data.getStringExtra("good_price");
				ImageManager.loadImage(good_pic3, good_iv3);
				break;
			case R.id.card_iv:
				card_id = data.getStringExtra("card_id");
				String card_icon = data.getStringExtra("card_icon");
				ImageManager.loadImage(card_icon, card_iv);
				break;
			case R.id.package_iv:
				pack_id = data.getStringExtra("pack_id");
				String pack_icon = data.getStringExtra("pack_icon");
				LogUtils.d("lyp", pack_id);
				ImageManager.loadImage(pack_icon, package_iv);
				break;
			default:
				break;
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

}
