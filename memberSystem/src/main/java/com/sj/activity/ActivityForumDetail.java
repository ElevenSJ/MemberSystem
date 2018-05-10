package com.sj.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jady.retrofitclient.HttpManager;
import com.lyp.membersystem.R;
import com.lyp.membersystem.utils.Constant;
import com.lyp.membersystem.utils.ToastUtil;
import com.sj.activity.base.ActivityBase;
import com.sj.activity.bean.Bannerbean;
import com.sj.activity.bean.ForumBean;
import com.sj.activity.bean.ForumListBean;
import com.sj.activity.fragment.FragmentMain;
import com.sj.http.Callback;
import com.sj.http.GsonResponsePasare;
import com.sj.http.UrlConfig;
import com.sj.utils.ImageUtils;
import com.sj.widgets.AmountView;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.loader.ImageLoader;

import java.util.ArrayList;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 创建时间: on 2018/5/6.
 * 创建人: 孙杰
 * 功能描述:
 */
public class ActivityForumDetail extends ActivityBase implements View.OnClickListener {
    TextView txtName;
    Banner banner;
    TextView txtPrice;
    TextView txtArea;
    AmountView amountView;
    String tokenid;
    ForumBean forumBean;
    ForumBean.ItemsBean itemsBean;

    @Override
    public int getContentLayout() {
        return R.layout.activity_forum_detail;
    }

    @Override
    public void initView() {
        SharedPreferences mSharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCE, MODE_PRIVATE);
        tokenid = mSharedPreferences.getString(Constant.TOKEN_ID, "");
        forumBean = (ForumBean) getIntent().getSerializableExtra("data");
        setTitleTxt("首席论坛");
        txtName = findViewById(R.id.txt_name);
        banner = findViewById(R.id.banner);
        txtPrice = findViewById(R.id.txt_price);
        txtArea = findViewById(R.id.txt_area);
        amountView = findViewById(R.id.amount_view);

        findViewById(R.id.layout_area).setOnClickListener(this);
        findViewById(R.id.bt_buy).setOnClickListener(this);

        //设置图片加载器
        banner.setImageLoader(new GlideImageLoader());
        //设置自动轮播，默认为true
        banner.isAutoPlay(true);
        //设置轮播时间
        banner.setDelayTime(3000);
        //设置指示器位置（当banner模式中有指示器时）
        banner.setIndicatorGravity(BannerConfig.RIGHT);
        //设置banner样式
        banner.setBannerStyle(BannerConfig.NUM_INDICATOR);

        amountView.setGoods_storage(0);
        initData();
    }

    private void initData() {
        if (forumBean == null) {
            return;
        }
        banner.setImages(forumBean.getSlideshowUrl());
        banner.start();
        txtName.setText(forumBean.getName());
        txtPrice.setText("¥ " + forumBean.getIntro());
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
        if (forumBean == null) {
            return;
        }
        int id = v.getId();
        switch (id) {
            case R.id.layout_area:
                ArrayList<String> names = new ArrayList<>();
                for (ForumBean.ItemsBean item : forumBean.getItems()) {
                    names.add(item.getAreaName());
                }
                Intent intent = new Intent(ActivityForumDetail.this, ActivityChooseArea.class);
                intent.putExtra("data", names);
                startActivityForResult(intent, 101);
                break;
            case R.id.bt_buy:
                if (itemsBean==null||TextUtils.isEmpty(txtArea.getText().toString())) {
                    ToastUtil.showMessage("请选择区域");
                    break;
                }
                if (itemsBean.getStatus() == 0) {
                    ToastUtil.showMessage("该地区无法购买");
                    break;
                }
                if (amountView.getAmount() == 0) {
                    ToastUtil.showMessage("请选择数量");
                    break;
                }
                doBuy();
                break;
            default:

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 101) {
                itemsBean = forumBean.getItems().get(data.getIntExtra("data", 0));
                if (itemsBean != null) {
                    txtArea.setText(itemsBean.getAreaName());
                    amountView.setGoods_storage(itemsBean.getTotal());
                }
            }
        }
    }

    private void doBuy() {
        showProgress();
        Map<String, Object> parameters = new ArrayMap<>(3);
        parameters.put("token_id", tokenid);
        parameters.put("itemId", itemsBean.getId());
        parameters.put("quantity",amountView.getAmount());
        HttpManager.get(UrlConfig.FORUM_BUY_LIST, parameters, new Callback() {
            @Override
            public void onSuccess(String message) {
                ToastUtil.showMessage("购买成功");
            }

            @Override
            public void onSuccessData(String json) {
            }

            @Override
            public void onFailure(String error_code, String error_message) {
            }

            @Override
            public void onFinish() {
                hideProgress();
            }
        });

    }

    public class GlideImageLoader extends ImageLoader {
        @Override
        public void displayImage(Context context, Object path, ImageView imageView) {
            //Glide 加载图片简单用法
            ImageUtils.loadImageView(((String) path), imageView);
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
