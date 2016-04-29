package com.aransmith.app.receiptlogger.model;

import lombok.Data;

/**
 * Created by Aran on 2/8/2016.
 */

@Data
public class Expense {

    private int id;
    private String email;
    private double price;
    private String currency;
    private String card;
    private String category;
    private String date;
    private String description;
    private byte[] expenseImageData;
    private boolean approved;

    public Expense(
            String email, double price, String currency, String card, String category,
             String date, String description, byte[] expenseImageData){

        this.id = -1;
        this.email = email;
        this.price = price;
        this.card = card;
        this.currency = currency;
        this.category = category;
        this.date = date;
        this.description = description;
        this.expenseImageData = expenseImageData;
        this.approved = false;
    }
}
