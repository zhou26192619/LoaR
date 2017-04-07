package com.loar.listener;

import java.io.Serializable;
import java.util.Date;

import android.view.View;

public abstract class OnClickListener implements
        View.OnClickListener, Serializable {

    private long lastClickTime;
    /**
     * 间隔时间
     */
    private long interval = 800;

    @Override
    public void onClick(View v) {
        long ct = System.currentTimeMillis();
        if ((ct - lastClickTime) > interval) {
            onClick(v, ct);
        }
        lastClickTime = ct;
    }

    public abstract void onClick(View v, long clickTime);

    public void setInterval(long interval) {
        this.interval = interval;
    }
}
