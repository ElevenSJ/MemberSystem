package com.lyp.membersystem.adapter;

import java.util.ArrayList;

import com.lyp.membersystem.R;
import com.lyp.membersystem.bean.ProductTypeBean;
import com.lyp.membersystem.manager.ImageManager;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MallFragmentAdapter extends BaseAdapter {

	private ArrayList<ProductTypeBean> productTypes;
	private Context context;

	public MallFragmentAdapter(ArrayList<ProductTypeBean> productTypes, Context context) {
		super();
		this.productTypes = productTypes;
		this.context = context;
	}

	@Override
	public int getCount() {
		if (productTypes != null)
			return productTypes.size();
		return 0;
	}

	@Override
	public Object getItem(int position) {
		if (productTypes != null)
			return productTypes.get(position);
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
			convertView = LayoutInflater.from(context).inflate(R.layout.frament_item, null);
			viewHolder.fragment_item_iv = (ImageView) convertView.findViewById(R.id.fragment_item_iv);
			viewHolder.fragment_item_tv = (TextView) convertView.findViewById(R.id.fragment_item_tv);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		ProductTypeBean productTypeBean = productTypes.get(position);
		viewHolder.fragment_item_tv.setText(productTypeBean.getName());
		if (productTypeBean.getIcon() != null) {
			ImageManager.loadImage(productTypeBean.getIcon(), viewHolder.fragment_item_iv);
		}
		return convertView;
	}

	public class ViewHolder {
		public TextView fragment_item_tv;
		public ImageView fragment_item_iv;
	}
}
