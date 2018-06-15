package com.lyp.membersystem.net;

public class API {
    public static  final String ROOT_WEB = "http://115.239.252.186:8086";
//    public static  final String ROOT_WEB = "https://forvips.cn";
    /*login interface*/
    public static final String API_LOGIN = ROOT_WEB + "/api/app/v1/appLogin";
    
    /*get sms auth code interface*/
    public static final String API_GET_SMS_AUTH_CODE = ROOT_WEB + "/api/app/v1/getSmsAuthcode";
    
    /*get customer info list interface*/
    public static final String API_GET_CUSTOMER_LIST = ROOT_WEB + "/api/app/v1/getCustomerList";
    
    /*get add customer interface*/
    public static final String API_ADD_CUSTOMER = ROOT_WEB + "/api/app/v1/addCustomer";
    
    /*get update customer interface*/
    public static final String API_UPDATE_CUSTOMER = ROOT_WEB + "/api/app/v1/updateCustomer";
    
    /*get delete customer interface*/
    public static final String API_DELETE_CUSTOMER = ROOT_WEB + "/api/app/v1/delCustomer";
    
    /*get Product Type*/
    public static  final String API_PRODUCT_TYPE = ROOT_WEB + "/api/app/v1/getProductTypeTree";
    
    /*get Product List*/
    public static  final String API_PRODUCT_LIST = ROOT_WEB + "/api/app/v1/getProductList";
    
    /*get person info interface*/
    public static final String API_GET_PERSON_INFO = ROOT_WEB + "/api/app/v1/getPersonaInfo";
    
    /*update person info interface*/
    public static final String API_UPDATE_PERSON_INFO = ROOT_WEB + "/api/app/v1/updatePsersonInfo";
    
    /*get notice list infor interface   old: api/app/v1/getNoticeList*/
    public static final String API_GET_NOTICE_LIST = ROOT_WEB + "/api/app/v1/getSystemRuleList";
    
    /*change notice state infor interface*/
    public static final String API_CHANGE_NOTICE_STATE = ROOT_WEB + "/api/app/v1/createFsOrderCharge";
    
    /*change notice state infor interface*/
    public static final String API_DELETE_NOTICE = ROOT_WEB + "/api/app/v1/delNotice";
    
    /*get card ENVELOP infor interface*/
    public static final String API_GET_CARD_LIST = ROOT_WEB + "/api/app/v1/getCardEnvelopList";
    
    /*get package infor interface*/
    public static final String API_GET_PACKAGE_LIST = ROOT_WEB + "/api/app/v1/getPackList";
    
    /*add order infor interface*/
    public static final String API_ADD_ORDER = ROOT_WEB + "/api/app/v1/addOrder";

//    /*get order list infor interface*/old
//    public static final String API_GET_ORDER_LIST = ROOT_WEB + "/api/app/v1/getOrderList";

    /*get order list infor interface*/
    public static final String API_GET_ORDER_LIST = ROOT_WEB + "/api/app/v1/getMemberOrderList";
    
    /*get order detail infor interface*/
    public static final String API_GET_ORDER_DETAIL = ROOT_WEB + "/api/app/v1/getOrderDetail";
    
    /*get Good Info*/
    public static  final String API_Good_INFO = ROOT_WEB + "/api/app/v1/getProductById";
    
    /*add order infor interface*/
    public static final String API_ADD_RULE_ORDER = ROOT_WEB + "/api/app/v1/addRuleOrder";
    
    /*pay order infor interface*/
    public static final String API_PAY_ORDER = ROOT_WEB + "/api/app/v1/orderPayment";
    
    /*upload avater interface*/
    public static final String API_UPLOAD_AVATER = ROOT_WEB + "/api/app/v1/uploadAvatar";
    
    /*get current agent interface*/
    public static final String API_GET_CURRENT_AGNET = ROOT_WEB + "/api/app/v1/getCurrentAgent";
    
    /*get rule list infor interface */
    public static final String API_GET_RULE_LIST = ROOT_WEB + "/api/app/v1/getPersonRuleList";
    
    /*add person rule interface*/
    public static final String API_ADD_PERSON_RULE = ROOT_WEB + "/api/app/v1/addRule";
    
    /*update person rule interface*/
    public static final String API_UPDATE_PERSON_RULE = ROOT_WEB + "/api/app/v1/updateRule";
    
    /*delete person rule interface*/
    public static final String API_DELETE_PERSON_RULE = ROOT_WEB + "/api/app/v1/deleteRule";
    
    /*delete person rule interface*/
    public static final String API_GET_PRODUCT_INTRODUCE = ROOT_WEB + "/api/app/v1/getProductIntroduceInfoById";
    
    /*add order infor interface*/
    public static final String API_ADD_SERVICE_ORDER = ROOT_WEB + "/api/app/v1/addServiceOrder";
    
    /*get User Agreement infor interface*/
    public static final String API_GET_USER_AGREEMENT = ROOT_WEB + "/api/app/v1/getUserAgreement";
    
