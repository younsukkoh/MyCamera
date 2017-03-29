package com.younsukkoh.foundation.mycamera.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.util.Log;

import java.math.BigInteger;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteOrder;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import cz.msebera.android.httpclient.conn.util.InetAddressUtils;

import static android.content.Context.WIFI_SERVICE;

/**
 * Utility methods that can be used by all classes
 *
 * Created by Younsuk on 12/8/2016.
 */

public class Utility {

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
