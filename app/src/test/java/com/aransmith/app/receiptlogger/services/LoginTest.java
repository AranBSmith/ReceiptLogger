package com.aransmith.app.receiptlogger.services;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by Aran on 2/23/2016.
 */
public class LoginTest {

    Login login;
    String userName, password, invalidUserName, dcuUserName;

    @Before
    public void setUp() {
        login = new Login();
        userName = "admin@mail.com";
        dcuUserName = "aran.smith47@mail.dcu.ie";
        password = "apassword";
        invalidUserName = "admin";
    }

    @Test
    public void checkEmptyFalse(){
        assertFalse("The result is " + login.checkCredentials("", ""),
                login.checkCredentials("", ""));
    }

    @Test
    public void checkDCUUsername(){
        assertTrue(login.checkCredentials(dcuUserName,password));
    }

    @Test
    public void checkInvalidUserName(){
        assertFalse(login.checkCredentials(invalidUserName, password));
    }

    @Test
    public void checkResultNotNull() {
        assertNotNull(login.checkCredentials(userName, password));
    }
}
