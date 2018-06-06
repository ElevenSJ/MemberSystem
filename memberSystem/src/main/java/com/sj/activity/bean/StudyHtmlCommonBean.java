package com.sj.activity.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by SunJ on 2018/5/17.
 */

public class StudyHtmlCommonBean  extends StudyBean{

    private String readQuantity;
    private String createTime;
    private int attachStatus;
    private String detailUrl;
    private List<AttachsBean> attachs;


    public String getReadQuantity() {
        return readQuantity;
    }

    public void setReadQuantity(String readQuantity) {
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
