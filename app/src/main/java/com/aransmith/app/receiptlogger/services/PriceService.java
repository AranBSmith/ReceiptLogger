package com.aransmith.app.receiptlogger.services;

import com.aransmith.app.receiptlogger.model.Expense;
import com.aransmith.app.receiptlogger.model.ExpenseRetrievalResponse;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedList;

/**
 * Created by Aran on 4/30/2016.
 */
public class PriceService {

    private DateService dateService;

    public PriceService(){
        dateService = new DateService();
    }

    public double calculateMonthlyTotal(ExpenseRetrievalResponse expenseRetrievalResponse){
        LinkedList<Expense> expenses = expenseRetrievalResponse.getExpenses();

        double monthlyTotal = 0.0;

        for(Expense expense : expenses){
            if(dateService.withinMonth(expense.getDate()))
                monthlyTotal = monthlyTotal + expense.getPrice();
        }

        return round(monthlyTotal, 2);
    }

    public double calculateMonthlyTotal(LinkedList<Expense> expenses){
        double monthlyTotal = 0.0;

        for(Expense expense : expenses){
            if(dateService.withinMonth(expense.getDate()))
                monthlyTotal = monthlyTotal + expense.getPrice();
        }

        return round(monthlyTotal, 2);
    }

    public double calculateApprovedMonthlyTotal(ExpenseRetrievalResponse expenseRetrievalResponse){
        LinkedList<Expense> expenses = expenseRetrievalResponse.getExpenses();

        double monthlyTotal = 0.0;

        for(Expense expense : expenses){
            if(expense.isApproved() && dateService.withinMonth(expense.getDate()))
                monthlyTotal = monthlyTotal + expense.getPrice();
        }

        return round(monthlyTotal, 2);
    }

    public double calculateApprovedMonthlyTotal(LinkedList<Expense> expenses){
        double monthlyTotal = 0.0;

        for(Expense expense : expenses){
            if(expense.isApproved() && dateService.withinMonth(expense.getDate()))
                monthlyTotal = monthlyTotal + expense.getPrice();
        }

        return round(monthlyTotal, 2);
    }

    public double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
