package com.lyp.membersystem.imagepicker;

import java.util.ArrayList;
import java.util.List;

import com.lyp.membersystem.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class ChooseImageAdapter extends CommonAdapter<String> {

	/**
	 * 用户选择的图片，存储为图片的完整路径
	 */
	public ArrayList<String> mSelectedImage = new ArrayList<String>();

	/**
	 * 文件夹路径
	 */
	private String mDirPath;
	
	private Activity activity;
	
	private boolean mIsRadio = false;

	public ChooseImageAdapter(Activity context, List<String> mDatas, int itemLayoutId,
			String dirPath, boolean isRadio) {
		super(context, mDatas, itemLayoutId);
		activity = context;
		this.mDirPath = dirPath;
		this.mIsRadio = isRadio;
	}

	@Override
	public void convert(
			final com.lyp.membersystem.imagepicker.CommonAdapter.ViewHolder helper,
			final String item) {
		// 设置no_pic
		helper.setImageResource(R.id.id_item_image, R.drawable.pictures_no);
		if (mIsRadio) {
			helper.setImageViewDisplay(R.id.id_item_select, View.GONE);
		} else {
			helper.setImageViewDisplay(R.id.id_item_select, View.VISIBLE);
		// 设置no_selected
		    helper.setImageResource(R.id.id_item_select,
				    R.drawable.picture_unselected);
		}
		// 设置图片
		helper.setImageByUrl(R.id.id_item_image, mDirPath + "/" + item);

		final ImageView mImageView = helper.getView(R.id.id_item_image);
		final ImageView mSelect = helper.getView(R.id.id_item_select);

		mImageView.setColorFilter(null);
		// 设置ImageView的点击事件
		mImageView.setOnClickListener(new OnClickListener() {
			// 选择，则将图片变暗，反之则反之
			@Override
			public void onClick(View v) {

				if (mIsRadio) {
					mSelectedImage.add(mDirPath + "/" + item);
					Intent intent = new Intent();
					intent.putExtra("choose_images", mSelectedImage);
					activity.setResult(Activity.RESULT_OK, intent);
					activity.finish();
					return;
				}
				
				// 已经选择过该图片
				if (mSelectedImage.contains(mDirPath + "/" + item)) {
					mSelectedImage.remove(mDirPath + "/" + item);
					mSelect.setImageResource(R.drawable.picture_unselected);
					mImageView.setColorFilter(null);
					((ChooseImageActivity)activity).setChooseCount(mSelectedImage.size());
				} else
				// 未选择该图片
				{
					mSelectedImage.add(mDirPath + "/" + item);
					mSelect.setImageResource(R.drawable.pictures_selected);
					mImageView.setColorFilter(Color.parseColor("#77000000"));
					((ChooseImageActivity)activity).setChooseCount(mSelectedImage.size());
				}

			}
		});

		/**
		 * 已经选择过的图片，显示出选择过的效果
		 */
		if (mSelectedImage.contains(mDirPath + "/" + item)) {
			mSelect.setImageResource(R.drawable.pictures_selected);
			mImageView.setColorFilter(Color.parseColor("#77000000"));
		}

	}

	public ArrayList<String> getmSelectedImage() {
		return mSelectedImage;
	}

}
