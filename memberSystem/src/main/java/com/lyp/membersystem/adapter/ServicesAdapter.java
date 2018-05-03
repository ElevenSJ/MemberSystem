package com.lyp.membersystem.adapter;

import java.util.List;

import com.lyp.membersystem.R;
import com.lyp.membersystem.bean.ServiceBean;
import com.lyp.membersystem.manager.ImageManager;
import com.lyp.membersystem.ui.ServiceResultActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ServicesAdapter extends BaseAdapter {

	private List<ServiceBean> servicesList;
	private Activity context;
	private Handler mHandler;

	public ServicesAdapter(List<ServiceBean> servicesList, Activity context) {
		super();
		this.servicesList = servicesList;
		this.context = context;
	}

	@Override
	public int getCount() {
		if (servicesList != null) {
			return servicesList.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		if (servicesList != null) {
			return servicesList.get(position);
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
		final ServiceBean serviceBean = servicesList.get(position);
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.services_item, null);
			viewHolder.icon = (ImageView) convertView.findViewById(R.id.icon);
			viewHolder.name = (TextView) convertView.findViewById(R.id.name);
			viewHolder.description = (TextView) convertView.findViewById(R.id.description);
			viewHolder.apply = (Button) convertView.findViewById(R.id.apply);
			viewHolder.apply.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent i = new Intent();
					i.setClass(context, ServiceResultActivity.class);
					i.putExtra("id", serviceBean.getServiceId());
					i.putExtra("name", serviceBean.getName());
					context.startActivity(i);
				}
			});
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		ImageManager.loadImage(serviceBean.getIcon(), viewHolder.icon);
		viewHolder.name.setText(serviceBean.getName());
		viewHolder.description.setText(serviceBean.getSname());
		return convertView;
	}

	public class ViewHolder {
		public ImageView icon;
		public TextView name;
		public TextView description;
		public Button apply;
	}
}
