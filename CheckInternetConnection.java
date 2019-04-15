package com.quicklib.quicklib;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by root on 10/7/17.
 */

public class CheckInternetConnection {

    public static boolean checkConnection(Context context) {
        final ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetworkInfo = null;
        if (connMgr != null) {
            activeNetworkInfo = connMgr.getActiveNetworkInfo();
        }

        if (activeNetworkInfo != null) { // connected to the internet

            if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {

                return true ;
            } else if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                // connected to the mobile provider's data plan
                return true;
            }
        }
        return false;
    }
}
