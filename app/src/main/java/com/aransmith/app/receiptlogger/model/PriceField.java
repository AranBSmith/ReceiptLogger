package com.aransmith.app.receiptlogger.model;

/**
 * Created by Aran on 3/20/2016.
 */
public class PriceField {

    private String sale, totalAmount, amountDue, total, visaDebitSale, saleAmount, cardSales,
        totalDue;

    public PriceField(){
        sale = "sale";
        totalAmount = "total amount";
        amountDue = "amount due";
        total = "total";
        totalDue = "total due";
        visaDebitSale = "visadebit sale";
        saleAmount = "sale amount";
        cardSales = "card sales";
    }

    public boolean equals(String word){
        return (
                word.equals(sale) ||
                word.equals(totalAmount) ||
                word.equals(amountDue) ||
                word.equals(total) ||
                word.equals(totalDue) ||
                word.equals(visaDebitSale) ||
                word.equals(saleAmount) ||
                word.equals(cardSales)
        );
    }
}
