package com.lyp.membersystem.bean;

import com.lyp.membersystem.view.contactsort.ContactSortModel;

public class OrderDetailBean {
     private GoodBean goodBean;
     private CardEnvelopBean cardEnvelopBean;
     private PackageBean packageBean;
     private ContactSortModel customer;
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
	public ContactSortModel getCustomer() {
		return customer;
	}
	public void setCustomer(ContactSortModel customer) {
		this.customer = customer;
	}
	public GoodBean getGoodBean() {
		return goodBean;
	}
	public void setGoodBean(GoodBean goodBean) {
		this.goodBean = goodBean;
	}
}
