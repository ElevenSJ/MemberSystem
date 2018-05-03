package com.lyp.membersystem.bean;

import java.util.List;

import com.lyp.membersystem.view.contactsort.ContactSortModel;

public class OrderBean {
     private String orderId;
     private String orderDate;
     private List<GoodBean> goodlist;
     private CardEnvelopBean cardEnvelopBean;
     private PackageBean packageBean;
     private List<ContactSortModel> customerList;
     private double orderPrice;
     private String orderState = "0";
     private String receiveType;
     private String logisticsno;//receivetype=1取customerlist下的logisticsno,=2的时候取同级别的logisticsno
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getOrderDate() {
		return orderDate;
	}
	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}
	public List<GoodBean> getGoodlist() {
		return goodlist;
	}
	public void setGoodlist(List<GoodBean> goodlist) {
		this.goodlist = goodlist;
	}
	public CardEnvelopBean getCardEnvelopBean() {
		return cardEnvelopBean;
	}
	public void setCardEnvelopBean(CardEnvelopBean cardEnvelopBean) {
		this.cardEnvelopBean = cardEnvelopBean;
	}
	public PackageBean getPackageBean() {
		return packageBean;
	}
	public void setPackageBean(PackageBean packageBean) {
		this.packageBean = packageBean;
	}
	public List<ContactSortModel> getCustomerList() {
		return customerList;
	}
	public void setCustomerList(List<ContactSortModel> customerList) {
		this.customerList = customerList;
	}
	public double getOrderPrice() {
		return orderPrice;
	}
	public void setOrderPrice(double orderPrice) {
		this.orderPrice = orderPrice;
	}
	public String getOrderState() {
		return orderState;
	}
	public void setOrderState(String orderState) {
		this.orderState = orderState;
	}
	public String getReceiveType() {
		return receiveType;
	}
	public void setReceiveType(String receiveType) {
		this.receiveType = receiveType;
	}
	public String getLogisticsno() {
		return logisticsno;
	}
	public void setLogisticsno(String logisticsno) {
		this.logisticsno = logisticsno;
	}
     
}
