package com.aransmith.app.receiptlogger.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.aransmith.app.receiptlogger.interfaces.AsyncBoolResponse;
import com.aransmith.app.receiptlogger.model.Categories;
import com.aransmith.app.receiptlogger.model.Expense;
import com.aransmith.app.receiptlogger.services.CameraActivitySetup;
import com.aransmith.app.receiptlogger.services.DirectoryCreate;
import com.aransmith.app.receiptlogger.services.ExpenseService;
import com.aransmith.app.receiptlogger.services.FieldExtractor;
import com.aransmith.app.receiptlogger.services.PerformOCR;
import com.aransmith.app.receiptlogger.services.PhotoOrient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

public class AutoLogExpense extends Activity {
    public static final String DATA_PATH = Environment.getExternalStorageDirectory().toString() +
            "/AutoLogExpense/";

    public static final String lang = "eng";

    private static final String TAG = "SimpleAndroidOCR.java";
    private static final String PHOTO_TAKEN = "photo_taken";
    private static final int REQUEST_WRITE_STORAGE = 112;

    private Button button, submitExpenseButton;
    private EditText textField, descriptionTextField;
    private Spinner spinner;
    private String path;
    private boolean photoTaken;
    private Bundle bundle;
    private FieldExtractor fieldExtractor;

    HashMap<String, String> priceValues;

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

        path = DATA_PATH + "/ocr.jpg";

        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        textField = (EditText) findViewById(R.id.field);
        descriptionTextField = (EditText) findViewById(R.id.expensedescription);
        button = (Button) findViewById(R.id.takepicbutton);
        button.setOnClickListener(new PhotoButtonClickHandler());
        submitExpenseButton = (Button) findViewById(R.id.submitexpensebutton);
        submitExpenseButton.setOnClickListener(new SubmitExpenseClickHandler());

        spinner = (Spinner)findViewById(R.id.spinner);

        // get an array of strings which are valid categories
        String[] items = new Categories().getCategories();
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
        spinner.setAdapter(adapter);
    }

    public class PhotoButtonClickHandler implements View.OnClickListener {
        public void onClick(View view) {
            Log.v(TAG, "Starting Camera app");
            startCameraActivity();
        }
    }

    // submit an expense object, if it nothing instantiated in the expense object notify the user
    // to fill in the required fields.
    public class SubmitExpenseClickHandler implements View.OnClickListener, AsyncBoolResponse {
        public void onClick(View view) {
            Log.v(TAG, "Submitting expense");
            bundle = getIntent().getExtras();
            String email = bundle.getString("email");

            priceValues = new HashMap<>();

            EditText priceTextField = (EditText) findViewById(R.id.price);
            priceValues.put("amount", priceTextField.getText().toString());

            EditText currencyTextField = (EditText) findViewById(R.id.currency);
            priceValues.put("currency", currencyTextField.getText().toString());

            EditText dateTextField = (EditText) findViewById(R.id.date);
            priceValues.put("date", dateTextField.getText().toString());

            EditText descriptionTextField = (EditText) findViewById(R.id.expensedescription);
            priceValues.put("description", descriptionTextField.getText().toString());

            Expense expense = new Expense(email, Double.parseDouble(priceValues.get("amount")),
                    priceValues.get("currency"), spinner.getSelectedItem().toString(),
                    priceValues.get("date"),  priceValues.get("description"),
                    "Random".getBytes(), false);

            MyAsyncTask asyncTask = new MyAsyncTask();
            asyncTask.delegate = this;
            asyncTask.execute(expense);
        }

        // the process was finished and now we must notify the user
        public void processFinish(Boolean result){
            if(result != null){
                if(result){
                    Log.v(TAG, "Expense submission was successful");
                    Intent i = new Intent(getApplicationContext(),ActionSet.class);
                    i.putExtra("email", "aran.smith47@mail.dcu.ie");

                    Context context = getApplicationContext();
                    CharSequence text = "Expense submission was successful!";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();

                    startActivity(i);
                    setContentView(R.layout.actionset);

                } else {
                    Log.v(TAG, "Expense submission was unsuccessful");

                    Context context = getApplicationContext();
                    CharSequence text = "Expense submission was unsuccessful!";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
            }
        }
    }

    private class MyAsyncTask extends AsyncTask<Expense, Void, Boolean> {

        public AsyncBoolResponse delegate = null;

        @Override
        protected Boolean doInBackground(Expense... params) {
            Expense expense = params[0];
            ExpenseService expenseService = new ExpenseService();

            // expense submission is true
            if(expenseService.submitExpense(expense))
                return true;

            else return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            delegate.processFinish(result);
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

        if ( lang.equalsIgnoreCase("eng") ) {
            expenseText = expenseText.replaceAll("[^a-zA-Z0-9]+", " ");
        }

        expenseText = expenseText.trim();

        if ( expenseText.length() != 0 ) {
            fieldExtractor = new FieldExtractor();

            textField.setText(textField.getText().toString().length() == 0 ? expenseText :
                    textField.getText() + " " + expenseText);
            textField.setSelection(textField.getText().toString().length());


            // set price field to price found
            System.out.println(expenseText);
            HashMap<String,String> priceInfo = fieldExtractor.getFields(expenseText);

            EditText priceTextField = (EditText) findViewById(R.id.price);
            priceTextField.setText(priceInfo.get("amount"));

            EditText currencyTextField = (EditText) findViewById(R.id.currency);
            currencyTextField.setText(priceInfo.get("currency"));

            EditText dateTextField = (EditText) findViewById(R.id.date);
            dateTextField.setText(priceInfo.get("date"));

            System.out.println("price information is: " + priceInfo);

            priceValues = priceInfo;
        }
    }
}