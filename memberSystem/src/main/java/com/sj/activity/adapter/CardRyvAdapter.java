package com.sj.activity.adapter;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.lyp.membersystem.R;
import com.sj.activity.bean.CardBean;
import com.sj.widgets.ImageDialog;


public class CardRyvAdapter extends RecyclerArrayAdapter<CardBean.CpBaseBean> {
    Context context;
    int index;
    public CardRyvAdapter(Context context,int index) {
        super(context);
        this.context = context;
        this.index = index;
    }

    @Override
    public CardRyvHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new CardRyvHolder(parent,context,index);
    }

    private static class CardRyvHolder extends BaseViewHolder<CardBean.CpBaseBean> {
        private ConstraintLayout layoutMain;
        private ImageView imgIcon;
        private TextView txtTitle;
        private TextView txtTime;
        private TextView txtCreatTime;
        private ImageView imgCode;
        ImageDialog imageDialog;
        Context context;
        int index;
        public CardRyvHolder(ViewGroup parent,Context context, int index) {
            super(parent, R.layout.card_item);
            this.context = context;
            this.index = index;
            layoutMain= $(R.id.layout_main);
            imgIcon = $(R.id.img_icon);
            txtTitle = $(R.id.txt_title);
            txtTime = $(R.id.txt_desc);
            txtCreatTime = $(R.id.txt_creat_time);
            imgCode = $(R.id.img_code);
        }

        @Override
        public void setData(final CardBean.CpBaseBean data) {
            super.setData(data);
            if (index == 0){
                layoutMain.setBackgroundResource(R.drawable.img_card_bg);
            }else{
                layoutMain.setBackgroundResource(R.drawable.img_card_bg_used);
            }
//            ImageUtils.loadImageWithError(data.getPreviewUrl(),R.drawable.ic_launcher,imgIcon);
            if (data instanceof CardBean.CpChiefBBSBean){
                CardBean.CpChiefBBSBean bean = (CardBean.CpChiefBBSBean)data;
                txtTitle.setText(bean.getBbsName());
                txtCreatTime.setText("时间："+bean.getDayNum());
            }else if (data instanceof CardBean.CpCourse){
                CardBean.CpCourse bean = (CardBean.CpCourse)data;
                txtTitle.setText(bean.getCourseName());
                txtCreatTime.setText("上课时间："+bean.getSchoolTime());
            }
            txtTime.setText(data.getIndate());
            imgCode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(data.getQrCode())){
                        if (imageDialog == null){
                            imageDialog = new ImageDialog(context);
                        }
                        imageDialog.show(data.getQrCode());
                    }
                }
            });
        }
    }
}
