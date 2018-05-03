package com.lyp.membersystem.adapter;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.lyp.membersystem.R;
import com.lyp.membersystem.bean.NoticeBean;
import com.lyp.membersystem.log.LogUtils;
import com.lyp.membersystem.utils.DateUtil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NoticeAdapter extends BaseAdapter {

	private List<NoticeBean> noticeList;
	private Context context;
	
	public NoticeAdapter(List<NoticeBean> noticeList, Context context) {
		super();
		this.noticeList = noticeList;
		this.context = context;
	}

	@Override
	public int getCount() {
		if (noticeList != null)
			return noticeList.size();
		return 0;
	}

	@Override
	public Object getItem(int position) {
		if (noticeList != null)
			return noticeList.get(position);
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
			convertView = LayoutInflater.from(context).inflate(R.layout.item_notice
					, null);
			viewHolder = new ViewHolder();
			viewHolder.notice_title = (TextView) convertView.findViewById(R.id.notice_title);
			viewHolder.notice_manager = (TextView) convertView.findViewById(R.id.notice_manager);
			viewHolder.notice_date = (TextView) convertView.findViewById(R.id.notice_date);
			viewHolder.handle = (ImageView) convertView.findViewById(R.id.handle);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		NoticeBean noticeBean = noticeList.get(position);
		viewHolder.notice_title.setText(noticeBean.getTitle());
		viewHolder.notice_manager.setText(noticeBean.getRuleType().equals("0") ? "全局规则" : "个人规则");
		viewHolder.notice_date.setText(DateUtil.stringToStr(noticeBean.getRemindTime()));
		if ("1".equals(noticeBean.getState())) {
			viewHolder.handle.setVisibility(View.VISIBLE);
			viewHolder.handle.setImageResource(R.drawable.handled);
		} else {
//			LogUtils.d("lyp", "curTime: " + System.currentTimeMillis());
//			LogUtils.d("lyp", "InValidTime: " + noticeBean.getInvalidTime());
			if (System.currentTimeMillis() > noticeBean.getInvalidTime()) {
				viewHolder.handle.setVisibility(View.VISIBLE);
				viewHolder.handle.setImageResource(R.drawable.abate);
			} else {
			    viewHolder.handle.setVisibility(View.GONE);
			}
		}
		return convertView;
	}

	public class ViewHolder {
		public TextView notice_title;
		public TextView notice_manager;
		public TextView notice_date;
		public ImageView handle;
	}
	
}
