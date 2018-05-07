package com.sj.activity.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lyp.membersystem.R;
import com.lyp.membersystem.imagepicker.ChooseImageActivity;
import com.lyp.membersystem.log.LogUtils;
import com.lyp.membersystem.manager.ImageManager;
import com.lyp.membersystem.net.Errors;
import com.lyp.membersystem.net.MessageContants;
import com.lyp.membersystem.net.NetProxyManager;
import com.lyp.membersystem.ui.MemberActivity;
import com.lyp.membersystem.ui.NoticeActivity;
import com.lyp.membersystem.ui.OrderActivity;
import com.lyp.membersystem.ui.ServiceApplyActivity;
import com.lyp.membersystem.ui.ServiceOrderActivity;
import com.lyp.membersystem.ui.SetExpressActivity;
import com.lyp.membersystem.ui.SettingsActivity;
import com.lyp.membersystem.utils.Constant;
import com.lyp.membersystem.utils.PermissionsChecker;
import com.lyp.membersystem.utils.PhotoUtil;
import com.lyp.membersystem.utils.ToastUtil;
import com.lyp.membersystem.view.CircleImageView;
import com.lyp.membersystem.view.CustomPopupWindow;
import com.lyp.membersystem.view.dialog.WaitDialog;
import com.sj.activity.ActivityCardBag;
import com.sj.activity.base.FragmentBase;
import com.yuntongxun.ecdemo.common.utils.LogUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * 创建时间: on 2018/5/4.
 * 创建人: 孙杰
 * 功能描述:
 */
public class FragmentMy extends FragmentBase implements OnClickListener {

    private static final String PHOTO_DATE_FORMAT = "'AVATER'_yyyyMMdd_HHmmss";
    private static final String AVATER_PATH = Environment.getExternalStorageDirectory() + "/membersystem/";
    private static final int TAKE_PHOTO = 1000;
    private static final int SELECT_PHOTO = 1001;
    private static final int CROP_PHOTO = 1002;
    private static final int CHOOSE_IMAGE = 1003;
    private static final int REQUEST_CAPTURE = 1004;
    private static final int REQUEST_PICTURE = 1005;
    private static final int CHOOSE_Q_IMAGE = 1006;
    private static final int TAKE_PHOTO_SIGN = 1007;
    private static final int CHOOSE_IMAGE_SIGN = 1008;
    private static final int CROP_PHOTO_SIGN = 1009;

    private PermissionsChecker mPermissionsChecker; // 权限检测器
    private static final int REQUEST_PERMISSION = 1006; // 权限请求
    static final String[] PERMISSIONS = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
    private PhotoUtil photoUtil;
    private CustomPopupWindow mPopWin;
    private Uri imageUri;
    private String cropImageUri;
    private Bitmap avaterBitmap = null;
    private WaitDialog mWaitDialog;
    private SharedPreferences mSharedPreferences;
    private TextView tv_username;
    private CircleImageView user_avater;
    private String id;
    private ImageView q_iv;
    private String userName = "";
    private ImageView iv_signature;
    private ImageView iv_signature_base;

