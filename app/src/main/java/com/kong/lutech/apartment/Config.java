package com.kong.lutech.apartment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;
import com.kong.lutech.apartment.ui.StartActivity;
import com.kong.lutech.apartment.model.Mobile;
import com.kong.lutech.apartment.model.MobileRssi;
import com.kong.lutech.apartment.model.Permission;
import com.kong.lutech.apartment.utils.SharedPreferenceUtil;
import com.kongtech.smapsdk.services.PersistentService;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by gimdonghyeog on 2017. 5. 24..
 */

public class Config {
    public static final String KEY = "kongtechkongtech";

    public static final String PREFERENCE_KEY = "prefs";
    public static final String PREFERENCE_KEY_ACCESS_TOKEN = "accessToken";
    public static final String PREFERENCE_KEY_AUTHCODE = "authcode";
    public static final String PREFERENCE_KEY_MOBILE = "mobile";
    public static final String PREFERENCE_KEY_MOBILE_RSSI = "mobile_rssi";
    public static final String PREFERENCE_KEY_PERMISSION = "permission";
    public static final String PREFERENCE_KEY_THRESHOLD_INDEX = "threshold-index";
    public static final String PREFERENCE_KEY_DISCOVER_RANGE = "discover-range";


    private static String accessToken;
    private static Mobile mobile;
    private static String authCode;
    private static MobileRssi mobileRssi;
    private static Permission permission;


    // Getter & Setter
    public static Permission getPermission() {
        return permission;
    }

    public static void setPermission(Permission permission) {
        Config.permission = permission;
    }

    public static MobileRssi getMobileRssi() {
        return mobileRssi;
    }

    public static void setMobileRssi(MobileRssi mobileRssi) {
        Config.mobileRssi = mobileRssi;
    }

    public static String getAuthCode() {
        return authCode;
    }

    public static void setAuthCode(String authCode) {
        Config.authCode = authCode;
    }

