package com.lyp.membersystem.bean;

public class CardEnvelopBean{

	private String id;
	private String name;
	private String price;
	private String cardVersoFile;
	private String cardFrontFile;
	private String cardEnvelop;
	private int cardShowImg = 0;//用于展示选择的那张图片.0:正面，1：反面，2：信封
	private String icon;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public String getCardVersoFile() {
		return cardVersoFile;
	}
	public void setCardVersoFile(String cardVersoFile) {
		this.cardVersoFile = cardVersoFile;
	}
	public String getCardFrontFile() {
		return cardFrontFile;
	}
	public void setCardFrontFile(String cardFrontFile) {
		this.cardFrontFile = cardFrontFile;
	}
	public String getCardEnvelop() {
		return cardEnvelop;
	}
	public void setCardEnvelop(String cardEnvelop) {
		this.cardEnvelop = cardEnvelop;
	}
	public int getCardShowImg() {
		return cardShowImg;
	}
	public void setCardShowImg(int cardShowImg) {
		this.cardShowImg = cardShowImg;
	}
	
}
