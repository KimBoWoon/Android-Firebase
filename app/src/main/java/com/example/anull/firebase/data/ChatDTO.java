package com.example.anull.firebase.data;

/**
 * Created by Null on 2016-11-21.
 */

public class ChatDTO {
    String id;
    String msg;

    public ChatDTO() {}

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