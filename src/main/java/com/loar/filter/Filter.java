package com.loar.filter;

/**
 * 过滤器基本接口
 * Created by Administrator on 2016/3/29.
 */
public interface Filter<T> {
    boolean passBy(T t);
}
