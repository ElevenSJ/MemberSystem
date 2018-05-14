package com.lyp.membersystem.base;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.jady.retrofitclient.HttpManager;
import com.lyp.membersystem.BuildConfig;
import com.lyp.membersystem.log.LogUtils;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.sj.http.UrlConfig;
import com.sj.utils.Utils;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.smtt.sdk.QbSdk;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.PlatformConfig;
import com.yuntongxun.ecdemo.common.CCPAppManager;
import com.yuntongxun.ecdemo.common.utils.ECPreferenceSettings;
import com.yuntongxun.ecdemo.common.utils.ECPreferences;
import com.yuntongxun.ecdemo.common.utils.FileAccessor;
import com.yuntongxun.ecdemo.common.utils.LogUtil;
import com.yuntongxun.ecdemo.ui.huawei.PustDemoActivity;

import java.io.File;
import java.io.InvalidClassException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Stack;

import cn.jpush.android.api.JPushInterface;

import static com.umeng.socialize.utils.DeviceConfig.context;

public class BaseApplication extends MultiDexApplication {

    private static BaseApplication instance;
    // 存储某种情况下需要被干掉的activity
    private static Stack<Activity> activityStack;
    // 存储所有活着的activity
    private static Stack<Activity> allActivityStack;

    private static PustDemoActivity mPustTestActivity = null;

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        instance = this;
        activityStack = new Stack<Activity>();
        allActivityStack = new Stack<Activity>();
        // 第三个参数为SDK调试模式开关，调试模式的行为特性如下：
        // 输出详细的Bugly SDK的Log
        // 每一条Crash都会被立即上报
        // 自定义日志将会在Logcat中输出
        // 建议在测试阶段建议设置成true，发布时设置为false。
//		CrashReport.initCrashReport(getApplicationContext(), "87e0dbcc90", true);
//		initImageLoader(getApplicationContext());
        CCPAppManager.setContext(instance);
        FileAccessor.initFileAccess();
        setChattingContactId();
        initImageLoader();
//		SDKInitializer.initialize(instance);

