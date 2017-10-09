package com.android.practice.firebase_remoteconfig;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "MAINACTIVITY";
    private FirebaseRemoteConfig config;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initFirebase();
    }

    private void initFirebase() {
        // 1. config 획득
        config = FirebaseRemoteConfig.getInstance();
        // 2. setting 획득
        FirebaseRemoteConfigSettings configSettings
                = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(true)
                .build();

        // 3. 설정
        config.setConfigSettings(configSettings);
        // 4. data 획득
        loadRemoteData();
    }

    public void loadRemoteData() {
        // 리모트로 가져오는 시간
        long cacheException = 3600;
        if (config.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            cacheException = 0;
        }

        // 원격 매개변수 요청
        config.fetch(cacheException).addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    config.activateFetched();

                    // 원격 매개변수 데이터 저장
                    // 늦게 저장이 되더라도 한번 저장되면 앱 구동시 즉각 반응 가능하다.
                    SharedStore.setString(getApplicationContext(),
                            Constant.APP_DOMAIN,
                            config.getString(Constant.APP_DOMAIN));
                    SharedStore.setInt(getApplicationContext(),
                            Constant.APP_CNT,
                            Integer.parseInt(config.getString(Constant.APP_CNT)));
                    SharedStore.setBoolean(getApplicationContext(),
                            Constant.APP_DEBUG,
                            Boolean.parseBoolean(config.getString(Constant.APP_DEBUG)));

                    Log.i(TAG, String.valueOf(config.getString("APP_DOMAIN")));
                    Log.i(TAG, String.valueOf(config.getLong("APP_CNT")));
                    Log.i(TAG, String.valueOf(config.getBoolean("APP_DEBUG")));
                }
            }
        });
    }
}
