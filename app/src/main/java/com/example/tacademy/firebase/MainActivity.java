package com.example.tacademy.firebase;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
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

public class MainActivity extends AppCompatActivity {
    // firebase 원격 구성
    // 별도의 서버구성 없이 매개변수 데이터가 제공이 되고, 이를 통해서 앱을 제어한다.
    FirebaseRemoteConfig config;
    ListView listView;
    ImageLoader imageLoader;
    DisplayImageOptions options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageLoaderConfiguration configuration
                = new ImageLoaderConfiguration.Builder(this).build();

        options =
            new DisplayImageOptions.Builder()
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .build();

        imageLoader = ImageLoader.getInstance();
        imageLoader.init(configuration);

        String token = FirebaseInstanceId.getInstance().getToken();
        if (token != null) {
            SharedStore.setString(this, "token", token);
            U.getInstance().m(token);
        }

        Log.i("FCM", "앱구동시 즉각적 : " + SharedStore.getString(this, Contant.RE.APP_DOMAIN));

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

        listView = (ListView) this.findViewById(R.id.listview);
        chatAdapter = new ChatAdapter();
        listView.setAdapter(chatAdapter);

        initRealDatabase();

        initStorage();
    }

    // 느리다
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

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    EditText editText;
    String chatChannel = "cmsCHS";
    // 실시간 데이터 베이스 초기화
    public void initRealDatabase() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        // 화면쪽 사항임 여기에 없어도 됨
        editText = (EditText) this.findViewById(R.id.editText);
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
                U.getInstance().m("아이디 " + chatDTO.getId());
                U.getInstance().m("메시지 " + chatDTO.getMsg());

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

    // 작성된 글을 실시간 데이터 베이스로 올린다.
    public void onChat(View view) {
        // 데이터 구조
        ChatDTO cdMsg = new ChatDTO("guest", editText.getText().toString());
        // 전송
        databaseReference.child(chatChannel).push().setValue(cdMsg);
        editText.setText("");
    }

    // 이미지 전송
    // 차후 카메라, 포토앨범과 연결하여 이미지 경로를 받아온다.
    // RxPaparazzo 오픈 소스
    public void onSendImg(View view) {
        imgFileUpload();
    }

    ChatAdapter chatAdapter;
    ArrayList<ChatDTO> chatDTOs = new ArrayList<ChatDTO>();
    class ViewHolder {
        TextView chatText, chatID;
        ImageView chatImg;
    }
    class ChatAdapter extends BaseAdapter {
        ViewHolder holder;

        @Override
        public int getCount() {
            return chatDTOs.size();
        }

        @Override
        public ChatDTO getItem(int position) {
            return chatDTOs.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView
                        = MainActivity.this.getLayoutInflater()
                        .inflate(R.layout.layout_cell, parent, false);

                holder = new ViewHolder();
                convertView.setTag(holder);

                holder.chatText = (TextView) convertView.findViewById(R.id.chatText);
                holder.chatID = (TextView) convertView.findViewById(R.id.chatID);
                holder.chatImg = (ImageView) convertView.findViewById(R.id.chatImg);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            // 세팅
            ChatDTO cd = getItem(position);
            if (cd.getMsg().indexOf("http") >= 0) {
                // 이미지 처리
                holder.chatImg.setVisibility(View.VISIBLE);
                holder.chatText.setVisibility(View.GONE);
                imageLoader.displayImage(cd.getMsg(), holder.chatImg, options);
            } else {
                holder.chatImg.setVisibility(View.GONE);
                holder.chatText.setVisibility(View.VISIBLE);
                holder.chatText.setText(cd.getMsg());
            }
            holder.chatID.setText(cd.getId());

            return convertView;
        }
    }

    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    // 저장소 초기화
    public void initStorage() {
        // 스토리지 생성
        firebaseStorage = FirebaseStorage.getInstance();
        // 스토리지 데이터를 가져올 root 까지 참조를 생성
        storageReference
                = firebaseStorage.getReferenceFromUrl("gs://fir-test-cd20c.appspot.com/res/");
        // 원하는 리소스까지 참조
        StorageReference pngRef
                = storageReference.child("google.png");

        U.getInstance().m(pngRef.getBucket());
        U.getInstance().m(pngRef.getName());
        U.getInstance().m(pngRef.getPath());

        // URL 회득
        pngRef.getDownloadUrl().addOnSuccessListener(this, new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                U.getInstance().m(uri.toString());
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
                U.getInstance().m("진행률 : " + progress);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downUri = taskSnapshot.getMetadata().getDownloadUrl();
                U.getInstance().m("실제경로 : " + downUri);

                ChatDTO chMsg = new ChatDTO("guest", downUri.toString());
                databaseReference.child(chatChannel).push().setValue(chMsg);
            }
        });
    }
}