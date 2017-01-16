package com.younsukkoh.foundation.mycamera.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Run-time permission for Android Marshmallow and above.
 * Created by Younsuk on 12/9/2016.
 */

public class RuntimePermissions {

    public static final int REQUEST_CAMERA_PERMISSION = 1;
    public static final String[] CAMERA_PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /**
     * Check if Android SDK requires run-time permission.
     * Then check which permissions have been granted (because those do not need to be asked again).
     * Then convert the list of permissions that has not been granted to string array.
     * Finally, request permissions.
     *
     * @param applicationContext getApplicationContext()
     * @param activity Activity from which the method was called.
     * @param list               String array of permissions in the manifest.
     */
    public static void checkAndRequestPermissions(Context applicationContext, Activity activity, String[] list) {
        // Check if Android SDK requires run-time permission.
        // Only SDK above Android Marshmallow requires run-time permissions.
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M)
            return;

        // needPermissionList will take permissions that are not granted yet.
        ArrayList<String> needPermissionList = new ArrayList<>();
        // Check which permissions have been granted.
        for (int i = 0; i < list.length; i++)
            // If this permission is denied or not granted yet, then add it to needPermissionList.
            if (ActivityCompat.checkSelfPermission(applicationContext, list[i]) != PackageManager.PERMISSION_GRANTED)
                needPermissionList.add(list[i]);

        // If all permissions are granted. Return.
        if (needPermissionList.size() == 0) return;

        //Convert array list of permissions that need grant to string array
        Object[] objects = needPermissionList.toArray();
        String[] finalList = Arrays.copyOf(objects, objects.length, String[].class);

        // Request permissions with only those that are not granted permissions yet.
        ActivityCompat.requestPermissions(activity, finalList, REQUEST_CAMERA_PERMISSION);
    }

    /**
     * When permission is denied ask the user again for permission.
     *
     * @param applicationContext getApplicationContext()
     * @param activity Activity from which the method was called.
     * @param list               String array of permissions in the manifest.
     */
    public static void showPermissionDialog(final Context applicationContext, final Activity activity, final String[] list) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                .setTitle("Alert")

                .setMessage("Your permission is essential for full experience of this application! Would you please give us your permission?")

                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        RuntimePermissions.checkAndRequestPermissions(applicationContext, activity, list);
                    }
                })

                .setNeutralButton("Cancel", null)

                .setNegativeButton("Checked 'Never ask again'. Change it in Settings.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Go through and check if the users checked "Never ask again."
                        for (String permission : list) {
                            //The user checked "Never ask again" Open Settings.
                            if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                                Utility.goToAppInfo(applicationContext);
                                break;
                            }
                        }
                    }
                });

        builder.create().show();
    }
}
