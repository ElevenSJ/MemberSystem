package com.sj.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.util.ArrayMap;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.jady.retrofitclient.HttpManager;
import com.lyp.membersystem.R;
import com.lyp.membersystem.utils.Constant;
import com.lyp.membersystem.utils.ToastUtil;
import com.sj.activity.base.ActivityBase;
import com.sj.http.Callback;
import com.sj.http.UrlConfig;

import java.util.Map;

public class ActivityRegister extends ActivityBase {


    private EditText phone_number_et;
    private EditText verify_code_et;
    private TextView btGetcode;
    private String phone;
    private SharedPreferences mSharedPreferences;

    private String html;
    /**
     * 倒计时60秒，一次1秒
     */
    CountDownTimer timer = new CountDownTimer(60 * 1000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            btGetcode.setText(millisUntilFinished / 1000 + "秒");
            btGetcode.setEnabled(false);
        }

        @Override
        public void onFinish() {
            btGetcode.setText(ActivityRegister.this.getText(R.string.verify_code));
            btGetcode.setEnabled(true);
        }
    };

    @Override
    public int getContentLayout() {
        // TODO Auto-generated method stub
        return R.layout.activity_register;
    }

    @Override
    public void initView() {
        mSharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCE, MODE_PRIVATE);
        phone_number_et = (EditText) findViewById(R.id.phone_number_et);
        verify_code_et = (EditText) findViewById(R.id.verify_code_et);
        btGetcode = (TextView) findViewById(R.id.get_verify_code_tv);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showProgress();
        getRegisterHtml();
    }

    private void getRegisterHtml() {
        HttpManager.get(UrlConfig.GET_REGISTER_HTML, null, new Callback() {
            @Override
            public void onSuccess(String message) {
                html = message;
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

            @Override
            public boolean enableShowToast() {
                return false;
            }
        });
    }

    public void getVerifyCode(View view) {
        Editable phoneET = phone_number_et.getText();
        if (phoneET == null) {
            ToastUtil.showLong(ActivityRegister.this, "号码不能为空");
            return;
        }
        phone = phoneET.toString();
        if (phone.length() <= 0) {
            ToastUtil.showLong(ActivityRegister.this, "号码不能为空");
            return;
        }
        timer.start();
        Map<String,Object> parameters = new ArrayMap<>(1);
        parameters.put("phone",phone);

        HttpManager.get(UrlConfig.GET_SMS_CODE, parameters, new Callback() {
            @Override
            public void onSuccess(String message) {
                ToastUtil.showMessage(message);
            }
            @Override
            public void onSuccessData(String json) {

            }
            @Override
            public void onFailure(String error_code, String error_message) {
                timer.onFinish();
            }
        });
    }

    public void toRegisterHtml(View view) {
        Intent intent = new Intent(this, RegisterHtmlActivity.class);
        intent.putExtra("html",html);
        startActivity(intent);
    }

    public void register(View view) {
        Editable verifyCodeET = verify_code_et.getText();
        if (verifyCodeET == null) {
            ToastUtil.showLong(ActivityRegister.this, "验证码不能为空");
            return;
        }
        Editable phoneET = phone_number_et.getText();
        if (phoneET == null) {
            ToastUtil.showLong(ActivityRegister.this, "号码不能为空");
            return;
        }
        phone = phoneET.toString();
        if (phone.length() <= 0) {
            ToastUtil.showLong(ActivityRegister.this, "号码不能为空");
            return;
        }
        showProgress();
        Map<String,Object> parameters = new ArrayMap<>(3);
        parameters.put("phone",phone);
        parameters.put("authCode",verifyCodeET.toString());
        parameters.put("name","");

        HttpManager.get(UrlConfig.DO_REGISTER, parameters, new Callback() {
            @Override
            public void onSuccess(String message) {
                RegisterSuccess();
            }
            @Override
            public void onSuccessData(String json) {

            }
            @Override
            public void onFailure(String error_code, String error_message) {
            }
            @Override
            public void onFinish() {
                hideProgress();
            }
        });
    }

    private void RegisterSuccess() {
        Intent intent = new Intent();
        intent.putExtra("phoneNum",phone);
        setResult(RESULT_OK,intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }
}
