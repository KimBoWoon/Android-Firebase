package com.example.tacademy.firebase;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

/**
 * 메시지 수신
 */

public class MyFCMListenerService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        U.getInstance().m("===============FCM MSG===============");
        U.getInstance().m("메시지 수신 : " + remoteMessage.getData().toString());
        String jsonStr = remoteMessage.getData().get("key2");
        FCMMsgModel msg = new Gson().fromJson(jsonStr, FCMMsgModel.class);
        U.getInstance().m("나이 : " + msg.getAge());
        U.getInstance().m("===============FCM MSG===============");
    }
}
