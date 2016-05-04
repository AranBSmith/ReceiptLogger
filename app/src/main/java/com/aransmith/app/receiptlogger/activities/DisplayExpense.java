package com.aransmith.app.receiptlogger.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.aransmith.app.receiptlogger.interfaces.AsyncCancelExpenseResponse;
import com.aransmith.app.receiptlogger.model.CancelExpenseResponse;
import com.aransmith.app.receiptlogger.services.CancelExpenseService;

import java.util.HashMap;

/**
 * Created by Aran on 4/28/2016.
 */
public class DisplayExpense extends Activity implements AsyncCancelExpenseResponse{
    private static final String TAG = "DisplayExpense";

    private Bundle bundle;
    private String email, password;
    private int expenseID;

    protected Button cancelExpenseButton;
    private AlertDialog alert1;
    private ProgressDialog mDialog;
    private CancelExpenseService cancelExpenseService;

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

                            HashMap<String,String> cancelExpenseRequest = new HashMap<>();
                            cancelExpenseRequest.put("email", email);
                            cancelExpenseRequest.put("password", password);
                            cancelExpenseRequest.put("id", String.valueOf(expenseID));

                            MyAsyncTask asyncTask = new MyAsyncTask();
                            asyncTask.delegate = DisplayExpense.this;
                            asyncTask.execute(cancelExpenseRequest);
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
    public void processFinish(CancelExpenseResponse output) {

        if(output.isSuccess()){
            Intent i = new Intent(getApplicationContext(), ListExpenses.class);
            i.putExtra("email", email);
            i.putExtra("password", password);

            startActivity(i);

        } else {

        }
    }

    public class cancelExpenseClickHandler implements View.OnClickListener {
        public void onClick(View view){
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
        if(approval) {
            approved.setText("Expense Approved");
            approved.setTextColor(Color.GREEN);
        } else {
            approved.setText("Expense pending");
            approved.setTextColor(Color.RED);
        }
    }

    private class MyAsyncTask extends AsyncTask<HashMap<String,String>, Void, CancelExpenseResponse> {

        public AsyncCancelExpenseResponse delegate = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mDialog = new ProgressDialog(DisplayExpense.this);
            mDialog.setMessage("Cancelling selected Expense...");
            mDialog.show();
        }

        @Override
        protected CancelExpenseResponse doInBackground(HashMap<String,String>... params) {
            HashMap<String,String> cancelExpenseRequest = params[0];

            String email = cancelExpenseRequest.get("email");
            String password = cancelExpenseRequest.get("password");
            int id = Integer.parseInt(cancelExpenseRequest.get("id"));

            System.out.println("cancelling: " + cancelExpenseRequest.get("email")
                    + " " + cancelExpenseRequest.get("password") + " " + cancelExpenseRequest.get("id"));

            cancelExpenseService = new CancelExpenseService();
            return cancelExpenseService.cancelExpense(email, password, id);
        }

        @Override
        protected void onPostExecute(CancelExpenseResponse result) {
            delegate.processFinish(result);
            mDialog.dismiss();
        }
    }
}