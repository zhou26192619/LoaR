package com.loar.net;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Justsy on 2016/5/3.
 */
public interface HttpCallback<T> {

    void onBefore(String url);

    void onAfter(String url);

    void onFailure(String url, Call call, Exception e);

    T onResponse(String url, Call call, Response response) throws Exception;

    void onSuccess(String url, Call call, Response response, T t);

}
