package com.sj.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.LinearLayoutManager;
import android.util.ArrayMap;
import android.widget.ImageView;
import android.widget.TextView;

import com.jady.retrofitclient.HttpManager;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.DividerDecoration;
import com.lyp.membersystem.R;
import com.lyp.membersystem.utils.Constant;
import com.sj.activity.adapter.ForumRyvAdapter;
import com.sj.activity.adapter.LeturerCourseRyvAdapter;
import com.sj.activity.base.ActivityBase;
import com.sj.activity.bean.LecturerDetailBean;
import com.sj.activity.bean.TeacherIntroduceBean;
import com.sj.activity.bean.UserBean;
import com.sj.http.Callback;
import com.sj.http.GsonResponsePasare;
import com.sj.http.UrlConfig;
import com.sj.utils.ImageUtils;

import java.util.Map;

/**
 * 创建时间: on 2018/5/29.
 * 创建人: 孙杰
 * 功能描述:
 */
public class ActivityTeacherIntroduceDetail extends ActivityBase {

    private ImageView imgIcon;
    private TextView txtName;
    private TextView txtDesc;
    private TextView txtCourseCount;
    EasyRecyclerView rylView;

    LeturerCourseRyvAdapter mAdapter;
    TeacherIntroduceBean teacherIntroduceBean;

    String tokenid;

    @Override
    public int getContentLayout() {
        return R.layout.activity_teacher_detail;
    }

    @Override
    public void initView() {
        setTitleTxt("讲师介绍");
        SharedPreferences mSharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCE, MODE_PRIVATE);
        tokenid = mSharedPreferences.getString(Constant.TOKEN_ID, "");

        teacherIntroduceBean = (TeacherIntroduceBean) getIntent().getSerializableExtra("data");

        imgIcon = findViewById(R.id.img_icon);
        txtName = findViewById(R.id.txt_name);
        txtDesc = findViewById(R.id.txt_desc);
        txtCourseCount = findViewById(R.id.txt_course_count);
        rylView = findViewById(R.id.ryl_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rylView.setLayoutManager(layoutManager);
        DividerDecoration dividerDecoration = new DividerDecoration(getResources().getColor(R.color.item_line_color), 1, 16, 16);
        dividerDecoration.setDrawLastItem(false);
        rylView.addItemDecoration(dividerDecoration);
        mAdapter = new LeturerCourseRyvAdapter(this);
        mAdapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(ActivityTeacherIntroduceDetail.this, ActivityStudyHtml.class);
                intent.putExtra("needBuy", true);
                intent.putExtra("data", mAdapter.getItem(position));
                intent.putExtra("title", "培训课程");
                ActivityTeacherIntroduceDetail.this.startActivity(intent);
            }
        });
        mAdapter.setNoMore(R.layout.layout_load_no_more);
        rylView.setAdapter(mAdapter);
        initData();
    }

    private void initData() {
        ImageUtils.loadImageView(teacherIntroduceBean.getAvatar(), imgIcon);
        txtName.setText(teacherIntroduceBean.getName());
        txtDesc.setText(teacherIntroduceBean.getBriefIntro());

        getDetail(teacherIntroduceBean.getId());
    }

    public void onRefresh(){
        mAdapter.clear();
        getDetail(teacherIntroduceBean.getId());
    }
    private void getDetail(String id) {
        showProgress();
        Map<String, Object> parameters = new ArrayMap<>(3);
        parameters.put("token_id", tokenid);
        parameters.put("lecturerId", id);
        HttpManager.get(UrlConfig.LECTURER_Detail, parameters, new Callback() {
            @Override
            public void onSuccess(String json) {
            }

            @Override
            public void onSuccessData(String json) {
                LecturerDetailBean lecturerDetailBean = new GsonResponsePasare<LecturerDetailBean>() {
                }.deal(json);
                if (lecturerDetailBean != null) {
                    txtCourseCount.setText("讲师课程(" + lecturerDetailBean.getCourseTotal() + ")");
                    if (lecturerDetailBean.getTrainCourse() != null) {
                        mAdapter.addAll(lecturerDetailBean.getTrainCourse());
                    }
                }
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
}
