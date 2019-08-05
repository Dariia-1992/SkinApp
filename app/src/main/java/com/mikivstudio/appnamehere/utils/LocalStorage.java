package com.mikivstudio.appnamehere.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Dariia Mshanetskaya  on 02.06.2019.
 */
public class LocalStorage {
    private static final String PREFERENCES_FILE = "preferences_file";
    private static final String KEY_OPENS_WITHOUT_ADD = "opens_without_ad";

    public static void setOpensWithoutAd(Context context, int count) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putInt(KEY_OPENS_WITHOUT_ADD, count);
        editor.apply();
    }

    public static int getOpensWithoutAd(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        return preferences.getInt(KEY_OPENS_WITHOUT_ADD, 0);
    }
}