package com.aransmith.app.receiptlogger.services;

import com.aransmith.app.receiptlogger.dao.WebServiceAccessObject;

/**
 * Created by Aran on 2/8/2016.
 */
public class Login {

    private WebServiceAccessObject wsab;
    private EmailService emailService;

    public Login(){
        wsab = new WebServiceAccessObject();
        emailService = new EmailService();
    }

    public boolean checkCredentials(String email, String password){

        // verify if email is of a valid format.
        boolean validEmail = emailService.isValidEmailAddress(email);
        if(email.equals("") || password.equals("") || !validEmail ){
            return false;
        } else {
            return wsab.login(email, password);
        }
    }
}