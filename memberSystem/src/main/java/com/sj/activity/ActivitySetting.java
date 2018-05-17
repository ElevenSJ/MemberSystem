package com.sj.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;

import com.jady.retrofitclient.HttpManager;
import com.jady.retrofitclient.callback.FileResponseResult;
import com.jph.takephoto.model.TResult;
import com.lyp.membersystem.R;
import com.lyp.membersystem.net.API;
import com.lyp.membersystem.utils.Constant;
import com.sj.activity.base.ActivityTakePhotoBase;
import com.sj.http.Callback;
import com.sj.http.FileCallback;
import com.sj.http.GsonResponsePasare;
import com.sj.http.HttpUtils;
import com.sj.http.UrlConfig;
import com.sj.utils.ImageUtils;
import com.sj.utils.Utils;
import com.yuntongxun.ecdemo.common.utils.ToastUtil;

import java.util.List;
import java.util.Map;

/**
 * Created by SunJ on 2018/5/15.
 */

public class ActivitySetting extends ActivityTakePhotoBase implements View.OnClickListener {
    int position = -1;
    String keyName = "";

    String tokenId;
    @Override
    public void onClick(View v) {
        int id  = v.getId();
        switch (id){
            case R.id.txt_q_img:
                position = 0;
                keyName = "qrCode";
                break;
            case R.id.txt_sign_img:
                position =1;
                keyName = "signature";
                break;
            case R.id.txt_card_img:
                position =2;
                keyName = "businesscard";
                break;
                default:
        }
        showTakePhotoDialog();
    }

    private void showTakePhotoDialog() {
        String[] item = {"拍照", "从相册选择"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请选择");
        builder.setItems(item, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                getPhoto(which);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void takeCancel() {
        ToastUtil.showMessage("取消操作");
    }

    @Override
    public void takeFail(TResult result, String msg) {
        ToastUtil.showMessage(msg);
    }

    @Override
    public void takeSuccess(TResult result) {
        String path = result.getImage().getOriginalPath();
        Log.d("takePhoto", "成功：" + result.getImages().size() + "--" + path);
        uploadImg(path);
    }

    public void uploadImg(final String path) {
        if (TextUtils.isEmpty(path)) {
            ToastUtil.showMessage("图片为空");
            return;
        }
        showProgress();
        Uri.Builder builder = Uri.parse(API.API_UPLOAD_AVATER).buildUpon();
        builder.appendQueryParameter("token_id", tokenId);
        HttpUtils.getInstance().uploadFileFullPath(builder.toString(), path, "", new FileCallback() {
            @Override
            public void onNext(String result) {
                List<String> imgPaths = new GsonResponsePasare<List<String>>() {
                }.deal(result);
                if (imgPaths == null || imgPaths.isEmpty()) {
                    ToastUtil.showMessage("未获取到图片地址");
                } else {
                    updateUserInfo(keyName, imgPaths.get(0));
                }
            }

            @Override
            public void onSuccess() {
            }

            @Override
            public void onFailure(Throwable throwable, String content) {
                hideProgress();
                ToastUtil.showMessage(content);
            }
        });
    }

    private void updateUserInfo(final String key, final String value) {
        Map<String, Object> parameters = new ArrayMap<>(2);
        parameters.put("token_id", tokenId);
        parameters.put(key, value);
        HttpManager.get(UrlConfig.UPDATE_MEMBER_INFO, parameters, new Callback() {
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
                super.onFinish();
                hideProgress();
            }
        });
    }
    @Override
    public int getContentLayout() {
        return R.layout.activity_setting;
    }

    @Override
    public void initView() {
        SharedPreferences mSharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCE, MODE_PRIVATE);
        tokenId = mSharedPreferences.getString(Constant.TOKEN_ID, "");

        findViewById(R.id.txt_q_img).setOnClickListener(this);
        findViewById(R.id.txt_sign_img).setOnClickListener(this);
        findViewById(R.id.txt_card_img).setOnClickListener(this);
    }
    public void exitApp(View view){
        Intent intent = new Intent(this, ActivityMain.class);
        intent.putExtra("LoginOut", true);
        startActivity(intent);
        finish();
    }
}
