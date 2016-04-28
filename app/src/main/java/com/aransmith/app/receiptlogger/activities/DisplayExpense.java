package com.aransmith.app.receiptlogger.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Aran on 4/28/2016.
 */
public class DisplayExpense extends Activity {
    private static final String TAG = "DisplayExpense";

    private Bundle bundle;
    private String email, password;

    protected Button cancelExpenseButton;
    private AlertDialog alert1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        this.bundle = getIntent().getExtras();
        email = bundle.getString("email");
        password = bundle.getString("password");

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

                            Intent i = new Intent(getApplicationContext(), ListExpenses.class);
                            i.putExtra("email", email);
                            i.putExtra("password", password);

                            startActivity(i);
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

    public class cancelExpenseClickHandler implements View.OnClickListener {
        public void onClick(View view){
            Log.i(TAG, "Expense is being cancelled");
            alert1.show();
            // return to list expenses
        }
    }

    private void populateFields(Bundle bundle) {

        TextView priceText = (TextView) findViewById(R.id.price);
        TextView currency = (TextView) findViewById(R.id.currency);
        TextView date = (TextView) findViewById(R.id.date);
        TextView card = (TextView) findViewById(R.id.card);
        TextView category = (TextView) findViewById(R.id.category);
        TextView description = (TextView) findViewById(R.id.description);

        Double price = (Double) bundle.get("price");
        priceText.setText(price.toString());

        currency.setText((String) bundle.get("currency"));
        date.setText((String) bundle.get("date"));
        card.setText((String) bundle.get("card"));
        category.setText((String) bundle.get("category"));
        description.setText((String) bundle.get("description"));
    }
}
