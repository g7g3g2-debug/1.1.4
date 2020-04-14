package com.kong.lutech.apartment.utils.playservice;

import android.content.Context;
import android.content.pm.PackageManager;

/**
 * Created by kimdonghyuk on 2018. 4. 23..
 */

public class PlayServiceChecker {

    private static final int TargetVersion = 3;

    public static boolean isValid(Context context) {
        final int version = getVersion(context);
        if (version < 0) {
            return false;
        } else {
            return version >= PlayServiceChecker.TargetVersion;
        }
    }

    public static int getVersion(Context context) {
        try {

            return context.getPackageManager().getPackageInfo("com.google.android.gms", 0 ).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return -1;
        }
    }
}
