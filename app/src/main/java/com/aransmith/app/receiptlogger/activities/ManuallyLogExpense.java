package com.aransmith.app.receiptlogger.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
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

import com.aransmith.app.receiptlogger.interfaces.AsyncExpenseResponse;
import com.aransmith.app.receiptlogger.model.Categories;
import com.aransmith.app.receiptlogger.model.Currencies;
import com.aransmith.app.receiptlogger.model.Expense;
import com.aransmith.app.receiptlogger.model.ExpenseSubmissionResponse;
import com.aransmith.app.receiptlogger.services.DateService;
import com.aransmith.app.receiptlogger.services.ExpenseService;
import com.aransmith.app.receiptlogger.services.FileSystemService;
import com.aransmith.app.receiptlogger.services.ImageService;
import com.aransmith.app.receiptlogger.services.PriceService;
import com.aransmith.app.receiptlogger.services.UserInputChecker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

/**
 * Created by Aran on 19/05/2016.
 */
public class ManuallyLogExpense extends FeedbackNotificationActivity {

    public static final String DATA_PATH = Environment.getExternalStorageDirectory().toString() +
            "/AutoLogExpense/";

    public static final String lang = "eng";

    private static final String TAG = "AutoLogExpense.java";
    private static final String PHOTO_TAKEN = "photo_taken";
    private static final int REQUEST_WRITE_STORAGE = 112;

    private Button button, submitExpenseButton, cancelButton;
    private EditText textField, descriptionTextField;
    private Spinner categorySpinner, currencySpinner;
    private String path;
    private boolean photoTaken;
    private Bundle bundle;
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

        boolean hasPermission = (ContextCompat.checkSelfPermission(ManuallyLogExpense.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission) {
            ActivityCompat.requestPermissions(this.getParent(),
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_STORAGE);
        }

        FileSystemService dcreate = new FileSystemService();
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

        path = DATA_PATH + "/ocr.png";

        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        textField = (EditText) findViewById(R.id.field);
        descriptionTextField = (EditText) findViewById(R.id.expensedescription);

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

            mDialog = new ProgressDialog(ManuallyLogExpense.this);
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

            return expenseService.submitExpense(expense);
        }

        @Override
        protected void onPostExecute(ExpenseSubmissionResponse result) {
            delegate.processFinish(result);
            mDialog.dismiss();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(ManuallyLogExpense.PHOTO_TAKEN, photoTaken);
    }

}
