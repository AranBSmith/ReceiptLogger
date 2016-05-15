package com.aransmith.app.receiptlogger.services;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Aran on 14/05/2016.
 */
public class PriceServiceTest {

    String invalidPrice, validPrice;
    PriceService priceService;

    @Before
    public void setup(){
        priceService = new PriceService();
        validPrice = "1.11";
        invalidPrice = ".00";
    }

    @Test
    public void testValidPriceInput(){
        assertTrue(priceService.checkFormat(validPrice));
    }

    @Test
    public void testInvalidPriceInput(){
        assertFalse(priceService.checkFormat(invalidPrice));
    }
}
