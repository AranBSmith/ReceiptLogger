package com.aransmith.app.receiptlogger.model;



import lombok.Data;

/**
 * Abstract which response classes must implement, contains methods for checking if the response
 * indicates a successful response, and a failed response. Upon initialisation the class will assume
 * it is a failed response. It is only successful upon calling setSuccess().
 */
@Data
public abstract class Response {

    private String response;

    /**
     * constructor that sets the response to that of failure
     */
    public Response(){
        setFail();
    }

    /**
     * set failure
     */
    public void setFail(){
        this.response = "fail";
    }

    /**
     * set success
     */
    public void setSuccess(){
        this.response = "success";
    }

    /**
     * used to check if the response is successful or not.
     * @return true if the response was successful, false if otherwise.
     */
    public boolean isSuccess(){
        try{
            return response.substring(0,7).equals("success");
        } catch (StringIndexOutOfBoundsException e){
            return false;
        }
    }

    /**
     * append information to the response message, such as exception messages and indicators as to
     * what the status of the web service is.
     * @param message
     */
    public void appendMessage(String message){
        this.response = this.response + " " + message;
    }

}