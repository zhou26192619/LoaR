package com.loar.util;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

/**
 * 界面跳转调用工具
 */
public class Steering {
    /**
     * 不知道写来干嘛的
     *
     * @param context
     * @param classOfT
     */
    public static <T> void goToTAct(Context context, Class<T> classOfT) {
        Intent intent = new Intent(context, classOfT);
        if (!(context instanceof Activity)) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    /**
     * 界面跳转带参数
     *
     * @param context
     * @param cla
     * @param bundle
     */
    public static <T> void goToNewActForResult(Activity context, Class<T> cla,
                                               Bundle bundle, int requestCode) {
        Log.d("goToNewAct", context.getClass().getCanonicalName()
                + " ==> " + cla.getCanonicalName());
        Intent intent = new Intent(context, cla);
        if (!(context instanceof Activity)) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
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
    public static <T> void goToNewAct(Context context, Class<T> cla,
                                      Bundle bundle) {
        Log.d("goToNewAct", context.getClass().getCanonicalName()
                + " ==> " + cla.getCanonicalName());
        Intent intent = new Intent(context, cla);
        if (!(context instanceof Activity)) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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
    public static <T> void goToNewAct(Context context, Class<T> cla) {
        goToNewAct(context, cla, null);
    }

    /**
     * 界面跳转不带参数
     *
     * @param context
     * @param className
     */
    public static void goToNewAct(Context context, String className) {
        Intent intent = new Intent();
        intent.setClassName(context, className);
        context.startActivity(intent);
    }

    /**
     * 通过包名启动应用
     *
     * @param context
     * @param pkgName
     */
    public static void goToNewApp(Context context, String pkgName) {
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
    public static void goToNewApp(Context context, String pkgName, String className) {
        Intent intent = new Intent();
        ComponentName cn = new ComponentName(pkgName, className);
        intent.setComponent(cn);
        intent.setAction("android.intent.action.VIEW");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
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
//        intent.setAction("android.intent.action.VIEW");
        intent.addCategory(Intent.CATEGORY_HOME);
//        intent.setComponent(new ComponentName("android", "com.android.internal.app.ResolverActivity"));
        context.startActivity(intent);
    }

    /**
     * 支持华为，其它的不支持
     * @param context
     */
    public static void callDefaultSetting(Context context) throws Exception{
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setComponent(new ComponentName("com.android.settings", "com.android.settings.Settings$PreferredListSettingsActivity"));
        context.startActivity(intent);
    }

    /**
     * 弹出Launcher的选择对话框
     *
     * @param context
     */
    public static void callSetting(Context context) {
        Intent intent = new Intent(Settings.ACTION_SETTINGS);
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
