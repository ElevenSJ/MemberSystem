package com.sj.http;

import android.text.TextUtils;

import com.jady.retrofitclient.download.DownloadInterceptor;
import com.jady.retrofitclient.interceptor.HeaderAddInterceptor;
import com.jady.retrofitclient.interceptor.OffLineIntercept;
import com.jady.retrofitclient.interceptor.UploadFileInterceptor;
import com.jady.retrofitclient.request.CommonRequest;
import com.jady.retrofitclient.upload.UploadFileRequestBody;

import java.io.File;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by jady on 2016/12/7.
 */
public class RetrofitClient {

    public static final String TAG = "RetrofitClient";

    private CommonRequest commonRequest;
    private Retrofit retrofit;
    private Retrofit.Builder retrofitBuilder;
    private OkHttpClient okHttpClient;
    private OkHttpClient.Builder okHttpClientBuilder;
    /**
     * 默认超时时间：30秒
     */
    private static final int DEFAULT_TIMEOUT = 30;
    /**
     * retrofit缓存文件夹大小：10M
     */
    private static final int RETROFIT_CACHEDIR_SIZE = 10 * 1024 * 1024;

    public RetrofitClient(CommonRequest commonRequest, Retrofit retrofit, Retrofit.Builder retrofitBuilder, OkHttpClient okHttpClient, OkHttpClient.Builder okHttpClientBuilder) {
        this.commonRequest = commonRequest;
        this.retrofit = retrofit;
        this.retrofitBuilder = retrofitBuilder;
        this.okHttpClient = okHttpClient;
        this.okHttpClientBuilder = okHttpClientBuilder;
    }

    public static class Builder {

        private String baseUrl;
        private int TIME_OUT = 60;
        private boolean isLog = true;

        private CommonRequest request;
        private Retrofit retrofit;
        private Retrofit.Builder retrofitBuilder;
        private OkHttpClient okHttpClient;
        private OkHttpClient.Builder okHttpClientBuilder;

        //Interceptor
        private GsonConverterFactory gsonConverterInterceptor;
        private RxJavaCallAdapterFactory javaCallAdapterInterceptor;
        private UploadFileInterceptor uploadFileInterceptor;
        private DownloadInterceptor downLoadFileInterceptor;
        private OffLineIntercept offLineIntercept;

        public Builder() {
            retrofitBuilder = new Retrofit.Builder();
            okHttpClientBuilder = new OkHttpClient.Builder();
        }

        public Builder baseUrl(String url) {
            baseUrl = url;
            return this;
        }

        public Builder timeOut(int TIME_OUT) {
            this.TIME_OUT = TIME_OUT;
            return this;
        }

        public Builder isLog(boolean isLog) {
            this.isLog = isLog;
            return this;
        }

        public Builder addOffLineIntercept(OffLineIntercept offLineIntercept) {
            this.offLineIntercept = offLineIntercept;
            return this;
        }

        public Builder addRxJavaCallAdapterInterceptor(RxJavaCallAdapterFactory factory) {
            this.javaCallAdapterInterceptor = factory;
            return this;
        }

        public Builder addUploadFileInterceptor(UploadFileInterceptor interceptor) {
            this.uploadFileInterceptor = interceptor;
            return this;
        }

        public Builder client(OkHttpClient client) {
            this.okHttpClient = client;
            return this;
        }

        public Builder addHeader(Map<String, String> headers) {
            okHttpClientBuilder.addInterceptor(new HeaderAddInterceptor(headers));
            return this;
        }

        public void createHttpClient() {

            if (this.uploadFileInterceptor != null) {
                okHttpClientBuilder.addInterceptor(this.uploadFileInterceptor);
            }

            if (this.downLoadFileInterceptor != null) {
                okHttpClientBuilder.addInterceptor(this.downLoadFileInterceptor);
            }

            if (this.offLineIntercept != null) {
                okHttpClientBuilder.addInterceptor(this.offLineIntercept);
            }

            okHttpClientBuilder.connectTimeout(TIME_OUT, TimeUnit.SECONDS);
            okHttpClientBuilder.readTimeout(TIME_OUT, TimeUnit.SECONDS);
            okHttpClientBuilder.writeTimeout(TIME_OUT, TimeUnit.SECONDS);
            if (isLog)
                okHttpClientBuilder.addNetworkInterceptor(
                        new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY));

            okHttpClient = okHttpClientBuilder.build();
        }

        public RetrofitClient build() {

            createHttpClient();

            // retrofit
            if (this.javaCallAdapterInterceptor != null) {
                retrofitBuilder.addCallAdapterFactory(javaCallAdapterInterceptor);
            }

            if (this.gsonConverterInterceptor != null) {
                retrofitBuilder.addConverterFactory(gsonConverterInterceptor);
            }
            if (!TextUtils.isEmpty(baseUrl)) {
                if (baseUrl.startsWith("http")) {
                    retrofitBuilder.baseUrl(baseUrl);
                } else {
                    throw new RuntimeException("base url不合法，请以http开头");
                }
            }
            retrofitBuilder.client(okHttpClient);
            retrofit = retrofitBuilder.build();

            request = retrofit.create(CommonRequest.class);

            return new RetrofitClient(request, retrofit, retrofitBuilder, okHttpClient, okHttpClientBuilder);
        }
    }

    public OkHttpClient getOkHttpClient() {
        if (okHttpClient == null) {
            new Builder().createHttpClient();
        }
        return okHttpClient;
    }

    public void uploadFile(String url, String filePath, String fileDes, boolean isFullUrl, final FileCallback callback) {
        final File file = new File(filePath);
        if (!file.exists()) {
            return;
        }
        RequestBody description = RequestBody.create(MediaType.parse("multipart/form-data; charset=utf-8"), fileDes);
        RequestBody requestBody = UploadFileRequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), requestBody);
        if (isFullUrl) {
            commonRequest.uploadFileFullPath(url, description, part)
                    .compose(schedulerTransformer)
                    .subscribe(new UploadSubscriber(callback));
        } else {
            commonRequest.uploadFile(url, description, part)
                    .compose(schedulerTransformer)
                    .subscribe(new UploadSubscriber(callback));
        }
    }

    Observable.Transformer schedulerTransformer = new Observable.Transformer() {
        @Override
        public Object call(Object o) {
            return ((Observable) o)
                    .subscribeOn(Schedulers.io())
                    .unsubscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
        }
    };

}
