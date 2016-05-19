package com.aransmith.app.receiptlogger.services;

/**
 * Created by Aran on 06/05/2016.
 * Generic user input checker, to be used by any activity that requires user input.
 */
public class UserInputChecker {
    public static boolean noValue(String input){
        if(input.equals("") || input == null){
            return true;
        }

        return false;
    }
}
