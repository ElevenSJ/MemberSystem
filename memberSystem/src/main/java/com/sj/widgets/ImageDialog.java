package com.sj.widgets;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import com.lyp.membersystem.R;
import com.sj.utils.ImageUtils;
import com.sj.widgets.photoview.PhotoView;

/**
 * 创建时间: on 2018/4/1.
 * 创建人: 孙杰
 * 功能描述:ImageDialog
 */
public class ImageDialog extends Dialog {

    private boolean isCancelable = false;
    private boolean isCanceledOnTouchOutside = false;

    PhotoView photoView;
    Context mContext;
    public ImageDialog(Context context) {
        this(context, R.style.dialog);
    }


    public ImageDialog(Context context, int theme) {
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

        photoView = new PhotoView(false,context);
        setContentView(photoView);
    }

    public void show(String imagePath) {
        this.show();
        ImageUtils.loadImageView(imagePath,photoView);
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

}