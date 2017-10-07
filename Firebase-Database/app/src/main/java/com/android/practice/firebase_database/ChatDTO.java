package com.android.practice.firebase_database;

/**
 * Created by Null on 2017-10-07.
 */

public class ChatDTO {
    private String id;
    private String msg;

    public ChatDTO() {
    }

    public ChatDTO(String id, String msg) {
        this.id = id;
        this.msg = msg;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}