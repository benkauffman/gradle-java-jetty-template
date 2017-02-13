package com.krashidbuilt.api.model;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ben Kauffman on 1/15/2017.
 */
public class Detail implements Serializable {
    private String devMessage;
    private String userMessage;
    private String field;
    private String value;

    public Detail() {

    }

    public Detail(String message, String field, String value) {
        this.devMessage = message;
        this.userMessage = message;
        this.field = field;
        this.value = value;
    }

    public Detail(String devMessage, String userMessage, String field, String value) {
        this.devMessage = devMessage;
        this.userMessage = userMessage;
        this.field = field;
        this.value = value;
    }

    public List<Detail> toList() {
        List<Detail> details = new ArrayList<Detail>();
        details.add(this);
        return details;
    }

    public String getDevMessage() {
        return devMessage;
    }

    public void setDevMessage(String devMessage) {
        this.devMessage = devMessage;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getUserMessage() {
        return userMessage;
    }

    public void setUserMessage(String userMessage) {
        this.userMessage = userMessage;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
