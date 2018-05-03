package com.lyp.membersystem.utils;

import android.os.Environment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;


public class FileStorage {
	private static final String PHOTO_DATE_FORMAT = "'AVATER'_yyyyMMdd_HHmmss";
    private File cropIconDir;
    private File iconDir;

    public FileStorage() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            File external = Environment.getExternalStorageDirectory();
            String rootDir = "/" + "membersystem";
            cropIconDir = new File(external, rootDir);
            if (!cropIconDir.exists()) {
                cropIconDir.mkdirs();

            }
            iconDir = new File(external, rootDir);
            if (!iconDir.exists()) {
                iconDir.mkdirs();

            }
        }
    }

    public File createCropFile() {
        String fileName = "";
        if (cropIconDir != null) {
            fileName = generateTempPhotoFileName();
        }
        return new File(cropIconDir, fileName);
    }

    public File createIconFile() {
        String fileName = "";
        if (iconDir != null) {
            fileName = generateTempPhotoFileName();
        }
        return new File(iconDir, fileName);
    }
    
    private String generateTempPhotoFileName() {
  		Date date = new Date(System.currentTimeMillis());
  		SimpleDateFormat dateFormat = new SimpleDateFormat(PHOTO_DATE_FORMAT);
  		return dateFormat.format(date) + ".jpg";
  	}
}
