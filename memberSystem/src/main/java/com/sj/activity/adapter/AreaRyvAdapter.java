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
import com.sj.utils.ImageUtils;


public class AreaRyvAdapter extends RecyclerArrayAdapter<String> {
    Context context;
    public AreaRyvAdapter(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public ForumRyvHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new ForumRyvHolder(parent,context);
    }

    private static class ForumRyvHolder extends BaseViewHolder<String> {
        private TextView txtName;

        public ForumRyvHolder(ViewGroup parent, Context context) {
            super(parent, R.layout.area_item);
            txtName = $(R.id.txt_name);
        }

        @Override
        public void setData(final String data) {
            super.setData(data);
            txtName.setText(data);
        }
    }
}
