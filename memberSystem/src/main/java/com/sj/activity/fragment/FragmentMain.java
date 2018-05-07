package com.sj.activity.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jude.easyrecyclerview.EasyRecyclerView;
import com.lyp.membersystem.R;
import com.lyp.membersystem.ui.MallFragmentActivity;
import com.lyp.membersystem.ui.MyCustomerActivity;
import com.lyp.membersystem.ui.ServiceActivity;
import com.lyp.membersystem.utils.ToastUtil;
import com.sj.activity.ActivityStudy;
import com.sj.activity.base.FragmentBase;
import com.youth.banner.Banner;

/**
 * 创建时间: on 2018/5/4.
 * 创建人: 孙杰
 * 功能描述:
 */
public class FragmentMain extends FragmentBase implements View.OnClickListener{

    Banner banner;
    ImageView back;
    TextView tvTitle;
    ImageView right;
    TextView txtCustomer;
    TextView txtGiftShop;
    TextView txtStudy;
    TextView txtReservation;
    EasyRecyclerView rylView;

    public static FragmentMain newInstance() {
        return new FragmentMain();
    }


    public FragmentMain() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_main, container, false);
        return view;
    }

    private void initView() {
        banner = (Banner) findViewById(R.id.banner);
        back = (ImageView) findViewById(R.id.back);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        right = (ImageView) findViewById(R.id.right);
        txtCustomer = (TextView) findViewById(R.id.txt_customer);
        txtGiftShop = (TextView) findViewById(R.id.txt_gift_shop);
        txtStudy = (TextView) findViewById(R.id.txt_study);
        txtReservation = (TextView) findViewById(R.id.txt_reservation);
        rylView = (EasyRecyclerView) findViewById(R.id.ryl_view);
        tvTitle.setText(getResources().getString(R.string.app_name));
//        findViewById(R.id.layout_back).setVisibility(View.GONE);
        right.setImageResource(R.drawable.img_notice);
        right.setOnClickListener(this);
        txtCustomer.setOnClickListener(this);
        txtGiftShop.setOnClickListener(this);
        txtStudy.setOnClickListener(this);
        txtReservation.setOnClickListener(this);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        Intent intent = new Intent();
        switch (id){
            case R.id.right:
                ToastUtil.showMessage("系统通知");
                break;
            case R.id.txt_customer:
                intent.setClass(getHoldingActivity(), MyCustomerActivity.class);
                startActivity(intent);
                break;
            case R.id.txt_gift_shop:
                intent.setClass(getHoldingActivity(), MallFragmentActivity.class);
                startActivity(intent);
                break;
            case R.id.txt_study:
                intent.setClass(getHoldingActivity(), ActivityStudy.class);
                startActivity(intent);
                break;
            case R.id.txt_reservation:
                intent.setClass(getHoldingActivity(), ServiceActivity.class);
                startActivity(intent);
                break;
                default:
        }
    }
}
