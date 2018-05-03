package com.lyp.membersystem.adapter;

import java.text.DecimalFormat;
import java.util.ArrayList;

import com.lyp.membersystem.R;
import com.lyp.membersystem.bean.ServiceOrderBean;
import com.lyp.membersystem.manager.ImageManager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ServiceOrderAdapter extends BaseAdapter {

	private ArrayList<ServiceOrderBean> serviceOrderList;
	private Context context;

	public ServiceOrderAdapter(ArrayList<ServiceOrderBean> serviceCardList, Context context) {
		super();
		this.serviceOrderList = serviceCardList;
		this.context = context;
	}

	@Override
	public int getCount() {
		if (serviceOrderList != null)
			return serviceOrderList.size();
		return 0;
	}

	@Override
	public Object getItem(int position) {
		if (serviceOrderList != null)
			return serviceOrderList.get(position);
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
			convertView = LayoutInflater.from(context).inflate(R.layout.service_order_item, null);
			viewHolder.icon = (ImageView) convertView.findViewById(R.id.icon);
			viewHolder.price = (TextView) convertView.findViewById(R.id.price);
			viewHolder.service_applay_no = (TextView) convertView.findViewById(R.id.service_applay_no);
			viewHolder.name = (TextView) convertView.findViewById(R.id.name);
			viewHolder.order_state = (TextView) convertView.findViewById(R.id.order_state);
			viewHolder.reserve_time = (TextView) convertView.findViewById(R.id.reserve_time);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		ServiceOrderBean serviceOrderBean = serviceOrderList.get(position);
		if (serviceOrderBean.getIcon() != null) {
			ImageManager.loadImage(serviceOrderBean.getIcon(), viewHolder.icon, R.drawable.default_image);
		}
		viewHolder.name.setText(serviceOrderBean.getProductName());
		viewHolder.service_applay_no.setText(serviceOrderBean.getOrderId());
		DecimalFormat df = new DecimalFormat("0.0");
		viewHolder.price.setText(df.format(serviceOrderBean.getOrderPrice()));
		if (serviceOrderBean.getExpiryDate() == null) {
			viewHolder.reserve_time.setText("2099-12-12");
		} else {
			viewHolder.reserve_time.setText(serviceOrderBean.getReserve_time());
		}
		String orderState = serviceOrderBean.getOrderState();
		if (orderState.equals("300")) {
			viewHolder.order_state.setText("已取消");
			viewHolder.order_state.setTextColor(context.getResources().getColor(R.color.item_second_font_color));
			viewHolder.order_state.setBackgroundResource(R.drawable.btn_sm_grey_outline);

		} else if (orderState.equals("100")) {
			viewHolder.order_state.setText("已付款");
			viewHolder.order_state.setTextColor(context.getResources().getColor(R.color.main_bg_color));
			viewHolder.order_state.setBackgroundResource(R.drawable.btn_sm_green_outline);
		} else if (orderState.equals("200")) {
			viewHolder.order_state.setText("已完成");
			viewHolder.order_state.setTextColor(context.getResources().getColor(R.color.item_second_font_color));
			viewHolder.order_state.setBackgroundResource(R.drawable.btn_sm_grey_outline);
		} else if (orderState.equals("0")) {
			viewHolder.order_state.setText("未付款");
			viewHolder.order_state.setTextColor(context.getResources().getColor(R.color.item_second_font_color));
			viewHolder.order_state.setBackgroundResource(R.drawable.btn_sm_grey_outline);
		} else {
			viewHolder.order_state.setText("已取消");
			viewHolder.order_state.setTextColor(context.getResources().getColor(R.color.item_second_font_color));
			viewHolder.order_state.setBackgroundResource(R.drawable.btn_sm_grey_outline);
		}
		return convertView;
	}

	public class ViewHolder {
		public ImageView icon;
		public TextView service_applay_no;
		public TextView price;
		public TextView reserve_time;
		public TextView name;
		public TextView order_state;
	}
}
