package com.sj.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jude.easyrecyclerview.EasyRecyclerView;
import com.lyp.membersystem.R;
import com.lyp.membersystem.utils.ToastUtil;
import com.sj.activity.base.ActivityBase;
import com.youth.banner.Banner;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 创建时间: on 2018/5/6.
 * 创建人: 孙杰
 * 功能描述:
 */
public class ActivityStudy extends ActivityBase implements View.OnClickListener{
    @BindView(R.id.banner)
    Banner banner;
    @BindView(R.id.ryl_view)
    EasyRecyclerView rylView;
    TextView txtReadBook;
    TextView txtMorningMeetting;
    TextView txtGoodTime;
    TextView txtTrain;
    TextView txtTeacher;
    TextView txtMDRT;
    TextView txtForum;
    TextView txtClass;

    @Override
    public int getContentLayout() {
        return R.layout.activity_study;
    }

    @Override
    public void initView() {
        setTitleTxt("进修学习");
        txtReadBook = findViewById(R.id.txt_read_book);
        txtMorningMeetting = findViewById(R.id.txt_morning_meetting);
        txtGoodTime = findViewById(R.id.txt_good_time);
        txtTrain = findViewById(R.id.txt_train);
        txtTeacher = findViewById(R.id.txt_teacher);
        txtMDRT = findViewById(R.id.txt_MDRT);
        txtForum = findViewById(R.id.txt_forum);
        txtClass = findViewById(R.id.txt_class);
        txtReadBook.setOnClickListener(this);
        txtMorningMeetting.setOnClickListener(this);
        txtGoodTime.setOnClickListener(this);
        txtTrain.setOnClickListener(this);
        txtTeacher.setOnClickListener(this);
        txtMDRT.setOnClickListener(this);
        txtForum.setOnClickListener(this);
        txtClass.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        ToastUtil.showMessage(((TextView) v).getText().toString());
        Intent intent = new Intent();
        switch (id) {
            case R.id.txt_forum:
                intent.setClass(this, ActivityForum.class);
                startActivity(intent);
                break;
        }
    }
}
