package com.sj.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.jady.retrofitclient.HttpManager;
import com.lyp.membersystem.R;
import com.lyp.membersystem.utils.Constant;
import com.lyp.membersystem.utils.ToastUtil;
import com.sj.activity.base.ActivityBase;
import com.sj.activity.bean.ForumBean;
import com.sj.http.Callback;
import com.sj.http.UrlConfig;
import com.sj.utils.ImageUtils;
import com.sj.widgets.AmountView;
import com.sj.widgets.SlideDetailsLayout;
import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient;
import com.tencent.smtt.export.external.interfaces.JsResult;
import com.tencent.smtt.sdk.DownloadListener;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMWeb;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.loader.ImageLoader;
import com.yuntongxun.ecdemo.common.utils.FileAccessor;

import java.util.ArrayList;
import java.util.Map;

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

    SlideDetailsLayout slideDetailsLayout;
    FrameLayout slidedetailsBehind;
    WebView webview;

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
        setTitleRightTxt("分享");
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
//        banner.isAutoPlay(true);
        //设置轮播时间
        banner.setDelayTime(3000);
        //设置指示器位置（当banner模式中有指示器时）
        banner.setIndicatorGravity(BannerConfig.RIGHT);
        //设置banner样式
        banner.setBannerStyle(BannerConfig.NUM_INDICATOR);

        amountView.setGoods_storage(0);

        slideDetailsLayout = findViewById(R.id.slidedetails);
        slidedetailsBehind = findViewById(R.id.slidedetails_behind);


        webview = new WebView(this);
        slidedetailsBehind.addView(webview, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));

        initWebSetting();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences mSharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCE, MODE_PRIVATE);
        tokenid = mSharedPreferences.getString(Constant.TOKEN_ID, "");
    }

    private void getForumDetailHtml() {
        showProgress();
        Map<String, Object> parameters = new ArrayMap<>(3);
        parameters.put("token_id", tokenid);
        parameters.put("id", forumBean.getId());
        HttpManager.get(UrlConfig.FORUM_DETAIL_HTML, parameters, new Callback() {
            @Override
            public void onSuccess(String message) {
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

    private void initWebSetting() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            webview.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

        });
        webview.setWebChromeClient(new WebChromeClient() {

            @Override
            public boolean onJsConfirm(WebView arg0, String arg1, String arg2,
                                       JsResult arg3) {
                return super.onJsConfirm(arg0, arg1, arg2, arg3);
            }

            View myVideoView;
            View myNormalView;
            IX5WebChromeClient.CustomViewCallback callback;

            /**
             * 全屏播放配置
             */
            @Override
            public void onShowCustomView(View view,
                                         IX5WebChromeClient.CustomViewCallback customViewCallback) {
                FrameLayout normalView = new FrameLayout(ActivityForumDetail.this);
                ViewGroup viewGroup = (ViewGroup) normalView.getParent();
                viewGroup.removeView(normalView);
                viewGroup.addView(view);
                myVideoView = view;
                myNormalView = normalView;
                callback = customViewCallback;
            }

            @Override
            public void onHideCustomView() {
                if (callback != null) {
                    callback.onCustomViewHidden();
                    callback = null;
                }
                if (myVideoView != null) {
                    ViewGroup viewGroup = (ViewGroup) myVideoView.getParent();
                    viewGroup.removeView(myVideoView);
                    viewGroup.addView(myNormalView);
                }
            }

            @Override
            public boolean onJsAlert(WebView arg0, String arg1, String arg2,
                                     JsResult arg3) {
                /**
                 * 这里写入你自定义的window alert
                 */
                return super.onJsAlert(null, arg1, arg2, arg3);
            }
        });

        webview.setDownloadListener(new DownloadListener() {

            @Override
            public void onDownloadStart(String arg0, String arg1, String arg2,
                                        String arg3, long arg4) {
                new AlertDialog.Builder(ActivityForumDetail.this)
                        .setTitle("是否允许下载？")
                        .setPositiveButton("yes",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        dialog.dismiss();
                                        ToastUtil.showLongMessage("开始下载");
                                    }
                                })
                        .setNegativeButton("no",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        dialog.dismiss();
                                    }
                                })
                        .setOnCancelListener(
                                new DialogInterface.OnCancelListener() {

                                    @Override
                                    public void onCancel(DialogInterface dialog) {
                                        // TODO Auto-generated method stub
                                        dialog.dismiss();
                                    }
                                }).show();
            }
        });

        WebSettings webSetting = webview.getSettings();
        webSetting.setAllowFileAccess(true);
        webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSetting.setSupportZoom(false);
        webSetting.setBuiltInZoomControls(false);
        webSetting.setUseWideViewPort(true);
        webSetting.setSupportMultipleWindows(false);
        webSetting.setUseWideViewPort(true);
        webSetting.setLoadWithOverviewMode(true);
        // webSetting.setLoadWithOverviewMode(true);
        webSetting.setAppCacheEnabled(true);
        // webSetting.setDatabaseEnabled(true);
        webSetting.setDomStorageEnabled(true);
        webSetting.setJavaScriptEnabled(true);
        webSetting.setGeolocationEnabled(true);
        webSetting.setAppCacheMaxSize(Long.MAX_VALUE);
        webSetting.setAppCachePath(this.getDir("appcache", 0).getPath());
        webSetting.setDatabasePath(this.getDir("databases", 0).getPath());
        webSetting.setGeolocationDatabasePath(this.getDir("geolocation", 0)
                .getPath());
        // webSetting.setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);
        webSetting.setPluginState(WebSettings.PluginState.ON_DEMAND);

