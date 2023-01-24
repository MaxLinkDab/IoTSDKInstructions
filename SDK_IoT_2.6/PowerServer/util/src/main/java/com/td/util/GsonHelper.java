package com.td.util;

import com.google.gson.GsonBuilder;

public class GsonHelper {
    private static GsonBuilder builder = new GsonBuilder();

    static {
        builder.setDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    public static <T> String toJson(T t) {
        return builder.create().toJson(t);
    }

    public static <T> T parseJson(Class<T> clazz, String json) {
        return builder.create().fromJson(json, clazz);
    }
}
