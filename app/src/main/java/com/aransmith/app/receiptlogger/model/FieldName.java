package com.aransmith.app.receiptlogger.model;

import com.aransmith.app.receiptlogger.services.StringComparison;

/**
 * Created by Aran on 3/30/2016.
 * Abstract class providing generic methods for searchable fields in a receipt that are identified
 * by some sort of label.
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

    /**
     * iterate through all the fieldnames and compare it with word, if a match is found this will
     * return true, otherwise false.
     * @param word
     * @return boolean
     */
    public boolean equals(String word){
        for(String field: fieldNames){
            if(field.equals(word)){
                return true;
            }
        }
        return false;
    }

    /**
     * Compare a given word with those in the fieldNames array, iterate through each fieldName
     * a collect its comparison score, if it is better than any other score found, store it. Return
     * the best score found. If no good match was found return -1.
     * @param word
     * @return
     */
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
