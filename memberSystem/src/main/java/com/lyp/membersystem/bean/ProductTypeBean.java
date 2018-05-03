package com.lyp.membersystem.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class ProductTypeBean implements Parcelable{

	private String id;
	private String pId;
	private String name;
	private String icon;
	public ProductTypeBean() {
		super();
	}

	public ProductTypeBean(Parcel source) {
		super();
		id = source.readString();
		pId = source.readString();
		name = source.readString();
		icon = source.readString();
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getpId() {
		return pId;
	}
	public void setpId(String pId) {
		this.pId = pId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeString(pId);
		dest.writeString(name);
		dest.writeString(icon);
	}
	
	public static Parcelable.Creator<ProductTypeBean> CREATOR = new Creator<ProductTypeBean>() {

		@Override
		public ProductTypeBean createFromParcel(Parcel source) {
			return new ProductTypeBean(source);
		}

		@Override
		public ProductTypeBean[] newArray(int size) {
			return new ProductTypeBean[size];
		}
		
	};
}
