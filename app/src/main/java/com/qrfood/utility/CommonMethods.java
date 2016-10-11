package com.qrfood.utility;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Prerak on 10/5/2016.
 */

public class CommonMethods {

    private Context context;

    public CommonMethods(Context context) {
        this.context = context;
    }

    public void setSharedPreferences(String key, Object value, String dataType) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.sharedPreferencesId, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        switch (dataType) {
            case "String":
                editor.putString(key, value.toString());
                break;
            case "Integer":
                editor.putInt(key, Integer.parseInt(value.toString()));
                break;
            case "Boolean":
                editor.putBoolean(key, Boolean.parseBoolean(value.toString()));
                break;
        }
        editor.apply();
    }

    public void removeSharedPreference(String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.sharedPreferencesId, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key).apply();
    }

    public Object getSharedPreferences(String key, String dataType) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.sharedPreferencesId, Context.MODE_PRIVATE);

        switch (dataType) {
            case "String":
                return sharedPreferences.getString(key, null);

            case "Integer":
                return sharedPreferences.getInt(key, -1);

            case "Boolean":
                return sharedPreferences.getBoolean(key, false);
        }
        return null;
    }
}
