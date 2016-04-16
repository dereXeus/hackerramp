package com.parse.starter.services;

/**
 * Created by 11162 on 16/07/15.
 */
public interface ResponseListener<RESULT> {
    void onRequestFailure(Exception e);
    void onRequestSuccess(RESULT result);
}
