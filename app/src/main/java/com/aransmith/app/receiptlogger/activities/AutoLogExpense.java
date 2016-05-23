package com.aransmith.app.receiptlogger.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.aransmith.app.receiptlogger.interfaces.AsyncExpenseResponse;
import com.aransmith.app.receiptlogger.interfaces.AsyncHashMapResponse;
import com.aransmith.app.receiptlogger.interfaces.FeedbackNotificationActivity;
import com.aransmith.app.receiptlogger.model.Categories;
import com.aransmith.app.receiptlogger.model.Currencies;
import com.aransmith.app.receiptlogger.model.Expense;
import com.aransmith.app.receiptlogger.model.ExpenseSubmissionResponse;
import com.aransmith.app.receiptlogger.services.DateService;
import com.aransmith.app.receiptlogger.services.ExpenseService;
import com.aransmith.app.receiptlogger.services.FieldExtractor;
import com.aransmith.app.receiptlogger.services.FileSystemService;
import com.aransmith.app.receiptlogger.services.ImageService;
import com.aransmith.app.receiptlogger.services.PerformOCR;
import com.aransmith.app.receiptlogger.services.PhotoOrient;
import com.aransmith.app.receiptlogger.services.PriceService;
import com.aransmith.app.receiptlogger.services.UserInputChecker;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by Aran on 2/3/2016.
 * Activity class for logging an expense, carries out the same function as the ManuallyLogExpense
 * Activity with the addition of performing OCR on the photo taken by the user. OCR is in this
 * activity is performed separately from the UI thread so as not to freeze the app when processing
 * an image. This class also submits expense information in the background, it will handle user
 * inputs and prevent illegal user expense submissions. Upon submitting an expense the ActionSet
 * Activity will be displayed.
 */
public class AutoLogExpense extends FeedbackNotificationActivity implements AsyncHashMapResponse {
    public static final String DATA_PATH = FileSystemService.getStorageDirectory();

    public static final String lang = "eng";

    private static final String TAG = "AutoLogExpense.java";
    private static final String PHOTO_TAKEN = "photo_taken";
    private static final int REQUEST_WRITE_STORAGE = FileSystemService.requestWriteStorageCode();

    private Button button, submitExpenseButton, cancelButton;
    private EditText textField, descriptionTextField;
    private Spinner categorySpinner, currencySpinner;
    private String path;
    private boolean photoTaken;
    private Bundle bundle;
    private FieldExtractor fieldExtractor;
    private ProgressDialog mDialog;
    private Currencies currencies;

    private String email, password;

    private HashMap<String, String> priceValues;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        this.bundle = getIntent().getExtras();
        email = bundle.getString("email");
        password = bundle.getString("password");

        String[] paths = new String[] { DATA_PATH, DATA_PATH + "tessdata/" };

        boolean hasPermission = (ContextCompat.checkSelfPermission(AutoLogExpense.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission) {
            ActivityCompat.requestPermissions(this.getParent() ,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_STORAGE);
        }

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

        path = DATA_PATH + "/ocr.png";

        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        // used in debugging
        /*textField = (EditText) findViewById(R.id.field);
        descriptionTextField = (EditText) findViewById(R.id.expensedescription);*/

        submitExpenseButton = (Button) findViewById(R.id.submitexpensebutton);
        submitExpenseButton.setOnClickListener(new SubmitExpenseClickHandler());

        cancelButton = (Button) findViewById(R.id.cancel);
        cancelButton.setOnClickListener(new CancelClickHandler());

        categorySpinner = (Spinner) findViewById(R.id.spinner);
        currencySpinner = (Spinner) findViewById(R.id.currency);

        // get an array of strings which are valid categories
        String[] categories = new Categories().getCategories();

        currencies = new Currencies();
        String[] currencyList = currencies.getCurrencies();

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories);

        categorySpinner.setAdapter(adapter);

        adapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, currencyList);

        currencySpinner.setAdapter(adapter);

        // performOCR();

        PerformOCRTask ocrTask = new PerformOCRTask();
        ocrTask.delegate = this;
        ocrTask.execute();
    }

    private boolean noNullValues(HashMap<String, String> values){
        // iterate through keys
        for(String key: values.keySet()){
            String value = values.get(key);
            if(UserInputChecker.noValue(value)){
                return false;
            }
        }

        return true;
    }

    public class CancelClickHandler implements View.OnClickListener {
        public void onClick(View view){
            Intent i = new Intent(getApplicationContext(), ActionSet.class);

            i.putExtra("email", email);
            i.putExtra("password", password);

            startActivity(i);
        }
    }

    // submit an expense object, if it nothing instantiated in the expense object notify the user
    // to fill in the required fields.
    public class SubmitExpenseClickHandler implements View.OnClickListener, AsyncExpenseResponse {
        public void onClick(View view) {
            Log.v(TAG, "Submitting expense");
            bundle = getIntent().getExtras();

            priceValues = new HashMap<>();

            EditText priceTextField = (EditText) findViewById(R.id.price);
            priceValues.put("amount", priceTextField.getText().toString());

            EditText cardTextField = (EditText) findViewById(R.id.card);
            priceValues.put("card", cardTextField.getText().toString());

            EditText dateTextField = (EditText) findViewById(R.id.date);
            priceValues.put("date", dateTextField.getText().toString());

            EditText descriptionTextField = (EditText) findViewById(R.id.expensedescription);
            priceValues.put("description", descriptionTextField.getText().toString());

            // there are no empty values
            if(noNullValues(priceValues)){

                // check if text fields go by their proper formats

                boolean validDate = new DateService().checkInputDate(priceValues.get("date"));
                boolean validPrice = new PriceService().checkFormat(priceValues.get("amount"));

                if(validDate && validPrice){
                    Expense expense = new Expense(email, Double.parseDouble(priceValues.get("amount")),
                            currencySpinner.getSelectedItem().toString(), priceValues.get("card"),
                            categorySpinner.getSelectedItem().toString(), priceValues.get("date"),
                            priceValues.get("description"), null);

                    System.out.println("Now submitting information.");
                    MyAsyncTask asyncTask = new MyAsyncTask();
                    asyncTask.delegate = this;
                    asyncTask.execute(expense);

                } else if (!validPrice && !validDate){
                    notifyUser("Price and Date invalid");
                } else if(!validPrice){
                    notifyUser("Price invalid");
                } else if(!validDate){
                    notifyUser("Date invalid");
                }

            } else {
                notifyUser("Please fill all fields");
            }
        }

        // the process was finished and now we must notify the user
        public void processFinish(ExpenseSubmissionResponse result){
            if(result != null){
                if(result.isSuccess()) {
                    Log.v(TAG, "Expense submission was successful");
                    Intent i = new Intent(getApplicationContext(), ActionSet.class);

                    i.putExtra("email", email);
                    i.putExtra("password", password);

                    Context context = getApplicationContext();
                    CharSequence text = "Expense submission was successful!";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();

                    startActivity(i);

                } else {
                    Log.v(TAG, "Expense submission was unsuccessful!");
                    Log.v(TAG, result.getResponse());

                    Context context = getApplicationContext();
                    CharSequence text = "Expense submission was unsuccessful!";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
            }
        }
    }

    private class MyAsyncTask extends AsyncTask<Expense, Void, ExpenseSubmissionResponse> {

        public AsyncExpenseResponse delegate = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mDialog = new ProgressDialog(AutoLogExpense.this);
            mDialog.setMessage("Submitting your Expense...");
            mDialog.show();
        }

        @Override
        protected ExpenseSubmissionResponse doInBackground(Expense... params) {
            Expense expense = params[0];
            ExpenseService expenseService = new ExpenseService();

            // getting bytes from an image is slow and shouldnt be done on the UI thread
            ImageService imageService = new ImageService();
            byte[] bytes = imageService.getPNGDataFromJPEG(path);

            expense.setExpenseImageData(bytes);

            return expenseService.submitExpense(expense, password);
        }

        @Override
        protected void onPostExecute(ExpenseSubmissionResponse result) {
            delegate.processFinish(result);
            mDialog.dismiss();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(AutoLogExpense.PHOTO_TAKEN, photoTaken);
    }

    public void processFinish(HashMap<String, String> priceInfo){
        EditText priceTextField = (EditText) findViewById(R.id.price);
        priceTextField.setText(priceInfo.get("amount"));

        String currencyFound = priceInfo.get("currency");

        if (!currencyFound.equals("")){
            currencyFound = currencyFound.toUpperCase();
            currencySpinner.setSelection(new Currencies().getPosition(currencyFound));
        } else {
            currencySpinner.setSelection(new Currencies().getPosition("EUR"));
        }

        EditText dateTextField = (EditText) findViewById(R.id.date);
        dateTextField.setText(priceInfo.get("date"));

        System.out.println("price information is: " + priceInfo);

        priceValues = priceInfo;
    }

    private class PerformOCRTask extends AsyncTask<Void, Void, HashMap<String,String>> {

        public AsyncHashMapResponse delegate = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mDialog = new ProgressDialog(AutoLogExpense.this);
            mDialog.setMessage("Scanning your receipt...");
            mDialog.show();
        }

        @Override
        protected HashMap<String,String> doInBackground(Void... params) {
            photoTaken = true;

            PhotoOrient photoOrient = new PhotoOrient(path);
            Bitmap bitmap = photoOrient.orientImage();

            // the below will be performed as an async task.
            PerformOCR performOCR = new PerformOCR(bitmap, lang, DATA_PATH);
            String expenseText = performOCR.performOCR();

            if (lang.equalsIgnoreCase("eng")) {
                expenseText = expenseText.replaceAll("[^a-zA-Z0-9]+", " ");
            }

            expenseText = expenseText.trim();

            if (expenseText.length() != 0) {
                fieldExtractor = new FieldExtractor();
                System.out.println(expenseText);
                return fieldExtractor.getFields(expenseText);
            }

            return null;
        }

        @Override
        protected void onPostExecute(HashMap<String,String> result) {
            delegate.processFinish(result);
            mDialog.dismiss();
        }
    }
}