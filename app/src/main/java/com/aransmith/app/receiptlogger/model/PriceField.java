package com.aransmith.app.receiptlogger.model;

/**
 * Created by Aran on 3/20/2016.
 */
public class PriceField extends FieldName{

    public PriceField(){
        super.fieldNames = new String[] {"sale", "totalamount", "amountdue", "total", "totaldue",
                                    "visadebitsale", "saleamount", "cardsales", "goodsvalue",
                                        "amount"};
    }
}
