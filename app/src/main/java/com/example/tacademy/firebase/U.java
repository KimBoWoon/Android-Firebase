package com.example.tacademy.firebase;

import android.util.Log;

/**
 * 유틸리티
 */
public class U {
    private static U ourInstance = new U();

    public static U getInstance() {
        return ourInstance;
    }

    private U() {
    }

    public void m(String msg) {
        Log.i("FCM", msg);
    }
}
