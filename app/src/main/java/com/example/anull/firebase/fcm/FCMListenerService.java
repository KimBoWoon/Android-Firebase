package com.example.anull.firebase.fcm;

import com.example.anull.firebase.data.FCMMsgModel;
import com.example.anull.firebase.data.Loging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

/**
 * Created by Null on 2016-11-22.
 */

public class FCMListenerService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Loging.i("===============FCM MSG===============");
        Loging.i("메시지 수신 : " + remoteMessage.getData().toString());
        String jsonStr = remoteMessage.getData().get("key2");
        FCMMsgModel msg = new Gson().fromJson(jsonStr, FCMMsgModel.class);
        Loging.i("나이 : " + msg.getAge());
        Loging.i("===============FCM MSG===============");
    }
}