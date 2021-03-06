package com.aransmith.app.receiptlogger.dao;

import android.util.Base64;
import android.util.Log;

import com.aransmith.app.receiptlogger.model.CancelExpenseResponse;
import com.aransmith.app.receiptlogger.model.Expense;
import com.aransmith.app.receiptlogger.model.ExpenseRetrievalResponse;
import com.aransmith.app.receiptlogger.model.ExpenseSubmissionResponse;
import com.aransmith.app.receiptlogger.services.CompressionUtils;
import com.google.gson.Gson;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aran on 2/8/2016.
 * This data access class interacts with the web service for the service layer.
 */
public class WebServiceAccess {

    private static final String TAG = "WebServiceAccess";
    private HttpClient httpClient;
    private HttpPost httpPost;
    private ExpenseSubmissionResponse expenseSubmissionResponse;
    JSONObject jsonObj;

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

            jsonObj = null;
            jsonObj = new JSONObject(content);

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

    /**
     * Retrieve all of a user's expenses by their email.
     * @param email
     * @param password
     * @return ExpenseRetrievalResponse containing the status of the user expense retrieval, as well
     * as Expenses if the retrieval was successful. This will not retrieve image information as the
     * process of retrieving all expense images can be slow, with the result of the user not even
     * viewing all the his/her expenses.
     */
    public ExpenseRetrievalResponse retrieveUserExpenses(String email, String password){
        try {
            String target = url + "userExpenseRetrieval/";

            httpClient = new DefaultHttpClient();
            httpPost = new HttpPost(target);

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("email", email));
            nameValuePairs.add(new BasicNameValuePair("password", password));

            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = httpClient.execute(httpPost);
            String content = EntityUtils.toString(response.getEntity());

            jsonObj = null;
            jsonObj = new JSONObject(content);

            ExpenseRetrievalResponse expenseRetrievalResponse = new Gson().fromJson(jsonObj.toString(), ExpenseRetrievalResponse.class);
            expenseRetrievalResponse.appendMessage(jsonObj.toString());
            return expenseRetrievalResponse;

        } catch (Exception e) {
            Log.e("WebServiceAccess", e.getMessage(), e);
        }
        return null;
    }

    /**
     * Retrieve a particular expense by its ID, all information about the expense will be returned
     * including image data. The user's credentials must be provided for verification.
     * @param email
     * @param password
     * @param id
     * @return ExpenseRetrivalResponse with image data that is compressed and in a base64 string
     */
    public ExpenseRetrievalResponse retreiveExpenseByID(String email, String password, int id){
        try {
            String target = url + "retrieveExpensesByID/";

            httpClient = new DefaultHttpClient();
            httpPost = new HttpPost(target);

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
            nameValuePairs.add(new BasicNameValuePair("email", email));
            nameValuePairs.add(new BasicNameValuePair("password", password));
            nameValuePairs.add(new BasicNameValuePair("recordID", Integer.toString(id)));

            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = httpClient.execute(httpPost);
            String content = EntityUtils.toString(response.getEntity());

            jsonObj = null;
            jsonObj = new JSONObject(content);

            ExpenseRetrievalResponse expenseRetrievalResponse = new Gson().fromJson(jsonObj.toString(), ExpenseRetrievalResponse.class);
            return expenseRetrievalResponse;

        } catch (Exception e) {
            Log.e("WebServiceAccess", e.getMessage(), e);
        }

        return new ExpenseRetrievalResponse();
    }


    /**
     * Submit a user's expense, requires a password for verification.
     * @param expense
     * @param password
     * @return ExpenseSubmissionResponse containing the status of the expense submission as well as
     * exceptions thrown during this process on the webservice.
     */
    public ExpenseSubmissionResponse submitExpense(Expense expense, String password){
        try{
            expenseSubmissionResponse = new ExpenseSubmissionResponse();
            String target = url + "submitExpense/";

            httpClient = new DefaultHttpClient();
            httpPost = new HttpPost(target);

            List<NameValuePair> nameValuePairs = preparedExpenseNameValuePair(expense, password);

            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            HttpResponse response = httpClient.execute(httpPost);

            String content = EntityUtils.toString(response.getEntity());

            jsonObj = null;
            jsonObj = new JSONObject(content);

            if(jsonObj!=null) {
                if ((Boolean) jsonObj.get("success")) {
                    expenseSubmissionResponse.setSuccess();
                    return expenseSubmissionResponse;
                } else {
                    expenseSubmissionResponse.setResponse(jsonObj.toString());
                    expenseSubmissionResponse.appendMessage("Length before compression, base 64, submission is: " + expense.getExpenseImageData().length);
                    return expenseSubmissionResponse;
                }
            } else {
                expenseSubmissionResponse.appendMessage("Json is null");
                return expenseSubmissionResponse;
            }
        }

        catch(ClientProtocolException e){
            expenseSubmissionResponse = new ExpenseSubmissionResponse();
            expenseSubmissionResponse.appendMessage(e.getMessage());
            return expenseSubmissionResponse;
        }

        catch (Exception e) {
            expenseSubmissionResponse = new ExpenseSubmissionResponse();
            expenseSubmissionResponse.appendMessage(e.getMessage());
            return expenseSubmissionResponse;
        }
    }

    // convert Expense into a format that can be submitted via http post through the httpclient class
    private List<NameValuePair> preparedExpenseNameValuePair(Expense expense, String password){
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(9);
        nameValuePairs.add(new BasicNameValuePair("email", expense.getEmail()));
        nameValuePairs.add(new BasicNameValuePair("password", password));
        nameValuePairs.add(new BasicNameValuePair("price", String.valueOf(expense.getPrice())));
        nameValuePairs.add(new BasicNameValuePair("currency", expense.getCurrency()));
        nameValuePairs.add(new BasicNameValuePair("card", expense.getCard()));
        nameValuePairs.add(new BasicNameValuePair("category", expense.getCategory()));
        nameValuePairs.add(new BasicNameValuePair("date", expense.getDate()));
        nameValuePairs.add(new BasicNameValuePair("description", expense.getDescription()));
        Log.i(TAG, "description on submission is: " + expense.getDescription());
        try {
            // convert to base64 encoding
            byte[] compressedImage = CompressionUtils.compress(expense.getExpenseImageData());
            String imgString = Base64.encodeToString(compressedImage , Base64.NO_WRAP);
            nameValuePairs.add(new BasicNameValuePair("expenseimage",imgString));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        nameValuePairs.add(new BasicNameValuePair("approved", (String.valueOf(expense.isApproved()))));

        return nameValuePairs;
    }

    /**
     * Used to cancel an expense by its identifier. email and password are required to verify the
     * request.
     * @param email
     * @param password
     * @param id
     * @return CancelExpenseResponse containing the status of the request as well as exceptions
     * thrown during the process.
     */
    public CancelExpenseResponse cancelExpense(String email, String password, int id) {
        try {
            String target = url + "cancelExpense/";

            httpClient = new DefaultHttpClient();
            httpPost = new HttpPost(target);

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
            nameValuePairs.add(new BasicNameValuePair("email", email));
            nameValuePairs.add(new BasicNameValuePair("password", password));
            nameValuePairs.add(new BasicNameValuePair("id",String.valueOf(id)));

            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = httpClient.execute(httpPost);
            String content = EntityUtils.toString(response.getEntity());

            jsonObj = null;
            jsonObj = new JSONObject(content);

            CancelExpenseResponse cancelExpenseResponse = new Gson()
                    .fromJson(jsonObj.toString(), CancelExpenseResponse.class);

            cancelExpenseResponse.appendMessage(jsonObj.toString());
            return cancelExpenseResponse;

        } catch (Exception e) {
            Log.e("WebServiceAccess", e.getMessage(), e);
        }

        return null;
    }
}
