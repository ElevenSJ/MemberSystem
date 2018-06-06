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
import com.sj.activity.ActivityStudyCommon;
import com.sj.activity.ActivityStudyHtml;
import com.sj.activity.ActivityTeacherIntroduceDetail;
import com.sj.activity.PayManager;
import com.sj.activity.base.ActivityBase;
import com.sj.activity.bean.BuyResultBean;
import com.sj.activity.bean.LecturerDetailBean;
import com.sj.activity.bean.TrainClassBean;
import com.sj.http.Callback;
import com.sj.http.GsonResponsePasare;
import com.sj.http.UrlConfig;
import com.sj.utils.ImageUtils;

import java.util.Map;


public class LeturerCourseRyvAdapter extends RecyclerArrayAdapter<TrainClassBean> {
    static Context context;
    static String tokenId;
    public LeturerCourseRyvAdapter(Context context) {
        super(context);
        this.context = context;
       SharedPreferences mSharedPreferences = context.getSharedPreferences(Constant.SHARED_PREFERENCE, context.MODE_PRIVATE);
        tokenId = mSharedPreferences.getString(Constant.TOKEN_ID, "");
    }

    @Override
    public  LeturerCourseRyvHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new  LeturerCourseRyvHolder(parent,context);
    }

    private static class  LeturerCourseRyvHolder extends BaseViewHolder<TrainClassBean> {
        private ImageView imgIcon;
        private TextView txtName;
        private TextView txtTime;
        private TextView txtLocation;
        private TextView txtStart;
        private TextView txtEnd;
        private TextView txtPrice;
        private Button btBuy;

        public  LeturerCourseRyvHolder(ViewGroup parent, Context context) {
            super(parent, R.layout.teacher_course_item);
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
        public void setData(final  TrainClassBean data) {
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
                            ((ActivityTeacherIntroduceDetail)context).onRefresh();
                        }

                        @Override
                        public void fail() {

                        }
                    });
                }
            });
        }
    }

}
