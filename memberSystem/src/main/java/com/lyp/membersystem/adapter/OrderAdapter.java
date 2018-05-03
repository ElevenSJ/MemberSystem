package com.lyp.membersystem.adapter;

import java.text.DecimalFormat;
import java.util.List;

import com.lyp.membersystem.R;
import com.lyp.membersystem.bean.GoodBean;
import com.lyp.membersystem.bean.OrderBean;
import com.lyp.membersystem.manager.ImageManager;
import com.lyp.membersystem.ui.GoodDetailActivity;
import com.lyp.membersystem.ui.OrderActivity;
import com.lyp.membersystem.ui.OrderDetailActivity;
import com.lyp.membersystem.view.contactsort.ContactSortModel;
import com.nodeprogress.nodeprogress.ExpressActivity;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class OrderAdapter extends BaseAdapter {

	private List<OrderBean> orderList;
	private OrderActivity activity;

	public OrderAdapter(List<OrderBean> orderList, OrderActivity context) {
		super();
		this.orderList = orderList;
		this.activity = context;
	}

	@Override
	public int getCount() {
		if (orderList != null) {
			return orderList.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		if (orderList != null) {
			return orderList.get(position);
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
		final OrderBean orderBean = orderList.get(position);
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(activity).inflate(R.layout.order_item, null);
			viewHolder.order_no = (TextView) convertView.findViewById(R.id.order_no);
			viewHolder.order_date = (TextView) convertView.findViewById(R.id.order_date);
			viewHolder.civ_1 = (ImageView) convertView.findViewById(R.id.civ_1);
			viewHolder.civ_2 = (ImageView) convertView.findViewById(R.id.civ_2);
			viewHolder.civ_3 = (ImageView) convertView.findViewById(R.id.civ_3);
			viewHolder.civ_4 = (ImageView) convertView.findViewById(R.id.civ_4);
			viewHolder.civ_5 = (ImageView) convertView.findViewById(R.id.civ_5);
			viewHolder.customer_num = (TextView) convertView.findViewById(R.id.customer_num);
			viewHolder.good_iv1 = (ImageView) convertView.findViewById(R.id.good_iv1);
			viewHolder.good_iv2 = (ImageView) convertView.findViewById(R.id.good_iv2);
			viewHolder.good_iv3 = (ImageView) convertView.findViewById(R.id.good_iv3);
			viewHolder.order_name = (TextView) convertView.findViewById(R.id.order_name);
			viewHolder.order_price = (TextView) convertView.findViewById(R.id.order_price);
			viewHolder.order_detail = (TextView) convertView.findViewById(R.id.order_detail);
			viewHolder.order_state = (TextView) convertView.findViewById(R.id.order_state);
			viewHolder.order_detail.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent i = new Intent();
					i.setClass(activity, OrderDetailActivity.class);
					i.putExtra("order_id", orderBean.getOrderId());
					activity.startActivity(i);
				}
			});
			viewHolder.order_state.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

				}
			});
			viewHolder.express = (TextView) convertView.findViewById(R.id.express);
			viewHolder.express.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent i = new Intent();
					i.setClass(activity, ExpressActivity.class);
					i.putExtra("logisticsNo", orderBean.getLogisticsno());
					activity.startActivity(i);
				}
			});
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.order_no.setText("No." + orderBean.getOrderId());
		viewHolder.order_date.setText(orderBean.getOrderDate());
		viewHolder.civ_1.setVisibility(View.GONE);
		viewHolder.civ_2.setVisibility(View.GONE);
		viewHolder.civ_3.setVisibility(View.GONE);
		viewHolder.civ_4.setVisibility(View.GONE);
		viewHolder.civ_5.setVisibility(View.GONE);
		List<ContactSortModel> customerList = orderBean.getCustomerList();
		int contactNum = customerList.size();
		for (int i = 0; i < contactNum; i++) {
			ContactSortModel contact = customerList.get(i);
			switch (i) {
			case 0:
				viewHolder.civ_1.setVisibility(View.VISIBLE);
				if (contact.getAvater() != null) {
					ImageManager.loadImage(contact.getAvater(), viewHolder.civ_1, R.drawable.personal);
				}
				break;
			case 1:
				viewHolder.civ_2.setVisibility(View.VISIBLE);
				if (contact.getAvater() != null) {
					ImageManager.loadImage(contact.getAvater(), viewHolder.civ_2, R.drawable.personal);
				}
				break;
			case 2:
				viewHolder.civ_3.setVisibility(View.VISIBLE);
				if (contact.getAvater() != null) {
					ImageManager.loadImage(contact.getAvater(), viewHolder.civ_3, R.drawable.personal);
				}
				break;
			case 3:
				viewHolder.civ_4.setVisibility(View.VISIBLE);
				if (contact.getAvater() != null) {
					ImageManager.loadImage(contact.getAvater(), viewHolder.civ_4, R.drawable.personal);
				}
				break;
			case 4:
				viewHolder.civ_5.setVisibility(View.VISIBLE);
				if (contact.getAvater() != null) {
					ImageManager.loadImage(contact.getAvater(), viewHolder.civ_5, R.drawable.personal);
				}
				break;
			default:
				break;
			}
		}
		viewHolder.customer_num.setText("" + contactNum);
		viewHolder.good_iv1.setVisibility(View.GONE);
		viewHolder.good_iv2.setVisibility(View.GONE);
		viewHolder.good_iv3.setVisibility(View.GONE);
		List<GoodBean> goodlist = orderBean.getGoodlist();
		int goodNum = goodlist.size();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < goodNum; i++) {
			final GoodBean goodBean = goodlist.get(i);
			switch (i) {
			case 0:
				viewHolder.good_iv1.setVisibility(View.VISIBLE);
				viewHolder.good_iv1.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						Intent intent = new Intent();
						intent.putExtra("id", goodBean.getId());
						intent.putExtra("isService", goodBean.getIsService());
						intent.putExtra("customTexts", goodBean.getCustomTexts());
						intent.putExtra("isShowBuy", "1");
						intent.setClass(activity, GoodDetailActivity.class);
						activity.startActivity(intent);					
					}
				});
				sb.append(goodBean.getPname());
				if (goodBean.getPicUrls() != null) {
					ImageManager.loadImage(goodBean.getPicUrls(), viewHolder.good_iv1);
				}
				break;
			case 1:
				viewHolder.good_iv2.setVisibility(View.VISIBLE);
				sb.append(" 等" + goodNum + "件商品");
				if (goodBean.getPicUrls() != null) {
					ImageManager.loadImage(goodBean.getPicUrls(), viewHolder.good_iv2);
				}
				break;
			case 2:
				viewHolder.good_iv3.setVisibility(View.VISIBLE);
				if (goodBean.getPicUrls() != null) {
					ImageManager.loadImage(goodBean.getPicUrls(), viewHolder.good_iv3);
				}
				break;
			}
		}
		viewHolder.order_name.setText(sb.toString());
		DecimalFormat df = new DecimalFormat("0.0"); 
		viewHolder.order_price.setText("共计 ￥" + df.format(orderBean.getOrderPrice()));
		String orderState = orderBean.getOrderState();
		if (orderState.equals("0")) {
//			viewHolder.order_state.setText("去支付");
//			viewHolder.order_state.setTextColor(activity.getResources().getColor(R.color.main_bg_color));
//			viewHolder.order_state.setBackgroundResource(R.drawable.btn_sm_green_outline);
//			viewHolder.order_state.setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					Intent i = new Intent();
//			        i.setClass(activity, PayActivity.class);
//			        i.putExtra("order_id", orderBean.getOrderId());
//			        activity.startActivityForResult(i, 0x1314);
//				}
//			});
			viewHolder.order_state.setText("未支付");
			viewHolder.order_state.setTextColor(activity.getResources().getColor(R.color.white));
			viewHolder.order_state.setBackgroundResource(R.drawable.btn_grey);
			viewHolder.order_state.setOnClickListener(null);
			 
		} else if (orderState.equals("1")) {
			viewHolder.order_state.setText("已付款");
			viewHolder.order_state.setTextColor(activity.getResources().getColor(R.color.white));
			viewHolder.order_state.setBackgroundResource(R.drawable.btn_grey);
			viewHolder.order_state.setOnClickListener(null);
		} else if (orderState.equals("2")) {
			viewHolder.order_state.setText("已发货");
			viewHolder.order_state.setTextColor(activity.getResources().getColor(R.color.white));
			viewHolder.order_state.setBackgroundResource(R.drawable.btn_grey);
			viewHolder.order_state.setOnClickListener(null);
		} else if (orderState.equals("3")) {
			viewHolder.order_state.setText("已取消");
			viewHolder.order_state.setTextColor(activity.getResources().getColor(R.color.white));
			viewHolder.order_state.setBackgroundResource(R.drawable.btn_grey);
			viewHolder.order_state.setOnClickListener(null);
		} else if (orderState.equals("100")) {
			viewHolder.order_state.setText("支付中");
			viewHolder.order_state.setTextColor(activity.getResources().getColor(R.color.white));
			viewHolder.order_state.setBackgroundResource(R.drawable.btn_grey);
			viewHolder.order_state.setOnClickListener(null);
		} else {
			viewHolder.order_state.setText("已取消");
			viewHolder.order_state.setTextColor(activity.getResources().getColor(R.color.white));
			viewHolder.order_state.setBackgroundResource(R.drawable.btn_grey);
			viewHolder.order_state.setOnClickListener(null);
		}
		return convertView;
	}

	public class ViewHolder {
		public TextView order_no;
		public TextView order_date;
		public ImageView civ_1;
		public ImageView civ_2;
		public ImageView civ_3;
		public ImageView civ_4;
		public ImageView civ_5;
		public TextView customer_num;
		public ImageView good_iv1;
		public ImageView good_iv2;
		public ImageView good_iv3;
		public TextView order_name;
		public TextView order_price;
		public TextView order_detail;
		public TextView order_state;
		public TextView express;
	}
}
