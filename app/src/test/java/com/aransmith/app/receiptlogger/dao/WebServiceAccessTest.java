package com.aransmith.app.receiptlogger.dao;

import org.junit.Before;
import org.junit.Test;

/**
 * Created by Aran on 2/20/2016.
 */

public class WebServiceAccessTest {

    private WebServiceAccess wsab;
    private static final String UNAME = "admin";
    private static final String PWORD = "admin";
    Thread thread;

    @Before
    public void setUp() throws Exception {
        wsab = new WebServiceAccess();
    }

    @Test
    public void testLogin(){
        wsab.login(UNAME, PWORD);
    }

}
