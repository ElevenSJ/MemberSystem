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
import com.sj.activity.bean.StudyBean;
import com.sj.utils.ImageUtils;


public class StudyRyvAdapter extends RecyclerArrayAdapter<StudyBean> {
    Context context;
    public StudyRyvAdapter(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public StudyRyvHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new StudyRyvHolder(parent,context);
    }

    private static class StudyRyvHolder extends BaseViewHolder<StudyBean> {
        private ImageView imgIcon;
        private TextView txtName;
        private TextView txtTime;
        private TextView txtPrice;
        private Button btBuy;

        public StudyRyvHolder(ViewGroup parent, Context context) {
            super(parent, R.layout.study_item);
            imgIcon = $(R.id.img_icon);
            txtName = $(R.id.txt_name);
            txtTime = $(R.id.txt_time);
            txtPrice = $(R.id.txt_price);
            btBuy = $(R.id.buyBtn);
        }

        @Override
        public void setData(final StudyBean data) {
            super.setData(data);
            ImageUtils.loadImageWithError(data.getPreviewUrl(),R.drawable.ic_launcher,imgIcon);
            txtName.setText(data.getName());
            txtTime.setText(data.getIndate());
            txtPrice.setText(data.getIntro());
            btBuy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    if (data  instanceof ForumBean){
                        intent.setClass(v.getContext(), ActivityForumDetail.class);
                        intent.putExtra("data",data);
                    }
                    v.getContext().startActivity(intent);
                }
            });
        }
    }
}
