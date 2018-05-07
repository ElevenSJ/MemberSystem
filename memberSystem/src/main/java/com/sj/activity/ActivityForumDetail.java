package com.sj.activity;

import com.lyp.membersystem.R;
import com.sj.activity.base.ActivityBase;

/**
 * 创建时间: on 2018/5/6.
 * 创建人: 孙杰
 * 功能描述:
 */
public class ActivityForumDetail extends ActivityBase {
    @Override
    public int getContentLayout() {
        return R.layout.activity_forum_detail;
    }

    @Override
    public void initView() {
        setTitleTxt("首席论坛");
    }
}
