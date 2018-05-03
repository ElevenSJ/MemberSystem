package com.lyp.membersystem.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class SelectGoodBean implements Parcelable{

	private String cid;
	private String cname;
	private String avater;
	private String pid;
	private String pname;
	private String pprice;
	private String pUrl;
	private String pack;
	private String card;
	private String customTexts;

	public SelectGoodBean() {
		super();
	}
	
	public SelectGoodBean(Parcel source) {
		super();
		cid = source.readString();
		pname = source.readString();
		cname = source.readString();
		avater = source.readString();
		pid = source.readString();
		pprice = source.readString();
		pack = source.readString();
		card = source.readString();
		pUrl = source.readString();
		customTexts = source.readString();
	}
	
	public static Parcelable.Creator<SelectGoodBean> CREATOR = new Creator<SelectGoodBean>() {

		@Override
		public SelectGoodBean createFromParcel(Parcel source) {
			return new SelectGoodBean(source);
		}

		@Override
		public SelectGoodBean[] newArray(int size) {
			return new SelectGoodBean[size];
		}
		
	};

	public String getCid() {
		return cid;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}

	public String getCname() {
		return cname;
	}

	public void setCname(String cname) {
		this.cname = cname;
	}

	public String getAvater() {
		return avater;
	}

	public void setAvater(String avater) {
		this.avater = avater;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getPname() {
		return pname;
	}

	public void setPname(String pname) {
		this.pname = pname;
	}
 
	public String getpUrl() {
		return pUrl;
	}

	public void setpUrl(String pUrl) {
		this.pUrl = pUrl;
	}

	public String getPprice() {
		return pprice;
	}

	public void setPprice(String pprice) {
		this.pprice = pprice;
	}

	public String getPack() {
		return pack;
	}

	public void setPack(String pack) {
		this.pack = pack;
	}

	public String getCard() {
		return card;
	}

	public void setCard(String card) {
		this.card = card;
	}
	
	public String getCustomTexts() {
		return customTexts;
	}

	public void setCustomTexts(String customTexts) {
		this.customTexts = customTexts;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(cid);
		dest.writeString(pname);
		dest.writeString(cname);
		dest.writeString(avater);
		dest.writeString(pid);
		dest.writeString(pprice);
		dest.writeString(pack);
		dest.writeString(card);
		dest.writeString(pUrl);
		dest.writeString(customTexts);
	}


}
