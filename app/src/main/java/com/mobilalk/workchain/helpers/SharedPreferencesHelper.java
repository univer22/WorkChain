package com.mobilalk.workchain.helpers;

import android.content.Context;
import android.content.SharedPreferences;
public class SharedPreferencesHelper {
    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;
    public SharedPreferencesHelper(Context context) {
        sharedPreferences = context.getSharedPreferences("WorkChainPrefs", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public String getItem(String key, String defaultValue) {
        return sharedPreferences.getString(key, defaultValue);
    }

    public void addItem(String key, String value) {
        editor.putString(key, value);
        editor.apply();
    }

    public void deleteItem(String key) {
        editor.remove(key);
        editor.apply();
    }

    public void clear() {
        editor.clear();
        editor.apply();
    }
}
