package com.sj.activity.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 创建时间: on 2018/5/6.
 * 创建人: 孙杰
 * 功能描述:
 */
public class ForumBean extends StudyBean{

    /**
     * id : 1
     * name : 首席论坛第一期【营销技巧】
     * intro : 600~2000
     * indate : 2018-04-30 13:52:55.0
     * daynum : 2018-04-29 13:53:08.0
     * status : 1
     * slideshowUrl : ["1111.jpg","2222.jpg","333.jpg"]
     * previewUrl : mms/2018050318090002.jpg
     * items : [{"id":1,"areaName":"东南区","price":1000,"total":10,"status":1},{"id":2,"areaName":"东北区","price":2000,"total":10,"status":1},{"id":3,"areaName":"西南区","price":3000,"total":20,"status":1},{"id":4,"areaName":"西北区","price":4000,"total":20,"status":1}]
     */

    private String daynum;
    private int status;
    private List<String> slideshowUrl;
    private List<ItemsBean> items;

    public String getDaynum() {
        return daynum;
    }

    public void setDaynum(String daynum) {
        this.daynum = daynum;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<String> getSlideshowUrl() {
        return slideshowUrl;
    }

    public void setSlideshowUrl(List<String> slideshowUrl) {
        this.slideshowUrl = slideshowUrl;
    }

    public List<ItemsBean> getItems() {
        return items;
    }

    public void setItems(List<ItemsBean> items) {
        this.items = items;
    }

    public static class ItemsBean implements Serializable{
        /**
         * id : 1
         * areaName : 东南区
         * price : 1000
         * total : 10
         * status : 1
         */

        private int id;
        private String areaName;
        private int price;
        private int total;
        private int status;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getAreaName() {
            return areaName;
        }

        public void setAreaName(String areaName) {
            this.areaName = areaName;
        }

        public int getPrice() {
            return price;
        }

        public void setPrice(int price) {
            this.price = price;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }
    }
}
