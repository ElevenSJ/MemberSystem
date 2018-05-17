package com.sj.http;

import android.support.annotation.Keep;

/**
 * 创建时间: on 2018/3/31.
 * 创建人: 孙杰
 * 功能描述:请求地址配置
 */
@Keep
public class UrlConfig {
    //baseUrl
    //生成环境
    public static final String BASE_URL = "http://115.239.252.186:8086/";
//    public static final String BASE_URL = "http://yeedaa.com/";

    //注册协议地址
    public static final String GET_REGISTER_HTML = "/api/app/v1/ getMemberRegistAgreement";
    //获取短信验证码
    public static final String GET_SMS_CODE = "api/app/v1/getMemberRegisterAuthcode";
    //注册
    public static final String DO_REGISTER = "api/app/v1/memberRegister";

    //首席论坛列表
    public static final String FORUM_LIST = "api/app/v1/getChiefbbsList";
    //卡包列表
    public static final String CARD_LIST = "api/app/v1/getMemberCouponListById";
    //首席论坛购买
    public static final String FORUM_BUY_LIST = "api/app/v1/buyCpChiefbbs";
    //首席论坛详情
    public static final String FORUM_DETAIL_HTML = "api/app/v1/getChiefbbsDetailDisplay";
    //首页轮播图
    public static final String BANNER_LIST = "api/app/v1/getHomePageSlideshowDisplay";
    //进修学习轮播图
    public static final String BANNER_LIST_STUDY = "api/app/v1/getFurtherStudySlideshowDisplay";
    //会员信息
    public static final String MEMBER_INFO = "api/app/v1/getMemberPersonalInfo";
    //修改会员信息
    public static final String UPDATE_MEMBER_INFO = "api/app/v1/updateMemberPersonalInfo";

    //早会题材列表
    public static final String MORNING_MEETING_LIST = "api/app/v1/getMorningMeetingList";
    //精粹时光列表
    public static final String BRIEFING_TIME_LIST = "api/app/v1/getBriefingTimeList";
    //培训课程列表
    public static final String TRAIN_COURSE_LIST = "api/app/v1/getTrainCourseList";
    //讲师介绍列表
    public static final String LECTURER_LIST = "api/app/v1/getLecturerList";
    //MDRT秘籍列表
    public static final String MDRT_LIST = "api/app/v1/getMdrtList";
    //小课堂列表
    public static final String SMALL_CLASS_LIST = "api/app/v1/getSmallClassList";

}