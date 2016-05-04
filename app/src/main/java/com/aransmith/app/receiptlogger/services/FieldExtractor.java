package com.aransmith.app.receiptlogger.services;

import com.aransmith.app.receiptlogger.model.CurrencyField;
import com.aransmith.app.receiptlogger.model.DateField;
import com.aransmith.app.receiptlogger.model.FieldName;
import com.aransmith.app.receiptlogger.model.PriceField;

import java.util.HashMap;
import java.util.regex.PatternSyntaxException;

/**
 * Created by Aran on 3/15/2016.
 * FieldExtractor is responsible for reading source OCR text
 * and interpreting receipt fields within e.g. Date, Currency,
 * Sale price, end debit card number etc.
 */
public class FieldExtractor {

    StringComparison stringComparison;

    public HashMap<String,String> getPrice(String[] words){

        HashMap<String,String> priceInformation = new HashMap<>();
        String amount = "";

        amount = initialFieldSearch(new PriceField(), words);

        if(amount.equals("")){
            amount = imperfectFieldSearch(new PriceField(), words);
        }

        priceInformation.put("amount", amount);

        return priceInformation;
    }

    public String getDate(String[] words) {

        String date = "";
        date = initialFieldSearch(new DateField(), words);

        if(date.equals("")){
            date = imperfectFieldSearch(new DateField(), words);
        }
        return date;
    }

    public String getCurrency(String[] words) {

        String currency = "";
        currency = initialFieldSearch(new CurrencyField(), words);

        if(currency.equals("")){
            currency = imperfectFieldSearch(new CurrencyField(), words);
        }

        return currency;
    }

    private String initialFieldSearch(FieldName field, String[] words){

        int i = 0 ;
        String value;
        for(String word : words){
            // a comparable fieldname is found so extract the corresponding value
            if (field.equals(word)){
                value = getNextConsecutiveMembers(words, i, 2, field.getSeperators());

                return extractGoodValue(value, field);
            }

            // try checking with a combination of this word and the next
            else if(i != words.length-1 && field.equals(word + words[i + 1])){

                System.out.println("value comprises of two strings: " + word+words[i+1]);
                value = getNextConsecutiveMembers(words, i + 1, 2, field.getSeperators());

                return extractGoodValue(value, field);
            }
            i++;
        }

        return "";
    }

    private String imperfectFieldSearch(FieldName field, String[] words){
        System.out.println("===No perfect price field was found, performing a second pass===");

        String value = "";
        stringComparison = new StringComparison();
        int pos = 0;
        int bestScore = -1;
        int candidatePos = -1;
        String bestCandidate = null;
        boolean candidateFound = false;

        // search through all the words for the usual field name for a price and note its
        // position
        for(String word: words){

            // find the best candidate based off levenshtein distance
            int score = field.compare(word);

            if(score != -1 && (bestCandidate == null || score < bestScore)){
                // a likely candidate was found of one word, store the word at this position,
                // with its score

                // check if it is in fact a price amount
                String test = getNextConsecutiveMembers(words, pos, 2, field.getSeperators());

                System.out.println("2ndP: Found a likely candidate: " + word + " " + test);

                if(checkPriceFormat(test) || test.matches(field.getFormat())){

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
                score = field.compare(word+words[pos+1]);
            }

            if(score != -1 && (bestCandidate == null || score < bestScore)){
                String test = getNextConsecutiveMembers(words, pos+1, 2, field.getSeperators());
                System.out.println("2ndP: Found a likely candidate: " + word + words[pos+1] +
                        " " + test);

                if(checkPriceFormat(test) || test.matches(field.getFormat())){

                    System.out.println("2ndP: its format is what we are looking for.");

                    bestScore = score;
                    bestCandidate = word + words[pos+1];
                    candidatePos = pos+1;
                    candidateFound = true;

                    System.out.println("2ndP: Candidate " + bestCandidate + " will be considered");
                }
            }
            pos++;
        }

        if(candidateFound && candidatePos != -1) {

            System.out.println("2ndP: The best candidate found was: " + bestCandidate);

            value = getNextConsecutiveMembers(words, candidatePos, 2, field.getSeperators());

            System.out.println("2ndP: Amount found was from imperfectfieldocr: " + value);
            // check if the amount is of the right format
            if (!checkPriceFormat(value)) {

                // check if the format is 9.99 rather than EUR9.99
                System.out.println("2ndP: Checking if amount is of the right format");
                if (value.matches(field.getFormat())) {
                    System.out.println("2ndP: " + value + " was found to be of the right format.");
                    return value;
                }
            }

            else if(checkPriceFormat(value)){
                System.out.println("2ndP: Amount is of the right format.");

                // cleanup
                return field.cleanup(value);
            }

        } else {
            System.out.println("2ndP: No candidate found on second pass.");
        }

        return "";
    }

    private String extractGoodValue(String value, FieldName field){
        if(!field.checkFormat(value)){

            System.out.println("Checking if value is of the right format.");
            if(value.matches(field.getFormat())){
                return value;
            }
        }

        // otherwise it is of the format EUR9.99
        else if(field.checkFormat(value)) {
            System.out.println("Amount is of the right format.");

            // perform any cleanup if required
            value = field.cleanup(value);
            return value;
        }

        return "";
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
