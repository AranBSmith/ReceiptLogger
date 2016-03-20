package com.aransmith.app.receiptlogger.services;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by Aran on 3/15/2016.
 */

public class FieldExtractorTest {

    private static final String TEXTEXTRACTION = "TEXTEXTRACTION";

    FieldExtractor fieldExtractor;

    String imperfectOCRText, prettyGoodOCRText;
    String AID, prettyGoodAID, imperfectAID;
    String merchantID, prettyGoodMerchantID, imperfectMerchantID;
    String total, prettyGoodTotal, imperfectTotal;
    String card, prettyGoodCard, imperfectCard;

    @Before
    public void setup(){
        AID = "aid";
        merchantID = "merchant ID";
        total = "sale";
        card = "number";


        fieldExtractor = new FieldExtractor();

        imperfectOCRText = "l a 1 Q Q w Md1 Stok relanj m i St garets Road 1 F has 1 1 " +
                "11 1 5 Dub in 1 3 SAIJEBIT I 1 1 lard number HHHHH QSQI mp w Date 11 16 f App " +
                "PAN Seq No 00 Merchant ID 677003600007595 1 Fermmal ID 25675942 EH N13 3140 y " +
                "SALE r if r t Your amount will be de W with the total amoum 1 Goods EUR 2 0 1 V" +
                " 100 Authormation code 605442 1 1 IUD 10000000031010 1 V AED g g 1010 20161128 " +
                "23 9 DIN Verified 151 3 Please keep the receipt for your racigds H 1203101131 " +
                "copy";

        prettyGoodOCRText = "III I It E L l ru c All 7 T FRENCH CFFE EUR2 30 BEEF SLICES " +
                "EUR2 40 MARSHMALLOWS x EURO 74 TOTAL EUR5 44 VISADEBIT SALE EUR5 44 AID " +
                "A0000000031010 NUMBER 2691 ICC PAN SEQ N0 00 AUTH CODE 946459 MERCHANT 75401743 " +
                "START 11 13 EXPIRY 11 16 Cardholder PIN Verified CHANGE DUE EUR0 00 sIGN UP FOR " +
                "CLUBCARD You could have earned 5 Ciubcard points in this transaction";

        prettyGoodAID = "A0000000031010";
        prettyGoodCard = "2691";
        prettyGoodTotal = "eur544";

    }

    @Test
    public void testPerformFieldExtraction(){
        HashMap<String, String> fields = fieldExtractor.performFieldExtraction(prettyGoodOCRText);
        // Log.v(TEXTEXTRACTION, fields.get(AID)); this causes a bug..
        assertNotNull(fields.get(AID));
        assertTrue(fields.get(AID).equals(prettyGoodAID));
        assertTrue(fields.get(total).equals(prettyGoodAID));
        assertTrue(fields.get(card).equals(prettyGoodAID));
    }
}
