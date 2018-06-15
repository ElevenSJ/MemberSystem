package com.sj.activity.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by SunJ on 2018/5/14.
 */

public class StudyBean implements Serializable {


    protected String id;
    protected String title;
    protected String thumbnail;

    protected String detailUrl;
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

    public static class AttachsBean implements Serializable {
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
}
