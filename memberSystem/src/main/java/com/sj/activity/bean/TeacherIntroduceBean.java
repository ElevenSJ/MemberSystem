package com.sj.activity.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by SunJ on 2018/5/14.
 */

public class TeacherIntroduceBean extends StudyBean{
    @SerializedName("name")
    protected String title;
    @SerializedName("avatar")
    protected String thumbnail;
    protected String briefIntro;

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getThumbnail() {
        return thumbnail;
    }

    @Override
    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getBriefIntro() {
        return briefIntro;
    }

    public void setBriefIntro(String briefIntro) {
        this.briefIntro = briefIntro;
    }
}
