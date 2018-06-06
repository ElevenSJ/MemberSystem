package com.sj.activity.bean;

import java.util.List;

/**
 * 创建时间: on 2018/5/29.
 * 创建人: 孙杰
 * 功能描述:
 */
public class LecturerDetailBean {


    /**
     * id : 1
     * avatar : http://project.app-storage-node.com/prj-mms/2018050318090002.jpg
     * name : 张学明
     * briefIntro : 11
     * courseTotal : 1
     * trainCourse : [{"id":1,"title":"新人培养成MDR会员的秘籍","thumbnail":"http://project.app-storage-node.com/prj-mms/2018050318090002.jpg","price":150,"schooltime":"05-24 18:13","schoolLocation":"天河区体育西路111号建和中心","applicationStartTime":"05-14 18:13","applicationEndTime":"05-14 18:13","detailUrl":"http://115.239.252.186:8086/api/app/v1/getTrainCourseDetailDisplay/1","lecturerId":1}]
     */

    private String id;
    private String avatar;
    private String name;
    private String briefIntro;
    private int courseTotal;
    private List<TrainClassBean> trainCourse;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public String getBriefIntro() {
        return briefIntro;
    }

    public void setBriefIntro(String briefIntro) {
        this.briefIntro = briefIntro;
    }

    public int getCourseTotal() {
        return courseTotal;
    }

    public void setCourseTotal(int courseTotal) {
        this.courseTotal = courseTotal;
    }

    public List<TrainClassBean> getTrainCourse() {
        return trainCourse;
    }

    public void setTrainCourse(List<TrainClassBean> trainCourse) {
        this.trainCourse = trainCourse;
    }
}
