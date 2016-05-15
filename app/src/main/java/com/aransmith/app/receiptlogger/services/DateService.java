package com.aransmith.app.receiptlogger.services;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Aran on 4/30/2016.
 */
public class DateService {
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
