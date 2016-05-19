package com.aransmith.app.receiptlogger.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.aransmith.app.receiptlogger.interfaces.AsyncResponse;
import com.aransmith.app.receiptlogger.model.CancelExpenseResponse;
import com.aransmith.app.receiptlogger.model.ExpenseRetrievalResponse;
import com.aransmith.app.receiptlogger.model.Response;
import com.aransmith.app.receiptlogger.services.CancelExpenseService;
import com.aransmith.app.receiptlogger.services.FileSystemService;

import java.util.HashMap;

import lombok.Data;

/**
 * Created by Aran on 4/28/2016.
 * Activity used to show information and image of a particular expense. Upon entering this activity
 * a request for the expense is created to the webservice, and the image will be provided in this
 * request, however if the image already exists in the storage directory, the request will not be
 * made to the webservice. This Activity allows for the user to cancel an expense.
 * The user will also be able to view from this screen the approval status of their expense.
 */
public class DisplayExpense extends Activity implements AsyncResponse {
    private static final String TAG = "DisplayExpense";
    public static final String DATA_PATH = FileSystemService.getStorageDirectory();

    private Bundle bundle;
    private String email, password;
    private int expenseID;

    protected Button cancelExpenseButton;
    private AlertDialog alert1;
    private ProgressDialog mDialog;
    private CancelExpenseService cancelExpenseService;
    private Bitmap imageBitmap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        this.bundle = getIntent().getExtras();
        email = bundle.getString("email");
        password = bundle.getString("password");
        expenseID = bundle.getInt("id");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.displayexpense);

        cancelExpenseButton = (Button) findViewById(R.id.cancelexpense);
        cancelExpenseButton.setOnClickListener(new cancelExpenseClickHandler());

        AlertDialog.Builder builder1 = new AlertDialog.Builder(DisplayExpense.this);
        builder1.setMessage("Are you sure you want to cancel this expense?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();

                        // perform asynchronous task with cancelling an expense on the webservice

                        HashMap<String, String> cancelExpenseRequest = new HashMap<>();
                        cancelExpenseRequest.put("email", email);
                        cancelExpenseRequest.put("password", password);
                        cancelExpenseRequest.put("id", String.valueOf(expenseID));

                        MyAsyncTask asyncTask = new MyAsyncTask();
                        asyncTask.delegate = DisplayExpense.this;

                        AsyncParameters asyncParameters = new AsyncParameters();
                        asyncParameters.setResponseType(new CancelExpenseResponse());
                        asyncParameters.setCancelExpenseRequest(cancelExpenseRequest);

                        asyncTask.execute(asyncParameters);
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });


        alert1 = builder1.create();
        populateFields(bundle);
    }

    @Override
    public void processFinish(Response output) {

        if (output != null && output.isSuccess() && output instanceof CancelExpenseResponse) {
            Intent i = new Intent(getApplicationContext(), ListExpenses.class);
            i.putExtra("email", email);
            i.putExtra("password", password);

            startActivity(i);
        } else {

            ImageView imageView = (ImageView) findViewById(R.id.imageView);
            imageBitmap = BitmapFactory.decodeFile(DATA_PATH + expenseID + ".png");
            imageView.setImageBitmap(imageBitmap);
        }
    }

    public class cancelExpenseClickHandler implements View.OnClickListener {
        public void onClick(View view) {
            Log.i(TAG, "Expense is being cancelled");
            alert1.show();
        }
    }

    private void populateFields(Bundle bundle) {

        TextView priceText = (TextView) findViewById(R.id.price);
        TextView currency = (TextView) findViewById(R.id.currency);
        TextView date = (TextView) findViewById(R.id.date);
        TextView card = (TextView) findViewById(R.id.card);
        TextView category = (TextView) findViewById(R.id.category);
        TextView description = (TextView) findViewById(R.id.description);
        TextView approved = (TextView) findViewById(R.id.approved);

        Double price = (Double) bundle.get("price");
        priceText.setText(price.toString());

        currency.setText((String) bundle.get("currency"));
        date.setText((String) bundle.get("date"));
        card.setText((String) bundle.get("card"));
        category.setText((String) bundle.get("category"));
        description.setText((String) bundle.get("description"));

        Boolean approval = (Boolean) bundle.get("approval");
        if (approval) {
            approved.setText("Expense Approved");
            approved.setTextColor(Color.GREEN);
        } else {
            approved.setText("Expense pending");
            approved.setTextColor(Color.RED);
        }

        // display image
        MyAsyncTask asyncTask = new MyAsyncTask();
        asyncTask.delegate = this;

        AsyncParameters asyncParameters = new AsyncParameters();
        asyncParameters.setId(expenseID);
        asyncParameters.setResponseType(new ExpenseRetrievalResponse());

        System.out.println("Retrieving Image Expense ID: " + expenseID);

        asyncTask.execute(asyncParameters);
    }

    @Data
    private class AsyncParameters {
        HashMap<String, String> cancelExpenseRequest;
        Response responseType;
        int id;
    }

    private class MyAsyncTask extends AsyncTask<AsyncParameters, Void, Response> {

        public AsyncResponse delegate = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mDialog = new ProgressDialog(DisplayExpense.this);
            mDialog.setMessage("Processing Request Expense...");
            mDialog.show();
        }

        @Override
        protected Response doInBackground(AsyncParameters... params) {
            if (params[0].getResponseType() instanceof ExpenseRetrievalResponse) {

                FileSystemService fileSystemService = new FileSystemService();
                imageBitmap = fileSystemService.retrieveImage(email, password, params[0].getId());
                return null;

            } else {
                HashMap<String, String> cancelExpenseRequest = params[0].getCancelExpenseRequest();

                String email = cancelExpenseRequest.get("email");
                String password = cancelExpenseRequest.get("password");
                int id = Integer.parseInt(cancelExpenseRequest.get("id"));

                System.out.println("cancelling: " + cancelExpenseRequest.get("email")
                        + " " + cancelExpenseRequest.get("password") + " " + cancelExpenseRequest.get("id"));


                cancelExpenseService = new CancelExpenseService();
                return cancelExpenseService.cancelExpense(email, password, id);

            }
        }

        @Override
        protected void onPostExecute(Response result) {
            delegate.processFinish(result);
            mDialog.dismiss();
        }
    }
}