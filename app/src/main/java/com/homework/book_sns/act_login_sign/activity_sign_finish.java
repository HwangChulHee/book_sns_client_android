package com.homework.book_sns.act_login_sign;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.homework.book_sns.R;
import com.homework.book_sns.javaclass.LoginSharedPref;

public class activity_sign_finish extends AppCompatActivity {

    String TAG = "hch";

    String user_id;
    String nickname;
    String email;
    String profile_photo;
    String sign_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_finish);

        setView();
        setPref();
    }

    private void setPref() {
        Intent intent = getIntent();
        user_id = intent.getStringExtra("user_id");
        nickname = intent.getStringExtra("nickname");
        email = intent.getStringExtra("email");
        profile_photo = intent.getStringExtra("profile_photo");
        sign_type = intent.getStringExtra("sign_type");

        Log.d(TAG, "setPref: "+profile_photo);

        LoginSharedPref.
                setUserInfo(activity_sign_finish.this, user_id,
                        sign_type, nickname, profile_photo, email);
    }

    private void setView() {
        ActionBar bar = getSupportActionBar();
        bar.hide();



        findViewById(R.id.button7).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity_sign_finish.this, com.homework.book_sns.activity_home_main.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("user_id", user_id);
                startActivity(intent);
                finish();

            }
        });
    }
}