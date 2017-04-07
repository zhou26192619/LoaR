package com.loar.storage;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

/**
 * SharedPreferences的操作对象
 */
public class SPOperator {
    private SharedPreferences sharedPreferences;

    public String spName = null;

    public SPOperator(Context context) {
        this(context, null);
    }

    public SPOperator(Context context, String name) {
        this(context, name, Context.MODE_PRIVATE);
    }

    public SPOperator(Context context, String name, int mode) {
        if (name == null) {
            spName = context.getPackageName();
        } else {
            spName = name;
        }
        sharedPreferences = context.getSharedPreferences(
                spName, mode);
    }

    /**
     * @param key
     * @param value
     */
    public void save(String key, Object value) {
        SharedPreferences.Editor edit = sharedPreferences.edit();
        if (value == null) {
            remove(key);
            return;
        }
        if (value instanceof Boolean) {
            edit.putBoolean(key, (Boolean) value);
        } else if (value instanceof String) {
            edit.putString(key, (String) value);
        } else if (value instanceof Float) {
            edit.putFloat(key, (Float) value);
        } else if (value instanceof Integer) {
            edit.putInt(key, (Integer) value);
        } else if (value instanceof String) {
            edit.putLong(key, (Long) value);
        } else if (value instanceof Set) {
            edit.putStringSet(key, (Set<String>) value);
        } else {
            edit.putString(key, value.toString());
        }
        edit.commit();
    }

    public void remove(String key) {
        sharedPreferences.edit().remove(key).commit();
    }

    public SharedPreferences getTheSP() {
        return sharedPreferences;
    }

    public void clear() {
        sharedPreferences.edit().clear().commit();
    }
}
