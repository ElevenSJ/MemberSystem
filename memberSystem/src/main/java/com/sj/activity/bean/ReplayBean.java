package com.sj.activity.bean;

/**
 * 创建时间: on 2018/6/11.
 * 创建人: 孙杰
 * 功能描述:
 */
public class ReplayBean {


    /**
     * id : 4
     * askcontent : 随便评论的
     * askName : Jack
     * askTime : 2018-06-09 18:37
     * avatar : http://project.app-storage-node.com/prj-mms/2018051415370001.png
     * starNumber : 1
     * replyComment :
     */

    private int id;
    private String askcontent;
    private String askName;
    private String askTime;
    private String avatar;
    private float starNumber;
    private String replyComment;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAskcontent() {
        return askcontent;
    }

    public void setAskcontent(String askcontent) {
        this.askcontent = askcontent;
    }

    public String getAskName() {
        return askName;
    }

    public void setAskName(String askName) {
        this.askName = askName;
    }

    public String getAskTime() {
        return askTime;
    }

    public void setAskTime(String askTime) {
        this.askTime = askTime;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public float getStarNumber() {
        return starNumber;
    }

    public void setStarNumber(float starNumber) {
        this.starNumber = starNumber;
    }

    public String getReplyComment() {
        return replyComment;
    }

    public void setReplyComment(String replyComment) {
        this.replyComment = replyComment;
    }
}
