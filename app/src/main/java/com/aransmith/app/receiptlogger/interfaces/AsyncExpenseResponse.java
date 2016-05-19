package com.aransmith.app.receiptlogger.interfaces;

import com.aransmith.app.receiptlogger.model.ExpenseSubmissionResponse;

/**
 * Created by Aran on 4/20/2016.
 * Used by asynchronous tasks/threads that have an Expense response.
 */
public interface AsyncExpenseResponse {
    void processFinish(ExpenseSubmissionResponse output);
}
