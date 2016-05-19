package com.aransmith.app.receiptlogger.model;

import lombok.Data;

/**
 * Created by Aran on 4/6/2016.
 * Encapsulation of categories a user can categorize their expense under.
 */

@Data
public class Categories {
    String[] categories;

    public Categories(){
        categories = new String[]{"Breakfast", "Lunch", "Dinner", "Travel", "Hotel", "Car Rental",
                        "Entertainment", "Taxi", "Miscellaneous"};
    }
}
