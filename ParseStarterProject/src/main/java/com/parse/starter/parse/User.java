package com.parse.starter.parse;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;

import java.io.Serializable;

/**
 * Created by avagarwa on 7/25/2015.
 */
@ParseClassName("User")
public class User extends ParseObject implements Serializable{
    public String getName(){
        try {
            return fetchIfNeeded().getString("name");
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            return getString("name");
        }
    }
    public void setName(String value){
        put("name",value);
    }

    public String getPhone(){
        try {
            return fetchIfNeeded().getString("phone");
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            return getString("phone");
        }
    }
    public void setPhone(String value){
        put("phone",value);
    }

    @Override
    public boolean equals(Object b) {
        return this.getPhone().compareTo(((User)b).getPhone()) == 0;
    }
}
