package com.sj.activity.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by SunJ on 2018/5/17.
 */

public class StudyHtmlCommonBean  extends StudyBean{

    private String readQuantity;
    private String createTime;
    private int attachStatus;
    private String detailUrl;


    public String getReadQuantity() {
        return readQuantity;
    }

    public void setReadQuantity(String readQuantity) {
        this.readQuantity = readQuantity;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getAttachStatus() {
        return attachStatus;
    }

    public void setAttachStatus(int attachStatus) {
        this.attachStatus = attachStatus;
    }

    public String getDetailUrl() {
        return detailUrl;
    }

    public void setDetailUrl(String detailUrl) {
        this.detailUrl = detailUrl;
    }

}
