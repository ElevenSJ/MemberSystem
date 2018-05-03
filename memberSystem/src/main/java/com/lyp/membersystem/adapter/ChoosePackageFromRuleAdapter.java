package com.lyp.membersystem.adapter;

import java.util.HashMap;
import java.util.List;

import com.lyp.membersystem.R;
import com.lyp.membersystem.bean.PackageBean;
import com.lyp.membersystem.manager.ImageManager;
import com.lyp.membersystem.utils.ToastUtil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

@SuppressLint("UseSparseArrays")
public class ChoosePackageFromRuleAdapter extends BaseAdapter {
	private List<PackageBean> list = null;
	private Context mContext;
	// 选中的包装
	private int selectPackage;
	// 用来记录显示的checkBox是否是选中的状态
	public HashMap<Integer, Boolean> ischeck;
	private double goodPrice;
	private double cardPrice;
	private int customerNumber = 1;

	public ChoosePackageFromRuleAdapter(Context mContext, List<PackageBean> list) {
		this.mContext = mContext;
		this.list = list;
		ischeck = new HashMap<Integer, Boolean>();
		for (int i = 0; i < list.size(); i++) {
			if (i == 0) {
				ischeck.put(i, true);
				selectPackage = i;
			} else {
				ischeck.put(i, false);
			}
		}
	}

	/**
	 * 当ListView数据发生变化时,调用此方法来更新ListView
	 *
	 * @param list
	 */
	public void updateListView(List<PackageBean> list) {
		this.list = list;
		for (int i = 0; i < list.size(); i++) {
			if (i == 0) {
				ischeck.put(i, true);
				selectPackage = i;
			} else {
				ischeck.put(i, false);
			}
		}
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
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_choose_package, null);
			viewHolder = new ViewHolder();
			viewHolder.package_select = (ImageView) convertView.findViewById(R.id.package_select);
			viewHolder.package_img = (ImageView) convertView.findViewById(R.id.package_img);
			viewHolder.package_name = ((TextView) convertView.findViewById(R.id.package_name));
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		final PackageBean packageBean = list.get(position);

		viewHolder.package_select.setImageResource(
				ischeck.get(position) ? R.drawable.checkbox_circle_checked : R.drawable.checkbox_circle);
		viewHolder.package_select.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!ischeck.get(position)) {
					selectPackage = position;
					for (int i = 0; i < list.size(); i++) {
						if (i == position) {
							ischeck.put(i, true);
						} else {
							ischeck.put(i, false);
						}
					}
					notifyDataSetChanged();
				}
			}
		});
		ImageManager.loadDefaultImage(packageBean.getIcon(), viewHolder.package_img);
		viewHolder.package_name.setText(packageBean.getName());
		return convertView;

	}

	public int getSelectPackage() {
		return selectPackage;
	}

	public void setSelectPackage(int selectPackage) {
		this.selectPackage = selectPackage;
	}
	
	public double getGoodPrice() {
		return goodPrice;
	}

	public void setGoodPrice(double goodPrice) {
		this.goodPrice = goodPrice;
	}

	public double getCardPrice() {
		return cardPrice;
	}

	public void setCardPrice(double cardPrice) {
		this.cardPrice = cardPrice;
	}

	public int getCustomerNumber() {
		return customerNumber;
	}

	public void setCustomerNumber(int customerNumber) {
		this.customerNumber = customerNumber;
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}

	public class ViewHolder {
		public TextView package_name;
		public ImageView package_select;
		public ImageView package_img;
	}
}