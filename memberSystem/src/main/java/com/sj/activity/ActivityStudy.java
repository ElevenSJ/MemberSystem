package com.sj.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jady.retrofitclient.HttpManager;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.lyp.membersystem.R;
import com.lyp.membersystem.utils.Constant;
import com.lyp.membersystem.utils.ToastUtil;
import com.sj.activity.base.ActivityBase;
import com.sj.activity.bean.Bannerbean;
import com.sj.activity.bean.ForumBean;
import com.sj.http.Callback;
import com.sj.http.GsonResponsePasare;
import com.sj.http.UrlConfig;
import com.sj.utils.ImageUtils;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 创建时间: on 2018/5/6.
 * 创建人: 孙杰
 * 功能描述:
 */
public class ActivityStudy extends ActivityBase implements View.OnClickListener{
    Banner banner;
    EasyRecyclerView rylView;
    TextView txtReadBook;
    TextView txtMorningMeetting;
    TextView txtGoodTime;
    TextView txtTrain;
    TextView txtTeacher;
    TextView txtMDRT;
    TextView txtForum;
    TextView txtClass;

    List<Bannerbean> bannerData;
    String tokenId;

    @Override
    public int getContentLayout() {
        return R.layout.activity_study;
    }

    @Override
    public void initView() {
        setTitleTxt("进修学习");
//        findViewById(R.id.layout_title).setBackgroundColor(getResources().getColor(R.color.half_transparent_color));
        findViewById(R.id.layout_title).setBackgroundColor(getResources().getColor(R.color.transparent_color));
        SharedPreferences mSharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCE, MODE_PRIVATE);
        tokenId = mSharedPreferences.getString(Constant.TOKEN_ID, "");

        banner = findViewById(R.id.banner);
        rylView =  findViewById(R.id.ryl_view);
        txtReadBook = findViewById(R.id.txt_read_book);
        txtMorningMeetting = findViewById(R.id.txt_morning_meetting);
        txtGoodTime = findViewById(R.id.txt_good_time);
        txtTrain = findViewById(R.id.txt_train);
        txtTeacher = findViewById(R.id.txt_teacher);
        txtMDRT = findViewById(R.id.txt_MDRT);
        txtForum = findViewById(R.id.txt_forum);
        txtClass = findViewById(R.id.txt_class);
        txtReadBook.setOnClickListener(this);
        txtMorningMeetting.setOnClickListener(this);
        txtGoodTime.setOnClickListener(this);
        txtTrain.setOnClickListener(this);
        txtTeacher.setOnClickListener(this);
        txtMDRT.setOnClickListener(this);
        txtForum.setOnClickListener(this);
        txtClass.setOnClickListener(this);


        //设置图片加载器
        banner.setImageLoader(new GlideImageLoader());
        //设置自动轮播，默认为true
        banner.isAutoPlay(true);
        //设置轮播时间
        banner.setDelayTime(3000);
        //设置指示器位置（当banner模式中有指示器时）
        banner.setIndicatorGravity(BannerConfig.CENTER);
        //设置banner样式
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);

        banner.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {
                if (bannerData==null||bannerData.isEmpty()|| TextUtils.isEmpty(bannerData.get(position).getAccessLink())){
                    return;
                }
                Intent intent = new Intent(ActivityStudy.this, ActivityHtml.class);
                intent.putExtra("title",bannerData.get(position).getName());
                intent.putExtra("url",bannerData.get(position).getAccessLink());
                startActivity(intent);
            }
        });

        getBannerData();

    }

    private void getBannerData() {
        Map<String, Object> parameters = new ArrayMap<>(1);
        parameters.put("token_id", tokenId);
        HttpManager.get(UrlConfig.BANNER_LIST_STUDY, parameters, new Callback() {
            @Override
            public void onSuccess(String message) {
                Log.d(TAG, "onSuccess: ");
            }

            @Override
            public void onSuccessData(String json) {
                Log.d(TAG, "onSuccessData: ");
                bannerData = new GsonResponsePasare<List<Bannerbean>>() {
                }.deal(json);
                if (bannerData!=null&&!bannerData.isEmpty()){
                    banner.setImages(bannerData);
                    banner.start();
                }
            }

            @Override
            public void onFailure(String error_code, String error_message) {
                Log.d(TAG, "onSuccessData: ");

            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        if (banner != null) {
            banner.startAutoPlay();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (banner != null) {
            banner.stopAutoPlay();
        }
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        String title = ((TextView) v).getText().toString();
        Intent intent = new Intent();
        intent.setClass(this, ActivityStudyCommon.class);
        intent.putExtra("title",title);
        intent.putExtra("type",id);
        switch (id) {
            case R.id.txt_read_book:
                intent.putExtra("url",UrlConfig.FORUM_LIST);
                break;
            case R.id.txt_morning_meetting:
                intent.putExtra("url",UrlConfig.FORUM_LIST);
                break;
            case R.id.txt_good_time:
                intent.putExtra("url",UrlConfig.FORUM_LIST);
                break;
            case R.id.txt_train:
                intent.putExtra("url",UrlConfig.FORUM_LIST);
                break;
            case R.id.txt_teacher:
                intent.putExtra("url",UrlConfig.FORUM_LIST);
                break;
            case R.id.txt_MDRT:
                intent.putExtra("url",UrlConfig.FORUM_LIST);
                break;
            case R.id.txt_forum:
                intent.putExtra("url",UrlConfig.FORUM_LIST);
                break;
            case R.id.txt_class:
                intent.putExtra("url",UrlConfig.FORUM_LIST);
                break;
        }
        startActivity(intent);
    }

    public class GlideImageLoader extends ImageLoader {
        @Override
        public void displayImage(Context context, Object path, ImageView imageView) {
            //Glide 加载图片简单用法
            ImageUtils.loadImageView(((Bannerbean) path).getPicUrl(), imageView);
        }

        //提供createImageView 方法，如果不用可以不重写这个方法，主要是方便自定义ImageView的创建
        @Override
        public ImageView createImageView(Context context) {
            ImageView imageView = new ImageView(context);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            return imageView;
        }
    }
}
