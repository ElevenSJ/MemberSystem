package com.sj.http;

import com.jady.retrofitclient.callback.FileResponseResult;

/**
 * Created by SunJ on 2018/5/15.
 */

public interface FileCallback extends FileResponseResult{
    void onNext(String result);
}