//        if (!TextUtils.isEmpty(url)) {
//        webview.loadUrl("http://www.baidu.com");
//        }

//        android.webkit.WebSettings settings = webview.getSettings();
//        settings.setJavaScriptEnabled(true);//启用js
//        settings.setJavaScriptCanOpenWindowsAutomatically(true);//js和android交互
//        settings.setAppCachePath(FileAccessor.IMESSAGE_FILE); //设置缓存的指定路径
//        settings.setAllowFileAccess(true); // 允许访问文件
//        settings.setAppCacheEnabled(true); //设置H5的缓存打开,默认关闭
//        settings.setUseWideViewPort(true);//设置webview自适应屏幕大小
//        settings.setLayoutAlgorithm(android.webkit.WebSettings.LayoutAlgorithm.NARROW_COLUMNS);//设置，可能的话使所有列的宽度不超过屏幕宽度
//        settings.setLoadWithOverviewMode(true);//设置webview自适应屏幕大小
//        settings.setDomStorageEnabled(true);//设置可以使用localStorage
//        settings.setSupportZoom(false);//关闭zoom按钮
//        settings.setBuiltInZoomControls(false);//关闭zoom
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//            webview.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
//        }
//        webview.setWebViewClient(new android.webkit.WebViewClient() {
//            @Override
//            public boolean shouldOverrideUrlLoading(android.webkit.WebView view, String url) {
//                view.loadUrl(url);
//                return false;
//            }
//
//            @Override
//            public void onLoadResource(android.webkit.WebView view, String url) {
//            }
//
//            @Override
//            public void onPageFinished(android.webkit.WebView view, String url) {
//            }
//        });
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
//        getForumDetailHtml();

        Uri.Builder builder = Uri.parse(UrlConfig.BASE_URL+UrlConfig.FORUM_DETAIL_HTML).buildUpon();
        builder.appendQueryParameter("token_id", tokenid);
        builder.appendQueryParameter("id", forumBean.getId());
        webview.loadUrl(builder.toString());
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
                if (itemsBean == null || TextUtils.isEmpty(txtArea.getText().toString())) {
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
                if (amountView.getAmount() > itemsBean.getTotal()) {
                    ToastUtil.showMessage("超出库存量，请重新选择");
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
                    txtPrice.setText("¥ " + itemsBean.getPrice());
                    txtArea.setText(itemsBean.getAreaName());
                    if (amountView.getAmount() > itemsBean.getTotal()) {
                        amountView.setAmount(itemsBean.getTotal());
                    }
                    amountView.setGoods_storage(itemsBean.getTotal());
                }
            }
        }
    }

    //if(Build.VERSION.SDK_INT>=23)
//
//    {
//        Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CALL_PHONE, Manifest.permission.READ_LOGS,
// Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.SET_DEBUG_APP, Manifest.permission.SYSTEM_ALERT_WINDOW, Manifest.permission.GET_ACCOUNTS, Manifest.permission.WRITE_APN_SETTINGS};
//    }
    @Override
    public void onRightTxt(View view) {
        super.onRightTxt(view);
        if (forumBean == null) {
            return;
        }
        ToastUtil.showMessage("去分享");
        new ShareAction(ActivityForumDetail.this).withText(forumBean.getName()).withMedia(new UMWeb(webview.getOriginalUrl())).setDisplayList(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE)
                .setCallback(shareListener).open();
    }

    private void doBuy() {
        showProgress();
        Map<String, Object> parameters = new ArrayMap<>(3);
        parameters.put("token_id", tokenid);
        parameters.put("itemId", itemsBean.getId());
        parameters.put("quantity", amountView.getAmount());
        HttpManager.get(UrlConfig.FORUM_BUY_LIST, parameters, new Callback() {
            @Override
            public void onSuccess(String message) {
                ToastUtil.showMessage("购买成功");
            }

            @Override
            public void onSuccessData(String json) {
                ToastUtil.showMessage("购买成功");
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

    private UMShareListener shareListener = new UMShareListener() {
        /**
         * @descrption 分享开始的回调
         * @param platform 平台类型
         */
        @Override
        public void onStart(SHARE_MEDIA platform) {
        }

        /**
         * @descrption 分享成功的回调
         * @param platform 平台类型
         */
        @Override
        public void onResult(SHARE_MEDIA platform) {
            ToastUtil.showMessage("分享成功");
        }

        /**
         * @descrption 分享失败的回调
         * @param platform 平台类型
         * @param t 错误原因
         */
        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            ToastUtil.showMessage("分享失败");
        }

        /**
         * @descrption 分享取消的回调
         * @param platform 平台类型
         */
        @Override
        public void onCancel(SHARE_MEDIA platform) {
            ToastUtil.showMessage("取消分享");
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        webview.destroy();
        UMShareAPI.get(this).release();
    }
}
