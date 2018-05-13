package com.sj.activity.adapter;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.lyp.membersystem.R;
import com.sj.activity.ActivityEditUserInfo;


public class UserInfoRyvAdapter extends RecyclerArrayAdapter<ActivityEditUserInfo.Item> {
    Context context;
    public UserInfoRyvAdapter(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public UserInfoRyvHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new UserInfoRyvHolder(parent,context);
    }

    private static class UserInfoRyvHolder extends BaseViewHolder<ActivityEditUserInfo.Item> {
        private TextView txtName;
        private TextView txtValue;

        public UserInfoRyvHolder(ViewGroup parent, Context context) {
            super(parent, R.layout.userinfo_item);
            txtName = $(R.id.txt_name);
            txtValue = $(R.id.txt_value);
        }

        @Override
        public void setData(final ActivityEditUserInfo.Item data) {
            super.setData(data);
            txtName.setText(data.getName());
            txtValue.setText(data.getValue());
        }
    }
}
