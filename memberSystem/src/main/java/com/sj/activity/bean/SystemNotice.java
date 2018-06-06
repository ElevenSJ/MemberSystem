package com.sj.activity.bean;

/**
 * 创建时间: on 2018/5/27.
 * 创建人: 孙杰
 * 功能描述: 系统公告
 */
public class SystemNotice {

    protected String id;
    protected String title;
    protected String thumbnail;
    protected String createTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
