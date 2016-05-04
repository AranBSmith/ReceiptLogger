package com.aransmith.app.receiptlogger.interfaces;

import com.aransmith.app.receiptlogger.model.CancelExpenseResponse;

/**
 * Created by Aran on 5/1/2016.
 */
public interface AsyncCancelExpenseResponse {
    void processFinish(CancelExpenseResponse output);
}
