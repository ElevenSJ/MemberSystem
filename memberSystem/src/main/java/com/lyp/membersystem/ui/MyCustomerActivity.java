package com.lyp.membersystem.ui;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.jady.retrofitclient.HttpManager;
import com.lyp.membersystem.R;
import com.lyp.membersystem.adapter.MyCustomerAdapter;
import com.lyp.membersystem.base.BaseActivity;
import com.lyp.membersystem.database.CustomerDao;
import com.lyp.membersystem.log.LogUtils;
import com.lyp.membersystem.net.Errors;
import com.lyp.membersystem.net.MessageContants;
import com.lyp.membersystem.net.NetProxyManager;
import com.lyp.membersystem.utils.Constant;
import com.lyp.membersystem.utils.DateUtil;
import com.lyp.membersystem.utils.SystemStatusManager;
import com.lyp.membersystem.utils.ToastUtil;
import com.lyp.membersystem.view.CustomPopupWindow;
import com.lyp.membersystem.view.contactsort.ContactSortModel;
import com.lyp.membersystem.view.contactsort.PinyinComparator;
import com.lyp.membersystem.view.contactsort.PinyinUtils;
import com.lyp.membersystem.view.contactsort.SideBar;
import com.lyp.membersystem.view.dialog.WaitDialog;
import com.sj.activity.bean.DataListBean;
import com.sj.activity.bean.ReplayBean;
import com.sj.http.BaseResponse;
import com.sj.http.Callback;
import com.sj.http.GsonResponsePasare;
import com.sj.http.UrlConfig;
import com.sj.widgets.TitlePopAdapter;
import com.sj.widgets.TitlePopupWindow;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import me.next.tagview.TagCloudView;

public class MyCustomerActivity extends BaseActivity {

    private final static int ADD_CUSTOMER_REQUEST_CODE = 0x1314;
    private final static int UPDATE_CUSTOMER_REQUEST_CODE = 0x1315;
    private ListView sortListView;
    private SideBar sideBar;
    private TextView dialog, mTvTitle;
    private MyCustomerAdapter adapter;
    // private EditTextWithDel mEtSearchName;
    private List<ContactSortModel> mCustomerList = new CopyOnWriteArrayList<>();
    ;
    private RelativeLayout select_mode;
    private CheckBox customer_select_all;
    private WaitDialog mWaitDialog;
    private CustomPopupWindow mPopWin;
    private SharedPreferences mSharedPreferences;
    private CustomerDao mCustomerDao;
    private int deletePosition = -1;
    private CustomPopupWindow mGengerSelectPopWin;
    private TextView genger_select;

    private TagCloudView tagCloudView;
    private TextView txtOpenOrClose;
    String tokenid;
    String saleId;

    List<String> selectedTags = new ArrayList<>();
    final List<String> alltags = new ArrayList<>();

    private int selectedType = 0;

    private TitlePopupWindow titlePop;
    private TitlePopAdapter titlePopAdapter;

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MessageContants.MSG_GET_CUSTOMER_LIST: {
                    parseCustomerList((String) msg.obj);
                    break;
                }
                case MessageContants.MSG_DELETE_CUSTOMER: {
                    parseDeleteCustomer((String) msg.obj);
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
        setContentView(R.layout.my_customer_layout);
        ToastUtil.showLongMessage("长按对应的条目可以删除！");
        mSharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCE, MODE_PRIVATE);
        tokenid = mSharedPreferences.getString(Constant.TOKEN_ID, "");
        saleId = mSharedPreferences.getString(Constant.ID, "");

