package com.android.practice.firebase_fcm;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by secret on 10/10/17.
 */

public class FCMInstanceListenerService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String token = FirebaseInstanceId.getInstance().getToken();
//        SharedStore.setString(this, "token", token);
        Log.i("FCMToken", token);
    }
}
