package com.aransmith.app.receiptlogger.services;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;

/**
 * Created by Aran on 2/19/2016.
 */
public class CameraActivitySetup {

    private static final String TAG = "StartCamera.java";

    private String path;

    public CameraActivitySetup(String path) {
        this.path = path;
    }

    public Intent startCameraActivity() {
        File file = new File(path);
        Uri outputFileUri = Uri.fromFile(file);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

        return intent;
    }
}