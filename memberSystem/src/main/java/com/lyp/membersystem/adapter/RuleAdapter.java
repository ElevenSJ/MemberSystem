package com.lyp.membersystem.adapter;

import java.util.List;

import com.lyp.membersystem.R;
import com.lyp.membersystem.bean.NoticeBean;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class RuleAdapter extends BaseAdapter {

	private List<NoticeBean> ruleList;
	private Context context;
	
	public RuleAdapter(List<NoticeBean> ruleList, Context context) {
		super();
		this.ruleList = ruleList;
		this.context = context;
	}

	@Override
	public int getCount() {
		if (ruleList != null)
			return ruleList.size();
		return 0;
	}

	@Override
	public Object getItem(int position) {
		if (ruleList != null)
			return ruleList.get(position);
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
			convertView = LayoutInflater.from(context).inflate(R.layout.item_rule
					, null);
			viewHolder = new ViewHolder();
			viewHolder.rule_title = (TextView) convertView.findViewById(R.id.rule_title);
			viewHolder.rule_date = (TextView) convertView.findViewById(R.id.rule_date);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		NoticeBean noticeBean = ruleList.get(position);
		viewHolder.rule_title.setText(noticeBean.getTitle());
		viewHolder.rule_date.setText(noticeBean.getDate());
		return convertView;
	}

	public class ViewHolder {
		public TextView rule_title;
		public TextView rule_date;
	}
	
}
