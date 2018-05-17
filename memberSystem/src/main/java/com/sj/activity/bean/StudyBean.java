package com.sj.activity.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SunJ on 2018/5/14.
 */

public class StudyBean implements Parcelable {

    /**
     * id : 1
     * title : 断剑-不相信自己的意志、永远也成不了将军
     * thumbnail : http://project.app-storage-node.com/prj-mms/2018050318090002.jpg
     * readQuantity : 1
     * createTime : 2018-05-14 17:57
     * attachStatus : 0
     * detailUrl : api/app/v1/getMorningMeetingDetailDisplay/1
     * attachs : [{"attachId":1,"fileName":"sss","fileUrl":"sss"}]
     */

    private String id;
    private String title;
    private String thumbnail;
    private int readQuantity;
    private String createTime;
    private int attachStatus;
    private String detailUrl;
    private List<AttachsBean> attachs;

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

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public int getReadQuantity() {
        return readQuantity;
    }

    public void setReadQuantity(int readQuantity) {
        this.readQuantity = readQuantity;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getAttachStatus() {
        return attachStatus;
    }

    public void setAttachStatus(int attachStatus) {
        this.attachStatus = attachStatus;
    }

    public String getDetailUrl() {
        return detailUrl;
    }

    public void setDetailUrl(String detailUrl) {
        this.detailUrl = detailUrl;
    }

    public List<AttachsBean> getAttachs() {
        return attachs;
    }

    public void setAttachs(List<AttachsBean> attachs) {
        this.attachs = attachs;
    }

    public static class AttachsBean {
        /**
         * attachId : 1
         * fileName : sss
         * fileUrl : sss
         */
        private int attachId;
        private String fileName;
        private String fileUrl;

        public int getAttachId() {
            return attachId;
        }

        public void setAttachId(int attachId) {
            this.attachId = attachId;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getFileUrl() {
            return fileUrl;
        }

        public void setFileUrl(String fileUrl) {
            this.fileUrl = fileUrl;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.title);
        dest.writeString(this.thumbnail);
        dest.writeInt(this.readQuantity);
        dest.writeString(this.createTime);
        dest.writeInt(this.attachStatus);
        dest.writeString(this.detailUrl);
        dest.writeList(this.attachs);
    }

    public StudyBean() {
    }

    protected StudyBean(Parcel in) {
        this.id = in.readString();
        this.title = in.readString();
        this.thumbnail = in.readString();
        this.readQuantity = in.readInt();
        this.createTime = in.readString();
        this.attachStatus = in.readInt();
        this.detailUrl = in.readString();
        this.attachs = new ArrayList<AttachsBean>();
        in.readList(this.attachs, AttachsBean.class.getClassLoader());
    }

    public static final Parcelable.Creator<StudyBean> CREATOR = new Parcelable.Creator<StudyBean>() {
        @Override
        public StudyBean createFromParcel(Parcel source) {
            return new StudyBean(source);
        }

        @Override
        public StudyBean[] newArray(int size) {
            return new StudyBean[size];
        }
    };
}
