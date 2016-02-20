package com.aransmith.app.receiptlogger.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.aransmith.app.receiptlogger.services.Login;

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

    public void performOCR(){

    }

    public class loginClickHandler implements View.OnClickListener {
        public void onClick(View view){
            String email = emailField.getText().toString();
            String password = passwordField.getText().toString();
            if(loginService.checkCredentials(email, password)){
                Log.i(TAG, "Login successful");
                Intent i = new Intent(getApplicationContext(),ActionSet.class);
                startActivity(i);
                setContentView(R.layout.actionset);
            } else {
                Log.i(TAG, "Login unsuccessful");
            }
        }
    }
}