    private Handler mainHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MessageContants.MSG_GET_PERSON_INFO: {
                    parseGetPersonInfo((String) msg.obj);
                    break;
                }
                case MessageContants.MSG_UPLOAD_AVATER: {
                    parseUploadInfo((String) msg.obj);
                    break;
                }
                case MessageContants.MSG_UPDATE_PERSON_INFO: {
                    parseUpdatePersionInfo((String) msg.obj);
                    break;
                }
                case MessageContants.MSG_UPLOAD_Q_IMG: {
                    parseUploadQGraphInfo((String) msg.obj);
                    break;
                }
                case MessageContants.MSG_UPLOAD_SIGN: {
                    parseUploadSignInfo((String) msg.obj);
                    break;
                }
                default:
                    break;
            }
        }

        ;
    };

    public static FragmentMy newInstance() {
        return new FragmentMy();
    }

    public FragmentMy() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_my, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initData();
    }

    private void initView() {

        iv_signature = (ImageView) findViewById(R.id.iv_signature);
        iv_signature_base = (ImageView) findViewById(R.id.iv_signature_base);
        iv_signature_base.setVisibility(View.GONE);
        tv_username = (TextView) findViewById(R.id.tv_username);
        user_avater = (CircleImageView) findViewById(R.id.user_avater);
        q_iv = (ImageView) findViewById(R.id.q_iv);
        mPermissionsChecker = new PermissionsChecker(getHoldingActivity());
        photoUtil = new PhotoUtil(getHoldingActivity());
        user_avater.setOnClickListener(this);
        findViewById(R.id.signatureBtn).setOnClickListener(this);
        findViewById(R.id.renewalBtn).setOnClickListener(this);
        findViewById(R.id.layout_queryapplyservice).setOnClickListener(this);
        findViewById(R.id.layout_cardPackBtn).setOnClickListener(this);
        findViewById(R.id.layout_notForget).setOnClickListener(this);
        findViewById(R.id.layout_settings).setOnClickListener(this);
    }

    private void initData() {
        mSharedPreferences = getHoldingActivity().getSharedPreferences(Constant.SHARED_PREFERENCE, getHoldingActivity().MODE_PRIVATE);
        String tokenid = mSharedPreferences.getString(Constant.TOKEN_ID, "");
        String saleId = mSharedPreferences.getString(Constant.ID, "");
        if (mWaitDialog == null) {
            mWaitDialog = new WaitDialog(getHoldingActivity(), R.string.loading_data);
        }
        mWaitDialog.show();
        NetProxyManager.getInstance().toGetPersonInfo(mainHandler, tokenid, saleId);
    }

    public void signature(View view) {
        View inflate = getLayoutInflater().inflate(R.layout.set_avater_popupwindow, null);
        mPopWin = new CustomPopupWindow(inflate, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, getHoldingActivity());
        mPopWin.showAsDropDown(iv_signature);
        inflate.findViewById(R.id.invoke_camera).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopWin.dismiss();
                imageUri = photoUtil.takePhoto(TAKE_PHOTO_SIGN);
            }
        });

        inflate.findViewById(R.id.select_from_gallery).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopWin.dismiss();
                Intent intent = new Intent(getHoldingActivity(), ChooseImageActivity.class);
                intent.putExtra("radio", true);
                startActivityForResult(intent, CHOOSE_IMAGE_SIGN);
            }
        });
    }


    public void uploadAvater(View view) {
        View inflate = getLayoutInflater().inflate(R.layout.set_avater_popupwindow, null);
        mPopWin = new CustomPopupWindow(inflate, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, getHoldingActivity());
        mPopWin.showAsDropDown(user_avater);
        inflate.findViewById(R.id.invoke_camera).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopWin.dismiss();
                // 检查权限(6.0以上做权限判断)
                imageUri = photoUtil.takePhoto(TAKE_PHOTO);
            }
        });

        inflate.findViewById(R.id.select_from_gallery).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopWin.dismiss();
                // Intent intentFromGallery = new Intent();
                // intentFromGallery.setType("image/*");
                // intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
                // startActivityForResult(intentFromGallery, SELECT_PHOTO);

                // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // if (mPermissionsChecker.lacksPermissions(PERMISSIONS)) {
                // startPermissionsActivity();
                // } else {
                // // 打开相册
                // photoUtil.callPhoto(SELECT_PHOTO);
                // }
                // } else {
                // photoUtil.callPhoto(SELECT_PHOTO);
                // }

                Intent intent = new Intent(getHoldingActivity(), ChooseImageActivity.class);
                intent.putExtra("radio", true);
                startActivityForResult(intent, CHOOSE_IMAGE);
            }
        });
    }


    public String generateTempPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat(PHOTO_DATE_FORMAT);
        return dateFormat.format(date) + ".jpg";
    }

    public void memberBuy(View view) {
        Intent intent = new Intent();
        intent.setClass(getHoldingActivity(), MemberActivity.class);
        startActivity(intent);
    }

    public void serviceBtn(View view) {
        Intent intent = new Intent();
        intent.setClass(getHoldingActivity(), ServiceOrderActivity.class);
        startActivity(intent);
    }

    public void cardPackBtn(View view) {
        Intent intent = new Intent();
        intent.setClass(getHoldingActivity(), ActivityCardBag.class);
        startActivity(intent);
    }

    public void expressAddressBtn(View view) {
        Intent intent = new Intent();
        intent.setClass(getHoldingActivity(), SetExpressActivity.class);
        startActivity(intent);
    }

    public void queryOrder(View view) {
        Intent intent = new Intent();
        intent.setClass(getHoldingActivity(), OrderActivity.class);
        startActivity(intent);
    }

    public void queryApplyService(View view) {
        Intent i = new Intent();
        i.setClass(getHoldingActivity(), ServiceApplyActivity.class);
        startActivity(i);
    }

    public void notForget(View view) {
        Intent intent = new Intent();
        intent.setClass(getHoldingActivity(), NoticeActivity.class);
        startActivity(intent);
    }

    public void settings(View view) {
        Intent intent = new Intent();
        intent.setClass(getHoldingActivity(), SettingsActivity.class);
        startActivity(intent);
    }

    public void setCropImageIntent(Uri data) {
        File file = new File(AVATER_PATH);
        if (!file.exists()) {
            file.mkdirs();
        }
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(data, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 118);
        intent.putExtra("outputY", 118);
        intent.putExtra("scale", true);
        intent.putExtra("scaleUpIfNeeded", true);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, CROP_PHOTO);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtil.d("lyp",
                "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode + ", data=" + data);
        if (resultCode == getHoldingActivity().RESULT_OK) {
            switch (requestCode) {
                case R.id.tv_username:
                    userName = data.getStringExtra("content");
                    updatePersonInfo(null, null, userName, null, null, null, null, null);
                    return;
            }
        }
        switch (requestCode) {
            case TAKE_PHOTO:
                if (imageUri != null) {
                    cropImageUri = photoUtil.startPhotoZoom(CROP_PHOTO, imageUri);
                }
                break;
            case SELECT_PHOTO:
                String imgPath = photoUtil.getCallPhoto(data);
                if (!TextUtils.isEmpty(imgPath)) {
                    imageUri = Uri.parse(imgPath);
                    cropImageUri = photoUtil.startPhotoZoom(CROP_PHOTO, imageUri);
                } else {
                    ToastUtil.showShort(getHoldingActivity(), R.string.set_avater_fail);
                }
                break;
            case CHOOSE_IMAGE:
                if (resultCode != getHoldingActivity().RESULT_OK) {
                    return;
                }
                ArrayList<String> images = data.getStringArrayListExtra("choose_images");
                if (images.size() <= 0) {
                    ToastUtil.showShort(getHoldingActivity(), R.string.set_avater_fail);
                    return;
                }
                LogUtils.d("chooseImage: " + images.get(0));
                cropImageUri = photoUtil.startPhotoZoom(CROP_PHOTO, images.get(0));
                break;
            case CHOOSE_Q_IMAGE:
                if (resultCode != getHoldingActivity().RESULT_OK) {
                    return;
                }
                ArrayList<String> qImages = data.getStringArrayListExtra("choose_images");
                if (qImages.size() <= 0) {
                    ToastUtil.showShort(getHoldingActivity(), "设置Q图失败！");
                    return;
                }
                LogUtils.d("chooseImage: " + qImages.get(0));
                String token_id = mSharedPreferences.getString(Constant.TOKEN_ID, "");
                if (mWaitDialog == null) {
                    mWaitDialog = new WaitDialog(getHoldingActivity(), R.string.loading_data);
                }
                mWaitDialog.show();
                String q_path = PhotoUtil.compressPicture(qImages.get(0));
                if (q_path == null) {
                    // mWaitDialog.dismiss();
                    q_path = qImages.get(0);
                }
                NetProxyManager.getInstance().toUploadQImg(mainHandler, token_id, q_path);
                break;
            case CROP_PHOTO:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (cropImageUri == null) {
                        return;
                    }
                    String tokenid = mSharedPreferences.getString(Constant.TOKEN_ID, "");
                    String saleId = mSharedPreferences.getString(Constant.ID, "");
                    if (mWaitDialog == null) {
                        mWaitDialog = new WaitDialog(getHoldingActivity(), R.string.loading_data);
                    }
                    mWaitDialog.show();
                    NetProxyManager.getInstance().toUploadAvater(mainHandler, tokenid, cropImageUri);
                    return;
                }
                if (data == null) {
                    return;
                }
                avaterBitmap = data.getParcelableExtra("data");
                if (saveBitmap(avaterBitmap)) {
                    // mSharedPreferences =
                    // getSharedPreferences(Constant.SHARED_PREFERENCE,
                    // MODE_PRIVATE);
                    String tokenid = mSharedPreferences.getString(Constant.TOKEN_ID, "");
                    String saleId = mSharedPreferences.getString(Constant.ID, "");
                    if (mWaitDialog == null) {
                        mWaitDialog = new WaitDialog(getHoldingActivity(), R.string.loading_data);
                    }
                    mWaitDialog.show();
                    NetProxyManager.getInstance().toUploadAvater(mainHandler, tokenid, AVATER_PATH + "userAvater.png");
                    // user_avater.setImageBitmap(avaterBitmap);
                } else {
                    ToastUtil.showShort(getHoldingActivity(), R.string.set_avater_fail);
                }
                break;
            case TAKE_PHOTO_SIGN:
                if (imageUri != null) {
                    cropImageUri = photoUtil.startSignPhotoZoom(CROP_PHOTO_SIGN, imageUri);
                }
                break;
            case CHOOSE_IMAGE_SIGN:
                if (resultCode != getHoldingActivity().RESULT_OK) {
                    return;
                }
                ArrayList<String> imagesSign = data.getStringArrayListExtra("choose_images");
                if (imagesSign.size() <= 0) {
                    ToastUtil.showShort(getHoldingActivity(), "设置签名失败");
                    return;
                }
                LogUtils.d("chooseImage: " + imagesSign.get(0));
                cropImageUri = photoUtil.startSignPhotoZoom(CROP_PHOTO_SIGN, imagesSign.get(0));
                break;
            case CROP_PHOTO_SIGN:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (cropImageUri == null) {
                        return;
                    }
                    String tokenid = mSharedPreferences.getString(Constant.TOKEN_ID, "");
                    String saleId = mSharedPreferences.getString(Constant.ID, "");
                    if (mWaitDialog == null) {
                        mWaitDialog = new WaitDialog(getHoldingActivity(), R.string.loading_data);
                    }
                    mWaitDialog.show();
                    NetProxyManager.getInstance().toUploadSign(mainHandler, tokenid, cropImageUri);
                    return;
                }
                if (data == null) {
                    return;
                }
                avaterBitmap = data.getParcelableExtra("data");
                if (saveSignBitmap(avaterBitmap)) {
                    String tokenid = mSharedPreferences.getString(Constant.TOKEN_ID, "");
                    String saleId = mSharedPreferences.getString(Constant.ID, "");
                    if (mWaitDialog == null) {
                        mWaitDialog = new WaitDialog(getHoldingActivity(), R.string.loading_data);
                    }
                    mWaitDialog.show();
                    NetProxyManager.getInstance().toUploadSign(mainHandler, tokenid, AVATER_PATH + "userSign.png");
                } else {
                    ToastUtil.showShort(getHoldingActivity(), "设置签名失败");
                }
                break;
            default:
                break;
        }
    }

    private void updatePersonInfo(String phone, String email, String name, String cpmpanyStr, String birthday,
                                  String job, String businessAddress, String companyAddress) {
        String tokenid = mSharedPreferences.getString(Constant.TOKEN_ID, "");
        String saleId = mSharedPreferences.getString(Constant.ID, "");
        mWaitDialog = new WaitDialog(getHoldingActivity(), R.string.loading_press);
        mWaitDialog.show();
        NetProxyManager.getInstance().toUpdatePersonInfo(mainHandler, tokenid, null, id, phone, email, name, cpmpanyStr,
                birthday, job, businessAddress, companyAddress, null, null);
    }


    private boolean saveBitmap(Bitmap bitmap) {
        File f = new File(AVATER_PATH, "userAvater.png");
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private boolean saveSignBitmap(Bitmap bitmap) {
        File f = new File(AVATER_PATH, "userSign.png");
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * to parse person infomations from network
     */
    private void parseGetPersonInfo(String result) {
        if (Errors.ERROR_NET.equals(result) || Errors.ERROR_SERVER.equals(result)) {
            ToastUtil.showLong(getHoldingActivity(), R.string.network_error);
            if (mWaitDialog != null && mWaitDialog.isShowing()) {
                mWaitDialog.dismiss();
            }
            return;
        }
        // to parser json data
        try {
            JSONObject json = new JSONObject(result);
            LogUtils.d("Json: " + json);
            boolean success = json.getBoolean("success");
            if (mWaitDialog != null && mWaitDialog.isShowing()) {
                mWaitDialog.dismiss();
            }
            if (!success) {
                String message = json.getString("message");
                ToastUtil.showShort(getHoldingActivity(), message);
                if (json.getString("resCode").equals(Constant.RELOGIN)) {
                    getHoldingActivity().backLogin();
                }
                return;
            }
            JSONObject obj = json.getJSONObject("object");
            id = obj.getString("id");
            userName = obj.getString("name");
            tv_username.setText(userName);
            if (obj.has("avatar")) {
                ImageManager.loadImage(obj.getString("avatar"), user_avater, R.drawable.avater_login);
            }
            if (obj.has("qgraph")) {
                ImageManager.loadImage(obj.getString("qgraph"), q_iv, R.drawable.default_q_icon);
            }
            if (obj.has("signature")) {
                ImageManager.loadDefaultImage(obj.getString("signature"), iv_signature);
            }
            if (obj.has("signaturebasemap")) {
                ImageManager.loadDefaultImage(obj.getString("signaturebasemap"), iv_signature_base);
            }
        } catch (Exception ex) {
            LogUtils.e(ex.getMessage());
            return;
        }
    }

    /**
     * to parse upload avater from network
     */
    private void parseUploadInfo(String result) {
        if (Errors.ERROR_NET.equals(result) || Errors.ERROR_SERVER.equals(result)) {
            ToastUtil.showLong(getHoldingActivity(), R.string.network_error);
            if (mWaitDialog != null && mWaitDialog.isShowing()) {
                mWaitDialog.dismiss();
            }
            return;
        }
        // to parser json data
        try {
            JSONObject json = new JSONObject(result);
            LogUtils.d("Json: " + json);
            boolean success = json.getBoolean("success");
            if (!success) {
                String message = json.getString("message");
                ToastUtil.showShort(getHoldingActivity(), message);
                if (json.getString("resCode").equals(Constant.RELOGIN)) {
                    getHoldingActivity().backLogin();
                }
                return;
            }
            JSONArray jsonArray = json.getJSONArray("object");
            if (jsonArray.length() > 0) {
                String tokenid = mSharedPreferences.getString(Constant.TOKEN_ID, "");
                String saleId = mSharedPreferences.getString(Constant.ID, "");
                NetProxyManager.getInstance().toUpdatePersonInfo(mainHandler, tokenid, null, id, null, null, null, null,
                        null, null, null, null, jsonArray.getString(0), null);
                ImageManager.loadImage(jsonArray.getString(0), user_avater, R.drawable.avater_login);
            }
        } catch (Exception ex) {
            LogUtils.e(ex.getMessage());
            return;
        } finally {
            if (mWaitDialog != null && mWaitDialog.isShowing()) {
                mWaitDialog.dismiss();
            }
        }
    }

    /**
     * to parse upload avater from network
     */
    private void parseUpdatePersionInfo(String result) {
        if (Errors.ERROR_NET.equals(result) || Errors.ERROR_SERVER.equals(result)) {
            ToastUtil.showLong(getHoldingActivity(), R.string.network_error);
            if (mWaitDialog != null && mWaitDialog.isShowing()) {
                mWaitDialog.dismiss();
            }
            return;
        }
        // to parser json data
        try {
            LogUtils.d("result: " + result);
            JSONObject json = new JSONObject(result);
            boolean success = json.getBoolean("success");
            if (mWaitDialog != null && mWaitDialog.isShowing()) {
                mWaitDialog.dismiss();
            }
            if (!success) {
                String message = json.getString("message");
                ToastUtil.showShort(getHoldingActivity(), message);
                if (json.getString("resCode").equals(Constant.RELOGIN)) {
                    getHoldingActivity().backLogin();
                }
                return;
            }
            tv_username.setText(userName);
        } catch (Exception ex) {
            LogUtils.e(ex.getMessage());
            return;
        }
    }

    public void qGraphSelect(View view) {
        Intent intent = new Intent(getHoldingActivity(), ChooseImageActivity.class);
        intent.putExtra("radio", true);
        startActivityForResult(intent, CHOOSE_Q_IMAGE);
    }

    /**
     * to parse upload avater from network
     */
    private void parseUploadQGraphInfo(String result) {
        if (Errors.ERROR_NET.equals(result) || Errors.ERROR_SERVER.equals(result)) {
            ToastUtil.showLong(getHoldingActivity(), R.string.network_error);
            if (mWaitDialog != null && mWaitDialog.isShowing()) {
                mWaitDialog.dismiss();
            }
            return;
        }
        // to parser json data
        try {
            JSONObject json = new JSONObject(result);
            LogUtils.d("Json: " + json);
            boolean success = json.getBoolean("success");
            if (!success) {
                String message = json.getString("message");
                ToastUtil.showShort(getHoldingActivity(), message);
                if (json.getString("resCode").equals(Constant.RELOGIN)) {
                    getHoldingActivity().backLogin();
                }
                return;
            }
            JSONArray jsonArray = json.getJSONArray("object");
            if (jsonArray.length() > 0) {
                String tokenid = mSharedPreferences.getString(Constant.TOKEN_ID, "");
                String saleId = mSharedPreferences.getString(Constant.ID, "");
                NetProxyManager.getInstance().toUpdatePersonInfo(mainHandler, tokenid, null, id, null, null, null, null,
                        null, null, null, null, null, jsonArray.getString(0));
                ImageManager.loadImage(jsonArray.getString(0), q_iv, R.drawable.default_q_icon);
            }
        } catch (Exception ex) {
            LogUtils.e(ex.getMessage());
            return;
        } finally {
            if (mWaitDialog != null && mWaitDialog.isShowing()) {
                mWaitDialog.dismiss();
            }
        }
    }

    /**
     * to parse upload avater from network
     */
    private void parseUploadSignInfo(String result) {
        if (Errors.ERROR_NET.equals(result) || Errors.ERROR_SERVER.equals(result)) {
            ToastUtil.showLong(getHoldingActivity(), R.string.network_error);
            if (mWaitDialog != null && mWaitDialog.isShowing()) {
                mWaitDialog.dismiss();
            }
            return;
        }
        // to parser json data
        try {
            JSONObject json = new JSONObject(result);
            LogUtils.d("Json: " + json);
            boolean success = json.getBoolean("success");
            if (!success) {
                String message = json.getString("message");
                ToastUtil.showShort(getHoldingActivity(), message);
                if (json.getString("resCode").equals(Constant.RELOGIN)) {
                    getHoldingActivity().backLogin();
                }
                return;
            }
            JSONArray jsonArray = json.getJSONArray("object");
            if (jsonArray.length() > 0) {
                String tokenid = mSharedPreferences.getString(Constant.TOKEN_ID, "");
                String saleId = mSharedPreferences.getString(Constant.ID, "");
                NetProxyManager.getInstance().toUpdatePersonSignInfo(mainHandler, tokenid, null,
                        id, jsonArray.getString(0));
                ImageManager.loadDefaultImage(jsonArray.getString(0), iv_signature);
            }
        } catch (Exception ex) {
            LogUtils.e(ex.getMessage());
            return;
        } finally {
            if (mWaitDialog != null && mWaitDialog.isShowing()) {
                mWaitDialog.dismiss();
            }
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.user_avater:
                uploadAvater(v);
                break;
            case R.id.signatureBtn:
                signature(v);
                break;
            case R.id.renewalBtn:
                memberBuy(v);
                break;
            case R.id.layout_queryapplyservice:
                queryApplyService(v);
                break;
            case R.id.layout_cardPackBtn:
                cardPackBtn(v);
                break;
            case R.id.layout_notForget:
                notForget(v);
                break;
            case R.id.layout_settings:
                settings(v);
                break;
                default:
        }
    }
}
