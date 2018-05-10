package com.sj.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;

import com.jady.retrofitclient.HttpManager;
import com.lyp.membersystem.R;
import com.lyp.membersystem.utils.ToastUtil;
import com.lyp.membersystem.view.tabmenu.PagerSlidingTabStrip;
import com.sj.activity.base.ActivityBase;
import com.sj.activity.fragment.CardFragment;
import com.sj.http.Callback;
import com.sj.http.UrlConfig;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.common.Constant;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ActivityCardBag extends ActivityBase {

    private int REQUEST_CODE_SCAN = 111;

    PagerSlidingTabStrip tabs;
    ViewPager pager;
    String[] titles = new String[2];
    private SharedPreferences mSharedPreferences;

    List<Fragment> fragmentList = new ArrayList<>(2);

    MyAdapter pageAdapter;

    String tokenid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initView() {
        SharedPreferences mSharedPreferences = getSharedPreferences(com.lyp.membersystem.utils.Constant.SHARED_PREFERENCE, MODE_PRIVATE);
        tokenid = mSharedPreferences.getString(com.lyp.membersystem.utils.Constant.TOKEN_ID, "");
        setRightImg(R.drawable.qr_code_white);
        pager = (ViewPager) findViewById(R.id.pager);
        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        titles[0] = "我的卡券";
        titles[1] = "已使用卡券";
        fragmentList.add(CardFragment.newInstance(0));
        fragmentList.add(CardFragment.newInstance(1));
        pageAdapter = new MyAdapter(getSupportFragmentManager());
        pager.setAdapter(pageAdapter);
        tabs.setViewPager(pager);
        tabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                pager.setCurrentItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void setCardNum(int index, int count, int sideCount) {
        Log.d("setCardNum", "setCardNum: index = " + index);
        if (index == 0) {
            titles[0] = "我的卡券(" + count + ")";
            titles[1] = "已使用卡券(" + sideCount + ")";
            tabs.setViewPager(pager);
        }
    }


    public class MyAdapter extends FragmentPagerAdapter {

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

    }

    @Override
    public int getContentLayout() {
        return R.layout.activity_card_bag;
    }

    @Override
    public void onRightImg(View view) {
        //ZxingConfig config = new ZxingConfig();
//config.setShowbottomLayout(true);//底部布局（包括闪光灯和相册）
//config.setPlayBeep(true);//是否播放提示音
//config.setShake(true);//是否震动
//config.setShowAlbum(true);//是否显示相册
//config.setShowFlashLight(true);//是否显示闪光灯


//如果不传 ZxingConfig的话，两行代码就能搞定了
        Intent intent = new Intent(ActivityCardBag.this, CaptureActivity.class);
//intent.putExtra(Constant.INTENT_ZXING_CONFIG, config);
        startActivityForResult(intent, REQUEST_CODE_SCAN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 扫描二维码/条码回传
        if (requestCode == REQUEST_CODE_SCAN && resultCode == RESULT_OK) {
            if (data != null) {
                String content = data.getStringExtra(Constant.CODED_CONTENT);
                doChangeCard(content);
            }
        }
    }

    private void doChangeCard(String id) {
        showProgress();
        Map<String, Object> parameters = new ArrayMap<>(3);
        parameters.put("token_id", tokenid);
        parameters.put("itemId", id);
        HttpManager.get(UrlConfig.FORUM_BUY_LIST, parameters, new Callback() {
            @Override
            public void onSuccess(String message) {
                ToastUtil.showMessage("购买成功");
            }

            @Override
            public void onSuccessData(String json) {
            }

            @Override
            public void onFailure(String error_code, String error_message) {
            }

            @Override
            public void onFinish() {
                hideProgress();
            }
        });
    }
}
