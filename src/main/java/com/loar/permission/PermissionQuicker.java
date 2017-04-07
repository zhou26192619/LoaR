package com.loar.permission;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by Justsy on 2016/10/12.
 */

public class PermissionQuicker {

    /**
     * 请求判断手机权限，结果返回仍然在Activity的onRequestPermissionsResult方法中
     *
     * @param activity
     * @param requestCode
     * @param permissions
     * @return
     */
    public static boolean checkPermissions(Activity activity, int requestCode, String... permissions) {
        if (permissions != null) {
            boolean need = false;
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                    need = true;
                    break;
                }
            }
            if (need) {
                ActivityCompat.requestPermissions(activity, permissions, requestCode);
            }
            return !need;
        }
        return true;
    }
}
