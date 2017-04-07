package com.loar.util;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.text.TextUtils;

import java.util.List;

/**
 * 获取手机安装的app信息
 * Created by LoaR on 2016/3/28.
 */
public class PackageHelper {

    /**
     * 根据包名获取相应的应用信息
     *
     * @param packageName
     * @return 返回包名所对应的应用程序的信息。
     */
    public static PackageInfo findInfoByPackageName(Context context, String packageName) {
        PackageInfo appInfo = null;
        if (TextUtils.isEmpty(packageName)) {
            return appInfo;
        }
        try {
            appInfo = context.getPackageManager().getPackageInfo(packageName,
                    0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return appInfo;
    }

    /**
     * 判断应用是否可用状态
     *
     * @param pkgName
     */
    public boolean isEnable(Context context, String pkgName) {
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(pkgName, 0);
            return ai.enabled;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 判断是否是最顶层应用,只能判断自己应用
     *
     * @param context
     * @param pkg
     * @return
     */
    public boolean isTopApp(Context context, String pkg) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                //前台程序
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(pkg)) {
                            return true;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            if (taskInfo != null) {
                ComponentName componentInfo = taskInfo.get(0).topActivity;
                if (componentInfo.getPackageName().equals(context.getPackageName())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断是否是默认launcher
     *
     * @param context
     * @return
     */
    public static boolean isDefaultLauncher(Context context, String pkg) {
        PackageManager pm = context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_HOME);
        ResolveInfo info = pm.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        //判断是否是默认launcher
        return info.activityInfo.packageName.equals(pkg);
    }

    /**
     * 判断是否是默认launcher
     *
     * @param context
     * @return
     */
    public static boolean isDefaultLauncher(Context context) {
        return isDefaultLauncher(context, context.getPackageName());
    }
}
