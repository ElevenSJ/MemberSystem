package com.lyp.membersystem.utils;

import java.util.ArrayList;
import java.util.List;

import com.lyp.membersystem.R;
import com.lyp.membersystem.adapter.CityAdapter;
import com.lyp.membersystem.bean.MyListItem;
import com.lyp.membersystem.log.LogUtils;
import com.lyp.membersystem.manager.CityDBManager;

import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.Spinner;

public class CityDialog extends Dialog {

	private static final String TAG = "CityDialog";

	private CityDBManager dbm;
	private SQLiteDatabase db;
	private Spinner spinner1 = null;
	private Spinner spinner2 = null;
	private Spinner spinner3 = null;
	private Button bt_ok;
	private Button bt_cancel;
	private String district = null;
	private String province = null;
	private String city = null;
	private Context context;
	private InputListener IListener;

	public CityDialog(Context context) {
		super(context, android.R.style.Theme_Holo_Light_Dialog_MinWidth);
		this.context = context;
	}

	public CityDialog(Context context, InputListener inputListener) {
		super(context, android.R.style.Theme_Holo_Light_Dialog_MinWidth);
		this.context = context;
		IListener = inputListener;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.city_spinner_dialog);
		spinner1 = (Spinner) findViewById(R.id.spinner1);
		spinner2 = (Spinner) findViewById(R.id.spinner2);
		spinner3 = (Spinner) findViewById(R.id.spinner3);
		bt_ok = (Button) findViewById(R.id.bt_ok);
		bt_cancel = (Button) findViewById(R.id.bt_cancel);
		bt_ok.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String address = province.trim() + ""
						+ city.trim() + district.trim();
				if (province.equals(city)) {
					address = city.trim() + district.trim();
				}
				IListener.getText(address);
				dismiss();
			}
		});
		bt_cancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dismiss();
			}
		});
		spinner1.setPrompt(context.getResources().getString(R.string.province));
		spinner2.setPrompt(context.getResources().getString(R.string.city));
		spinner3.setPrompt(context.getResources().getString(R.string.district));
		initSpinner1();
	}

	// 定义回调接口
	public interface InputListener {
		void getText(String str);
	}

	public void initSpinner1() {
		dbm = new CityDBManager(context);
		dbm.openDatabase();
		db = dbm.getDatabase();
		List<MyListItem> list = new ArrayList<MyListItem>();

		try {
			String sql = "select * from province";
			Cursor cursor = db.rawQuery(sql, null);
			cursor.moveToFirst();
			while (!cursor.isLast()) {
				String code = cursor.getString(cursor.getColumnIndex("province_id"));
				String name = cursor
						.getString(cursor.getColumnIndex("province_name"));
				MyListItem myListItem = new MyListItem();
				myListItem.setName(name);
				myListItem.setPcode(code);
				list.add(myListItem);
				cursor.moveToNext();
			}
			String code = cursor.getString(cursor.getColumnIndex("province_id"));
			String name = cursor.getString(cursor.getColumnIndex("province_name"));
			MyListItem myListItem = new MyListItem();
			myListItem.setName(name);
			myListItem.setPcode(code);
			list.add(myListItem);
			cursor.close();
			dbm.closeDatabase();
			db.close();
		} catch (Exception e) {
			LogUtils.e(TAG, e.getMessage());
		}

		CityAdapter myAdapter = new CityAdapter(context, list);
		spinner1.setAdapter(myAdapter);
		spinner1.setOnItemSelectedListener(new SpinnerOnSelectedListener1());
	}

	public void initSpinner2(String pcode) {
		dbm = new CityDBManager(context);
		dbm.openDatabase();
		db = dbm.getDatabase();
		List<MyListItem> list = new ArrayList<MyListItem>();

		try {
			String sql = "select * from city where province_id = '" + pcode + "'";
			Cursor cursor = db.rawQuery(sql, null);
			cursor.moveToFirst();
			while (!cursor.isLast()) {
				String code = cursor.getString(cursor.getColumnIndex("city_id"));
                String name = cursor.getString(cursor.getColumnIndex("city_name"));
				MyListItem myListItem = new MyListItem();
				myListItem.setName(name);
				myListItem.setPcode(code);
				list.add(myListItem);
				cursor.moveToNext();
			}
			String code = cursor.getString(cursor.getColumnIndex("city_id"));
            String name = cursor.getString(cursor.getColumnIndex("city_name"));
			MyListItem myListItem = new MyListItem();
			myListItem.setName(name);
			myListItem.setPcode(code);
			list.add(myListItem);

			dbm.closeDatabase();
			db.close();
		} catch (Exception e) {
			LogUtils.e(TAG, e.getMessage());
		}

		CityAdapter myAdapter = new CityAdapter(context, list);
		spinner2.setAdapter(myAdapter);
		spinner2.setOnItemSelectedListener(new SpinnerOnSelectedListener2());
	}

	public void initSpinner3(String pcode) {
		dbm = new CityDBManager(context);
		dbm.openDatabase();
		db = dbm.getDatabase();
		List<MyListItem> list = new ArrayList<MyListItem>();

		try {
			String sql = "select * from district where city_id = '" + pcode + "'";
			Cursor cursor = db.rawQuery(sql, null);
			cursor.moveToFirst();
			while (!cursor.isLast()) {
				String code = cursor.getString(cursor.getColumnIndex("district_id"));
                String name = cursor.getString(cursor.getColumnIndex("district_name"));
				MyListItem myListItem = new MyListItem();
				myListItem.setName(name);
				myListItem.setPcode(code);
				list.add(myListItem);
				cursor.moveToNext();
			}
			String code = cursor.getString(cursor.getColumnIndex("district_id"));
            String name = cursor.getString(cursor.getColumnIndex("district_name"));
			MyListItem myListItem = new MyListItem();
			myListItem.setName(name);
			myListItem.setPcode(code);
			list.add(myListItem);

		} catch (Exception e) {
		}
		dbm.closeDatabase();
		db.close();

		CityAdapter myAdapter = new CityAdapter(context, list);
		spinner3.setAdapter(myAdapter);
		spinner3.setOnItemSelectedListener(new SpinnerOnSelectedListener3());
	}

	class SpinnerOnSelectedListener1 implements OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> adapterView, View view,
				int position, long id) {
			province = ((MyListItem) adapterView.getItemAtPosition(position))
					.getName();
			String pcode = ((MyListItem) adapterView
					.getItemAtPosition(position)).getPcode();
			initSpinner2(pcode);
//			initSpinner3(pcode);
		}

		public void onNothingSelected(AdapterView<?> adapterView) {
		}
	}

	class SpinnerOnSelectedListener2 implements OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> adapterView, View view,
				int position, long id) {
			city = ((MyListItem) adapterView.getItemAtPosition(position))
					.getName();
			String pcode = ((MyListItem) adapterView
					.getItemAtPosition(position)).getPcode();
			initSpinner3(pcode);
		}

		public void onNothingSelected(AdapterView<?> adapterView) {
		}
	}

	class SpinnerOnSelectedListener3 implements OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> adapterView, View view,
				int position, long id) {
			district = ((MyListItem) adapterView.getItemAtPosition(position))
					.getName();
		}

		public void onNothingSelected(AdapterView<?> adapterView) {
		}
	}
}
