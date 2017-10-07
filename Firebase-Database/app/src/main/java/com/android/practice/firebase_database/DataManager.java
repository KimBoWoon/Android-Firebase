package com.android.practice.firebase_database;

import java.util.ArrayList;

/**
 * Created by Null on 2017-10-07.
 */

public class DataManager {
    private static final DataManager ourInstance = new DataManager();
    private ArrayList<ChatDTO> chatDTOs = new ArrayList<ChatDTO>();

    public static DataManager getInstance() {
        return ourInstance;
    }

    private DataManager() {
    }

    public ArrayList<ChatDTO> getChatDTOs() {
        return chatDTOs;
    }

    public void setChatDTOs(ArrayList<ChatDTO> chatDTOs) {
        this.chatDTOs = chatDTOs;
    }
}
