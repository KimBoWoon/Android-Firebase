# Android-Firebase

## Android 사용법
[Firebase Homepage][Firebase]
[Firebase]:https://firebase.google.com/
* FIrebase 홈페이지에서 프로젝트를 생성한다.
* 시키는대로 세 단계만 거치면 됨

## Database

## Storege

## Crash

## RemoteConfig
1. Firebase서버에서 데이터를 가져오기 위한 설정 작업
[Config][Config]
[Config]:https://github.com/KimBoWoon/Android-Firebase/blob/90a5d634d46fc1d83f63e82a79afebd4709a15ec/Firebase/app/src/main/java/com/example/tacademy/firebase/MainActivity.java#L75
```
FirebaseRemoteConfig config;

// 1. config 획득
config = FirebaseRemoteConfig.getInstance();
// 2. setting 획득
FirebaseRemoteConfigSettings configSettings
        = new FirebaseRemoteConfigSettings.Builder()
        .setDeveloperModeEnabled(true)
        .build();

// 3. 설정
config.setConfigSettings(configSettings);
```
2. 데이터 획득
[LoadData][LoadData]
[LoadData]:https://github.com/KimBoWoon/Android-Firebase/blob/90a5d634d46fc1d83f63e82a79afebd4709a15ec/Firebase/app/src/main/java/com/example/tacademy/firebase/MainActivity.java#L98
```
public void loadRemoteData() {
        // 리모트로 가져오는 시간
        long cacheException = 3600;
        if (config.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            cacheException = 0;
        }

        // 원격 매개변수 요청
        config.fetch(cacheException).addOnCompleteListener(this, new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    config.activateFetched();

                    // 원격 매개변수 데이터 저장
                    // 늦게 저장이 되더라도 한번 저장되면 앱 구동시 즉각 반응 가능하다.
                    SharedStore.setString(MainActivity.this,
                            Contant.RE.APP_DOMAIN,
                            config.getString(Contant.RE.APP_DOMAIN));
                    SharedStore.setInt(MainActivity.this,
                            Contant.RE.APP_CNT,
                            Integer.parseInt(config.getString(Contant.RE.APP_CNT)));
                    SharedStore.setBooolean(MainActivity.this,
                            Contant.RE.APP_DEBUG,
                            Boolean.parseBoolean(config.getString(Contant.RE.APP_DEBUG)));

                    Log.i("FCM", String.valueOf(config.getString("APP_DOMAIN")));
                    Log.i("FCM", String.valueOf(config.getLong("APP_CNT")));
                    Log.i("FCM", String.valueOf(config.getBoolean("APP_DEBUG")));
                }
            }
        });
    }
```
* 네트워크 환경에 따라 데이터를 가져오는 시간이 빠를수도있고 느릴수도있다. 그래서 SharedPreferences를 사용해 디바이스에 저장
[SharedPreferences][SharedPreferences]
[SharedPreferences]:https://github.com/KimBoWoon/Android-Firebase/blob/master/app/src/main/java/com/example/anull/firebase/data/SharedStore.java

## FCM
1. 앱 build.gradle에 라이브러리 추가
[build.gradle][build.gradle]
[build.gradle]:https://github.com/KimBoWoon/Android-Firebase/blob/master/app/build.gradle
```
compile 'com.google.firebase:firebase-messaging:9.6.1'
```
2. 토큰을 얻어오는 서비스를 정의
[FCMInstanceListenerService][FCMInstanceListenerService]
[FCMInstanceListenerService]:https://github.com/KimBoWoon/Android-Firebase/blob/master/app/src/main/java/com/example/anull/firebase/fcm/FCMInstanceListenerService.java
3. 메시지를 수신받는 서비스를 정의
[FCMListenerService][FCMListenerService]
[FCMListenerService]:https://github.com/KimBoWoon/Android-Firebase/blob/master/app/src/main/java/com/example/anull/firebase/fcm/FCMListenerService.java
4. AndroidManifest.xml에 다음과 같은 permission과 service를 정의
[AndroidManifest][AndroidManifest]
[AndroidManifest]:https://github.com/KimBoWoon/Android-Firebase/blob/master/app/src/main/AndroidManifest.xml
```
<uses-permission android:name="android.permission.INTERNET" />

<!-- fb messaging start -->
    <service android:name=".fcm.FCMListenerService">
        <intent-filter>
            <action android:name="com.google.firebase.MESSAGING_EVENT" />
        </intent-filter>
    </service>
    <service android:name=".fcm.FCMInstanceListenerService">
        <intent-filter>
            <action android:name="com.google.firebase.INSTANCE_ID_EVENT" />
        </intent-filter>
    </service>
<!-- fb messaging end -->
```
