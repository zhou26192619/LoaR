package com.loar.util;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by Justsy on 2016/5/12.
 */
public class PermissionUtil {
    public static final int CODE_REQUEST = 12;


    /**
     * 申请检查权限
     *
     * @param context
     * @param permission eg.Manifest.permission.SYSTEM_ALERT_WINDOW
     * @return
     */
    public static boolean requestPermission(Activity context, String permission) {
        boolean result = ContextCompat.checkSelfPermission(context, permission)
                == PackageManager.PERMISSION_GRANTED;
        if (!result) {
            ActivityCompat.requestPermissions(context, new String[]{permission},
                    CODE_REQUEST);
        }
        return result;
    }

}
