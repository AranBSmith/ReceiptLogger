package com.aransmith.app.receiptlogger.dao;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by Aran on 2/20/2016.
 */

public class WebServiceAccessTest {

    private WebServiceAccess wsab;
    private static final String UNAME = "admin";
    private static final String PWORD = "admin";

    @Before
    public void setUp() throws Exception {
        wsab = new WebServiceAccess();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testLogin(){
        wsab.login(UNAME, PWORD);
    }
}
