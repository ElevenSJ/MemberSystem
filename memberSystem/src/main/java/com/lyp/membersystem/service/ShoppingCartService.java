package com.lyp.membersystem.service;

import com.lyp.membersystem.R;
import com.lyp.membersystem.log.LogUtils;
import com.lyp.membersystem.ui.ShoppingCartActivity;
import com.lyp.membersystem.utils.DisplayUtil;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class ShoppingCartService extends Service {

	private String TAG = ShoppingCartService.class.getSimpleName();

	// 定义浮动窗口布局
	LinearLayout mFloatLayout;
	WindowManager.LayoutParams wmParams;
	// 创建浮动窗口设置布局参数的对象
	WindowManager mWindowManager;

	ImageView mFloatView;

	boolean mIsMove = false;
	float x1;
	float x2;
	float y1;
	float y2;

	@Override
	public void onCreate() {
		super.onCreate();
		createFloatView();

	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	private void createFloatView() {
		wmParams = new WindowManager.LayoutParams();
		// 获取的是WindowManagerImpl.CompatModeWrapper
		mWindowManager = (WindowManager) this
				.getSystemService(getApplication().WINDOW_SERVICE);
		LogUtils.i(TAG, "mWindowManager--->" + mWindowManager);
		// 设置window type
		wmParams.type = LayoutParams.TYPE_PHONE;
		// 设置图片格式，效果为背景透明
		wmParams.format = PixelFormat.RGBA_8888;
		// 设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
		wmParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE;
		// 调整悬浮窗显示的停靠位置为左侧置顶
		wmParams.gravity = Gravity.LEFT | Gravity.TOP;
		DisplayMetrics dm = this.getResources().getDisplayMetrics();
		DisplayUtil du = new DisplayUtil(this);
		// 以屏幕左上角为原点，设置x、y初始值，相对于gravity
		wmParams.x = dm.widthPixels - du.dipToPx(60);
		wmParams.y = dm.heightPixels - du.dipToPx(140) - 25;
		// 设置悬浮窗口长宽数据
		wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

		/*
		 * // 设置悬浮窗口长宽数据 wmParams.width = 200; wmParams.height = 80;
		 */

		LayoutInflater inflater = LayoutInflater.from(getApplication());
		// 获取浮动窗口视图所在布局
		mFloatLayout = (LinearLayout) inflater.inflate(R.layout.float_layout,
				null);
		// 浮动窗口按钮
		mFloatView = (ImageView) mFloatLayout.findViewById(R.id.float_id);

		mFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
				.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
		LogUtils.i(TAG, "Width/2--->" + mFloatView.getMeasuredWidth() / 2);
		LogUtils.i(TAG, "Height/2--->" + mFloatView.getMeasuredHeight() / 2);
		// 设置监听浮动窗口的触摸移动
		mFloatView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int action = event.getAction();
				switch (action) {
				case MotionEvent.ACTION_DOWN:
					x1 = event.getRawX();
					y1 = event.getRawY();
					mIsMove = false;
					return false;
				case MotionEvent.ACTION_MOVE:
					x2 = event.getRawX();
					y2 = event.getRawY();
					// getRawX是触摸位置相对于屏幕的坐标，getX是相对于按钮的坐标
					wmParams.x = (int) x2
							- mFloatView.getMeasuredWidth() / 2;
					LogUtils.i(TAG, "RawX" + event.getRawX());
					LogUtils.i(TAG, "X" + event.getX());
					// 减25为状态栏的高度
					wmParams.y = (int) y2
							- mFloatView.getMeasuredHeight() / 2 - 25;
					LogUtils.i(TAG, "RawY" + event.getRawY());
					LogUtils.i(TAG, "Y" + event.getY());
					// 刷新
					mWindowManager.updateViewLayout(mFloatLayout, wmParams);
					if (getDistance() > 20) {
					    mIsMove = true;
					}
					return true;
				default:
					if (mIsMove) {
						return true;
					}
					return false;// 此处必须返回false，否则OnClickListener获取不到监听
				}
			}
		});

		mFloatView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ShoppingCartService.this,
						ShoppingCartActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
						| Intent.FLAG_ACTIVITY_SINGLE_TOP);
				ShoppingCartService.this.startActivity(intent);
			}
		});
		// 添加mFloatLayout
		mWindowManager.addView(mFloatLayout, wmParams);
	}

	public double getDistance() {
		double _x = Math.abs(x1 - x2);
		double _y = Math.abs(y1 - y2);
		return Math.sqrt(_x * _x + _y * _y);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mFloatLayout != null) {
			// 移除悬浮窗口
			mWindowManager.removeView(mFloatLayout);
		}
	}
}
