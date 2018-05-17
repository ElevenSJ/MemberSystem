package com.sj.http;

import android.text.TextUtils;

import com.jady.retrofitclient.interceptor.OffLineIntercept;
import com.jady.retrofitclient.interceptor.UploadFileInterceptor;
import com.lyp.membersystem.base.BaseApplication;

import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;

/**
 * Created by SunJ on 2018/5/15.
 */

public class HttpUtils {

    private volatile static HttpUtils httpUtils;

    private HttpUtils() {
    }

    public static HttpUtils getInstance() {
        if (httpUtils == null) {
            synchronized (HttpUtils.class) {
                if (httpUtils == null) {
                    httpUtils = new HttpUtils();
                }
            }
        }
        return httpUtils;
    }

    /**
     * 上传文件
     *
     * @param fullUrl            文件上传绝对地址
     * @param filePath           本地文件路径
     * @param fileDes            文件描述
     * @param fileResponseResult 回调
     */
    public void uploadFileFullPath(String fullUrl, String filePath, String fileDes, FileCallback fileResponseResult) {
        uploadFile(fullUrl, filePath, fileDes, true, fileResponseResult);
    }

    public void uploadFile(String url, String filePath, String fileDes, boolean useFullUrl, final FileCallback fileResponseResult) {
        getRetrofitBuilder(UrlConfig.BASE_URL)
                .addUploadFileInterceptor(UploadFileInterceptor.create())
                .build()
                .uploadFile(url, filePath, fileDes, useFullUrl, fileResponseResult);
    }

    /**
     * 给Retrofit添加拦截器，设置链接前缀
     *
     * @param baseUrl
     * @return
     */
    public static RetrofitClient.Builder getRetrofitBuilder(String baseUrl) {
        RetrofitClient.Builder builder = new RetrofitClient.Builder()
                .addRxJavaCallAdapterInterceptor(RxJavaCallAdapterFactory.create())
                .addOffLineIntercept(OffLineIntercept.create(BaseApplication.getInstance()))
                .isLog(true);
        if (!TextUtils.isEmpty(baseUrl)) {
            builder.baseUrl(baseUrl);
        }
        return builder;
    }
}
