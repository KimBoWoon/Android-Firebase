package com.android.practice.firebase_fcm;

import com.google.gson.annotations.SerializedName;

/**
 * Created by secret on 10/10/17.
 */

public class FCMModel {
    @SerializedName("name")
    private String name;
    @SerializedName("message")
    private String message;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
