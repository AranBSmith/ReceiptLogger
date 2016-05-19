package com.aransmith.app.receiptlogger.model;

/**
 * An encapsulation of different labels that are used to identify an end price on a receipt.
 */
public class PriceField extends FieldName {

    public PriceField(){
        super.fieldNames = new String[] {"sale", "totalamount", "amountdue", "total", "totaldue",
                "visadebitsale", "saleamount", "cardsales", "goodsvalue","amount"};
    }

    /**
     * get the pattern to which an end price usually complies.
     * @return regular expression in String
     */
    public String getFormat(){
        return "(\\d+(.)(\\d+))";
    }

    /**
     * OCR text will show prices as 00 00, we need an appropriate seperator to make one string, this
     * is the "." character, for double values.
     * @return
     */
    @Override
    public String getSeperators() {
        return ".";
    }

    @Override
    public String cleanup(String priceWithCurrency){
        String value = priceWithCurrency.substring(3,priceWithCurrency.length());
        return value;
    }

    /**
     * Is the format of the full price value correct? i.e. eur99999.999999
     * @param price
     * @return true if it matches, else false
     */
    public boolean checkFormat(String price){
        boolean check = price.matches("([a-zA-Z]{3}\\d+(.)(\\d+))");
        if (check){
            System.out.println("checkPriceFormat: " + price + " is of the right format");
            return true;
        } else {
            System.out.println("checkPriceFormat: " + price + " was not of the right format.");
            return false;
        }
    }
}