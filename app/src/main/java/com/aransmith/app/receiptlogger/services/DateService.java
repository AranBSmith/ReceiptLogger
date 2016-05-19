package com.aransmith.app.receiptlogger.services;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Aran on 4/30/2016.
 * Service for performing tasks on strings representing dates.
 */
public class DateService {

    /**
     * checks if a date is within the last 30 days.
     * @param expenseDate
     * @return <Code>true</Code> if date was within that last 30 days
     */
    public boolean withinMonth(String expenseDate){
        try{
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date date = format.parse(expenseDate);

            Calendar beforeMonth = Calendar.getInstance();
            beforeMonth.add(Calendar.MONTH, -1);

            if (date.after(beforeMonth.getTime()))
                return true;

            else return false;
        } catch (ParseException e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Checks if a String representing a date adheres to the format of mm/dd/yyyy
     * @param inputDate
     * @return <Code>true</Code> if it matches the pattern mm/dd/yyyy
     */
    public boolean checkInputDate(String inputDate){
        try {
            DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            dateFormat.setLenient(false);
            dateFormat.parse(inputDate);
        } catch (ParseException e){
            return false;
        }

        return true;
    }
}
