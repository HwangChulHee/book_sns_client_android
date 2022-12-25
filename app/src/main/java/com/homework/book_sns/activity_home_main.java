package com.homework.book_sns;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.gson.Gson;
import com.homework.book_sns.act_chatting.service_chatting;
import com.homework.book_sns.javaclass.LoginSharedPref;
import com.homework.book_sns.javaclass.Noti_info;
import com.homework.book_sns.javaclass.Noti_msg;
import com.homework.book_sns.javaclass.User_info;

public class activity_home_main extends AppCompatActivity {

    public static Activity ACTIVITY_HOME_MAIN;

    private String TAG = "hch";
    private static String ACTIVITY_NAME = "activity_home_main";
    private static String ACTIVITY_FUNCTION = "login";


    private void log_activity(String msg) {
            Log.d(TAG, "액티비티 이름: "+ACTIVITY_NAME +", 액티비티 기능 : "+ACTIVITY_FUNCTION
            +", 로그 내용 : "+msg);
    }


    fragment_review fragment_review;
    fragment_mypage fragment_mypage;
    fragment_group fragment_group;
    fragment_noti fragment_noti;

    String user_id;
    ActionBar bar;

    private Intent ServiceIntent;
    private Intent notiServiceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_main);

        setView();
        setObject();

//        ServiceIntent = new Intent(getApplicationContext(), service_chatting.class);
//        startService(ServiceIntent); // startservice는 앱 시작시 해줘야한다..

        notiServiceIntent = new Intent(getApplicationContext(), service_noti.class);
        if(service_noti.notiSocket == null) {
            Log.d(TAG, "home_main 액티비티 onCreate: , service_noti 실행");
            startService(notiServiceIntent);
        } else {
            Log.d(TAG, "home_main 액티비티 onCreate: , service_noti 실행 안됨");
            startService(notiServiceIntent);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ACTIVITY_HOME_MAIN = null;

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                service_chatting.senWriter.println("disconnect"); // 종료 사인
//                service_chatting.senWriter.flush();
//                stopService(ServiceIntent);
//
//            }
//        }).start();

    }

    private void setView() {

        fragment_review = new fragment_review();
        fragment_mypage = new fragment_mypage();
        fragment_group = new fragment_group();
        fragment_noti = new fragment_noti();



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
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_home, fragment_noti).commit();
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
        bar = this.getSupportActionBar();

        ACTIVITY_HOME_MAIN = this;

        Intent intent = getIntent();
        String intentType = null;
        intentType = intent.getStringExtra("type");

        log_activity("intent의 type에 대한 정보... : "+intentType);

        if(intentType.equals("noti")) {
            
            String noti_type = intent.getStringExtra("noti_type");
            Intent goIntent = null;

            if(noti_type.equals("추천")){

                String review_board_id = intent.getStringExtra("review_board_id");
                goIntent = new Intent(this,
                        com.homework.book_sns.act_review.activity_review_read_detail.class);
                goIntent.putExtra("review_board_id", review_board_id);
                log_activity("review_board_id : "+review_board_id);

            } else if(noti_type.equals("팔로우")){

                Bundle bundle = intent.getExtras();
                User_info user_info = bundle.getParcelable("user_info");

                goIntent = new Intent(this,
                        com.homework.book_sns.activity_member_page.class);
                goIntent.putExtra("user_info", user_info);
                        
            } else if(noti_type.equals("댓글")){

            } else if(noti_type.equals("답글")){

            } else {

            }

            Bundle notiBundle = intent.getExtras();
            Noti_info noti_info = notiBundle.getParcelable("noti_info");
            goIntent.putExtra("noti_info", noti_info);

            goIntent.putExtra("type", "noti");
            startActivity(goIntent);
        }


    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if(com.homework.book_sns.fragment_noti.fragment_noti != null) {
            com.homework.book_sns.fragment_noti tf = (com.homework.book_sns.fragment_noti)
                    getSupportFragmentManager().findFragmentById(R.id.frame_home);

            String noti_info = intent.getStringExtra("noti_info");
            tf.addNoti(noti_info);
        }

    }
}