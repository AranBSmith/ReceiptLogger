package com.aransmith.app.receiptlogger.services;

import com.aransmith.app.receiptlogger.model.DateField;
import com.aransmith.app.receiptlogger.model.PriceField;

import java.util.HashMap;
import java.util.regex.PatternSyntaxException;

/**
 * Created by Aran on 3/15/2016.
 * FieldExtractor is responsible for reading source OCR text
 * and interpreting receipt fields withing e.g. Date, Currency,
 * Sale price, end debit card number etc.
 */
public class FieldExtractor {

    StringComparison stringComparison;

    public HashMap<String,String> getPrice(String[] words){

        HashMap<String,String> priceInformation = new HashMap<>();
        PriceField priceField = new PriceField();
        String amount = "";
        String currency;
        boolean found = false;

        // perform a firstpass of the ocrdtext, this assumes our pricefields aren't corrupt
        int i = 0 ;
        for(String word : words){
            // price field found, obtain the next two strings in array as they correspond to something
            // like EUR9 99
            if (priceField.equals(word)){
                amount = getNextConsecutiveMembers(words, i, 2, ".");

                // check if the amount is of the right format
                if(!checkPriceFormat(amount)){

                    // check if the format is 9.99 rather than EUR9.99
                    System.out.println("Checking if amount is of the right format.");
                    if(amount.matches("(\\d+(.)(\\d+))")){
                        priceInformation.put("amount", amount);
                        found = true;
                    }
                }

                // otherwise it is of the right format EUR9.99
                else if(checkPriceFormat(amount)) {
                    System.out.println("Amount is of the right format.");
                    // get currency
                    currency = amount.substring(0,3);
                    amount = amount.substring(3,amount.length());

                    System.out.println("Currency is found to be: " + currency);

                    // add currency and amount to hashmap
                    priceInformation.put("currency", currency);
                    priceInformation.put("amount", amount);
                    found = true;
                }
            }


            else if(i != words.length-1 && priceField.equals(word+words[i+1])){
                System.out.println("Price field comprises of two strings: " + word+words[i+1]);
                amount = getNextConsecutiveMembers(words, i + 1, 2, ".");

                // check if the amount is of the right format
                if(!checkPriceFormat(amount)){

                    // check if the format is 9.99 rather than EUR9.99
                    System.out.println("Checking if amount is of the right format.");
                    if(amount.matches("(\\d+(.)(\\d+))")){
                        priceInformation.put("amount", amount);
                        found = true;
                    }
                }

                // otherwise it is of the right format EUR9.99
                else if(checkPriceFormat(amount)) {
                    System.out.println("Amount is of the right format.");
                    // get currency
                    currency = amount.substring(0,3);
                    amount = amount.substring(3,amount.length());

                    System.out.println("Currency is found to be: " + currency);

                    // add currency and amount to hashmap
                    priceInformation.put("currency", currency);
                    priceInformation.put("amount", amount);
                    found = true;
                }
            }

            i++;
        }


        if(!found){
            System.out.println("===No perfect price field was found, performing a second pass===");

            stringComparison = new StringComparison();
            int pos = 0;
            int bestScore = -1;
            int candidatePos = -1;
            String bestCandidate = null;
            boolean candidateFound = false;
            int candidateLength = 1;

            // search through all the words for the usual field name for a price and note its
            // position
            for(String word: words){

            // find the best candidate based off levenshtein distance
                int score = priceField.compare(word);

                if(score != -1 && (bestCandidate == null || score < bestScore)){
                    // a likely candidate was found of one word, store the word at this position,
                    // with its score

                    // check if it is in fact a price amount
                    String test = getNextConsecutiveMembers(words, pos, 2, ".");

                    System.out.println("2ndP: Found a likely candidate: " + word + " " + test);

                    if(checkPriceFormat(test) || test.matches("(\\d+(.)(\\d+))")){

                        System.out.println("2ndP: its format is what we are looking for.");

                        bestScore = score;
                        bestCandidate = word;
                        candidatePos = pos;
                        candidateFound = true;

                        System.out.println("2ndP: Candidate " + bestCandidate + " will be considered");
                    }
                }

                // the score wasn't as good as what is currently found, try to see if it is two words
                // long

                if(pos != words.length-1) {
                    score = priceField.compare(word+words[pos+1]);
                    //System.out.println("The score of " +)
                }


                if(score != -1 && (bestCandidate == null || score < bestScore)){
                    // a likely candidate was found of two words in length, store the word at this position,
                    // with its score

                    // check if it is in fact a price amount
                    String test = getNextConsecutiveMembers(words, pos+1, 2, ".");

                    System.out.println("2ndP: Found a likely candidate: " + word + words[pos+1] +
                            " " + test);

                    if(checkPriceFormat(test) || test.matches("(\\d+(.)(\\d+))")){

                        System.out.println("2ndP: its format is what we are looking for.");

                        bestScore = score;
                        bestCandidate = word + words[pos+1];
                        candidatePos = pos+1;
                        candidateFound = true;
                        candidateLength = 2;

                        System.out.println("2ndP: Candidate " + bestCandidate + " will be considered");
                    }
                }

                pos++;
            }

            if(candidateFound && candidatePos != -1) {

                System.out.println("2ndP: The best candidate found was: " + bestCandidate);

                amount = getNextConsecutiveMembers(words, candidatePos, 2, ".");

                System.out.println("2ndP: Amount found was from imperfectpricefieldocr: " + amount);
                // check if the amount is of the right format
                if (!checkPriceFormat(amount)) {

                    // check if the format is 9.99 rather than EUR9.99
                    System.out.println("2ndP: Checking if amount is of the right format: e.g. : 9.99.");
                    if (amount.matches("(\\d+(.)(\\d+))")) {
                        System.out.println("2ndP: " + amount + " was found to be of the right format.");
                        priceInformation.put("amount", amount);
                        found = true;
                    }
                }

                else if(checkPriceFormat(amount)){
                    System.out.println("2ndP: Amount is of the right format.");
                    // get currency
                    currency = amount.substring(0, 3);
                    amount = amount.substring(3,amount.length());

                    System.out.println("2ndP: Currency is found to be: " + currency);

                    // add currency and amount to hashmap
                    priceInformation.put("currency", currency);
                    priceInformation.put("amount", amount);
                    found = true;
                }
            } else {
                System.out.println("2ndP: No candidate found on second pass.");
            }
        }

        // System.out.println("Amount was found to be: " + amount);

        return priceInformation;
    }

