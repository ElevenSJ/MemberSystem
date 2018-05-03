package com.lyp.membersystem.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.lyp.membersystem.R;
import com.lyp.membersystem.bean.NoticeBean;
import com.lyp.membersystem.log.LogUtils;
import com.lyp.membersystem.net.Errors;
import com.lyp.membersystem.net.MessageContants;
import com.lyp.membersystem.net.NetProxyManager;
import com.lyp.membersystem.ui.ChooseCustomerActivity;
import com.lyp.membersystem.ui.ChooseGoodActivity;
import com.lyp.membersystem.ui.MallFragmentFromNoticeActivity;
import com.lyp.membersystem.ui.NoticeActivity;
import com.lyp.membersystem.utils.Constant;
import com.lyp.membersystem.utils.DateUtil;
import com.lyp.membersystem.utils.ToastUtil;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.widget.RemoteViews;

public class NoticeMgrService extends Service {

	private static final int DELAY_TIME = 24 * 60 * 60 * 1000;
	private Notification noticeNotification;
	private NotificationManager notificationMgr;
	private SharedPreferences mSharedPreferences;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MessageContants.MSG_GET_RULE_NOTICE_LIST: {
				parseRuleNoticeList((String) msg.obj);
				break;
			}
			default:
				break;
			}
		};
	};

	@Override
	public void onCreate() {
		super.onCreate();
		mHandler.post(mRunnable);
	}

	private Runnable mRunnable = new Runnable() {

		@Override
		public void run() {
			getData();
			mHandler.postDelayed(mRunnable, DELAY_TIME);
		}
	};

	private void getData() {
		mSharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCE, MODE_PRIVATE);
		String tokenid = mSharedPreferences.getString(Constant.TOKEN_ID, "");
		String saleId = mSharedPreferences.getString(Constant.ID, "");
		NetProxyManager.getInstance().toGetRuleNoticeList(mHandler, tokenid, "0", null);
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	private void createNotification(int flag, NoticeBean noticeBean, String content) {
		notificationMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		noticeNotification = new Notification(R.drawable.ic_launcher, "神助理", System.currentTimeMillis());
		noticeNotification.flags = Notification.FLAG_INSISTENT | Notification.FLAG_AUTO_CANCEL;
		noticeNotification.contentView = new RemoteViews(getPackageName(), R.layout.notice_notification);
		// 显示消息内容
		noticeNotification.contentView.setTextViewText(R.id.content, content);
		Intent notificationIntent = new Intent();
		ComponentName componet = null;
		if (noticeBean.getCustomerIds() != null) {
			if (noticeBean.getGoodlist().size() >= 3) {
				componet = new ComponentName(getPackageName(), ChooseGoodActivity.class.getName());
				notificationIntent.putExtra("select_notice", noticeBean);
			} else {
				componet = new ComponentName(getPackageName(), MallFragmentFromNoticeActivity.class.getName());
			}
			notificationIntent.putExtra("customerIds", noticeBean.getCustomerIds());
			componet = new ComponentName(getPackageName(), NoticeActivity.class.getName());
		} else {
			componet = new ComponentName(getPackageName(), ChooseCustomerActivity.class.getName());
			notificationIntent.putExtra("select_notice", noticeBean);
		}
		notificationIntent.setComponent(componet);
		notificationIntent.putExtra("id", flag);
		notificationIntent.setAction(Intent.ACTION_MAIN);
		notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		noticeNotification.contentIntent = contentIntent;
		// 显示通知
		notificationMgr.notify(flag, noticeNotification);
	}

	private void parseNoticeList(String result) {
		if (Errors.ERROR_NET.equals(result) || Errors.ERROR_SERVER.equals(result)) {
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
				return;
			}
			JSONObject job = json.getJSONObject("object");
			JSONArray jsonArray = job.getJSONArray("infoList");
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				if (jsonObject.has("remindTime")) {
					if (isNotice(jsonObject.getString("remindTime"))) {
						// createNotification(Integer.valueOf(jsonObject.getString("id")),
						// jsonObject.getString("ruleName"));
					}
				}
			}
		} catch (Exception ex) {
			LogUtils.e(ex.getMessage());
			return;
		}
	}

	private void parseRuleNoticeList(String result) {
		if (Errors.ERROR_NET.equals(result) || Errors.ERROR_SERVER.equals(result)) {
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
				return;
			}
			JSONArray jsonArray = json.getJSONArray("object");
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject job = jsonArray.getJSONObject(i);
				if (job.has("invalidTime")) {
					if (System.currentTimeMillis() > job.getLong("invalidTime")) {
						continue;
					}
				}
				JSONObject obj = new JSONObject(job.getString("noticeInfo"));
				NoticeBean noticeBean = new NoticeBean();
				noticeBean.setId(job.getString("id"));
				JSONArray array = obj.getJSONArray("product");
				List<String> goods = new ArrayList<String>();
				for (int j = 0; j < array.length(); j++) {
					JSONObject jb = array.getJSONObject(j);
					String str = jb.getString("id") + "," + jb.getString("price") + "," + jb.getString("pic") + ","
							+ jb.getString("name");
					goods.add(str);
				}
				noticeBean.setGoodlist(goods);
				noticeBean.setRuleId(obj.getString("ruleId"));
				String ruleType = obj.getString("ruleType");
				noticeBean.setRuleType(ruleType);
				if (obj.has("ruleName")) {
					noticeBean.setTitle(obj.getString("ruleName"));
				}
//				if (obj.has("ruleType")) {
//					noticeBean.setRuleType(obj.getString("ruleType"));
//				}
				if (obj.has("condition")) {
					noticeBean.setCondition(obj.getString("condition"));
				}
				
				if (obj.has("gender")) {
					noticeBean.setGender(obj.getString("gender"));
				}

				if (obj.has("haveChildren")) {
					noticeBean.setHaveChildren(obj.getString("haveChildren"));
				}

				if (obj.has("maritalStatus")) {
					noticeBean.setMaritalStatus(obj.getString("maritalStatus"));
				}

				if (obj.has("ageMax")) {
					noticeBean.setAgeMax(obj.getString("ageMax"));
				}

				if (obj.has("ageMin")) {
					noticeBean.setAgeMin(obj.getString("ageMin"));
				}
				
                noticeBean.setGender(obj.getString("gender"));
				JSONObject object = obj.getJSONObject("card");
				if (object.has("custom1")) {
					String cardInfo = null;
					if (!TextUtils.isEmpty(object.getString("custom1"))) {
						cardInfo = object.getString("custom1");
					}
					
					if (object.has("custom2") && !TextUtils.isEmpty(object.getString("custom2"))) {
						cardInfo = cardInfo + "_" + object.getString("custom2");
						
						if (object.has("custom3") && !TextUtils.isEmpty(object.getString("custom3"))) {
							cardInfo = cardInfo + "_" + object.getString("custom3");
						}
					}
					
				    noticeBean.setCardTexts(cardInfo);
				}
				String cardPrice = object.getString("price");
				String name = object.getString("name");
				String cardId = object.getString("id");
				String cardImage = null;
				if (object.has("cardImage") && object.getJSONArray("cardImage").length() > 0) {
					cardImage = object.getJSONArray("cardImage").getString(0);
				}
				noticeBean.setCard(cardId + "," + cardPrice + ","
						+ cardImage);
				createNotification(Integer.valueOf(job.getString("id")), noticeBean, job.getString("title"));
			}
		} catch (Exception ex) {
			LogUtils.e(ex.getMessage());
			return;
		}
	}

	private boolean isNotice(String remindTime) {
		if (remindTime == null || remindTime.trim().length() <= 0) {
			return false;
		}
		// 判断提前2个月和推后1个月的的日期内
		// 返回 0 表示时间日期相同
		// 返回 1 表示日期1>日期2
		// 返回 -1 表示日期1<日期2
		Date curDate = Calendar.getInstance().getTime();
		int i = curDate.compareTo(DateUtil.getDate(remindTime, 1));
		int j = curDate.compareTo(DateUtil.getDate(remindTime, -2));
		if (i == -1 && j == 1) {
			return true;
		}
		return false;
	}
}
