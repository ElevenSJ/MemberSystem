package com.sj.activity.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.ArrayMap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.jady.retrofitclient.HttpManager;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.lyp.membersystem.R;
import com.lyp.membersystem.pay.PayActivity;
import com.lyp.membersystem.utils.Constant;
import com.lyp.membersystem.utils.ToastUtil;
import com.sj.activity.ActivityForumDetail;
import com.sj.activity.ActivityHtml;
import com.sj.activity.ActivityStoryDetail;
import com.sj.activity.ActivityStudyCommon;
import com.sj.activity.ActivityStudyHtml;
import com.sj.activity.ActivityTeacherIntroduceDetail;
import com.sj.activity.PayManager;
import com.sj.activity.base.ActivityBase;
import com.sj.activity.bean.BuyResultBean;
import com.sj.activity.bean.MDRTBean;
import com.sj.activity.bean.StorytellingBean;
import com.sj.activity.bean.StudyBean;
import com.sj.activity.bean.StudyHtmlCommonBean;
import com.sj.activity.bean.TeacherIntroduceBean;
import com.sj.activity.bean.TrainClassBean;
import com.sj.http.Callback;
import com.sj.http.GsonResponsePasare;
import com.sj.http.UrlConfig;
import com.sj.utils.ImageUtils;

import java.util.Map;


public class StudyRyvAdapter extends RecyclerArrayAdapter<StudyBean> {
    static Context context;
    static SharedPreferences mSharedPreferences;

    static boolean isVip;
    static String tokenId;

    public StudyRyvAdapter(Context context) {
        super(context);
        this.context = context;
        mSharedPreferences = context.getSharedPreferences(Constant.SHARED_PREFERENCE, context.MODE_PRIVATE);
        isVip = mSharedPreferences.getBoolean(Constant.IS_VIP, false);
        tokenId = mSharedPreferences.getString(Constant.TOKEN_ID, "");
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
            case 4:
                baseViewHolder = new StudyStoryRyvHolder(parent);
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
        if (getItem(position) instanceof StorytellingBean) {
            type = 4;
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
                    intent.putExtra("title", ((ActivityBase) context).getTitleTxt());
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
        private Button detailBuy;

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
            detailBuy = $(R.id.detailBtn);
        }

        @Override
        public void setData(final TrainClassBean data) {
            super.setData(data);
            ImageUtils.loadImageWithError(data.getThumbnail(), R.drawable.ic_launcher, imgIcon);
            txtName.setText(data.getTitle());
            txtTime.setText("上课时间：" + data.getSchoolTime());
            txtLocation.setText("上课地点：" + data.getSchoolLocation());
            txtStart.setText("报名开始：" + data.getApplicationStartTime());
            txtEnd.setText("报名截止：" + data.getApplicationStartTime());
            txtPrice.setText("¥ " + data.getPrice());
            btBuy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PayManager.doBuy(context, UrlConfig.BUY_TRAIN_COURSE, data.getId(), new PayManager.PayResultListener() {
                        @Override
                        public void success() {
                            ((ActivityStudyCommon)context).onRefresh();
                        }

                        @Override
                        public void fail() {

                        }
                    });
                }
            });
            detailBuy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), ActivityStudyHtml.class);
                    intent.putExtra("needBuy", true);
                    intent.putExtra("data", data);
                    intent.putExtra("title", ((ActivityBase) context).getTitleTxt());
                    v.getContext().startActivity(intent);
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
            ImageUtils.loadImageWithError(data.getAvatar(), R.drawable.ic_launcher, imgIcon);
            txtName.setText(data.getName());
            txtDesc.setText(data.getBriefIntro());
            btBuy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), ActivityTeacherIntroduceDetail.class);
                    intent.putExtra("data", data);
                    v.getContext().startActivity(intent);
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
        private Button detailBuy;

        public StudyMDRTRyvHolder(ViewGroup parent) {
            super(parent, R.layout.study_mdrt_item);
            imgIcon = $(R.id.img_icon);
            txtName = $(R.id.txt_name);
            txtTime = $(R.id.txt_time);
            txtPrice = $(R.id.txt_price);
            btBuy = $(R.id.buyBtn);
            detailBuy = $(R.id.detailBtn);
        }

        @Override
        public void setData(final MDRTBean data) {
            super.setData(data);
            ImageUtils.loadImageWithError(data.getThumbnail(), R.drawable.ic_launcher, imgIcon);
            txtName.setText(data.getTitle());
            txtTime.setText("时间：" + data.getCreateTime());
            txtPrice.setText("¥ " + data.getPrice());
            btBuy.setVisibility(data.getFreeStatus() == 1 || data.getBuyStatus() != 0 ? View.GONE : View.VISIBLE);
            btBuy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PayManager.doBuy(context, UrlConfig.BUY_MDRT, data.getId(), new PayManager.PayResultListener() {
                        @Override
                        public void success() {
                            ((ActivityStudyCommon)context).onRefresh();
                        }

                        @Override
                        public void fail() {

                        }
                    });
                }
            });
            detailBuy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), ActivityStudyHtml.class);
                    intent.putExtra("data", data);
                    intent.putExtra("title", ((ActivityBase) context).getTitleTxt());
                    v.getContext().startActivity(intent);
                }
            });
        }
    }

    private static class StudyStoryRyvHolder extends BaseViewHolder<StorytellingBean> {
        private ImageView imgIcon;
        private TextView txtName;
        private TextView txtDesc;
        private ImageView imgTeacherIcon;
        private TextView txtTeacherName;
        private TextView txtPrice;
        private Button btBuy;
        private Button detailBuy;

        public StudyStoryRyvHolder(ViewGroup parent) {
            super(parent, R.layout.study_story_item);
            imgIcon = $(R.id.img_icon);
            txtName = $(R.id.txt_name);
            txtDesc = $(R.id.txt_desc);
            imgTeacherIcon = $(R.id.img_teacher_icon);
            txtTeacherName = $(R.id.txt_teacher_name);
            txtPrice = $(R.id.txt_price);
            btBuy = $(R.id.buyBtn);
            detailBuy = $(R.id.detailBtn);
        }

        @Override
        public void setData(final StorytellingBean data) {
            super.setData(data);
            ImageUtils.loadImageWithError(data.getThumbnail(), R.drawable.ic_launcher, imgIcon);
            txtName.setText(data.getTitle());
            txtDesc.setText(data.getBriefIntro());
            if (data.getAuthor() != null && !data.getAuthor().isEmpty()) {
                ImageUtils.loadImageWithError(data.getAuthor().get(0).getAvatar(), R.drawable.ic_launcher, imgTeacherIcon);
                txtTeacherName.setText(data.getAuthor().get(0).getName());
            } else {
                txtTeacherName.setText("");
            }
            txtPrice.setText("¥ " + data.getPrice());
            btBuy.setVisibility(data.getFreeStatus() == 1 || data.getBuyStatus() != 0 ? View.GONE : View.VISIBLE);
            btBuy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PayManager.doBuy(context, UrlConfig.BUY_STORY, data.getId(), new PayManager.PayResultListener() {
                        @Override
                        public void success() {
                            ((ActivityStudyCommon)context).onRefresh();
                        }

                        @Override
                        public void fail() {

                        }
                    });
                }
            });
            detailBuy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), ActivityStoryDetail.class);
                    intent.putExtra("data", data);
                    v.getContext().startActivity(intent);
                }
            });
        }
    }


}
