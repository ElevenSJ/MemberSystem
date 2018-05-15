package com.sj.activity.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.jady.retrofitclient.HttpManager;
import com.jady.retrofitclient.callback.FileResponseResult;
import com.jph.takephoto.app.TakePhoto;
import com.jph.takephoto.app.TakePhotoFragment;
import com.jph.takephoto.compress.CompressConfig;
import com.jph.takephoto.model.CropOptions;
import com.jph.takephoto.model.TResult;
import com.jph.takephoto.model.TakePhotoOptions;
import com.lyp.membersystem.R;
import com.lyp.membersystem.log.LogUtils;
import com.lyp.membersystem.manager.ImageManager;
import com.lyp.membersystem.net.API;
import com.lyp.membersystem.net.Errors;
import com.lyp.membersystem.ui.MemberActivity;
import com.lyp.membersystem.ui.NoticeActivity;
import com.lyp.membersystem.ui.SettingsActivity;
import com.lyp.membersystem.utils.Constant;
import com.lyp.membersystem.view.CustomPopupWindow;
import com.sj.activity.ActivityCardBag;
import com.sj.activity.ActivityEditUserInfo;
import com.sj.activity.ActivitySetting;
import com.sj.activity.MessageActivity;
import com.sj.activity.base.ActivityBase;
import com.sj.activity.bean.UserBean;
import com.sj.http.BaseResponse;
import com.sj.http.Callback;
import com.sj.http.FileCallback;
import com.sj.http.GsonResponsePasare;
import com.sj.http.HttpUtils;
import com.sj.http.UrlConfig;
import com.sj.utils.ImageUtils;
import com.sj.widgets.ImageDialog;
import com.yuntongxun.ecdemo.common.utils.FileAccessor;
import com.yuntongxun.ecdemo.common.utils.ToastUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Created by SunJ on 2018/5/11.
 */

public class FragmentMyNew extends TakePhotoFragment implements View.OnClickListener {

    View view;

    /**
     * title
     */
    ImageView back;
    TextView tvTitle;
    ImageView right;
    /**
     * user info
     */
    ImageView imgUserHeader;
    TextView txtUserName;
    ImageView imgUserQHeader;
    ImageView imgUserSign;
    TextView tvRenewalTime;
    TextView tvWalletValue;

    CustomPopupWindow mPopWin;

    SharedPreferences mSharedPreferences;
    String tokenId;
    UserBean userBean;
    ImageDialog imageDialog;

    public static FragmentMyNew newInstance() {
        return new FragmentMyNew();
    }

