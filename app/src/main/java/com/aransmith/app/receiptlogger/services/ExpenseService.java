package com.aransmith.app.receiptlogger.services;

import com.aransmith.app.receiptlogger.dao.WebServiceAccess;
import com.aransmith.app.receiptlogger.model.Expense;
import com.aransmith.app.receiptlogger.model.ExpenseRetrievalResponse;
import com.aransmith.app.receiptlogger.model.ExpenseSubmissionResponse;

/**
 * Created by Aran on 2/8/2016.
 * Service used to invoke the web service access object for either submission of an expense, or
 * the retrieval of expenses.
 */
public class ExpenseService {

    private WebServiceAccess webServiceAccess;

    public ExpenseService(){
        webServiceAccess = new WebServiceAccess();
    }

    /**
     * Retrieve all of users expenses according to an email, password required for verification.
     * @param email
     * @param password
     * @return ExpenseRetrievalResponse containing the status of the expense retrieval and the
     * web service. Will also contain any exceptions in the web service.
     */
    public ExpenseRetrievalResponse retrieveUserExpenses(String email, String password){
        return webServiceAccess.retrieveUserExpenses(email, password);
    }

    /**
     * Submit an expense to the web service.
     * @param expense
     * @param password
     * @return ExpenseSubmissionResponse containing containing the status of the expense submission
     * and the web service. Will also contain any exceptions in the web service.
     */
    public ExpenseSubmissionResponse submitExpense(Expense expense, String password){
        return webServiceAccess.submitExpense(expense, password);
    }
}
