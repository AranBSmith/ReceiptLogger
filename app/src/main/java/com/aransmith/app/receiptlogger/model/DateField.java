package com.aransmith.app.receiptlogger.model;

import com.aransmith.app.receiptlogger.interfaces.FieldName;

/**
 * Created by Aran on 3/30/2016.
 */
public class DateField extends FieldName {
    public DateField(){
        super.fieldNames = new String[] {"date", "datepurchased"};
    }
}
