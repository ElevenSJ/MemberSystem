package com.sj.activity.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Keep;

/**
 * 创建时间: on 2018/4/15.
 * 创建人: 孙杰
 * 功能描述:
 */
@Keep
public class MessageBean implements Parcelable {

    /**
     * id : 75
     * jpushId : 2
     * title : jpush推送内容测试1
     * briefIntro : jpush推送内容测试1
     * detailUrl : http://115.239.252.186:8086/api/app/v1/getJpushDetailDisplay/75
     * createTime : 2018-05-30 15:01:12.0
     * readStatus : 1
     */

    private int id;
    private int jpushId;
    private String title;
    private String briefIntro;
    private String detailUrl;
    private String createTime;
    private int readStatus;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getJpushId() {
        return jpushId;
    }

    public void setJpushId(int jpushId) {
        this.jpushId = jpushId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBriefIntro() {
        return briefIntro;
    }

    public void setBriefIntro(String briefIntro) {
        this.briefIntro = briefIntro;
    }

    public String getDetailUrl() {
        return detailUrl;
    }

    public void setDetailUrl(String detailUrl) {
        this.detailUrl = detailUrl;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getReadStatus() {
        return readStatus;
    }

    public void setReadStatus(int readStatus) {
        this.readStatus = readStatus;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeInt(this.jpushId);
        dest.writeString(this.title);
        dest.writeString(this.briefIntro);
        dest.writeString(this.detailUrl);
        dest.writeString(this.createTime);
        dest.writeInt(this.readStatus);
    }

    public MessageBean() {
    }

    protected MessageBean(Parcel in) {
        this.id = in.readInt();
        this.jpushId = in.readInt();
        this.title = in.readString();
        this.briefIntro = in.readString();
        this.detailUrl = in.readString();
        this.createTime = in.readString();
        this.readStatus = in.readInt();
    }

    public static final Parcelable.Creator<MessageBean> CREATOR = new Parcelable.Creator<MessageBean>() {
        @Override
        public MessageBean createFromParcel(Parcel source) {
            return new MessageBean(source);
        }

        @Override
        public MessageBean[] newArray(int size) {
            return new MessageBean[size];
        }
    };
}
