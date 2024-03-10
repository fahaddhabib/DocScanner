package com.psychoutilities.camscan;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatDelegate;

import com.onesignal.OneSignal;
import com.psychoutilities.camscan.main_utils.Constant;

import me.pqpo.smartcropperlib.SmartCropper;

public class MyApplication extends Application {
    private static final String ONESIGNAL_APP_ID = "########-####-####-####-############";
    private static MyApplication instance;
    private boolean showAds = true;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        // 如果使用机器学习代替 Canny 算子，请初始化 ImageDetector
        SmartCropper.buildImageDetector(this);

        // Enable verbose OneSignal logging to debug issues if needed.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);

        // OneSignal Initialization
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);

        SharedPreferences adsPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        setShowAds(adsPref.getBoolean("showAds",true));


        initTheme();
    }

    private void initTheme() {
        int savedTheme = getSavedTheme();
        if (savedTheme == Constant.THEME_LIGHT) {
            setTheme(AppCompatDelegate.MODE_NIGHT_NO,Constant.THEME_LIGHT);
        } else if (savedTheme == Constant.THEME_DARK) {
            setTheme(AppCompatDelegate.MODE_NIGHT_YES,Constant.THEME_DARK);
        }
    }

    private int getSavedTheme() {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(Constant.PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(Constant.KEY_THEME, Constant.THEME_UNDEFINED);
    }


    private void setTheme(int themeMode, int prefsMode) {
        AppCompatDelegate.setDefaultNightMode(themeMode);
        saveTheme(prefsMode);
    }
    public static MyApplication getInstance() {
        return instance;
    }

    public boolean isShowAds() {
        return showAds;
    }

    public void setShowAds(boolean showAds) {
        this.showAds = showAds;
    }

    public void saveTheme(int theme) {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(Constant.PREFS_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().putInt(Constant.KEY_THEME, theme).apply();
    }

}
