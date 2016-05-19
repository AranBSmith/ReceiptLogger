package com.aransmith.app.receiptlogger.model;

/**
 * Created by Aran on 4/27/2016.
 */

import java.util.LinkedList;

import lombok.Data;

/**
 * Encapsulates the status of an ExpenseRetrieval task on the webservice.
 */
@Data
public class ExpenseRetrievalResponse extends Response{

    private LinkedList<Expense> expenses;
    private LinkedList<String> compressedImageData;

    public ExpenseRetrievalResponse(){
        super();
        expenses = new LinkedList<>();
        compressedImageData = new LinkedList<>();
    }

    public void addExpense(Expense expense){
        expenses.add(expense);
    }

    public void addCompressedData(String data){
        compressedImageData.add(data);
    }
}