package com.lyp.membersystem.adapter;

import java.util.ArrayList;
import java.util.List;

import com.lyp.membersystem.R;
import com.lyp.membersystem.bean.ImagePageBean;
import com.lyp.membersystem.log.LogUtils;
import com.lyp.membersystem.manager.ImageManager;
import com.lyp.membersystem.utils.DisplayUtil;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {

	private Context mContext;
	private int mGalleryItemBackground;
	private DisplayUtil mDisplayUtil;
	private List<ImagePageBean> mImagePageList;

	public ImageAdapter(Context context) {
		mContext = context;
		mImagePageList = new ArrayList<ImagePageBean>();
		mDisplayUtil = new DisplayUtil(mContext);
		// 　获得Gallery组件的属性
		TypedArray typedArray = context
				.obtainStyledAttributes(R.styleable.Gallery);
		mGalleryItemBackground = typedArray.getResourceId(
				R.styleable.Gallery_android_galleryItemBackground, 0);
		/* 让对象的styleable属性能够反复使用 */
		typedArray.recycle();
	}

	@Override
	public int getCount() {
		if (mImagePageList == null) {
			return 0;
		}
		return mImagePageList.size();
	}

	@Override
	public Object getItem(int position) {
		if (mImagePageList == null) {
			return null;
		}
		return mImagePageList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public List<ImagePageBean> getmImagePageList() {
		return mImagePageList;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView iv = new ImageView(mContext);
//		iv.setImageResource(mGalleryItemBackground);
//		iv.setScaleType(ImageView.ScaleType.FIT_XY);
		iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
		int height = mDisplayUtil.dipToPx(270);
		int width = mDisplayUtil.getScreenWidth();
		if (width > 0) {
			height = width * 2/3;
		}
		iv.setLayoutParams(new Gallery.LayoutParams(width, height));
		String uri = mImagePageList.get(position % mImagePageList.size()).getUri();
		LogUtils.d("lyp", width + "x" + height + ": " + uri);
		ImageManager.loadImage(uri, iv, R.drawable.default_image);
		return iv;
	}

}
