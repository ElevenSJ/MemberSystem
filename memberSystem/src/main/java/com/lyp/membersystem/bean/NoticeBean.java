package com.lyp.membersystem.bean;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class NoticeBean implements Parcelable{

	private String id;
	private String title;//规则名称
	private String operator;
	private String date;
	private String haveChildren;
	private String remindTime;
	private String pack;
	private String card;
	private String condition;//启用客户条件 0:不启用    1:普通通知   2:生日通
	private String ruleType;
	private String gender;
	private String ageMax;
	private String ageMin;
	private String maritalStatus;
	private List<String> goodlist;
	private String customerIds;
	private String cardTexts;
	private String readStatus;// 默认为0，已读状态为1
	private String state = "0";
	private long invalidTime;
	private String ruleId;//规则ID

	public NoticeBean() {
		super();
	}
	
	public NoticeBean(Parcel source) {
		super();
		id = source.readString();
		title = source.readString();
		operator = source.readString();
		date = source.readString();
		haveChildren = source.readString();
		remindTime = source.readString();
		pack = source.readString();
		card = source.readString();
		condition = source.readString();
		ruleType = source.readString();
		gender = source.readString();
		ageMax = source.readString();
		ageMin = source.readString();
		maritalStatus = source.readString();
		readStatus = source.readString();
		customerIds = source.readString();
		cardTexts = source.readString();
		state = source.readString();
		invalidTime = source.readLong();
		ruleId = source.readString();
		if (goodlist == null) {
			goodlist = new ArrayList<String>();
		}
		source.readStringList(goodlist);
	}
	
	public static Parcelable.Creator<NoticeBean> CREATOR = new Creator<NoticeBean>() {

		@Override
		public NoticeBean createFromParcel(Parcel source) {
			return new NoticeBean(source);
		}

		@Override
		public NoticeBean[] newArray(int size) {
			return new NoticeBean[size];
		}
		
	};
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getReadStatus() {
		return readStatus;
	}

	public void setReadStatus(String readStatus) {
		this.readStatus = readStatus;
	}

	public String getHaveChildren() {
		return haveChildren;
	}

	public void setHaveChildren(String haveChildren) {
		this.haveChildren = haveChildren;
	}

	public String getRemindTime() {
		return remindTime;
	}

	public void setRemindTime(String remindTime) {
		this.remindTime = remindTime;
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

	public String getCondition() {
		if (condition == null)
			return "-1";
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public String getRuleType() {
		return ruleType;
	}

	public void setRuleType(String ruleType) {
		this.ruleType = ruleType;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getAgeMax() {
		return ageMax;
	}

	public void setAgeMax(String ageMax) {
		this.ageMax = ageMax;
	}

	public String getAgeMin() {
		return ageMin;
	}

	public void setAgeMin(String ageMin) {
		this.ageMin = ageMin;
	}

	public String getMaritalStatus() {
		return maritalStatus;
	}

	public void setMaritalStatus(String maritalStatus) {
		this.maritalStatus = maritalStatus;
	}

	public List<String> getGoodlist() {
		return goodlist;
	}

	public void setGoodlist(List<String> goodlist) {
		this.goodlist = goodlist;
	}
	
	public String getCustomerIds() {
		return customerIds;
	}

	public void setCustomerIds(String customerIds) {
		this.customerIds = customerIds;
	}

	public String getCardTexts() {
		return cardTexts;
	}

	public void setCardTexts(String cardTexts) {
		this.cardTexts = cardTexts;
	}
	

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public long getInvalidTime() {
		return invalidTime;
	}

	public void setInvalidTime(long invalidTime) {
		this.invalidTime = invalidTime;
	}

	public String getRuleId() {
		return ruleId;
	}

	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeString(title);
		dest.writeString(operator);
		dest.writeString(date);
		dest.writeString(haveChildren);
		dest.writeString(remindTime);
		dest.writeString(pack);
		dest.writeString(card);
		dest.writeString(condition);
		dest.writeString(ruleType);
		dest.writeString(gender);
		dest.writeString(ageMax);
		dest.writeString(ageMin);
		dest.writeString(maritalStatus);
		dest.writeString(readStatus);
		dest.writeString(customerIds);
		dest.writeString(cardTexts);
		dest.writeString(state);
		dest.writeLong(invalidTime);
		dest.writeString(ruleId);
		dest.writeStringList(goodlist);
	}


}
