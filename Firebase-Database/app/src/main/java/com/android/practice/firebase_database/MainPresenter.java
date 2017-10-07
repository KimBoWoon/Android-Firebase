package com.android.practice.firebase_database;

import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Created by Null on 2017-10-07.
 */

public class MainPresenter implements MainContract.UserAction {
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private String chatChannel = "cmsCHS";
    private MainContract.View view;
    private ArrayList<ChatDTO> chatDTOs;

    public MainPresenter(MainContract.View view) {
        this.view = view;
        this.chatDTOs = DataManager.getInstance().getChatDTOs();

        initRealDatabase();
    }

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
                Log.i("MainActivity", chatDTO.getId());
                Log.i("MainActivity", chatDTO.getMsg());

                // 채팅 데이터 추가
                chatDTOs.add(chatDTO);
                // 화면 갱신
//                chatAdapter.notifyDataSetChanged();
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

    public void sendDatabase(ChatDTO cdMsg) {
        databaseReference.child(chatChannel).push().setValue(cdMsg);
    }
}
