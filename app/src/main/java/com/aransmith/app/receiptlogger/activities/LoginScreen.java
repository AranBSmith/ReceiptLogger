package com.aransmith.app.receiptlogger.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.aransmith.app.receiptlogger.interfaces.AsyncBoolResponse;
import com.aransmith.app.receiptlogger.interfaces.FeedbackNotificationActivity;
import com.aransmith.app.receiptlogger.services.Login;
import com.aransmith.app.receiptlogger.services.UserInputChecker;

import java.util.HashMap;

/**
 * Created by Aran on 26/01/2016.
 * This is the first Activity the user will be presented with upon opening the App for the first time.
 * The user must enter valid credentials in order to sign in. The process of signing in will run in
 * the background and the user is presented with a loading screen while this happens. When successful
 * the user is presented with the ActionSet activity, else the user is notified that they must
 * enter valid credentials.
 */
public class LoginScreen extends FeedbackNotificationActivity {

    private static final String TAG = "LoginScreen";
    protected Button loginButton;
    protected EditText emailField, passwordField;
    protected Login loginService;
    ProgressDialog mDialog;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        emailField = (EditText) findViewById(R.id.input_email);
        passwordField = (EditText) findViewById(R.id.input_password);
        loginButton = (Button) findViewById(R.id.btn_login);
        loginButton.setOnClickListener(new loginClickHandler());
        loginService = new Login();
    }

    public class loginClickHandler implements View.OnClickListener, AsyncBoolResponse {
        public void onClick(View view){
            String email = emailField.getText().toString();
            String password = passwordField.getText().toString();

            if(UserInputChecker.noValue(email) || UserInputChecker.noValue(password)){
                notifyUser("Enter username and password");
            } else {
                HashMap<String, String> credentials = new HashMap<>();
                credentials.put("email", email);
                credentials.put("password", password);

                MyAsyncTask asyncTask = new MyAsyncTask();
                asyncTask.delegate = this;
                asyncTask.execute(credentials);
            }
        }

        // run this method when the async task is complete.
        public void processFinish(Boolean result){
             if(result != null){
                if(result){
                    Log.i(TAG, "Login successful");
                    Intent i = new Intent(getApplicationContext(), ActionSet.class);
                    i.putExtra("email", emailField.getText().toString());
                    i.putExtra("password", passwordField.getText().toString());

                    startActivity(i);
                } else {
                    Log.i(TAG, "Login unsuccessful");
                    notifyUser("Username or Password invalid");
                }
             }
        }

        private class MyAsyncTask extends AsyncTask<HashMap<String,String>, Void, Boolean> {

            public AsyncBoolResponse delegate = null;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                mDialog = new ProgressDialog(LoginScreen.this);
                mDialog.setMessage("Logging you in...");
                mDialog.show();
            }

            @Override
            protected Boolean doInBackground(HashMap<String,String>... params) {
                HashMap<String,String> credentials = params[0];

                if(new Login().checkCredentials(credentials.get("email"),credentials.get("password")))
                    return true;

                else return false;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                delegate.processFinish(result);
                mDialog.dismiss();
            }
        }
    }
}


