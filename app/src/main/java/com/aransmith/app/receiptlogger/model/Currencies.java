package com.aransmith.app.receiptlogger.model;

import lombok.Data;

/**
 * Created by Aran on 06/05/2016.
 * http://stackoverflow.com/questions/3536968/get-all-possible-available-currencies
 */

@Data
public class Currencies {
    String[] currencies;

    public Currencies(){
        this.currencies = new String[] { "EUR", "GBP", "USD", "JPY", "CNY", "SDG", "RON", "MKD",
                "MXN", "CAD", "ZAR", "AUD", "NOK", "ILS", "ISK", "SYP", "LYD", "UYU", "YER", "CSD",
                "EEK", "THB", "IDR", "LBP", "AED", "BOB", "QAR", "BHD", "HNL", "HRK",
                "COP", "ALL", "DKK", "MYR", "SEK", "RSD", "BGN", "DOP", "KRW", "LVL",
                "VEF", "CZK", "TND", "KWD", "VND", "JOD", "NZD", "PAB", "CLP", "PEN",
                "DZD", "CHF", "RUB", "UAH", "ARS", "SAR", "EGP", "INR", "PYG",
                "TWD", "TRY", "BAM", "OMR", "SGD", "MAD", "BYR", "NIO", "HKD", "LTL",
                "SKK", "GTQ", "BRL", "HUF", "IQD", "CRC", "PHP", "SVC", "PLN"};
    }

    public int getPosition(String curr){
        int i = 0;
        for(String member: currencies){
            if(curr.equals(member)){
                return i;
            }

            i++;
        }

        return -1;
    }
}
