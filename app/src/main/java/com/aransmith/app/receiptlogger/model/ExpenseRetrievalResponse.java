package com.aransmith.app.receiptlogger.model;

/**
 * Created by Aran on 4/27/2016.
 */

import java.util.LinkedList;

import lombok.Data;

@Data
public class ExpenseRetrievalResponse extends Response{

    private LinkedList<Expense> expenses;

    public ExpenseRetrievalResponse(){
        super();
        expenses = new LinkedList<>();
    }

    public void addExpense(Expense expense){
        expenses.add(expense);
    }
}