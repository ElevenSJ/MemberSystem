package com.sj.activity.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 创建时间: on 2018/5/12.
 * 创建人: 孙杰
 * 功能描述:
 */
public class UserBean implements Parcelable {


    /**
     * avatar :
     * name :
     * phone : 15605198042
     * type : 0
     * status : 1
     * validEndTime : 2019-05-06
     * gender : 0
     * age : 0
     * jobTitle :
     * education :
     * company :
     * companyAddress :
     * businessAddress :
     * businesscard :
     * email :
     * qgraph :
     * qgraphbasemap :
     * birthday :
     * signature :
     * signaturebasemap :
     * maritalStatus : 0
     * isRegister : 1
     * qrCode : http://project.app-storage-node.com/prj-mms/qrcode2018051216230001
     * balance : 0.00
     */

    private String avatar;
    private String name;
    private String phone;
    private int type;
    private int status;
    private String validEndTime;
    private int gender;
    private int age;
    private String jobTitle;
    private String education;
    private String company;
    private String companyAddress;
    private String businessAddress;
    private String businesscard;
    private String email;
    private String qgraph;
    private String qgraphbasemap;
    private String birthday;
    private String signature;
    private String signaturebasemap;
    private int maritalStatus;
    private int isRegister;
    private String qrCode;
    private String balance;

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getValidEndTime() {
        return validEndTime;
    }

    public void setValidEndTime(String validEndTime) {
        this.validEndTime = validEndTime;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
    }

    public String getBusinessAddress() {
        return businessAddress;
    }

    public void setBusinessAddress(String businessAddress) {
        this.businessAddress = businessAddress;
    }

    public String getBusinesscard() {
        return businesscard;
    }

    public void setBusinesscard(String businesscard) {
        this.businesscard = businesscard;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getQgraph() {
        return qgraph;
    }

    public void setQgraph(String qgraph) {
        this.qgraph = qgraph;
    }

    public String getQgraphbasemap() {
        return qgraphbasemap;
    }

    public void setQgraphbasemap(String qgraphbasemap) {
        this.qgraphbasemap = qgraphbasemap;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getSignaturebasemap() {
        return signaturebasemap;
    }

    public void setSignaturebasemap(String signaturebasemap) {
        this.signaturebasemap = signaturebasemap;
    }

    public int getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(int maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public int getIsRegister() {
        return isRegister;
    }

    public void setIsRegister(int isRegister) {
        this.isRegister = isRegister;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.avatar);
        dest.writeString(this.name);
        dest.writeString(this.phone);
        dest.writeInt(this.type);
        dest.writeInt(this.status);
        dest.writeString(this.validEndTime);
        dest.writeInt(this.gender);
        dest.writeInt(this.age);
        dest.writeString(this.jobTitle);
        dest.writeString(this.education);
        dest.writeString(this.company);
        dest.writeString(this.companyAddress);
        dest.writeString(this.businessAddress);
        dest.writeString(this.businesscard);
        dest.writeString(this.email);
        dest.writeString(this.qgraph);
        dest.writeString(this.qgraphbasemap);
        dest.writeString(this.birthday);
        dest.writeString(this.signature);
        dest.writeString(this.signaturebasemap);
        dest.writeInt(this.maritalStatus);
        dest.writeInt(this.isRegister);
        dest.writeString(this.qrCode);
        dest.writeString(this.balance);
    }

    public UserBean() {
    }

    protected UserBean(Parcel in) {
        this.avatar = in.readString();
        this.name = in.readString();
        this.phone = in.readString();
        this.type = in.readInt();
        this.status = in.readInt();
        this.validEndTime = in.readString();
        this.gender = in.readInt();
        this.age = in.readInt();
        this.jobTitle = in.readString();
        this.education = in.readString();
        this.company = in.readString();
        this.companyAddress = in.readString();
        this.businessAddress = in.readString();
        this.businesscard = in.readString();
        this.email = in.readString();
        this.qgraph = in.readString();
        this.qgraphbasemap = in.readString();
        this.birthday = in.readString();
        this.signature = in.readString();
        this.signaturebasemap = in.readString();
        this.maritalStatus = in.readInt();
        this.isRegister = in.readInt();
        this.qrCode = in.readString();
        this.balance = in.readString();
    }

    public static final Parcelable.Creator<UserBean> CREATOR = new Parcelable.Creator<UserBean>() {
        @Override
        public UserBean createFromParcel(Parcel source) {
            return new UserBean(source);
        }

        @Override
        public UserBean[] newArray(int size) {
            return new UserBean[size];
        }
    };
}
