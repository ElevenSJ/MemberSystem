package com.sj.widgets.downloadview;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.decoration.DividerDecoration;
import com.lyp.membersystem.R;
import com.sj.activity.adapter.ForumRyvAdapter;
import com.sj.activity.bean.StudyHtmlCommonBean;
import com.sj.utils.ImageUtils;
import com.sj.widgets.photoview.PhotoView;

import java.util.List;

/**
 * 创建时间: on 2018/4/1.
 * 创建人: 孙杰
 * 功能描述:ImageDialog
 */
public class DownloadDialog extends Dialog {

    private boolean isCancelable = false;
    private boolean isCanceledOnTouchOutside = false;

    EasyRecyclerView rylView;
    Context mContext;
    DownloadAdapter mAdapter;
    public DownloadDialog(Context context) {
        this(context, R.style.transdialog);
    }


    public DownloadDialog(Context context, int theme) {
        super(context, theme);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(mContext);
    }

    private void init(Context context) {
        //设置不可取消，点击其他区域不能取消，实际中可以抽出去封装供外包设置
        setCancelable(isCancelable);
        setCanceledOnTouchOutside(isCanceledOnTouchOutside);
        rylView = new EasyRecyclerView(context);
        setContentView(rylView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        rylView.setLayoutManager(layoutManager);
        DividerDecoration dividerDecoration = new DividerDecoration(context.getResources().getColor(R.color.item_line_color), 1, 16, 16);
        dividerDecoration.setDrawLastItem(false);
        rylView.addItemDecoration(dividerDecoration);
        mAdapter = new DownloadAdapter(context);
        rylView.setAdapter(mAdapter);
    }

    public void show(List<StudyHtmlCommonBean.AttachsBean> datas) {
        this.show();
        mAdapter.clear();
        mAdapter.addAll(datas);
    }

    @Override
    public void show() {
        super.show();
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.gravity = Gravity.CENTER;
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        getWindow().getDecorView().setPadding(60, 60, 60, 60);
        getWindow().setAttributes(layoutParams);
    }

    @Override
    public void setCancelable(boolean cancelable) {
        isCancelable = cancelable;
    }

    @Override
    public void setCanceledOnTouchOutside(boolean canceledOnTouchOutside) {
        isCanceledOnTouchOutside = canceledOnTouchOutside;
    }

    public void setData(List<StudyHtmlCommonBean.AttachsBean> attachs) {
        mAdapter.clear();
        mAdapter.addAll(attachs);
    }
}