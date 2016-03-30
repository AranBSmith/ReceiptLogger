package com.aransmith.app.receiptlogger.dao;

import android.util.Log;

import com.aransmith.app.receiptlogger.model.Expense;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aran on 2/8/2016.
 */
public class WebServiceAccess {

    public String content, id;
    final String url = "http://ec2-52-18-58-195.eu-west-1.compute.amazonaws.com:8090" +
            "/ReceiptLoggerService/";

    public boolean login(String email, String password) {
        try {

            String target = url + "login/";

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(target);

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("email", email));
            nameValuePairs.add(new BasicNameValuePair("password", password));

            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = httpclient.execute(httppost);
            String content = EntityUtils.toString(response.getEntity());

            JSONObject jsonObj = new JSONObject(content);

            if(jsonObj.get("response").equals("valid")){
                return true;
            } else {
                return false;
            }

        } catch (Exception e) {
            Log.e("MainActivity", e.getMessage(), e);
        }
        return false;
    }

    public boolean submitExpense(Expense expense){

        return true;
    }
}
