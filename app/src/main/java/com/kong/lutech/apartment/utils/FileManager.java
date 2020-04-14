package com.kong.lutech.apartment.utils;

import android.os.Environment;
import android.util.Log;

import java.io.File;

/**
 * Created by gimdonghyeog on 2017. 11. 8..
 * KDH
 */
public class FileManager {

    public boolean isExternalStorageWriteable() {
        final String state = Environment.getExternalStorageState();

        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public File getFileStorageDir(String dirName) {
        final File file = new File(Environment.getExternalStorageDirectory(), dirName);
        if (!file.exists()) {
            if(!file.mkdirs()) {
                Log.e("FileManager", "Directory not created");
            }
        }
        return file;
    }
}
