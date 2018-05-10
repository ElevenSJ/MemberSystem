package com.sj.activity;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;

import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.DividerDecoration;
import com.lyp.membersystem.R;
import com.sj.activity.adapter.AreaRyvAdapter;
import com.sj.activity.base.ActivityBase;
import com.sj.activity.bean.ForumBean;

import java.util.ArrayList;

/**
 * 创建时间: on 2018/5/9.
 * 创建人: 孙杰
 * 功能描述:
 */
public class ActivityChooseArea extends ActivityBase {

    EasyRecyclerView rylView;

    AreaRyvAdapter mAdapter;

    ArrayList<String> items;

    @Override
    public int getContentLayout() {
        return R.layout.activity_choose_area;
    }

    @Override
    public void initView() {
        setTitleTxt("选择区域");
        items = getIntent().getStringArrayListExtra("data");
        rylView = findViewById(R.id.ryl_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rylView.setLayoutManager(layoutManager);
        DividerDecoration dividerDecoration = new DividerDecoration(getResources().getColor(R.color.item_line_color), 1, 16, 16);
        dividerDecoration.setDrawLastItem(false);
        rylView.addItemDecoration(dividerDecoration);
        mAdapter = new AreaRyvAdapter(this);
        rylView.setAdapter(mAdapter);
        mAdapter.addAll(items);
        mAdapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent();
                intent.putExtra("data",position);
                setResult(RESULT_OK,intent);
                finish();
            }
        });
    }
}
