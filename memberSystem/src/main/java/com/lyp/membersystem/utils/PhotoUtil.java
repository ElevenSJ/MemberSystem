package com.lyp.membersystem.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import com.lyp.membersystem.log.LogUtils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Log;

public class PhotoUtil {
	private Activity activity;
	private Fragment fragment;
	private Uri imageUri;
	private String imagePath;

	public PhotoUtil(Activity activity) {
		this.activity = activity;
	}

	public PhotoUtil(Fragment fragment) {
		this.activity = fragment.getActivity();
		this.fragment = fragment;
	}

	public Uri takePhoto(int requestCode) {
		File file = new FileStorage().createIconFile();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			imageUri = FileProvider.getUriForFile(activity, "com.lyp.membersystem.provider", file);// 通过FileProvider创建一个content类型的Uri
		} else {
			imageUri = Uri.fromFile(file);
		}
		Intent intent = new Intent();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // 添加这一句表示对目标应用临时授权该Uri所代表的文件
		}
		intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);// 设置Action为拍照
		intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);// 将拍取的照片保存到指定URI
		activity.startActivityForResult(intent, requestCode);

		return imageUri;
	}

	public void callPhoto(int requestCode) {
		Intent intent = new Intent();
		 intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
		 "image/*");
		 intent.setAction(Intent.ACTION_PICK);
