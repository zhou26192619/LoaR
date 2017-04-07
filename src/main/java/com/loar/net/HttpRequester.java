package com.loar.net;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.loar.util.GsonParser;

import java.io.File;
import java.io.IOException;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Justsy on 2016/4/29.
 */
public class HttpRequester {

    private static HttpRequester httpRequester;
    private static OkHttpClient okHttpClient;
    private static RequestBuilder requestBuilder;

    private static Handler handler = new Handler(Looper.getMainLooper());

    private HttpRequester() {
        getOkHttpClient();
    }

    private synchronized static OkHttpClient getOkHttpClient() {
        if (okHttpClient == null) {
            okHttpClient = new OkHttpClient();
        }
        okHttpClient.newBuilder().readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS);
        return okHttpClient;
    }

    public HttpRequester setTimeOut(int readTimeout, int writeTimeout, int connectTimeout) {
        getOkHttpClient().newBuilder().readTimeout(readTimeout, TimeUnit.SECONDS)
                .writeTimeout(writeTimeout, TimeUnit.SECONDS)
                .connectTimeout(connectTimeout, TimeUnit.SECONDS);
        return this;
    }

    public static synchronized RequestBuilder getInstance() {
        httpRequester = new HttpRequester();
        requestBuilder = new RequestBuilder(httpRequester);
        return requestBuilder;
    }

    public void execute() {
        execute(null);
    }

    public void execute(final HttpCallback callback) {
        try {
            final RequestBuilder requestBuilder = HttpRequester.requestBuilder;
            Log.e("HttpRequester", requestBuilder.url + (requestBuilder.params == null ? "" : "?" + requestBuilder.params.string()));
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (callback != null) {
                        callback.onBefore(requestBuilder.url);
                    }
                }
            });
            getOkHttpClient().newCall(requestBuilder.giveRequest()).enqueue(new Callback() {
                @Override
                public void onFailure(final Call call, final IOException e) {
                    if (callback == null) {
                        return;
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                callback.onFailure(requestBuilder.url, call, e);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            } finally {
                                callback.onAfter(requestBuilder.url);
                            }
                        }
                    });

                }

                @Override
                public void onResponse(final Call call, final Response response) throws IOException {
                    try {
                        if (callback == null) {
                            return;
                        }
                        final Object t = callback.onResponse(requestBuilder.url, call, response);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    callback.onSuccess(requestBuilder.url, call, response, t);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    } finally {
                        response.body().close();
                        if (callback != null) {
                            callback.onAfter(requestBuilder.url);
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Response excuteAsync() throws Exception {
        return getOkHttpClient().newCall(requestBuilder.giveRequest()).execute();
    }

    /**
     * 构建请求数据对象
     */
    public static class RequestBuilder {
        public static final MediaType MEDIA_TYPE_IMAGE = MediaType.parse("image/*");
        public static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
        public static final MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("text/x-markdown; charset=utf-8");

        public static final String METHOD_GET = "get";
        public static final String METHOD_POST = "post";

        private HttpRequester httpRequester;
        private String url;
        private Request.Builder builder;
        private String method = METHOD_GET;
        private Params params;
        private MultipartBody.Builder mulBuilder;
        private RequestBody body;

        public RequestBuilder(HttpRequester httpRequester) {
            this.builder = new Request.Builder();
            this.httpRequester = httpRequester;
            this.mulBuilder = new MultipartBody.Builder().setType(MultipartBody.MIXED);
        }

        public RequestBuilder addHeader(String key, String value) {
            builder.addHeader(key, value);
            return this;
        }

        public HttpRequester ready() {
            return httpRequester;
        }

        public RequestBuilder setBuilder(MediaType type) {
            mulBuilder.setType(type);
            return this;
        }

        public RequestBuilder url(String url) {
            this.url = url;
            return this;
        }

        public RequestBuilder method(String method) {
            this.method = method;
            return this;
        }

        public Request giveRequest() throws Exception {
            if (params != null) {
                builder.url(url + "?" + params.string());
            } else {
                builder.url(url);
            }
            if (method.equalsIgnoreCase(METHOD_GET)) {
                builder.get();
            } else if (method.equalsIgnoreCase(METHOD_POST)) {
                if (body != null) {
                    return builder.post(body).build();
                }
                if (mulBuilder == null) {
                    throw new NullPointerException("post请求体为空");
                }
                builder.post(mulBuilder.build());
            }
            return builder.build();
        }

        public RequestBuilder addParams(String key, String value) {
            if (params == null) {
                params = new Params();
            }
            params.add(key, value);
            return this;
        }

        public RequestBuilder addBodyParams(String key, String value) {
            if (key != null && value != null)
                mulBuilder.addFormDataPart(key, value);
            return this;
        }

        public RequestBuilder addJsonParams(String value) {
            body = RequestBody.create(MEDIA_TYPE_JSON, value);
            mulBuilder.addPart(body);
            return this;
        }

        public RequestBuilder addBodyParams(String key, String filename, File file) {
            mulBuilder.addFormDataPart(key, filename,
                    RequestBody.create(MediaType.parse(guessMimeType(file.getPath())), file));
            return this;
        }

        /**
         * 获取minetype类型
         *
         * @param path
         * @return
         */
        private String guessMimeType(String path) {
            FileNameMap fileNameMap = URLConnection.getFileNameMap();
            String contentTypeFor = fileNameMap.getContentTypeFor(path);
            if (contentTypeFor == null) {
                contentTypeFor = "application/octet-stream";
            }
            return contentTypeFor;
        }
    }


    /**
     * 构建get参数的类
     */
    public static class Params {
        private StringBuffer sb = new StringBuffer();

        private Params() {
        }

        public static Params getInstance() {
            return new Params();
        }

        public Params add(String key, String value) {
            sb.append(key).append("=").append(value).append("&");
            return this;
        }

        public String string() {
            return sb.substring(0, sb.length() - 1);
        }
    }

    public static class Helper {
        public static final int KEY_STATUS_OK = 200;

        private Response response;
        private BaseResult baseResult;

        public Helper(Response response) {
            this.response = response;
        }

        public <T> T parse(Class<T> c) throws IOException {
            String str = response.body().string();
            return GsonParser.getInstance().toJsonObj(str, c);
        }

        public BaseResult getBaseResult() throws IOException {
            baseResult = parse(BaseResult.class);
            return baseResult;
        }

        public boolean isSuccess() {
            return response.code() == 200;
        }
    }


    public class BaseResult {
        private int status;
        private boolean success;
        private int totalCount;
        private JsonObject msgs;
        private JsonArray content;

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public int getTotalCount() {
            return totalCount;
        }

        public void setTotalCount(int totalCount) {
            this.totalCount = totalCount;
        }

        public JsonObject getMsgs() {
            return msgs;
        }

        public void setMsgs(JsonObject msgs) {
            this.msgs = msgs;
        }

        public JsonArray getContent() {
            return content;
        }

        public void setContent(JsonArray content) {
            this.content = content;
        }

        @Override
        public String toString() {
            return "BaseResult{" +
                    "status=" + status +
                    ", success=" + success +
                    ", totalCount=" + totalCount +
                    ", msgs=" + msgs.toString() +
                    ", content=" + content.toString() +
                    '}';
        }
    }
}
