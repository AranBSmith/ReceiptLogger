package com.aransmith.app.receiptlogger.services;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


/**
 * Created by Aran on 4/30/2016.
 */
public class DateServiceTest {
    private DateService dateService;

    @Before
    public void setup(){
        dateService = new DateService();
    }

    @Test
    public void testValidDate(){
        assertTrue(dateService.withinMonth("2016-04-30"));
    }

    @Test
    public void testInvalidDate(){
        assertFalse(dateService.withinMonth("2016-01-30"));
    }
}
