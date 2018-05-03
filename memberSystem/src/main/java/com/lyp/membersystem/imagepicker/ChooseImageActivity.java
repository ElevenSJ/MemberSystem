package com.lyp.membersystem.imagepicker;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import com.lyp.membersystem.R;
import com.lyp.membersystem.imagepicker.ListImageDirPopupWindow.OnImageDirSelected;
import com.lyp.membersystem.utils.SystemStatusManager;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ChooseImageActivity extends Activity implements OnImageDirSelected {
	private static final String TAG = ChooseImageActivity.class.getSimpleName();
	private ProgressDialog mProgressDialog;
	private boolean mIsRadio = false;// 是否单选

	/**
	 * 存储文件夹中的图片数量
	 */
	private int mPicsSize;
	/**
	 * 图片数量最多的文件夹
	 */
	private File mImgDir;
	/**
	 * 所有的图片
	 */
	private List<String> mImgs;

	private GridView mGirdView;
	private ChooseImageAdapter mAdapter;
	/**
	 * 临时的辅助类，用于防止同一个文件夹的多次扫描
	 */
	private HashSet<String> mDirPaths = new HashSet<String>();

	/**
	 * 扫描拿到所有的图片文件夹
	 */
	private List<ImageFloder> mImageFloders = new ArrayList<ImageFloder>();

	private RelativeLayout mBottomLy;

	private TextView mChooseDir;
	private TextView mImageCount;
	private TextView has_choose_tv;
	int totalCount = 0;

	private int mScreenHeight;

	private ListImageDirPopupWindow mListImageDirPopupWindow;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			mProgressDialog.dismiss();
			// 为View绑定数据
			data2View();
			// 初始化展示文件夹的popupWindw
			initListDirPopupWindw();
		}
	};

	/**
	 * 为View绑定数据
	 */
	private void data2View() {
		if (mImgDir == null) {
			Toast.makeText(getApplicationContext(), "一张图片没扫描到", Toast.LENGTH_SHORT).show();
			return;
		}

		mImgs = Arrays.asList(mImgDir.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String filename) {
				if (filename.toLowerCase().endsWith(".jpg") || filename.toLowerCase().endsWith(".png")
						|| filename.toLowerCase().endsWith(".jpeg"))
					return true;
				return false;
			}
		}));
		/**
		 * 可以看到文件夹的路径和图片的路径分开保存，极大的减少了内存的消耗；
		 */
		mAdapter = new ChooseImageAdapter(this, mImgs, R.layout.choose_image_grid_item, mImgDir.getAbsolutePath(),
				mIsRadio);
		mGirdView.setAdapter(mAdapter);
	};

	/**
	 * 初始化展示文件夹的popupWindw
	 */
	private void initListDirPopupWindw() {
		mListImageDirPopupWindow = new ListImageDirPopupWindow(LayoutParams.MATCH_PARENT, (int) (mScreenHeight * 0.7),
				mImageFloders, LayoutInflater.from(getApplicationContext()).inflate(R.layout.list_dir, null));

		mListImageDirPopupWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				// 设置背景颜色变暗
				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.alpha = 1.0f;
				getWindow().setAttributes(lp);
			}
		});
		// 设置选择文件夹的回调
		mListImageDirPopupWindow.setOnImageDirSelected(this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTranslucentStatus();
		setContentView(R.layout.choose_image_layout);
		mIsRadio = getIntent().getBooleanExtra("radio", false);
		DisplayMetrics outMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
		mScreenHeight = outMetrics.heightPixels;

		initView();
		getImages();
		initEvent();

	}

	/**
	 * 利用ContentProvider扫描手机中的图片，此方法在运行在子线程中 完成图片的扫描，最终获得jpg最多的那个文件夹
	 */
	private void getImages() {
		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			Toast.makeText(this, "暂无外部存储", Toast.LENGTH_SHORT).show();
			return;
		}
		// 显示进度条
		mProgressDialog = ProgressDialog.show(this, null, "正在加载...");

		new Thread(new Runnable() {
			@Override
			public void run() {

				String firstImage = null;

				Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				ContentResolver mContentResolver = ChooseImageActivity.this.getContentResolver();

				// 只查询jpeg和png的图片
				Cursor mCursor = mContentResolver.query(mImageUri, null,
						MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=?",
						new String[] { "image/jpeg", "image/png" }, MediaStore.Images.Media.DATE_MODIFIED);

				Log.e(TAG, mCursor.getCount() + "");
				while (mCursor.moveToNext()) {
					// 获取图片的路径
					String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA));

					Log.d(TAG, path);

					// 拿到第一张图片的路径
					if (firstImage == null)
						firstImage = path;
					// 获取该图片的父路径名
					File parentFile = new File(path).getParentFile();
					if (parentFile == null)
						continue;
					String dirPath = parentFile.getAbsolutePath();
					ImageFloder imageFloder = null;
					// 利用一个HashSet防止多次扫描同一个文件夹（不加这个判断，图片多起来还是相当恐怖的~~）
					if (mDirPaths.contains(dirPath)) {
						continue;
					} else {
						mDirPaths.add(dirPath);
						// 初始化imageFloder
						imageFloder = new ImageFloder();
						imageFloder.setDir(dirPath);
						imageFloder.setFirstImagePath(path);
					}
					if (parentFile.list() == null) {
						Log.e(TAG, "数据库脏数据 parentFile: " + parentFile.getAbsolutePath());
						continue;
					}
					int picSize = parentFile.list(new FilenameFilter() {
						@Override
						public boolean accept(File dir, String filename) {
							if (filename.toLowerCase().endsWith(".jpg") || filename.toLowerCase().endsWith(".png")
									|| filename.toLowerCase().endsWith(".jpeg"))
								return true;
							return false;
						}
					}).length;
					totalCount += picSize;

					imageFloder.setCount(picSize);
					if (picSize <= 0) {
						continue;
					}
					mImageFloders.add(imageFloder);

					if (picSize > mPicsSize) {
						mPicsSize = picSize;
						mImgDir = parentFile;
					}
				}
				mCursor.close();

				// 扫描完成，辅助的HashSet也就可以释放内存了
				mDirPaths = null;

				// 通知Handler扫描图片完成
				mHandler.sendEmptyMessage(0x110);

			}
		}).start();

	}

	/**
	 * 初始化View
	 */
	private void initView() {
		mGirdView = (GridView) findViewById(R.id.id_gridView);
		mChooseDir = (TextView) findViewById(R.id.id_choose_dir);
		mImageCount = (TextView) findViewById(R.id.choose_count);
		has_choose_tv = (TextView) findViewById(R.id.has_choose_tv);
		View btn_ok = findViewById(R.id.btn_ok);
		if (mIsRadio) {
			btn_ok.setVisibility(View.GONE);
		} else {
			btn_ok.setVisibility(View.VISIBLE);
			btn_ok.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					chooseOk();
				}
			});
		}
		mBottomLy = (RelativeLayout) findViewById(R.id.id_bottom_ly);

	}

	private void initEvent() {
		if (mIsRadio) {
//			mBottomLy.setVisibility(View.GONE);
			mImageCount.setVisibility(View.INVISIBLE);
			has_choose_tv.setVisibility(View.INVISIBLE);
//			return;
		} else {
//		    mBottomLy.setVisibility(View.VISIBLE);
		    mImageCount.setVisibility(View.VISIBLE);
		    has_choose_tv.setVisibility(View.VISIBLE);
		}
		/**
		 * 为底部的布局设置点击事件，弹出popupWindow
		 */
		mBottomLy.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mListImageDirPopupWindow.setAnimationStyle(R.style.anim_popup_dir);
				mListImageDirPopupWindow.showAsDropDown(mBottomLy, 0, 0);

				// 设置背景颜色变暗
				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.alpha = .3f;
				getWindow().setAttributes(lp);
			}
		});
	}

	@Override
	public void selected(ImageFloder floder) {

		mImgDir = new File(floder.getDir());
		mImgs = Arrays.asList(mImgDir.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String filename) {
				if (filename.toLowerCase().endsWith(".jpg") || filename.toLowerCase().endsWith(".png")
						|| filename.toLowerCase().endsWith(".jpeg"))
					return true;
				return false;
			}
		}));
		/**
		 * 可以看到文件夹的路径和图片的路径分开保存，极大的减少了内存的消耗；
		 */
		mAdapter = new ChooseImageAdapter(this, mImgs, R.layout.choose_image_grid_item, mImgDir.getAbsolutePath(),
				mIsRadio);
		mGirdView.setAdapter(mAdapter);
		// mAdapter.notifyDataSetChanged();
		mChooseDir.setText(floder.getName());
		mListImageDirPopupWindow.dismiss();

	}

	public void setChooseCount(int count) {
		mImageCount.setText(String.valueOf(count));
	}

	public void chooseBack(View view) {
		finish();
	}

	public void chooseOk() {
		Intent intent = new Intent();
		intent.putExtra("choose_images", mAdapter.getmSelectedImage());
		setResult(RESULT_OK, intent);
		finish();
	}

	public boolean isImage(String filename) {
		if (filename.toLowerCase().endsWith(".jpg") || filename.toLowerCase().endsWith(".png")
				|| filename.toLowerCase().endsWith(".jpeg"))
			return true;
		return false;
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
}
