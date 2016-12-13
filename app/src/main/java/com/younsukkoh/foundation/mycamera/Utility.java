package com.younsukkoh.foundation.mycamera;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

/**
 * Created by Younsuk on 12/8/2016.
 */

public class Utility {
    public static final int REQUEST_CAMERA_PERMISSION = 1;

    /**
     * Take the user to application information, where the user can change permission status.
     */
    public static void goToAppInfo(Context applicationContext) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + applicationContext.getPackageName()));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        applicationContext.startActivity(intent);
    }
}
