package com.sj.activity.bean;

/**
 * Created by SunJ on 2018/5/14.
 */

public class MDRTBean extends StudyBean{
    String price;
    int freeStatus;
    String createTime;
    int buyStatus;
    String detailUrl;
    String chargeContentUrl;

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getFreeStatus() {
        return freeStatus;
    }

    public void setFreeStatus(int freeStatus) {
        this.freeStatus = freeStatus;
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

    public String getDetailUrl() {
        return detailUrl;
    }

    public void setDetailUrl(String detailUrl) {
        this.detailUrl = detailUrl;
    }

    public String getChargeContentUrl() {
        return chargeContentUrl;
    }

    public void setChargeContentUrl(String chargeContentUrl) {
        this.chargeContentUrl = chargeContentUrl;
    }
}
