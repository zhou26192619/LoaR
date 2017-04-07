package com.loar.util;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

/**
 * 界面跳转调用工具
 */
public class SteerUtil {

    /**
     * 界面跳转带参数
     *
     * @param context
     * @param cla
     * @param bundle
     */
    public static void startForResult(Activity context, Class cla,
                                      Bundle bundle, int requestCode) {
        Intent intent = new Intent(context, cla);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        context.startActivityForResult(intent, requestCode);
    }

    /**
     * 界面跳转带参数
     *
     * @param context
     * @param cla
     * @param bundle
     */
    public static void startActivity(Context context, Class cla,
                                     Bundle bundle) {
        Intent intent = new Intent(context, cla);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        context.startActivity(intent);
    }

    /**
     * 界面跳转不带参数
     *
     * @param context
     * @param cla
     */
    public static void startActivity(Context context, Class cla) {
        startActivity(context, cla, null);
    }

    /**
     * 界面跳转不带参数
     *
     * @param context
     * @param className
     */
    public static void startActivity(Context context, String className) {
        Bundle bundle = null;
        startActivity(context, className, bundle);
    }

    public static void startActivity(Context context, String className, Bundle bundle) {
        Intent intent = new Intent();
        intent.setClassName(context, className);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        context.startActivity(intent);
    }

    /**
     * 通过包名调用启动器启动应用
     *
     * @param context
     * @param pkgName
     */
    public static void startApp(Context context, String pkgName) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(pkgName);
        context.startActivity(intent);
    }

    /**
     * 启动应用到指定的activity
     *
     * @param context
     * @param pkgName
     * @param className
     */
    public static void startActivity(Context context, String pkgName, String className, Bundle bundle) {
        Intent intent = new Intent();
        ComponentName cn = new ComponentName(pkgName, className);
        intent.setComponent(cn);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        context.startActivity(intent);
    }

    public static void startActivity(Context context, String pkgName, String className) {
        startActivity(context, pkgName, className, null);
    }

    public static void startForResult(Activity context, String pkgName, String className, Bundle bundle, int requestCode) {
        Intent intent = new Intent();
        ComponentName cn = new ComponentName(pkgName, className);
        intent.setComponent(cn);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        context.startActivityForResult(intent, requestCode);
    }

    public static void startWithAction(Context context, String action, Bundle bundle) {
        Intent intent = new Intent(action);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        context.startActivity(intent);
    }

    public static void startWithAction(Context context, String action) {
        startWithAction(context, action, null);
    }

    /**
     * 调用图片裁剪功能
     *
     * @param act
     * @return 裁剪后保存的uri
     */
//    public static Uri goToPicCrop(Activity act, String url, int requestCode) {
//        Uri tempUri = Uri.fromFile(FileOperator.initFile(
//                BaseConfig.DEFAULT_DIR_NAME, "temp.jpg"));
//        int w = DensityUtil.getScreenW(act);
//        Uri mUri = Uri.parse(url);
//        Intent intent = new Intent();
//        intent.setAction("com.android.camera.action.CROP");
//        intent.setDataAndType(mUri, "image/*");// mUri是已经选择的图片Uri
//        intent.putExtra("crop", "true");
//        intent.putExtra("aspectX", 1);// 裁剪框比例
//        intent.putExtra("aspectY", 1);
//        intent.putExtra("outputX", w);// 输出图片大小
//        intent.putExtra("outputY", w);
//        intent.putExtra("return-data", false);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);
//        act.startActivityForResult(intent, requestCode);
//        return tempUri;
//    }

    public static void callPhone(Context act, String phone) {
        Uri uri = Uri.parse("tel:" + phone);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_DIAL);
        intent.setData(uri);
        act.startActivity(intent);
    }

    public static void callMap(Context act, String geo) {
        Uri uri = Uri.parse("geo:" + geo);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        act.startActivity(intent);
    }

    /**
     * 弹出Launcher的选择对话框
     *
     * @param context
     */
    public static void callHome(Context context) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        context.startActivity(intent);
    }

    /**
     * 弹出Launcher的选择对话框
     *
     * @param context
     */
    public static void callInstall(Context context, Uri uri) {
        Intent install = new Intent(Intent.ACTION_VIEW);
        install.setDataAndType(uri, "application/vnd.android.package-archive");
        install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(install);
    }

}
