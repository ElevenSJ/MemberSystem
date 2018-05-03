package com.lyp.membersystem.bean;

public class ServiceBean {
	private String serviceId;
	private String name;
	private String description;
	private String icon;
	private String recordStatus;
	private String pstatus;
	private String introduceInfo;
	private String applyId;
	private String reserveTime;
	private String serviceStatus = "";
	
	public String getServiceId() {
		return serviceId;
	}
	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public String getRecordStatus() {
		return recordStatus;
	}
	public void setRecordStatus(String recordStatus) {
		this.recordStatus = recordStatus;
	}
	public String getPstatus() {
		return pstatus;
	}
	public void setPstatus(String pstatus) {
		this.pstatus = pstatus;
	}
	public String getSname() {
		return introduceInfo;
	}
	public void setSname(String sname) {
		this.introduceInfo = sname;
	}
	public String getIntroduceInfo() {
		return introduceInfo;
	}
	public void setIntroduceInfo(String introduceInfo) {
		this.introduceInfo = introduceInfo;
	}
	public String getApplyId() {
		return applyId;
	}
	public void setApplyId(String applyId) {
		this.applyId = applyId;
	}
	public String getReserveTime() {
		return reserveTime;
	}
	public void setReserveTime(String reserveTime) {
		this.reserveTime = reserveTime;
	}
	public String getServiceStatus() {
		return serviceStatus;
	}
	public void setServiceStatus(String serviceStatus) {
		this.serviceStatus = serviceStatus;
	}
	
}
