package com.lyp.membersystem.adapter;

import java.util.ArrayList;
import java.util.List;

import com.lyp.membersystem.R;
import com.lyp.membersystem.bean.GoodBean;
import com.lyp.membersystem.bean.OrderDetailBean;
import com.lyp.membersystem.bean.SelectGoodBean;
import com.lyp.membersystem.manager.ImageManager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ConfirmChooseOrderAdapter extends BaseAdapter {

	private ArrayList<SelectGoodBean> mSelectGoodlist;
	private Context context;

	public ConfirmChooseOrderAdapter(ArrayList<SelectGoodBean> selectGoodlist, Context context, GoodBean goodBean) {
		super();
		this.mSelectGoodlist = selectGoodlist;
		this.context = context;
	}

	@Override
	public int getCount() {
		if (mSelectGoodlist != null)
			return mSelectGoodlist.size();
		return 0;
	}

	@Override
	public Object getItem(int position) {
		if (mSelectGoodlist != null)
			return mSelectGoodlist.get(position);
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.item_confrim_choose_order, null);
			viewHolder = new ViewHolder();
			viewHolder.customer_name = (TextView) convertView.findViewById(R.id.customer_name);
			viewHolder.good_name = (TextView) convertView.findViewById(R.id.good_name);
			viewHolder.good_price = (TextView) convertView.findViewById(R.id.good_price);
			viewHolder.customer_avater = (ImageView) convertView.findViewById(R.id.customer_avater);
			viewHolder.good_iv = (ImageView) convertView.findViewById(R.id.good_iv);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		SelectGoodBean orderDetailBean = mSelectGoodlist.get(position);
		viewHolder.customer_name.setText(orderDetailBean.getCname());
		viewHolder.good_name.setText(orderDetailBean.getPname());
		viewHolder.good_price.setText("￥" + orderDetailBean.getPprice());
		if (orderDetailBean.getAvater() != null) {
			ImageManager.loadImage(orderDetailBean.getAvater(), viewHolder.customer_avater);
		}
		ImageManager.loadImage(orderDetailBean.getpUrl(), viewHolder.good_iv);
		return convertView;
	}

	public class ViewHolder {
		public ImageView customer_avater;
		public TextView customer_name;
		public ImageView good_iv;
		public TextView good_name;
		public TextView good_price;
	}

}
