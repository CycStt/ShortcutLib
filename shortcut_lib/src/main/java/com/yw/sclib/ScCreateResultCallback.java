package com.yw.sclib;

/**
 * Created by wengyiming on 2016/3/14.
 */
public interface ScCreateResultCallback {
    void createSuccessed(String createdOrUpdate,Object tag);
    void createError(String errorMsg,Object tag);
}
