package com.sj.activity.bean;

/**
 * 创建时间: on 2018/5/9.
 * 创建人: 孙杰
 * 功能描述:
 */
public class Bannerbean {

    /**
     * name : 轮播图1
     * accessLink : http://www.baidu.com
     * picUrl :  F2007-12-29%2F200712299383135_2.jpg
     */
    private String id;
    private String name;
    private String accessLink;
    private String picUrl;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccessLink() {
        return accessLink;
    }

    public void setAccessLink(String accessLink) {
        this.accessLink = accessLink;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
