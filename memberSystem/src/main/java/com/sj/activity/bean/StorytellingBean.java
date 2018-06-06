package com.sj.activity.bean;

import java.util.List;

/**
 * 创建时间: on 2018/5/27.
 * 创建人: 孙杰
 * 功能描述:作者说书列表
 */
public class StorytellingBean extends StudyBean{
    String price;
    int readQuantity;
    String briefIntro;
    String detailUrl;
    String audioUrl;
    String createTime;
    int buyStatus;
    int freeStatus;
    List<TeacherIntroduceBean> author;


    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getReadQuantity() {
        return readQuantity;
    }

    public void setReadQuantity(int readQuantity) {
        this.readQuantity = readQuantity;
    }

    public String getBriefIntro() {
        return briefIntro;
    }

    public void setBriefIntro(String briefIntro) {
        this.briefIntro = briefIntro;
    }

    public String getDetailUrl() {
        return detailUrl;
    }

    public void setDetailUrl(String detailUrl) {
        this.detailUrl = detailUrl;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getBuyStatus() {
        return buyStatus;
    }

    public void setBuyStatus(int buyStatus) {
        this.buyStatus = buyStatus;
    }

    public List<TeacherIntroduceBean> getAuthor() {
        return author;
    }

    public void setAuthor(List<TeacherIntroduceBean> author) {

        this.author = author;
    }

    public int getFreeStatus() {
        return freeStatus;
    }

    public void setFreeStatus(int freeStatus) {
        this.freeStatus = freeStatus;
    }
}
