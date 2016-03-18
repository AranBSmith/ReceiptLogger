package com.aransmith.app.receiptlogger.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.aransmith.app.receiptlogger.services.CameraActivitySetup;
import com.aransmith.app.receiptlogger.services.DirectoryCreate;
import com.aransmith.app.receiptlogger.services.PerformOCR;
import com.aransmith.app.receiptlogger.services.PhotoOrient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class AutoLogExpense extends Activity {
    public static final String DATA_PATH = Environment.getExternalStorageDirectory().toString() +
            "/AutoLogExpense/";

    public static final String lang = "eng";

    private static final String TAG = "SimpleAndroidOCR.java";
    protected static final String PHOTO_TAKEN = "photo_taken";
    private static final int REQUEST_WRITE_STORAGE = 112;

    protected Button button;
    protected EditText textField;
    protected String path;
    protected boolean photoTaken;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        String[] paths = new String[] { DATA_PATH, DATA_PATH + "tessdata/" };

        boolean hasPermission = (ContextCompat.checkSelfPermission(AutoLogExpense.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission) {
            ActivityCompat.requestPermissions(this.getParent() ,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_STORAGE);
        }

        DirectoryCreate dcreate = new DirectoryCreate();
        if(!dcreate.createDirectories(paths)){
            Log.i(TAG, "ERROR: Not all directories were created so this activity cannot proceed");
        }

        if (!(new File(DATA_PATH + "tessdata/" + lang + ".traineddata")).exists()) {
            try {

                AssetManager assetManager = getAssets();
                InputStream in = assetManager.open("tessdata/" + lang + ".traineddata");
                OutputStream out = new FileOutputStream(DATA_PATH
                        + "tessdata/" + lang + ".traineddata");

                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                out.close();

                Log.v(TAG, "Copied " + lang + " traineddata");
            } catch (IOException e) {
                Log.e(TAG, "Was unable to copy " + lang + " traineddata " + e.toString());
            }
        }

        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        textField = (EditText) findViewById(R.id.field);
        button = (Button) findViewById(R.id.takepicbutton);
        button.setOnClickListener(new ButtonClickHandler());

        path = DATA_PATH + "/ocr.jpg";
    }

    public class ButtonClickHandler implements View.OnClickListener {
        public void onClick(View view) {
            Log.v(TAG, "Starting Camera app");
            startCameraActivity();
        }
    }

    protected void startCameraActivity() {
        Intent intent = null;
        CameraActivitySetup camerAcivitySetup = new CameraActivitySetup(path);
        intent = camerAcivitySetup.startCameraActivity();
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.i(TAG, "resultCode: " + resultCode);

        if (resultCode == -1) {
            onPhotoTaken();
        } else {
            Log.v(TAG, "User cancelled");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(AutoLogExpense.PHOTO_TAKEN, photoTaken);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.i(TAG, "onRestoreInstanceState()");
        if (savedInstanceState.getBoolean(AutoLogExpense.PHOTO_TAKEN)) {
            onPhotoTaken();
        }
    }

    protected void onPhotoTaken() {
        photoTaken = true;
        PhotoOrient photoOrient = new PhotoOrient(path);
        Bitmap bitmap = photoOrient.orientImage();

        PerformOCR performOCR = new PerformOCR(bitmap, lang, DATA_PATH);
        String expenseText = performOCR.performOCR();

        //Log.v(TAG, "OCRED TEXT: " + expenseText);

        if ( lang.equalsIgnoreCase("eng") ) {
            expenseText = expenseText.replaceAll("[^a-zA-Z0-9]+", " ");
        }

        expenseText = expenseText.trim();

        if ( expenseText.length() != 0 ) {
            textField.setText(textField.getText().toString().length() == 0 ? expenseText : textField.getText()
                    + " " + expenseText);
            textField.setSelection(textField.getText().toString().length());
        }
    }
}