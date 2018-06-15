package com.lyp.membersystem.ui;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.lyp.membersystem.R;
import com.lyp.membersystem.adapter.OrderDetailAdapter;
import com.lyp.membersystem.base.BaseActivity;
import com.lyp.membersystem.bean.GoodBean;
import com.lyp.membersystem.bean.OrderDetailBean;
import com.lyp.membersystem.log.LogUtils;
import com.lyp.membersystem.manager.ImageManager;
import com.lyp.membersystem.net.Errors;
import com.lyp.membersystem.net.MessageContants;
import com.lyp.membersystem.net.NetProxyManager;
import com.lyp.membersystem.pay.PayActivity;
import com.lyp.membersystem.utils.Constant;
import com.lyp.membersystem.utils.DateUtil;
import com.lyp.membersystem.utils.SystemStatusManager;
import com.lyp.membersystem.utils.ToastUtil;
import com.lyp.membersystem.view.contactsort.ContactSortModel;
import com.lyp.membersystem.view.dialog.WaitDialog;
import com.sj.activity.bean.OrderBean;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class OrderDetailActivity extends BaseActivity {

    private WaitDialog mWaitDialog;
    private TextView order_no;
    private TextView order_date;
    private TextView order_state;
    private ListView lv_order_detail;
    private TextView pay_money_tv;
    private TextView num;
    private TextView pay_btn;
    private ImageView card_iv;
    private ImageView package_iv;
    private SharedPreferences mSharedPreferences;
    private List<OrderDetailBean> orderDetailList;
    private OrderDetailAdapter mOrderDetailAdapter;
    private String order_id;
    private OrderBean orderBean;

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MessageContants.MSG_GET_ORDER_DETAIL: {
                    parseOrderList((String) msg.obj);
                    break;
                }
                default:
                    break;
            }
        }

        ;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTranslucentStatus();
        setContentView(R.layout.order_detail_layout);
        orderBean = (OrderBean) getIntent().getSerializableExtra("data");
        order_id = orderBean.getOrderCode();
        initView();
        if (orderBean.getBigOrderType() == 1) {
            initData();
        } else {
            order_no.setText("No." + order_id);
            order_date.setText(DateUtil.stringToStr(orderBean.getOrderTime()));
            ((TextView)findViewById(R.id.txt_card_name)).setText(orderBean.getProductName());
            num.setVisibility(View.GONE);
            if (orderBean.getPreviewImgUrl() != null && !orderBean.getPreviewImgUrl().isEmpty()) {
                ImageManager.loadImage(orderBean.getPreviewImgUrl().get(0), card_iv);
            }
            pay_money_tv.setText("实付款：￥" + orderBean.getTotalPrice());
            String orderStatus = orderBean.getOrderStatus();
            if (orderStatus.equals("0")) {
                order_state.setText("未支付");
//					order_state.setText("已取消");
                pay_btn.setVisibility(View.GONE);
            } else if (orderStatus.equals("1")) {
                order_state.setText("已付款");
                pay_btn.setVisibility(View.GONE);
            } else if (orderStatus.equals("2")) {
                order_state.setText("已发货");
                pay_btn.setVisibility(View.GONE);
            } else if (orderStatus.equals("3")) {
                order_state.setText("已取消");
                pay_btn.setVisibility(View.GONE);
            } else if (orderStatus.equals("100")) {
                order_state.setText("支付中");
                pay_btn.setVisibility(View.GONE);
            } else {
                order_state.setText("已取消");
                pay_btn.setVisibility(View.GONE);
            }
        }

    }

    private void initView() {
        lv_order_detail = (ListView) findViewById(R.id.lv_order_detail);
        order_no = (TextView) findViewById(R.id.order_no);
        order_date = (TextView) findViewById(R.id.order_date);
        order_state = (TextView) findViewById(R.id.order_state);
        pay_money_tv = (TextView) findViewById(R.id.pay_money_tv);
        card_iv = (ImageView) findViewById(R.id.card_iv);
        package_iv = (ImageView) findViewById(R.id.package_iv);
        num = (TextView) findViewById(R.id.num);
        pay_btn = (TextView) findViewById(R.id.pay_btn);
        orderDetailList = new ArrayList<OrderDetailBean>();
        mOrderDetailAdapter = new OrderDetailAdapter(orderDetailList, this, null);
        lv_order_detail.setAdapter(mOrderDetailAdapter);
        lv_order_detail.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long arg3) {

            }
        });
    }

    private void initData() {
        if (mWaitDialog == null) {
            mWaitDialog = new WaitDialog(this, R.string.loading_data);
        }
        mWaitDialog.show();
        mSharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCE, MODE_PRIVATE);
        String tokenid = mSharedPreferences.getString(Constant.TOKEN_ID, "");
        String saleId = mSharedPreferences.getString(Constant.ID, "");
        NetProxyManager.getInstance().toGetOrderDetail(mHandler, tokenid, saleId, order_id);
    }

    public void onBack(View view) {
        finish();
    }

    public void payBtn(View view) {
        Intent i = new Intent();
        i.setClass(this, PayActivity.class);
        if (orderBean.getBigOrderType() == 2) {
            i.putExtra("type", "study");
        }
        i.putExtra("order_id", order_id);
        startActivityForResult(i, 0x1314);
    }

    private void parseOrderList(String result) {
        if (Errors.ERROR_NET.equals(result) || Errors.ERROR_SERVER.equals(result)) {
            ToastUtil.showLong(this, R.string.network_error);
            if (mWaitDialog != null && mWaitDialog.isShowing()) {
                mWaitDialog.dismiss();
            }
            return;
        }
        // to parser json data
        try {
            JSONObject json = new JSONObject(result);
            boolean success = json.getBoolean("success");
            LogUtils.d("" + json);
            if (!success) {
                String message = json.getString("message");
                ToastUtil.showShort(this, message);
                if (json.getString("resCode").equals(Constant.RELOGIN)) {
                    backLogin();
                }
                return;
            }
            JSONObject job = json.getJSONObject("object");
            orderDetailList.clear();
            double orderPrice = 0d;
            order_no.setText("No." + job.getString("orderId"));
            order_date.setText(DateUtil.stringToStr(job.getString("orderTime")));
            orderPrice = job.getDouble("cardPrice");
//			orderPrice = orderPrice + job.getDouble("packPrice");

            // orderBean.setGoodlist(goodlist);
            JSONArray jsonArray1 = job.getJSONArray("customerList");
            for (int i = 0; i < jsonArray1.length(); i++) {
                OrderDetailBean orderDetailBean = new OrderDetailBean();
                JSONObject contactJB = jsonArray1.getJSONObject(i);
                ContactSortModel contactSortModel = new ContactSortModel();
                contactSortModel.setAvater(contactJB.getString("avatar"));
//				contactSortModel.setId(contactJB.getString("customerId"));
                contactSortModel.setName(contactJB.getString("customerName"));
                orderDetailBean.setCustomer(contactSortModel);

                JSONArray jsonArray = contactJB.getJSONArray("productList");
//				List<GoodBean> goodlist = new ArrayList<GoodBean>();
                GoodBean goodBean = new GoodBean();
                for (int j = 0; j < jsonArray.length(); j++) {
                    JSONObject goodJB = jsonArray.getJSONObject(j);
//					goodBean.setId(goodJB.getString("pId"));
                    String goodPrice = goodJB.getString("price");
                    orderPrice = orderPrice + Double.valueOf(goodPrice);
                    goodBean.setPiprice(goodPrice);
                    goodBean.setId(goodJB.getString("id"));
                    if (goodJB.has("smallPicUrl")) {
                        String picUrls = goodJB.getString("smallPicUrl");
//						if (picUrls.contains(",")) {
//							goodBean.setPicUrls(picUrls.split(",")[0]);
//						} else {
                        goodBean.setPicUrls(picUrls);
//						}
                    }
                    goodBean.setPname(goodJB.getString("name"));
                    // goodlist.add(goodBean);
                }
                orderDetailBean.setGoodBean(goodBean);
                orderDetailList.add(orderDetailBean);
            }
            mOrderDetailAdapter.notifyDataSetChanged();
            if (job.has("cardFile")) {
                JSONArray jsonArray = job.getJSONArray("cardFile");
                if (jsonArray.length() > 0) {
                    ImageManager.loadImage(jsonArray.getString(0), card_iv);
                }
            }
            if (job.has("packImage")) {
                ImageManager.loadImage(job.getString("packImage"), package_iv);
            }

            orderPrice = orderPrice * jsonArray1.length();
            num.setText("" + jsonArray1.length());
            DecimalFormat df = new DecimalFormat("0.0");
            pay_money_tv.setText("实付款：￥" + df.format(job.getDouble("totalPrice")));
            if (job.has("orderStatus")) {
                String orderStatus = job.getString("orderStatus");
                if (orderStatus.equals("0")) {
                    order_state.setText("未支付");
//					order_state.setText("已取消");
                    pay_btn.setVisibility(View.GONE);
                } else if (orderStatus.equals("1")) {
                    order_state.setText("已付款");
                    pay_btn.setVisibility(View.GONE);
                } else if (orderStatus.equals("2")) {
                    order_state.setText("已发货");
                    pay_btn.setVisibility(View.GONE);
                } else if (orderStatus.equals("3")) {
                    order_state.setText("已取消");
                    pay_btn.setVisibility(View.GONE);
                } else if (orderStatus.equals("100")) {
                    order_state.setText("支付中");
                    pay_btn.setVisibility(View.GONE);
                } else {
                    order_state.setText("已取消");
                    pay_btn.setVisibility(View.GONE);
                }
            }

            if (mWaitDialog != null && mWaitDialog.isShowing()) {
                mWaitDialog.dismiss();
            }
        } catch (Exception ex) {
            LogUtils.e(ex.getMessage());
            return;
        }
    }

    /**
     * 设置状态栏背景状态
     */
    private void setTranslucentStatus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            // 透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        SystemStatusManager tintManager = new SystemStatusManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.main_bg_color);// 状态栏背景
        getWindow().getDecorView().setFitsSystemWindows(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == 0x1314) {
            initData();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
