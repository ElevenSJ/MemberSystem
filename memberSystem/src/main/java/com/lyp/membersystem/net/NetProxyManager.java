package com.lyp.membersystem.net;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.lyp.membersystem.bean.NoticeBean;
import com.lyp.membersystem.log.LogUtils;
import com.lyp.membersystem.view.contactsort.ContactSortModel;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class NetProxyManager {

	private ExecutorService executorService;
	private static NetProxyManager netProxyManager;
	private static final String TAG = NetProxyManager.class.getSimpleName();

	private NetProxyManager() {
		executorService = Executors.newSingleThreadExecutor();
	};

	public static NetProxyManager getInstance() {
		if (netProxyManager == null) {
			netProxyManager = new NetProxyManager();
		}
		return netProxyManager;
	}

	/**
	 * 获取验证码
	 * 
	 * @param handler
	 * @param phone
	 * @param what
	 */
	public void toGetSMSAuthCode(final Handler handler, final String phone) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				StringBuffer sb = new StringBuffer();
				sb.append(API.API_GET_SMS_AUTH_CODE);
				sb.append("?");
				sb.append("phone=");
				sb.append(phone);
				String result = HttpManager.httpGet(sb.toString());
				Message msg = new Message();
				msg.what = MessageContants.MSG_GET_SMS_AUTH_CODE;
				msg.obj = result;
				handler.sendMessage(msg);
			}
		}).start();
	}

	/**
	 * 登录接口
	 * 
	 * @param handler
	 * @param phone
	 * @param authcode
	 * @param what
	 */
	public void toLogin(final Handler handler, final String phone, final String authcode) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				StringBuffer sb = new StringBuffer();
				sb.append(API.API_LOGIN);
				sb.append("?");
				sb.append("phone=");
				sb.append(phone);
				sb.append("&authCode=");
				sb.append(authcode);
				String result = HttpManager.httpGet(sb.toString());
				Message msg = new Message();
				msg.what = MessageContants.MSG_LOGIN;
				msg.obj = result;
				handler.sendMessage(msg);
			}
		}).start();
	}

	/**
	 * 获取客户列表
	 * 
	 * @param handler
	 * @param tokenid
	 * @param saleId
	 * @param cname
	 * @param cphone
	 * @param pageNum
	 */
	public void toGetCustomerList(final Handler handler, final String tokenid, final String saleId, final String cname,
			final String cphone, final int pageNum) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				StringBuffer sb = new StringBuffer();
				sb.append(API.API_GET_CUSTOMER_LIST);
				sb.append("?");
				sb.append("token_id=");
				sb.append(tokenid);
				sb.append("&saleId=");
				sb.append(saleId);
				if (cname != null) {
					sb.append("&cname=");
					sb.append(cname);
				}
				if (cphone != null) {
					sb.append("&cphone=");
					sb.append(cphone);
				}
				if (pageNum > 0) {
					sb.append("&pageNum=");
					sb.append(pageNum);
				}
				sb.append("&pageSize=");
				sb.append("0");

				String result = HttpManager.httpGet(sb.toString());
				Message msg = new Message();
				msg.what = MessageContants.MSG_GET_CUSTOMER_LIST;
				msg.obj = result;
				handler.sendMessage(msg);
			}
		}).start();
	}

	/**
	 * 增加客户接口
	 * 
	 * @param handler
	 * @param tokenid
	 * @param salemanId
	 * @param cname
	 * @param cphone
	 * @param nickname
	 * @param specialday
	 * @param profiles
	 * @param caddress
	 * @param gender
	 */
	public void toAddCustomer(final Handler handler, final String tokenid, final String salemanId,
			final ContactSortModel contactSortModel) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				Map<String, String> params = new HashMap<String, String>();
				params.put("token_id", tokenid);
				params.put("salemanId", salemanId);
				params.put("cname", contactSortModel.getName());
				params.put("cphone", contactSortModel.getCphone());
				params.put("district", contactSortModel.getDistrict());
				params.put("maritalStatus", contactSortModel.getMarry());
				if (contactSortModel.getHaveChildren() != null) {
				    params.put("haveChildren", contactSortModel.getHaveChildren());
				}
				if (contactSortModel.getCemail() != null) {
				    params.put("cemail", contactSortModel.getCemail());
				}
				if (contactSortModel.getAvater() != null) {
					params.put("avatar", contactSortModel.getAvater());
				}
				if (contactSortModel.getNickname() != null) {
					params.put("nickname", contactSortModel.getNickname());
				}
				if (contactSortModel.getGender() != null) {
					params.put("gender", contactSortModel.getGender());
				}
				if (contactSortModel.getSpecialday() != null) {
					params.put("specialday", contactSortModel.getSpecialday());
				}
				if (contactSortModel.getBirthday() != null) {
					params.put("birthday", contactSortModel.getBirthday());
				}
				if (contactSortModel.getProfiles() != null) {
					params.put("profiles", contactSortModel.getProfiles());
				}
				if (contactSortModel.getCaddress() != null) {
					params.put("caddress", contactSortModel.getCaddress());
				}
				if (contactSortModel.getPolicyNo() != null) {
					params.put("policyNo", contactSortModel.getPolicyNo());
				}
				// 职业移除了
//				if (contactSortModel.getProfession() != null) {
//					params.put("profession", contactSortModel.getProfession());
//				}
				// List<String> images = eLog.getImages();
				// Map<String, List<File>> files = null;
				// if (images != null && images.size() > 0) {
				// files = new HashMap<String, List<File>>();
				// List<File> fileList = new ArrayList<File>();
				// for (int i = 0; i < images.size(); i++) {
				// fileList.add(new File(images.get(i)));
				// }
				// files.put("imageFiles", fileList);
				// }
				String result = "error";
				try {
					// result = HttpSession.getPostResults(API.API_ADD_CUSTOMER,
					// params, files);
					result = HttpSession.getRequestResult(API.API_ADD_CUSTOMER, params);
				} catch (Exception ex) {
					result = "error";
					Log.e(TAG, ex.getMessage());
				} finally {
					Message msg = new Message();
					msg.what = MessageContants.MSG_ADD_CUSTOMER;
					msg.obj = result;
					handler.sendMessage(msg);
				}
			}
		}).start();
	}

	/**
	 * 增加客户接口 暂不使用
	 * 
	 * @param handler
	 * @param tokenid
	 * @param salemanId
	 * @param cname
	 * @param cphone
	 * @param nickname
	 * @param specialday
	 * @param profiles
	 * @param caddress
	 * @param gender
	 */
	public void toAddCustomerGet(final Handler handler, final String tokenid, final String salemanId,
			final ContactSortModel contactSortModel) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				StringBuffer sb = new StringBuffer();
				sb.append(API.API_ADD_CUSTOMER);
				sb.append("?");
				sb.append("token_id=");
				sb.append(tokenid);
				sb.append("&salemanId=");
				sb.append(salemanId);
				sb.append("&cname=");
				sb.append(contactSortModel.getName());
				sb.append("&cphone=");
				sb.append(contactSortModel.getCphone());
				sb.append("&district=");
				sb.append(contactSortModel.getDistrict());
				sb.append("&maritalStatus=");
				sb.append(contactSortModel.getMarry());
				sb.append("&haveChildren=");
				sb.append(contactSortModel.getHaveChildren());
				if (contactSortModel.getNickname() != null) {
					sb.append("&nickname=");
					sb.append(contactSortModel.getNickname());
				}
				if (contactSortModel.getGender() != null) {
					sb.append("&gender=");
					sb.append(contactSortModel.getGender());
				}
				// if (contactSortModel.getSpecialday() != null) {
				// sb.append("&specialday=");
				// sb.append(contactSortModel.getSpecialday());
				// }
				if (contactSortModel.getBirthday() != null) {
					sb.append("&specialday=");
					sb.append(contactSortModel.getBirthday());
				}
				if (contactSortModel.getProfiles() != null) {
					sb.append("&profiles=");
					sb.append(contactSortModel.getProfiles());
				}
				if (contactSortModel.getCaddress() != null) {
					sb.append("&caddress=");
					sb.append(contactSortModel.getCaddress());
				}
				String result = HttpManager.httpGet(sb.toString());
				Message msg = new Message();
				msg.what = MessageContants.MSG_ADD_CUSTOMER;
				msg.obj = result;
				handler.sendMessage(msg);
			}
		}).start();
	}

	/**
	 * 修改客户接口
	 * 
	 * @param handler
	 * @param tokenid
	 * @param salemanId
	 * @param cname
	 * @param cphone
	 * @param nickname
	 * @param specialday
	 * @param profiles
	 * @param caddress
	 * @param gender
	 */
	public void toUpdateCustomer(final Handler handler, final String tokenid, final String salemanId,
			final ContactSortModel contactSortModel) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				Map<String, String> params = new HashMap<String, String>();
				params.put("token_id", tokenid);
				params.put("salemanId", salemanId);
				params.put("id", contactSortModel.getId());
				params.put("district", contactSortModel.getDistrict());
				params.put("maritalStatus", contactSortModel.getMarry());
				if (contactSortModel.getHaveChildren() != null) {
				    params.put("haveChildren", contactSortModel.getHaveChildren());
				}
				if (contactSortModel.getCemail() != null) {
			    	params.put("cemail", contactSortModel.getCemail());
				}
				if (contactSortModel.getAvater() != null) {
					params.put("avatar", contactSortModel.getAvater());
				}
				if (contactSortModel.getName() != null) {
					params.put("cname", contactSortModel.getName());
				}
				if (contactSortModel.getCphone() != null) {
					params.put("cphone", contactSortModel.getCphone());
				}
				if (contactSortModel.getNickname() != null) {
					params.put("nickname", contactSortModel.getNickname());
				}
				if (contactSortModel.getGender() != null) {
					params.put("gender", contactSortModel.getGender());
				}
				if (contactSortModel.getSpecialday() != null) {
					params.put("specialday", contactSortModel.getSpecialday());
				}
				if (contactSortModel.getBirthday() != null) {
					// params.put("specialday", contactSortModel.getBirthday());
					params.put("birthday", contactSortModel.getBirthday());
				}
				if (contactSortModel.getProfiles() != null) {
					params.put("profiles", contactSortModel.getProfiles());
				}
				if (contactSortModel.getCaddress() != null) {
					params.put("caddress", contactSortModel.getCaddress());
				}
				if (contactSortModel.getPolicyNo() != null) {
					params.put("policyNo", contactSortModel.getPolicyNo());
				}
				// 职业移除了
