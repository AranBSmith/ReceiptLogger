package com.aransmith.app.receiptlogger.dao;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created by Aran on 2/20/2016.
 */

public class WebServiceAccessTest {
    private WebServiceAccess wsab;
    private static final String UNAME = "aran.smith47@mail.dcu.ie";
    private static final String PWORD = "apassword";
    boolean isRun;

    @Before
    public void setUp() throws Exception {
        wsab = new WebServiceAccess();
        isRun = false;

    }

    @Test
    public void vanillaTestLogin() {
        boolean result = wsab.login(UNAME, PWORD);
        assertTrue(result);
    }
}

   /* public void testLogin(){
        assertTrue(wsab.login(UNAME, PWORD));
        MyAsyncTask asyncTask = new MyAsyncTask();
        asyncTask.delegate = this;
        asyncTask.execute(credentials);
    }

    @Test
    public void processFinish(Boolean result) {
        if(!isRun){
            // start the request

        }
    }


    private class MyAsyncTask extends AsyncTask<HashMap<String,String>, Void, Boolean> {

        public AsyncBoolResponse delegate = null;

        @Override
        protected Boolean doInBackground(HashMap<String,String>... params) {
            HashMap<String,String> credentials = params[0];

            if(new Login().checkCredentials(credentials.get("email"),credentials.get("password")))
                return true;

            else return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            delegate.processFinish(result);
        }
    }
}*/
