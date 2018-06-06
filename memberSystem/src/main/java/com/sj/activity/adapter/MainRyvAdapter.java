package com.sj.activity.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.lyp.membersystem.R;
import com.sj.activity.ActivityForumDetail;
import com.sj.activity.bean.ForumBean;
import com.sj.activity.bean.SystemNotice;
import com.sj.utils.ImageUtils;


public class MainRyvAdapter extends RecyclerArrayAdapter<SystemNotice> {
    Context context;

    public MainRyvAdapter(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public MainRyvHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new MainRyvHolder(parent, context);
    }

    private static class MainRyvHolder extends BaseViewHolder<SystemNotice> {
        private ImageView imgIcon;
        private TextView txtName;
        private TextView txtTime;

        public MainRyvHolder(ViewGroup parent, Context context) {
            super(parent, R.layout.item_main);
            imgIcon = $(R.id.img_icon);
            txtName = $(R.id.txt_name);
            txtTime = $(R.id.txt_time);
        }

        @Override
        public void setData(final SystemNotice data) {
            super.setData(data);
            ImageUtils.loadImageWithError(data.getThumbnail(), R.drawable.ic_launcher, imgIcon);
            txtName.setText(data.getTitle());
            txtTime.setText(data.getCreateTime());
        }
    }
}
