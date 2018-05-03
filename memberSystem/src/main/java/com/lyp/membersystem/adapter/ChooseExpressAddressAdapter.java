package com.lyp.membersystem.adapter;

import java.util.HashMap;
import java.util.List;

import com.lyp.membersystem.R;
import com.lyp.membersystem.bean.CardEnvelopBean;
import com.lyp.membersystem.bean.ExpressAddressBean;
import com.lyp.membersystem.net.NetProxyManager;
import com.lyp.membersystem.ui.AddExpressActivity;
import com.lyp.membersystem.utils.Constant;
import com.lyp.membersystem.utils.ToastUtil;

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

public class ChooseExpressAddressAdapter extends BaseAdapter {

	private List<ExpressAddressBean> list;
	private Activity context;
	private boolean isSelect = false;
	private Handler mHandler;
	private ExpressAddressBean deleteBean;
	// 用来记录显示的checkBox是否是选中的状态
	public HashMap<Integer, Boolean> ischeck;
	// 选中的
	private int selectIndex;

	public ChooseExpressAddressAdapter(List<ExpressAddressBean> list, Activity context, Handler mHandler,
			boolean isSelect) {
		super();
		this.list = list;
		this.context = context;
		this.isSelect = isSelect;
		this.mHandler = mHandler;
		ischeck = new HashMap<Integer, Boolean>();
		for (int i = 0; i < list.size(); i++) {
			if (i == 0) {
				ischeck.put(i, true);
				selectIndex = i;
			} else {
				ischeck.put(i, false);
			}
		}
	}

	/**
	 * 当ListView数据发生变化时,调用此方法来更新ListView
	 *
	 * @param list
	 */
	public void updateListView(List<ExpressAddressBean> list) {
		this.list = list;
		for (int i = 0; i < list.size(); i++) {
			if (i == 0) {
				ischeck.put(i, true);
				selectIndex = i;
			} else {
				ischeck.put(i, false);
			}
		}
		notifyDataSetChanged();
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
			viewHolder.check.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (!ischeck.get(position)) {
						selectIndex = position;
						for (int i = 0; i < list.size(); i++) {
							if (i == position) {
								ischeck.put(i, true);
							} else {
								ischeck.put(i, false);
							}
						}
						notifyDataSetChanged();
					}
				}
			});
			viewHolder.tag_name = (TextView) convertView.findViewById(R.id.tag_name);
			viewHolder.default_address = (TextView) convertView.findViewById(R.id.default_address);
			viewHolder.name = (TextView) convertView.findViewById(R.id.name);
			viewHolder.phone = (TextView) convertView.findViewById(R.id.phone);
			viewHolder.express_address = (TextView) convertView.findViewById(R.id.express_address);
			viewHolder.edit_layout = (LinearLayout) convertView.findViewById(R.id.edit_layout);
			viewHolder.delete = (ImageView) convertView.findViewById(R.id.delete);
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
			viewHolder.edit = (ImageView) convertView.findViewById(R.id.edit);
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
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.tag_name.setText(expressAddressBean.getTagName());
		viewHolder.name.setText(expressAddressBean.getName());
		viewHolder.phone.setText(expressAddressBean.getPhoneNumber());
		viewHolder.express_address.setText(expressAddressBean.getArea() + expressAddressBean.getAddress());
		viewHolder.edit_layout.setVisibility(View.GONE);
		viewHolder.default_address.setVisibility(View.GONE);
		if (ischeck.get(position)) {
			viewHolder.check.setImageResource(R.drawable.checkbox_circle_checked);
		} else {
			viewHolder.check.setImageResource(R.drawable.checkbox_circle);
		}
		return convertView;
	}

	public ExpressAddressBean getDeleteBean() {
		return deleteBean;
	}

	public void setDeleteBean(ExpressAddressBean deleteBean) {
		this.deleteBean = deleteBean;
	}

	public int getSelectIndex() {
		return selectIndex;
	}

	public void setSelectIndex(int selectIndex) {
		this.selectIndex = selectIndex;
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