    public String getDate(String[] words) {
        String date = "";
        DateField dateField = new DateField();
        boolean found = false;

        // perform a firstpass of the ocrdtext, this assumes our pricefields aren't corrupt
        int i = 0;
        for (String word : words) {
            // price field found, obtain the next two strings in array as they correspond to something
            // like EUR9 99
            if (dateField.equals(word)) {
                date = getNextConsecutiveMembers(words, i, 2, "/");

                // check if the amount is of the right format
                if (!checkDateFormat(date)) {

                    // check if the format is 9.99 rather than EUR9.99
                    System.out.println("Checking if date is of the right format.");
                    if (date.matches("(\\d+(/)(\\d+))")) {
                        /*dateInformation.put("amount", amount);
                        found = true;*/
                    }
                }

                // otherwise it is of the right format 99/99
                else if (checkDateFormat(date)) {
                    System.out.println("Date is of the right format.");
                    // get currency

                    System.out.println("Date is found to be: " + date);

                    // add currency and amount to hashmap
                    found = true;
                }
            }

            else if (i != words.length - 1 && dateField.equals(word + words[i + 1])) {
                System.out.println("Date field comprises of two strings: " + word + words[i + 1]);
                date = getNextConsecutiveMembers(words, i + 1, 2, "/");

                // check if the amount is of the right format
                if (!checkDateFormat(date)) {

                    // check if the format is ?? rather than 99/99
                    System.out.println("Checking if amount is of the right format.");
                    if (date.matches("(\\d+(/)(\\d+))")) {
                       /* priceInformation.put("amount", amount);
                        found = true;*/
                        }
                    }

                    // otherwise it is of the right format EUR9.99
                    else if (checkDateFormat(date)) {
                        System.out.println("Amount is of the right format.");
                        // get currency
                  /*  currency = amount.substring(0,3);
                    amount = amount.substring(3,amount.length());*/

                        System.out.println("Date is found to be: " + date);

                    /*// add currency and amount to hashmap
                    priceInformation.put("currency", currency);
                    priceInformation.put("amount", amount);*/
                    found = true;
                }
            }

            i++;
        }

        if(!found){
            System.out.println("===No perfect price field was found, performing a second pass===");

            stringComparison = new StringComparison();
            int pos = 0;
            int bestScore = -1;
            int candidatePos = -1;
            String bestCandidate = null;
            boolean candidateFound = false;
            int candidateLength = 1;

            // search through all the words for the usual field name for a price and note its
            // position
            for(String word: words){

                // find the best candidate based off levenshtein distance
                int score = dateField.compare(word);

                if(score != -1 && (bestCandidate == null || score < bestScore)){
                    // a likely candidate was found of one word, store the word at this position,
                    // with its score

                    // check if it is in fact a price amount
                    String test = getNextConsecutiveMembers(words, pos, 2, ".");

                    System.out.println("2ndP: Found a likely candidate: " + word + " " + test);

                    if(checkPriceFormat(test) || test.matches("(\\d+(.)(\\d+))")){

                        System.out.println("2ndP: its format is what we are looking for.");

                        bestScore = score;
                        bestCandidate = word;
                        candidatePos = pos;
                        candidateFound = true;

                        System.out.println("2ndP: Candidate " + bestCandidate + " will be considered");
                    }
                }

                // the score wasn't as good as what is currently found, try to see if it is two words
                // long

                if(pos != words.length-1) {
                    score = dateField.compare(word+words[pos+1]);
                    //System.out.println("The score of " +)
                }


                if(score != -1 && (bestCandidate == null || score < bestScore)){
                    // a likely candidate was found of two words in length, store the word at this position,
                    // with its score

                    // check if it is in fact a price amount
                    String test = getNextConsecutiveMembers(words, pos+1, 2, ".");

                    System.out.println("2ndP: Found a likely candidate: " + word + words[pos+1] +
                            " " + test);

                    if(checkPriceFormat(test) || test.matches("(\\d+(.)(\\d+))")){

                        System.out.println("2ndP: its format is what we are looking for.");

                        bestScore = score;
                        bestCandidate = word + words[pos+1];
                        candidatePos = pos+1;
                        candidateFound = true;
                        candidateLength = 2;

                        System.out.println("2ndP: Candidate " + bestCandidate + " will be considered");
                    }
                }

                pos++;
            }

            if(candidateFound && candidatePos != -1) {

                System.out.println("2ndP: The best candidate found was: " + bestCandidate);

                date = getNextConsecutiveMembers(words, candidatePos, 2, "/");

                System.out.println("2ndP: Date found was from imperfectpricefieldocr: " + date);
                // check if the amount is of the right format
                if (!checkDateFormat(date)) {

                    // check if the format is 9.99 rather than EUR9.99
                    System.out.println("2ndP: Checking if amount is of the right format: e.g. : 9.99.");
                    if (date.matches("(\\d+(/)(\\d+))")) {
                        System.out.println("2ndP: " + date + " was found to be of the right format.");
                        found = true;
                    }
                }

                else if(checkDateFormat(date)){
                    System.out.println("2ndP: Amount is of the right format.");

                    System.out.println("2ndP: Date is found to be: " + date);

                    found = true;
                }
            } else {
                System.out.println("2ndP: No candidate found on second pass.");
            }
        }

        return date;
    }

