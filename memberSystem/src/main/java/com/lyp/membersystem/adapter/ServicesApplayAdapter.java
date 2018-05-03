package com.lyp.membersystem.adapter;

import java.util.List;

import com.lyp.membersystem.R;
import com.lyp.membersystem.bean.ServiceBean;
import com.lyp.membersystem.manager.ImageManager;
import com.lyp.membersystem.net.NetProxyManager;
import com.lyp.membersystem.utils.Constant;

import android.app.Activity;
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

public class ServicesApplayAdapter extends BaseAdapter {

	private List<ServiceBean> servicesList;
	private Activity context;
	private Handler mHandler;

	public ServicesApplayAdapter(Handler mHandler, List<ServiceBean> servicesList, Activity context) {
		super();
		this.mHandler = mHandler;
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
//		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.services_applay_item, null);
			viewHolder.icon = (ImageView) convertView.findViewById(R.id.icon);
			viewHolder.service_applay_no = (TextView) convertView.findViewById(R.id.service_applay_no);
			viewHolder.name = (TextView) convertView.findViewById(R.id.name);
			viewHolder.reserve_time = (TextView) convertView.findViewById(R.id.reserve_time);
			viewHolder.description = (TextView) convertView.findViewById(R.id.description);
			viewHolder.unApply = (Button) convertView.findViewById(R.id.upApply);
			viewHolder.unApply.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					SharedPreferences mSharedPreferences = context.getSharedPreferences(Constant.SHARED_PREFERENCE, Activity.MODE_PRIVATE);
					String tokenid = mSharedPreferences.getString(Constant.TOKEN_ID, "");
					String saleId = mSharedPreferences.getString(Constant.ID, "");
					NetProxyManager.getInstance().toDeleteService(mHandler, tokenid, serviceBean.getServiceId());
					servicesList.remove(serviceBean);
				}
			});
			viewHolder.status = (TextView) convertView.findViewById(R.id.status);
			convertView.setTag(viewHolder);
//		} else {
//			viewHolder = (ViewHolder) convertView.getTag();
//		}
		if (serviceBean.getIcon() != null) {
			ImageManager.loadImage(serviceBean.getIcon(), viewHolder.icon);
		}
		viewHolder.service_applay_no.setText("No." + serviceBean.getApplyId());
		viewHolder.name.setText(serviceBean.getName());
		viewHolder.reserve_time.setText(serviceBean.getReserveTime());
//		viewHolder.description.setText(serviceBean.getSname());
		String serviceStatus = serviceBean.getServiceStatus();
		if (serviceStatus.equals("0")) {
			viewHolder.status.setText("预约中");
			viewHolder.status.setTextColor(context.getResources().getColor(R.color.item_second_font_color));
		} else if (serviceStatus.equals("1")) {
			viewHolder.status.setText("已确认");
			viewHolder.status.setTextColor(context.getResources().getColor(R.color.main_bg_color));
			viewHolder.unApply.setOnClickListener(null);
			viewHolder.unApply.setVisibility(View.GONE);
		} else if (serviceStatus.equals("2")) {
			viewHolder.status.setText("已取消");
			viewHolder.status.setTextColor(context.getResources().getColor(R.color.item_second_font_color));
			viewHolder.unApply.setOnClickListener(null);
			viewHolder.unApply.setVisibility(View.GONE);
		} else {
			viewHolder.status.setText("预约中");
			viewHolder.status.setTextColor(context.getResources().getColor(R.color.item_second_font_color));
		}
		return convertView;
	}

	public class ViewHolder {
		public ImageView icon;
		public TextView name;
		public TextView description;
		public TextView service_applay_no;
		public TextView reserve_time;
		public TextView status;
		public Button unApply;
	}
}
