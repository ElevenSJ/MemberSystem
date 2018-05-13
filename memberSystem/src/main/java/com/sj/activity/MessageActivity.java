package com.sj.activity;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.util.ArrayMap;

import com.jady.retrofitclient.HttpManager;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.decoration.DividerDecoration;
import com.lyp.membersystem.R;
import com.sj.activity.adapter.MessageRyvAdapter;
import com.sj.activity.base.ActivityBase;
import com.sj.activity.bean.MessageBean;
import com.sj.http.Callback;
import com.sj.http.GsonResponsePasare;
import com.sj.http.UrlConfig;

import java.util.List;
import java.util.Map;

/**
 * Created by SunJ on 2018/5/10.
 */

public class MessageActivity extends ActivityBase implements SwipeRefreshLayout.OnRefreshListener {

    EasyRecyclerView rylView;
    MessageRyvAdapter mAdapter;
    String tokenid;

    @Override
    public void onRefresh() {
        Map<String, Object> parameters = new ArrayMap<>(3);
        parameters.put("token_id", tokenid);
//        HttpManager.get(UrlConfig.FORUM_LIST, parameters, new Callback() {
//            @Override
//            public void onSuccess(String json) {
//
//            }
//
//            @Override
//            public void onSuccessData(String json) {
//                List<MessageBean> messageBeanList = new GsonResponsePasare<List<MessageBean>>() {
//                }.deal(json);
//            }
//
//            @Override
//            public void onFailure(String error_code, String error_message) {
//            }
//
//        });
    }

    @Override
    public int getContentLayout() {
        return R.layout.activity_message;
    }

    @Override
    public void initView() {
        setTitleTxt("系统通知");
        rylView = findViewById(R.id.ryl_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rylView.setLayoutManager(layoutManager);
        DividerDecoration dividerDecoration = new DividerDecoration(getResources().getColor(R.color.item_line_color), 1, 16, 16);
        dividerDecoration.setDrawLastItem(false);
        rylView.addItemDecoration(dividerDecoration);
        mAdapter = new MessageRyvAdapter(this);
        rylView.setAdapterWithProgress(mAdapter);
        rylView.setRefreshListener(this);
        onRefresh();
    }
}
