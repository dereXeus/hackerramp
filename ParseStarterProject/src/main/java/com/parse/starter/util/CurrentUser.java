package com.parse.starter.util;

import com.parse.starter.parse.User;

/**
 * Created by avagarwa on 7/25/2015.
 */
public class CurrentUser {
    private static CurrentUser mInstance = null;

    private User curr;

    private CurrentUser(){
        curr = new User();
    }

    public static CurrentUser getInstance(){
        if(mInstance == null) {
            mInstance = new CurrentUser();
        }
        return mInstance;
    }

    public User getUser(){
        return this.curr;
    }

    public void setUser(User value){
        curr = value;
    }

}
