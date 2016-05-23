package com.aransmith.app.receiptlogger.interfaces;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

/**
 * Created by Aran on 06/05/2016.
 * Abstract class used by activities to notify the user of events such as when a field as not had
 * details entered, or when an expense submission was successful or unsuccessful. Heps with removing
 * boiler plate code.
 */
public abstract class FeedbackNotificationActivity extends Activity {
    public void notifyUser(String message){
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, message, duration);
        toast.show();
    }
}
