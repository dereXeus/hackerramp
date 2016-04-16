package com.parse.starter.parse;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by avagarwa on 7/25/2015.
 */
@ParseClassName("Account")
public class Account extends ParseObject{
    public int getAmount() {
        return getInt("amount");
    }
    public void setAmount(int value) {
        put("amount", value);
    }

    public User getTo() {
        return (User)getParseObject("To");
    }
    public void setTo(User value){
        put("To",value);
    }

    public User getFrom() {
        return (User)getParseObject("From");
    }
    public void setFrom(User value){
        put("From",value);
    }
}
