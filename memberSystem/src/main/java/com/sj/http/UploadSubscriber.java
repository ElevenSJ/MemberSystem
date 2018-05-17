package com.sj.http;

import java.io.IOException;

import okhttp3.ResponseBody;
import rx.Subscriber;

/**
 * Created by jady on 2016/12/8.
 */
public class UploadSubscriber<T extends ResponseBody> extends Subscriber<T> {

    private FileCallback callback;

    /**
     * 不带加载框回调
     *
     * @param callback
     */
    public UploadSubscriber(FileCallback callback) {
        this.callback = callback;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onCompleted() {
        callback.onSuccess();
    }

    @Override
    public void onError(Throwable e) {
        callback.onFailure(e, "服务器错误");
    }

    @Override
    public void onNext(T t) {
        if (t.contentLength() == 0) {
            return;
        }
        try {
            callback.onNext(t.string());
        } catch (IOException e) {
            e.printStackTrace();
            callback.onFailure(e,e.getMessage());
        }
    }
}
