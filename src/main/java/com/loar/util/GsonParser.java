package com.loar.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

public class GsonParser {

    private static GsonParser gsonParser;
    private static Gson gson;

    public static GsonParser getInstance() {
        if (gsonParser == null) {
            synchronized (GsonParser.class) {
                if (gsonParser == null) {
                    gsonParser = new GsonParser();
                }
            }
        }
        getGsonInstance();
        return gsonParser;
    }

    private static Gson getGsonInstance() {
        if (gson == null) {
            synchronized (GsonParser.class) {
                if (gson == null) {
                    gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                }
            }
        }
        return gson;
    }

    public <T> T toJsonObj(JsonObject obj, Class<T> t) {
        return gson.fromJson(obj.toString(), t);
    }

    public <T> T toJsonObj(String json, Class<T> t) {
        return toJsonObj(toJsonObj(json), t);
    }

    public <T> T toJsonArr(String arrJson, TypeToken<T> typeToken) {
        return gson.fromJson(arrJson, typeToken.getType());
    }

    public <T> String toJsonString(T t) {
        return gson.toJson(t);
    }

    public JsonObject toJsonObj(String json) {
        JsonElement jp = new JsonParser().parse(json);
        return jp.getAsJsonObject();
    }
}
