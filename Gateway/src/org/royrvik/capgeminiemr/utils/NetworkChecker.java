package org.royrvik.capgeminiemr.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class    NetworkChecker {

    /**
     * Utility for checking the status of the internet connection
     * @param context A context to gain network privileges.
     * @return True if the device is connected to the internet.
     */
    public static boolean isNetworkAvailable(Context context) {
        boolean isConnectedWifi = false;
        boolean isConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] networkInfo = cm.getAllNetworkInfo();
        for (NetworkInfo n : networkInfo) {
            if (n.getTypeName().equalsIgnoreCase("WIFI"))
                if (n.isConnected())
                    isConnectedWifi = true;
            if (n.getTypeName().equalsIgnoreCase("MOBILE"))
                if (n.isConnected())
                    isConnectedMobile = true;
        }
        return isConnectedWifi || isConnectedMobile;
    }
}