    private boolean checkDateFormat(String date) {

        boolean check = date.matches("(\\d\\d.\\d\\d)");
        if (check){
            System.out.println("checkDateFormat: " + date + " is of the right format");
            return true;
        } else {
            System.out.println("checkDateFormat: " + date + " was not of the right format.");
            return false;
        }
    }

    public boolean checkPriceFormat(String price){
        boolean check = price.matches("([a-zA-Z]{3}\\d+(.)(\\d+))");
        if (check){
            System.out.println("checkPriceFormat: " + price + " is of the right format");
            return true;
        } else {
            System.out.println("checkPriceFormat: " + price + " was not of the right format.");
            return false;
        }
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

    private String[] splitOcrdText(String ocrdText){
        String[] words = null;

        try {
            words = ocrdText.split("\\s+");
        } catch (PatternSyntaxException ex) {
            ex.printStackTrace();
        }

        return words;
    }

    public HashMap<String, String> getFields(String ocrdText){
        // split text and pass into each getField function

        ocrdText = ocrdText.toLowerCase();
        String[] splitOcrdText = splitOcrdText(ocrdText);
        HashMap<String,String> priceInfo = getPrice(splitOcrdText);
        String date = getDate(splitOcrdText);

        priceInfo.put("date", date);
        return priceInfo;
    }
}
