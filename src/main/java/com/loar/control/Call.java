package com.loar.control;

import java.util.concurrent.Callable;

/**
 * 任务队列
 * Created by Administrator on 2016/3/23.
 */
public interface Call<V> extends Callable {

    int executeCount();//设置执行次数

    long delayExecuteTime();//首次执行时间

    long executeGap();//设置执行间隔
}
