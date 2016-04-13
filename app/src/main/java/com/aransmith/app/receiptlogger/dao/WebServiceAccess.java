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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aran on 2/8/2016.
 */
public class WebServiceAccess {

    private HttpClient httpClient;
    private HttpPost httpPost;

    public String content, id;
    final String url = "http://ec2-52-18-58-195.eu-west-1.compute.amazonaws.com:8090" +
            "/ReceiptLoggerService/";

    public boolean login(String email, String password) {
        try {
            String target = url + "login/";

            httpClient = new DefaultHttpClient();
            httpPost = new HttpPost(target);

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("email", email));
            nameValuePairs.add(new BasicNameValuePair("password", password));

            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = httpClient.execute(httpPost);
            String content = EntityUtils.toString(response.getEntity());

            JSONObject jsonObj = new JSONObject(content);

            if(jsonObj.get("response").equals("success")){
                return true;
            } else {
                return false;
            }

        } catch (Exception e) {
            Log.e("WebServiceAccess", e.getMessage(), e);
        }
        return false;
    }

    public boolean submitExpense(Expense expense){
        try{
            String target = url + "submitExpense/";
            httpClient = new DefaultHttpClient();
            httpPost = new HttpPost(target);

            List<NameValuePair> nameValuePairs = preparedExpenseNameValuePair(expense);

            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = httpClient.execute(httpPost);
            String content = EntityUtils.toString(response.getEntity());

            JSONObject jsonObj = new JSONObject(content);

            if(jsonObj.get("response").equals("success")){
                return true;
            } else {
                return false;
            }

        } catch (Exception e) {
            Log.e("WebServiceAccess", "tried submitting: " + expense.getCategory() + " " +
                    expense.getEmail() + " " + expense.getCurrency() + " " + expense.getDate() + " "
                    +expense.getDescription() + " " + expense.getExpenseImageData() + " "
                    + e.getMessage(), e);
        }
        return false;
    }

    // convert Expense into a format that can be submitted via http post through the httpclient class
    private List<NameValuePair> preparedExpenseNameValuePair(Expense expense){
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(8);
        nameValuePairs.add(new BasicNameValuePair("email", expense.getEmail()));
        nameValuePairs.add(new BasicNameValuePair("price", String.valueOf(expense.getPrice())));
        nameValuePairs.add(new BasicNameValuePair("currency", expense.getCurrency()));
        nameValuePairs.add(new BasicNameValuePair("category", expense.getCategory()));
        nameValuePairs.add(new BasicNameValuePair("date", expense.getDate()));
        nameValuePairs.add(new BasicNameValuePair("description", expense.getDescription()));
        try {
            nameValuePairs.add(new BasicNameValuePair("expenseimage", new String(expense.getExpenseImageData(), "UTF-8")));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        nameValuePairs.add(new BasicNameValuePair("approved", (String.valueOf(expense.isApproved()))));

        return nameValuePairs;
    }


    /*public ArrayList<NameObjectPair> getObjectNameValuePairs(Object obj) throws IllegalArgumentException, IllegalAccessException {
        ArrayList<NameObjectPair> list = new ArrayList<>();
        for (Field field : obj.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            NameObjectPair nameValuePair = new NameObjectPair();
            nameValuePair.setName(field.getName());
            nameValuePair.setValue(field.get(obj));
            list.add(nameValuePair);
        }
        return list;
    }*/

}
