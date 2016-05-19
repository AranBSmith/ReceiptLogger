package com.aransmith.app.receiptlogger.interfaces;

/**
 * Created by Aran on 3/29/2016.
 * Used by asynchronous tasks/threads that have a boolean response.
 */
public interface AsyncBoolResponse {
    void processFinish(Boolean output);
}
