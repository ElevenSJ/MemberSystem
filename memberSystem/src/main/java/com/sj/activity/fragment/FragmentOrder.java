package com.sj.activity.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lyp.membersystem.R;
import com.lyp.membersystem.adapter.OrderAdapter;
import com.lyp.membersystem.bean.GoodBean;
import com.lyp.membersystem.bean.OrderBean;
import com.lyp.membersystem.log.LogUtils;
import com.lyp.membersystem.net.Errors;
import com.lyp.membersystem.net.MessageContants;
import com.lyp.membersystem.net.NetProxyManager;
import com.lyp.membersystem.utils.Constant;
import com.lyp.membersystem.utils.DateUtil;
import com.lyp.membersystem.utils.DisplayUtil;
import com.lyp.membersystem.utils.ToastUtil;
import com.lyp.membersystem.view.CustomPopupWindow;
import com.lyp.membersystem.view.contactsort.ContactSortModel;
import com.lyp.membersystem.view.dialog.WaitDialog;
import com.sj.activity.base.FragmentBase;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 创建时间: on 2018/5/4.
 * 创建人: 孙杰
 * 功能描述:
 */
public class FragmentOrder extends FragmentBase implements PullToRefreshBase.OnRefreshListener2<ListView> {

    PullToRefreshListView mPullRefreshListView;
    private ListView lv_order;
    private WaitDialog mWaitDialog;
    private CustomPopupWindow mPopWin;
    private SharedPreferences mSharedPreferences;
    private List<OrderBean> orderList;
    private OrderAdapter mOrderAdapter;
    private int mPage = 1;
    private int mRow = 10;
    private int mTotal;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MessageContants.MSG_GET_ORDER_LIST: {
                    parseOrderList((String) msg.obj);
                    break;
                }
                default:
                    break;
            }
        }

        ;
    };

    public static FragmentOrder newInstance() {
        return new FragmentOrder();
    }


    public FragmentOrder() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initData();
    }

    private void initView() {
        mPullRefreshListView = getHoldingActivity().findViewById(R.id.pull_refresh_listview);
        lv_order = mPullRefreshListView.getRefreshableView();
        lv_order.setDividerHeight(new DisplayUtil(getHoldingActivity()).dipToPx(1));
        lv_order.setSelector(android.R.color.transparent);
        mPullRefreshListView.setMode(PullToRefreshBase.Mode.DISABLED);
        mPullRefreshListView.setOnRefreshListener(this);
        orderList = new ArrayList<OrderBean>();
        mOrderAdapter = new OrderAdapter(orderList, this.getContext());
        lv_order.setAdapter(mOrderAdapter);
        lv_order.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long arg3) {
                // Intent intent = new Intent();
                // intent.setClass(OrderActivity.getHoldingActivity(),
                // OrderDetailActivity.class);
                // // intent.putExtra("id", contactSortModel.getId());
                // startActivity(intent);
            }
        });
    }

    private void initData() {
//        if (mWaitDialog == null) {
//            mWaitDialog = new WaitDialog(getHoldingActivity(), R.string.loading_data);
//        }
//        mWaitDialog.show();
        getData();
    }

    private void getData() {
        mSharedPreferences = getHoldingActivity().getSharedPreferences(Constant.SHARED_PREFERENCE, getHoldingActivity().MODE_PRIVATE);
        String tokenid = mSharedPreferences.getString(Constant.TOKEN_ID, "");
        String saleId = mSharedPreferences.getString(Constant.ID, "");
        NetProxyManager.getInstance().toGetOrderList(mHandler, tokenid, saleId, mPage + (orderList.size() / mRow),
                mRow);
    }

    private void parseOrderList(String result) {
        if (Errors.ERROR_NET.equals(result) || Errors.ERROR_SERVER.equals(result)) {
            ToastUtil.showLong(getHoldingActivity(), R.string.network_error);
            if (mWaitDialog != null && mWaitDialog.isShowing()) {
                mWaitDialog.dismiss();
            }
            return;
        }
        // to parser json data
        try {
            JSONObject json = new JSONObject(result);
            boolean success = json.getBoolean("success");
            LogUtils.d("-----" + json);
            if (!success) {
                String message = json.getString("message");
                ToastUtil.showShort(getHoldingActivity(), message);
                if (json.getString("resCode").equals(Constant.RELOGIN)) {
                   getHoldingActivity().backLogin();
                }
                return;
            }
            JSONObject job = json.getJSONObject("object");
            JSONArray jsonArray = job.getJSONArray("infoList");
            mTotal = job.getInt("totalCount");
            // orderList.clear();
            for (int i = 0; i < jsonArray.length(); i++) {
                double orderPrice = 0d;
                JSONObject jb = jsonArray.getJSONObject(i);
                OrderBean orderBean = new OrderBean();
                orderBean.setOrderId(jb.getString("orderId"));
                String receiveType = "";
                if (jb.has("receiveType")) {
                    receiveType = jb.getString("receiveType");
                    orderBean.setReceiveType(receiveType);
                }
                if (receiveType.equals("2")) {
                    orderBean.setLogisticsno(jb.getString("logisticsNo"));
                }
                orderBean.setOrderDate(DateUtil.stringToStr(jb.getString("orderTime")));
                orderPrice = jb.getDouble("cardPrice");
                // orderPrice = orderPrice + jb.getDouble("packPrice");

                JSONArray jsonArray3 = jb.getJSONArray("customerList");
                List<ContactSortModel> contactList = new ArrayList<ContactSortModel>();
                List<GoodBean> goodlist = new ArrayList<GoodBean>();
                List<String> goodIds = new ArrayList<String>();
                for (int j = 0; j < jsonArray3.length(); j++) {
                    JSONObject contactJB = jsonArray3.getJSONObject(j);
                    ContactSortModel contactSortModel = new ContactSortModel();
                    if (contactJB.has("avatar") && !contactJB.isNull("avatar")) {
                        contactSortModel.setAvater(contactJB.getString("avatar"));
                    }
                    if (contactJB.has("customerAvatar") && !contactJB.isNull("customerAvatar")) {
                        contactSortModel.setAvater(contactJB.getString("customerAvatar"));
                    }
                    // contactSortModel.setId(contactJB.getString("customerId"));
                    contactList.add(contactSortModel);

                    if (receiveType.equals("1")) {
                        orderBean.setLogisticsno(contactJB.getString("logisticsNo"));
                    }

                    JSONArray jsonArray2 = contactJB.getJSONArray("productList");
                    for (int k = 0; k < jsonArray2.length(); k++) {
                        JSONObject goodJB = jsonArray2.getJSONObject(k);
                        String pId = goodJB.getString("id");
                        if (goodIds.contains(pId)) {
                            continue;
                        }
                        goodIds.add(pId);
                        GoodBean goodBean = new GoodBean();
                        goodBean.setId(pId);
                        String goodPrice = goodJB.getString("price");
                        orderPrice = orderPrice + Double.valueOf(goodPrice);
                        goodBean.setPiprice(goodPrice);
                        if (goodJB.has("smallPicUrl")) {
                            String picUrls = goodJB.getString("smallPicUrl");
                            if (picUrls.contains(",")) {
                                goodBean.setPicUrls(picUrls.split(",")[0]);
                            } else if (picUrls.trim().length() > 2) {
                                goodBean.setPicUrls(picUrls);
                            }
                        }
                        if (goodJB.has("isService") && !goodJB.isNull("isService")) {
                            goodBean.setIsService(goodJB.getString("isService"));
                        }
                        goodBean.setPname(goodJB.getString("name"));
                        goodlist.add(goodBean);
                    }
                }
                orderBean.setGoodlist(goodlist);
                orderPrice = orderPrice * jsonArray3.length();
                orderBean.setCustomerList(contactList);
                orderBean.setOrderPrice(jb.getDouble("totalPrice"));
                if (jb.has("orderStatus"))
                    orderBean.setOrderState(jb.getString("orderStatus"));
                orderList.add(orderBean);
            }
            mPullRefreshListView.onRefreshComplete();
            mOrderAdapter.notifyDataSetChanged();
            if (mTotal <= 0) {
                ToastUtil.showShort(getHoldingActivity(), R.string.not_data);
            } else if (mTotal > mRow) {
                mPullRefreshListView.setMode(PullToRefreshBase.Mode.PULL_UP_TO_REFRESH);
            }
            if (mWaitDialog != null && mWaitDialog.isShowing()) {
                mWaitDialog.dismiss();
            }
        } catch (Exception ex) {
            LogUtils.e(ex.getMessage());
            if (mWaitDialog != null && mWaitDialog.isShowing()) {
                mWaitDialog.dismiss();
            }
            return;
        }
    }


    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        mHandler.postDelayed(mRefresCompleteRunnable, 1000);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        if (mTotal == orderList.size()) {
            ToastUtil.showShort(getHoldingActivity(), R.string.no_more_data_loading);
            mHandler.postDelayed(mRefresCompleteRunnable, 1000);
        } else {
            getData();
        }
    }

    private Runnable mRefresCompleteRunnable = new Runnable() {

        @Override
        public void run() {
            mPullRefreshListView.onRefreshComplete();
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == getHoldingActivity().RESULT_OK && requestCode == 0x1314) {
            getData();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
