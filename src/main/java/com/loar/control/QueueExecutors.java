package com.loar.control;

import android.os.UserManager;

import java.util.List;
import java.util.concurrent.Executors;

/**
 * 多线程队列池
 *
 * @author LoaR
 */
public class QueueExecutors {

    private List<QueueExecutor> queueExecutor;

    public QueueExecutors() {
        Executors.newSingleThreadExecutor();

    }
}
