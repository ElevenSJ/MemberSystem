package com.lyp.membersystem.view.contactsort;

public class ContactSortModel {

    private String name;//显示的数据
    private String sortLetters;//显示数据拼音的首字母
    
    private String id;
    private String birthday;//YYYY-MM-DD
    private String feteDay;//MM-DD
    private String nickname;
    private String gender;// 0:女，1:男
    private String specialday;
    private String profiles;
    private String cphone;
    private String caddress;
    private String cemail;
    private String avater;
    private String marry;//婚姻状态（0未婚1已婚）
    private String district;
    private String haveChildren;//是否有小孩（0未有1有小孩）
    private int age;
    private String policyNo;
    private String profession;
    

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSortLetters() {
        return sortLetters;
    }

    public void setSortLetters(String sortLetters) {
        this.sortLetters = sortLetters;
    }

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getSpecialday() {
		return specialday;
	}

	public void setSpecialday(String specialday) {
		this.specialday = specialday;
	}

	public String getProfiles() {
		return profiles;
	}

	public void setProfiles(String profiles) {
		this.profiles = profiles;
	}

	public String getCphone() {
		return cphone;
	}

	public void setCphone(String cphone) {
		this.cphone = cphone;
	}

	public String getCaddress() {
		return caddress;
	}

	public void setCaddress(String caddress) {
		this.caddress = caddress;
	}

	public String getCemail() {
		return cemail;
	}

	public void setCemail(String cemail) {
		this.cemail = cemail;
	}

	public String getAvater() {
		return avater;
	}

	public void setAvater(String avater) {
		this.avater = avater;
	}

	public String getMarry() {
		return marry;
	}

	public void setMarry(String marry) {
		this.marry = marry;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getHaveChildren() {
		return haveChildren;
	}

	public void setHaveChildren(String haveChildren) {
		this.haveChildren = haveChildren;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getFeteDay() {
		return feteDay;
	}

	public void setFeteDay(String feteDay) {
		this.feteDay = feteDay;
	}

	public String getPolicyNo() {
		return policyNo;
	}

	public void setPolicyNo(String policyNo) {
		this.policyNo = policyNo;
	}

	public String getProfession() {
		return profession;
	}

	public void setProfession(String profession) {
		this.profession = profession;
	}

}
