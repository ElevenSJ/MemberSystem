package com.sj.activity.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jady.retrofitclient.HttpManager;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.lyp.membersystem.R;
import com.lyp.membersystem.ui.MallFragmentActivity;
import com.lyp.membersystem.ui.MyCustomerActivity;
import com.lyp.membersystem.ui.ServiceActivity;
import com.lyp.membersystem.utils.Constant;
import com.lyp.membersystem.utils.ToastUtil;
import com.sj.activity.ActivityStudy;
import com.sj.activity.base.FragmentBase;
import com.sj.activity.bean.Bannerbean;
import com.sj.http.Callback;
import com.sj.http.GsonResponsePasare;
import com.sj.http.UrlConfig;
import com.sj.utils.ImageUtils;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.loader.ImageLoader;

import java.util.List;
import java.util.Map;

/**
 * 创建时间: on 2018/5/4.
 * 创建人: 孙杰
 * 功能描述:
 */
public class FragmentMain extends FragmentBase implements View.OnClickListener {

    Banner banner;
    ImageView back;
    TextView tvTitle;
    ImageView right;
    TextView txtCustomer;
    TextView txtGiftShop;
    TextView txtStudy;
    TextView txtReservation;
    EasyRecyclerView rylView;

    SharedPreferences mSharedPreferences;
    String tokenId;

    public static FragmentMain newInstance() {
        return new FragmentMain();
    }


    public FragmentMain() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSharedPreferences = getHoldingActivity().getSharedPreferences(Constant.SHARED_PREFERENCE, getHoldingActivity().MODE_PRIVATE);
        tokenId = mSharedPreferences.getString(Constant.TOKEN_ID, "");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_main, container, false);
        return view;
    }

    private void initView() {
        findViewById(R.id.layout_title).setBackgroundColor(getResources().getColor(R.color.ccp_translucent_half));
        banner = (Banner) findViewById(R.id.banner);
        back = (ImageView) findViewById(R.id.back);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        right = (ImageView) findViewById(R.id.right);
        txtCustomer = (TextView) findViewById(R.id.txt_customer);
        txtGiftShop = (TextView) findViewById(R.id.txt_gift_shop);
        txtStudy = (TextView) findViewById(R.id.txt_study);
        txtReservation = (TextView) findViewById(R.id.txt_reservation);
        rylView = (EasyRecyclerView) findViewById(R.id.ryl_view);
        tvTitle.setText(getResources().getString(R.string.app_name));
//        findViewById(R.id.layout_back).setVisibility(View.GONE);
        right.setImageResource(R.drawable.img_notice);
        right.setOnClickListener(this);
        txtCustomer.setOnClickListener(this);
        txtGiftShop.setOnClickListener(this);
        txtStudy.setOnClickListener(this);
        txtReservation.setOnClickListener(this);

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

    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        if (getUserVisibleHint()) {
            getBannerData();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser){
            if (banner != null) {
                //开始轮播
                banner.startAutoPlay();
            }
        }else{
            if (banner != null) {
                //结束轮播
                banner.stopAutoPlay();
            }
        }
        if (isVisibleToUser && banner != null && banner.getChildCount() == 0) {
            getBannerData();
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    private void getBannerData() {
        Map<String, Object> parameters = new ArrayMap<>(1);
        parameters.put("token_id", tokenId);
        HttpManager.get(UrlConfig.BANNER_LIST, parameters, new Callback() {
            @Override
            public void onSuccess(String message) {
                Log.d(TAG, "onSuccess: ");
            }

            @Override
            public void onSuccessData(String json) {
                Log.d(TAG, "onSuccessData: ");
                List<Bannerbean> bannerList = new GsonResponsePasare<List<Bannerbean>>() {
                }.deal(json);
                if (bannerList != null && bannerList.size() > 0) {
                    banner.setImages(bannerList);
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
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        Intent intent = new Intent();
        switch (id) {
            case R.id.right:
                ToastUtil.showMessage("系统通知");
                break;
            case R.id.txt_customer:
                intent.setClass(getHoldingActivity(), MyCustomerActivity.class);
                startActivity(intent);
                break;
            case R.id.txt_gift_shop:
                intent.setClass(getHoldingActivity(), MallFragmentActivity.class);
                startActivity(intent);
                break;
            case R.id.txt_study:
                intent.setClass(getHoldingActivity(), ActivityStudy.class);
                startActivity(intent);
                break;
            case R.id.txt_reservation:
                intent.setClass(getHoldingActivity(), ServiceActivity.class);
                startActivity(intent);
                break;
            default:
        }
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
