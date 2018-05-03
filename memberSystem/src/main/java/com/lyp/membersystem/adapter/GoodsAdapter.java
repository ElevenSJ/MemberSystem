package com.lyp.membersystem.adapter;

import java.util.List;

import com.lyp.membersystem.R;
import com.lyp.membersystem.bean.GoodBean;
import com.lyp.membersystem.manager.ImageManager;
import com.lyp.membersystem.net.MessageContants;
import com.lyp.membersystem.net.NetProxyManager;
import com.lyp.membersystem.ui.ChooseCardActivity;
import com.lyp.membersystem.utils.Constant;
import com.lyp.membersystem.utils.ToastUtil;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class GoodsAdapter extends BaseAdapter {

	private List<GoodBean> goodsList;
	private Activity context;
	private Handler mHandler;

	public GoodsAdapter(List<GoodBean> goodsList, Activity context, Handler mHandler) {
		super();
		this.goodsList = goodsList;
		this.context = context;
		this.mHandler = mHandler;
	}

	@Override
	public int getCount() {
		if (goodsList != null) {
			return goodsList.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		if (goodsList != null) {
			return goodsList.get(position);
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
		final GoodBean goodBean = goodsList.get(position);
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.goods_item, null);
			viewHolder.good_iv = (ImageView) convertView.findViewById(R.id.good_iv);
			viewHolder.good_name = (TextView) convertView.findViewById(R.id.good_name);
			viewHolder.good_price = (TextView) convertView.findViewById(R.id.good_price);
			viewHolder.buyBtn = (Button) convertView.findViewById(R.id.buyBtn);
			viewHolder.buyBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (goodBean.getIsService().equals("1")) {
						SharedPreferences mSharedPreferences = context.getSharedPreferences(Constant.SHARED_PREFERENCE, Activity.MODE_PRIVATE);
						String tokenid = mSharedPreferences.getString(Constant.TOKEN_ID, "");
//						String saleId = mSharedPreferences.getString(Constant.ID, "");
						NetProxyManager.getInstance().toAddOrderByService(mHandler, tokenid, goodBean.getId());
					} else {
						Intent i = new Intent();
					    i.setClass(context, ChooseCardActivity.class);
					    i.putExtra("good_info", goodBean.getId() + "_" + goodBean.getPsaleprice() + "_1");
					    i.putExtra("customTexts", goodBean.getCustomTexts());
					    context.startActivity(i);
					}
				}
			});
			viewHolder.addShopCarBtn = (Button) convertView.findViewById(R.id.addShopCarBtn);
			viewHolder.addShopCarBtn.setVisibility(View.GONE);
			viewHolder.addShopCarBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					ToastUtil.showLong(context, goodBean.getPname() + "已经加入购物车");
				}
			});
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		ImageManager.loadImage(goodBean.getSmallPicUrl(), viewHolder.good_iv);
		viewHolder.good_name.setText(goodBean.getPname());
		viewHolder.good_price.setText(goodBean.getPsaleprice());
		return convertView;
	}

	public class ViewHolder {
		public ImageView good_iv;
		public TextView good_name;
		public TextView good_price;
		public Button buyBtn;
		public Button addShopCarBtn;
	}
}
