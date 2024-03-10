package com.psychoutilities.camscan.main_utils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ColorMatrixColorFilter;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.widget.Toast;

import com.psychoutilities.camscan.R;

import java.util.Date;

public class Constant {
    public static int filterPosition = 0;
    public static String inputType = "Group";
    public static Bitmap original, singleSideBitmap;
    public static int selectedFont;
    public static int selectedWatermarkFont;
    public static String PREFS_NAME = "theme_prefs";
    public static String KEY_THEME = "prefs.theme";
    public static  int THEME_UNDEFINED = -1;
    public static  int THEME_LIGHT = 0;
    public static  int THEME_DARK = 1;
    public static Bitmap IDCardBitmap = null;
    public static String IdentifyActivity = "IdentifyActivity";
    public static int[][] adjustProgressArray = {new int[]{0, 128}, new int[]{1, 78}, new int[]{2, 66}, new int[]{3, 0}};
    public static String ascending_date = "Ascending date";
    public static String ascending_name = "Ascending name";
    public static Bitmap bookBitmap = null;
    public static String card_type = "Single";
    public static ColorMatrixColorFilter[] coloreffect = {new ColorMatrixColorFilter(new float[]{1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f}), new ColorMatrixColorFilter(new float[]{1.0f, 0.0f, 0.0f, 0.0f, -60.0f, 0.0f, 1.0f, 0.0f, 0.0f, -60.0f, 0.0f, 0.0f, 1.0f, 0.0f, -90.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f}), new ColorMatrixColorFilter(new float[]{0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f}), new ColorMatrixColorFilter(new float[]{1.1953125f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.671875f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.3984375f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.7265625f, 0.0f}), new ColorMatrixColorFilter(new float[]{1.1953125f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.671875f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.3984375f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.8671875f, 0.0f})};
    public static String current_camera_view = "Document";
    public static String current_tag = "All Docs";
    public static String descending_date = "Descending date";
    public static String descending_name = "Descending name";
    public static AppLangSessionManager appLangSessionManager;

    public static void shareApp(Activity activity) {
        try {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT,
                    activity.getResources().getString(R.string.check_out_my_app)+
                            "https://play.google.com/store/apps/details?id=" + activity.getPackageName());            sendIntent.setType("text/plain");
            activity.startActivity(sendIntent);
        } catch (Exception e) {
            e.toString();
        }
    }

    public static void rateApp(Activity activity) {
        Uri uri = Uri.parse("market://details?id=" + activity.getPackageName());
        Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            activity.startActivity(myAppLinkToMarket);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(activity, activity.getResources().getString(R.string.unable_to_find_market_app), Toast.LENGTH_LONG).show();
        }
    }
    public static String getDateTime(String str) {
        return new SimpleDateFormat(str).format(new Date());
    }
    public static int getProgressPercentage(int i, int i2) {
        return (i * 100) / i2;
    }

    public static final String MESSAGE = "message";
    public static final String IS_SERVICE_STARTED = "isServiceStarted";
    public static final String PREF_SERVER_PORT = "prefServerPort";
    public static final int DEFAULT_SERVER_PORT = 4567;
    public static final String ACCEPT_REQUEST = "acceptRequest";
    public static final boolean DEFAULT_ACCEPT_REQUEST = false;
    public static final Boolean LOG_DEBUG = false;
    public static final String[][] EXTENSIONS =
            {
                    /*Image*/
                    {
                            ".tif", ".tiff", ".gif", ".jpeg", ".jpg", ".jif", ".jfif", ".png", ".gif", ".jpeg", ".jpg", ".jif"
                    },
                    /*Video*/
                    {
                            ".webm", ".mp4", ".ogg"
                    },
                    /*Music*/
                    {
                            ".mp3", ".ogg", ".wav", ".acc"
                    },
                    /*Documents*/
                    {
                            ".doc", ".docx", ".pdf", ".pages", ".pub", ".epub", ".xps", ".odt", ".dotx", ".dot", ".ppt", ".vcard", ".dox", ".pptx"
                    }
            };
}