//				if (contactSortModel.getProfession() != null) {
//					params.put("profession", contactSortModel.getProfession());
//				}
				// List<String> images = eLog.getImages();
				// Map<String, List<File>> files = null;
				// if (images != null && images.size() > 0) {
				// files = new HashMap<String, List<File>>();
				// List<File> fileList = new ArrayList<File>();
				// for (int i = 0; i < images.size(); i++) {
				// fileList.add(new File(images.get(i)));
				// }
				// files.put("imageFiles", fileList);
				// }
				String result = "error";
				try {
					// result = HttpSession.getPostResults(API.API_ADD_CUSTOMER,
					// params, files);
					result = HttpSession.getRequestResult(API.API_UPDATE_CUSTOMER, params);
				} catch (Exception ex) {
					result = "error";
					Log.e(TAG, ex.getMessage());
				} finally {
					Message msg = new Message();
					msg.what = MessageContants.MSG_UPDATE_CUSTOMER;
					msg.obj = result;
					handler.sendMessage(msg);
				}
			}
		}).start();
	}

	/**
	 * 修改客户接口 暂不使用
	 * 
	 * @param handler
	 * @param tokenid
	 * @param salemanId
	 * @param cname
	 * @param cphone
	 * @param nickname
	 * @param specialday
	 * @param profiles
	 * @param caddress
	 * @param gender
	 */
	public void toUpdateCustomerGet(final Handler handler, final String tokenid, final String salemanId,
			final ContactSortModel contactSortModel) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				StringBuffer sb = new StringBuffer();
				sb.append(API.API_UPDATE_CUSTOMER);
				sb.append("?");
				sb.append("token_id=");
				sb.append(tokenid);
				sb.append("&salemanId=");
				sb.append(salemanId);
				sb.append("&id=");
				sb.append(contactSortModel.getId());
				sb.append("&cname=");
				sb.append(contactSortModel.getName());
				sb.append("&cphone=");
				sb.append(contactSortModel.getCphone());
				sb.append("&district=");
				sb.append(contactSortModel.getDistrict());
				sb.append("&maritalStatus=");
				sb.append(contactSortModel.getMarry());
				sb.append("&haveChildren=");
				sb.append(contactSortModel.getHaveChildren());
				if (contactSortModel.getNickname() != null) {
					sb.append("&nickname=");
					sb.append(contactSortModel.getNickname());
				}
				if (contactSortModel.getGender() != null) {
					sb.append("&gender=");
					sb.append(contactSortModel.getGender());
				}
				// if (contactSortModel.getSpecialday() != null) {
				// sb.append("&specialday=");
				// sb.append(contactSortModel.getSpecialday());
				// }
				if (contactSortModel.getBirthday() != null) {
					sb.append("&specialday=");
					sb.append(contactSortModel.getBirthday());
				}
				if (contactSortModel.getProfiles() != null) {
					sb.append("&profiles=");
					sb.append(contactSortModel.getProfiles());
				}
				if (contactSortModel.getCaddress() != null) {
					sb.append("&caddress=");
					sb.append(contactSortModel.getCaddress());
				}
				String result = HttpManager.httpGet(sb.toString());
				Message msg = new Message();
				msg.what = MessageContants.MSG_UPDATE_CUSTOMER;
				msg.obj = result;
				handler.sendMessage(msg);
			}
		}).start();
	}

	/**
	 * 删除客户
	 * 
	 * @param handler
	 * @param token_id
	 * @param what
	 */
	public void toDeleteCustomer(final Handler handler, final String token_id, final String salemanId,
			final String customerId) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				StringBuffer sb = new StringBuffer();
				sb.append(API.API_DELETE_CUSTOMER);
				sb.append("?");
				sb.append("token_id=");
				sb.append(token_id);
				sb.append("&salemanId=");
				sb.append(salemanId);
				sb.append("&customerId=");
				sb.append(customerId);
				String result = HttpManager.httpGet(sb.toString());
				Message msg = new Message();
				msg.what = MessageContants.MSG_DELETE_CUSTOMER;
				msg.obj = result;
				handler.sendMessage(msg);
			}
		}).start();
	}

	/**
	 * 获取产品分类
	 * 
	 * @param handler
	 * @param token_id
	 */
	public void toGetProductType(final Handler handler, final String token_id) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				StringBuffer sb = new StringBuffer();
				sb.append(API.API_PRODUCT_TYPE);
				sb.append("?");
				sb.append("token_id=");
				sb.append(token_id);
				String result = HttpManager.httpGet(sb.toString());
				Message msg = new Message();
				msg.what = MessageContants.MSG_PRODUCT_TYPE;
				msg.obj = result;
				handler.sendMessage(msg);
			}
		}).start();
	}

	/**
	 * 获取产品分类
	 * 
	 * @param handler
	 * @param token_id
	 */
	public void toGetProductList(final Handler handler, final String token_id, final int pageNum, final int pageSize,
			final String pname, final String typeId) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				StringBuffer sb = new StringBuffer();
				sb.append(API.API_PRODUCT_LIST);
				sb.append("?");
				sb.append("token_id=");
				sb.append(token_id);
				if (pageNum > 0) {
					sb.append("&pageNum=");
					sb.append(pageNum);
				}
				if (pageSize > 0) {
					sb.append("&pageSize=");
					sb.append(pageSize);
				}
				if (pname != null) {
					sb.append("&pname=");
					sb.append(pname);
				}
				if (typeId != null) {
					sb.append("&typeId=");
					sb.append(typeId);
				}
				String result = HttpManager.httpGet(sb.toString());
				Message msg = new Message();
				msg.what = MessageContants.MSG_PRODUCT_LIST;
				msg.obj = result;
				handler.sendMessage(msg);
			}
		}).start();
	}

	/**
	 * 获取产品详情
	 * 
	 * @param handler
	 * @param token_id
	 */
	public void toGetGoodInfo(final Handler handler, final String token_id, final String id) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				StringBuffer sb = new StringBuffer();
				sb.append(API.API_Good_INFO);
				sb.append("?");
				sb.append("token_id=");
				sb.append(token_id);
				sb.append("&pid=");
				sb.append(id);
				String result = HttpManager.httpGet(sb.toString());
				Message msg = new Message();
				msg.what = MessageContants.MSG_GET_GOOD_INFO;
				msg.obj = result;
				handler.sendMessage(msg);
			}
		}).start();
	}

	/**
	 * 获取个人信息
	 * 
	 * @param handler
	 * @param tokenid
	 * @param saleId
	 * @param cname
	 * @param cphone
	 * @param pageNum
	 */
	public void toGetPersonInfo(final Handler handler, final String tokenid, final String saleId) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				StringBuffer sb = new StringBuffer();
				sb.append(API.API_GET_PERSON_INFO);
				sb.append("?");
				sb.append("token_id=");
				sb.append(tokenid);
				sb.append("&saleId=");
				sb.append(saleId);

				String result = HttpManager.httpGet(sb.toString());
				Message msg = new Message();
				msg.what = MessageContants.MSG_GET_PERSON_INFO;
				msg.obj = result;
				handler.sendMessage(msg);
			}
		}).start();
	}

	/**
	 * 获取通知信息列表
	 * 
	 * @param handler
	 * @param tokenid
	 * @param saleId
	 * @param cname
	 * @param cphone
	 * @param pageNum
	 */
	public void toGetNoticeList(final Handler handler, final String tokenid, final String saleId) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				StringBuffer sb = new StringBuffer();
				sb.append(API.API_GET_NOTICE_LIST);
				sb.append("?");
				sb.append("token_id=");
				sb.append(tokenid);
				sb.append("&saleId=");
				sb.append(saleId);

				String result = HttpManager.httpGet(sb.toString());
				Message msg = new Message();
				msg.what = MessageContants.MSG_GET_NOTICE_LIST;
				msg.obj = result;
				handler.sendMessage(msg);
			}
		}).start();
	}

	/**
	 * 删除通知
	 * 
	 * @param handler
	 * @param token_id
	 * @param what
	 */
	public void toDeleteNotice(final Handler handler, final String token_id, final String salemanId,
			final String noticeId) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				StringBuffer sb = new StringBuffer();
				sb.append(API.API_DELETE_NOTICE);
				sb.append("?");
				sb.append("token_id=");
				sb.append(token_id);
				// sb.append("&salemanId=");
				// sb.append(salemanId);
				sb.append("&id=");
				sb.append(noticeId);
				String result = HttpManager.httpGet(sb.toString());
				Message msg = new Message();
				msg.what = MessageContants.MSG_DELETE_NOTICE;
				msg.obj = result;
				handler.sendMessage(msg);
			}
		}).start();
	}

	/**
	 * 获取卡片，信封列表
	 * 
	 * @param handler
	 * @param tokenid
	 * @param saleId
	 * @param cname
	 * @param cphone
	 * @param pageNum
	 */
	public void toGetCardEnvelopList(final Handler handler, final String tokenid, final String saleId) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				StringBuffer sb = new StringBuffer();
				sb.append(API.API_GET_CARD_LIST);
				sb.append("?");
				sb.append("token_id=");
				sb.append(tokenid);
				sb.append("&saleId=");
				sb.append(saleId);

				String result = HttpManager.httpGet(sb.toString());
				Message msg = new Message();
				msg.what = MessageContants.MSG_GET_CARD_LIST;
				msg.obj = result;
				handler.sendMessage(msg);
			}
		}).start();
	}

	/**
	 * 获取包装列表
	 * 
	 * @param handler
	 * @param tokenid
	 * @param saleId
	 * @param cname
	 * @param cphone
	 * @param pageNum
	 */
	public void toGetPackageList(final Handler handler, final String tokenid, final String saleId) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				StringBuffer sb = new StringBuffer();
				sb.append(API.API_GET_PACKAGE_LIST);
				sb.append("?");
				sb.append("token_id=");
				sb.append(tokenid);
				sb.append("&saleId=");
				sb.append(saleId);

				String result = HttpManager.httpGet(sb.toString());
				Message msg = new Message();
				msg.what = MessageContants.MSG_GET_PACKAGE_LIST;
				msg.obj = result;
				handler.sendMessage(msg);
			}
		}).start();
	}

	/**
	 * 下订单接口
	 */
	public void toAddOrder(final Handler handler, final String tokenid, final String salemanId,
			final String customerIds, final String products, final String card, final String productSkuIds,
			final String customText, final String addressType, final String memberAddressId) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				Map<String, String> params = new HashMap<String, String>();
				params.put("token_id", tokenid);
				params.put("salemanId", salemanId);
				params.put("customerIds", customerIds);
				params.put("productId", products);
				params.put("cardId", card);
				// 1：选中客户地址，2:会员自定义地址
				params.put("addressType", addressType);
				if (addressType.equals("2")) {
					params.put("memberAddressId", memberAddressId);
				}
				if (productSkuIds != null) {
					params.put("productSkuIds", productSkuIds);
				}
				if (customText != null) {
					params.put("customText", customText);
				}
				String result = "error";
				try {
					result = HttpSession.getRequestResult(API.API_ADD_ORDER, params);
				} catch (Exception ex) {
					result = "error";
					Log.e(TAG, ex.getMessage());
				} finally {
					Message msg = new Message();
					msg.what = MessageContants.MSG_ADD_ORDER;
					msg.obj = result;
					handler.sendMessage(msg);
				}
			}
		}).start();
	}

	public void toAddOrderGet(final Handler handler, final String tokenid, final String salemanId,
			final String customerIds, final String products, final String card, final String pack,
			final String customText) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				StringBuffer sb = new StringBuffer();
				sb.append(API.API_ADD_ORDER);
				sb.append("?");
				sb.append("token_id=");
				sb.append(tokenid);
				sb.append("&salemanId=");
				sb.append(salemanId);
				sb.append("&customerIds=");
				sb.append(customerIds);
				sb.append("&products=");
				sb.append(products);
				sb.append("&card=");
				sb.append(card);
				sb.append("&pack=");
				sb.append(pack);
				sb.append("&customText=");
				sb.append(customText);
				String result = HttpManager.httpGet(sb.toString());
				Message msg = new Message();
				msg.what = MessageContants.MSG_ADD_ORDER;
				msg.obj = result;
				handler.sendMessage(msg);
			}
		}).start();
	}

	/**
	 * 获取订单列表
	 * 
	 * @param handler
	 * @param tokenid
	 * @param saleId
	 * @param pageSize
	 * @param pageNum
	 */
	public void toGetOrderList(final Handler handler, final String tokenid, final String saleId, final int pageNum,
			final int pageSize) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				StringBuffer sb = new StringBuffer();
				sb.append(API.API_GET_ORDER_LIST);
				sb.append("?");
				sb.append("token_id=");
				sb.append(tokenid);
				sb.append("&salemanId=");
				sb.append(saleId);
				if (pageNum > 0) {
					sb.append("&pageNum=");
					sb.append(pageNum);
				}
				if (pageSize > 0) {
					sb.append("&pageSize=");
					sb.append(pageSize);
				}

				String result = HttpManager.httpGet(sb.toString());
				Message msg = new Message();
				msg.what = MessageContants.MSG_GET_ORDER_LIST;
				msg.obj = result;
				handler.sendMessage(msg);
			}
		}).start();
	}

	/**
	 * 获取订单详情
	 * 
	 * @param handler
	 * @param tokenid
	 * @param saleId
	 * @param orderId
	 */
	public void toGetOrderDetail(final Handler handler, final String tokenid, final String saleId,
			final String orderId) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				StringBuffer sb = new StringBuffer();
				sb.append(API.API_GET_ORDER_DETAIL);
				sb.append("?");
				sb.append("token_id=");
				sb.append(tokenid);
				// sb.append("&salemanId=");
				// sb.append(saleId);
				sb.append("&orderId=");
				sb.append(orderId);

				String result = HttpManager.httpGet(sb.toString());
				Message msg = new Message();
				msg.what = MessageContants.MSG_GET_ORDER_DETAIL;
				msg.obj = result;
				handler.sendMessage(msg);
			}
		}).start();
	}

	/**
	 * 规则下订单接口
	 */
	public void toAddOrderByRule(final Handler handler, final String tokenid, final String customerIds,
			final String notice_id, final String productIds, final String productSkuIds, final String cardId,
			final String customText, final String addressType, final String memberAddressId) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				Map<String, String> params = new HashMap<String, String>();
				params.put("token_id", tokenid);
				params.put("notice_id", notice_id);
				params.put("customerIds", customerIds);
				params.put("productIds", productIds);
				if (productSkuIds != null) {
					params.put("productSkuIds", productSkuIds);
				}
				params.put("cardId", cardId);
				// 1：选中客户地址，2:会员自定义地址
				params.put("addressType", addressType);
				if (addressType.equals("2")) {
					params.put("memberAddressId", memberAddressId);
				}
				if (customText != null) {
					params.put("customText", customText);
				}
				String result = "error";
				try {
					result = HttpSession.getRequestResult(API.API_ADD_RULE_ORDER, params);
				} catch (Exception ex) {
					result = "error";
					Log.e(TAG, ex.getMessage());
				} finally {
					Message msg = new Message();
					msg.what = MessageContants.MSG_ADD_RULE_ORDER;
					msg.obj = result;
					handler.sendMessage(msg);
				}
			}
		}).start();
	}

	/**
	 * 支付订单接口
	 */
	public void toPayOrder(final Handler handler, final String tokenid, final String salemanId, final String orderId) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				Map<String, String> params = new HashMap<String, String>();
				params.put("token_id", tokenid);
				params.put("salemanId", salemanId);
				params.put("orderId", orderId);
				String result = "error";
				try {
					result = HttpSession.getRequestResult(API.API_PAY_ORDER, params);
				} catch (Exception ex) {
					result = "error";
					Log.e(TAG, ex.getMessage());
				} finally {
					Message msg = new Message();
					msg.what = MessageContants.MSG_PAY_ORDER;
					msg.obj = result;
					handler.sendMessage(msg);
				}
			}
		}).start();
	}

	/**
	 * 添加头像
	 * 
	 * @param handler
	 * @param fileName
	 */
	public void toUploadAvater(final Handler handler, final String tokenId, final String fileName) {
		LogUtils.d("fileNameRRRR: " + fileName);
		new Thread(new Runnable() {
			@Override
			public void run() {
				Map<String, String> params = new HashMap<String, String>();
				params.put("token_id", tokenId);

				Map<String, File> files = new HashMap<String, File>();
				if (fileName != null && fileName.length() > 0) {
					files.put("fileName", new File(fileName));
				}
				String result = "error";
				try {
					result = HttpSession.getPostResult(API.API_UPLOAD_AVATER, params, files);
				} catch (Exception ex) {
					result = "error";
					Log.e(TAG, ex.getMessage());
				} finally {
					Message msg = new Message();
					msg.what = MessageContants.MSG_UPLOAD_AVATER;
					msg.obj = result;
					handler.sendMessage(msg);
				}

			}
		}).start();
	}

	/**
	 * 更新用户信息接口
	 */
	public void toUpdatePersonInfo(final Handler handler, final String tokenid, final String salemanId, final String id,
			final String phone, final String email, final String name, final String cpmpany, final String birthday,
			final String profession, final String office, final String company_address, final String avatar,
			final String qgraph) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				Map<String, String> params = new HashMap<String, String>();
				params.put("token_id", tokenid);
				if (salemanId != null) {
					params.put("salemanId", salemanId);
				}
				params.put("id", id);
				if (phone != null) {
					params.put("phone", phone);
				}
				if (email != null) {
					params.put("email", email);
				}
				if (name != null) {
					params.put("name", name);
				}
				if (cpmpany != null) {
					params.put("company", cpmpany);
				}
				if (birthday != null) {
					// params.put("age", age);
					params.put("birthday", birthday);
				}
				if (profession != null) {
					params.put("job", profession);
				}
				if (office != null) {
					params.put("businessAddress", office);
				}
				if (company_address != null) {
					params.put("companyAddress", company_address);
				}
				if (avatar != null) {
					params.put("avatar", avatar);
				}
				if (qgraph != null) {
					params.put("qgraph", qgraph);
				}
				String result = "error";
				try {
					result = HttpSession.getRequestResult(API.API_UPDATE_PERSON_INFO, params);
				} catch (Exception ex) {
					result = "error";
					Log.e(TAG, ex.getMessage());
				} finally {
					Message msg = new Message();
					msg.what = MessageContants.MSG_UPDATE_PERSON_INFO;
					msg.obj = result;
					handler.sendMessage(msg);
				}
			}
		}).start();
	}

	/**
	 * 获取客服ID
	 * 
	 * @param handler
	 * @param tokenid
	 */
	public void toGetCurrentAgent(final Handler handler, final String tokenid) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				StringBuffer sb = new StringBuffer();
				sb.append(API.API_GET_CURRENT_AGNET);
				sb.append("?");
				sb.append("token_id=");
				sb.append(tokenid);

				String result = HttpManager.httpGet(sb.toString());
				Message msg = new Message();
				msg.what = MessageContants.MSG_GET_CURRENT_AGNET;
				msg.obj = result;
				handler.sendMessage(msg);
			}
		}).start();
	}

	/**
	 * 获取通知信息列表
	 * 
	 * @param handler
	 * @param tokenid
	 * @param pageSize
	 * @param pageNum
	 */
	public void toGetRuleList(final Handler handler, final String tokenid, final int pageNum, final int pageSize) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				StringBuffer sb = new StringBuffer();
				sb.append(API.API_GET_RULE_LIST);
				sb.append("?");
				sb.append("token_id=");
				sb.append(tokenid);
				if (pageSize > 0) {
					sb.append("&pageSize=");
					sb.append(pageSize);
				}
				if (pageNum > 0) {
					sb.append("&pageNum=");
					sb.append(pageNum);
				}

				String result = HttpManager.httpGet(sb.toString());
				Message msg = new Message();
				msg.what = MessageContants.MSG_GET_RULE_LIST;
				msg.obj = result;
				handler.sendMessage(msg);
			}
		}).start();
	}

	/**
	 * 增加个人规则信息接口
	 */
	public void toAddPersonRule(final Handler handler, final String tokenid, final String salemanId,
			final NoticeBean noticeBean) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				Map<String, String> params = new HashMap<String, String>();
				params.put("token_id", tokenid);
				// if (salemanId != null) {
				// params.put("salemanId", salemanId);
				// }
				// params.put("Id", noticeBean.getId());
				params.put("ruleName", noticeBean.getTitle());
				params.put("remindTime", noticeBean.getRemindTime());
				params.put("condition", noticeBean.getCondition());
				params.put("cardId", noticeBean.getCard());
				if (noticeBean.getPack() != null) {
					params.put("packId", noticeBean.getPack());
				}
				String productIds = null;
				for (int i = 0; i < noticeBean.getGoodlist().size(); i++) {
					if (i == 0) {
						productIds = noticeBean.getGoodlist().get(0);
					} else {
						productIds = productIds + "," + noticeBean.getGoodlist().get(i);
					}
				}
				if (productIds != null) {
					params.put("productIds", productIds);
				}
				if (noticeBean.getGender() != null) {
					params.put("gender", noticeBean.getGender());
				}
				if (noticeBean.getMaritalStatus() != null) {
					params.put("maritalStatus", noticeBean.getMaritalStatus());
				}
				if (noticeBean.getHaveChildren() != null) {
					params.put("haveChildren", noticeBean.getHaveChildren());
				}
				if (noticeBean.getAgeMin() != null) {
					params.put("ageMin", noticeBean.getAgeMin());
				}
				if (noticeBean.getAgeMax() != null) {
					params.put("ageMax", noticeBean.getAgeMax());
				}
				String result = "error";
				try {
					result = HttpSession.getRequestResult(API.API_ADD_PERSON_RULE, params);
				} catch (Exception ex) {
					result = "error";
					Log.e(TAG, ex.getMessage());
				} finally {
					Message msg = new Message();
					msg.what = MessageContants.MSG_ADD_PERSON_RULE;
					msg.obj = result;
					handler.sendMessage(msg);
				}
			}
		}).start();
	}

	/**
	 * 更新个人规则信息接口
	 */
	public void toUpdatePersonRule(final Handler handler, final String tokenid, final String salemanId,
			final NoticeBean noticeBean) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				Map<String, String> params = new HashMap<String, String>();
				params.put("token_id", tokenid);
				// if (salemanId != null) {
				// params.put("salemanId", salemanId);
				// }
				params.put("Id", noticeBean.getId());
				params.put("ruleName", noticeBean.getTitle());
				params.put("remindTime", noticeBean.getRemindTime());
				params.put("condition", noticeBean.getCondition());
				params.put("cardId", noticeBean.getCard());
				if (noticeBean.getPack() != null) {
					params.put("packId", noticeBean.getPack());
				}
				String productIds = null;
				for (int i = 0; i < noticeBean.getGoodlist().size(); i++) {
					if (i == 0) {
						productIds = noticeBean.getGoodlist().get(0);
					} else {
						productIds = productIds + "," + noticeBean.getGoodlist().get(i);
					}
				}
				if (productIds != null) {
					params.put("productIds", productIds);
				}
				if (noticeBean.getGender() != null) {
					params.put("gender", noticeBean.getGender());
				}
				if (noticeBean.getMaritalStatus() != null) {
					params.put("maritalStatus", noticeBean.getMaritalStatus());
				}
				if (noticeBean.getHaveChildren() != null) {
					params.put("haveChildren", noticeBean.getHaveChildren());
				}
				if (noticeBean.getAgeMin() != null) {
					params.put("ageMin", noticeBean.getAgeMin());
				}
				if (noticeBean.getAgeMax() != null) {
					params.put("ageMax", noticeBean.getAgeMax());
				}
				String result = "error";
				try {
					result = HttpSession.getRequestResult(API.API_UPDATE_PERSON_RULE, params);
				} catch (Exception ex) {
					result = "error";
					Log.e(TAG, ex.getMessage());
				} finally {
					Message msg = new Message();
					msg.what = MessageContants.MSG_UPDATE_PERSON_RULE;
					msg.obj = result;
					handler.sendMessage(msg);
				}
			}
		}).start();
	}

	/**
	 * 删除个人规则
	 * 
	 * @param handler
	 * @param tokenid
	 */
	public void toDeletePersonRule(final Handler handler, final String tokenid, final String Id) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				StringBuffer sb = new StringBuffer();
				sb.append(API.API_DELETE_PERSON_RULE);
				sb.append("?");
				sb.append("token_id=");
				sb.append(tokenid);
				sb.append("&id=");
				sb.append(Id);

				String result = HttpManager.httpGet(sb.toString());
				Message msg = new Message();
				msg.what = MessageContants.MSG_DELETE_PERSON_RULE;
				msg.obj = result;
				handler.sendMessage(msg);
			}
		}).start();
	}

	/**
	 * 添加Q图
	 * 
	 * @param handler
	 * @param fileName
	 */
	public void toUploadQImg(final Handler handler, final String tokenId, final String fileName) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				Map<String, String> params = new HashMap<String, String>();
				params.put("token_id", tokenId);

				Map<String, File> files = new HashMap<String, File>();
				if (fileName != null && fileName.length() > 0) {
					files.put("fileName", new File(fileName));
				}
				String result = "error";
				try {
					result = HttpSession.getPostResult(API.API_UPLOAD_AVATER, params, files);
				} catch (Exception ex) {
					result = "error";
					Log.e(TAG, ex.getMessage());
				} finally {
					Message msg = new Message();
					msg.what = MessageContants.MSG_UPLOAD_Q_IMG;
					msg.obj = result;
					handler.sendMessage(msg);
				}

			}
		}).start();
	}

	/**
	 * 服务下订单接口
	 */
	public void toAddOrderByService(final Handler handler, final String tokenid, final String productId) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				Map<String, String> params = new HashMap<String, String>();
				params.put("token_id", tokenid);
				// params.put("salemanId", salemanId);
				params.put("productId", productId);
				String result = "error";
				try {
					result = HttpSession.getRequestResult(API.API_ADD_SERVICE_ORDER, params);
				} catch (Exception ex) {
					result = "error";
					Log.e(TAG, ex.getMessage());
				} finally {
					Message msg = new Message();
					msg.what = MessageContants.MSG_ADD_SERVICE_ORDER;
					msg.obj = result;
					handler.sendMessage(msg);
				}
			}
		}).start();
	}

	/**
	 * 获取用户协议
	 * 
	 * @param handler
	 * @param tokenid
	 */
	public void toGetUserAgreement(final Handler handler, final String tokenid) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				StringBuffer sb = new StringBuffer();
				sb.append(API.API_GET_USER_AGREEMENT);
				sb.append("?");
				sb.append("token_id=");
				sb.append(tokenid);

				String result = HttpManager.httpGet(sb.toString());
				Message msg = new Message();
				msg.what = MessageContants.MSG_GET_USER_AGREEMENT;
				msg.obj = result;
				handler.sendMessage(msg);
			}
		}).start();
	}

	/**
	 * 获取会员卡包
	 * 
	 * @param handler
	 * @param tokenid
	 */
	public void toGetMemeberCardList(final Handler handler, final String tokenid, final String type) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				StringBuffer sb = new StringBuffer();
				sb.append(API.API_GET_MEMBER_CARD_LIST);
				sb.append("?");
				sb.append("token_id=");
				sb.append(tokenid);
				if (type != null) {
					sb.append("&type=");
					sb.append(type);
				}

				String result = HttpManager.httpGet(sb.toString());
				Message msg = new Message();
				msg.what = MessageContants.MSG_GET_MEMBER_CARD_LIST;
				msg.obj = result;
				handler.sendMessage(msg);
			}
		}).start();
	}

	/**
	 * 获取服务订单列表
	 * 
	 * @param handler
	 * @param tokenid
	 */
	public void toGetServiceOrderList(final Handler handler, final String tokenid, final int pageNum,
			final int pageSize) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				StringBuffer sb = new StringBuffer();
				sb.append(API.API_GET_SERVICE_ORDER_LIST);
				sb.append("?");
				sb.append("token_id=");
				sb.append(tokenid);
				if (pageSize > 0) {
					sb.append("&pageSize=");
					sb.append(pageSize);
				}
				if (pageNum > 0) {
					sb.append("&pageNum=");
					sb.append(pageNum);
				}

				String result = HttpManager.httpGet(sb.toString());
				Message msg = new Message();
				msg.what = MessageContants.MSG_GET_SERVICE_ORDER_LIST;
				msg.obj = result;
				handler.sendMessage(msg);
			}
		}).start();
	}

	/**
	 * 获取服务列表
	 * 
	 * @param handler
	 * @param tokenid
	 */
	public void toGetServiceGroupList(final Handler handler, final String tokenid, final int pageNum,
			final int pageSize, final String pname) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				StringBuffer sb = new StringBuffer();
				sb.append(API.API_GET_SERVICE_GROUP_LIST);
				sb.append("?");
				sb.append("token_id=");
				sb.append(tokenid);
				if (pageSize > 0) {
					sb.append("&pageSize=");
					sb.append(pageSize);
				}
				if (pageNum > 0) {
					sb.append("&pageNum=");
					sb.append(pageNum);
				}
				if (pname != null) {
					sb.append("&pname=");
					sb.append(pname);
				}

				String result = HttpManager.httpGet(sb.toString());
				Message msg = new Message();
				msg.what = MessageContants.MSG_GET_SERVICE_GROUP_LIST;
				msg.obj = result;
				handler.sendMessage(msg);
			}
		}).start();
	}

	/**
	 * 获取我的服务列表
	 * 
	 * @param handler
	 * @param tokenid
	 */
	public void toGetMyServiceList(final Handler handler, final String tokenid, final int pageNum, final int pageSize,
			final String pname) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				StringBuffer sb = new StringBuffer();
				sb.append(API.API_GET_MY_SERVICE_LIST);
				sb.append("?");
				sb.append("token_id=");
				sb.append(tokenid);
				if (pageSize > 0) {
					sb.append("&pageSize=");
					sb.append(pageSize);
				}
				if (pageNum > 0) {
					sb.append("&pageNum=");
					sb.append(pageNum);
				}
				if (pname != null) {
					sb.append("&pname=");
					sb.append(pname);
				}

				String result = HttpManager.httpGet(sb.toString());
				Message msg = new Message();
				msg.what = MessageContants.MSG_GET_MY_SERVICE_LIST;
				msg.obj = result;
				handler.sendMessage(msg);
			}
		}).start();
	}

	/**
	 * add我的服务
	 * 
	 * @param handler
	 * @param tokenid
	 */
	public void toAddService(final Handler handler, final String tokenid, final String reserveTime,
			final String serivceID) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				StringBuffer sb = new StringBuffer();
				sb.append(API.API_ADD_SERVICE);
				sb.append("?");
				sb.append("token_id=");
				sb.append(tokenid);
				if (reserveTime != null) {
					sb.append("&reserveTime=");
					sb.append(reserveTime);
				}
				if (serivceID != null) {
					sb.append("&serivceID=");
					sb.append(serivceID);
				}

				String result = HttpManager.httpGet(sb.toString());
				Message msg = new Message();
				msg.what = MessageContants.MSG_ADD_SERVICE;
				msg.obj = result;
				handler.sendMessage(msg);
			}
		}).start();
	}

	/**
	 * delete我的服务
	 * 
	 * @param handler
	 * @param tokenid
	 */
	public void toDeleteService(final Handler handler, final String tokenid, final String serivceID) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				StringBuffer sb = new StringBuffer();
				sb.append(API.API_DELETE_SERVICE);
				sb.append("?");
				sb.append("token_id=");
				sb.append(tokenid);
				if (serivceID != null) {
					sb.append("&id=");
					sb.append(serivceID);
				}

				String result = HttpManager.httpGet(sb.toString());
				Message msg = new Message();
				msg.what = MessageContants.MSG_DELETE_SERVICE;
				msg.obj = result;
				handler.sendMessage(msg);
			}
		}).start();
	}

	/**
	 * 28) 获取团队成员列表
	 * 
	 * @param handler
	 * @param tokenid
	 */
	public void toGetSalemanMemberList(final Handler handler, final String tokenid, final int pageNum,
			final int pageSize) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				StringBuffer sb = new StringBuffer();
				sb.append(API.API_GET_SALEMAN_MEMBER_LIST);
				sb.append("?");
				sb.append("token_id=");
				sb.append(tokenid);
				if (pageNum > 0) {
					sb.append("&pageNum=");
					sb.append(pageNum);
				}
				if (pageSize > 0) {
					sb.append("&pageSize=");
					sb.append(pageSize);
				}

				String result = HttpManager.httpGet(sb.toString());
				Message msg = new Message();
				msg.what = MessageContants.MSG_GET_SALEMAN_MEMBER_LIST;
				msg.obj = result;
				handler.sendMessage(msg);
			}
		}).start();
	}

	/**
	 * 33) 获取会员收货地址列表
	 * 
	 * @param handler
	 * @param tokenid
	 */
	public void toGetMemberAddressList(final Handler handler, final String tokenid, final int pageNum,
			final int pageSize) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				StringBuffer sb = new StringBuffer();
				sb.append(API.API_GET_MEMBER_ADDRESS_LIST);
				sb.append("?");
				sb.append("token_id=");
				sb.append(tokenid);
				if (pageSize > 0) {
					sb.append("&pageSize=");
					sb.append(pageSize);
				}
				if (pageNum > 0) {
					sb.append("&pageNum=");
					sb.append(pageNum);
				}

				String result = HttpManager.httpGet(sb.toString());
				Message msg = new Message();
				msg.what = MessageContants.MSG_GET_MEMBER_ADDRESS_LIST;
				msg.obj = result;
				handler.sendMessage(msg);
			}
		}).start();
	}

	/**
	 * 30) 会员新增收货地址
	 * 
	 * @param handler
	 * @param tokenid
	 */
	public void toAddMemberAddress(final Handler handler, final String tokenid, final String aliasName,
			final String name, final String phone, final String district, final String address,
			final String isDefault) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				Map<String, String> params = new HashMap<String, String>();
				params.put("token_id", tokenid);
				params.put("aliasName", aliasName);
				params.put("name", name);
				params.put("phone", phone);
				params.put("district", district);
				params.put("address", address);
				if (isDefault != null) {
					params.put("isDefault", isDefault);
				}
				String result = "error";
				try {
					result = HttpSession.getRequestResult(API.API_ADD_MEMBER_ADDRESS, params);
				} catch (Exception ex) {
					result = "error";
					Log.e(TAG, ex.getMessage());
				} finally {
					Message msg = new Message();
					msg.what = MessageContants.MSG_ADD_MEMBER_ADDRESS;
					msg.obj = result;
					handler.sendMessage(msg);
				}
			}
		}).start();
	}

	/**
	 * 31) 会员修改收货地址
	 * 
	 * @param handler
	 * @param tokenid
	 */
	public void toUpdateMemberAddress(final Handler handler, final String tokenid, final String id,
			final String aliasName, final String name, final String phone, final String district, final String address,
			final String isDefault) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				Map<String, String> params = new HashMap<String, String>();
				params.put("token_id", tokenid);
				params.put("id", id);
				params.put("aliasName", aliasName);
				params.put("name", name);
				params.put("phone", phone);
				params.put("district", district);
				params.put("address", address);
				if (isDefault != null) {
					params.put("isDefault", isDefault);
				}
				String result = "error";
				try {
					result = HttpSession.getRequestResult(API.API_UPDATE_MEMBER_ADDRESS, params);
				} catch (Exception ex) {
					result = "error";
					Log.e(TAG, ex.getMessage());
				} finally {
					Message msg = new Message();
					msg.what = MessageContants.MSG_UPDATE_MEMBER_ADDRESS;
					msg.obj = result;
					handler.sendMessage(msg);
				}
			}
		}).start();
	}

	/**
	 * 32) 会员删除收货地址
	 * 
	 * @param handler
	 * @param tokenid
	 */
	public void toDeleteMemberAddress(final Handler handler, final String tokenid, final String id) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				StringBuffer sb = new StringBuffer();
				sb.append(API.API_DELETE_MEMBER_ADDRESS);
				sb.append("?");
				sb.append("token_id=");
				sb.append(tokenid);
				sb.append("&id=");
				sb.append(id);

				String result = HttpManager.httpGet(sb.toString());
				Message msg = new Message();
				msg.what = MessageContants.MSG_DELETE_MEMBER_ADDRESS;
				msg.obj = result;
				handler.sendMessage(msg);
			}
		}).start();
	}

	/**
	 * 34) 修改会员收货地址为默认地址
	 * 
	 * @param handler
	 * @param tokenid
	 */
	public void toUpdateMemberDefaultAddress(final Handler handler, final String tokenid, final String id) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				Map<String, String> params = new HashMap<String, String>();
				params.put("token_id", tokenid);
				params.put("id", id);
				String result = "error";
				try {
					result = HttpSession.getRequestResult(API.API_UPDATE_MEMBER_DEFAULT_ADDRESS, params);
				} catch (Exception ex) {
					result = "error";
					Log.e(TAG, ex.getMessage());
				} finally {
					Message msg = new Message();
					msg.what = MessageContants.MSG_UPDATE_MEMBER_DEFAULT_ADDRESS;
					msg.obj = result;
					handler.sendMessage(msg);
				}
			}
		}).start();
	}

	/**
	 * 35) 会员续费界面信息
	 * 
	 * @param handler
	 * @param tokenid
	 */
	public void toGetRenewalInfo(final Handler handler, final String tokenid) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				StringBuffer sb = new StringBuffer();
				sb.append(API.API_GET_RENEWAL_FEE_INFO);
				sb.append("?");
				sb.append("token_id=");
				sb.append(tokenid);

				String result = HttpManager.httpGet(sb.toString());
				Message msg = new Message();
				msg.what = MessageContants.MSG_GET_RENEWAL_FEE_INFO;
				msg.obj = result;
				handler.sendMessage(msg);
			}
		}).start();
	}

	/**
	 * 35) 会员续费下单接口
	 * 
	 * @param handler
	 * @param tokenid
	 */
	public void toBuyMember(final Handler handler, final String tokenid, final String productid) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				Map<String, String> params = new HashMap<String, String>();
				params.put("token_id", tokenid);
				params.put("renewId", productid);
				String result = "error";
				try {
					result = HttpSession.getRequestResult(API.API_RENEWAL_FEE, params);
				} catch (Exception ex) {
					result = "error";
					Log.e(TAG, ex.getMessage());
				} finally {
					Message msg = new Message();
					msg.what = MessageContants.MSG_RENEWAL_FEE;
					msg.obj = result;
					handler.sendMessage(msg);
				}
			}
		}).start();
	}

	/**
	 * 37) 会员续费界面信息记录列表
	 * 
	 * @param handler
	 * @param tokenid
	 */
	public void toGetRenewalList(final Handler handler, final String tokenid) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				StringBuffer sb = new StringBuffer();
				sb.append(API.API_RENEWAL_FEE_LIST);
				sb.append("?");
				sb.append("token_id=");
				sb.append(tokenid);
				sb.append("&pageSize=");
				sb.append("100");
				sb.append("&pageNum=");
				sb.append("1");

				String result = HttpManager.httpGet(sb.toString());
				Message msg = new Message();
				msg.what = MessageContants.MSG_RENEWAL_FEE_LIST;
				msg.obj = result;
				handler.sendMessage(msg);
			}
		}).start();
	}

	/**
	 * 添加签名
	 * 
	 * @param handler
	 * @param fileName
	 */
	public void toUploadSign(final Handler handler, final String tokenId, final String fileName) {
		LogUtils.d("fileNameRRRR: " + fileName);
		new Thread(new Runnable() {
			@Override
			public void run() {
				Map<String, String> params = new HashMap<String, String>();
				params.put("token_id", tokenId);

				Map<String, File> files = new HashMap<String, File>();
				if (fileName != null && fileName.length() > 0) {
					files.put("fileName", new File(fileName));
				}
				String result = "error";
				try {
					result = HttpSession.getPostResult(API.API_UPLOAD_AVATER, params, files);
				} catch (Exception ex) {
					result = "error";
					Log.e(TAG, ex.getMessage());
				} finally {
					Message msg = new Message();
					msg.what = MessageContants.MSG_UPLOAD_SIGN;
					msg.obj = result;
					handler.sendMessage(msg);
				}

			}
		}).start();
	}

	/**
	 * 更新用户信息接口
	 */
	public void toUpdatePersonSignInfo(final Handler handler, final String tokenid, final String salemanId,
			final String id, final String signature) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				Map<String, String> params = new HashMap<String, String>();
				params.put("token_id", tokenid);
				if (salemanId != null) {
					params.put("salemanId", salemanId);
				}
				params.put("id", id);
				if (signature != null) {
					params.put("signature", signature);
				}
				String result = "error";
				try {
					result = HttpSession.getRequestResult(API.API_UPDATE_PERSON_INFO, params);
				} catch (Exception ex) {
					result = "error";
					Log.e(TAG, ex.getMessage());
				} finally {
					Message msg = new Message();
					msg.what = MessageContants.MSG_UPDATE_PERSON_INFO;
					msg.obj = result;
					handler.sendMessage(msg);
				}
			}
		}).start();
	}

	/**
	 * 39) 服务订单获取抵扣信息
	 * 
	 * @param handler
	 * @param tokenid
	 */
	public void toGetCardPayInfo(final Handler handler, final String tokenid, final String orderId,
			final boolean isService) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				StringBuffer sb = new StringBuffer();
				if (isService) {
					sb.append(API.API_GET_SERVICE_CARD_PAY_INFO);
				} else {
					sb.append(API.API_GET_CARD_PAY_INFO);
				}
				sb.append("?");
				sb.append("token_id=");
				sb.append(tokenid);
				sb.append("&orderId=");
				sb.append(orderId);

				String result = HttpManager.httpGet(sb.toString());
				Message msg = new Message();
				msg.what = MessageContants.MSG_GET_CARD_PAY_INFO;
				msg.obj = result;
				handler.sendMessage(msg);
			}
		}).start();
	}

	/**
	 * 40) 通知列表信息
	 * 
	 * @param handler
	 * @param tokenid
	 */
	public void toGetRuleNoticeList(final Handler handler, final String tokenid, final String status,
			final String remindTime) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				StringBuffer sb = new StringBuffer();
				sb.append(API.API_GET_RULE_NOTICE_LIST);
				sb.append("?");
				sb.append("token_id=");
				sb.append(tokenid);
				if (status != null) {
					sb.append("&status=");
					sb.append(status);
				}
				if (remindTime != null) {
					sb.append("&remindTime=");
					sb.append(remindTime);
				}

				String result = HttpManager.httpGet(sb.toString());
				Message msg = new Message();
				msg.what = MessageContants.MSG_GET_RULE_NOTICE_LIST;
				msg.obj = result;
				handler.sendMessage(msg);
			}
		}).start();
	}

	/**
	 * 41) 处理通知
	 * 
	 * @param handler
	 * @param tokenid
	 */
	public void toDealRuleNotice(final Handler handler, final String tokenid, final String status,
			final String notice_id) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				StringBuffer sb = new StringBuffer();
				sb.append(API.API_DEAL_RULE_NOTICE);
				sb.append("?");
				sb.append("token_id=");
				sb.append(tokenid);
				if (status != null) {
					sb.append("&status=");
					sb.append(status);
				}
				if (notice_id != null) {
					sb.append("&notice_id=");
					sb.append(notice_id);
				}

				String result = HttpManager.httpGet(sb.toString());
				Message msg = new Message();
				msg.what = MessageContants.MSG_DEAL_RULE_NOTICE;
				msg.obj = result;
				handler.sendMessage(msg);
			}
		}).start();
	}

	/**
	 * 确认支付接口
	 */
	public void toConfirmPayOrder(final Handler handler, final String tokenid, final String orderId,
			final String cashCardMoney, final String serviceCardMoney) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				StringBuffer sb = new StringBuffer();
				sb.append(API.API_CONFIRM_PAY_ORDER);
				sb.append("?");
				sb.append("token_id=");
				sb.append(tokenid);
				sb.append("&orderId=");
				sb.append(orderId);
				sb.append("&cashCardMoney=");
				sb.append(cashCardMoney);
				if (serviceCardMoney != null) {
					sb.append("&serviceCardMoney=");
					sb.append(serviceCardMoney);
				}

				String result = HttpManager.httpGet(sb.toString());
				Message msg = new Message();
				msg.what = MessageContants.MSG_CONFIRM_PAY_ORDER;
				msg.obj = result;
				handler.sendMessage(msg);
			}
		}).start();
	}

	/**
	 * 43) 添加成员
	 */
	public void toAddSaleManInfo(final Handler handler, final String tokenid, final String name, final String avatar,
			final String gender, final String birthday, final String maritalStatus, final String district,
			final String address) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				Map<String, String> params = new HashMap<String, String>();
				params.put("token_id", tokenid);
				params.put("name", name);
				if (avatar != null) {
					params.put("avatar", avatar);
				}
				if (gender != null)
					params.put("gender", gender);
				if (birthday != null) {
					params.put("birthday", birthday);
				}
				if (maritalStatus != null) {
					params.put("maritalStatus", maritalStatus);
				}
				if (district != null) {
					params.put("district", district);
				}
				if (address != null) {
					params.put("address", address);
				}
				String result = "error";
				try {
					result = HttpSession.getRequestResult(API.API_ADD_SALEMAN_MEMBER, params);
				} catch (Exception ex) {
					result = "error";
					Log.e(TAG, ex.getMessage());
				} finally {
					Message msg = new Message();
					msg.what = MessageContants.MSG_ADD_SALEMAN_MEMBER;
					msg.obj = result;
					handler.sendMessage(msg);
				}
			}
		}).start();
	}

	/**
	 * 更新成员
	 */
	public void toUpdateSaleManInfo(final Handler handler, final String tokenid, final String id, final String name,
			final String avatar, final String gender, final String birthday, final String maritalStatus,
			final String district, final String address) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				Map<String, String> params = new HashMap<String, String>();
				params.put("token_id", tokenid);
				params.put("id", id);
				params.put("name", name);
				if (avatar != null) {
					params.put("avatar", avatar);
				}
				if (gender != null)
					params.put("gender", gender);
				if (birthday != null) {
					params.put("birthday", birthday);
				}
				if (maritalStatus != null) {
					params.put("maritalStatus", maritalStatus);
				}
				if (district != null) {
					params.put("district", district);
				}
				if (address != null) {
					params.put("address", address);
				}
				String result = "error";
				try {
					result = HttpSession.getRequestResult(API.API_UPDATE_SALEMAN_MEMBER, params);
				} catch (Exception ex) {
					result = "error";
					Log.e(TAG, ex.getMessage());
				} finally {
					Message msg = new Message();
					msg.what = MessageContants.MSG_UPDATE_SALEMAN_MEMBER;
					msg.obj = result;
					handler.sendMessage(msg);
				}
			}
		}).start();
	}

	/**
	 * 44) 删除成员
	 * 
	 * @param handler
	 * @param tokenid
	 */
	public void toDeleteSaleManMember(final Handler handler, final String tokenid, final String saleManId) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				StringBuffer sb = new StringBuffer();
				sb.append(API.API_DELETE_SALEMAN_MEMBER);
				sb.append("?");
				sb.append("token_id=");
				sb.append(tokenid);
				sb.append("&saleManId=");
				sb.append(saleManId);

				String result = HttpManager.httpGet(sb.toString());
				Message msg = new Message();
				msg.what = MessageContants.MSG_DELETE_SALEMAN_MEMBER;
				msg.obj = result;
				handler.sendMessage(msg);
			}
		}).start();
	}

	/**
	 * 添加签名底图
	 * 
	 * @param handler
	 * @param fileName
	 */
	public void toUploadSignBaseline(final Handler handler, final String tokenId, final String fileName) {
		LogUtils.d("fileNameRRRR: " + fileName);
		new Thread(new Runnable() {
			@Override
			public void run() {
				Map<String, String> params = new HashMap<String, String>();
				params.put("token_id", tokenId);

				Map<String, File> files = new HashMap<String, File>();
				if (fileName != null && fileName.length() > 0) {
					files.put("file", new File(fileName));
				}
				String result = "error";
				try {
					result = HttpSession.getPostResult(API.API_UPLOAD_MEMBER_FILE, params, files);
				} catch (Exception ex) {
					result = "error";
					Log.e(TAG, ex.getMessage());
				} finally {
					Message msg = new Message();
					msg.what = MessageContants.MSG_UPLOAD_SIGN_BASELINE;
					msg.obj = result;
					handler.sendMessage(msg);
				}

			}
		}).start();
	}

	/**
	 *
	 */
	public void toUpdateQBaseMap(final Handler handler, final String tokenid, final String signatureBaseMap) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				Map<String, String> params = new HashMap<String, String>();
				params.put("token_id", tokenid);
				params.put("signatureBaseMap", signatureBaseMap);
				String result = "error";
				try {
					result = HttpSession.getRequestResult(API.API_UPLOAD_Q_BASE_URL, params);
				} catch (Exception ex) {
					result = "error";
					Log.e(TAG, ex.getMessage());
				} finally {
					Message msg = new Message();
					msg.what = MessageContants.MSG_UPLOAD_Q_BASE_URL;
					msg.obj = result;
					handler.sendMessage(msg);
				}
			}
		}).start();
	}
	
	/**
	 * 添加Q底图
	 * 
	 * @param handler
	 * @param fileName
	 */
	public void toUploadQBaseline(final Handler handler, final String tokenId, final String fileName) {
		LogUtils.d("fileNameRRRR: " + fileName);
		new Thread(new Runnable() {
			@Override
			public void run() {
				Map<String, String> params = new HashMap<String, String>();
				params.put("token_id", tokenId);

				Map<String, File> files = new HashMap<String, File>();
				if (fileName != null && fileName.length() > 0) {
					files.put("file", new File(fileName));
				}
				String result = "error";
				try {
					result = HttpSession.getPostResult(API.API_UPLOAD_MEMBER_FILE, params, files);
				} catch (Exception ex) {
					result = "error";
					Log.e(TAG, ex.getMessage());
				} finally {
					Message msg = new Message();
					msg.what = MessageContants.MSG_UPLOAD_Q_BASELINE;
					msg.obj = result;
					handler.sendMessage(msg);
				}

			}
		}).start();
	}
	
	public void toUpdateQgraphBaseMap(final Handler handler, final String tokenid, final String qgraphBaseMap) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				Map<String, String> params = new HashMap<String, String>();
				params.put("token_id", tokenid);
				params.put("qgraphBaseMap", qgraphBaseMap);
				String result = "error";
				try {
					result = HttpSession.getRequestResult(API.API_UPLOAD_Qgraph_BASE_URL, params);
				} catch (Exception ex) {
					result = "error";
					Log.e(TAG, ex.getMessage());
				} finally {
					Message msg = new Message();
					msg.what = MessageContants.MSG_UPLOAD_Qgraph_BASE_URL;
					msg.obj = result;
					handler.sendMessage(msg);
				}
			}
		}).start();
	}
	
	/**
	 * 添加名片
	 * 
	 * @param handler
	 * @param fileName
	 */
	public void toUploadBusinessCard(final Handler handler, final String tokenId, final String fileName) {
		LogUtils.d("fileNameRRRR: " + fileName);
		new Thread(new Runnable() {
			@Override
			public void run() {
				Map<String, String> params = new HashMap<String, String>();
				params.put("token_id", tokenId);

				Map<String, File> files = new HashMap<String, File>();
				if (fileName != null && fileName.length() > 0) {
					files.put("file", new File(fileName));
				}
				String result = "error";
				try {
					result = HttpSession.getPostResult(API.API_UPLOAD_MEMBER_FILE, params, files);
				} catch (Exception ex) {
					result = "error";
					Log.e(TAG, ex.getMessage());
				} finally {
					Message msg = new Message();
					msg.what = MessageContants.MSG_UPLOAD_BUSINESS_CARD;
					msg.obj = result;
					handler.sendMessage(msg);
				}

			}
		}).start();
	}
	
	public void toUpdateBusinessCard(final Handler handler, final String tokenid, final String qgraphBaseMap) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				Map<String, String> params = new HashMap<String, String>();
				params.put("token_id", tokenid);
				params.put("businesscard", qgraphBaseMap);
				String result = "error";
				try {
					result = HttpSession.getRequestResult(API.API_UPLOAD_BUSINESS_CARD_URL, params);
				} catch (Exception ex) {
					result = "error";
					Log.e(TAG, ex.getMessage());
				} finally {
					Message msg = new Message();
					msg.what = MessageContants.MSG_UPLOAD_BUSINESS_CARD_URL;
					msg.obj = result;
					handler.sendMessage(msg);
				}
			}
		}).start();
	}


	/**
	 * 获取快递信息
	 * 
	 * @param handler
	 * @param tokenid
	 */
	public void toGetExpressInfo(final Handler handler, final String tokenid, final String number) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				StringBuffer sb = new StringBuffer();
				sb.append(API.API_GET_EXPRESS_INFO);
				sb.append("?");
				sb.append("token_id=");
				sb.append(tokenid);
				sb.append("&number=");
				sb.append(number);

				String result = HttpManager.httpGet(sb.toString());
				Message msg = new Message();
				msg.what = MessageContants.MSG_GET_EXPRESS_INFO;
				msg.obj = result;
				handler.sendMessage(msg);
			}
		}).start();
	}
	
	/**
	 * 获取客户列表
	 * 
	 * @param handler
	 * @param tokenid
	 * @param saleId
	 * @param cname
	 * @param cphone
	 * @param pageNum
	 */
	public void toGetChooseCustomerList(final Handler handler, final String tokenid, final String saleId, final String cname,
			final String cphone, final int pageNum, final String ruleId) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				StringBuffer sb = new StringBuffer();
				sb.append(API.API_GET_CUSTOMER_LIST);
				sb.append("?");
				sb.append("token_id=");
				sb.append(tokenid);
				sb.append("&saleId=");
				sb.append(saleId);
				if (cname != null) {
					sb.append("&cname=");
					sb.append(cname);
				}
				if (cphone != null) {
					sb.append("&cphone=");
					sb.append(cphone);
				}
				if (pageNum > 0) {
					sb.append("&pageNum=");
					sb.append(pageNum);
				}
				sb.append("&pageSize=");
				sb.append("0");
				sb.append("&ruleId=");
				sb.append(ruleId);

				String result = HttpManager.httpGet(sb.toString());
				Message msg = new Message();
				msg.what = MessageContants.MSG_GET_CHOOSE_CUSTOMER_LIST;
				msg.obj = result;
				handler.sendMessage(msg);
			}
		}).start();
	}
}
