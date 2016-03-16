package com.aransmith.app.receiptlogger.services;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

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
        AID = "AID";

        fieldExtractor = new FieldExtractor();

        imperfectOCRText = "No Image l a 1 Q Q w Md1 Stok relanj m i St garets Road 1 F has 1 1 " +
                "11 1 5 Dub in 1 3 SAIJEBIT I 1 1 lard number HHHHH QSQI mp w Date 11 16 f App " +
                "PAN Seq No 00 Merchant ID 677003600007595 1 Fermmal ID 25675942 EH N13 3140 y " +
                "SALE r if r t Your amount will be de W with the total amoum 1 Goods EUR 2 0 1 V" +
                " 100 Authormation code 605442 1 1 IUD 10000000031010 1 V AED g g 1010 20161128 " +
                "23 9 DIN Verified 151 3 Please keep the receipt for your racigds H 1203101131 " +
                "copy";

        // TODO: get something better.
        prettyGoodOCRText = "11er ik m t x l I I Vl xfi x z l In beAumn Start 2091 igglryl ua Ix" +
                " SALE Please debit my account A WM 1 EUR220 00 0 VOTAL EUR220 00 1 PIN VERIFIED " +
                "s Please keep receipt 3 5 z for your records 1 Ev PTID 13333PP80435710 3 HID 3" +
                " 36314 1 TH 0769 f f Date 05 01 2016 4 Time 18 53 11 1 f Authcode 960175 r x Ref" +
                " 52 13 AID A0000000031010 APE Seq 09 To an 1W 00000000000019591445 v v v 1 085 r" +
                " 90 ENE w GEE nn";

        prettyGoodAID = "A0000000031010";

    }

    @Test
    public void testPerformFieldExtraction(){
        HashMap<String, String> fields = fieldExtractor.performFieldExtraction(prettyGoodOCRText);
        // Log.v(TEXTEXTRACTION, fields.get(AID)); this causes a bug..
        assertTrue(fields.get(AID).equals(prettyGoodAID));
    }
}
