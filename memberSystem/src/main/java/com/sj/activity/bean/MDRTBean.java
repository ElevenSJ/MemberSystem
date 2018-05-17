package com.sj.activity.bean;

/**
 * Created by SunJ on 2018/5/14.
 */

public class MDRTBean extends StudyBean{
    String price;
    String freeStatus;
    String createTime;

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getFreeStatus() {
        return freeStatus;
    }

    public void setFreeStatus(String freeStatus) {
        this.freeStatus = freeStatus;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
