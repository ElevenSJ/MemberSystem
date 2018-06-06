package com.sj.activity.base;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lyp.membersystem.LoginActivity;
import com.lyp.membersystem.R;
import com.lyp.membersystem.base.BaseApplication;
import com.lyp.membersystem.utils.Constant;
import com.lyp.membersystem.view.dialog.WaitDialog;
import com.sj.utils.Utils;

public abstract class ActivityBase extends AppCompatActivity {
	
	private WaitDialog mWaitDialog;
	public boolean isPause = false;

	TextView txtTitle;
	TextView txtTitleRight;
	ImageView imgRight;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFormat(PixelFormat.TRANSLUCENT);
		ViewManager.getInstance().addActivity(this);
		setContentView(getContentLayout());
		txtTitle = findViewById(R.id.tv_title);
		txtTitleRight = findViewById(R.id.tv_right);
		imgRight= findViewById(R.id.right);
		initView();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		isPause = false;
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		isPause = true;
	}
	
	@Override
	protected void onStop() {
		super.onStop();
	}
	
	@Override
	protected void onDestroy() {
		hideProgress();
		ViewManager.getInstance().finishActivity(this);
		super.onDestroy();
	}

	@Override
	public boolean onSupportNavigateUp() {
		onBackPressed();
		return true;
	}
	
	public void backLogin() {
		BaseApplication.getInstance().finishAllActivity();
		ViewManager.getInstance().finishAllActivity();
		SharedPreferences mSharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCE, MODE_PRIVATE);
		Editor editor = mSharedPreferences.edit();
		editor.clear().commit();
		Intent i = new Intent();
		i.setClass(this, LoginActivity.class);
		startActivity(i);
		finish();
	}

	public void showProgress(){
		if (mWaitDialog == null) {
			mWaitDialog = new WaitDialog(ActivityBase.this, R.string.loading_data);
		}
		mWaitDialog.show();
	}
	
	public void hideProgress() {
		if (mWaitDialog != null&&mWaitDialog.isShowing()) {
			mWaitDialog.dismiss();
		}
	}
	
	public abstract int getContentLayout();
	public abstract void initView();


	/**
	 * 添加fragment
	 *
	 * @param fragment
	 * @param frameId
	 */
	protected void addFragment(FragmentBase fragment, @IdRes int frameId) {
		Utils.checkNotNull(fragment);
		getSupportFragmentManager().beginTransaction()
				.add(frameId, fragment, fragment.getClass().getSimpleName())
				.addToBackStack(fragment.getClass().getSimpleName())
				.commitAllowingStateLoss();

	}


	/**
	 * 替换fragment
	 * @param fragment
	 * @param frameId
	 */
	protected void replaceFragment(FragmentBase fragment, @IdRes int frameId) {
		Utils.checkNotNull(fragment);
		getSupportFragmentManager().beginTransaction()
				.replace(frameId, fragment, fragment.getClass().getSimpleName())
				.addToBackStack(fragment.getClass().getSimpleName())
				.commitAllowingStateLoss();

	}


	/**
	 * 隐藏fragment
	 * @param fragment
	 */
	protected void hideFragment(FragmentBase fragment) {
		Utils.checkNotNull(fragment);
		getSupportFragmentManager().beginTransaction()
				.hide(fragment)
				.commitAllowingStateLoss();

	}


	/**
	 * 显示fragment
	 * @param fragment
	 */
	protected void showFragment(FragmentBase fragment) {
		Utils.checkNotNull(fragment);
		getSupportFragmentManager().beginTransaction()
				.show(fragment)
				.commitAllowingStateLoss();

	}


	/**
	 * 移除fragment
	 * @param fragment
	 */
	protected void removeFragment(FragmentBase fragment) {
		Utils.checkNotNull(fragment);
		getSupportFragmentManager().beginTransaction()
				.remove(fragment)
				.commitAllowingStateLoss();

	}


	/**
	 * 弹出栈顶部的Fragment
	 */
	protected void popFragment() {
		if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
			getSupportFragmentManager().popBackStack();
		} else {
			finish();
		}
	}

	public String getTitleTxt() {
		if (txtTitle != null&&txtTitle.getVisibility()==View.VISIBLE) {
			return txtTitle.getText().toString();
		}
		return "";
	}
	public void setTitleTxt(String titleTxt) {
		if (txtTitle != null) {
			txtTitle.setVisibility(TextUtils.isEmpty(titleTxt) ? View.INVISIBLE : View.VISIBLE);
			if (!TextUtils.isEmpty(titleTxt)) {
				txtTitle.setText(titleTxt);
			}
		}
	}
	public void setTitleRightTxt(String txt) {
		if (txtTitleRight != null) {
			txtTitleRight.setVisibility(TextUtils.isEmpty(txt) ? View.INVISIBLE : View.VISIBLE);
			if (!TextUtils.isEmpty(txt)) {
				txtTitleRight.setText(txt);
			}
		}
	}

	public void setRightImg(int res) {
		if (imgRight != null) {
			imgRight.setVisibility(res==0 ? View.GONE : View.VISIBLE);
			if (res!=0) {
				imgRight.setImageResource(res);
			}
		}
	}

	public void onRightImg(View view) {
	}
	public void onRightTxt(View view) {
	}

	public void onBack(View view) {
		finish();
	}

}
