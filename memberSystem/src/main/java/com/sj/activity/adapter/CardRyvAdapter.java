package com.sj.activity.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.lyp.membersystem.R;
import com.sj.activity.bean.CardBean;


public class CardRyvAdapter extends RecyclerArrayAdapter<CardBean.CpChiefBBSBean> {
    Context context;
    public CardRyvAdapter(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public CardRyvHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new CardRyvHolder(parent,context);
    }

    private static class CardRyvHolder extends BaseViewHolder<CardBean.CpChiefBBSBean> {
        private ImageView imgIcon;
        private TextView txtTitle;
        private TextView txtTime;
        private TextView txtCreatTime;
        private ImageView imgCode;

        public CardRyvHolder(ViewGroup parent, Context context) {
            super(parent, R.layout.card_item);
            imgIcon = $(R.id.img_icon);
            txtTitle = $(R.id.txt_title);
            txtTime = $(R.id.txt_time);
            txtCreatTime = $(R.id.txt_creat_time);
            imgCode = $(R.id.img_code);
        }

        @Override
        public void setData(final CardBean.CpChiefBBSBean data) {
            super.setData(data);
//            ImageUtils.loadImageWithError(data.getPreviewUrl(),R.drawable.ic_launcher,imgIcon);
            txtTitle.setText(data.getBbsName());
            txtTime.setText(data.getIndate());
            txtCreatTime.setText("时间："+data.getDayNum());
            imgCode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(data.getQrCode())){

                    }
                }
            });
        }
    }
}
