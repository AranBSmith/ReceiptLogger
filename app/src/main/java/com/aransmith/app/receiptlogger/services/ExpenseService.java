package com.aransmith.app.receiptlogger.services;

import com.aransmith.app.receiptlogger.model.Expense;

import java.util.List;

/**
 * Created by Aran on 2/8/2016.
 */
public class ExpenseService {

    private Expense expense = new Expense();

    public List<Expense> retrieveExpenses(){
        return null;
    }

    public boolean submitExpense(Expense expense){
        this.expense = expense;
        return true;
    }

    public boolean cancelExpense(Expense expense){
        return true;
    }
}
