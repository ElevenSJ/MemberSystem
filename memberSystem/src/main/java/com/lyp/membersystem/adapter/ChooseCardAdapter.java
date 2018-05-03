package com.lyp.membersystem.adapter;

import java.util.HashMap;
import java.util.List;

import com.lyp.membersystem.R;
import com.lyp.membersystem.bean.CardEnvelopBean;
import com.lyp.membersystem.manager.ImageManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

@SuppressLint("UseSparseArrays")
public class ChooseCardAdapter extends BaseAdapter {
	private List<CardEnvelopBean> list = null;
	private Context mContext;
	// 选中的卡片
	private int selectCard;
	// 用来记录显示的checkBox是否是选中的状态
	public HashMap<Integer, Boolean> ischeck;

	public ChooseCardAdapter(Context mContext, List<CardEnvelopBean> list) {
		this.mContext = mContext;
		this.list = list;
		ischeck = new HashMap<Integer, Boolean>();
		for (int i = 0; i < list.size(); i++) {
			if (i == 0) {
				ischeck.put(i, true);
				selectCard = i;
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
	public void updateListView(List<CardEnvelopBean> list) {
		this.list = list;
		for (int i = 0; i < list.size(); i++) {
			if (i == 0) {
				ischeck.put(i, true);
				selectCard = i;
			} else {
				ischeck.put(i, false);
			}
		}
		notifyDataSetChanged();
	}

	public int getCount() {
		return this.list.size();
	}

	public Object getItem(int position) {
		return list.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View convertView, ViewGroup arg2) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_choose_card, null);
			viewHolder = new ViewHolder();
			viewHolder.card_select = (ImageView) convertView.findViewById(R.id.card_select);
			viewHolder.card_img = (ImageView) convertView.findViewById(R.id.card_img);
			viewHolder.card_name = ((TextView) convertView.findViewById(R.id.card_name));
			viewHolder.front_card = ((TextView) convertView.findViewById(R.id.front_card));
			viewHolder.back_card = ((TextView) convertView.findViewById(R.id.back_card));
			viewHolder.envelop = ((TextView) convertView.findViewById(R.id.envelop));
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		final CardEnvelopBean cardEnvelopBean = list.get(position);

		viewHolder.card_select.setImageResource(
				ischeck.get(position) ? R.drawable.checkbox_circle_checked : R.drawable.checkbox_circle);
		viewHolder.card_select.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!ischeck.get(position)) {
					selectCard = position;
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
		viewHolder.card_name.setText(cardEnvelopBean.getName());
		final ImageView cardImg = viewHolder.card_img;
		final TextView tv1 = viewHolder.front_card;
		final TextView tv2 = viewHolder.back_card;
		final TextView tv3 = viewHolder.envelop;
		if (cardEnvelopBean.getCardShowImg() == 0) {
			ImageManager.loadDefaultImage(cardEnvelopBean.getCardFrontFile(), cardImg);
			tv1.setBackgroundResource(R.drawable.btn_sm_green);
			tv2.setBackgroundResource(R.drawable.btn_grey);
			tv3.setBackgroundResource(R.drawable.btn_grey);
		} else if (cardEnvelopBean.getCardShowImg() == 1) {
			ImageManager.loadDefaultImage(cardEnvelopBean.getCardVersoFile(), cardImg);
			cardEnvelopBean.setCardShowImg(1);
			tv1.setBackgroundResource(R.drawable.btn_grey);
			tv2.setBackgroundResource(R.drawable.btn_sm_green);
			tv3.setBackgroundResource(R.drawable.btn_grey);
		} else {
			ImageManager.loadDefaultImage(cardEnvelopBean.getCardEnvelop(), cardImg);
			cardEnvelopBean.setCardShowImg(2);
			tv1.setBackgroundResource(R.drawable.btn_grey);
			tv2.setBackgroundResource(R.drawable.btn_grey);
			tv3.setBackgroundResource(R.drawable.btn_sm_green);
		}
		viewHolder.front_card.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ImageManager.loadDefaultImage(cardEnvelopBean.getCardFrontFile(), cardImg);
				cardEnvelopBean.setCardShowImg(0);
				tv1.setBackgroundResource(R.drawable.btn_sm_green);
				tv2.setBackgroundResource(R.drawable.btn_grey);
				tv3.setBackgroundResource(R.drawable.btn_grey);
			}
		});
		viewHolder.back_card.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ImageManager.loadDefaultImage(cardEnvelopBean.getCardVersoFile(), cardImg);
				cardEnvelopBean.setCardShowImg(1);
				tv1.setBackgroundResource(R.drawable.btn_grey);
				tv2.setBackgroundResource(R.drawable.btn_sm_green);
				tv3.setBackgroundResource(R.drawable.btn_grey);
			}
		});
		viewHolder.envelop.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ImageManager.loadDefaultImage(cardEnvelopBean.getCardEnvelop(), cardImg);
				cardEnvelopBean.setCardShowImg(2);
				tv1.setBackgroundResource(R.drawable.btn_grey);
				tv2.setBackgroundResource(R.drawable.btn_grey);
				tv3.setBackgroundResource(R.drawable.btn_sm_green);
			}
		});
		return convertView;

	}

	public int getSelectCard() {
		return selectCard;
	}

	public void setSelectCard(int selectCard) {
		this.selectCard = selectCard;
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}

	public class ViewHolder {
		public TextView card_name;
		public ImageView card_select;
		public ImageView card_img;
		public TextView front_card;
		public TextView back_card;
		public TextView envelop;
	}
}