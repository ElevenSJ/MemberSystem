package com.sj.widgets;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.lyp.membersystem.R;
import com.lyp.membersystem.base.BaseApplication;
import com.sj.utils.DisplayUtil;

import java.util.List;

/**
 * 创建时间: on 2018/6/17.
 * 创建人: 孙杰
 * 功能描述:
 */

public class TitlePopAdapter extends BaseAdapter {
    private List<String> content;
    private Context mCtx;
    private LayoutParams layoutParams;
    private int horizontalSpacing;
    private int paddingSpacing;
    private List<String> checkStrs;

    public TitlePopAdapter(Context ctx) {
        this.mCtx = ctx;
        horizontalSpacing = DisplayUtil.dip2px(ctx, 10) * 3;
        paddingSpacing = DisplayUtil.dip2px(ctx, 20);
    }

    public TitlePopAdapter(Context ctx, List<String> content) {
        this(ctx);
        this.content = content;
    }

    public void setData(List<String> content,List<String> checkList){
        this.content = content;
        this.checkStrs=checkList;
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return content == null?0:content.size();
    }

    @Override
    public String getItem(int position) {
        // TODO Auto-generated method stub
        return content.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mCtx).inflate(R.layout.title_pop_item, parent, false);
            holder.textView = (TextView) convertView.findViewById(R.id.tv_title);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        layoutParams = (LayoutParams) holder.textView.getLayoutParams();
        layoutParams.height = LayoutParams.WRAP_CONTENT;
        layoutParams.width = (BaseApplication.getInstance().getWidth(1f)- paddingSpacing - horizontalSpacing) / 4;
        holder.textView.setLayoutParams(layoutParams);
        holder.textView.setText(getItem(position));
        if (checkStrs.contains(getItem(position))) {
            convertView.setBackgroundResource(R.drawable.shape_circle_button_main_color);
            holder.textView.setTextColor(mCtx.getResources().getColor(R.color.main_bg_color));
        }else{
            holder.textView.setTextColor(mCtx.getResources().getColor(R.color.gray));
            convertView.setBackgroundResource(R.drawable.shape_circle_button_gray);
        }
        return convertView;
    }

    public class ViewHolder {
        TextView textView;
    }

    public void setChecked(){
        notifyDataSetChanged();
    }
}
