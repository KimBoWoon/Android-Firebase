package com.example.anull.firebase;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;

import com.example.anull.firebase.adapter.ChatAdapter;
import com.example.anull.firebase.data.ChatDTO;
import com.example.anull.firebase.data.Constant;
import com.example.anull.firebase.data.Loging;
import com.example.anull.firebase.data.SharedStore;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Null on 2016-11-21.
 */

public class MainPresenter implements MainContract.UserAction {
    private MainContract.View view;
    private MainModel mainModel;
    private FirebaseRemoteConfig config;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private String chatChannel = "cmsCHS";
    private ArrayList<ChatDTO> chatDTOs;
    private ChatAdapter chatAdapter;
    public static boolean DEBUG = true;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    public MainPresenter(MainContract.View view) {
        this.mainModel = new MainModel();
        this.view = view;
        this.chatDTOs = new ArrayList<ChatDTO>();

        chatAdapter = new ChatAdapter((MainActivity) view, chatDTOs, imageLoader, options);

        initImageLoader();
        initFirebase();
        initStorage();
    }

    @Override
    public void textSendButtonClicked() {

    }

    @Override
    public void imgSendButtonClicked() {

    }

    public boolean isDebuggable(Context context) {
        boolean debuggable = false;
        PackageManager pm = context.getPackageManager();

        try {
            ApplicationInfo appinfo = pm.getApplicationInfo(context.getPackageName(), 0);
            debuggable = (0 != (appinfo.flags & ApplicationInfo.FLAG_DEBUGGABLE));
        } catch (PackageManager.NameNotFoundException e) {
            /* debuggable variable will remain false */
            e.printStackTrace();
        }

        return debuggable;
    }

    private void initImageLoader() {
        ImageLoaderConfiguration configuration
                = new ImageLoaderConfiguration.Builder((MainActivity) view).build();

        options =
                new DisplayImageOptions.Builder()
                        .cacheInMemory(true)
                        .cacheOnDisk(true)
                        .bitmapConfig(Bitmap.Config.RGB_565)
                        .build();

        imageLoader = ImageLoader.getInstance();
        imageLoader.init(configuration);
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

        initRealDatabase();
    }

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

    // 파일 업로드
    public void imgFileUpload() {
        // sd카드에 이미지가 존재하는 전재 (3.jpg)
        // 리소스까지 경로 확인
        Uri uriFile
                = Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/3.jpg"));
        // 메타 정보 구성
        StorageMetadata meta = new StorageMetadata.Builder()
                .setContentType("image/jpeg")
                .build();
        // 업로드
        UploadTask uploadTask = storageReference
                .child(uriFile.getLastPathSegment()).putFile(uriFile, meta);
        // 업로드 상황 이벤트 등록
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress
                        = (100.0 * taskSnapshot.getBytesTransferred())
                        / taskSnapshot.getTotalByteCount();
                Loging.i("진행률 : " + progress);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downUri = taskSnapshot.getMetadata().getDownloadUrl();
                Loging.i("실제경로 : " + downUri);

                ChatDTO chMsg = new ChatDTO("guest", downUri.toString());
                databaseReference.child(chatChannel).push().setValue(chMsg);
            }
        });
    }

    public ChatAdapter getChatAdapter() {
        return chatAdapter;
    }

    public DatabaseReference getDatabaseReference() {
        return databaseReference;
    }
}
