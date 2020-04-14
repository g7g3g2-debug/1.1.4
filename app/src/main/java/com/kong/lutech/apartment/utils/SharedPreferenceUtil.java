package com.kong.lutech.apartment.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.kong.lutech.apartment.Config;

/**
 * Created by gimdonghyeog on 2017. 5. 24..
 */

public class SharedPreferenceUtil {

    private SharedPreferences pref;

    public SharedPreferenceUtil(Context context) {
        pref = context.getSharedPreferences(Config.PREFERENCE_KEY, Context.MODE_PRIVATE);
    }

    public SharedPreferenceUtil(Context context, String name) {
        pref = context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    public SharedPreferences getPref() {
        return pref;
    }

    public static class Builder {

        private SharedPreferences.Editor editor;

        public Builder(Context context, String name) {
            final SharedPreferences pref = context.getSharedPreferences(name, Context.MODE_PRIVATE);
            editor = pref.edit();
        }

        public Builder(Context context) {
            final SharedPreferences pref = context.getSharedPreferences(Config.PREFERENCE_KEY, Context.MODE_PRIVATE);
            editor = pref.edit();
        }

        public Builder putString(String key, String value) {
            editor.putString(key, value);

            return this;
        }

        public Builder putInt(String key, int value) {
            editor.putInt(key, value);

            return this;
        }

        public void commit() {
            editor.commit();
        }
    }
}
