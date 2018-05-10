package com.sj.activity;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import com.lyp.membersystem.R;
import com.sj.activity.base.ActivityBase;
import com.yuntongxun.ecdemo.common.utils.FileAccessor;

import butterknife.BindView;
import butterknife.ButterKnife;
import okio.Utf8;

/**
 * 创建时间: on 2018/4/26.
 * 创建人: 孙杰
 * 功能描述:
 */
public class ActivityHtml extends ActivityBase {

    WebView webview;
    @BindView(R.id.webview_layout)
    FrameLayout webviewLayout;

    @Override
    public int getContentLayout() {
        return R.layout.activity_register_html;
    }

    @Override
    public void initView() {
        setTitleTxt("注册协议");

        String url = getIntent().getStringExtra("url");
        String html = getIntent().getStringExtra("html");

        webview = new WebView(this);
        webviewLayout.addView(webview);
        WebSettings settings = webview.getSettings();
        settings.setJavaScriptEnabled(true);//启用js
        settings.setJavaScriptCanOpenWindowsAutomatically(true);//js和android交互
        settings.setAppCachePath(FileAccessor.IMESSAGE_FILE); //设置缓存的指定路径
        settings.setAllowFileAccess(true); // 允许访问文件
        settings.setAppCacheEnabled(true); //设置H5的缓存打开,默认关闭
        settings.setUseWideViewPort(true);//设置webview自适应屏幕大小
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);//设置，可能的话使所有列的宽度不超过屏幕宽度
        settings.setLoadWithOverviewMode(true);//设置webview自适应屏幕大小
        settings.setDomStorageEnabled(true);//设置可以使用localStorage
        settings.setSupportZoom(false);//关闭zoom按钮
        settings.setBuiltInZoomControls(false);//关闭zoom
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            webview.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return false;
            }

            @Override
            public void onLoadResource(WebView view, String url) {
            }

            @Override
            public void onPageFinished(WebView view, String url) {
            }
        });
        if (!TextUtils.isEmpty(url)){
            webview.loadUrl(url);
        }else if(!TextUtils.isEmpty(html)){
            webview.loadData(html,"text/html", "UTF-8");
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        webview.destroy();
    }

}
