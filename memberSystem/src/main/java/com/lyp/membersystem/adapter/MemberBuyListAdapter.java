package com.lyp.membersystem.adapter;

import java.util.List;

import com.lyp.membersystem.R;
import com.lyp.membersystem.bean.MemberBuyListBean;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MemberBuyListAdapter extends BaseAdapter {

	private List<MemberBuyListBean> list;
	private Activity context;

	public MemberBuyListAdapter(List<MemberBuyListBean> servicesList, Activity context) {
		super();
		this.list = servicesList;
		this.context = context;
	}

	@Override
	public int getCount() {
		if (list != null) {
			return list.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		if (list != null) {
			return list.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		final MemberBuyListBean bean = list.get(position);
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.member_buy_list_item, null);
			viewHolder.icon = (ImageView) convertView.findViewById(R.id.icon);
			viewHolder.name = (TextView) convertView.findViewById(R.id.name);
			viewHolder.pay_date = (TextView) convertView.findViewById(R.id.pay_date);
			viewHolder.price = (TextView) convertView.findViewById(R.id.price);
			viewHolder.duration = (TextView) convertView.findViewById(R.id.duration);
			viewHolder.end_time = (TextView) convertView.findViewById(R.id.end_time);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.name.setText(bean.getName());
		viewHolder.pay_date.setText(bean.getPay_date());
		viewHolder.price.setText("￥" + bean.getPrice());
		if (bean.getDuration() != null) {
			viewHolder.duration.setText(bean.getDuration());
		}
		viewHolder.end_time.setText(bean.getEnd_time());
		return convertView;
	}

	public class ViewHolder {
		public ImageView icon;
		public TextView name;
		public TextView pay_date;
		public TextView price;
		public TextView duration;
		public TextView end_time;
	}
}
