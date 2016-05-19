package com.aransmith.app.receiptlogger.interfaces;


import java.util.HashMap;

/**
 * Created by Aran on 15/05/2016.
 * Used by asynchronous tasks/threads that have a HashMap<String,String> response.
 */
public interface AsyncHashMapResponse {
    void processFinish(HashMap<String,String> result);
}
