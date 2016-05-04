package com.aransmith.app.receiptlogger.activities;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.aransmith.app.receiptlogger.interfaces.AsyncExpenseRetrievalResponse;
import com.aransmith.app.receiptlogger.model.Expense;
import com.aransmith.app.receiptlogger.model.ExpenseRetrievalResponse;
import com.aransmith.app.receiptlogger.services.ExpenseService;
import com.aransmith.app.receiptlogger.services.PriceService;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by Aran on 2/8/2016.
 */
public class ListExpenses extends ListActivity implements AsyncExpenseRetrievalResponse {
    private static final String TAG = "ListExpenses";
    private Bundle bundle;
    private String email, password;
    ListView listView ;
    LinkedList<Expense> expenses;
    private ProgressDialog mDialog;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        this.bundle = getIntent().getExtras();
        email = bundle.getString("email");
        password = bundle.getString("password");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.listexpenses);

        // Get ListView object from xml
        listView = (ListView) findViewById(android.R.id.list);

        HashMap<String,String> credentials = new HashMap<>();
        credentials.put("email", email);
        credentials.put("password", password);

        // obtain information
        MyAsyncTask asyncTask = new MyAsyncTask();
        asyncTask.delegate = this;
        asyncTask.execute(credentials);

    }

    @Override
    public void processFinish(ExpenseRetrievalResponse output) {
        // Defined Array values to show in ListView
        System.out.println(output.toString());

        LinkedList<String> titles = new LinkedList<>();

        expenses = output.getExpenses();

        for(Expense expense: expenses){
            titles.add(expense.getDescription());
        }

        String[] values = titles.toArray(new String[titles.size()]);

        System.out.println(values.toString());

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, values);

        // Assign adapter to ListView
        listView.setAdapter(adapter);

        // ListView Item Click Listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // access position in linkedlist and display in DisplayExpense Activity
                Expense expense = expenses.get(position);
                startDisplayExpenseActivity(expense);
            }
        });

        // display monthly total and approved total
        TextView monthTotal = (TextView) findViewById(R.id.totalmonth);
        TextView monthApprovedTotal = (TextView) findViewById(R.id.approvedmonth);

        // display the approved amounts and the total amount for the last 30 days
        PriceService priceService = new PriceService();
        monthTotal.setText("Monthly Total: " + priceService.calculateMonthlyTotal(output));
        monthApprovedTotal.setText("Approved: " + priceService.calculateApprovedMonthlyTotal(output));
    }

    private void startDisplayExpenseActivity(Expense expense){
        Log.i(TAG, "Expense has been clicked, switching activities to view expense");
        Intent i = new Intent(getApplicationContext(), DisplayExpense.class);
        i.putExtra("email", email);
        i.putExtra("password", password);
        i.putExtra("id", expense.getId());
        i.putExtra("description", expense.getDescription());
        i.putExtra("date", expense.getDate());
        i.putExtra("category", expense.getCategory());
        i.putExtra("currency", expense.getCurrency());
        i.putExtra("price", expense.getPrice());
        i.putExtra("card", expense.getCard());
        i.putExtra("approval", expense.isApproved());

        startActivity(i);
    }

    private class MyAsyncTask extends AsyncTask<HashMap<String,String>, Void, ExpenseRetrievalResponse> {

        public AsyncExpenseRetrievalResponse delegate = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mDialog = new ProgressDialog(ListExpenses.this);
            mDialog.setMessage("Getting your Expenses...");
            mDialog.show();
        }

        @Override
        protected ExpenseRetrievalResponse doInBackground(HashMap<String,String>... params) {
            ExpenseService expenseService = new ExpenseService();
            HashMap<String,String> credentials = params[0];

            System.out.println("retrieving: " + credentials.get("email")
                    + " " + credentials.get("password"));

            ExpenseRetrievalResponse expenseRetrievalResponse = expenseService
                    .retrieveUserExpenses(credentials.get("email"), credentials.get("password"));

            expenseRetrievalResponse.appendMessage("submitted " + credentials.get("email") + " "
                    + credentials.get("password") + " for expense submission.");

            return expenseRetrievalResponse;
        }

        @Override
        protected void onPostExecute(ExpenseRetrievalResponse result) {
            delegate.processFinish(result);
            mDialog.dismiss();
        }
    }
}
