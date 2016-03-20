package com.aransmith.app.receiptlogger.services;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.regex.PatternSyntaxException;

/**
 * Created by Aran on 3/15/2016.
 * FieldExtractor is responsible for reading source OCR text
 * and interpreting receipt fields withing e.g. AID, MerchantID,
 * Sale price, end debit card number etc.
 */
public class FieldExtractor {
    /**
     * performFieldExtraction will extract relevant expense details from
     * a receipt using performFieldExtraction. This performs a number of
     * passes on the ocrd text, the first assuming that the text is perfect,
     * the second pass will try and find a best match when the field name is
     * as its own string e.g. " A10 " for AID. The third pass will look for
     * the best matching substring and uses information about those fields to
     * find it as best it can. e.g. "Total" is normally associated with the end
     * of the receipt, "MerchantID" can also be written as "MID", etc.
     *
     * @param ocrdText provided as a String which is produced by the Tesseract
     *                 OCR function.
     * @return HashMap containing the names of relevent fields and their values
     * e.g. AID, Total, MerchantId, cardNumber etc.
     */

    StringComparison stringComparison;

    HashMap<String, String> performFieldExtraction(String ocrdText){

        HashMap<String, String> fields = new HashMap<>();

        ocrdText = ocrdText.toLowerCase();

        // aid field
        String AID = "aid";
        // total, amount
        String total = "sale";
        // number, visadebit
        String cardNumber = "number";

        fields.put(AID, getField(AID, ocrdText));
        fields.put(total, getField(total, ocrdText));
        fields.put(cardNumber, getField(cardNumber, ocrdText));

        return fields;
    }

    private String getField(String fieldName, String ocrdText){
        String value = "";
        LinkedList<String> fieldValues = firstPass(fieldName, ocrdText);

        // perform the first pass.
        if(fieldValues.size() == 1){
            value = fieldValues.getFirst();
        }

        else if(fieldValues.size() > 1){

        }

        // need to perform a more sophisticated search, perform a second pass.
        else if(fieldValues.size() == 0){
            secondPass(fieldName, ocrdText);
        }

        // need to perform a more sophisticated search, perform a third pass.
        else {

        }

        return value;
    }

    /**
     * Method used by performFieldExtraction to find a relevent field,
     * it assumes that the name of the field is perfect.
     *
     * Passes through ocrdText word by word searching for the relevent field.
     * If a match is found return the next string value. Check it is not too
     * long and not too short for the situation.
     *
     * @return The associated with the given field as a String.
     */
    private LinkedList<String> firstPass(String fieldName, String ocrdText){
        String[] words = splitOcrdText(ocrdText);

        boolean found = false;
        LinkedList<String> values = new LinkedList<>();

        int i = 0;
        for(String word : words){
            // System.out.println(word);
            if(word.equals(fieldName)){
                found = true;
            }

            // to include cent amount after space character.
            else if(found && fieldName.equals("sale")){
                word = word.concat(words[i+1]);
                values.add(word);
            }

            else if(found){
                values.add(word);
                found = false;
            }

            i++;
        }
        return values;
    }

    private LinkedList<String> secondPass(String fieldName, String ocrdText){
        String[] words = splitOcrdText(ocrdText);
        stringComparison = new StringComparison();

        boolean found = false;
        LinkedList<String> values = new LinkedList<>();

        for(String word : words){
            int score = stringComparison.stringCompare(word,"AID");

            if(found){
                values.add(word);
                found = false;
            }

            else if(score!=-1){
                found = true;
            }
        }

        return values;
    }

    private String[] splitOcrdText(String ocrdText){
        String[] words = null;

        try {
            words = ocrdText.split("\\s+");
        } catch (PatternSyntaxException ex) {
            ex.printStackTrace();
        }

        return words;
    }

    private boolean checkValue(String fieldName, String textFound){
        return true;
    }
}
