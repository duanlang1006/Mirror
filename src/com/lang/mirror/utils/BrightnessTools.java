package com.lang.mirror.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.net.Uri;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.view.WindowManager;

/**
 * manage bright's tool
 */
public class BrightnessTools {

    /** * is autobright is on */
    public static boolean isAutoBrightness(ContentResolver aContentResolver) {

        boolean automicBrightness = false;

        try {

            automicBrightness = Settings.System.getInt(aContentResolver,

            Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;

        }

        catch (SettingNotFoundException e)

        {

            e.printStackTrace();

        }

        return automicBrightness;
    }

    /** * get screen bright */
    public static int getScreenBrightness(Activity activity) {

        int nowBrightnessValue = 0;

        ContentResolver resolver = activity.getContentResolver();

        try {
            nowBrightnessValue = android.provider.Settings.System.getInt(resolver, Settings.System.SCREEN_BRIGHTNESS);

        }

        catch (Exception e) {

            e.printStackTrace();

        }

        return nowBrightnessValue;
    }

    /** * set bright */
    public static void setBrightness(Activity activity, int brightness) {
        // activity = activity.getParent();
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        float value = Float.valueOf(brightness) * (1f / 255f);
        if (value > 1.0f) {
            value = 0.99f;
        }
        if (value < 0.0f) {
            value = 0.0f;
        }
        lp.screenBrightness = value;
        activity.getWindow().setAttributes(lp);
    }

    /** * stop auto bright */
    public static void stopAutoBrightness(Activity activity) {
        Settings.System.putInt(activity.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
    }

    /**
     * * start auto bright *
     * 
     * @param activity
     */
    public static void startAutoBrightness(Activity activity) {

        Settings.System.putInt(activity.getContentResolver(),

        Settings.System.SCREEN_BRIGHTNESS_MODE,

        Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);

    }

    /** * save setting state */
    public static void saveBrightness(ContentResolver resolver, int brightness) {

        Uri uri = android.provider.Settings.System.getUriFor("screen_brightness");

        android.provider.Settings.System.putInt(resolver, "screen_brightness", brightness);

        resolver.notifyChange(uri, null);
    }
}
