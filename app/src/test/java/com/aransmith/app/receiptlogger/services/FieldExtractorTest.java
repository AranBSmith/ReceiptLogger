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
    String noPerfectPriceField;
    String AID, prettyGoodAID, imperfectAID;
    String merchantID, prettyGoodMerchantID, imperfectMerchantID;
    String total, prettyGoodTotal, imperfectTotal;
    String card, prettyGoodCard, imperfectCard;
    String[] prettyGoodOcrdArray;

    @Before
    public void setup(){
        AID = "aid";
        merchantID = "merchant ID";
        total = "sale";
        card = "number";

        //

        fieldExtractor = new FieldExtractor();

        noPerfectPriceField = "I II R E L A N D I WEETABIX EURS 9 T MILK 3LTR EURZJQ MANGO EU 00 " +
                "ICING SUGAR EUR 09 MILK CHOC EUROVBQ MILK CHOC Un t 9 MILK CHOC EURO b5 MILK CH" +
                " EURO 9 MILK CHI EUQO 59 PORK PIES EUR 50 DNVDELICKDM E094 00 CH1CKEN NR ERIN SE5" +
                " 38 LF 071 39 RADIUM BA Em1 99 OTAL ELR29 80 AID WWI 331010 " +
                "W IItIIIIllIll2691 ICC PAN SEO 0 oo AUTH out 33442 s um 11 13 EXPIRY 11 15 V " +
                "Carmggr pIN Vormod EURO 00 W P on CLIBC l 31 You 099 E d 29 ARI";

        imperfectOCRText = "I II R E L A N D I WEETABIX EURS 9 T MILK 3LTR EURZJQ MANGO EU 00 " +
                "ICING SUGAR EUR 09 MILK CHOC EUROVBQ MILK CHOC Un t 9 MILK CHOC EURO b5 MILK CH" +
                " EURO 9 MILK CHI EUQO 59 PORK PIES EUR 50 DNVDELICKDM E094 00 CH1CKEN NR ERIN SE5" +
                " 38 LF 071 39 RADIUM BA Em1 99 OTAL ELR29 80 VISAMBU SALE 29 80 AID WWI 331010 " +
                "W IItIIIIllIll2691 ICC PAN SEO 0 oo AUTH out 33442 s um 11 13 EXPIRY 11 15 V " +
                "Carmggr pIN Vormod EURO 00 W P on CLIBC l 31 You 099 E d 29 ARI";

        prettyGoodOCRText = "III I It E L l ru c All 7 T FRENCH CFFE EUR2 30 BEEF SLICES " +
                "EUR2 40 MARSHMALLOWS x EURO 74 TOTAL EUR5 44 VISADEBIT SALE EUR5 44 AID " +
                "A0000000031010 NUMBER 2691 ICC PAN SEQ N0 00 AUTH CODE 946459 MERCHANT 75401743 " +
                "START 11 13 EXPIRY 11 16 Cardholder PIN Verified CHANGE DUE EUR0 00 sIGN UP FOR " +
                "CLUBCARD You could have earned 5 Ciubcard points in this transaction";

        prettyGoodAID = "A0000000031010";
        prettyGoodCard = "2691";
        prettyGoodTotal = "eur544";

        prettyGoodOcrdArray = prettyGoodOCRText.split("\\s+");
    }

   /* @Test
    public void testPerformFieldExtraction(){
        HashMap<String, String> fields = fieldExtractor.performFieldExtraction(prettyGoodOCRText);
        // Log.v(TEXTEXTRACTION, fields.get(AID)); this causes a bug..
        assertNotNull(fields.get(AID));
        assertTrue(fields.get(AID).equals(prettyGoodAID));
        assertTrue(fields.get(total).equals(prettyGoodAID));
        assertTrue(fields.get(card).equals(prettyGoodAID));
    }*/

    @Test
    public void testGetPrice(){
        // perform field extraction

        // this segment is concerned with testing on ocr text that isn't too corrupt
        HashMap<String,String> priceInformation =
                fieldExtractor.getPrice(prettyGoodOCRText.toLowerCase().split("\\s+"));
        assertNotNull(priceInformation);
        String price = priceInformation.get("amount");
        System.out.println("Price information from pretty good ocr: " + price);
        assertTrue(price.equals("eur5.44"));

        // this segment is concerned with testing on ocr text is imperfect
        priceInformation = fieldExtractor.getPrice(imperfectOCRText.toLowerCase().split("\\s+"));
        assertNotNull(priceInformation);
        price = priceInformation.get("amount");
        System.out.println("Price information from imperfect ocr: " + price);
        assertTrue(price.equals("29.80"));

        // this segment is concerned with testing on ocr text that has no perfect price field
        priceInformation = fieldExtractor.getPrice(noPerfectPriceField.toLowerCase().split("\\s+"));
        assertNotNull(priceInformation);
        price = priceInformation.get("amount");
        System.out.println("Price information from noperfectpricefield ocr: " + price);
        assertTrue(price.equals("elr29.80"));
    }

    @Test
    public void testGetNextConsecutiveMembers() {
        String result = fieldExtractor.getNextConsecutiveMembers(prettyGoodOcrdArray,23,2,".");
        System.out.println("Caught: " + result);
        System.out.println(prettyGoodOcrdArray[23]);
        System.out.println(prettyGoodOcrdArray[24]);
        System.out.println(prettyGoodOcrdArray[25]);
        assertTrue(result.equals("EUR5.44"));
    }

    @Test
    public void testCheckPriceFormat(){
        assertTrue(fieldExtractor.checkPriceFormat("EUR5.44"));
    }
}
