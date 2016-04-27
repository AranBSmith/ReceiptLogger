package com.aransmith.app.receiptlogger.activities;

import android.app.ListActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.aransmith.app.receiptlogger.interfaces.AsyncExpenseRetrievalResponse;
import com.aransmith.app.receiptlogger.model.Expense;
import com.aransmith.app.receiptlogger.model.ExpenseRetrievalResponse;
import com.aransmith.app.receiptlogger.services.ExpenseService;

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

        for(Expense expense: output.getExpenses()){
            titles.add(expense.getDescription());
        }

        String[] values = titles.toArray(new String[titles.size()]);

        System.out.println(values.toString());

        // Define a new Adapter
        // First parameter - Context
        // Second parameter - Layout for the row
        // Third parameter - ID of the TextView to which the data is written
        // Forth - the Array of data

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, values);

        // Assign adapter to ListView
        listView.setAdapter(adapter);

        // ListView Item Click Listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // ListView Clicked item index
                int itemPosition = position;
                // ListView Clicked item value
                String itemValue = (String) listView.getItemAtPosition(position);
                // Show Alert
                Toast.makeText(getApplicationContext(),
                        "Position :"+itemPosition+"  ListItem : " +itemValue , Toast.LENGTH_LONG)
                        .show();
            }
        });
    }

    private class MyAsyncTask extends AsyncTask<HashMap<String,String>, Void, ExpenseRetrievalResponse> {

        public AsyncExpenseRetrievalResponse delegate = null;

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
        }
    }
}
