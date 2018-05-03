package com.lyp.membersystem.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.lyp.membersystem.R;
import com.lyp.membersystem.manager.ImageManager;
import com.lyp.membersystem.view.CircleImageView;
import com.lyp.membersystem.view.contactsort.ContactSortModel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.SectionIndexer;
import android.widget.TextView;

@SuppressLint("UseSparseArrays")
public class MyCustomerAdapter extends BaseAdapter implements SectionIndexer {
	private List<ContactSortModel> list = null;
	private Context mContext;
	private boolean isMulChoice = false;// 是否多选
	// 选中的客户
	private List<ContactSortModel> selectContact = new ArrayList<ContactSortModel>();
	// 每个客户的Item view集合
	private HashMap<Integer, View> mView;
	// 用来记录是否需要显示checkBox
	public HashMap<Integer, Integer> visiblecheck;
	// 用来记录显示的checkBox是否是选中的状态
	public HashMap<Integer, Boolean> ischeck;
	// 用来计算选中的数目，以便于更新全选按钮
	public int isCheckNum = 0;

	public MyCustomerAdapter(Context mContext, List<ContactSortModel> list) {
		this.mContext = mContext;
		this.list = list;
		mView = new HashMap<Integer, View>();
		visiblecheck = new HashMap<Integer, Integer>();
		ischeck = new HashMap<Integer, Boolean>();
		for (int i = 0; i < list.size(); i++) {
			ischeck.put(i, false);
			visiblecheck.put(i, CheckBox.INVISIBLE);
		}
	}

	/**
	 * 当ListView数据发生变化时,调用此方法来更新ListView
	 *
	 * @param list
	 */
	public void updateListView(List<ContactSortModel> list) {
		this.list = list;
		notifyDataSetChanged();
	}

	public int getCount() {
		return this.list.size();
	}

	public Object getItem(int position) {
		return list.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View convertView, ViewGroup arg2) {
		View view = mView.get(position);
		if (view == null) {
			view = LayoutInflater.from(mContext).inflate(R.layout.item_contact, null);
			mView.put(position, view);
		}

		CircleImageView customer_avater = (CircleImageView) view.findViewById(R.id.customer_avater);
		CheckBox mCheckBox = (CheckBox) view.findViewById(R.id.customer_select);
		if (isMulChoice) {
			customer_avater.setVisibility(View.INVISIBLE);
			// mCheckBox.setVisibility(visiblecheck.get(position));
		} else {
			customer_avater.setVisibility(View.VISIBLE);
			ImageManager.loadImage(list.get(position).getAvater(), customer_avater, R.drawable.personal);
		}
		mCheckBox.setVisibility(visiblecheck.get(position));
		mCheckBox.setChecked(ischeck.get(position));
		TextView tvName= ((TextView) view.findViewById(R.id.customer_name));
		tvName.setText(this.list.get(position).getName());

		return view;

	}

	public List<ContactSortModel> getSelectContact() {
		return selectContact;
	}

	public void setSelectid(List<ContactSortModel> setSelectContact) {
		this.selectContact = setSelectContact;
	}

	public boolean isMulChoice() {
		return isMulChoice;
	}

	public void setMulChoice(boolean isMulChoice) {
		this.isMulChoice = isMulChoice;
	}

	public int getSectionForPosition(int position) {
		return list.get(position).getSortLetters().charAt(0);
	}

	public HashMap<Integer, View> getmView() {
		return mView;
	}

	public void setmView(HashMap<Integer, View> mView) {
		this.mView = mView;
	}

	public int getPositionForSection(int section) {
		for (int i = 0; i < getCount(); i++) {
			String sortStr = list.get(i).getSortLetters();
			char firstChar = sortStr.toUpperCase().charAt(0);
			if (firstChar == section) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public Object[] getSections() {
		return null;
	}

	@Override
	public void notifyDataSetChanged() {
		isCheckNum = 0;
		if (isMulChoice) {
			for (int i = 0; i < list.size(); i++) {
				ischeck.put(i, false);
				visiblecheck.put(i, CheckBox.VISIBLE);
			}
		} else {
			for (int i = 0; i < list.size(); i++) {
				ischeck.put(i, false);
				visiblecheck.put(i, CheckBox.INVISIBLE);
			}
		}
		super.notifyDataSetChanged();
	}
}