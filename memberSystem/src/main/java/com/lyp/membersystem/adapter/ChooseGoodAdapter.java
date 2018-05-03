package com.lyp.membersystem.adapter;

import java.util.ArrayList;
import java.util.List;

import com.lyp.membersystem.R;
import com.lyp.membersystem.bean.NoticeBean;
import com.lyp.membersystem.bean.SelectGoodBean;
import com.lyp.membersystem.log.LogUtils;
import com.lyp.membersystem.manager.ImageManager;
import com.lyp.membersystem.ui.GoodDetailActivity;
import com.lyp.membersystem.view.contactsort.ContactSortModel;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ChooseGoodAdapter extends BaseAdapter {

	private List<ContactSortModel> contactList;
	private Activity context;
	private NoticeBean mNoticeBean;
	private ArrayList<SelectGoodBean> mSelectGoodlist;

	public ChooseGoodAdapter(List<ContactSortModel> orderDetailList, Activity context, NoticeBean noticeBean) {
		super();
		this.contactList = orderDetailList;
		this.context = context;
		this.mNoticeBean = noticeBean;
		mSelectGoodlist = new ArrayList<SelectGoodBean>();
		List<String> goodlist = mNoticeBean.getGoodlist();
		for (int i = 0; i < contactList.size(); i++) {
			SelectGoodBean selectGoodBean = new SelectGoodBean();
			ContactSortModel contact = contactList.get(i);
			selectGoodBean.setCid(contact.getId());
			selectGoodBean.setAvater(contact.getAvater());
			selectGoodBean.setCname(contact.getName());
			selectGoodBean.setPid(goodlist.get(0).split(",")[0]);
			selectGoodBean.setPprice(goodlist.get(0).split(",")[1]);
			selectGoodBean.setpUrl(goodlist.get(0).split(",")[2]);
			selectGoodBean.setPname(goodlist.get(0).split(",")[3]);
			selectGoodBean.setPack(mNoticeBean.getPack());
			selectGoodBean.setCard(mNoticeBean.getCard());
			mSelectGoodlist.add(selectGoodBean);
		}
	}

	@Override
	public int getCount() {
		if (contactList != null)
			return contactList.size();
		return 0;
	}

	@Override
	public Object getItem(int position) {
		if (contactList != null)
			return contactList.get(position);
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.item_choose_good, null);
			viewHolder = new ViewHolder();
			viewHolder.customer_name = (TextView) convertView.findViewById(R.id.customer_name);
			viewHolder.customer_avater = (ImageView) convertView.findViewById(R.id.customer_avater);
			viewHolder.good_iv1 = (ImageView) convertView.findViewById(R.id.good_iv1);
			viewHolder.good_iv2 = (ImageView) convertView.findViewById(R.id.good_iv2);
			viewHolder.good_iv2.setVisibility(View.GONE);
			viewHolder.good_iv3 = (ImageView) convertView.findViewById(R.id.good_iv3);
			viewHolder.good_iv3.setVisibility(View.GONE);
			viewHolder.good_one = (TextView) convertView.findViewById(R.id.good_one);
			viewHolder.good_two = (TextView) convertView.findViewById(R.id.good_two);
			viewHolder.good_two.setVisibility(View.GONE);
			viewHolder.good_three = (TextView) convertView.findViewById(R.id.good_three);
			viewHolder.good_three.setVisibility(View.GONE);
			viewHolder.good_one_name = (TextView) convertView.findViewById(R.id.good_one_name);
			viewHolder.good_two_name = (TextView) convertView.findViewById(R.id.good_two_name);
			viewHolder.good_two_name.setVisibility(View.GONE);
			viewHolder.good_three_name = (TextView) convertView.findViewById(R.id.good_three_name);
			viewHolder.good_three_name.setVisibility(View.GONE);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		ContactSortModel contact = contactList.get(position);
		final SelectGoodBean selectGoodBean = mSelectGoodlist.get(position);
		// final SelectGoodBean selectGoodBean = new SelectGoodBean();
		// selectGoodBean.setCid(contact.getId());
		// selectGoodBean.setAvater(contact.getAvater());
		// selectGoodBean.setCname(contact.getName());
		viewHolder.customer_name.setText(contact.getName());
		if (contact.getAvater() != null) {
			ImageManager.loadImage(contact.getAvater(), viewHolder.customer_avater);
		}
		final List<String> goodlist = mNoticeBean.getGoodlist();
		// selectGoodBean.setPid(goodlist.get(0).split(",")[0]);
		// selectGoodBean.setPprice(goodlist.get(0).split(",")[1]);
		// selectGoodBean.setpUrl(goodlist.get(0).split(",")[2]);
		// selectGoodBean.setPname(goodlist.get(0).split(",")[3]);
		// selectGoodBean.setPack(mNoticeBean.getPack());
		// selectGoodBean.setCard(mNoticeBean.getCard());
		int size = goodlist.size();
		if (size == 2) {
			viewHolder.good_iv2.setVisibility(View.VISIBLE);
			viewHolder.good_two.setVisibility(View.VISIBLE);
			viewHolder.good_two_name.setVisibility(View.VISIBLE);
			ImageManager.loadImage(goodlist.get(1).split(",")[2], viewHolder.good_iv2);
			viewHolder.good_two_name.setText(goodlist.get(1).split(",")[3]);
		} else if (size >= 3) {
			viewHolder.good_iv2.setVisibility(View.VISIBLE);
			viewHolder.good_two.setVisibility(View.VISIBLE);
			viewHolder.good_two_name.setVisibility(View.VISIBLE);
			viewHolder.good_iv3.setVisibility(View.VISIBLE);
			viewHolder.good_three.setVisibility(View.VISIBLE);
			viewHolder.good_three_name.setVisibility(View.VISIBLE);
			ImageManager.loadImage(goodlist.get(1).split(",")[2], viewHolder.good_iv2);
			ImageManager.loadImage(goodlist.get(2).split(",")[2], viewHolder.good_iv3);
			viewHolder.good_two_name.setText(goodlist.get(1).split(",")[3]);
			viewHolder.good_three_name.setText(goodlist.get(2).split(",")[3]);
		}

		ImageManager.loadImage(goodlist.get(0).split(",")[2], viewHolder.good_iv1);

		viewHolder.good_one_name.setText(goodlist.get(0).split(",")[3]);

		viewHolder.good_iv1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra("id", goodlist.get(0).split(",")[0]);
				intent.putExtra("isService", "0");
				// intent.putExtra("customTexts", null);
				intent.putExtra("isShowBuy", "1");
				intent.setClass(context, GoodDetailActivity.class);
				context.startActivity(intent);
			}
		});

		viewHolder.good_iv2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra("id", goodlist.get(1).split(",")[0]);
				intent.putExtra("isService", "0");
				// intent.putExtra("customTexts", null);
				intent.putExtra("isShowBuy", "1");
				intent.setClass(context, GoodDetailActivity.class);
				context.startActivity(intent);
			}
		});

		viewHolder.good_iv3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra("id", goodlist.get(2).split(",")[0]);
				intent.putExtra("isService", "0");
				// intent.putExtra("customTexts", null);
				intent.putExtra("isShowBuy", "1");
				intent.setClass(context, GoodDetailActivity.class);
				context.startActivity(intent);
			}
		});

		final TextView tv1 = viewHolder.good_one;
		final TextView tv2 = viewHolder.good_two;
		final TextView tv3 = viewHolder.good_three;
		if (selectGoodBean.getPid().equals(goodlist.get(0).split(",")[0])) {
			tv1.setBackgroundResource(R.drawable.btn_sm_green);
			tv2.setBackgroundResource(R.drawable.btn_grey);
			tv3.setBackgroundResource(R.drawable.btn_grey);
			tv1.setText("已选");
			tv2.setText("未选");
			tv3.setText("未选");
		} else if (selectGoodBean.getPid().equals(goodlist.get(1).split(",")[0])) {
			tv1.setBackgroundResource(R.drawable.btn_grey);
			tv2.setBackgroundResource(R.drawable.btn_sm_green);
			tv3.setBackgroundResource(R.drawable.btn_grey);
			tv1.setText("未选");
			tv2.setText("已选");
			tv3.setText("未选");
		} else if (selectGoodBean.getPid().equals(goodlist.get(2).split(",")[0])) {
			tv1.setBackgroundResource(R.drawable.btn_grey);
			tv2.setBackgroundResource(R.drawable.btn_grey);
			tv3.setBackgroundResource(R.drawable.btn_sm_green);
			tv1.setText("未选");
			tv2.setText("未选");
			tv3.setText("已选");
		}
		viewHolder.good_one.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				tv1.setBackgroundResource(R.drawable.btn_sm_green);
				tv2.setBackgroundResource(R.drawable.btn_grey);
				tv3.setBackgroundResource(R.drawable.btn_grey);
				tv1.setText("已选");
				tv2.setText("未选");
				tv3.setText("未选");
				selectGoodBean.setPid(goodlist.get(0).split(",")[0]);
				selectGoodBean.setPprice(goodlist.get(0).split(",")[1]);
				selectGoodBean.setpUrl(goodlist.get(0).split(",")[2]);
				selectGoodBean.setPname(goodlist.get(0).split(",")[3]);
			}
		});
		viewHolder.good_two.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				tv1.setBackgroundResource(R.drawable.btn_grey);
				tv2.setBackgroundResource(R.drawable.btn_sm_green);
				tv3.setBackgroundResource(R.drawable.btn_grey);
				tv1.setText("未选");
				tv2.setText("已选");
				tv3.setText("未选");
				selectGoodBean.setPid(goodlist.get(1).split(",")[0]);
				selectGoodBean.setPprice(goodlist.get(1).split(",")[1]);
				selectGoodBean.setpUrl(goodlist.get(1).split(",")[2]);
				selectGoodBean.setPname(goodlist.get(1).split(",")[3]);
			}
		});
		viewHolder.good_three.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				tv1.setBackgroundResource(R.drawable.btn_grey);
				tv2.setBackgroundResource(R.drawable.btn_grey);
				tv3.setBackgroundResource(R.drawable.btn_sm_green);
				tv1.setText("未选");
				tv2.setText("未选");
				tv3.setText("已选");
				selectGoodBean.setPid(goodlist.get(2).split(",")[0]);
				selectGoodBean.setPprice(goodlist.get(2).split(",")[1]);
				selectGoodBean.setpUrl(goodlist.get(2).split(",")[2]);
				selectGoodBean.setPname(goodlist.get(2).split(",")[3]);
			}
		});
		// if (!mSelectGoodlist.contains(selectGoodBean)) {
		// mSelectGoodlist.add(selectGoodBean);
		// LogUtils.d("lyp", "add selectGoodBean: " + selectGoodBean.getCid());
		// }
		return convertView;
	}

	public class ViewHolder {
		public ImageView customer_avater;
		public TextView customer_name;
		public ImageView good_iv1;
		public ImageView good_iv2;
		public ImageView good_iv3;
		public TextView good_one;
		public TextView good_two;
		public TextView good_three;
		public TextView good_one_name;
		public TextView good_two_name;
		public TextView good_three_name;
	}

	public ArrayList<SelectGoodBean> getmSelectGoodlist() {
		return mSelectGoodlist;
	}

	public void setmSelectGoodlist(ArrayList<SelectGoodBean> mSelectGoodlist) {
		this.mSelectGoodlist = mSelectGoodlist;
	}
}
