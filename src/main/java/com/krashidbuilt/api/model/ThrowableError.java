package com.krashidbuilt.api.model;

import com.google.gson.Gson;

/**
 * Created by Ben Kauffman on 9/20/2016.
 */
public class ThrowableError extends Throwable {
    private Error error;

    public ThrowableError(Error error) {
        this.error = error;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

}
