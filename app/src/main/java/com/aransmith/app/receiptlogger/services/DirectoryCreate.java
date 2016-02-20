package com.aransmith.app.receiptlogger.services;

import android.util.Log;

import java.io.File;

/**
 * Created by Aran on 2/20/2016.
 */
public class DirectoryCreate {

    private static final String TAG = "DirectoryCreate";

    public boolean createDirectories(String[] paths){
        for (String path : paths) {
            File dir = new File(path);
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    Log.v(TAG, "ERROR: Creation of directory " + path + " on sdcard failed");
                    return false;
                } else {
                    Log.v(TAG, "Created directory " + path + " on sdcard");
                }
            }

        }
        return true;
    }
}
