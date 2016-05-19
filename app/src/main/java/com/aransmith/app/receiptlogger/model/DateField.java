package com.aransmith.app.receiptlogger.model;

/**
 * Created by Aran on 3/30/2016.
 */
public class DateField extends FieldName {
    public DateField(){
        super.fieldNames = new String[] {"date", "datepurchased", "dateofpurchase"};
    }

    public String getFormat(){
        return "(\\d+(/)(\\d+))";
    }

    public String cleanup(String value){
        return value;
    }

    @Override
    public String getSeperators(){
        return "/";
    }

    public boolean checkFormat(String date){
        boolean check =
                date.matches("(\\d\\d.\\d\\d)") ||  // (01/01)
                date.matches("\\d\\d.\\d\\d.\\d\\d\\d\\d") ||  // (01/01/2001)
                date.matches("\\d\\d.\\d\\d.\\d\\d");  // (01/01/01)

        if (check){
            System.out.println("checkDateFormat: " + date + " is of the right format");
            return true;
        } else {
            System.out.println("checkDateFormat: " + date + " was not of the right format.");
            return false;
        }
    }
}
