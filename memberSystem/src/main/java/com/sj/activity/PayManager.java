package com.sj.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.ArrayMap;

import com.jady.retrofitclient.HttpManager;
import com.lyp.membersystem.pay.PayActivity;
import com.lyp.membersystem.utils.Constant;
import com.lyp.membersystem.utils.ToastUtil;
import com.sj.activity.base.ActivityBase;
import com.sj.activity.base.FragmentBase;
import com.sj.activity.bean.BuyResultBean;
import com.sj.http.Callback;
import com.sj.http.GsonResponsePasare;

import java.util.Map;

/**
 * 创建时间: on 2018/6/2.
 * 创建人: 孙杰
 * 功能描述:
 */
public class PayManager {

    public static void doBuy(final Context context,String url, String id,final PayResultListener payResultListener) {
        if (context instanceof ActivityBase){
            ((ActivityBase) context).showProgress();
        }
        SharedPreferences mSharedPreferences = context.getSharedPreferences(Constant.SHARED_PREFERENCE, context.MODE_PRIVATE);
        String tokenId = mSharedPreferences.getString(Constant.TOKEN_ID, "");
        Map<String, Object> parameters = new ArrayMap<>(2);
        parameters.put("token_id", tokenId);
        parameters.put("id", id);
        HttpManager.get(url, parameters, new Callback() {
            @Override
            public void onSuccess(String message) {
            }

            @Override
            public void onSuccessData(String json) {
                BuyResultBean buyResultBean = new GsonResponsePasare<BuyResultBean>() {
                }.deal(json);
                ToastUtil.showMessage("购买成功");
                if (payResultListener !=null){
                    payResultListener.success();
                }
//                Intent i = new Intent(context, PayActivity.class);
//                i.putExtra("order_id", buyResultBean.getOrderId());
//                i.putExtra("type", "study");
//                i.putExtra("order_amount", buyResultBean.getOrderPrice());
//                context.startActivity(i);
            }

            @Override
            public void onFailure(String error_code, String error_message) {
                ToastUtil.showMessage(error_message);
                if (payResultListener !=null){
                    payResultListener.fail();
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (context instanceof ActivityBase){
                    ((ActivityBase) context).hideProgress();
                }
            }
        });

    }
    public static interface  PayResultListener{
        void success();
        void fail();
    }
}
