package com.sj.activity.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.lyp.membersystem.R;
import com.lyp.membersystem.ui.NoticeActivity;
import com.lyp.membersystem.ui.SettingsActivity;
import com.lyp.membersystem.view.CustomPopupWindow;
import com.sj.activity.ActivityCardBag;
import com.sj.activity.MessageActivity;
import com.yuntongxun.ecdemo.common.utils.FileAccessor;
import com.yuntongxun.ecdemo.common.utils.ToastUtil;

import org.devio.takephoto.app.TakePhoto;
import org.devio.takephoto.app.TakePhotoFragment;
import org.devio.takephoto.compress.CompressConfig;
import org.devio.takephoto.model.CropOptions;
import org.devio.takephoto.model.LubanOptions;
import org.devio.takephoto.model.TResult;
import org.devio.takephoto.model.TakePhotoOptions;

import java.io.File;

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

    CustomPopupWindow mPopWin;

    public static FragmentMyNew newInstance() {
        return new FragmentMyNew();
    }

    public FragmentMyNew() {
        // Required empty public constructor
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
        back = (ImageView) view.findViewById(R.id.back);
        tvTitle = (TextView) view.findViewById(R.id.tv_title);
        right = (ImageView) view.findViewById(R.id.right);
        imgUserHeader = (ImageView) view.findViewById(R.id.user_avater);
        txtUserName = (TextView) view.findViewById(R.id.txt_user_name);
        imgUserHeader = (ImageView) view.findViewById(R.id.q_iv);

        view.findViewById(R.id.user_avater).setOnClickListener(this);
        view.findViewById(R.id.img_edit).setOnClickListener(this);
        view.findViewById(R.id.img_qcode).setOnClickListener(this);


        view.findViewById(R.id.layout_my_wallet).setOnClickListener(this);
        view.findViewById(R.id.layout_system_message).setOnClickListener(this);
        view.findViewById(R.id.layout_custom).setOnClickListener(this);

        tvTitle.setText("我的");
        right.setImageResource(R.drawable.bqmm_setting2x);
        right.setOnClickListener(this);

    }

    private void getUserData() {
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        Intent intent = new Intent();
        switch (id) {
            case R.id.right:
                intent.setClass(v.getContext(), SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.user_avater:
                uploadAvater(v);
                break;
            case R.id.img_edit:
                break;
            case R.id.img_qcode:
                break;
            case R.id.layout_my_wallet:
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
        boolean showProgressBar = true;
        boolean enableRawFile = true;
        CompressConfig config;
        LubanOptions option = new LubanOptions.Builder().setMaxHeight(height).setMaxWidth(width).setMaxSize(maxSize).create();
        config = CompressConfig.ofLuban(option);
        config.enableReserveRaw(enableRawFile);
        takePhoto.onEnableCompress(config, showProgressBar);
    }

    private CropOptions getCropOptions() {
        int height = 200;
        int width = 200;
        boolean withWonCrop = true;
        CropOptions.Builder builder = new CropOptions.Builder();
        builder.setOutputX(width).setOutputY(height);
        builder.setWithOwnCrop(withWonCrop);
        return builder.create();
    }

    @Override
    public void takeCancel() {
        super.takeCancel();
        ToastUtil.showMessage("取消操作");
    }

    @Override
    public void takeFail(TResult result, String msg) {
        super.takeFail(result, msg);
        ToastUtil.showMessage("出现异常");
    }

    @Override
    public void takeSuccess(TResult result) {
        super.takeSuccess(result);
        ToastUtil.showMessage("成功："+result.getImages().size()+"--"+result.getImage().getOriginalPath());

    }


}
