package com.example.taskapp.utils;


import okhttp3.Response;

/**
 * Created by ${hlink} on 18/1/16.
 */
public  interface OnTaskComplete<T> {
    public void onSuccess(Response response, boolean success);

    public void onFailure();


}
