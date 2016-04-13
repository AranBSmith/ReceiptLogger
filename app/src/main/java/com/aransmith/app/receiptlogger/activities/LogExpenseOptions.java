package com.aransmith.app.receiptlogger.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

/**
 * Created by Aran on 3/18/2016.
 */
public class LogExpenseOptions extends Activity {
    private static final String TAG = "LogExpenseOptions";
    protected Button autoLogExpense, manualLogExpense;
    private Bundle bundle;
    private String email;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        bundle = getIntent().getExtras();
        email = bundle.getString("email");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_expense_options);

        autoLogExpense = (Button) findViewById(R.id.automaticLog);
        manualLogExpense = (Button) findViewById(R.id.manualLog);

        autoLogExpense.setOnClickListener(new automaticLogExpenseClickHandler());
    }

    public class automaticLogExpenseClickHandler implements View.OnClickListener {
        public void onClick(View view){
            Log.i(TAG, "Moving to AutoLogExpense Activity");
            Intent i = new Intent(getApplicationContext(),AutoLogExpense.class);
            i.putExtra("email", email);
            startActivity(i);
            // the below line might have been cause of crashes when reversing activities.
            // setContentView(R.layout.main);
        }
    }
}