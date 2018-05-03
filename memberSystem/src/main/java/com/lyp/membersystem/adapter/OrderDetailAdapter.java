package com.lyp.membersystem.adapter;

import java.util.List;

import com.lyp.membersystem.R;
import com.lyp.membersystem.bean.GoodBean;
import com.lyp.membersystem.bean.OrderDetailBean;
import com.lyp.membersystem.manager.ImageManager;
import com.lyp.membersystem.ui.GoodDetailActivity;
import com.lyp.membersystem.ui.GoodsActivity;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class OrderDetailAdapter extends BaseAdapter {

	private List<OrderDetailBean> orderDetailList;
	private Activity context;

	public OrderDetailAdapter(List<OrderDetailBean> orderDetailList, Activity context, GoodBean goodBean) {
		super();
		this.orderDetailList = orderDetailList;
		this.context = context;
	}

	@Override
	public int getCount() {
		if (orderDetailList != null)
			return orderDetailList.size();
		return 0;
	}

	@Override
	public Object getItem(int position) {
		if (orderDetailList != null)
			return orderDetailList.get(position);
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
			convertView = LayoutInflater.from(context).inflate(R.layout.item_order_detail, null);
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
		final OrderDetailBean orderDetailBean = orderDetailList.get(position);
		viewHolder.customer_name.setText(orderDetailBean.getCustomer().getName());
		viewHolder.good_name.setText(orderDetailBean.getGoodBean().getPname());
		viewHolder.good_price.setText("￥" + orderDetailBean.getGoodBean().getPiprice());
		if (orderDetailBean.getCustomer().getAvater() != null) {
			ImageManager.loadImage(orderDetailBean.getCustomer().getAvater(), viewHolder.customer_avater, R.drawable.personal);
		}
		ImageManager.loadImage(orderDetailBean.getGoodBean().getPicUrls(), viewHolder.good_iv);
		viewHolder.good_iv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				GoodBean goodBean = orderDetailBean.getGoodBean();
				intent.putExtra("id", goodBean.getId());
				intent.putExtra("isService", goodBean.getIsService());
				intent.putExtra("customTexts", goodBean.getCustomTexts());
				intent.putExtra("isShowBuy", "1");
				intent.setClass(context, GoodDetailActivity.class);
				context.startActivity(intent);					
			}
		});
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
