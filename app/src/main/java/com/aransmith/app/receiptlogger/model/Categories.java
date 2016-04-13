package com.aransmith.app.receiptlogger.model;

import lombok.Data;

/**
 * Created by Aran on 4/6/2016.
 */

@Data
public class Categories {
    String[] categories;

    public Categories(){
        categories =
                new String[]{"Breakfast", "Lunch", "Dinner", "Travel", "Hotel", "Car Rental",
                        "Entertainment", "Taxi", "Miscellaneous"};
    }
}
