package com.aransmith.app.receiptlogger.services;

import com.aransmith.app.receiptlogger.dao.WebServiceAccess;
import com.aransmith.app.receiptlogger.model.Expense;
import com.aransmith.app.receiptlogger.model.ExpenseRetrievalResponse;
import com.aransmith.app.receiptlogger.model.ExpenseSubmissionResponse;

/**
 * Created by Aran on 2/8/2016.
 */
public class ExpenseService {

    private WebServiceAccess webServiceAccess;

    public ExpenseService(){
        webServiceAccess = new WebServiceAccess();
    }

    public ExpenseRetrievalResponse retrieveUserExpenses(String email, String password){
        return webServiceAccess.retrieveUserExpenses(email, password);
    }

    public ExpenseSubmissionResponse submitExpense(Expense expense){
        return webServiceAccess.submitExpense(expense);
    }

    public boolean cancelExpense(Expense expense){
        return true;
    }
}
