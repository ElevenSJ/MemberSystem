package com.lyp.membersystem.adapter;

import java.util.List;

import com.lyp.membersystem.R;
import com.lyp.membersystem.bean.SpecDateBean;
import com.lyp.membersystem.ui.SetSpecInfo;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SpecDateAdapter extends BaseAdapter {
	private List<SpecDateBean> list = null;
	private Context mContext;

	public SpecDateAdapter(Context mContext, List<SpecDateBean> list) {
		this.mContext = mContext;
		this.list = list;
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
			convertView = LayoutInflater.from(mContext).inflate(R.layout.specially_layout_item2, null);
			viewHolder = new ViewHolder();
			viewHolder.edit_spec = (ImageView) convertView.findViewById(R.id.edit_spec);
			viewHolder.delete_spec = (ImageView) convertView.findViewById(R.id.delete_spec);
			viewHolder.add_spec = (ImageView) convertView.findViewById(R.id.add_spec);
			viewHolder.spec_tv = ((TextView) convertView.findViewById(R.id.spec_tv));
			viewHolder.spec_date = ((TextView) convertView.findViewById(R.id.spec_date));
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		final SpecDateBean specDateBean = list.get(position);
		viewHolder.edit_spec.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
			}
		});
		viewHolder.delete_spec.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
			}
		});
		viewHolder.add_spec.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SpecDateBean specDateBean = new SpecDateBean();
				specDateBean.setFlag(0);
				specDateBean.setDate("");
				specDateBean.setRemark("");
				list.add(specDateBean);
				Intent i = new Intent();
				i.setClass(mContext, SetSpecInfo.class);
				mContext.startActivity(i);
			}
		});
		viewHolder.spec_tv.setText(specDateBean.getRemark());
		viewHolder.spec_date.setText(specDateBean.getDate());
		return convertView;

	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}

	public class ViewHolder {
		public TextView spec_tv;
		public TextView spec_date;
		public ImageView edit_spec;
		public ImageView delete_spec;
		public ImageView add_spec;
	}
}