package com.aransmith.app.receiptlogger.services;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
/**
 * Created by Aran on 2/23/2016.
 * Service for handling email addresses, currently checks if an email address is a valid one.
 */
public class EmailService {
    /**
     * Check if email is valid.
     * @param email
     * @return <Code>true</Code> if the email is valid.
     */
    public boolean isValidEmailAddress(String email) {
        boolean result = true;
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
        } catch (AddressException ex) {
            result = false;
        }
        return result;
    }
}
