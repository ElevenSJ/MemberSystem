package com.sj.activity.bean;

import java.util.List;

/**
 * 创建时间: on 2018/5/6.
 * 创建人: 孙杰
 * 功能描述:
 */
public class CardBean {

    /**
     * count : 8
     * sideCount : 0
     * cpGoods : []
     * cpService : []
     * cpCourse : []
     * cpChiefBBS : [{"id":8,"type":0,"bbsName":"首席论坛第一期【营销技巧】","areaName":"东北区","price":"20.00","dayNum":"2018年04月29日 13:53","qrCode":"","indate":"2018年04月30日"},{"id":9,"type":0,"bbsName":"首席论坛第一期【营销技巧】","areaName":"东北区","price":"20.00","dayNum":"2018年04月29日 13:53","qrCode":"","indate":"2018年04月30日"},{"id":10,"type":0,"bbsName":"首席论坛第一期【营销技巧】","areaName":"东北区","price":"20.00","dayNum":"2018年04月29日 13:53","qrCode":"","indate":"2018年04月30日"},{"id":1525597882195000,"type":0,"bbsName":"首席论坛第一期【营销技巧】","areaName":"东南区","price":"10.00","dayNum":"2018年04月29日 13:53","qrCode":"http://project.app-storage-node.com/prj-mms/qrcode2018050617110001","indate":"2018年04月30日"},{"id":1525597882195001,"type":0,"bbsName":"首席论坛第一期【营销技巧】","areaName":"东南区","price":"10.00","dayNum":"2018年04月29日 13:53","qrCode":"http://project.app-storage-node.com/prj-mms/qrcode2018050617110002","indate":"2018年04月30日"},{"id":1525750453040000,"type":0,"bbsName":"首席论坛第一期【营销技巧】","areaName":"东南区","price":"10.00","dayNum":"2018年04月29日 13:53","qrCode":"http://project.app-storage-node.com/prj-mms/qrcode2018050811340001","indate":"2018年04月30日"},{"id":1525750453040001,"type":0,"bbsName":"首席论坛第一期【营销技巧】","areaName":"西北区","price":"40.00","dayNum":"2018年04月29日 13:53","qrCode":"http://project.app-storage-node.com/prj-mms/qrcode2018050811350001","indate":"2018年04月30日"},{"id":7,"type":0,"bbsName":"首席论坛第二期【如何成为一个牛逼的人】","areaName":"EAST_2","price":"1,500.00","dayNum":"2018年06月01日 18:10","qrCode":"","indate":"2018年06月01日"}]
     */

    private int count;
    private int sideCount;
//    private List<?> cpGoods;
//    private List<?> cpService;
//    private List<?> cpCourse;
    private List<CpChiefBBSBean> cpChiefBBS;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getSideCount() {
        return sideCount;
    }

    public void setSideCount(int sideCount) {
        this.sideCount = sideCount;
    }

//    public List<?> getCpGoods() {
//        return cpGoods;
//    }
//
//    public void setCpGoods(List<?> cpGoods) {
//        this.cpGoods = cpGoods;
//    }
//
//    public List<?> getCpService() {
//        return cpService;
//    }
//
//    public void setCpService(List<?> cpService) {
//        this.cpService = cpService;
//    }
//
//    public List<?> getCpCourse() {
//        return cpCourse;
//    }
//
//    public void setCpCourse(List<?> cpCourse) {
//        this.cpCourse = cpCourse;
//    }

    public List<CpChiefBBSBean> getCpChiefBBS() {
        return cpChiefBBS;
    }

    public void setCpChiefBBS(List<CpChiefBBSBean> cpChiefBBS) {
        this.cpChiefBBS = cpChiefBBS;
    }

    public static class CpChiefBBSBean {
        /**
         * id : 8
         * type : 0
         * bbsName : 首席论坛第一期【营销技巧】
         * areaName : 东北区
         * price : 20.00
         * dayNum : 2018年04月29日 13:53
         * qrCode :
         * indate : 2018年04月30日
         */

        private String id;
        private int type;
        private String bbsName;
        private String areaName;
        private String price;
        private String dayNum;
        private String qrCode;
        private String indate;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getBbsName() {
            return bbsName;
        }

        public void setBbsName(String bbsName) {
            this.bbsName = bbsName;
        }

        public String getAreaName() {
            return areaName;
        }

        public void setAreaName(String areaName) {
            this.areaName = areaName;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getDayNum() {
            return dayNum;
        }

        public void setDayNum(String dayNum) {
            this.dayNum = dayNum;
        }

        public String getQrCode() {
            return qrCode;
        }

        public void setQrCode(String qrCode) {
            this.qrCode = qrCode;
        }

        public String getIndate() {
            return indate;
        }

        public void setIndate(String indate) {
            this.indate = indate;
        }
    }
}
