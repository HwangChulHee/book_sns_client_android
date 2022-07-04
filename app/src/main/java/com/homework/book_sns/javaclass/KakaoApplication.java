package com.homework.book_sns.javaclass;

import android.app.Application;

import com.kakao.sdk.common.KakaoSdk;

public class KakaoApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        KakaoSdk.init(this, "4255aaf23ae8cf4320a77cf25d846f27");


    }
}
