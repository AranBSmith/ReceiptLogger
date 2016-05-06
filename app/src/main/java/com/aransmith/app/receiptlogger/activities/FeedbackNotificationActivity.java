package com.aransmith.app.receiptlogger.activities;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

/**
 * Created by Aran on 06/05/2016.
 */
public abstract class FeedbackNotificationActivity extends Activity {
    public void notifyUser(String message){
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, message, duration);
        toast.show();
    }
}
