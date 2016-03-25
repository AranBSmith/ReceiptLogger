package com.aransmith.app.receiptlogger.model;

import com.aransmith.app.receiptlogger.services.StringComparison;

/**
 * Created by Aran on 3/20/2016.
 */
public class PriceField {
    private String[] fieldNames;
    private StringComparison stringComparison;

    public PriceField(){
        fieldNames = new String[] {"sale", "total amount", "amount due", "total", "total due",
                                    "visadebit sale", "sale amount", "card sales", "goods value",
                                        "amount"};
    }

    public boolean equals(String word){
        for(String field: fieldNames){
            if(field.equals(word)){
                return true;
            }
        }
        return false;
    }

    public int compare(String word){
        stringComparison = new StringComparison();

        int score = 5000;
        boolean match = false;

        // find a VALID field
        for(String field: fieldNames){
            int t = stringComparison.stringCompare(field, word);
            if(t > -1 && t < score){
                score = t;
                match = true;
            }
        }
        if(match) return score;

        else return -1;
    }
}
