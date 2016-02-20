package com.aransmith.app.receiptlogger.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

/**
 * Created by Aran on 2/3/2016.
 */
public class ActionSet extends Activity {
    private static final String TAG = "ActionSet";
    protected Button logExpenseButton, viewExpensesButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actionset);

        logExpenseButton = (Button) findViewById(R.id.expenselog);
        viewExpensesButton = (Button) findViewById(R.id.anotherfunction);

        logExpenseButton.setOnClickListener(new logExpenseClickHandler());
    }

    public class logExpenseClickHandler implements View.OnClickListener {
        public void onClick(View view){
            Log.i(TAG, "logExpenseButton has been pressed. Moving to new activity");
            Intent i = new Intent(getApplicationContext(),LogExpense.class);
            startActivity(i);
            setContentView(R.layout.main);
        }
    }

}
