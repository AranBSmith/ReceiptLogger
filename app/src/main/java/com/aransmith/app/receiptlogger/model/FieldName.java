package com.aransmith.app.receiptlogger.model;

import com.aransmith.app.receiptlogger.services.StringComparison;

/**
 * Created by Aran on 3/30/2016.
 */
public abstract class FieldName {
    public String[] fieldNames;
    private StringComparison stringComparison;

    public abstract String getFormat();

    public abstract boolean checkFormat(String value);

    public String cleanup(String value){
        return value;
    }

    public String getSeperators(){
        return "";
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