    public static String getAccessToken() {
        try {
            return Config.Decrypt(accessToken, KEY);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void setAccessToken(String accesstoken) {
        try {
            Config.accessToken = Config.Encrypt(accesstoken, KEY);
        } catch (Exception e) {
            e.printStackTrace();
            Config.accessToken = null;
        }
    }

    public static Mobile getMobile() {
        return mobile;
    }

    public static void setMobile(Mobile mobile) {
        Config.mobile = mobile;
    }


    // Scan Option
    public static double getRatioValue() {
        return mobileRssi.getRatioValue();
    }

    public static int getThreshold(Context context) {
        final int index = getThresholdIndex(context);
        switch (index) {
            case 0:
                return mobileRssi.getLowValue();
            case 2:
                return mobileRssi.getHighValue();
            default:
                return mobileRssi.getMiddleValue();
        }
    }

    public static int getThresholdIndex(Context context) {
        final SharedPreferences pref = context.getSharedPreferences(PREFERENCE_KEY, Context.MODE_PRIVATE);
        final int index = pref.getInt(PREFERENCE_KEY_THRESHOLD_INDEX, 1);
        return index;
    }

    public static int getDiscoverRange(Context context) {
        final SharedPreferences pref = context.getSharedPreferences(PREFERENCE_KEY, Context.MODE_PRIVATE);
        final int discoverRange = pref.getInt(PREFERENCE_KEY_DISCOVER_RANGE, getThreshold(context) - 10);
        return discoverRange;
    }

    public static void setDiscoverRange(Context context, int newDiscoverRange) {
        final SharedPreferences pref = context.getSharedPreferences(PREFERENCE_KEY, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = pref.edit();
        editor.putInt(PREFERENCE_KEY_DISCOVER_RANGE, newDiscoverRange);
        editor.apply();
    }

    public static boolean Check() {
        return !TextUtils.isEmpty(accessToken) && mobile != null && mobileRssi != null && !TextUtils.isEmpty(authCode) && permission != null;
    }

    public static boolean isLogin(Context context) {
        final SharedPreferences pref = new SharedPreferenceUtil(context).getPref();
        Log.d("Config", pref.getString(PREFERENCE_KEY_MOBILE,""));

        final String accessToken = pref.getString(PREFERENCE_KEY_ACCESS_TOKEN, "");
        final String authcode = pref.getString(PREFERENCE_KEY_AUTHCODE, "");
        Log.d("Config", authcode);
        final Mobile mobile = new Gson().fromJson(pref.getString(PREFERENCE_KEY_MOBILE,""), Mobile.class);
        final MobileRssi mobileRssi = new Gson().fromJson(pref.getString(PREFERENCE_KEY_MOBILE_RSSI, ""), MobileRssi.class);
        final Permission permission = new Gson().fromJson(pref.getString(PREFERENCE_KEY_PERMISSION, ""), Permission.class);

        return !(TextUtils.isEmpty(accessToken) || mobile == null || TextUtils.isEmpty(authcode) || mobileRssi == null || mobile.getDong() == null || mobile.getHome() == null || permission == null);
    }

    public static void CheckingUser(Activity context) {
        final SharedPreferences pref = new SharedPreferenceUtil(context).getPref();
        final String accessToken = pref.getString(PREFERENCE_KEY_ACCESS_TOKEN, "");
        final String authcode = pref.getString(PREFERENCE_KEY_AUTHCODE, "");
        final Mobile mobile = new Gson().fromJson(pref.getString(PREFERENCE_KEY_MOBILE, ""), Mobile.class);
        final MobileRssi mobileRssi = new Gson().fromJson(pref.getString(PREFERENCE_KEY_MOBILE_RSSI, ""), MobileRssi.class);
        final Permission permission = new Gson().fromJson(pref.getString(PREFERENCE_KEY_PERMISSION, ""), Permission.class);

        if (TextUtils.isEmpty(accessToken) || mobile == null || TextUtils.isEmpty(authcode) || mobileRssi == null) {

            context.sendBroadcast(new Intent(PersistentService.INTENT_LOGOUT));
            pref.edit().clear().apply();

            Intent intent = new Intent(context, StartActivity.class);
            context.startActivity(intent);
            context.finishAffinity();
        } else {
            Config.accessToken = accessToken;
            Config.setMobile(mobile);
            Config.setAuthCode(authcode);
            Config.setMobileRssi(mobileRssi);
            Config.setPermission(permission);
        }
    }

    public static void settingData(Context context) {
        final SharedPreferences pref = new SharedPreferenceUtil(context).getPref();
        final String accessToken = pref.getString(PREFERENCE_KEY_ACCESS_TOKEN, "");
        final String authcode = pref.getString(PREFERENCE_KEY_AUTHCODE, "");
        final Mobile mobile = new Gson().fromJson(pref.getString(PREFERENCE_KEY_MOBILE,""), Mobile.class);
        final MobileRssi mobileRssi = new Gson().fromJson(pref.getString(PREFERENCE_KEY_MOBILE_RSSI, ""), MobileRssi.class);
        final Permission permission = new Gson().fromJson(pref.getString(PREFERENCE_KEY_PERMISSION, ""), Permission.class);

        Config.accessToken = accessToken;
        Config.setMobile(mobile);
        Config.setAuthCode(authcode);
        Config.setMobileRssi(mobileRssi);
        Config.setPermission(permission);
    }

    public static void Logout() {
        accessToken = null;
        mobile = null;
        authCode = null;
        mobileRssi = null;
        permission = null;
    }


    // Token Encrypt & Decrpyt
    public static String Decrypt(String text, String key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] keyBytes = new byte[16];
        byte[] b = key.getBytes("UTF-8");
        int len = b.length;
        if (len > keyBytes.length) len = keyBytes.length;
        System.arraycopy(b, 0, keyBytes, 0, len);
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(keyBytes);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

        byte[] results = cipher.doFinal(Base64.decode(text, 0));

        return new String(results, "UTF-8");
    }

    public static String Encrypt(String text, String key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] keyBytes = new byte[16];
        byte[] b = key.getBytes("UTF-8");
        int len = b.length;
        if (len > keyBytes.length) len = keyBytes.length;
        System.arraycopy(b, 0, keyBytes, 0, len);
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(keyBytes);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

        byte[] results = cipher.doFinal(text.getBytes("UTF-8"));

        return Base64.encodeToString(results, 0);
    }
}
