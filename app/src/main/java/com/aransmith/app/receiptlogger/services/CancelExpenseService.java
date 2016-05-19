package com.aransmith.app.receiptlogger.services;

import com.aransmith.app.receiptlogger.dao.WebServiceAccess;
import com.aransmith.app.receiptlogger.model.CancelExpenseResponse;

/**
 * Created by Aran on 5/1/2016.
 * service used to invoke the web service access object for the cancellation of an expense
 */
public class CancelExpenseService {
    WebServiceAccess webServiceAccess;

    public CancelExpenseService(){
        webServiceAccess = new WebServiceAccess();
    }

    public CancelExpenseResponse cancelExpense(String email, String password, int id){
        return webServiceAccess.cancelExpense(email, password, id);
    }
}
