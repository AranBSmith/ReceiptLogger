package com.aransmith.app.receiptlogger.services;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;

/**
 * Created by Aran on 2/19/2016.
 * Used by activities to allow for the use of the devices camera while in app.
 */
public class CameraActivitySetup {

    private static final String TAG = "StartCamera.java";

    private String path;

    /**
     * specify path to which the photo will be saved
     * @param path
     */
    public CameraActivitySetup(String path) {
        this.path = path;
    }

    /**
     * take a photo
     * @return Intent
     */
    public Intent startCameraActivity() {
        File file = new File(path);
        Uri outputFileUri = Uri.fromFile(file);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

        return intent;
    }
}