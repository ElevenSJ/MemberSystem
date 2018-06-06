package com.sj.activity.bean;

import java.io.Serializable;

/**
 * Created by SunJ on 2018/5/14.
 */

public class StudyBean implements Serializable {


    protected String id;
    protected String title;
    protected String thumbnail;

    protected String detailUrl;

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

    public String getDetailUrl() {
        return detailUrl;
    }

    public void setDetailUrl(String detailUrl) {
        this.detailUrl = detailUrl;
    }
}
