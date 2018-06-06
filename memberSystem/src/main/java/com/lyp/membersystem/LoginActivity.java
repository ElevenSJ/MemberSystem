package com.lyp.membersystem;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.lyp.membersystem.base.BaseActivity;
import com.lyp.membersystem.log.LogUtils;
import com.lyp.membersystem.net.Errors;
import com.lyp.membersystem.net.MessageContants;
import com.lyp.membersystem.net.NetProxyManager;
import com.lyp.membersystem.utils.Constant;
import com.lyp.membersystem.utils.SystemStatusManager;
import com.lyp.membersystem.utils.ToastUtil;
import com.lyp.membersystem.view.dialog.WaitDialog;
import com.sj.activity.ActivityRegister;
import com.sj.activity.ActivityMain;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import org.json.JSONObject;

import java.util.List;

public class LoginActivity extends BaseActivity {

    private WaitDialog mWaitDialog;
    private EditText phone_number_et;
    private EditText verify_code_et;
    private String phone;
    private SharedPreferences mSharedPreferences;
    private boolean isPause = false;

    private Handler mainHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MessageContants.MSG_LOGIN: {
                    parseLoginInfo((String) msg.obj);
                    break;
                }
                case MessageContants.MSG_GET_SMS_AUTH_CODE: {
                    parseSMSAuthCodeInfo((String) msg.obj);
                    break;
                }
                default:
                    break;
            }
        }

        ;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTranslucentStatus();
        setContentView(R.layout.login_layout);
        initView();
        AndPermission.with(this)
                .permission(Permission.WRITE_EXTERNAL_STORAGE,
                        Permission.READ_EXTERNAL_STORAGE,
                        Permission.READ_PHONE_STATE,
                        Permission.ACCESS_COARSE_LOCATION,
                        Permission.CAMERA,
                        Permission.READ_PHONE_STATE,
                        Permission.ACCESS_FINE_LOCATION,
                        Permission.CALL_PHONE,
                        Permission.RECORD_AUDIO)
                .onGranted(new Action() {
                    @Override
                    public void onAction(List<String> permissions) {
                        // TODO what to do.
                    }
                }).onDenied(new Action() {
            @Override
            public void onAction(List<String> permissions) {
                // TODO what to do
            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isPause = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isPause = true;
    }

    private void initView() {
        mSharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCE, MODE_PRIVATE);
        phone_number_et = (EditText) findViewById(R.id.phone_number_et);
        verify_code_et = (EditText) findViewById(R.id.verify_code_et);
    }

    public void getVerifyCode(View view) {
        Editable phoneET = phone_number_et.getText();
        if (phoneET == null) {
            ToastUtil.showLong(LoginActivity.this, "号码不能为空");
            return;
        }
        phone = phoneET.toString();
        if (phone.length() <= 0) {
            ToastUtil.showLong(LoginActivity.this, "号码不能为空");
            return;
        }
        if (mWaitDialog == null) {
            mWaitDialog = new WaitDialog(LoginActivity.this, R.string.loading_data);
        }
        if (!isPause) {
            mWaitDialog.show();
        }
        NetProxyManager.getInstance().toGetSMSAuthCode(mainHandler, phone);
    }

    public void toRegister(View view) {
        Intent intent = new Intent(this, ActivityRegister.class);
        startActivityForResult(intent, 101);
    }

    public void login(View view) {
//		loginSuccess();

        Editable verifyCodeET = verify_code_et.getText();
        if (verifyCodeET == null) {
            ToastUtil.showLong(LoginActivity.this, "验证码不能为空");
            return;
        }
        Editable phoneET = phone_number_et.getText();
        if (phoneET == null) {
            ToastUtil.showLong(LoginActivity.this, "号码不能为空");
            return;
        }
        phone = phoneET.toString();
        if (phone.length() <= 0) {
            ToastUtil.showLong(LoginActivity.this, "号码不能为空");
            return;
        }
        if (mWaitDialog == null) {
            mWaitDialog = new WaitDialog(LoginActivity.this, R.string.loading_data);
        }
        if (!isPause) {
            mWaitDialog.show();
        }
        NetProxyManager.getInstance().toLogin(mainHandler, phone, verifyCodeET.toString());
    }

    private void loginSuccess() {
        Intent intent = new Intent();
        intent.setClass(LoginActivity.this, ActivityMain.class);
        startActivity(intent);
        finish();
    }

    /**
     * 设置状态栏背景状态
     */
    private void setTranslucentStatus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // Window win = getWindow();
            // WindowManager.LayoutParams winParams = win.getAttributes();
            // final int bits =
            // WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            // winParams.flags |= bits;
            // win.setAttributes(winParams);

            // 透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        SystemStatusManager tintManager = new SystemStatusManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(android.R.color.white);// 状态栏无背景
        getWindow().getDecorView().setFitsSystemWindows(true);
    }

    /**
     * to parse get sms auth code
     */
    private void parseSMSAuthCodeInfo(String result) {
        if (Errors.ERROR_NET.equals(result) || Errors.ERROR_SERVER.equals(result)) {
            ToastUtil.showLong(this, R.string.network_error);
            if (mWaitDialog != null && mWaitDialog.isShowing()) {
                mWaitDialog.dismiss();
            }
            return;
        }
        // to parser json data
        try {
            JSONObject json = new JSONObject(result);
            boolean success = json.getBoolean("success");
            if (mWaitDialog != null && mWaitDialog.isShowing()) {
                mWaitDialog.dismiss();
            }
            LogUtils.d("" + json);
            if (!success) {
                String message = json.getString("message");
                ToastUtil.showShort(this, message);
                return;
            }
            ToastUtil.showLong(LoginActivity.this, "发送成功，请等待验证码。");
        } catch (Exception ex) {
            LogUtils.e(ex.getMessage());
            return;
        }
    }

    /**
     * to parse login infomations from network
     */
    private void parseLoginInfo(String result) {
        if (Errors.ERROR_NET.equals(result) || Errors.ERROR_SERVER.equals(result)) {
            ToastUtil.showLong(this, R.string.network_error);
            if (mWaitDialog != null && mWaitDialog.isShowing()) {
                mWaitDialog.dismiss();
            }
            return;
        }
        // to parser json data
        try {
            JSONObject json = new JSONObject(result);
            boolean success = json.getBoolean("success");
            if (mWaitDialog != null && mWaitDialog.isShowing()) {
                mWaitDialog.dismiss();
            }
            if (!success) {
                String message = json.getString("message");
                ToastUtil.showShort(this, message);
                return;
            }
            JSONObject jot = json.getJSONObject("object");
            String id = jot.getString("id");
            String name = jot.getString("name");
            String token = jot.getString("token_id");
            Editor editor = mSharedPreferences.edit();
            editor.putBoolean(Constant.IS_FIRST, false);
            editor.putString(Constant.TOKEN_ID, token);
            editor.putString(Constant.ID, id);
            editor.putString(Constant.USER_NAME, name);
            editor.putString(Constant.USER_ACCOUNT, phone);
            editor.commit();
            loginSuccess();
        } catch (Exception ex) {
            LogUtils.e(ex.getMessage());
            return;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 101) {
                if (data != null) {
                    String phoneNum = data.getStringExtra("phoneNum");
                    phone_number_et.setText(phoneNum);
                }
            }
        }
    }
}
