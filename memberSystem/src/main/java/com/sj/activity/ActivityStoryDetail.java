package com.sj.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
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

import com.jady.retrofitclient.HttpManager;
import com.jady.retrofitclient.listener.DownloadFileListener;
import com.lyp.membersystem.R;
import com.lyp.membersystem.utils.Constant;
import com.lyp.membersystem.utils.ToastUtil;
import com.lyp.membersystem.view.CircleImageView;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.sj.activity.base.ActivityBase;
import com.sj.activity.bean.StorytellingBean;
import com.sj.audiotool.IMAudioManager;
import com.sj.http.UrlConfig;
import com.sj.utils.ImageUtils;
import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient;
import com.tencent.smtt.export.external.interfaces.JsResult;
import com.tencent.smtt.sdk.DownloadListener;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import com.yuntongxun.ecdemo.common.utils.FileAccessor;

import java.io.File;

/**
 * 创建时间: on 2018/4/26.
 * 创建人: 孙杰
 * 功能描述:
 */
public class ActivityStoryDetail extends ActivityBase implements View.OnClickListener {

    RelativeLayout layoutAudio;
    SeekBar audioSeekBar;

    WebView webview;
    FrameLayout webviewLayout;
    ProgressBar progressBar;
    ImageView imgIcon;
    CircleImageView imgHeader;
    TextView txtTitle;
    TextView txtName;
    TextView txtDesc;
    TextView txtPrice;
    Button btBuy;

    StorytellingBean storytellingBean;
    String tokenid;
    boolean isVip = false;
    boolean isPrepared = false;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (IMAudioManager.instance().getMediaPlayer() != null) {
                audioSeekBar.setProgress(IMAudioManager.instance().getMediaPlayer().getCurrentPosition());
            }
        }
    };

    private SeekBar.OnSeekBarChangeListener sbLis = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // TODO Auto-generated method stub
