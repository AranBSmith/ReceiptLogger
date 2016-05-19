package com.aransmith.app.receiptlogger.model;

/**
 * Created by Aran on 3/30/2016.
 *  An encapsulation of different labels that are used to identify a date on a receipt.
 */
public class DateField extends FieldName {
    public DateField(){
        super.fieldNames = new String[] {"date", "datepurchased", "dateofpurchase"};
    }

    /**
     * get the pattern to which a date usually complies.
     * @return regular expression in String
     */
    public String getFormat(){
        return "(\\d+(/)(\\d+))";
    }

    public String cleanup(String value){
        return value;
    }

    /**
     * OCR text will show dates as 00 00 0000, we need an appropriate seperator to make one string, this
     * is the "/" character, for double values.
     * @return "/"
     */
    @Override
    public String getSeperators(){
        return "/";
    }

    /**
     * Is the format of the date value correct?
     * @param date
     * @return true if it matches, else false
     */
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
