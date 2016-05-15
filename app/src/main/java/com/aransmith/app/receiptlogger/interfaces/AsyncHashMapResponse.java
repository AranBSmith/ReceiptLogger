package com.aransmith.app.receiptlogger.interfaces;


import java.util.HashMap;

/**
 * Created by Aran on 15/05/2016.
 */
public interface AsyncHashMapResponse {
    void processFinish(HashMap<String,String> result);
}
