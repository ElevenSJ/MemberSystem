
package com.lyp.membersystem.ui;

import java.util.Calendar;

import com.lyp.membersystem.R;
import com.lyp.membersystem.utils.DateUtil;
import com.lyp.membersystem.utils.SystemStatusManager;
import com.lyp.membersystem.utils.ToastUtil;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

public class SetSpecInfo extends Activity {

	private final String TAG = SetSpecInfo.class.getSimpleName();

	private String remark;
	private String date;
	private int item;
	private EditText spec_item_et;
	private TextView spec_date;
	private DatePickerDialog mDatePickerDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle extras = getIntent().getExtras();
		item = extras.getInt("item");
		remark = extras.getString("remark");
		date = extras.getString("date");
		setTranslucentStatus();
		setContentView(R.layout.set_spec_info);
		spec_item_et = (EditText) findViewById(R.id.spec_item_et);
		spec_item_et.setText(remark);
		spec_date = (TextView)findViewById(R.id.spec_date);
		spec_date.setText(date);
	}

	public void setSpecDate(View view) {
		if (mDatePickerDialog == null) {
			Calendar calendar = Calendar.getInstance();
			mDatePickerDialog = new DatePickerDialog(this, DatePickerDialog.THEME_HOLO_LIGHT, new OnDateSetListener() {

				@Override
				public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//					date = year + "年" + DateUtil.addZeroStr(String.valueOf(monthOfYear + 1)) + "月" +  DateUtil.addZeroStr(String.valueOf(dayOfMonth)) + "日";
					date = year + "年" + (monthOfYear + 1) + "月" +  dayOfMonth + "日";
//					date = (monthOfYear + 1)+ "月" +  dayOfMonth + "日";
					spec_date.setText(date);
				}
			}, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
		}

		if (!mDatePickerDialog.isShowing()) {
			mDatePickerDialog.show();
		}
	}
	
	public void saveBtn(View view) {
		Editable text = spec_item_et.getText();
		if (text == null || text.toString().length() <= 0) {
			ToastUtil.showLongMessage("编辑内容不能为空！");
			return;
		}
		if (date == null || date.toString().length() <= 0) {
			ToastUtil.showLongMessage("日期不能为空！");
			return;
		}
		Intent intent = new Intent();
		intent.putExtra("item", item);
		intent.putExtra("remark", text.toString());
		intent.putExtra("date", date);
		setResult(RESULT_OK, intent);
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
