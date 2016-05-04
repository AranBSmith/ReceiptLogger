package com.aransmith.app.receiptlogger.model;

/**
 * Created by Aran on 3/20/2016.
 */
public class CurrencyField extends FieldName{

    public CurrencyField(){
        super.fieldNames = new String[] {"sale", "totalamount", "amountdue", "total", "totaldue",
                "visadebitsale", "saleamount", "cardsales", "goodsvalue","amount"};
    }

    public String getFormat(){
        return "(\\d+(.)(\\d+))";
    }

    @Override
    public String getSeperators() {
        return ".";
    }

    @Override
    public String cleanup(String priceWithCurrency){
        String value = priceWithCurrency.substring(0,3);
        return value;
    }

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
