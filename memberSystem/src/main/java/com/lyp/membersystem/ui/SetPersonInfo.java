
package com.lyp.membersystem.ui;

import com.lyp.membersystem.R;
import com.lyp.membersystem.utils.SystemStatusManager;
import com.lyp.membersystem.utils.ToastUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

public class SetPersonInfo extends Activity {

	private final String TAG = SetPersonInfo.class.getSimpleName();

	private String content;
	private EditText person_item_et;
	private TextView set_name;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle extras = getIntent().getExtras();
		content = extras.getString("content");
		String name = extras.getString("name");
		setTranslucentStatus();
		setContentView(R.layout.set_person_info);
		set_name = (TextView) findViewById(R.id.set_name);
		set_name.setText(name);
		person_item_et = (EditText) findViewById(R.id.person_item_et);
		person_item_et.setText(content);
	}

	public void saveBtn(View view) {
		Editable text = person_item_et.getText();
		if (text == null || text.toString().length() <= 0) {
			ToastUtil.showLongMessage("编辑内容不能为空！");
			return;
		}
		if (!content.equals(text.toString())) {
		    setResult(RESULT_OK, new Intent().putExtra("content", 
				 text.toString()));
		}
		finish();
	}

	public void cancelBtn(View view) {
		finish();
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
