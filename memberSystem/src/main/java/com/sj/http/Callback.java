package com.sj.http;

import android.content.Intent;
import android.support.annotation.Keep;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.jady.retrofitclient.callback.HttpCallback;
import com.lyp.membersystem.utils.Constant;
import com.sj.activity.ActivityMain;
import com.sj.utils.Utils;


/**
 * 创建时间: on 2018/3/31.
 * 创建人: 孙杰
 * 功能描述:请求回调基类
 */
@Keep
public abstract class Callback extends HttpCallback<String> {
    @Override
    public void onResolve(String json) {
        try {
            BaseResponse baseResponse = JSON.parseObject(json, BaseResponse.class);
            if (baseResponse.success) {
                if (baseResponse.object == null) {
                    onSuccess(baseResponse.message);
                } else {
                    onSuccessData(json);
                }
            } else {
                onFailed(baseResponse.resCode, baseResponse.message);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            onSuccess(json);
        }
        onFinish();
    }

    @Override
    public void onFailed(String error_code, String error_message) {
        onFinish();
        onFailure(error_code, error_message);
        //其他设备登录统一处理
        if (error_code.equals(Constant.RELOGIN)) {
            Intent intent = new Intent(Utils.getContext(), ActivityMain.class);
            intent.putExtra("LoginOut", true);
            Utils.getContext().startActivity(intent);
        }
    }

    public abstract void onSuccess(String message);

    public abstract void onSuccessData(String json);

    public abstract void onFailure(String error_code, String error_message);

    public boolean enableShowToast() {
        return true;
    }

    public void onFinish() {
    }

}
