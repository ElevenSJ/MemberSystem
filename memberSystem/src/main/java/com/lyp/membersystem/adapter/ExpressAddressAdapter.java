package com.lyp.membersystem.adapter;

import java.util.List;

import com.lyp.membersystem.R;
import com.lyp.membersystem.bean.ExpressAddressBean;
import com.lyp.membersystem.net.NetProxyManager;
import com.lyp.membersystem.ui.AddExpressActivity;
import com.lyp.membersystem.utils.Constant;
import com.lyp.membersystem.utils.ToastUtil;
import com.yuntongxun.ecdemo.common.utils.LogUtil;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ExpressAddressAdapter extends BaseAdapter {

	private List<ExpressAddressBean> list;
	private Activity context;
	private boolean isSelect = false;
	private Handler mHandler;
	private ExpressAddressBean deleteBean;

	public ExpressAddressAdapter(List<ExpressAddressBean> list, Activity context, Handler mHandler, boolean isSelect) {
		super();
		this.list = list;
		this.context = context;
		this.isSelect = isSelect;
		this.mHandler = mHandler;
	}

	@Override
	public int getCount() {
		if (list != null) {
			return list.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		if (list != null) {
			return list.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		final ExpressAddressBean expressAddressBean = list.get(position);
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.express_address_item, null);
			viewHolder.check = (ImageView) convertView.findViewById(R.id.check);
			viewHolder.tag_name = (TextView) convertView.findViewById(R.id.tag_name);
			viewHolder.default_address = (TextView) convertView.findViewById(R.id.default_address);
			viewHolder.name = (TextView) convertView.findViewById(R.id.name);
			viewHolder.phone = (TextView) convertView.findViewById(R.id.phone);
			viewHolder.express_address = (TextView) convertView.findViewById(R.id.express_address);
			viewHolder.edit_layout = (LinearLayout) convertView.findViewById(R.id.edit_layout);
			viewHolder.delete = (ImageView) convertView.findViewById(R.id.delete);
			viewHolder.edit = (ImageView) convertView.findViewById(R.id.edit);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.check.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!expressAddressBean.getFlag().equals("1")) {
					SharedPreferences mSharedPreferences = context.getSharedPreferences(Constant.SHARED_PREFERENCE,
							Activity.MODE_PRIVATE);
					String tokenid = mSharedPreferences.getString(Constant.TOKEN_ID, "");
					NetProxyManager.getInstance().toUpdateMemberDefaultAddress(mHandler, tokenid,
							expressAddressBean.getId());
				}
			}
		});
		viewHolder.delete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SharedPreferences mSharedPreferences = context.getSharedPreferences(Constant.SHARED_PREFERENCE,
						Activity.MODE_PRIVATE);
				String tokenid = mSharedPreferences.getString(Constant.TOKEN_ID, "");
				// String saleId = mSharedPreferences.getString(Constant.ID,
				// "");
				deleteBean = expressAddressBean;
				NetProxyManager.getInstance().toDeleteMemberAddress(mHandler, tokenid, expressAddressBean.getId());
			}
		});
		viewHolder.edit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent();
				i.setClass(context, AddExpressActivity.class);
				i.putExtra("id", expressAddressBean.getId());
				i.putExtra("update", true);
				i.putExtra("tag", expressAddressBean.getTagName());
				i.putExtra("name", expressAddressBean.getName());
				i.putExtra("phone", expressAddressBean.getPhoneNumber());
				i.putExtra("area", expressAddressBean.getArea());
				i.putExtra("address", expressAddressBean.getAddress());
				i.putExtra("flag", expressAddressBean.getFlag());
				context.startActivityForResult(i, 0x1314);
			}
		});
		viewHolder.tag_name.setText(expressAddressBean.getTagName());
		viewHolder.name.setText(expressAddressBean.getName());
		viewHolder.phone.setText(expressAddressBean.getPhoneNumber());
		viewHolder.express_address.setText(expressAddressBean.getArea() + expressAddressBean.getAddress());
//		if (isSelect) {
//			viewHolder.edit_layout.setVisibility(View.GONE);
//			viewHolder.default_address.setVisibility(View.GONE);
//		} else {
			viewHolder.edit_layout.setVisibility(View.VISIBLE);
			if (expressAddressBean.getFlag().equals("1")) {
				viewHolder.check.setImageResource(R.drawable.checkbox_circle_checked);
				viewHolder.default_address.setVisibility(View.VISIBLE);
			} else {
				viewHolder.check.setImageResource(R.drawable.checkbox_circle);
				viewHolder.default_address.setVisibility(View.GONE);
			}
//		}
			LogUtil.d("lyp", expressAddressBean.getTagName() + expressAddressBean.getFlag());
		return convertView;
	}

	public ExpressAddressBean getDeleteBean() {
		return deleteBean;
	}

	public void setDeleteBean(ExpressAddressBean deleteBean) {
		this.deleteBean = deleteBean;
	}

	public class ViewHolder {
		public ImageView check;
		public TextView tag_name;
		public TextView default_address;
		public TextView name;
		public TextView phone;
		public TextView express_address;
		public LinearLayout edit_layout;
		public ImageView edit;
		public ImageView delete;
	}
}
