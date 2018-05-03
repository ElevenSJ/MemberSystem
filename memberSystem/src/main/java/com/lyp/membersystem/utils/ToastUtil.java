package com.lyp.membersystem.utils;

import com.lyp.membersystem.base.BaseApplication;
import com.yuntongxun.ecdemo.common.utils.LogUtil;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

/**
 * Toast统一管理类
 * 
 * @author way
 * 
 */
public class ToastUtil {
	// Toast
	private static Toast toast;

	/**
	 * 短时间显示Toast
	 * 
	 * @param context
	 * @param message
	 */
	public static void showShort(Context context, CharSequence message) {
		if (null == toast) {
			toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
			// toast.setGravity(Gravity.CENTER, 0, 0);
		} else {
			toast.setText(message);
		}
		toast.show();
	}

	/**
	 * 短时间显示Toast
	 * 
	 * @param context
	 * @param message
	 */
	public static void showShort(Context context, int message) {
		if (null == toast) {
			toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
			// toast.setGravity(Gravity.CENTER, 0, 0);
		} else {
			toast.setText(message);
		}
		toast.show();
	}

	/**
	 * 长时间显示Toast
	 * 
	 * @param context
	 * @param message
	 */
	public static void showLong(Context context, CharSequence message) {
		if (null == toast) {
			toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
			// toast.setGravity(Gravity.CENTER, 0, 0);
		} else {
			toast.setText(message);
		}
		toast.show();
	}

	/**
	 * 长时间显示Toast
	 * 
	 * @param context
	 * @param message
	 */
	public static void showLong(Context context, int message) {
		if (null == toast) {
			toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
			// toast.setGravity(Gravity.CENTER, 0, 0);
		} else {
			toast.setText(message);
		}
		toast.show();
	}

	/**
	 * 自定义显示Toast时间
	 * 
	 * @param context
	 * @param message
	 * @param duration
	 */
	public static void show(Context context, CharSequence message, int duration) {
		if (null == toast) {
			toast = Toast.makeText(context, message, duration);
			// toast.setGravity(Gravity.CENTER, 0, 0);
		} else {
			toast.setText(message);
		}
		toast.show();
	}

	/**
	 * 自定义显示Toast时间
	 * 
	 * @param context
	 * @param message
	 * @param duration
	 */
	public static void show(Context context, int message, int duration) {
		if (null == toast) {
			toast = Toast.makeText(context, message, duration);
			// toast.setGravity(Gravity.CENTER, 0, 0);
		} else {
			toast.setText(message);
		}
		toast.show();
	}

	/** Hide the toast, if any. */
	public static void hideToast() {
		if (null != toast) {
			toast.cancel();
		}
	}

	private static Handler handler = new Handler(Looper.getMainLooper());

	private static Object synObj = new Object();

	public static void showMessage(final String msg) {
		showMessage(msg, Toast.LENGTH_SHORT);
	}

	/**
	 * 根据设置的文本显示
	 * 
	 * @param msg
	 */
	public static void showMessage(final int msg) {
		showMessage(msg, Toast.LENGTH_SHORT);
	}
	
	public static void showLongMessage(final String msg) {
		showMessage(msg, Toast.LENGTH_LONG);
	}

	/**
	 * 根据设置的文本显示
	 * 
	 * @param msg
	 */
	public static void showLongMessage(final int msg) {
		showMessage(msg, Toast.LENGTH_LONG);
	}

	/**
	 * 显示一个文本并且设置时长
	 * 
	 * @param msg
	 * @param len
	 */
	public static void showMessage(final CharSequence msg, final int len) {
		if (msg == null || msg.equals("")) {
			LogUtil.w("[ToastUtil] response message is null.");
			return;
		}
		handler.post(new Runnable() {
			@Override
			public void run() {
				synchronized (synObj) { // 加上同步是为了每个toast只要有机会显示出来
					if (toast != null) {
						// toast.cancel();
						toast.setText(msg);
						toast.setDuration(len);
					} else {
						toast = Toast.makeText(BaseApplication.getInstance().getApplicationContext(), msg, len);
					}
					toast.show();
				}
			}
		});
	}

	/**
	 * 资源文件方式显示文本
	 * 
	 * @param msg
	 * @param len
	 */
	public static void showMessage(final int msg, final int len) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				synchronized (synObj) {
					if (toast != null) {
						// toast.cancel();
						toast.setText(msg);
						toast.setDuration(len);
					} else {
						toast = Toast.makeText(BaseApplication.getInstance().getApplicationContext(), msg, len);
					}
					toast.show();
				}
			}
		});
	}
}
