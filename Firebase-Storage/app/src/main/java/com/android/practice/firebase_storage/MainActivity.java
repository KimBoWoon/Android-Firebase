package com.android.practice.firebase_storage;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements MainContract.View {
    private MainPresenter mMainPresenter;

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
    }

    @OnClick(R.id.img_send_btn)
    public void imageSendBtnClicked() {
        mMainPresenter.imageUpload();
    }

    @OnClick(R.id.img_download_btn)
    public void imageDownloadClicked() {
        mMainPresenter.imageDownload();
    }
}
