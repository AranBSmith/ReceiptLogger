package com.aransmith.app.receiptlogger.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.aransmith.app.receiptlogger.interfaces.AsyncBoolResponse;
import com.aransmith.app.receiptlogger.services.Login;

import java.util.HashMap;

/**
 * Created by Aran on 26/01/2016.
 */
public class LoginScreen extends Activity {

    private static final String TAG = "LoginScreen";
    protected Button loginButton;
    protected EditText domainField, emailField, passwordField;
    protected Login loginService;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);


        domainField = (EditText) findViewById(R.id.input_domain);
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
            HashMap<String, String> credentials = new HashMap<>();
            credentials.put("email", email); credentials.put("password", password);

            MyAsyncTask asyncTask = new MyAsyncTask();
            asyncTask.delegate = this;
            asyncTask.execute(credentials);
        }

        // run this method when the async task is complete.
        public void processFinish(Boolean result){
             if(result != null){
                if(result){
                    Log.i(TAG, "Login successful");
                    Intent i = new Intent(getApplicationContext(),ActionSet.class);
                    startActivity(i);
                    setContentView(R.layout.actionset);
                } else {
                    Log.i(TAG, "Login unsuccessful");
                }
             }
        }

        private class MyAsyncTask extends AsyncTask<HashMap<String,String>, Void, Boolean> {

            public AsyncBoolResponse delegate = null;

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
            }
        }
    }
}


