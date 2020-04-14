package com.kong.lutech.apartment.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.Arrays;
import java.util.List;

/**
 * Created by kimdonghyuk on 2017. 3. 29..
 */

public class ServiceMonitor {

    private static final String TAG = "ServiceMonitor";

    public static boolean isServiceRunning(Context context, Class<?> cls) {
        boolean isRunning = false;

        final List<ActivityManager.RunningServiceInfo> info = getRunningServices(context);

        if (info != null) {
            for (ActivityManager.RunningServiceInfo serviceInfo : info) {
                String className = serviceInfo.service.getClassName();

                if (className.equals(cls.getName())) {
                    isRunning = true;
                    break;
                }
            }
        }
        return isRunning;
    }

    public static List<ActivityManager.RunningServiceInfo> getRunningServices(Context context) {
        ActivityManager activityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        return activityManager.getRunningServices(Integer.MAX_VALUE);
    }

    public static boolean isServiceRunningInForeground(Context context, Class<?> cls) {
        boolean isRunning = false;

        final List<ActivityManager.RunningServiceInfo> info = getRunningServices(context);

        if (info != null) {
            for (ActivityManager.RunningServiceInfo serviceInfo : info) {
                String className = serviceInfo.service.getClassName();

                if (className.equals(cls.getName())) {
                    isRunning = serviceInfo.foreground;
                    break;
                }
            }
        }
        return isRunning;
    }

    public static boolean isRunningApp(Context context, String name) {
        boolean isRunning = false;
        ActivityManager am = (ActivityManager)context.getSystemService(Activity.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcessInfos = am.getRunningAppProcesses();

        for (ActivityManager.RunningAppProcessInfo runningApp: appProcessInfos) {
            Log.d(TAG, "Name : "+ runningApp.processName+", Length : "+runningApp.pkgList.length);

            if (runningApp.processName.equals(name)) {
                isRunning = true;
                break;
            }
        }

        return isRunning;
    }

    public static boolean appInForeground(@NonNull Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
        if (runningAppProcesses == null) {
            return false;
        }

        for (ActivityManager.RunningAppProcessInfo runningAppProcess : runningAppProcesses) {
            if (runningAppProcess.processName.equals(context.getPackageName()) &&
                    runningAppProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }
        return false;
    }
}
