package com.sj.activity.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.lyp.membersystem.R;
import com.sj.activity.bean.MessageBean;

/**
 * 创建时间: on 2018/4/15.
 * 创建人: 孙杰
 * 功能描述:
 */
public class MessageRyvAdapter extends RecyclerArrayAdapter<MessageBean> {
    Context context;

    public MessageRyvAdapter(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public MessageRyvHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new MessageRyvHolder(parent);
    }

    private static class MessageRyvHolder extends BaseViewHolder<MessageBean> {
        TextView txtTitle;
        TextView txtdetail;
        TextView txtTime;
        TextView txtReadStatus;

        public MessageRyvHolder(ViewGroup parent) {
            super(parent, R.layout.message_item);
            txtTitle = $(R.id.txt_message_title);
            txtdetail = $(R.id.txt_message_detail);
            txtTime = $(R.id.txt_message_time);
            txtReadStatus= $(R.id.txt_read_status);
        }

        @Override
        public void setData(final MessageBean data) {
            super.setData(data);
            txtTitle.setText(data.getTitle());
            txtdetail.setText(data.getBriefIntro());
            txtTime.setText(data.getCreateTime());
            txtReadStatus.setVisibility(data.getReadStatus()==0? View.VISIBLE:View.GONE);
        }
    }
}
