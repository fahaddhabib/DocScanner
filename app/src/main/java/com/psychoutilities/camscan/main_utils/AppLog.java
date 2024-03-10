package com.psychoutilities.camscan.main_utils;

import android.util.Log;


public class AppLog {
    public static final String LOG = "transfile";
    public static final String WARNING = "Warnning";

    public static void logString(String message) {
        if (Constant.LOG_DEBUG)
            Log.i(LOG, message);
    }
//
//    public static void logString(String message, String tag) {
//        if (Constants.LOG_DEBUG)
//            Log.i(tag, message);
//    }
//
//    public static void logWarningString(String message) {
//        if (Constants.LOG_DEBUG)
//            Log.i(WARNING, message);
//    }
}
