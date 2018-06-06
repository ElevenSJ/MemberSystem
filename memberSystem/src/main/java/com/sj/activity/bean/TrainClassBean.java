package com.sj.activity.bean;

/**
 * Created by SunJ on 2018/5/14.
 */

public class TrainClassBean extends StudyBean{
    String price;
    String schoolTime;
    String schooltime;
    String schoolLocation;
    String applicationStartTime;
    String applicationEndTime;
    String detailUrl;
    String lecturerId;

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getSchoolTime() {
        return schoolTime;
    }

    public void setSchoolTime(String schoolTime) {
        this.schoolTime = schoolTime;
        this.schooltime = schoolTime;
    }
    public void setSchooltime(String schooltime) {
        this.schoolTime = schooltime;
        this.schooltime = schooltime;
    }

    public String getSchoolLocation() {
        return schoolLocation;
    }

    public void setSchoolLocation(String schoolLocation) {
        this.schoolLocation = schoolLocation;
    }

    public String getApplicationStartTime() {
        return applicationStartTime;
    }

    public void setApplicationStartTime(String applicationStartTime) {
        this.applicationStartTime = applicationStartTime;
    }

    public String getApplicationEndTime() {
        return applicationEndTime;
    }

    public void setApplicationEndTime(String applicationEndTime) {
        this.applicationEndTime = applicationEndTime;
    }

    public String getDetailUrl() {
        return detailUrl;
    }

    public void setDetailUrl(String detailUrl) {
        this.detailUrl = detailUrl;
    }

    public String getLecturerId() {
        return lecturerId;
    }

    public void setLecturerId(String lecturerId) {
        this.lecturerId = lecturerId;
    }
}
