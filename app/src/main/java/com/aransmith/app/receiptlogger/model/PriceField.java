package com.aransmith.app.receiptlogger.model;

public class PriceField extends FieldName {

    public PriceField(){
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
        String value = priceWithCurrency.substring(3,priceWithCurrency.length());
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