package com.sj.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jady.retrofitclient.HttpManager;
import com.jady.retrofitclient.callback.FileResponseResult;
import com.jph.takephoto.model.TResult;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.DividerDecoration;
import com.lyp.membersystem.R;
import com.lyp.membersystem.net.API;
import com.lyp.membersystem.utils.Constant;
import com.sj.activity.adapter.UserInfoRyvAdapter;
import com.sj.activity.base.ActivityTakePhotoBase;
import com.sj.activity.bean.UserBean;
import com.sj.http.Callback;
import com.sj.http.UrlConfig;
import com.sj.utils.ImageUtils;
import com.yuntongxun.ecdemo.common.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 创建时间: on 2018/5/12.
 * 创建人: 孙杰
 * 功能描述:
 */
public class ActivityEditUserInfo extends ActivityTakePhotoBase implements OnClickListener {

    EasyRecyclerView rylView;
    ImageView imgUserHeader;
    UserInfoRyvAdapter mAdapter;

    List<Item> items = new ArrayList<>();
    UserBean userBean;
    int itemIndex = -1;

    SharedPreferences mSharedPreferences;
    String tokenId;

    boolean editAble = false;

    @Override
    public int getContentLayout() {
        return R.layout.activity_edit_user_info;
    }

    @Override
    public void initView() {
        setTitleTxt("个人信息");
//        setTitleRightTxt("修改");
        userBean = getIntent().getParcelableExtra("data");

        mSharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCE, MODE_PRIVATE);
        tokenId = mSharedPreferences.getString(Constant.TOKEN_ID, "");

        imgUserHeader = findViewById(R.id.user_avater);
        imgUserHeader.setOnClickListener(this);
        rylView = findViewById(R.id.ryl_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rylView.setLayoutManager(layoutManager);
        DividerDecoration dividerDecoration = new DividerDecoration(getResources().getColor(R.color.item_line_color), 1, 16, 16);
        dividerDecoration.setDrawLastItem(false);
        rylView.addItemDecoration(dividerDecoration);
        mAdapter = new UserInfoRyvAdapter(this);
        mAdapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
//                if (!editAble) {
//                    return;
//                }
                itemIndex = position;
                switch (position) {
                    case 0:
                        ShowEditDialog("name", mAdapter.getItem(position).getName());
                        break;
                    case 1:
                        ShowSingleSelectDialog("gender", new String[]{"女", "男"});
                        break;
                    case 2:
                        ShowEditDialog("phone", mAdapter.getItem(position).getName());
                        break;
                    case 3:
                        ShowEditDialog("birthday", mAdapter.getItem(position).getName());
                        break;
                    case 4:
                        ShowEditDialog("jobTitle", mAdapter.getItem(position).getName());
                        break;
                    case 5:
                        ShowEditDialog("email", mAdapter.getItem(position).getName());
                        break;
                    case 6:
                        ShowEditDialog("company", mAdapter.getItem(position).getName());
                        break;
                    case 7:
                        ShowEditDialog("companyAddress", mAdapter.getItem(position).getName());
                        break;
                    case 8:
                        ShowEditDialog("businessAddress", mAdapter.getItem(position).getName());
                        break;
                }
            }
        });
        rylView.setAdapter(mAdapter);

        initData();
    }

    private void ShowSingleSelectDialog(final String key, String[] item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请选择");
        builder.setItems(item, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                updateUserInfo(key, which + "");
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @SuppressLint("RestrictedApi")
    private void ShowEditDialog(final String key, String name) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请输入" + name);
        final EditText et = new EditText(this);
        et.setHint("请输入您的" + name);
        et.setSingleLine(true);
        builder.setView(et, 20, 20, 20, 20);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                String value = et.getText().toString();
                updateUserInfo(key, value);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void initData() {
        ImageUtils.loadImageWithError(userBean.getAvatar(), R.drawable.personal, imgUserHeader);

        items.add(new Item("姓名", userBean.getName()));
        items.add(new Item("性别", userBean.getGender() == 0 ? "女" : "男"));
        items.add(new Item("电话", userBean.getPhone()));
        items.add(new Item("生日", userBean.getBirthday()));
        items.add(new Item("职称", userBean.getJobTitle()));
        items.add(new Item("邮箱", userBean.getEmail()));
        items.add(new Item("公司", userBean.getCompany()));
        items.add(new Item("公司地址", userBean.getCompanyAddress()));
        items.add(new Item("营业处", userBean.getBusinessAddress()));
        mAdapter.addAll(items);
    }

    @Override
    public void onRightTxt(View view) {
        super.onRightTxt(view);
        if (((TextView) view).getText().toString().equals("修改")) {
            ToastUtil.showMessage("请点击需要修改的内容");
            ((TextView) view).setText("保存");
            editAble = true;
        } else {
            editAble = false;
            setTitleRightTxt("修改");
        }
    }

    private void updateUserInfo(final String key, final String value) {
        showProgress();
        Map<String, Object> parameters = new ArrayMap<>(2);
        parameters.put("token_id", tokenId);
        parameters.put(key, value);
        HttpManager.get(UrlConfig.UPDATE_MEMBER_INFO, parameters, new Callback() {
            @Override
            public void onSuccess(String message) {
                items.get(itemIndex).setValue(key.equals("gender") ? (value.equals("0") ? "女" : "男") : "男");
                mAdapter.notifyItemChanged(itemIndex);
            }

            @Override
            public void onSuccessData(String json) {

            }

            @Override
            public void onFailure(String error_code, String error_message) {
                Log.d(TAG, "onSuccessData: ");

            }

            @Override
            public void onFinish() {
                super.onFinish();
                setTitleRightTxt("修改");
                hideProgress();
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.user_avater:
                showTakePhotoDialog();
                break;
        }
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
        uploadAvater(path);
    }

    public void uploadAvater(final String path) {
        if (TextUtils.isEmpty(path)) {
            ToastUtil.showMessage("图片为空");
            return;
        }
        showProgress();
        Uri.Builder builder = Uri.parse(API.API_UPLOAD_AVATER).buildUpon();
        builder.appendQueryParameter("token_id", tokenId);
        HttpManager.uploadFile(builder.toString(), path, "", new FileResponseResult() {
            @Override
            public void onSuccess() {
                hideProgress();
                ImageUtils.loadImageWithError(path, R.drawable.personal, imgUserHeader);
            }

            @Override
            public void onFailure(Throwable throwable, String content) {
                hideProgress();
                ToastUtil.showMessage(content);
            }
        });
    }

    public static class Item {
        String name;
        String value;

        public Item(String name, String value) {
            setName(name);
            setValue(value);
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
