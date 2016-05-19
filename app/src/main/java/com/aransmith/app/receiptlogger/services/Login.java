package com.aransmith.app.receiptlogger.services;

import com.aransmith.app.receiptlogger.dao.WebServiceAccess;

/**
 * Created by Aran on 2/8/2016.
 * Used to submit credentials to the web service
 */
public class Login {

    private WebServiceAccess wsab;
    private EmailService emailService;

    public Login(){
        wsab = new WebServiceAccess();
        emailService = new EmailService();
    }

    /**
     * check if the submitted credentials are not null and are of a valid format.
     * @param email
     * @param password
     * @return
     */
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
