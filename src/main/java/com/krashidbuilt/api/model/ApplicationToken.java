package com.krashidbuilt.api.model;

import com.google.gson.Gson;
import io.swagger.annotations.ApiModel;

import java.io.Serializable;

/**
 * Created by Ben Kauffman on 1/15/2017.
 */
@ApiModel(value = "ApplicationToken", discriminator = "type")
public class ApplicationToken implements Serializable {
    private String accessToken;
    private String refreshToken;
    private long accessExpires;
    private long refreshExpires;

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public long getAccessExpires() {
        return accessExpires;
    }

    public void setAccessExpires(long accessExpires) {
        this.accessExpires = accessExpires;
    }

    public long getRefreshExpires() {
        return refreshExpires;
    }

    public void setRefreshExpires(long refreshExpires) {
        this.refreshExpires = refreshExpires;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