        initViews();
    }

    private void initViews() {
        // mEtSearchName = (EditTextWithDel) findViewById(R.id.et_search);
        tagCloudView = findViewById(R.id.tag_cloud_view);
        sideBar = (SideBar) findViewById(R.id.sidrbar);
        dialog = (TextView) findViewById(R.id.dialog);
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        sortListView = (ListView) findViewById(R.id.lv_contact);
        mTvTitle.setText(R.string.my_customer);
        select_mode = (RelativeLayout) findViewById(R.id.select_mode);
        select_mode.setVisibility(View.GONE);
        customer_select_all = (CheckBox) findViewById(R.id.customer_select_all);
        initDatas();
        initEvents();
        adapter = new MyCustomerAdapter(this, mCustomerList);
        sortListView.setAdapter(adapter);
        getCustomerData();
        if (mWaitDialog == null) {
            mWaitDialog = new WaitDialog(MyCustomerActivity.this, R.string.loading_data);
        }
        mWaitDialog.show();
        genger_select = (TextView) findViewById(R.id.genger_select);

        txtOpenOrClose = findViewById(R.id.txt_open_or_close);
        txtOpenOrClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(titlePop!=null){
                    titlePop.showAsDropDown(findViewById(R.id.layout_title));
                    titlePopAdapter.setChecked();
                }
            }
        });
        tagCloudView.setOnTagClickListener(new TagCloudView.OnTagClickListener() {
            @Override
            public void onTagClick(int position) {
                TextView tagTextView = (TextView) tagCloudView.getChildAt(position);
                if (tagTextView.isSelected()) {
                    tagTextView.setSelected(false);
                    tagTextView.setTextColor(getResources().getColor(R.color.gray));
                    if (position == 0) {
                        selectedTags.clear();
                    }else{
                        selectedTags.remove(tagTextView.getText().toString());
                    }
                } else {
                    tagTextView.setSelected(true);
                    tagTextView.setTextColor(getResources().getColor(R.color.main_bg_color));
                    if (position == 0) {
                        selectedTags.addAll(alltags);
                    }else{
                        selectedTags.add(tagTextView.getText().toString());
                    }

                }
                if (position == 0) {
                    for (int i = 0; i < tagCloudView.getChildCount(); i++) {
                        TextView textView = (TextView) tagCloudView.getChildAt(i);
                        textView.setSelected(tagTextView.isSelected());
                        textView.setTextColor(tagTextView.isSelected()?getResources().getColor(R.color.main_bg_color):getResources().getColor(R.color.gray));
                    }
                }
                checkDataList();

            }
        });
        tagCloudView.post(new Runnable() {
            @Override
            public void run() {
                getTagData();
            }
        });
    }


    private void getTagData() {
        Map<String, Object> parameters = new ArrayMap<>(4);
        parameters.put("token_id", tokenid);
        HttpManager.get(UrlConfig.CUSTOM_TAG_LIST, parameters, new Callback() {
            @Override
            public void onSuccess(String message) {
            }

            @Override
            public void onSuccessData(String json) {
                List<ContactSortModel.TaglistBean> tagList = new GsonResponsePasare<List<ContactSortModel.TaglistBean>>() {
                }.deal(json);
                if (tagList != null) {
                    if (tagList != null && !tagList.isEmpty()) {
                        initTag(tagList);
                    }
                }
            }

            @Override
            public void onFailure(String error_code, String error_message) {
            }

        });
    }

    private void initTag(List<ContactSortModel.TaglistBean> tagList) {
        alltags.clear();
        alltags.add("全部");
        for (int i = 0; i < tagList.size(); i++) {
            alltags.add(tagList.get(i).getTagName());
        }
        tagCloudView.setTags(alltags);
        if (titlePop == null) {
            titlePop = new TitlePopupWindow(this, R.layout.title_popupwindows);
            titlePopAdapter = new TitlePopAdapter(this);
            titlePop.setAdapater(titlePopAdapter);
            titlePop.setOnItemClick(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (selectedTags.contains(titlePopAdapter.getItem(position))) {
                        if (titlePopAdapter.getItem(position).equals("全部")) {
                            selectedTags.clear();
                            titlePopAdapter.setChecked();
                        } else {
                            view.setBackgroundResource(R.drawable.shape_circle_button_gray);
                            ((TextView) view.findViewById(R.id.tv_title)).setTextColor(getResources().getColor(R.color.gray));
                            selectedTags.remove(titlePopAdapter.getItem(position));
                        }
                    } else {
                        if (titlePopAdapter.getItem(position).equals("全部")) {
                            selectedTags.clear();
                            selectedTags.addAll(alltags);
                            titlePopAdapter.setChecked();
                        } else {
                            ((TextView) view.findViewById(R.id.tv_title)).setTextColor(getResources().getColor(R.color.main_bg_color));
                            view.setBackgroundResource(R.drawable.shape_circle_button_main_color);
                            selectedTags.add(titlePopAdapter.getItem(position));
                        }
                    }
                    for (int i = 0; i < tagCloudView.getChildCount(); i++) {
                        TextView textView = (TextView) tagCloudView.getChildAt(i);
                        textView.setSelected(selectedTags.contains(textView.getText().toString()));
                        textView.setTextColor(selectedTags.contains(textView.getText().toString()) ? getResources().getColor(R.color.main_bg_color) : getResources().getColor(R.color.gray));
                    }
                    checkDataList();
                }
            });
        }
        titlePopAdapter.setData(alltags, selectedTags);
    }

    private void getCustomerData() {

        NetProxyManager.getInstance().toGetCustomerList(mHandler, tokenid, saleId, null, null, 0);
    }

    public void gengerSelect(View view) {
        View inflate = getLayoutInflater().inflate(R.layout.genger_select_popupwindow, null);
        mGengerSelectPopWin = new CustomPopupWindow(inflate, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
                MyCustomerActivity.this);
        inflate.findViewById(R.id.btn1).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedType = 0;
                checkDataList();
                genger_select.setText("全部");
                mGengerSelectPopWin.dismiss();
            }
        });
        inflate.findViewById(R.id.btn2).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedType = 1;
                checkDataList();
                genger_select.setText("男");
                mGengerSelectPopWin.dismiss();
            }
        });
        inflate.findViewById(R.id.btn3).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedType = 2;
                checkDataList();
                genger_select.setText("女");
                mGengerSelectPopWin.dismiss();
            }
        });
        inflate.findViewById(R.id.btn4).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedType = 3;
                genger_select.setText("儿童");
                mGengerSelectPopWin.dismiss();
            }
        });

        mGengerSelectPopWin.showAsDropDown(view);
    }

    private void checkDataList() {
        mCustomerList.clear();
        switch (selectedType) {
            case 0:
                mCustomerList.addAll(mCustomerDao.getContactList());
                break;
            case 1:
                mCustomerList.addAll(mCustomerDao.getMaleContactList());
                break;
            case 2:
                mCustomerList.addAll(mCustomerDao.getFeMaleContactList());
                break;
            case 3:
                mCustomerList.addAll(mCustomerDao.getErTongContactList());
                break;
        }
        for (String tag : selectedTags) {
            for (int i = 0; i < mCustomerList.size(); i++) {
                if (!mCustomerList.get(i).getTags().contains(tag)) {
                    mCustomerList.remove(i);
                }
            }
        }
        contactsSort();
    }

    private void parseCustomerList(String result) {
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
            JSONArray jsonArray = job.getJSONArray("infoList");
            mCustomerList.clear();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                ContactSortModel contactSortModel = new ContactSortModel();
                contactSortModel.setId(jsonObject.getString("id"));
                if (jsonObject.has("birthday")) {
                    String birthday = jsonObject.getString("birthday");
                    contactSortModel.setBirthday(birthday);
                    contactSortModel.setFeteDay(DateUtil.stringToStr5(birthday));
                    LogUtils.d("lyp", "fete day: " + DateUtil.stringToStr5(birthday));
                    LogUtils.d("lyp", "age: " + DateUtil.getAgeNum(birthday));
                    contactSortModel.setAge(DateUtil.getAgeNum(birthday));
                }
                if (jsonObject.has("specialday")) {
                    contactSortModel.setSpecialday(jsonObject.getString("specialday"));
                }
                if (jsonObject.has("cname")) {
                    contactSortModel.setName(jsonObject.getString("cname"));
                }
                if (jsonObject.has("nickname")) {
                    contactSortModel.setNickname(jsonObject.getString("nickname"));
                }
                if (jsonObject.has("gender")) {
                    contactSortModel.setGender(jsonObject.getString("gender"));
                }
                if (jsonObject.has("profiles")) {
                    contactSortModel.setProfiles(jsonObject.getString("profiles"));
                }
                if (jsonObject.has("cphone")) {
                    contactSortModel.setCphone(jsonObject.getString("cphone"));
                }
                if (jsonObject.has("caddress")) {
                    contactSortModel.setCaddress(jsonObject.getString("caddress"));
                }
                if (jsonObject.has("cemail")) {
                    contactSortModel.setCemail(jsonObject.getString("cemail"));
                }
                if (jsonObject.has("maritalStatus")) {
                    contactSortModel.setMarry(jsonObject.getString("maritalStatus"));
                }
                if (jsonObject.has("district")) {
                    contactSortModel.setDistrict(jsonObject.getString("district"));
                }
                if (jsonObject.has("haveChildren")) {
                    contactSortModel.setHaveChildren(jsonObject.getString("haveChildren"));
                }
                if (jsonObject.has("avatar")) {
                    contactSortModel.setAvater(jsonObject.getString("avatar"));
                }
//				if (jsonObject.has("age")) {
//					contactSortModel.setAge(jsonObject.getInt("age"));
//				}
                if (jsonObject.has("policyNo")) {
                    contactSortModel.setPolicyNo(jsonObject.getString("policyNo"));
                }
                if (jsonObject.has("profession")) {
                    contactSortModel.setProfession(jsonObject.getString("profession"));
                }
                List<String> tags = new ArrayList<>();
                JSONArray tagArray = jsonObject.getJSONArray("taglist");
                for (int j = 0; j < tagArray.length(); j++) {
                    JSONObject jsontagObject = tagArray.getJSONObject(j);
                    ContactSortModel.TaglistBean taglistBean = new ContactSortModel.TaglistBean();
                    taglistBean.setId(jsontagObject.getString("tagId"));
                    taglistBean.setTagName(jsontagObject.getString("tagName"));
                    tags.add(jsontagObject.getString("tagName"));
                }
                contactSortModel.setTags(TextUtils.join(",", tags.toArray()));
                mCustomerList.add(contactSortModel);
            }
            contactsSort();
            mCustomerDao.saveContactList(mCustomerList);
            if (mWaitDialog != null && mWaitDialog.isShowing()) {
                mWaitDialog.dismiss();
            }
        } catch (Exception ex) {
            LogUtils.e(ex.getMessage());
            return;
        }
    }

    private void parseDeleteCustomer(String result) {
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
                return;
            }
            mCustomerDao.deleteContact(mCustomerList.get(deletePosition).getId());
            mCustomerList.remove(deletePosition);
            adapter.notifyDataSetChanged();
        } catch (Exception ex) {
            LogUtils.e(ex.getMessage());
            return;
        }
    }

    public void addCustomer(View view) {
        Intent intent = new Intent();
        intent.setClass(this, AddCustomerActivity.class);
        startActivityForResult(intent, ADD_CUSTOMER_REQUEST_CODE);
    }

    public void backMyCustomer(View view) {
        back();
    }

    private void back() {
        if (adapter.isMulChoice()) {
            mTvTitle.setText(R.string.my_customer);
            adapter.setMulChoice(false);
            adapter.getSelectContact().clear();
            adapter.isCheckNum = 0;
            select_mode.setVisibility(View.GONE);
            customer_select_all.setChecked(false);
            for (int i = 0; i < mCustomerList.size(); i++) {
                adapter.visiblecheck.put(i, CheckBox.INVISIBLE);
                adapter.ischeck.put(i, false);
            }
            adapter.notifyDataSetChanged();
        } else {
            finish();
        }
    }

    private void contactsSort() {
        ArrayList<String> indexString = new ArrayList<String>();

        for (int i = 0; i < mCustomerList.size(); i++) {
            ContactSortModel sortModel = mCustomerList.get(i);
            String name = sortModel.getName();
            if (Character.isDigit(name.charAt(0))) {
                sortModel.setSortLetters("#");
                if (!indexString.contains("#")) {
                    indexString.add("#");
                }
                continue;
            }
            String pinyin = PinyinUtils.getPingYin(name);
            String sortString = pinyin.substring(0, 1).toUpperCase();
            LogUtils.d("lyp", "Sort contacts: " + sortString);
            if (sortString.matches("[A-Z]")) {
                sortModel.setSortLetters(sortString.toUpperCase());
                LogUtils.d("contacts Letters: " + sortString.toUpperCase());
                if (!indexString.contains(sortString)) {
                    indexString.add(sortString);
                }
            }
        }
        Comparator<Object> com = Collator.getInstance(java.util.Locale.CHINA);
        Collections.sort(mCustomerList, new PinyinComparator());
        Collections.sort(indexString, com);
        // sideBar.setIndexText(indexString);
        adapter.notifyDataSetChanged();
    }

    private void initEvents() {
        // 设置右侧触摸监听
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                // 该字母首次出现的位置
                int position = adapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    sortListView.setSelection(position + 1);
                }
            }
        });

        // ListView的点击事件
        sortListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ContactSortModel contactSortModel = mCustomerList.get(position);
                Intent intent = new Intent();
                intent.setClass(MyCustomerActivity.this, CustomerDetailActivity.class);
                intent.putExtra("id", contactSortModel.getId());
                startActivityForResult(intent, UPDATE_CUSTOMER_REQUEST_CODE);
                // showClickEvent(view, position);

                // if (adapter.isMulChoice()) {
                // CheckBox checkBox = (CheckBox)
                // view.findViewById(R.id.customer_select);
                // if (checkBox.isChecked()) {
                // customer_select_all.setChecked(false);
                // checkBox.setChecked(false);
                // adapter.ischeck.put(position, false);
                // adapter.isCheckNum--;
                // adapter.getSelectContact().remove(mCustomerList.get(position));
                // } else {
                // checkBox.setChecked(true);
                // adapter.ischeck.put(position, true);
                // adapter.isCheckNum++;
                // if (adapter.isCheckNum == adapter.getCount()) {
                // customer_select_all.setChecked(true);
                // }
                // adapter.getSelectContact().add(mCustomerList.get(position));
                // }
                // adapter.notifyDataSetChanged();
                // ToastUtil.showShort(MyCustomerActivity.this,
                // position + " " + adapter.isCheckNum + " " +
                // mCustomerList.get(position).getName());
                // } else {
                // ToastUtil.showShort(MyCustomerActivity.this,
                // position + " " + mCustomerList.get(position).getName());
                // }
            }
        });

        sortListView.setOnItemLongClickListener(new Onlongclick());

        // 根据输入框输入值的改变来过滤搜索
        // mEtSearchName.addTextChangedListener(new TextWatcher() {
        // @Override
        // public void beforeTextChanged(CharSequence s, int start, int count,
        // int after) {
        //
        // }
        //
        // @Override
        // public void onTextChanged(CharSequence s, int start, int before, int
        // count) {
        // //当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
        // filterData(s.toString());
        // }
        //
        // @Override
        // public void afterTextChanged(Editable s) {
        //
        // }
        // });
    }

    private void showClickEvent(View view, final int position) {
        final ContactSortModel contactSortModel = mCustomerList.get(position);
        View inflate = getLayoutInflater().inflate(R.layout.customer_info_popupwindow, null);
        mPopWin = new CustomPopupWindow(inflate, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
                MyCustomerActivity.this);
        inflate.findViewById(R.id.dial_phone).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                Uri uri = Uri.parse("tel:" + contactSortModel.getCphone());
                intent.setData(uri);
                // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                mPopWin.dismiss();
            }
        });
        inflate.findViewById(R.id.send_sms).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("smsto:" + contactSortModel.getCphone());
                Intent intentMessage = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intentMessage);
                mPopWin.dismiss();
            }
        });
        inflate.findViewById(R.id.delet_customer).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSharedPreferences == null) {
                    mSharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCE, MODE_PRIVATE);
                }
                String tokenid = mSharedPreferences.getString(Constant.TOKEN_ID, "");
                String saleId = mSharedPreferences.getString(Constant.ID, "");
                deletePosition = position;
                NetProxyManager.getInstance().toDeleteCustomer(mHandler, tokenid, saleId, contactSortModel.getId());
                mPopWin.dismiss();
            }
        });

        mPopWin.showAtLocation(view, Gravity.CENTER, 0, 0);
    }

    private void initDatas() {
        sideBar.setTextView(dialog);
        mCustomerDao = new CustomerDao(this.getApplicationContext());
        mCustomerList.addAll(mCustomerDao.getContactList());
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

    class Onlongclick implements AdapterView.OnItemLongClickListener {

        @Override
        public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            showClickEvent(arg1, arg2);
            // if (adapter.isMulChoice()) {
            // return false;
            // }
            // mTvTitle.setText(R.string.select_customer);
            // adapter.setMulChoice(true);
            // // adapter.getSelectContact().clear();
            // select_mode.setVisibility(View.VISIBLE);
            // customer_select_all.setChecked(false);
            // for (int i = 0; i < mCustomerList.size(); i++) {
            // adapter.visiblecheck.put(i, CheckBox.VISIBLE);
            // // adapter.ischeck.put(i, false);
            // }
            // adapter.notifyDataSetChanged();
            // ToastUtil.showShort(MyCustomerActivity.this, "onItemLongClick");
            return true;
        }
    }

    public void selectAll(View view) {
        CheckBox checkbox = (CheckBox) view;
        if (checkbox.isChecked()) {
            for (int i = 0; i < mCustomerList.size(); i++) {
                adapter.ischeck.put(i, true);
            }
            adapter.isCheckNum = adapter.getCount();
        } else {
            for (int i = 0; i < mCustomerList.size(); i++) {
                adapter.ischeck.put(i, false);
            }
            adapter.isCheckNum = 0;
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            back();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void backLogin() {
        super.backLogin();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case ADD_CUSTOMER_REQUEST_CODE:
                    getCustomerData();
                    break;
                case UPDATE_CUSTOMER_REQUEST_CODE:
                    getCustomerData();
                default:
                    break;
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
