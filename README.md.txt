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

## FCM
1. 앱 build.gradle에 라이브러리 추가
```
compile 'com.google.firebase:firebase-messaging:9.6.1'
```
[build.gradle][build.gradle]
[build.gradle]:https://github.com/KimBoWoon/Android-Firebase/blob/master/app/build.gradle
2. [FCMInstanceListenerService] 토큰을 얻어오는 서비스를 정의
[FCMInstanceListenerService][FCMInstanceListenerService]
[FCMInstanceListenerService]:https://github.com/KimBoWoon/Android-Firebase/blob/master/app/src/main/java/com/example/anull/firebase/fcm/FCMInstanceListenerService.java
3. 메시지를 수신받는 서비스를 정의
[FCMListenerService][FCMListenerService]
[FCMListenerService]:https://github.com/KimBoWoon/Android-Firebase/blob/master/app/src/main/java/com/example/anull/firebase/fcm/FCMListenerService.java
4. AndroidManifest.xml에 다음과 같은 permission과 service를 정의
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
[AndroidManifest][AndroidManifest]
[AndroidManifest]:https://github.com/KimBoWoon/Android-Firebase/blob/master/app/src/main/AndroidManifest.xml