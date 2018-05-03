package com.lyp.membersystem.database;

import java.util.List;

import com.lyp.membersystem.bean.QueryContactBean;
import com.lyp.membersystem.view.contactsort.ContactSortModel;

import android.content.Context;

public class CustomerDao {
	public static final String TABLE_NAME = "customer";
	public static final String COLUMN_NAME_ID = "id";
	public static final String COLUMN_NAME_NAME = "name";
	public static final String COLUMN_NAME_NICK = "nickname";
	public static final String COLUMN_NAME_AVATAR = "avatar";
	public static final String COLUMN_NAME_BIRTHDAY = "birthday";//出生日期YYYY-MM-DD
	public static final String COLUMN_NAME_FETE_DAY = "fete_day";//生日MM-DD
	public static final String COLUMN_NAME_GENDER = "gender";//性别1：男，0：女
	public static final String COLUMN_NAME_SPECIALDAY = "specialday";
	public static final String COLUMN_NAME_PROFILES = "profiles";
	public static final String COLUMN_NAME_PHONE = "cphone";
	public static final String COLUMN_NAME_ADDRESS = "caddress";
	public static final String COLUMN_NAME_EMAIL = "cemail";
	public static final String COLUMN_NAME_MARRY = "marry";//婚姻状况1：已婚，0：未婚, 3:离异，4:丧偶
	public static final String COLUMN_NAME_DISTRICT = "district";
	public static final String COLUMN_NAME_HAVECHILDREN = "haveChildren";//是否有小孩1：有，0：暂无
	public static final String COLUMN_NAME_AGE = "age";
	public static final String COLUMN_NAME_POLICY_NO = "policyNo";
	public static final String COLUMN_NAME_PROFESSION = "profession";
	
    public CustomerDao(Context context) {
    	DBManager.getInstance().onInit(context);
    }
	
	/**
	 * 保存好友list
	 * 
	 * @return
	 */
	public void saveContactList(List<ContactSortModel> contactList) {
		
	    DBManager.getInstance().saveContactList(contactList);
	}
	
	/**
	 * 获取好友list
	 * 
	 * @return
	 */
	public List<ContactSortModel> getContactList() {
		
	    return DBManager.getInstance().getContactList();
	}

	
	/**
	 * 获取一个好友
	 * 
	 * @return
	 */
	public ContactSortModel getContact(String id) {
		
	    return DBManager.getInstance().getContact(id);
	}
	
	/**
	 * 删除一个联系人
	 * @param username
	 */
	public void deleteContact(String id){
		DBManager.getInstance().deleteContact(id);
	}
	
	/**
	 * 保存一个联系人
	 * @param user
	 */
	public void saveContact(ContactSortModel contactSortModel){
		DBManager.getInstance().saveContact(contactSortModel);
	}
	
	/**
	 * 通过生日获取好友list
	 * 
	 * @return
	 */
	public List<ContactSortModel> getContactList(String feteDay) {
	    return DBManager.getInstance().getContactList(feteDay);
	}
	
	/**
	 * 通过条件获取好友list
	 * 
	 * @return
	 */
	public List<ContactSortModel> getContactList(QueryContactBean queryContactBean) {
	    return DBManager.getInstance().getContactList(queryContactBean);
	}
	
	/**
	 * 通过性别获取男好友list
	 * 
	 * @return
	 */
	public List<ContactSortModel> getMaleContactList() {
	    return DBManager.getInstance().getGengerContactList("1");
	}
	
	/**
	 * 通过性别获取女好友list
	 * 
	 * @return
	 */
	public List<ContactSortModel> getFeMaleContactList() {
	    return DBManager.getInstance().getGengerContactList("0");
	}
	
	/**
	 * 获取儿童list
	 * 
	 * @return
	 */
	public List<ContactSortModel> getErTongContactList() {
	    return DBManager.getInstance().getErTongContactList("12");
	}
}