    /*get Card package infor interface*/
    public static final String API_GET_MEMBER_CARD_LIST = ROOT_WEB + "/api/app/v1/getMemeberCardList";
    
    /*get service order infor interface*/
    public static final String API_GET_SERVICE_ORDER_LIST = ROOT_WEB + "/api/app/v1/getServiceOrderList";
    
    /*get service group infor interface*/
    public static final String API_GET_SERVICE_GROUP_LIST = ROOT_WEB + "/api/app/v1/getgroupserviceList";
    
    /*get my service infor interface*/
    public static final String API_GET_MY_SERVICE_LIST = ROOT_WEB + "/api/app/v1/getmyReserveList";
    
    /*get add my service infor interface*/
    public static final String API_ADD_SERVICE = ROOT_WEB + "/api/app/v1/addReserve";
    
    /*get delete my service infor interface*/
    public static final String API_DELETE_SERVICE = ROOT_WEB + "/api/app/v1/delReserve";
    
    /*get get saleman member list infor interface*/
    public static final String API_GET_SALEMAN_MEMBER_LIST = ROOT_WEB + "/api/app/v1/getSalemanMemberList";
    
    /*get get member address list infor interface*/
    public static final String API_GET_MEMBER_ADDRESS_LIST = ROOT_WEB + "/api/app/v1/getMemberAddressList";
    
    /*get add member address infor interface*/
    public static final String API_ADD_MEMBER_ADDRESS = ROOT_WEB + "/api/app/v1/addMemberAddress";
    
    /*get update member default address infor interface*/
    public static final String API_UPDATE_MEMBER_DEFAULT_ADDRESS = ROOT_WEB + "/api/app/v1/updateMemberAddressIsDefault";
    
    /*get update member address infor interface*/
    public static final String API_UPDATE_MEMBER_ADDRESS = ROOT_WEB + "/api/app/v1/updateMemberAddress";
    
    /*get delete member address infor interface*/
    public static final String API_DELETE_MEMBER_ADDRESS = ROOT_WEB + "/api/app/v1/delMemberAddress";
    
    /*ping++ pay order infor interface*/
    public static final String API_PING_PAY_ORDER_RENEWAL_FEE = ROOT_WEB + "/api/app/v1/renewalFeeCharge";

    /*ping++ pay order infor interface*/
    public static final String API_PING_PAY_ORDER_STUDY = ROOT_WEB + "/api/app/v1/renewalFeeCharge";
    
    public static final String API_GET_RENEWAL_FEE_INFO = ROOT_WEB + "/api/app/v1/renewalFeeInfo";
    public static final String API_RENEWAL_FEE = ROOT_WEB + "/api/app/v1/addRenewalFeeOrder";
    public static final String API_RENEWAL_FEE_LIST = ROOT_WEB + "/api/app/v1/getRenewalFeeList";
    
    public static final String API_GET_SERVICE_CARD_PAY_INFO = ROOT_WEB + "/api/app/v1/planPayServiceOrder";
    public static final String API_GET_CARD_PAY_INFO = ROOT_WEB + "/api/app/v1/planPayOrder";

    public static final String API_GET_RULE_NOTICE_LIST = ROOT_WEB + "/api/app/v1/RuleNoticeList";
    
    public static final String API_DEAL_RULE_NOTICE = ROOT_WEB + "/api/app/v1/DealRuleNotice";
    
    public static final String API_CONFIRM_PAY_ORDER = ROOT_WEB + "/api/app/v1/confirmCashCardPayOrder";
    
    /*ping++ pay order infor interface*/
    public static final String API_PING_PAY_ORDER = ROOT_WEB + "/api/app/v1/orderCharge";
    
    /*ping++ pay order infor interface*/
    public static final String API_PING_PAY_ORDER_SERVICE = ROOT_WEB + "/api/app/v1/serviceOrderCharge";
    
    public static final String API_ADD_SALEMAN_MEMBER = ROOT_WEB + "/api/app/v1/addSalemanMember";
    public static final String API_UPDATE_SALEMAN_MEMBER = ROOT_WEB + "/api/app/v1/updateSalemanMember";
    public static final String API_DELETE_SALEMAN_MEMBER = ROOT_WEB + "/api/app/v1/delSalemanMember";
    
    public static final String API_GET_SERVICE_INTRODUCE = ROOT_WEB + "/api/app/v1/getGroupServiceIntroduceInfoById";
    
    public static final String API_UPLOAD_MEMBER_FILE = ROOT_WEB + "/api/app/v1/uploadMemberFile";
    
    public static final String API_UPLOAD_Q_BASE_URL = ROOT_WEB + "/api/app/v1/updateSignatureBaseMap";
    
    public static final String API_UPLOAD_Qgraph_BASE_URL = ROOT_WEB + "/api/app/v1/updateQgraphBaseMap";
    
    
    public static final String API_UPLOAD_BUSINESS_CARD_URL = ROOT_WEB + "/api/app/v1/updateBusinesscard";
    
    public static final String API_GET_EXPRESS_INFO = ROOT_WEB + "/api/app/v1/expressquery";
}
