package com.lyp.membersystem.adapter;

import java.util.ArrayList;

import com.lyp.membersystem.R;
import com.lyp.membersystem.bean.MemberCardBean;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ServiceCardFragAdapter extends BaseAdapter {

	private ArrayList<MemberCardBean> serviceCardList;
	private Context context;

	public ServiceCardFragAdapter(ArrayList<MemberCardBean> serviceCardList, Context context) {
		super();
		this.serviceCardList = serviceCardList;
		this.context = context;
	}

	@Override
	public int getCount() {
		if (serviceCardList != null)
			return serviceCardList.size();
		return 0;
	}

	@Override
	public Object getItem(int position) {
		if (serviceCardList != null)
			return serviceCardList.get(position);
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.service_card_frag_item, null);
			viewHolder.cash_card_money = (TextView) convertView.findViewById(R.id.cash_card_money);
			viewHolder.expiry_date = (TextView) convertView.findViewById(R.id.expiry_date);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		MemberCardBean memberCardBean = serviceCardList.get(position);
		viewHolder.cash_card_money.setText(memberCardBean.getBalance());
		if (memberCardBean.getExpiryDate() == null) {
//			viewHolder.expiry_date.setText("2088");
		} else {
		    viewHolder.expiry_date.setText(memberCardBean.getExpiryDate());
		}
		return convertView;
	}

	public class ViewHolder {
		public TextView cash_card_money;
		public TextView expiry_date;
	}
}
