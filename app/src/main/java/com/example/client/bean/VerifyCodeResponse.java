package com.example.client.bean;

import java.io.Serializable;

public class VerifyCodeResponse implements Serializable {


    /**
     * status : 0
     * message : 该手机号绑定用户不存在
     * stack :
     * result : false
     * code : 500
     * errorCode :
     * date : 2020-09-02 16:49:50
     */

    public int status;
    public String message;
    public String stack;
    public boolean result;
    public int code;
    public String errorCode;
    public String date;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStack() {
        return stack;
    }

    public void setStack(String stack) {
        this.stack = stack;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
