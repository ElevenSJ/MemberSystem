package com.lyp.membersystem.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.lyp.membersystem.R;
import com.lyp.membersystem.bean.ShopCartBean;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

@SuppressLint("UseSparseArrays")
public class ShopCartAdapter extends BaseAdapter implements SectionIndexer {
	private List<ShopCartBean> list = null;
	private Context mContext;
	// 选中的客户
	private List<ShopCartBean> selectContact = new ArrayList<ShopCartBean>();
	// 每个客户的Item view集合
	private HashMap<Integer, View> mView;
	// 用来记录显示的checkBox是否是选中的状态
	public HashMap<Integer, Boolean> ischeck;
	// 用来计算选中的数目，以便于更新全选按钮
	public int isCheckNum = 0;

	public ShopCartAdapter(Context mContext, List<ShopCartBean> list) {
		this.mContext = mContext;
		this.list = list;
		mView = new HashMap<Integer, View>();
		ischeck = new HashMap<Integer, Boolean>();
		for (int i = 0; i < list.size(); i++) {
			ischeck.put(i, false);
		}
	}

	/**
	 * 当ListView数据发生变化时,调用此方法来更新ListView
	 *
	 * @param list
	 */
	public void updateListView(List<ShopCartBean> list) {
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
			view = LayoutInflater.from(mContext).inflate(R.layout.item_choose_contact, null);
			mView.put(position, view);
		}

		ImageView customer_avater = (ImageView) view.findViewById(R.id.customer_avater);
		CheckBox mCheckBox = (CheckBox) view.findViewById(R.id.customer_select);
		mCheckBox.setChecked(ischeck.get(position));
		TextView tvName= ((TextView) view.findViewById(R.id.customer_name));
		tvName.setText(this.list.get(position).getName());

		return view;

	}

	public List<ShopCartBean> getSelectContact() {
		return selectContact;
	}

	public void setSelectid(List<ShopCartBean> setSelectContact) {
		this.selectContact = setSelectContact;
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
		super.notifyDataSetChanged();
	}
}