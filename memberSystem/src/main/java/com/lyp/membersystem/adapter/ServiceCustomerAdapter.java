package com.lyp.membersystem.adapter;

import java.util.List;

import com.lyp.membersystem.R;
import com.lyp.membersystem.bean.TeamMemberBean;
import com.lyp.membersystem.manager.ImageManager;
import com.lyp.membersystem.view.CircleImageView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

@SuppressLint("UseSparseArrays")
public class ServiceCustomerAdapter extends BaseAdapter {
	private List<TeamMemberBean> list = null;
	private Context mContext;

	public ServiceCustomerAdapter(Context mContext, List<TeamMemberBean> list) {
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
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_team_member, null);
			viewHolder = new ViewHolder();
			viewHolder.customer_avater = (CircleImageView) convertView.findViewById(R.id.customer_avater);
			viewHolder.customer_name = ((TextView) convertView.findViewById(R.id.customer_name));
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		TeamMemberBean teamMemberBean = list.get(position);

		ImageManager.loadImage(teamMemberBean.getAvatar(), viewHolder.customer_avater, R.drawable.personal);

		viewHolder.customer_name.setText(this.list.get(position).getName());

		return convertView;

	}

	public class ViewHolder {
		public CircleImageView customer_avater;
		public TextView customer_name;

	}
}