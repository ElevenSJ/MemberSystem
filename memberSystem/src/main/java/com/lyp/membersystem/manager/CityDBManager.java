package com.lyp.membersystem.manager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.lyp.membersystem.R;
import com.lyp.membersystem.log.LogUtils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

public class CityDBManager {
    private final int BUFFER_SIZE = 1024;
    public static final String DB_NAME = "citys.db3";
    public static String PACKAGE_NAME = "com.lyp.membersystem";
    public static final String DB_PATH = "/data"
            + Environment.getDataDirectory().getAbsolutePath() + "/" + PACKAGE_NAME;
    private SQLiteDatabase database;
    private Context context;
    private File file = null;

    public CityDBManager(Context context) {
        PACKAGE_NAME = context.getPackageName();
        this.context = context;
    }

    public void openDatabase() {
        this.database = this.openDatabase(DB_PATH + "/" + DB_NAME);
    }

    public SQLiteDatabase getDatabase() {
        return this.database;
    }

    private SQLiteDatabase openDatabase(String dbfile) {
        try {
            LogUtils.d("open and return");
            file = new File(dbfile);
            if (!file.exists()) {
                InputStream is = context.getResources().openRawResource(R.raw.citys);
                if (is != null) {
                	LogUtils.d("is != null");
                } else {
                }
                FileOutputStream fos = new FileOutputStream(dbfile);
                if (is != null) {
                	LogUtils.d("fosnull");
                } else {
                }
                byte[] buffer = new byte[BUFFER_SIZE];
                int count = 0;
                while ((count = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);
                    fos.flush();
                }
                fos.close();
                is.close();
            }
            database = SQLiteDatabase.openOrCreateDatabase(dbfile, null);
            return database;
        } catch (FileNotFoundException e) {
        	LogUtils.e("File not found");
            e.printStackTrace();
        } catch (IOException e) {
        	LogUtils.e("IO exception");
            e.printStackTrace();
        } catch (Exception e) {
        	LogUtils.e("exception " + e.toString());
        }
        return null;
    }

    public void closeDatabase() {
        if (this.database != null)
            this.database.close();
    }
}
