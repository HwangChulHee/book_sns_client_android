package com.homework.book_sns;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.homework.book_sns.act_chatting.service_chatting;

public class activity_home_main extends AppCompatActivity {

    String TAG = "hch";
    fragment_review fragment_review;
    fragment_mypage fragment_mypage;
    fragment_group fragment_group;

    String user_id;
    ActionBar bar;

    private Intent ServiceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_main);

        setView();
        setObject();

        ServiceIntent = new Intent(getApplicationContext(), service_chatting.class);
        startService(ServiceIntent); // startservice는 앱 시작시 해줘야한다..
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        new Thread(new Runnable() {
            @Override
            public void run() {
                service_chatting.senWriter.println("disconnect"); // 종료 사인
                service_chatting.senWriter.flush();
                stopService(ServiceIntent);

            }
        }).start();
    }

    private void setView() {

        fragment_review = new fragment_review();
        fragment_mypage = new fragment_mypage();
        fragment_group = new fragment_group();


        getSupportFragmentManager().beginTransaction().replace(R.id.frame_home, fragment_review).commit();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.tab1:
//                        Toast.makeText(getApplicationContext(), "리뷰", Toast.LENGTH_LONG).show();

                        bar.setTitle("도서 리뷰, 모임");
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_home, fragment_review).commit();
                        return true;
                    case R.id.tab2:
                        bar.setTitle("독서 모임");
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_home, fragment_group).commit();
//                        Toast.makeText(getApplicationContext(), "모임", Toast.LENGTH_LONG).show();
                        return true;
                    case R.id.tab3:
                        bar.setTitle("리뷰 작성");
                        Intent intent = new Intent(activity_home_main.this, com.homework.book_sns.act_review.activity_review_create_001.class);
                        intent.putExtra("update", "false");
                        startActivity(intent);
//                        Toast.makeText(getApplicationContext(), "글 작성", Toast.LENGTH_LONG).show();
                        return true;
                    case R.id.tab4:
                        bar.setTitle("알림");
//                        Toast.makeText(getApplicationContext(), "알림", Toast.LENGTH_LONG).show();
                        return true;
                    case R.id.tab5:
//                        Toast.makeText(getApplicationContext(), "내 글/설정", Toast.LENGTH_LONG).show();
                        bar.setTitle("마이페이지");
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_home, fragment_mypage).commit();
                        return true;
                }
                return false;
            }
        });


    }

    private void setObject() {
//        Intent intent = getIntent();
//        user_id = intent.getStringExtra("user_id");
        bar = this.getSupportActionBar();

    }


}