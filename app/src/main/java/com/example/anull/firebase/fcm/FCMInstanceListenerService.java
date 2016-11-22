package com.example.anull.firebase.fcm;

import com.example.anull.firebase.data.Loging;
import com.example.anull.firebase.data.SharedStore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Null on 2016-11-22.
 */

public class FCMInstanceListenerService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String token = FirebaseInstanceId.getInstance().getToken();
        SharedStore.setString(this, "token", token);
        Loging.i(token);
    }
}