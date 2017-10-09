# 혼자 편히 사용할려고 작성한 레파지토리인데 스타가 있는걸 보고 좀 더 잘 만들어야겠다는 생각이 들어서 천천히 조금이라도 업데이트합니다!!
# Android-Firebase

## Android 사용법
[Firebase](https://firebase.google.com/)
* Firebase 홈페이지에서 프로젝트를 생성한다.
* 시키는대로 세 단계만 거치면 됨

## Database
* 앱 build.gradle에 라이브러리 추가
[build.gradle](https://github.com/KimBoWoon/Android-Firebase/blob/master/app/build.gradle)
```
com.google.firebase:firebase-database:9.6.1
```
* 데이터베이스 초기화
[InitDatabase](https://github.com/KimBoWoon/Android-Firebase/blob/master/app/src/main/java/com/example/anull/firebase/main/MainPresenter.java#L170)
```
private FirebaseDatabase firebaseDatabase;
private DatabaseReference databaseReference;

// 실시간 데이터 베이스 초기화
public void initRealDatabase() {
    firebaseDatabase = FirebaseDatabase.getInstance();
    databaseReference = firebaseDatabase.getReference();
    // 데이터 베이스 이벤트 감지
    DatabaseReference evtReference = firebaseDatabase.getReference(chatChannel);
    // 메시지 컬럼을 기준으로 정렬하고 뒤에서부터 5개만 가져온다.
    // evtReference.orderByChild("msg").limitToFirst(5).addChildEventListener(new ChildEventListener() {
    evtReference.addChildEventListener(new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            // s -> key
            ChatDTO chatDTO = dataSnapshot.getValue(ChatDTO.class);
            // U.getInstance().m("자식이 추가 되었다 " + s);
            Loging.i(chatDTO.getId());
            Loging.i(chatDTO.getMsg());

            // 채팅 데이터 추가
            chatDTOs.add(chatDTO);
            // 화면 갱신
            chatAdapter.notifyDataSetChanged();
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    });
}
```
* 데이터베이스는 사용자에 대해 접근성을 제어 해야한다.
* [데이터베이스 어플리케이션](https://github.com/KimBoWoon/Android-Firebase/tree/master/Firebase-Database)

## Storege
* 앱 build.gradle에 라이브러리 추가
[build.gradle](https://github.com/KimBoWoon/Android-Firebase/blob/master/app/build.gradle)
```
com.google.firebase:firebase-storage:9.6.1
```
* 저장소 초기화
[InitSrorege](https://github.com/KimBoWoon/Android-Firebase/blob/master/app/src/main/java/com/example/anull/firebase/main/MainPresenter.java#L216)
```
private FirebaseStorage firebaseStorage;
private StorageReference storageReference;

// 저장소 초기화
public void initStorage() {
    // 스토리지 생성
    firebaseStorage = FirebaseStorage.getInstance();
    // 스토리지 데이터를 가져올 root 까지 참조를 생성
    storageReference
            = firebaseStorage.getReferenceFromUrl("gs://practice-e24ab.appspot.com/res/");
    // 원하는 리소스까지 참조
    StorageReference pngRef
            = storageReference.child("google.png");

    Loging.i(pngRef.getBucket());
    Loging.i(pngRef.getName());
    Loging.i(pngRef.getPath());

    // URL 회득
    pngRef.getDownloadUrl().addOnSuccessListener((MainActivity) view, new OnSuccessListener<Uri>() {
        @Override
        public void onSuccess(Uri uri) {
            Loging.i(uri.toString());
        }
    });

    //imgFileUpload();
}
```
* 스토리지는 사용자에 대해 접근성을 제어 해야한다.
* [저장소 어플리케이션](https://github.com/KimBoWoon/Android-Firebase/tree/master/Firebase-Storage)

## Crash
* 앱 build.gradle에 라이브러리 추가
[build.gradle](https://github.com/KimBoWoon/Android-Firebase/blob/master/app/build.gradle)
```
com.google.firebase:firebase-crash:9.6.1
```
* gradle에 추가하고 나면 별 다른 초기화 작업이 없다.
* 앱이 죽으면 알아서 Firebase 홈페이지에 기록이 된다.
* 어디서 어떻게 죽었는지, 배터리는 얼마나 있었고, 디바이스는 어떤거, 인터넷(LTE or WIFI)을 사용했는지 등등 여러기록들이 남음

## RemoteConfig
* 앱 build.gradle에 라이브러리 추가
[build.gradle](https://github.com/KimBoWoon/Android-Firebase/blob/master/app/build.gradle)
```
compile 'com.google.firebase:firebase-config:9.6.1'
```
* Firebase서버에서 데이터를 가져오기 위한 설정 작업
[Config](https://github.com/KimBoWoon/Android-Firebase/blob/master/app/src/main/java/com/example/anull/firebase/main/MainPresenter.java#L119)
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
* 데이터 획득
[LoadData](https://github.com/KimBoWoon/Android-Firebase/blob/master/app/src/main/java/com/example/anull/firebase/main/MainPresenter.java#L136)
```
public void loadRemoteData() {
    // 리모트로 가져오는 시간
    long cacheException = 3600;
    if (config.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
        cacheException = 0;
    }

    // 원격 매개변수 요청
    config.fetch(cacheException).addOnCompleteListener((MainActivity) view, new OnCompleteListener<Void>() {
        @Override
        public void onComplete(@NonNull Task task) {
            if (task.isSuccessful()) {
                config.activateFetched();

                // 원격 매개변수 데이터 저장
                // 늦게 저장이 되더라도 한번 저장되면 앱 구동시 즉각 반응 가능하다.
                SharedStore.setString((MainActivity) view,
                        Constant.APP_DOMAIN,
                        config.getString(Constant.APP_DOMAIN));
                SharedStore.setInt((MainActivity) view,
                        Constant.APP_CNT,
                        Integer.parseInt(config.getString(Constant.APP_CNT)));
                SharedStore.setBooolean((MainActivity) view,
                        Constant.APP_DEBUG,
                        Boolean.parseBoolean(config.getString(Constant.APP_DEBUG)));

                Loging.i(String.valueOf(config.getString("APP_DOMAIN")));
                Loging.i(String.valueOf(config.getLong("APP_CNT")));
                Loging.i(String.valueOf(config.getBoolean("APP_DEBUG")));
            }
        }
    });
}
```
* 네트워크 환경에 따라 데이터를 가져오는 시간이 빠를수도있고 느릴수도있다. 그래서 SharedPreferences를 사용해 디바이스에 저장
[SharedPreferences](https://github.com/KimBoWoon/Android-Firebase/blob/master/app/src/main/java/com/example/anull/firebase/data/SharedStore.java)
* [원격 구성 어플리케이션](https://github.com/KimBoWoon/Android-Firebase/tree/master/Firebase-RemoteConfig)

## FCM
* 앱 build.gradle에 라이브러리 추가
[build.gradle](https://github.com/KimBoWoon/Android-Firebase/blob/master/app/build.gradle)
```
compile 'com.google.firebase:firebase-messaging:9.6.1'
```
* 토큰을 얻어오는 서비스를 정의
[FCMInstanceListenerService](https://github.com/KimBoWoon/Android-Firebase/blob/master/app/src/main/java/com/example/anull/firebase/fcm/FCMInstanceListenerService.java)
* 메시지를 수신받는 서비스를 정의
[FCMListenerService](https://github.com/KimBoWoon/Android-Firebase/blob/master/app/src/main/java/com/example/anull/firebase/fcm/FCMListenerService.java)
* AndroidManifest.xml에 다음과 같은 permission과 service를 정의
[AndroidManifest](https://github.com/KimBoWoon/Android-Firebase/blob/master/app/src/main/AndroidManifest.xml)
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
