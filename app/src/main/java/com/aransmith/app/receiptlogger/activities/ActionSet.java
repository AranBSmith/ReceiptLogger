package com.aransmith.app.receiptlogger.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.aransmith.app.receiptlogger.services.CameraActivitySetup;
import com.aransmith.app.receiptlogger.services.FileSystemService;

import java.io.IOException;

/**
 * Created by Aran on 2/3/2016.
 * Activity class acting as the main menu of the application, allowing the user to either list
 * expenses or log an expense. Upon clicking log an expense the activity will immediately allow
 * the user to take a photo of their expense, after which they will then move to the LogExpenseOptions
 * Activity.
 */
public class ActionSet extends Activity {
    private static final String TAG = "ActionSet";
    private static final int REQUEST_WRITE_STORAGE = FileSystemService.requestWriteStorageCode();

    protected Button logExpenseButton, viewExpensesButton;
    private Bundle bundle;
    private String email, password, path;
    public static final String lang = "eng";

    public static final String DATA_PATH = FileSystemService.getStorageDirectory();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        this.bundle = getIntent().getExtras();
        email = bundle.getString("email");
        password = bundle.getString("password");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.actionset);

        logExpenseButton = (Button) findViewById(R.id.expenselog);
        viewExpensesButton = (Button) findViewById(R.id.listexpenses);

        logExpenseButton.setOnClickListener(new logExpenseClickHandler());
        viewExpensesButton.setOnClickListener(new listExpenseHandler());

        path = DATA_PATH + "/ocr.png";

        boolean hasPermission = (
            ContextCompat.checkSelfPermission(
            ActionSet.this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);

        if (!hasPermission) {
            ActivityCompat.requestPermissions(this.getParent(),
            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
            REQUEST_WRITE_STORAGE);
        }

        String[] paths = new String[] { DATA_PATH, DATA_PATH + "tessdata/" };

        FileSystemService fileSystemService = new FileSystemService();
        if(!fileSystemService.createDirectories(paths)){
            Log.i(TAG, "ERROR: Not all directories were created so this activity cannot proceed");
        }

        AssetManager assetManager = getAssets();

        try {
            fileSystemService.storeTrainedData(assetManager);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Was unable to copy " + lang + " traineddata " + e.toString());
        }

        path = DATA_PATH + "/ocr.png";
    }

    public class logExpenseClickHandler implements View.OnClickListener {
        public void onClick(View view){

            Log.v(TAG, "Starting Camera app");
            startCameraActivity();
        }
    }

    private void startCameraActivity() {
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

    private void onPhotoTaken() {
        Log.i(TAG, "logExpenseButton has been pressed. Moving to new activity");
        Intent i = new Intent(getApplicationContext(), LogExpenseOptions.class);
        i.putExtra("email", email);
        i.putExtra("password", password);
        startActivity(i);
    }

    public class listExpenseHandler implements View.OnClickListener {
        public void onClick(View view){
            Log.i(TAG, "listExpenseButton has been pressed. Moving to new activity");
            Intent i = new Intent(getApplicationContext(), ListExpenses.class);
            i.putExtra("email", email);
            i.putExtra("password", password);

            startActivity(i);
        }
    }

}
