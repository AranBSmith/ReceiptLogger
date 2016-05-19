package com.aransmith.app.receiptlogger.interfaces;

import com.aransmith.app.receiptlogger.model.ExpenseRetrievalResponse;

/**
 * Created by Aran on 4/27/2016.
 * Used by asynchronous tasks/threads that have an ExpenseRetrievalResponse response.
 */
public interface AsyncExpenseRetrievalResponse {
    void processFinish(ExpenseRetrievalResponse output);
}
