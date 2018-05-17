package com.sj.activity.bean;

import java.util.List;

/**
 * 创建时间: on 2018/5/6.
 * 创建人: 孙杰
 * 功能描述:
 */
public class StudyCommonListBean<T> {
    List<T> infoList;

    int pageNum;
    int pageSize;
    int totalCount;
    String defaultPageNum;

    public List<T> getInfoList() {
        return infoList;
    }

    public void setInfoList(List<T> infoList) {
        this.infoList = infoList;
    }
}
