package com.lyp.membersystem.bean;

public class ServiceOrderBean {
	private String icon;
	private String orderId;
	private String productId;
	private String orderDate;
	private double orderPrice;
	private String salemanName;
	private String productName;
	private String orderState = "0";// 订单状态：0.未付款，100.已付款，200.已完成，300.已取消
	private String expiryDate;
	private String reserve_time;

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}

	public double getOrderPrice() {
		return orderPrice;
	}

	public void setOrderPrice(double orderPrice) {
		this.orderPrice = orderPrice;
	}

	public String getSalemanName() {
		return salemanName;
	}

	public void setSalemanName(String salemanName) {
		this.salemanName = salemanName;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getOrderState() {
		return orderState;
	}

	public void setOrderState(String orderState) {
		this.orderState = orderState;
	}

	public String getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(String expriyDate) {
		this.expiryDate = expriyDate;
	}

	public String getReserve_time() {
		return reserve_time;
	}

	public void setReserve_time(String reserve_time) {
		this.reserve_time = reserve_time;
	}

}