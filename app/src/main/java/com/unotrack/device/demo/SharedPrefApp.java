package com.unotrack.device.demo;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by aditya on 15/4/17.
 */

public class SharedPrefApp {
    SharedPreferences sharepreferences;

    public static SharedPrefApp instance = null;

    public static SharedPrefApp getInstance()
    {

        if (instance == null) {
            synchronized (SharedPrefApp.class) {
                instance = new SharedPrefApp();
            }
        }
        return instance;
    }
    public void saveISLogged_IN(Context context, Boolean isLoggedin) {
        sharepreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharepreferences.edit();
        editor.putBoolean("IS_LOGIN", isLoggedin);
        editor.commit();
    }

    public boolean getISLogged_IN(Context context) {
        sharepreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        return sharepreferences.getBoolean("IS_LOGIN", false);
    }

}

