package com.sj.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.util.ArrayMap;

import com.jady.retrofitclient.HttpManager;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.DividerDecoration;
import com.lyp.membersystem.R;
import com.lyp.membersystem.utils.Constant;
import com.sj.activity.adapter.StudyRyvAdapter;
import com.sj.activity.base.ActivityBase;
import com.sj.activity.bean.ForumBean;
import com.sj.activity.bean.StudyBean;
import com.sj.activity.bean.StudyCommonListBean;
import com.sj.http.Callback;
import com.sj.http.GsonResponsePasare;

import java.util.Map;

/**
 * 创建时间: on 2018/5/6.
 * 创建人: 孙杰
 * 功能描述:
 */
public class ActivityStudyCommon extends ActivityBase implements SwipeRefreshLayout.OnRefreshListener, RecyclerArrayAdapter.OnMoreListener {
    EasyRecyclerView rylView;

    StudyRyvAdapter mAdapter;
    int pageNum = 1;
    String tokenid;
    String title;
    String url;
    int type;

    @Override
    public int getContentLayout() {
        return R.layout.activity_study_common;
    }

    @Override
    public void initView() {
        title = getIntent().getStringExtra("title");
        url = getIntent().getStringExtra("url");
        type = getIntent().getIntExtra("type", 0);
        setTitleTxt(title);

        rylView = findViewById(R.id.ryl_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rylView.setLayoutManager(layoutManager);
        DividerDecoration dividerDecoration = new DividerDecoration(getResources().getColor(R.color.item_line_color), 1, 16, 16);
        dividerDecoration.setDrawLastItem(false);
        rylView.addItemDecoration(dividerDecoration);
        mAdapter = new StudyRyvAdapter(this);
        mAdapter.setMore(R.layout.layout_load_more, this);
        mAdapter.setNoMore(R.layout.layout_load_no_more);
        rylView.setAdapterWithProgress(mAdapter);
        rylView.setRefreshListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences mSharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCE, MODE_PRIVATE);
        tokenid = mSharedPreferences.getString(Constant.TOKEN_ID, "");
        if (type == 0) {
            return;
        }
        showProgress();
        onRefresh();
    }

    private void getData() {
        Map<String, Object> parameters = new ArrayMap<>(3);
        parameters.put("token_id", tokenid);
        parameters.put("pageNum", pageNum);
        parameters.put("pageSize", "10");
        HttpManager.get(url, parameters, new Callback() {
            @Override
            public void onSuccess(String message) {
            }

            @Override
            public void onSuccessData(String json) {
                StudyCommonListBean<StudyBean> studyListBean = new GsonResponsePasare<StudyCommonListBean<StudyBean>>() {
                }.deal(json);
                if (studyListBean != null && studyListBean.getInfoList() != null) {
                    if (pageNum == 1 && mAdapter.getCount() > 0) {
                        mAdapter.clear();
                    }
                    mAdapter.addAll(studyListBean.getInfoList());
                }
                pageNum++;
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

    @Override
    public void onRefresh() {
        pageNum = 1;
        getData();
    }

    @Override
    public void onMoreShow() {
        getData();
    }

    @Override
    public void onMoreClick() {

    }
}
