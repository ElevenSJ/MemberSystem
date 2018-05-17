package com.sj.activity.bean;

/**
 * Created by SunJ on 2018/5/14.
 */

public class TrainClassBean extends StudyBean{
    String price;
    String schoolTime;
    String schoolLocation;
    String applicationStartTime;
    String applicationEndTime;

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
}
