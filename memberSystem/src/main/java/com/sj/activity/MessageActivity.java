package com.sj.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.util.ArrayMap;

import com.jady.retrofitclient.HttpManager;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.DividerDecoration;
import com.lyp.membersystem.R;
import com.lyp.membersystem.utils.Constant;
import com.sj.activity.adapter.MessageRyvAdapter;
import com.sj.activity.base.ActivityBase;
import com.sj.activity.bean.DataListBean;
import com.sj.activity.bean.MessageBean;
import com.sj.http.Callback;
import com.sj.http.GsonResponsePasare;
import com.sj.http.UrlConfig;

import java.util.List;
import java.util.Map;

/**
 * Created by SunJ on 2018/5/10.
 */

public class MessageActivity extends ActivityBase implements SwipeRefreshLayout.OnRefreshListener, RecyclerArrayAdapter.OnMoreListener {

    EasyRecyclerView rylView;
    MessageRyvAdapter mAdapter;
    String tokenid;
    int pageNum = 1;

    @Override
    public void onRefresh() {
        Map<String, Object> parameters = new ArrayMap<>(3);
        parameters.put("token_id", tokenid);
        parameters.put("pageNum", pageNum);
        parameters.put("pageSize", "10");
        HttpManager.get(UrlConfig.SYSTEM_MESSAGE, parameters, new Callback() {
            @Override
            public void onSuccess(String json) {

            }

            @Override
            public void onSuccessData(String json) {
                DataListBean<MessageBean> messageBeanList = new GsonResponsePasare<DataListBean<MessageBean>>() {
                }.deal(json);
                if (messageBeanList != null && messageBeanList.getInfoList() != null) {
                    if (pageNum == 1 && mAdapter.getCount() > 0) {
                        mAdapter.clear();
                    }
                    mAdapter.addAll(messageBeanList.getInfoList());
                    pageNum++;
                }

            }

            @Override
            public void onFailure(String error_code, String error_message) {
            }

            @Override
            public void onFinish() {
                super.onFinish();
                rylView.setRefreshing(false);
            }
        });
    }

    @Override
    public int getContentLayout() {
        return R.layout.activity_message;
    }

    @Override
    public void initView() {
        setTitleTxt("系统通知");
        SharedPreferences mSharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCE, MODE_PRIVATE);
        tokenid = mSharedPreferences.getString(Constant.TOKEN_ID, "");

        rylView = findViewById(R.id.ryl_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rylView.setLayoutManager(layoutManager);
        DividerDecoration dividerDecoration = new DividerDecoration(getResources().getColor(R.color.item_line_color), 1, 16, 16);
        dividerDecoration.setDrawLastItem(false);
        rylView.addItemDecoration(dividerDecoration);
        mAdapter = new MessageRyvAdapter(this);
        mAdapter.setMore(R.layout.layout_load_more, this);
        mAdapter.setNoMore(R.layout.layout_load_no_more);
        mAdapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(MessageActivity.this, ActivityMessageDetail.class);
                intent.putExtra("data", mAdapter.getItem(position));
                startActivity(intent);
                if (mAdapter.getItem(position).getReadStatus() == 0) {
                    mAdapter.getItem(position).setReadStatus(1);
                    mAdapter.notifyItemChanged(position);
                }
            }
        });
        rylView.setAdapter(mAdapter);
        rylView.setRefreshListener(this);
        onRefresh();
    }

    @Override
    public void onMoreShow() {
        onRefresh();
    }

    @Override
    public void onMoreClick() {

    }

}
