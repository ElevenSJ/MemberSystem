package com.sj.activity.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jady.retrofitclient.HttpManager;
import com.jude.easyrecyclerview.EasyRecyclerView;
import android.support.v4.widget.SwipeRefreshLayout;
import com.lyp.membersystem.R;
import com.lyp.membersystem.utils.Constant;
import com.sj.activity.ActivityCardBag;
import com.sj.activity.adapter.CardRyvAdapter;
import com.sj.activity.base.FragmentBase;
import com.sj.activity.bean.CardBean;
import com.sj.http.Callback;
import com.sj.http.GsonResponsePasare;
import com.sj.http.UrlConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 创建时间: on 2018/5/6.
 * 创建人: 孙杰
 * 功能描述:
 */
public class CardFragment extends FragmentBase implements SwipeRefreshLayout.OnRefreshListener{

    EasyRecyclerView rylView;
    CardRyvAdapter mAdapter;
    SharedPreferences mSharedPreferences;
    String tokenId;
    int index=0;
    public static CardFragment newInstance(int index) {
        final CardFragment f = new CardFragment();
        final Bundle args = new Bundle();
        args.putInt("index", index);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        index = getArguments().getInt("index");
        mSharedPreferences = getHoldingActivity().getSharedPreferences(Constant.SHARED_PREFERENCE, getHoldingActivity().MODE_PRIVATE);
        tokenId = mSharedPreferences.getString(Constant.TOKEN_ID, "");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragmemt_card, container, false);
        initView();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getUserVisibleHint()){
            onRefresh();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser&&rylView!=null&&mAdapter!=null&&mAdapter.getCount()==0){
           onRefresh();
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    private void initView() {
        rylView = (EasyRecyclerView) findViewById(R.id.ryl_view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getHoldingActivity(), LinearLayoutManager.VERTICAL, false);
        rylView.setLayoutManager(layoutManager);
        rylView.setRefreshListener(CardFragment.this);
        mAdapter = new CardRyvAdapter(getHoldingActivity(),index);
        rylView.setAdapter(mAdapter);
    }


    private void getData() {
        Map<String, Object> parameters = new ArrayMap<>(2);
        parameters.put("token_id", tokenId);
        parameters.put("type", index);
        HttpManager.get(UrlConfig.CARD_LIST, parameters, new Callback() {
            @Override
            public void onSuccess(String message) {
                Log.d(TAG, "onSuccess: ");
            }

            @Override
            public void onSuccessData(String json) {
                Log.d(TAG, "onSuccessData: ");
                CardBean cardBean = new GsonResponsePasare<CardBean>() {
                }.deal(json);
                if (cardBean!=null){
                    Log.d(TAG, "onSuccessData: index = "+index+",size = "+cardBean.getCpChiefBBS().size());
                    ((ActivityCardBag)getHoldingActivity()).setCardNum(index,cardBean.getCount(),cardBean.getSideCount());
                    List<CardBean.CpBaseBean> allData = new ArrayList<>(cardBean.getCount());
                    allData.addAll(cardBean.getCpGoods());
                    allData.addAll(cardBean.getCpService());
                    allData.addAll(cardBean.getCpCourse());
                    allData.addAll(cardBean.getCpChiefBBS());
                    mAdapter.addAll(allData);
                }
            }

            @Override
            public void onFailure(String error_code, String error_message) {
                Log.d(TAG, "onSuccessData: ");

            }

            @Override
            public void onFinish() {
                super.onFinish();
                rylView.setRefreshing(false);
            }
        });
    }

    @Override
    public void onRefresh() {
        if (mAdapter!=null&&mAdapter.getCount()!=0){
            mAdapter.clear();
        }
        getData();
    }
}
