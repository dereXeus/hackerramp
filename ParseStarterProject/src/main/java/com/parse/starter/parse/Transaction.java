package com.parse.starter.parse;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.Date;

/**
 * Created by avagarwa on 7/25/2015.
 */
@ParseClassName("Transaction")
public class Transaction extends ParseObject{

    public int getAmount() {
        return getInt("amount");
    }
    public void setAmount(int value) {
        put("amount", value);
    }

    public String getDescription(){
        return getString("description");
    }
    public void setDescription(String value) {
        put("description",value);
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

    public void add()
    {
        try {
            this.save();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        ParseQuery<Account> query = ParseQuery.getQuery("Account");
        query.whereEqualTo("From", this.getFrom());
        query.whereEqualTo("To", this.getTo());

        ParseQuery<Account> query2 = ParseQuery.getQuery("Account");
        query2.whereEqualTo("From", this.getTo());
        query2.whereEqualTo("To", this.getFrom());
        Account account = null;
        try {
            if (query.find().size() == 0 && query2.find().size() == 0) {
                account = new Account();
                account.setFrom(this.getFrom());
                account.setTo(this.getTo());
                account.setAmount(this.getAmount());
                account.save();
            } else if (query.find().size() != 0) {
                account = query.find().get(0);
                account.increment("amount", this.getAmount());
                account.save();
            } else {
                account = query2.find().get(0);
                account.increment("amount", -1*this.getAmount());
                account.save();
            }
        }
        catch (ParseException e){
            e.printStackTrace();
        }

    }

}
