package com.aransmith.app.receiptlogger.services;

import com.aransmith.app.receiptlogger.model.PriceField;

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

    public HashMap<String,String> getPrice(String[] words){

        HashMap<String,String> priceInformation = new HashMap<>();
        PriceField priceField = new PriceField();
        String amount = "";
        String currency;
        boolean found = false;

        // perform a firstpass of the ocrdtext

        int i = 0 ;
        for(String word : words){
            // price field found, obtain the next two strings in array as they correspond to something
            // like EUR9 99
            if (priceField.equals(word)){
                amount = getNextConsecutiveMembers(words, i, 2, ".");

                // check if the amount is of the right format
                if(!checkPriceFormat(amount)){

                    // check if the format is 9.99 rather than EUR9.99
                    if(!amount.matches("(\\d+(.)(\\d+))")){
                        System.out.println("The format is neither what is expected. Must perform price" +
                                " recovery or search elsewhere for the end price.");
                    }
                }
                // get currency
                currency = amount.substring(0,2);

                System.out.println("Currency is found to be: " + currency);

                // add currency and amount to hashmap
                priceInformation.put("currency", currency);
                priceInformation.put("amount", amount);
                found = true;
            }
            i++;
        }

        if(!found){
            stringComparison = new StringComparison();
            int pos = 0;
            int bestScore = -1;
            int candidatePos = -1;
            String bestCandidate = null;
            boolean candidateFound = false;

            // search through all the words for the usual field name for a price and note its
            // position
            for(String word: words){
                int score = priceField.compare(word);

                // a likely candidate was found, store the word at this position, with its score
                if(score != -1 && (bestCandidate == null || score < bestScore)){
                    bestScore = score;
                    bestCandidate = word;
                    candidatePos = pos;
                    candidateFound = true;
                }
                pos++;
            }

            //
            if(candidateFound && candidatePos != -1) {
                System.out.println("The best candidate found was: " + bestCandidate);

                amount = getNextConsecutiveMembers(words, candidatePos, 2, ".");

                System.out.println("Amount found was from imperfectpricefieldocr: " + amount);
                // check if the amount is of the right format
                if (!checkPriceFormat(amount)) {

                    // check if the format is 9.99 rather than EUR9.99
                    if (!amount.matches("(\\d+(.)(\\d+))")) {
                        System.out.println("The format is neither what is expected. Must perform price" +
                                " recovery or search elsewhere for the end price.");
                    }
                }
                // get currency
                if (!amount.equals("")) {
                    currency = amount.substring(0, 2);

                    System.out.println("Currency is found to be: " + currency);

                    // add currency and amount to hashmap
                    priceInformation.put("currency", currency);
                    priceInformation.put("amount", amount);
                    found = true;
                }
            } else {
                System.out.println("No candidate found on second pass.");
            }
        }

        System.out.println("Amount was found to be: " + amount);

        return priceInformation;
    }

    public boolean checkPriceFormat(String price){
        return price.matches("([a-zA-Z]{3}\\d+(.)(\\d+))");
    }

    public String getNextConsecutiveMembers(String[] words, int pos, int amount, String seperator) {
        String result = "";
        try{


            int i = 0;
            while(i < amount){
                result = result + seperator + words[pos+ i + 1];
                i++;
            }

            result = result.substring(1);
        } catch(ArrayIndexOutOfBoundsException e){
            return result;
        }
        return result;
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


        /*boolean found = false;
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
        }*/
        return null;
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

    public HashMap<String, String> getFields(String ocrdText){
        // split text and pass into each getField function

        String[] splitOcrdText = splitOcrdText(ocrdText);
        HashMap<String,String> priceInfo = getPrice(splitOcrdText);

        return priceInfo;
    }
}
