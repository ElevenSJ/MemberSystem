/*
 *  Copyright (c) 2015 The CCP project authors. All Rights Reserved.
 *
 *  Use of this source code is governed by a Beijing Speedtong Information Technology Co.,Ltd license
 *  that can be found in the LICENSE file in the root of the web site.
 *
 *   http://www.yuntongxun.com
 *
 *  An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */package com.yuntongxun.ecdemo.common.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import com.lyp.membersystem.BuildConfig;
import com.lyp.membersystem.R;
import com.lyp.membersystem.base.BaseApplication;
import com.lyp.membersystem.provider.SZLSystemProvider;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;

/**
 * 文件操作工具类
 * ECSDK_Demo
 * Created by Jorstin on 2015/3/17.
 */
public class FileAccessor {


    public static final String TAG = FileAccessor.class.getName();
    public static String EXTERNAL_STOREPATH = getExternalStorePath();
    public static final String APPS_ROOT_DIR = getExternalStorePath() + "/membersystem";
    public static final String EXPORT_DIR = getExternalStorePath() + "/membersystem/ECDemo_IM";
    public static final String CAMERA_PATH = getExternalStorePath() + "/DCIM/membersystem";
    public static final String TACK_PIC_PATH = getExternalStorePath()+ "/membersystem/.tempchat";
    public static final String IMESSAGE_VOICE = getExternalStorePath() + "/membersystem/voice";
    public static final String IMESSAGE_IMAGE = getExternalStorePath() + "/membersystem/image";
    public static final String IMESSAGE_AVATAR = getExternalStorePath() + "/membersystem/avatar";
    public static final String IMESSAGE_FILE = getExternalStorePath() + "/membersystem/file";
    public static final String APP_AUDIO = getExternalStorePath() + "/membersystem/audio";
    public static final String IMESSAGE_RICH_TEXT = getExternalStorePath() + "/membersystem/richtext";
    public static final String LOCAL_PATH = APPS_ROOT_DIR + "/config.txt";

    //建立一个文件类型与文件后缀名的匹配表
    private static final String[][] MATCH_ARRAY={
            //{后缀名，    文件类型}
            {".3gp",    "video/3gpp"},
            {".apk",    "application/vnd.android.package-archive"},
            {".asf",    "video/x-ms-asf"},
            {".avi",    "video/x-msvideo"},
            {".bin",    "application/octet-stream"},
            {".bmp",      "image/bmp"},
            {".c",        "text/plain"},
            {".class",    "application/octet-stream"},
            {".conf",    "text/plain"},
            {".cpp",    "text/plain"},
            {".doc",    "application/msword"},
            {".exe",    "application/octet-stream"},
            {".gif",    "image/gif"},
            {".gtar",    "application/x-gtar"},
            {".gz",        "application/x-gzip"},
            {".h",        "text/plain"},
            {".htm",    "text/html"},
            {".html",    "text/html"},
            {".jar",    "application/java-archive"},
            {".java",    "text/plain"},
            {".jpeg",    "image/jpeg"},
            {".jpg",    "image/jpeg"},
            {".js",        "application/x-javascript"},
            {".log",    "text/plain"},
            {".m3u",    "audio/x-mpegurl"},
            {".m4a",    "audio/mp4a-latm"},
            {".m4b",    "audio/mp4a-latm"},
            {".m4p",    "audio/mp4a-latm"},
            {".m4u",    "video/vnd.mpegurl"},
            {".m4v",    "video/x-m4v"},
            {".mov",    "video/quicktime"},
            {".mp2",    "audio/x-mpeg"},
            {".mp3",    "audio/x-mpeg"},
            {".mp4",    "video/mp4"},
            {".mpc",    "application/vnd.mpohun.certificate"},
            {".mpe",    "video/mpeg"},
            {".mpeg",    "video/mpeg"},
            {".mpg",    "video/mpeg"},
            {".mpg4",    "video/mp4"},
            {".mpga",    "audio/mpeg"},
            {".msg",    "application/vnd.ms-outlook"},
            {".ogg",    "audio/ogg"},
            {".pdf",    "application/pdf"},
            {".png",    "image/png"},
            {".pps",    "application/vnd.ms-powerpoint"},
            {".ppt",    "application/vnd.ms-powerpoint"},
            {".prop",    "text/plain"},
            {".rar",    "application/x-rar-compressed"},
            {".rc",        "text/plain"},
            {".rmvb",    "audio/x-pn-realaudio"},
            {".rtf",    "application/rtf"},
            {".sh",        "text/plain"},
            {".tar",    "application/x-tar"},
            {".tgz",    "application/x-compressed"},
            {".txt",    "text/plain"},
            {".wav",    "audio/x-wav"},
            {".wma",    "audio/x-ms-wma"},
            {".wmv",    "audio/x-ms-wmv"},
            {".wps",    "application/vnd.ms-works"},
            {".xml",    "text/plain"},
            {".z",        "application/x-compress"},
            {".zip",    "application/zip"},
            {"",        "*/*"}
    };

