package com.aransmith.app.receiptlogger.model;

import lombok.Data;

/**
 * Created by Aran on 2/8/2016.
 */

@Data
public class Expense {

    private String email;
    private String category;
    private String date;
    private float price;
    private String currency;
}
