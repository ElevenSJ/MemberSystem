package com.sj.activity.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 创建时间: on 2018/6/7.
 * 创建人: 孙杰
 * 功能描述:
 */
public class OrderBean implements Serializable{

    /**
     * orderCode : ST2018060619080002
     * bigOrderType : 2
     * orderTime : 2018-06-06 19:08:39.0
     * orderStatus : 1
     * customerSize : 0
     * productName : 11111111111111111111
     * previewImgUrl : ["http://project.app-storage-node.com/prj-mms/2018053011100002.jpg"]
     * totalPrice : 4800
     * logisticsNo :
     */

    private String orderCode;
    private int bigOrderType;
    private String orderTime;
    private String orderStatus;
    private int customerSize;
    private String productName;
    private String totalPrice;
    private String logisticsNo;
    private List<String> previewImgUrl;

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public int getBigOrderType() {
        return bigOrderType;
    }

    public void setBigOrderType(int bigOrderType) {
        this.bigOrderType = bigOrderType;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public int getCustomerSize() {
        return customerSize;
    }

    public void setCustomerSize(int customerSize) {
        this.customerSize = customerSize;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getLogisticsNo() {
        return logisticsNo;
    }

    public void setLogisticsNo(String logisticsNo) {
        this.logisticsNo = logisticsNo;
    }

    public List<String> getPreviewImgUrl() {
        return previewImgUrl;
    }

    public void setPreviewImgUrl(List<String> previewImgUrl) {
        this.previewImgUrl = previewImgUrl;
    }
}
