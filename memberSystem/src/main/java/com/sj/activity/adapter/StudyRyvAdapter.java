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
import com.sj.activity.ActivityStudyHtml;
import com.sj.activity.bean.MDRTBean;
import com.sj.activity.bean.StudyBean;
import com.sj.activity.bean.StudyHtmlCommonBean;
import com.sj.activity.bean.TeacherIntroduceBean;
import com.sj.activity.bean.TrainClassBean;
import com.sj.utils.ImageUtils;


public class StudyRyvAdapter extends RecyclerArrayAdapter<StudyBean> {
    Context context;

    public StudyRyvAdapter(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        BaseViewHolder baseViewHolder = null;
        switch (viewType) {
            case 0:
                baseViewHolder = new StudyRyvHolder(parent);
                break;
            case 1:
                baseViewHolder = new StudyTrainRyvHolder(parent);
                break;
            case 2:
                baseViewHolder = new StudyTeacherRyvHolder(parent);
                break;
            case 3:
                baseViewHolder = new StudyMDRTRyvHolder(parent);
                break;
        }
        return baseViewHolder;
    }

    @Override
    public int getViewType(int position) {
        int type = 0;
        if (getItem(position) instanceof StudyHtmlCommonBean) {
            type = 0;
        }
        if (getItem(position) instanceof TrainClassBean) {
            type = 1;
        }
        if (getItem(position) instanceof TeacherIntroduceBean) {
            type = 2;
        }
        if (getItem(position) instanceof MDRTBean) {
            type = 3;
        }
        return type;
    }

    private static class StudyRyvHolder extends BaseViewHolder<StudyBean> {
        private ImageView imgIcon;
        private TextView txtName;
        private TextView txtTime;
        private TextView txtPrice;
        private Button btBuy;

        public StudyRyvHolder(ViewGroup parent) {
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
            ImageUtils.loadImageWithError(data.getThumbnail(), R.drawable.ic_launcher, imgIcon);
            txtName.setText(data.getTitle());
            txtTime.setText("时间：" + ((StudyHtmlCommonBean) data).getCreateTime());
            txtPrice.setVisibility(View.GONE);
            btBuy.setText("查看");
            btBuy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(v.getContext(), ActivityStudyHtml.class);
                    intent.putExtra("data", data);
                    v.getContext().startActivity(intent);
                }
            });
        }
    }

    private static class StudyTrainRyvHolder extends BaseViewHolder<TrainClassBean> {
        private ImageView imgIcon;
        private TextView txtName;
        private TextView txtTime;
        private TextView txtLocation;
        private TextView txtStart;
        private TextView txtEnd;
        private TextView txtPrice;
        private Button btBuy;

        public StudyTrainRyvHolder(ViewGroup parent) {
            super(parent, R.layout.study_train_item);
            imgIcon = $(R.id.img_icon);
            txtName = $(R.id.txt_name);
            txtTime = $(R.id.txt_time);
            txtLocation = $(R.id.txt_location);
            txtStart = $(R.id.txt_start);
            txtEnd = $(R.id.txt_end);
            txtPrice = $(R.id.txt_price);
            btBuy = $(R.id.buyBtn);
        }

        @Override
        public void setData(final TrainClassBean data) {
            super.setData(data);
            ImageUtils.loadImageWithError(data.getThumbnail(), R.drawable.ic_launcher, imgIcon);
            txtName.setText(data.getTitle());
            txtTime.setText("上课时间："+data.getSchoolTime());
            txtLocation.setText("上课地点：" + data.getSchoolLocation());
            txtStart.setText("报名开始："+data.getApplicationStartTime());
            txtEnd.setText("报名截止："+data.getApplicationStartTime());
            txtPrice.setText("¥ " + data.getPrice());
            btBuy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
        }
    }

    private static class StudyTeacherRyvHolder extends BaseViewHolder<TeacherIntroduceBean> {
        private ImageView imgIcon;
        private TextView txtName;
        private TextView txtDesc;
        private Button btBuy;

        public StudyTeacherRyvHolder(ViewGroup parent) {
            super(parent, R.layout.study_teacher_item);
            imgIcon = $(R.id.img_icon);
            txtName = $(R.id.txt_name);
            txtDesc = $(R.id.txt_desc);
            btBuy = $(R.id.buyBtn);
        }

        @Override
        public void setData(final TeacherIntroduceBean data) {
            super.setData(data);
            ImageUtils.loadImageWithError(data.getThumbnail(), R.drawable.ic_launcher, imgIcon);
            txtName.setText(data.getTitle());
            txtDesc.setText(data.getBriefIntro());
            btBuy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
        }
    }

    private static class StudyMDRTRyvHolder extends BaseViewHolder<MDRTBean> {
        private ImageView imgIcon;
        private TextView txtName;
        private TextView txtTime;
        private TextView txtPrice;
        private Button btBuy;

        public StudyMDRTRyvHolder(ViewGroup parent) {
            super(parent, R.layout.study_mdrt_item);
            imgIcon = $(R.id.img_icon);
            txtName = $(R.id.txt_name);
            txtTime = $(R.id.txt_time);
            txtPrice = $(R.id.txt_price);
            btBuy = $(R.id.buyBtn);
        }

        @Override
        public void setData(final MDRTBean data) {
            super.setData(data);
            ImageUtils.loadImageWithError(data.getThumbnail(), R.drawable.ic_launcher, imgIcon);
            txtName.setText(data.getTitle());
            txtTime.setText("时间：" + data.getCreateTime());
            txtPrice.setText("¥ " + data.getPrice());
            btBuy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
        }
    }

}
