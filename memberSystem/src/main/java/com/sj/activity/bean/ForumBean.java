package com.sj.activity.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 创建时间: on 2018/5/6.
 * 创建人: 孙杰
 * 功能描述:
 */
public class ForumBean implements Parcelable {

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

    private String id;
    private String name;
    private String indate;
    private String previewUrl;
    private String intro;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIndate() {
        return indate;
    }

    public void setIndate(String indate) {
        this.indate = indate;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.indate);
        dest.writeString(this.previewUrl);
        dest.writeString(this.intro);
        dest.writeString(this.daynum);
        dest.writeInt(this.status);
        dest.writeStringList(this.slideshowUrl);
        dest.writeList(this.items);
    }

    public ForumBean() {
    }

    protected ForumBean(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.indate = in.readString();
        this.previewUrl = in.readString();
        this.intro = in.readString();
        this.daynum = in.readString();
        this.status = in.readInt();
        this.slideshowUrl = in.createStringArrayList();
        this.items = new ArrayList<ItemsBean>();
        in.readList(this.items, ItemsBean.class.getClassLoader());
    }

    public static final Parcelable.Creator<ForumBean> CREATOR = new Parcelable.Creator<ForumBean>() {
        @Override
        public ForumBean createFromParcel(Parcel source) {
            return new ForumBean(source);
        }

        @Override
        public ForumBean[] newArray(int size) {
            return new ForumBean[size];
        }
    };
}