    public FragmentMyNew() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSharedPreferences = getActivity().getSharedPreferences(Constant.SHARED_PREFERENCE, getActivity().MODE_PRIVATE);
        tokenId = mSharedPreferences.getString(Constant.TOKEN_ID, "");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_my_new, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        getUserData();
    }

    private void initView() {
        back = view.findViewById(R.id.back);
        tvTitle = view.findViewById(R.id.tv_title);
        right = view.findViewById(R.id.right);
        imgUserHeader = view.findViewById(R.id.user_avater);
        txtUserName = view.findViewById(R.id.txt_user_name);
        imgUserQHeader = view.findViewById(R.id.q_iv);
        imgUserSign =  view.findViewById(R.id.iv_signature);
        tvRenewalTime = view.findViewById(R.id.txt_renewal_time);
        tvWalletValue =  view.findViewById(R.id.txt_my_wallet_value);

        view.findViewById(R.id.user_avater).setOnClickListener(this);
        view.findViewById(R.id.img_edit).setOnClickListener(this);
        view.findViewById(R.id.img_qcode).setOnClickListener(this);


        view.findViewById(R.id.bt_renewal).setOnClickListener(this);
        view.findViewById(R.id.layout_card_bag).setOnClickListener(this);
        view.findViewById(R.id.layout_system_message).setOnClickListener(this);
        view.findViewById(R.id.layout_custom).setOnClickListener(this);

        tvTitle.setText("我的");
        right.setImageResource(R.drawable.bqmm_setting2x);
        right.setVisibility(View.VISIBLE);
        right.setOnClickListener(this);

    }

    private void updateUserView() {
        if (userBean == null) {
            return;
        }
        ImageUtils.loadImageWithError(userBean.getAvatar(), R.drawable.personal, imgUserHeader);
        txtUserName.setText(TextUtils.isEmpty(userBean.getName()) ? userBean.getPhone() : userBean.getName());
        ImageUtils.loadImageWithError(userBean.getQgraph(), R.drawable.default_q_icon, imgUserQHeader);
        ImageUtils.loadImageView(userBean.getSignature(), imgUserSign);
        if (userBean.getType() == 1) {
            String status = "";
            switch (userBean.getStatus()) {
                case 1:
                    status = "正常";
                    break;
                case 2:
                    status = "即将到期，请续费";
                    break;
                case 3:
                    status = "已到期";
                    break;
                default:
            }
            tvRenewalTime.setText(status);
        }
        tvWalletValue.setText(userBean.getBalance() + "元");
    }


    private void getUserData() {
        Map<String, Object> parameters = new ArrayMap<>(1);
        parameters.put("token_id", tokenId);
        HttpManager.get(UrlConfig.MEMBER_INFO, parameters, new Callback() {
            @Override
            public void onSuccess(String message) {
                Log.d(TAG, "onSuccess: ");
            }

            @Override
            public void onSuccessData(String json) {
                userBean = new GsonResponsePasare<UserBean>() {
                }.deal(json);
                updateUserView();
            }

            @Override
            public void onFailure(String error_code, String error_message) {
                Log.d(TAG, "onSuccessData: ");

            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        Intent intent = new Intent();
        switch (id) {
            case R.id.right:
                intent.setClass(v.getContext(), ActivitySetting.class);
                startActivity(intent);
                break;
            case R.id.user_avater:
                uploadAvater(v);
                break;
            case R.id.img_edit:
                if (userBean == null) {
                    ToastUtil.showMessage("未获取到用户信息");
                    break;
                }
                intent.setClass(v.getContext(), ActivityEditUserInfo.class);
                intent.putExtra("data", userBean);
                startActivity(intent);
                break;
            case R.id.img_qcode:
                if (userBean == null || TextUtils.isEmpty(userBean.getQrCode())) {
                    ToastUtil.showMessage("暂无二维码");
                    return;
                }
                if (imageDialog == null) {
                    imageDialog = new ImageDialog(getActivity());
                }
                imageDialog.show(userBean.getQrCode());
                break;
            case R.id.bt_renewal:
                intent.setClass(v.getContext(), MemberActivity.class);
                startActivity(intent);
                break;
            case R.id.layout_card_bag:
                intent.setClass(v.getContext(), ActivityCardBag.class);
                startActivity(intent);
                break;
            case R.id.layout_system_message:
                intent.setClass(v.getContext(), MessageActivity.class);
                startActivity(intent);
                break;
            case R.id.layout_custom:
                intent.setClass(v.getContext(), NoticeActivity.class);
                startActivity(intent);
                break;

        }

    }

    public void uploadAvater(View v) {
        View view = null;
        if (mPopWin == null) {
            view = getLayoutInflater().inflate(R.layout.set_avater_popupwindow, null);
            mPopWin = new CustomPopupWindow(view, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, getActivity());
        } else {
            view = mPopWin.getPopupWindow().getContentView();
        }
        mPopWin.showAsDropDown(v);
        view.findViewById(R.id.invoke_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopWin.dismiss();
                getPhoto(0, getTakePhoto());
            }
        });
        view.findViewById(R.id.select_from_gallery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopWin.dismiss();
                getPhoto(1, getTakePhoto());
            }
        });
    }

    public void uploadAvater(final String path) {
        if (TextUtils.isEmpty(path)) {
            ToastUtil.showMessage("图片为空");
            return;
        }
        ((ActivityBase) getActivity()).showProgress();
        Uri.Builder builder = Uri.parse(API.API_UPLOAD_AVATER).buildUpon();
        builder.appendQueryParameter("token_id", tokenId);
        HttpUtils.getInstance().uploadFileFullPath(builder.toString(), path, "", new FileCallback() {
            @Override
            public void onNext(String result) {
               List<String> imgPaths = new GsonResponsePasare< List<String>>() {
                }.deal(result);
               if (imgPaths==null||imgPaths.isEmpty()){
                   ToastUtil.showMessage("未获取到图片地址");
                   ((ActivityBase) getActivity()).hideProgress();
               }else{
                   ImageUtils.loadImageWithError(imgPaths.get(0), R.drawable.personal, imgUserHeader);
                   updateUserInfo("avatar",imgPaths.get(0));
               }
            }

            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(Throwable throwable, String content) {
                ((ActivityBase) getActivity()).hideProgress();
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
                ToastUtil.showMessage(message);
            }
            @Override
            public void onSuccessData(String json) {
            }
            @Override
            public void onFailure(String error_code, String error_message) {
                Log.d(TAG, "onSuccessData: ");
                ToastUtil.showMessage(error_message);
            }
            @Override
            public void onFinish() {
                super.onFinish();
                ((ActivityBase) getActivity()).hideProgress();
            }
        });
    }

    private void getPhoto(int i, TakePhoto takePhoto) {
        File file = new File(FileAccessor.IMESSAGE_IMAGE, "/" + System.currentTimeMillis() + ".jpg");
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        Uri imageUri = Uri.fromFile(file);
        configCompress(getTakePhoto());
        configTakePhotoOption(getTakePhoto());
        switch (i) {
            case 0:
                takePhoto.onPickFromCaptureWithCrop(imageUri, getCropOptions());
                break;
            case 1:
                takePhoto.onPickFromGalleryWithCrop(imageUri, getCropOptions());
                break;
        }
    }

    private void configTakePhotoOption(TakePhoto takePhoto) {
        TakePhotoOptions.Builder builder = new TakePhotoOptions.Builder();
        builder.setWithOwnGallery(true);
        takePhoto.setTakePhotoOptions(builder.create());
    }

    private void configCompress(TakePhoto takePhoto) {
        int maxSize = 1024 * 100;
        int width = 200;
        int height = 200;
        CompressConfig config = new CompressConfig.Builder().setMaxSize(maxSize)
                .setMaxPixel(width >= height ? width : height)
                .enableReserveRaw(true)
                .create();
        takePhoto.onEnableCompress(config, true);
    }

    private CropOptions getCropOptions() {
        int height = 200;
        int width = 200;
        CropOptions.Builder builder = new CropOptions.Builder();
        builder.setOutputX(width).setOutputY(height);
        builder.setWithOwnCrop(true);
        return builder.create();
    }

    @Override
    public void takeCancel() {
        super.takeCancel();
//        ToastUtil.showMessage("取消操作");
    }

    @Override
    public void takeFail(TResult result, String msg) {
        super.takeFail(result, msg);
//        ToastUtil.showMessage(msg);
    }

    @Override
    public void takeSuccess(TResult result) {
        super.takeSuccess(result);
        String path = result.getImage().getOriginalPath();
        Log.d("takePhoto", "成功：" + result.getImages().size() + "--" + path);
        uploadAvater(path);

    }


}
