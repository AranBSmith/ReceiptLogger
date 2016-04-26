package com.aransmith.app.receiptlogger.model;

/**
 * Created by Aran on 4/20/2016.
 */

import lombok.Data;

@Data
public abstract class Response {

    private String response;

    public Response(){
        setFail();
    }

    public void setFail(){
        this.response = "fail";
    }

    public void setSuccess(){
        this.response = "success";
    }

    public boolean isSuccess(){
        try{
            return response.substring(0,7).equals("success");
        } catch (StringIndexOutOfBoundsException e){
            return false;
        }
    }

    public void appendMessage(String message){
        this.response = this.response + " " + message;
    }

}