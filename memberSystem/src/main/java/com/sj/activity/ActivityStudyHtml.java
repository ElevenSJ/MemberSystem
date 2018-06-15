package com.sj.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.lyp.membersystem.R;
import com.lyp.membersystem.utils.Constant;
import com.lyp.membersystem.utils.ToastUtil;
import com.sj.activity.base.ActivityBase;
import com.sj.activity.bean.MDRTBean;
import com.sj.activity.bean.StudyBean;
import com.sj.activity.bean.StudyHtmlCommonBean;
import com.sj.activity.bean.TrainClassBean;
import com.sj.http.UrlConfig;
import com.sj.widgets.downloadview.DownloadDialog;
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

import java.io.IOException;

/**
 * 创建时间: on 2018/4/26.
 * 创建人: 孙杰
 * 功能描述:
 */
public class ActivityStudyHtml extends ActivityBase {

    WebView webview;
    FrameLayout webviewLayout;
    ProgressBar progressBar;
    TextView titleTxt;
    TextView timeTxt;
    TextView readTxt;
    ImageView imgShare;
    TextView txtBuy;

    StudyBean studyBean;
    String tokenid;

    DownloadDialog downloadDialog;

    String title = "";
    boolean needBuy = false;

    @Override
    public int getContentLayout() {
        return R.layout.activity_study_html;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            if (Integer.parseInt(Build.VERSION.SDK) >= 11) {
                getWindow()
                        .setFlags(
                                android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                                android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
            }
        } catch (Exception e) {
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initView() {
        SharedPreferences mSharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCE, MODE_PRIVATE);
        tokenid = mSharedPreferences.getString(Constant.TOKEN_ID, "");
        needBuy = getIntent().getBooleanExtra("needBuy", false);
        studyBean = (StudyBean) getIntent().getSerializableExtra("data");
        title = getIntent().getStringExtra("title");
        setTitleTxt(title);

        if (studyBean.getAttachs() != null && !studyBean.getAttachs().isEmpty()) {
            setTitleRightTxt("下载");
        }

        if (studyBean instanceof StudyHtmlCommonBean || studyBean instanceof MDRTBean) {
            titleTxt = findViewById(R.id.txt_title);
            timeTxt = findViewById(R.id.txt_time);
            readTxt = findViewById(R.id.txt_read_count);
            imgShare = findViewById(R.id.img_share);
            titleTxt.setVisibility(View.VISIBLE);
            timeTxt.setVisibility(View.VISIBLE);
            readTxt.setVisibility(View.VISIBLE);
            imgShare.setVisibility(View.VISIBLE);
            titleTxt.setText(studyBean.getTitle());
            if (studyBean instanceof StudyHtmlCommonBean) {
                timeTxt.setText(((StudyHtmlCommonBean) studyBean).getCreateTime());
                readTxt.setText(((StudyHtmlCommonBean) studyBean).getReadQuantity());
            } else if (studyBean instanceof MDRTBean) {
                timeTxt.setText(((MDRTBean) studyBean).getCreateTime());
                readTxt.setVisibility(View.GONE);
            }
            imgShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ToastUtil.showMessage("去分享");
                    new ShareAction(ActivityStudyHtml.this).withText(studyBean.getTitle()).withMedia(new UMWeb(studyBean.getDetailUrl() + "?token_id=" + tokenid)).setDisplayList(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE)
                            .setCallback(shareListener).open();
                }
            });
        }
        if (needBuy && studyBean instanceof TrainClassBean) {
            txtBuy = findViewById(R.id.txt_buy);
            txtBuy.setVisibility(View.VISIBLE);
            txtBuy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PayManager.doBuy(ActivityStudyHtml.this, UrlConfig.BUY_TRAIN_COURSE, studyBean.getId(), new PayManager.PayResultListener() {
                        @Override
                        public void success() {
                            txtBuy.setVisibility(View.GONE);
                        }

                        @Override
                        public void fail() {

                        }
                    });
                }
            });
        }

        webviewLayout = findViewById(R.id.webview_layout);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setMax(100);
        progressBar.setProgressDrawable(this.getResources()
                .getDrawable(R.drawable.color_progressbar));

        webview = new WebView(this);
        webviewLayout.addView(webview, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));

        initWebSetting();

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

            @Override
            public void onPageStarted(WebView webView, String s, Bitmap bitmap) {
                super.onPageStarted(webView, s, bitmap);
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress(0);
            }
        });
        webview.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView webView, int i) {
                progressBar.setProgress(i);
                if (100 <= i) {
                    progressBar.setVisibility(View.GONE);
                }
                super.onProgressChanged(webView, i);
            }

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
                FrameLayout normalView = new FrameLayout(ActivityStudyHtml.this);
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
                new AlertDialog.Builder(ActivityStudyHtml.this)
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
        webSetting.setLoadWithOverviewMode(true);
        webSetting.setUseWideViewPort(true);
        webSetting.setSupportMultipleWindows(false);
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

        String url = studyBean.getDetailUrl() + "?token_id=" + tokenid;
        if (studyBean instanceof MDRTBean) {
            if (((MDRTBean) studyBean).getBuyStatus() != 0 || ((MDRTBean) studyBean).getFreeStatus() == 1) {
                url = ((MDRTBean) studyBean).getChargeContentUrl() + "?token_id=" + tokenid;

            }
        }
        webview.loadUrl(url);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (webview != null && webview.canGoBack()) {
                webview.goBack();
                return true;
            } else
                return super.onKeyDown(keyCode, event);
        }
        return super.onKeyDown(keyCode, event);
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
            Log.i("share", "onError: " + t.getMessage());
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
        if (webview != null) {
            webview.destroy();
        }
        UMShareAPI.get(this).release();
    }

    @Override
    public void onRightTxt(View view) {
        super.onRightTxt(view);
        ToastUtil.showMessage("附件下载列表，选择文件开始下载");
        if (downloadDialog == null) {
            downloadDialog = new DownloadDialog(this);
        }
        if (!downloadDialog.isShowing()) {
            downloadDialog.show(studyBean.getAttachs());
        } else {
            downloadDialog.setData(studyBean.getAttachs());
        }
    }

}