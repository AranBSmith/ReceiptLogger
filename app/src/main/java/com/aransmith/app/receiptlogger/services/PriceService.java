package com.aransmith.app.receiptlogger.services;

import com.aransmith.app.receiptlogger.model.Expense;
import com.aransmith.app.receiptlogger.model.ExpenseRetrievalResponse;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedList;

/**
 * Created by Aran on 4/30/2016.
 * Service class dealing with price tasks.
 */
public class PriceService {

    private DateService dateService;

    public PriceService(){
        dateService = new DateService();
    }

    /**
     * Check format of a String representing a price
     * @param inputPrice
     * @return
     */
    public boolean checkFormat(String inputPrice){
        // integer followed by a "." followed by 2 single digits
        String format = "((\\d+)(.)(\\d)(\\d))";
        if(inputPrice.matches(format))
            return true;

        return false;
    }

    /**
     * given an expense retrieval, calculate the amount pending within the last 30 days.
     * @param expenseRetrievalResponse
     * @return double representing amount pending within the last 30 days
     */
    public double calculateMonthlyTotal(ExpenseRetrievalResponse expenseRetrievalResponse){
        LinkedList<Expense> expenses = expenseRetrievalResponse.getExpenses();

        double monthlyTotal = 0.0;

        for(Expense expense : expenses){
            if(dateService.withinMonth(expense.getDate()))
                monthlyTotal = monthlyTotal + expense.getPrice();
        }

        return round(monthlyTotal, 2);
    }

    /**
     * given a LinkedList<Expense>, calculate the amount pending within the last 30 days.
     * @param expenses
     * @return amount approved
     */
    public double calculateMonthlyTotal(LinkedList<Expense> expenses){
        double monthlyTotal = 0.0;

        for(Expense expense : expenses){
            if(dateService.withinMonth(expense.getDate()))
                monthlyTotal = monthlyTotal + expense.getPrice();
        }

        return round(monthlyTotal, 2);
    }

    /**
     * given an expense retrieval, calculate the amount pending within the last 30 days.
     * @param expenseRetrievalResponse
     * @return amount approved
     */
    public double calculateApprovedMonthlyTotal(ExpenseRetrievalResponse expenseRetrievalResponse){
        LinkedList<Expense> expenses = expenseRetrievalResponse.getExpenses();

        double monthlyTotal = 0.0;

        for(Expense expense : expenses){
            if(expense.isApproved() && dateService.withinMonth(expense.getDate()))
                monthlyTotal = monthlyTotal + expense.getPrice();
        }

        return round(monthlyTotal, 2);
    }

    /**
     * given a LinkedList<Expense>, calculate the amount approved within the last 30 days.
     * @param expenses
     * @return
     */
    public double calculateApprovedMonthlyTotal(LinkedList<Expense> expenses){
        double monthlyTotal = 0.0;

        for(Expense expense : expenses){
            if(expense.isApproved() && dateService.withinMonth(expense.getDate()))
                monthlyTotal = monthlyTotal + expense.getPrice();
        }

        return round(monthlyTotal, 2);
    }

    /**
     * Round a double. e.g. 1.0000001, 2 returns 1.00
     * @param value
     * @param places
     * @return rounded double
     */
    public double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
