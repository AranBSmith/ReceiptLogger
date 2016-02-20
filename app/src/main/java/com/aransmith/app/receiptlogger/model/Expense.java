package com.aransmith.app.receiptlogger.model;

import lombok.Data;

/**
 * Created by Aran on 2/8/2016.
 */

@Data
public class Expense {

    String email;
    String category;
    String date;
}
