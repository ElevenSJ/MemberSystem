package com.lyp.membersystem.bean;

public class ExpressAddressBean {

	private String id;
	private String tagName;
	private String name;
	private String phoneNumber;
	private String area;
	private String address;
	private String flag;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTagName() {
		return tagName;
	}
	public void setTagName(String tagName) {
		this.tagName = tagName;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getFlag() {
		if (flag == null) {
			return "0";
		}
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
	
	
}
