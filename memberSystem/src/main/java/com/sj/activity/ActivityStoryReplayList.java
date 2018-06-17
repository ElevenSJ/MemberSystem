package com.sj.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.jady.retrofitclient.HttpManager;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.DividerDecoration;
import com.lyp.membersystem.R;
import com.lyp.membersystem.utils.Constant;
import com.lyp.membersystem.utils.ToastUtil;
import com.sj.activity.adapter.ForumRyvAdapter;
import com.sj.activity.adapter.ReplayRyvAdapter;
import com.sj.activity.base.ActivityBase;
import com.sj.activity.bean.DataListBean;
import com.sj.activity.bean.ForumBean;
import com.sj.activity.bean.ReplayBean;
import com.sj.http.Callback;
import com.sj.http.GsonResponsePasare;
import com.sj.http.UrlConfig;

import java.util.Map;

/**
 * 创建时间: on 2018/5/6.
 * 创建人: 孙杰
 * 功能描述:
 */
public class ActivityStoryReplayList extends ActivityBase implements SwipeRefreshLayout.OnRefreshListener, RecyclerArrayAdapter.OnMoreListener {
    EasyRecyclerView rylView;
    TextView txtCount;

    ReplayRyvAdapter mAdapter;
    int pageNum = 1;
    String tokenid;
    String storyid;
    RatingBar ratingBar;
    EditText edtReplay;

    @Override
    public int getContentLayout() {
        return R.layout.activity_story_replay;
    }

    @Override
    public void initView() {
        setTitleTxt("评论");

        SharedPreferences mSharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCE, MODE_PRIVATE);
        tokenid = mSharedPreferences.getString(Constant.TOKEN_ID, "");
        storyid = getIntent().getStringExtra("id");
        rylView = findViewById(R.id.ryl_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rylView.setLayoutManager(layoutManager);
        DividerDecoration dividerDecoration = new DividerDecoration(getResources().getColor(R.color.item_line_color), 1, 16, 16);
        dividerDecoration.setDrawLastItem(false);
        rylView.addItemDecoration(dividerDecoration);
        mAdapter = new ReplayRyvAdapter(this);
        mAdapter.setMore(R.layout.layout_load_more, this);
        mAdapter.setNoMore(R.layout.layout_load_no_more);
        rylView.setAdapterWithProgress(mAdapter);
        rylView.setRefreshListener(this);

        txtCount = findViewById(R.id.txt_replay_count);
        findViewById(R.id.txt_all).setVisibility(View.GONE);
        edtReplay = findViewById(R.id.edt_replay);
        ratingBar = findViewById(R.id.ratingBar);
        ratingBar.post(new Runnable() {
            @Override
            public void run() {
                setRatingBarHeight(ratingBar,R.drawable.star_big);
            }
        });
        findViewById(R.id.bt_replay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doReplay();
            }
        });

        rylView.post(new Runnable() {
            @Override
            public void run() {
                onRefresh();
            }
        });
    }

    private void doReplay() {
        String replayTxt = edtReplay.getText().toString();
        int rating = (int)ratingBar.getRating();
        if (TextUtils.isEmpty(replayTxt)){
            ToastUtil.showMessage("请输入评论内容");
            return;
        }
        showProgress();
        Map<String, Object> parameters = new ArrayMap<>(5);
        parameters.put("token_id", tokenid);
        parameters.put("storytellingId", storyid);
        parameters.put("commentId", "");
        parameters.put("starNumber", rating+"");
        parameters.put("content", replayTxt);
        HttpManager.get(UrlConfig.STORY_REPLAY, parameters, new Callback() {
            @Override
            public void onSuccess(String message) {
                replaySuccess();
            }

            @Override
            public void onSuccessData(String json) {
                replaySuccess();
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

    private void replaySuccess() {
        ToastUtil.showMessage("发表成功");
        edtReplay.setText("");
        rylView.requestFocus();
        ratingBar.setRating(0f);
        onRefresh();
    }

    private void getData() {
        Map<String, Object> parameters = new ArrayMap<>(4);
        parameters.put("token_id", tokenid);
        parameters.put("storytellingId", storyid);
        parameters.put("pageNum", pageNum);
        parameters.put("pageSize", "10");
        HttpManager.get(UrlConfig.STORY_REPLAY_LIST, parameters, new Callback() {
            @Override
            public void onSuccess(String message) {
            }

            @Override
            public void onSuccessData(String json) {
                DataListBean<ReplayBean> forumListBean = new GsonResponsePasare<DataListBean<ReplayBean>>() {
                }.deal(json);
                if (forumListBean != null&&forumListBean.getInfoList() != null) {
                    if (pageNum==1&& mAdapter.getCount() > 0){
                        mAdapter.clear();
                    }
                    mAdapter.addAll(forumListBean.getInfoList());
                }
                pageNum++;
            }

            @Override
            public void onFailure(String error_code, String error_message) {
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

    /**
     * 动态设置Ratingbar高度，解决图片在不同分辨率手机拉伸问题
     * @param ratingBar
     * @param resourceId 本地图片资源Id
     */
    public  void setRatingBarHeight(RatingBar ratingBar, int resourceId) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resourceId);
        int height = bitmap.getHeight();
        ViewGroup.LayoutParams params = ratingBar.getLayoutParams();
        params.height = height;
        ratingBar.setLayoutParams(params);
    }
}
