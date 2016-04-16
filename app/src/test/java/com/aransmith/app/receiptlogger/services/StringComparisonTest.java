package com.aransmith.app.receiptlogger.services;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created by Aran on 3/15/2016.
 */
public class StringComparisonTest {
    StringComparison stringComparison;

    String aid, merchantid, cardNum, total, date;
    String testaid, testmerchantid, testcardNum, testtotal, testdate;

    @Before
    public void setup(){
        stringComparison = new StringComparison();
        aid = "AID"; merchantid = "MerchantID";
        cardNum = "VISADEBIT"; total = "TOTAL";
        date = "Date";

        testaid = "A1D"; testmerchantid = "MercHontID00090900909090";
        testcardNum = "V1SA03B1T"; testtotal = "T0TAS";
        testdate = "";
    }

    @Test
    public void testStringCompare(){
        assertTrue(stringComparison.stringCompare(testaid,aid) == -1);
        assertTrue(stringComparison.stringCompare(testdate, date) == -1);
        assertTrue(stringComparison.stringCompare(testtotal, total) == -1);

        assertTrue(stringComparison.stringCompare(testmerchantid, merchantid) == -1);
        assertTrue(stringComparison.stringCompare(testcardNum, cardNum) == -1);
    }
}