//            mMediaPlayer.seekTo(seekBar.getProgress());
            seekBar.setProgress(IMAudioManager.instance().getMediaPlayer().getCurrentPosition());
        }

    };

    @Override
    public int getContentLayout() {
        return R.layout.activity_story_detail;
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
        IMAudioManager.instance().init(this.getApplicationContext());
    }

    @Override
    public void initView() {
        SharedPreferences mSharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCE, MODE_PRIVATE);
        tokenid = mSharedPreferences.getString(Constant.TOKEN_ID, "");
        isVip = mSharedPreferences.getBoolean(Constant.IS_VIP, false);
        storytellingBean = (StorytellingBean) getIntent().getSerializableExtra("data");
        setTitleTxt("作者说书");


        imgIcon = findViewById(R.id.img_icon);
        imgHeader = findViewById(R.id.img_teacher_icon);
        txtTitle = findViewById(R.id.txt_title);
        imgHeader = findViewById(R.id.img_teacher_icon);
        txtName = findViewById(R.id.txt_teacher_name);
        txtDesc = findViewById(R.id.txt_desc);
        txtPrice = findViewById(R.id.txt_price);

        btBuy = findViewById(R.id.buyBtn);
        btBuy.setOnClickListener(this);

        webviewLayout = findViewById(R.id.webview_layout);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setMax(100);
        progressBar.setProgressDrawable(this.getResources()
                .getDrawable(R.drawable.color_progressbar));

        webview = new WebView(this);
        webviewLayout.addView(webview, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));

        layoutAudio = findViewById(R.id.layout_audio);
        audioSeekBar = findViewById(R.id.seekBar);
        findViewById(R.id.bt_play).setOnClickListener(this);
        findViewById(R.id.bt_down).setOnClickListener(this);

        initData();
        initWebSetting();
    }

    private void initData() {
        txtTitle.setText(storytellingBean.getTitle());
        ImageUtils.loadImageWithError(storytellingBean.getThumbnail(), R.drawable.ic_launcher, imgIcon);
        if (storytellingBean.getAuthor() != null && !storytellingBean.getAuthor().isEmpty()) {
            ImageUtils.loadImageWithError(storytellingBean.getAuthor().get(0).getAvatar(), R.drawable.ic_launcher, imgHeader);
            txtName.setText(storytellingBean.getAuthor().get(0).getName());
            txtDesc.setText(storytellingBean.getAuthor().get(0).getBriefIntro());
        } else {
            txtName.setText("");
            txtDesc.setText("");
        }
        txtPrice.setText("¥ " + storytellingBean.getPrice());
        btBuy.setText(storytellingBean.getBuyStatus() != 0 || storytellingBean.getFreeStatus() == 1 ? "查看" : "购买");
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

            @Override
            public void onPageFinished(WebView webView, String s) {
                super.onPageFinished(webView, s);

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
                FrameLayout normalView = new FrameLayout(ActivityStoryDetail.this);
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
                new AlertDialog.Builder(ActivityStoryDetail.this)
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

        webview.loadUrl(storytellingBean.getDetailUrl() + "?token_id=" + tokenid);
    }

    private void initAudio() {
        String fileName = new Md5FileNameGenerator().generate(storytellingBean.getAudioUrl());
        File audioFile = new File(FileAccessor.APP_AUDIO, fileName);
        try {
            IMAudioManager.instance().playSound(audioFile.exists() ? audioFile.getAbsolutePath() : storytellingBean.getAudioUrl(), new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.reset();
                    audioSeekBar.setProgress(0);
                }
            });
            isPrepared = true;
            layoutAudio.setVisibility(View.VISIBLE);
            audioSeekBar.setMax(IMAudioManager.instance().getMediaPlayer().getDuration());
            audioSeekBar.setOnSeekBarChangeListener(sbLis);
            updateSeekBar();
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtil.showMessage("加载音频文件异常,请重试！");
            isPrepared = false;
            if (audioFile.exists()) {
                audioFile.delete();
            }
        }
    }

    private void updateSeekBar() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (IMAudioManager.instance().getMediaPlayer().isPlaying() && audioSeekBar.getProgress() <= audioSeekBar.getMax()) {
                    mHandler.sendEmptyMessageDelayed(0, 500);
                }
                if (IMAudioManager.instance().getMediaPlayer().isPlaying()) {
                    mHandler.postDelayed(this, 500);//延迟一秒运行线程
                }
            }
        };
        mHandler.postDelayed(runnable, 500);//延迟一秒运行线程

    }

    @Override
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        IMAudioManager.instance().release();
        if (webview != null) {
            webview.destroy();
        }
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.bt_down:
                final String fileName = new Md5FileNameGenerator().generate(storytellingBean.getAudioUrl());
                ToastUtil.showMessage("正在下载");
                HttpManager.getInstance().download(storytellingBean.getAudioUrl(), FileAccessor.APP_AUDIO + "/" + fileName, new DownloadFileListener() {
                    @Override
                    public void onNext(Object o) {

                    }

                    @Override
                    public void onComplete() {
                    }

                    @Override
                    public void updateProgress(float contentRead, long contentLength, boolean completed) {
                        ToastUtil.showMessage(((int)(contentRead / contentLength * 100)) + "%");
                        if (contentLength==contentRead){
                            ToastUtil.showMessage("下载成功");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("download", "onError: " + e.getMessage());
                        new File(FileAccessor.APP_AUDIO, fileName).delete();
                    }
                });
                break;
            case R.id.bt_play:
                if (IMAudioManager.instance().getMediaPlayer() == null) {
                    return;
                }
                if (!isPrepared) {
                    return;
                }
                if (IMAudioManager.instance().getMediaPlayer().isPlaying()) {
                    IMAudioManager.instance().pause();
                    ((ImageView) v).setImageResource(R.drawable.img_play);
                    mHandler.removeCallbacksAndMessages(null);
                } else {
                    ((ImageView) v).setImageResource(R.drawable.img_stop);
                    IMAudioManager.instance().resume();
                    updateSeekBar();
                }
                break;
            case R.id.buyBtn:
                if (storytellingBean.getBuyStatus() != 0 || storytellingBean.getFreeStatus() == 1) {
                    if (!TextUtils.isEmpty(storytellingBean.getAudioUrl())) {
                        initAudio();
                    }
                } else {
                    PayManager.doBuy(this, UrlConfig.BUY_STORY, storytellingBean.getId(), new PayManager.PayResultListener() {
                        @Override
                        public void success() {
                            storytellingBean.setBuyStatus(1);
                            storytellingBean.setFreeStatus(1);
                            btBuy.setText(storytellingBean.getBuyStatus() != 0 || storytellingBean.getFreeStatus() == 1 ? "查看" : "购买");
                        }

                        @Override
                        public void fail() {

                        }
                    });
                }
                break;
        }

    }
}
