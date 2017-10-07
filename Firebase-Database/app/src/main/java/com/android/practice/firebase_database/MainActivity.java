package com.android.practice.firebase_database;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements MainContract.View {
    private MainPresenter mMainPresenter;
    private Button textSendBtn;
    private EditText editText;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private ChatAdapter chatAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        mMainPresenter = new MainPresenter(this);

        chatAdapter = new ChatAdapter(getApplicationContext(), DataManager.getInstance().getChatDTOs());

        textSendBtn = (Button) findViewById(R.id.text_send_btn);
        editText = (EditText) findViewById(R.id.editText);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(chatAdapter);
        chatAdapter.notifyDataSetChanged();
    }

    @OnClick(R.id.text_send_btn)
    public void textSendBtnClicked() {
        Log.i("MainActivity", "Button Clicked");
        // 작성된 글을 실시간 데이터 베이스로 올린다.
        // 데이터 구조
        ChatDTO cdMsg = new ChatDTO("guest", editText.getText().toString());
        // 전송
        mMainPresenter.sendDatabase(cdMsg);
        editText.setText("");
        chatAdapter.notifyDataSetChanged();
    }
}
