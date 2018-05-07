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
import com.sj.activity.adapter.ForumRyvAdapter;
import com.sj.activity.base.ActivityBase;
import com.sj.activity.bean.ForumListBean;
import com.sj.http.Callback;
import com.sj.http.GsonResponsePasare;
import com.sj.http.UrlConfig;

import java.util.Map;

import butterknife.BindView;

/**
 * 创建时间: on 2018/5/6.
 * 创建人: 孙杰
 * 功能描述:
 */
public class ActivityForum extends ActivityBase implements SwipeRefreshLayout.OnRefreshListener, RecyclerArrayAdapter.OnMoreListener {
    EasyRecyclerView rylView;

    ForumRyvAdapter mAdapter;
    int pageNum = 1;
    String tokenid;

    @Override
    public int getContentLayout() {
        return R.layout.activity_forum;
    }

    @Override
    public void initView() {
        setTitleTxt("首席论坛");
        rylView = findViewById(R.id.ryl_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rylView.setLayoutManager(layoutManager);
        DividerDecoration dividerDecoration = new DividerDecoration(getResources().getColor(R.color.gray), 2, 16, 16);
        dividerDecoration.setDrawLastItem(false);
        rylView.addItemDecoration(dividerDecoration);
        mAdapter = new ForumRyvAdapter(this);
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
        onRefresh();
    }

    private void getData() {
        showProgress();
        Map<String, Object> parameters = new ArrayMap<>(3);
        parameters.put("token_id", tokenid);
        parameters.put("pageNum", pageNum);
        parameters.put("pageSize", "10");
        HttpManager.get(UrlConfig.FORUM_LIST, parameters, new Callback() {
            @Override
            public void onSuccess(String message) {
            }

            @Override
            public void onSuccessData(String json) {
                ForumListBean forumListBean = new GsonResponsePasare<ForumListBean>() {
                }.deal(json);
                if (forumListBean != null && forumListBean.getInfoList() != null) {
                    if (pageNum == 1 && mAdapter.getCount() > 0) {
                        mAdapter.clear();
                    }
                    mAdapter.addAll(forumListBean.getInfoList());
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
