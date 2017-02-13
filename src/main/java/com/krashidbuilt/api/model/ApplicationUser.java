package com.krashidbuilt.api.model;

import com.google.gson.Gson;
import io.swagger.annotations.ApiModel;

import java.io.Serializable;

/**
 * Created by Ben Kauffman on 1/15/2017.
 */

@ApiModel(value = "ApplicationUser", discriminator = "type")
public class ApplicationUser implements Serializable {

    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private String password;
    private String created;
    private String updated;

    private ApplicationToken applicationToken;


    public ApplicationUser() {
        applicationToken = new ApplicationToken();
    }

    public static ApplicationUser fromString(String json) {
        return new Gson().fromJson(json, ApplicationUser.class);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public ApplicationToken getApplicationToken() {
        return applicationToken;
    }

    public void setApplicationToken(ApplicationToken applicationToken) {
        this.applicationToken = applicationToken;
    }

    public boolean isValid() {
        return id >= 1;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
