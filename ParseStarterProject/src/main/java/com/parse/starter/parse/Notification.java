package com.parse.starter.parse;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.io.Serializable;

/**
 * Created by sugaddam on 7/26/2015.
 */
@ParseClassName("Notification")

public class Notification extends ParseObject implements Serializable {
    public  User getRequestUser(){ return (User)getParseObject("request_user");}
    public User getForwardToUser(){ return (User)getParseObject("forward_to");}
    public String getMerchantName(){ return getString("merchant_nm"); }
    public String getAmount(){ return getString("amount");}
    public String getStatus() { return getString("status");}
    public String getPaymentType(){
        return getString("pay_type");
    }
    public String getMerchantCard(){
        return getString("merchant_cd_no");
    }

    public void setAmount(String amount){
        put("amount",amount);
    }
    public void setStatus(String status){
        put("status",status);
    }
    public  void  setRequestUser(User requestUser){
        put("request_user",requestUser);
    }
    public void setForwardToUser(User forward_to){
        put("forward_to",forward_to);
    }
    public void setMerchantName(String merchantName){
        put("merchant_nm",merchantName);
    }
    public void setPaymentType(String merchantName){
        put("pay_type",merchantName);
    }
    public String setMerchantCard(){
        return getString("merchant_cd_no");
    }

}