//		intent.setType("image/*");
//		intent.setAction(Intent.ACTION_GET_CONTENT);
		activity.startActivityForResult(intent, requestCode);
	}

	public String getCallPhoto(Intent intent) {
		handleImageOnKitKat(intent);
		return imagePath;
	}

	@TargetApi(19)
	private void handleImageOnKitKat(Intent data) {
		imagePath = null;
		if (data == null)
			return;
		imageUri = data.getData();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			if (DocumentsContract.isDocumentUri(activity, imageUri)) {
				// 如果是document类型的uri,则通过document id处理
				String docId = DocumentsContract.getDocumentId(imageUri);
				if ("com.android.providers.media.documents".equals(imageUri.getAuthority())) {
					String id = docId.split(":")[1];// 解析出数字格式的id
					String selection = MediaStore.Images.Media._ID + "=" + id;
					imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
				} else if ("com.android.downloads.documents".equals(imageUri.getAuthority())) {
					Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),
							Long.valueOf(docId));
					imagePath = getImagePath(contentUri, null);
				}
			} else if ("content".equalsIgnoreCase(imageUri.getScheme())) {
				// 如果是content类型的Uri，则使用普通方式处理
				imagePath = getImagePath(imageUri, null);
			} else if ("file".equalsIgnoreCase(imageUri.getScheme())) {
				// 如果是file类型的Uri,直接获取图片路径即可
				imagePath = imageUri.getPath();
			}
		} else {
			imageUri = data.getData();
			imagePath = getImagePath(imageUri, null);
		}
	}

	private String getImagePath(Uri uri, String selection) {
		String path = null;
		// 通过Uri和selection老获取真实的图片路径
		Cursor cursor = activity.getContentResolver().query(uri, null, selection, null, null);
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
			}
			cursor.close();
		}
		return path;
	}

	public String startPhotoZoom(int requestCode, String imagePath) {
		Uri imageUri = null;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
		    imageUri = FileProvider.getUriForFile(activity, "com.lyp.membersystem.provider", new File(imagePath));
		} else {
			imageUri = Uri.parse("file://" + imagePath);
		}
		LogUtils.d("imageUri: " + imageUri);
		return startPhotoZoom(requestCode, imageUri);
	}
	
	/**
	 * 裁剪图片方法实现
	 */
	public String startPhotoZoom(int requestCode, Uri imageUri) {
		LogUtils.d("imageUri！！！！: " + imageUri);
		Intent intent = new Intent("com.android.camera.action.CROP");
		File file = new FileStorage().createCropFile();
		// TODO:outputUri不需要ContentUri,否则失败
		Uri cropImageUri = Uri.fromFile(file);
		;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			// cropImageUri = FileProvider.getUriForFile(activity,
			// "com.lyp.membersystem.provider", file);//通过FileProvider创建一个content类型的Uri
			intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		} else {
			// cropImageUri = Uri.fromFile(file);
		}
		intent.setDataAndType(imageUri, "image/*");
		intent.putExtra("crop", "true");
		// 设置宽高比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// 设置裁剪图片宽高
		intent.putExtra("outputX", 200);
		intent.putExtra("outputY", 200);
		intent.putExtra("scale", true);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, cropImageUri);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true);// 去除默认的人脸识别，否则和剪裁匡重叠
		intent.putExtra("return-data", true);
		activity.startActivityForResult(intent, requestCode);// 这里就将裁剪后的图片的Uri返回了
		return file.getAbsolutePath();
	}
	
	public String startSignPhotoZoom(int requestCode, String imagePath) {
		Uri imageUri = null;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
		    imageUri = FileProvider.getUriForFile(activity, "com.lyp.membersystem.provider", new File(imagePath));
		} else {
			imageUri = Uri.parse("file://" + imagePath);
		}
		LogUtils.d("imageUri: " + imageUri);
		return startSignPhotoZoom(requestCode, imageUri);
	}
	
	public String startBusinessCardPhotoZoom(int requestCode, String imagePath) {
		Uri imageUri = null;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
		    imageUri = FileProvider.getUriForFile(activity, "com.lyp.membersystem.provider", new File(imagePath));
		} else {
			imageUri = Uri.parse("file://" + imagePath);
		}
		LogUtils.d("imageUri: " + imageUri);
		return startBusinessCardPhotoZoom(requestCode, imageUri);
	}
	
	/**
	 * 裁剪签名图片方法实现
	 */
	public String startSignPhotoZoom(int requestCode, Uri imageUri) {
		LogUtils.d("imageUri！！！！: " + imageUri);
		Intent intent = new Intent("com.android.camera.action.CROP");
		File file = new FileStorage().createCropFile();
		// TODO:outputUri不需要ContentUri,否则失败
		Uri cropImageUri = Uri.fromFile(file);
		;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			// cropImageUri = FileProvider.getUriForFile(activity,
			// "com.lyp.membersystem.provider", file);//通过FileProvider创建一个content类型的Uri
			intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		} else {
			// cropImageUri = Uri.fromFile(file);
		}
		intent.setDataAndType(imageUri, "image/*");
		intent.putExtra("crop", "true");
		// 设置宽高比例
		intent.putExtra("aspectX", 5);
		intent.putExtra("aspectY", 1);
		// 设置裁剪图片宽高
		intent.putExtra("outputX", 200);
		intent.putExtra("outputY", 40);
		intent.putExtra("scale", true);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, cropImageUri);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true);// 去除默认的人脸识别，否则和剪裁匡重叠
		intent.putExtra("return-data", true);
		activity.startActivityForResult(intent, requestCode);// 这里就将裁剪后的图片的Uri返回了
		return file.getAbsolutePath();
	}
	
	/**
	 * 裁剪签名图片方法实现
	 */
	public String startBusinessCardPhotoZoom(int requestCode, Uri imageUri) {
		LogUtils.d("imageUri！！！！: " + imageUri);
		Intent intent = new Intent("com.android.camera.action.CROP");
		File file = new FileStorage().createCropFile();
		// TODO:outputUri不需要ContentUri,否则失败
		Uri cropImageUri = Uri.fromFile(file);
		;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			// cropImageUri = FileProvider.getUriForFile(activity,
			// "com.lyp.membersystem.provider", file);//通过FileProvider创建一个content类型的Uri
			intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		} else {
			// cropImageUri = Uri.fromFile(file);
		}
		intent.setDataAndType(imageUri, "image/*");
		intent.putExtra("crop", "true");
		// 设置宽高比例
		intent.putExtra("aspectX", 5);
		intent.putExtra("aspectY", 4);
		// 设置裁剪图片宽高
		intent.putExtra("outputX", 200);
		intent.putExtra("outputY", 160);
		intent.putExtra("scale", true);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, cropImageUri);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true);// 去除默认的人脸识别，否则和剪裁匡重叠
		intent.putExtra("return-data", true);
		activity.startActivityForResult(intent, requestCode);// 这里就将裁剪后的图片的Uri返回了
		return file.getAbsolutePath();
	}

	/**
	 * 图片按比例大小压缩方法
	 *
	 * @param image
	 *            （根据Bitmap图片压缩）
	 * @return
	 */
	public static Bitmap compressScale(Bitmap image) {
		Log.e("----------", image.getRowBytes() * image.getHeight() + "");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);

		// 判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
		if (baos.toByteArray().length / 1024 > 1024) {
			baos.reset();// 重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, 80, baos);// 这里压缩50%，把压缩后的数据存放到baos中
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		Log.i("------------", w + "---------------" + h);
		// 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
		// float hh = 800f;// 这里设置高度为800f
		// float ww = 480f;// 这里设置宽度为480f
		float hh = 240f;
		float ww = 600f;
		// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 1;// be=1表示不缩放
		if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) { // 如果高度高的话根据高度固定大小缩放
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be; // 设置缩放比例
	    newOpts.inPreferredConfig = Config.RGB_565;//降低图片从ARGB888到RGB565

		// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		isBm = new ByteArrayInputStream(baos.toByteArray());
		bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);

		return bitmap;// 压缩好比例大小后再进行质量压缩
	}
	
	public static String compressPicture(String srcPath) {
		File file = new FileStorage().createIconFile();
//		String desPath = file;
        FileOutputStream fos = null;  
        BitmapFactory.Options op = new BitmapFactory.Options();  

        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了  
        op.inJustDecodeBounds = true;  
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, op);  
        op.inJustDecodeBounds = false;  
  
        // 缩放图片的尺寸  
        float w = op.outWidth;  
        float h = op.outHeight;  
        float hh = 600f;// 压缩后高度
        float ww = 240f;// 压缩后宽度
        float be = 1.0f;  
        if (w > h && w > ww) {  // 如果宽度大的话根据宽度固定大小缩放
            be = (float) (w / ww);  
        } else if (w < h && h > hh) {  // 如果高度高的话根据高度固定大小缩放
            be = (float) (h / hh);  
        }
        if (be <= 0) {  
            be = 1.0f;  
        }
        if (be == 1.0f) {
        	return srcPath;
        }
        op.inSampleSize = (int) be;// 设置缩放比例,这个数字越大,图片大小越小.  
        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了  
        bitmap = BitmapFactory.decodeFile(srcPath, op);  
        int desWidth = (int) (w / be);  
        int desHeight = (int) (h / be);  
        bitmap = Bitmap.createScaledBitmap(bitmap, desWidth, desHeight, true);  
        try {  
            fos = new FileOutputStream(file);  
            if (bitmap != null) {  
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);  
            }
        } catch (FileNotFoundException e) {  
            e.printStackTrace();  
            return null;
        } finally {
        	if (bitmap != null && bitmap.isRecycled()) {
        	    bitmap.recycle();
        	    bitmap = null;
        	}
        }
        return file.getAbsolutePath();
    } 

	/**
	 * 质量压缩方法
	 *
	 * @param image
	 * @return
	 */
	public static Bitmap compressImage(Bitmap image) {

		int height = image.getHeight();
		int width = image.getWidth();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 90;

		while (baos.toByteArray().length / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
			baos.reset(); // 重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
			options -= 10;// 每次都减少10
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmap = BitmapFactory.decodeStream(isBm);// 把ByteArrayInputStream数据生成图片

		Log.e("----------", bitmap.getRowBytes() * bitmap.getHeight() + "");
		return bitmap;
	}

}
