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

    FieldExtractor fieldExtractor;

    String imperfectOCRText, prettyGoodOCRText;
    String noPerfectPriceField, ocrSample1, ocrSample2;
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
                " 38 LF 071 39 RADIUM BA Em1 99 OTAL ELR29 80 VISAMBU SALE 29 80 DATE 01 03 AID WWI" +
                " 331010 W IItIIIIllIll2691 ICC PAN SEO 0 oo AUTH out 33442 s um 11 13 EXPIRY 11 15 V " +
                "Carmggr pIN Vormod EURO 00 W P on CLIBC l 31 You 099 E d 29 ARI";

        prettyGoodOCRText = "III I It E L l ru c All 7 T FRENCH CFFE EUR2 30 BEEF SLICES " +
                "EUR2 40 MARSHMALLOWS x EURO 74 TOTAL EUR5 44 VISADEBIT SALE EUR5 44 AID " +
                "A0000000031010 NUMBER 2691 ICC PAN SEQ N0 00 AUTH CODE 946459 MERCHANT 75401743 " +
                "START 11 13 EXPIRY 11 16 Cardholder PIN Verified CHANGE DUE EUR0 00 sIGN UP FOR " +
                "CLUBCARD You could have earned 5 Ciubcard points in this transaction";

        ocrSample1 = "AAAAAAMKAAKA yum AX AAJ V m r A m 19 24 W Bg g y Vul MAR Ll r mu fun In" +
                " a Al litfv lk 7 18Lto VULNAL rm pr K gum TU AA AAAAAAAA a A AAKAAAAKAAA A SYGB" +
                " Mai r u 220 00 g 34 Intel i gs 2 HI VISA 220 UU u have pc V IBNJHH check in up" +
                " Arxtxkxx269 Stdl t ant xx see The St Expi ry xw xx ICC SALE Please mm 1 my " +
                "account Amount EUR220 00 a currency TOTAL EUR220 00 PIN VERIFIED P1 ease keep " +
                "recei p1 for your records PTID 13333PP80435710 Goo MID xxm36314 0mg Date 1 Time " +
                "18 53 11 V Authcode 960176 Total Ref 52 13 AID A0000000031010 Ap Seq 00 Tofu To " +
                "en 00000000000019591446 CODE RAIE GROSS VAT 0 23 0 6 220 00 41 14 mum n A no 1 f";

        ocrSample2 = "AAAAAAMKAAKA yum AX AAJ V m r A m 19 24 W Bg g y Vul MAR Ll r mu fun In" +
                " a Al litfv lk 7 18Lto VULNAL rm pr K gum TU AA AAAAAAAA a A AAKAAAAKAAA A SYGB" +
                " Mai r u 220 00 g 34 Intel i gs 2 HI VISA 220 UU u have pc V IBNJHH check in up" +
                " Arxtxkxx269 Stdl t ant xx see The St Expi ry xw xx ICC SALE Please mm 1 my " +
                "account a currency V1SADEBIT SA13 EUR220 00 PIN VERIFIED P1 ease keep " +
                "recei p1 for your records PTID 13333PP80435710 Goo MID xxm36314 0mg Date 1 Time " +
                "18 53 11 V Authcode 960176 Total Ref 52 13 AID A0000000031010 Ap Seq 00 Tofu To " +
                "en 00000000000019591446 CODE RAIE GROSS VAT 0 23 0 6 220 00 41 14 mum n A no 1 f";

        prettyGoodAID = "A0000000031010";
        prettyGoodCard = "2691";
        prettyGoodTotal = "eur544";

        prettyGoodOcrdArray = prettyGoodOCRText.split("\\s+");
    }

   /*@Test
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
        System.out.println("====Performing fieldExtraction on perfect string====");
        HashMap<String,String> priceInformation =
                fieldExtractor.getPrice(prettyGoodOCRText.toLowerCase().split("\\s+"));
        assertNotNull(priceInformation);
        String price = priceInformation.get("amount");
        System.out.println("Price information from pretty good ocr: " + price);
        assertTrue(price.equals("5.44"));

        // this segment is concerned with testing on ocr text is imperfect
        System.out.println("====Performing fieldExtraction on imperfect string====");
        priceInformation = fieldExtractor.getPrice(imperfectOCRText.toLowerCase().split("\\s+"));
        assertNotNull(priceInformation);
        price = priceInformation.get("amount");
        System.out.println("Price information from imperfect ocr: " + price);
        assertTrue(price.equals("29.80"));

        // this segment is concerned with testing on ocr text that has no perfect price field
        System.out.println("====Performing fieldExtraction on noperfectpricefield string====");
        priceInformation = fieldExtractor.getPrice(noPerfectPriceField.toLowerCase().split("\\s+"));
        assertNotNull(priceInformation);
        price = priceInformation.get("amount");
        System.out.println("Price information from noperfectpricefield ocr: " + price);
        assertTrue(price.equals("29.80"));

        // this segment is concerned with testing on ocr text that has no perfect price field
        System.out.println("====Performing fieldExtraction on ocrSample1 string====");
        priceInformation = fieldExtractor.getPrice(ocrSample1.toLowerCase().split("\\s+"));
        assertNotNull(priceInformation);
        price = priceInformation.get("amount");
        System.out.println("Price information from ocrSample1 ocr: " + price);
        assertTrue(price.equals("220.00"));

        System.out.println("====Performing fieldExtraction on ocrSample2 string====");
        priceInformation = fieldExtractor.getPrice(ocrSample2.toLowerCase().split("\\s+"));
        assertNotNull(priceInformation);
        price = priceInformation.get("amount");
        System.out.println("Price information from ocrSample2 ocr: " + price);
        assertTrue(price.equals("220.00"));
    }



    @Test
    public void testGetNextConsecutiveMembers() {
        System.out.println("===testGetNextConsecutiveMembers===");
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

    @Test
    public void testGetDate(){
        System.out.println("====Performing fieldExtraction on perfect string for a date.====");
        String dateInformation =
                fieldExtractor.getDate(imperfectOCRText.toLowerCase().split("\\s+"));
        assertNotNull(dateInformation);
        System.out.println("Date information from imperfectOCR is:  " + dateInformation);
        assertTrue(dateInformation.equals("01/03"));
    }
}
