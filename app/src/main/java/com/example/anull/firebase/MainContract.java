package com.example.anull.firebase;

/**
 * Created by Null on 2016-11-21.
 */

public interface MainContract {
    interface View {
        void initView();
    }
    interface UserAction {
        void textSendButtonClicked();
        void imgSendButtonClicked();
    }
}
