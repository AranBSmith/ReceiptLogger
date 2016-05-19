package com.aransmith.app.receiptlogger.interfaces;

import com.aransmith.app.receiptlogger.model.Response;

/**
 * Created by Aran on 5/1/2016.
 * Used by asynchronous tasks/threads that require a generic Response response.
 */
public interface AsyncResponse {
    void processFinish(Response output);
}
