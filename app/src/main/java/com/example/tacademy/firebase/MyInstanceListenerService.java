package com.example.tacademy.firebase;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * fcm 토큰값 획득
 */

public class MyInstanceListenerService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String token = FirebaseInstanceId.getInstance().getToken();
        SharedStore.setString(this, "token", token);
        U.getInstance().m(token);
    }
}