    /**
     * 初始化应用文件夹目录
     */
    public static void initFileAccess() {
        File rootDir = new File(APPS_ROOT_DIR);
        if (!rootDir.exists()) {
            rootDir.mkdir();
        }

        File voiceDir = new File(IMESSAGE_VOICE);
        if (!voiceDir.exists()) {
            voiceDir.mkdir();
        }

        File imageDir = new File(IMESSAGE_IMAGE);
        if (!imageDir.exists()) {
            imageDir.mkdir();
        }

        File fileDir = new File(IMESSAGE_FILE);
        if (!fileDir.exists()) {
            fileDir.mkdir();
        }
        File avatarDir = new File(IMESSAGE_AVATAR);
        if (!avatarDir.exists()) {
            avatarDir.mkdir();
        }
        File richTextDir = new File(IMESSAGE_RICH_TEXT);
        if (!richTextDir.exists()) {
            richTextDir.mkdir();
        }
        File audioDir = new File(APP_AUDIO);
        if (!audioDir.exists()) {
            audioDir.mkdir();
        }

    }

    public static String getAppKey() {
    	
    	boolean existCustomServer = ECPreferences.getSharedPreferences().getBoolean(
				ECPreferenceSettings.SETTINGS_SERVER_CUSTOM.getId(),
				((Boolean) ECPreferenceSettings.SETTINGS_SERVER_CUSTOM
						.getDefaultValue()).booleanValue());
    	
    	if(existCustomServer){
    		
    		return ECPreferences.getSharedPreferences().
                    getString(ECPreferenceSettings.SETTINGS_CUSTOM_APPKEY.getId(),
                            (String) ECPreferenceSettings.SETTINGS_CUSTOM_APPKEY.getDefaultValue());
    	}
    	
    	
    	
    	
        if (isExistExternalStore()) {
            String content = readContentByFile(LOCAL_PATH);
            if (content != null) {
                try {
                    String result = content.split(",")[0];
                    if(result != null && result.contains("appkey=")) {
                        return result.replace("appkey=" , "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return getConfig(ECPreferenceSettings.SETTINGS_APPKEY);
    }

    public static String getAppToken() {
    	
    	
    	boolean existCustomServer = ECPreferences.getSharedPreferences().getBoolean(
				ECPreferenceSettings.SETTINGS_SERVER_CUSTOM.getId(),
				((Boolean) ECPreferenceSettings.SETTINGS_SERVER_CUSTOM
						.getDefaultValue()).booleanValue());
    	
    	if(existCustomServer){
    		
    		return ECPreferences.getSharedPreferences().
                    getString(ECPreferenceSettings.SETTINGS_CUSTOM_TOKEN.getId(),
                            (String) ECPreferenceSettings.SETTINGS_CUSTOM_TOKEN.getDefaultValue());
    	}
    	
        if (isExistExternalStore()) {
            String content = readContentByFile(LOCAL_PATH);
            if (content != null) {
                try {
                    String result = content.split(",")[1];
                    if(result != null && result.contains("apptoken=")) {
                        return result.replace("apptoken=" , "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return getConfig(ECPreferenceSettings.SETTINGS_TOKEN);
    }

    private static String getConfig(ECPreferenceSettings settings) {
        SharedPreferences sharedPreferences = ECPreferences.getSharedPreferences();
        String value = sharedPreferences.getString(settings.getId(), (String) settings.getDefaultValue());
        return value;
    }

    public static String readContentByFile(String path) {
        BufferedReader reader = null;
        String line = null;
        try {
            File file = new File(path);
            if (file.exists()) {
                StringBuilder sb = new StringBuilder();
                reader = new BufferedReader(new FileReader(file));
                while ((line = reader.readLine()) != null) {
                    sb.append(line.trim());
                }
                return sb.toString().trim();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }


    /**
     * 获取语音文件存储目录
     * @return
     */
    public static File getVoicePathName() {
        if (!isExistExternalStore()) {
            ToastUtil.showMessage(R.string.media_ejected);
            return null;
        }

        File directory = new File(IMESSAGE_VOICE);
        if (!directory.exists() && !directory.mkdirs()) {
            ToastUtil.showMessage("Path to file could not be created");
            return null;
        }

        return directory;
    }

    /**
     * 头像
     * @return
     */
    public static File getAvatarPathName() {
        if (!isExistExternalStore()) {
            ToastUtil.showMessage(R.string.media_ejected);
            return null;
        }

        File directory = new File(IMESSAGE_AVATAR);
        if (!directory.exists() && !directory.mkdirs()) {
            ToastUtil.showMessage("Path to file could not be created");
            return null;
        }

        return directory;
    }



    /**
     * 获取文件目录
     * @return
     */
    public static File getFilePathName() {
        if (!isExistExternalStore()) {
            ToastUtil.showMessage(R.string.media_ejected);
            return null;
        }

        File directory = new File(IMESSAGE_FILE);
        if (!directory.exists() && !directory.mkdirs()) {
            ToastUtil.showMessage("Path to file could not be created");
            return null;
        }

        return directory;
    }

    /**
     * 返回图片存放目录
     * @return
     */
    public static File getImagePathName() {
        if (!isExistExternalStore()) {
            ToastUtil.showMessage(R.string.media_ejected);
            return null;
        }

        File directory = new File(IMESSAGE_IMAGE);
        if (!directory.exists() && !directory.mkdirs()) {
            ToastUtil.showMessage("Path to file could not be created");
            return null;
        }

        return directory;
    }

    /**
     * 获取文件名
     * @param pathName
     * @return
     */
    public static String getFileName(String pathName) {

        int start = pathName.lastIndexOf("/");
        if (start != -1) {
            return pathName.substring(start + 1, pathName.length());
        }
        return pathName;

    }

    /**
     * 外置存储卡的路径
     * @return
     */
    public static String getExternalStorePath() {
        if (isExistExternalStore()) {
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        return null;
    }

    /**
     * 是否有外存卡
     * @return
     */
    public static boolean isExistExternalStore() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * /data/data/com.ECSDK_Demo.bluetooth/files
     *
     * @return
     */
    public static String getAppContextPath() {
        return BaseApplication.getInstance().getFilesDir().getAbsolutePath();
    }

    /**
     *
     * @param fileName
     * @return
     */
    public static String getFileUrlByFileName(String fileName) {
        return FileAccessor.IMESSAGE_IMAGE + File.separator + FileAccessor.getSecondLevelDirectory(fileName)+ File.separator + fileName;
    }

    /**
     *
     * @param filePaths
     */
    public static void delFiles(ArrayList<String> filePaths) {
        for(String url : filePaths) {
            if(!TextUtils.isEmpty(url))
                delFile(url);
        }
    }


    public static boolean delFile(String filePath){
        File file = new File(filePath);
        if (file == null || !file.exists()) {
            return true;
        }

        return file.delete();
    }

    /**
     *
     * @param fileName
     * @return
     */
    public static String getSecondLevelDirectory(String fileName) {
        if(TextUtils.isEmpty(fileName) || fileName.length() < 4) {
            return null;
        }

        String sub1 = fileName.substring(0, 2);
        String sub2 = fileName.substring(2, 4);
        return sub1 + File.separator + sub2;
    }

    /**
     *
     * @param root
     * @param srcName
     * @param destName
     */
    public static void renameTo(String root , String srcName , String destName) {
        if(TextUtils.isEmpty(root) || TextUtils.isEmpty(srcName) || TextUtils.isEmpty(destName)){
            return;
        }

        File srcFile = new File(root + srcName);
        File newPath = new File(root + destName);

        if(srcFile.exists()) {
            srcFile.renameTo(newPath);
        }
    }

    public static File getTackPicFilePath() {
        File localFile = new File(getExternalStorePath()+ "/membersystem/.tempchat" , "temp.jpg");
        if ((!localFile.getParentFile().exists())
                && (!localFile.getParentFile().mkdirs())) {
            LogUtil.e("hhe", "SD卡不存在");
            localFile = null;
        }
        return localFile;
    }

    /**
     * 根据路径打开文件
     * @param context 上下文
     * @param path 文件路径
     */
    public static void openFileByPath(Context context, String path) {
        if(context==null||path==null) {
            ToastUtil.showMessage("文件不存在！");
            return;
        }
        Intent intent = new Intent();
        //设置intent的Action属性
        intent.setAction(Intent.ACTION_VIEW);
        //文件的类型
        String type = "";
        for(int i =0;i<MATCH_ARRAY.length;i++){
            //判断文件的格式
            if(path.toString().contains(MATCH_ARRAY[i][0].toString())){
                type = MATCH_ARRAY[i][1];
                break;
            }
        }
        try {
            //判断是否是AndroidN以及更高的版本
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Uri contentUri = SZLSystemProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", new File(path));
                intent.setDataAndType(contentUri, type);
            } else {
                intent.setDataAndType(Uri.fromFile(new File(path)), type);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            //跳转
            context.startActivity(intent);
        } catch (Exception e) { //当系统没有携带文件打开软件，提示
            ToastUtil.showMessage("无法打开该格式文件!");
            e.printStackTrace();
        }
    }
}