        //二期
        Utils.init(this);
        HttpManager.init(this.getApplicationContext(), UrlConfig.BASE_URL);
        UMConfigure.setLogEnabled(true);
        UMConfigure.init(this, "5af3fda6f43e483b3d0000bb", "main", UMConfigure.DEVICE_TYPE_PHONE, "");
        PlatformConfig.setWeixin("wx194d9f8a263dd765", "ef4ccdfddd9fcbc3b1ccbea12a865321");
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
        initX5WebKit();
        initBuglyCrash();
    }

    private void initX5WebKit() {
        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {

            @Override
            public void onViewInitFinished(boolean arg0) {
                // TODO Auto-generated method stub
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                Log.d("app", " onViewInitFinished is " + arg0);
            }

            @Override
            public void onCoreInitFinished() {
                // TODO Auto-generated method stub
            }
        };
        //x5内核初始化接口
        QbSdk.initX5Environment(getApplicationContext(),  cb);
    }

    private void initBuglyCrash() {
        // 获取当前包名
        String packageName = instance.getPackageName();
        // 获取当前进程名
        String processName = Utils.getProcessName(android.os.Process.myPid());
        // 设置是否为上报进程
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(instance);
        strategy.setUploadProcess(processName == null || processName.equals(packageName));
        strategy.setCrashHandleCallback(new CrashReport.CrashHandleCallback() {

            public Map<String, String> onCrashHandleStart(int crashType, String errorType, String errorMessage, String errorStack) {
                LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
                String x5CrashInfo = com.tencent.smtt.sdk.WebView.getCrashExtraMessage(instance);
                map.put("x5crashInfo", x5CrashInfo);
                return map;
            }

            @Override
            public byte[] onCrashHandleStart2GetExtraDatas(int crashType, String errorType, String errorMessage, String errorStack) {
                try {
                    return "Extra data.".getBytes("UTF-8");
                } catch (Exception e) {
                    return null;
                }
            }
        });
        // 初始化Bugly
        CrashReport.initCrashReport(instance, "6e0dfc9e9d", BuildConfig.DEBUG, strategy);
    }

    public static BaseApplication getInstance() {
        return instance;
    }

    /**
     * 添加Activity到堆栈
     */
    public void addActivity(Activity activity) {
        LogUtils.d("add " + activity);
        activityStack.add(activity);
    }

    /**
     * 移除Activity到堆栈
     */
    public void removeActivity(Activity activity) {
        LogUtils.d("remove " + activity);
        activityStack.remove(activity);
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    public Activity currentActivity() {
        Activity activity = activityStack.lastElement();
        return activity;
    }

    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    public void finishActivity() {
        Activity activity = activityStack.lastElement();
        finishActivity(activity);
    }

    /**
     * 结束指定的Activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null) {
            activityStack.remove(activity);
            activity.finish();
            activity = null;
        }
    }

    /**
     * 结束指定类名的Activity
     */
    public void finishActivity(Class<?> cls) {
        for (Activity activity : activityStack) {
            if (activity.getClass().equals(cls)) {
                finishActivity(activity);
            }
        }
    }

    /**
     * 结束特殊路径Activity
     */
    public void finishSpecialPathActivity() {
        for (int i = 0, size = activityStack.size(); i < size; i++) {
            if (null != activityStack.get(i)) {
                activityStack.get(i).finish();
            }
        }
        activityStack.clear();
    }

    /**
     * 退出应用程序
     */
    @SuppressWarnings("deprecation")
    public void AppExit(Context context) {
        try {
            finishAllActivity();
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            activityManager.restartPackage(context.getPackageName());
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 添加Activity到allActivityStack堆栈
     */
    public void addActivityToAll(Activity activity) {
        LogUtils.d("add " + activity);
        allActivityStack.add(activity);
    }

    /**
     * 移除Activity到allActivityStack堆栈
     */
    public void removeActivityFromAll(Activity activity) {
        LogUtils.d("remove " + activity);
        allActivityStack.remove(activity);
    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        for (int i = 0, size = allActivityStack.size(); i < size; i++) {
            if (null != allActivityStack.get(i)) {
                allActivityStack.get(i).finish();
            }
        }
        allActivityStack.clear();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    /**
     * 保存当前的聊天界面所对应的联系人、方便来消息屏蔽通知
     */
    private void setChattingContactId() {
        try {
            ECPreferences.savePreference(ECPreferenceSettings.SETTING_CHATTING_CONTACTID, "", true);
        } catch (InvalidClassException e) {
            e.printStackTrace();
        }
    }

    public void setMainActivity(PustDemoActivity activity) {
        mPustTestActivity = activity;
    }

    public PustDemoActivity getMainActivity() {
        return mPustTestActivity;
    }

    public void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you
        // may tune some of them,
        // or you can create default configuration by
        // ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.discCacheFileCount(100);// 缓存一百张图片
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.writeDebugLogs(); // Remove for release app

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config.build());
    }


    private void initImageLoader() {
        File cacheDir = StorageUtils.getOwnCacheDirectory(getApplicationContext(), "membersystem/image");
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).threadPoolSize(1)// 线程池内加载的数量
                .threadPriority(Thread.NORM_PRIORITY - 2).memoryCache(new WeakMemoryCache())
                // .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(CCPAppManager.md5FileNameGenerator)
                // 将保存的时候的URI名称用MD5 加密
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .diskCache(new UnlimitedDiscCache(cacheDir, null, CCPAppManager.md5FileNameGenerator))// 自定义缓存路径
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                // .writeDebugLogs() // Remove for release app
                .build();// 开始构建
        ImageLoader.getInstance().init(config);
    }

    /**
     * 返回配置文件的日志开关
     *
     * @return
     */
    public boolean getLoggingSwitch() {
        try {
            ApplicationInfo appInfo = getPackageManager().getApplicationInfo(getPackageName(),
                    PackageManager.GET_META_DATA);
            boolean b = appInfo.metaData.getBoolean("LOGGING");
            LogUtil.w("[BaseApplication - getLogging] logging is: " + b);
            return b;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean getAlphaSwitch() {
        try {
            ApplicationInfo appInfo = getPackageManager().getApplicationInfo(getPackageName(),
                    PackageManager.GET_META_DATA);
            boolean b = appInfo.metaData.getBoolean("ALPHA");
            LogUtil.w("[BaseApplication - getAlpha] Alpha is: " + b);
            return b;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }
}
