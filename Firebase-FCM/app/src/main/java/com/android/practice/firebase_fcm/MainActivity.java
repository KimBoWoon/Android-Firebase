package com.android.practice.firebase_fcm;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getToken();
    }

    private void getToken() {
        String token = FirebaseInstanceId.getInstance().getToken();
        if (token != null) {
            Log.i("mainActivityToken", token);
        }
    }
}
