package com.example.anull.firebase;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.anull.firebase.data.ChatDTO;

public class MainActivity extends AppCompatActivity implements MainContract.View {
    private Button textSendBtn, imgSendBtn;
    private EditText editText;
    private ListView listView;
    private MainPresenter mainPresenter;
    private String chatChannel = "cmsCHS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    @Override
    public void initView() {
        mainPresenter = new MainPresenter(this);

        textSendBtn = (Button) findViewById(R.id.text_send_btn);
        imgSendBtn = (Button) findViewById(R.id.img_send_btn);
        editText = (EditText) findViewById(R.id.editText);
        listView = (ListView) findViewById(R.id.listview);

        listView.setAdapter(mainPresenter.getChatAdapter());

        textSendBtn.setOnClickListener(listener);
        imgSendBtn.setOnClickListener(listener);
    }

    Button.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.text_send_btn:
                    // 작성된 글을 실시간 데이터 베이스로 올린다.
                    // 데이터 구조
                    ChatDTO cdMsg = new ChatDTO("guest", editText.getText().toString());
                    // 전송
                    mainPresenter.getDatabaseReference().child(chatChannel).push().setValue(cdMsg);
                    editText.setText("");
                    break;
                case R.id.img_send_btn:
                    mainPresenter.imgFileUpload();
                    break;
                default:
                    break;
            }
        }
    };
}
