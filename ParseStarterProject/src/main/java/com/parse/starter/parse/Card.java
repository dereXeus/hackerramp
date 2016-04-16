package com.parse.starter.parse;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.Date;

/**
 * Created by avagarwa on 7/30/2015.
 */
@ParseClassName("Card")
public class Card extends ParseObject{
    public User getUser(){
        return (User) getParseObject("user");
    }

    public void setUser(User value){
        put("user",value);
    }

    public String getCardNum(){
        return getString("card_num");
    }

    public void setCardNum(String value){
        put("card_num",value);
    }

    public String getExpiryDate() {
        return getString("exp_date");
    }

    public void setExpiryDate(String value) {
        put("exp_date",value);
    }

    public int getCVV(){
        return getInt("cvv");
    }

    public void setCVV(int value){
        put("cvv",value);
    }

}
