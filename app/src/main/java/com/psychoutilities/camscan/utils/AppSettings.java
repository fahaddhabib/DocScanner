package com.psychoutilities.camscan.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.psychoutilities.camscan.main_utils.Constant;


public class AppSettings {

    public static boolean isServiceStarted(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);

        return pref.getBoolean(Constant.IS_SERVICE_STARTED, false);
    }

    public static void setServiceStarted(Context context, boolean isStarted) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(Constant.IS_SERVICE_STARTED, isStarted);
        editor.apply();
    }

    public static int getPortNumber(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getInt(Constant.PREF_SERVER_PORT, Constant.DEFAULT_SERVER_PORT);
    }

    public static void setPortNumber(Context context, int port) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(Constant.PREF_SERVER_PORT, port);
        editor.apply();
    }

    public static boolean getClientIp(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getBoolean(Constant.ACCEPT_REQUEST, Constant.DEFAULT_ACCEPT_REQUEST);
    }

    public static void setClientIp(Context context, boolean accept) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(Constant.ACCEPT_REQUEST, accept);
        editor.apply();
    }
}
