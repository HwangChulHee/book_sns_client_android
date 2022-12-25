package com.homework.book_sns.act_login_sign;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.material.tabs.TabLayout;
import com.homework.book_sns.R;
import com.homework.book_sns.javaclass.Follow;
import com.homework.book_sns.javaclass.Follow_For_RCYV;
import com.homework.book_sns.javaclass.MyVolleyConnection;
import com.homework.book_sns.rcyv_adapter.Adt_af_follow_people;
import com.homework.book_sns.rcyv_adapter.Adt_fr_review_simple;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class activity_follow extends AppCompatActivity {

    /* --------------------------- */
    // 액티비티 필수 객체들
    Context aContext;
    String TAG = "hch";
    /* --------------------------- */

    /* --------------------------- */
    // xml의 view 객체들

    Button btn_back;
    TextView tv_nickname;

    TabLayout tl_follow_type;
    TabLayout.Tab tb_follower;
    TabLayout.Tab tb_following;

    RecyclerView rcyv_follow_people;
    ProgressBar progressBar;
    NestedScrollView nestedScrollView;

    /* --------------------------- */

    /* --------------------------- */
    // 각종 객체들
    Follow follow_info;
    Adt_af_follow_people adt_af_follow_people;
    /* --------------------------- */

    // 1페이지에 10개씩 데이터를 불러온다
    int page = 1, limit = 7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow);


        myInit();
    }

    private void myInit() {
        aContext = this;
        ActionBar bar = getSupportActionBar();
        bar.hide();


        setObject();
        setView();
    }

    private void setObject() {
        myGetIntentData();
    }

    private void myGetIntentData() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        follow_info = bundle.getParcelable("follow_info");
    }

    private void setView() {
        btn_back = findViewById(R.id.btn_act_follow_back);
        tv_nickname = findViewById(R.id.tv_act_follow_nickname);

        tl_follow_type = findViewById(R.id.tl_af_follow_type);
        tb_follower = tl_follow_type.getTabAt(0);
        tb_following = tl_follow_type.getTabAt(1);

        rcyv_follow_people = findViewById(R.id.rcyv_af_follower_or_following);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(aContext);
        rcyv_follow_people.setLayoutManager(linearLayoutManager);

        adt_af_follow_people = new Adt_af_follow_people();
        rcyv_follow_people.setAdapter(adt_af_follow_people);


        progressBar = findViewById(R.id.pgb_af);
        nestedScrollView = findViewById(R.id.nscrv_af);

        setViewData();
        setClickEvent();
    }

    private void setViewData() {
        Log.d(TAG, "setViewData: "+follow_info.getUser_nickname());
        tv_nickname.setText(follow_info.getUser_nickname());

        if(follow_info.getFollow_type().equals("follower")) {
            tb_follower.select();
        } else if(follow_info.getFollow_type().equals("following")) {
            tb_following.select();
        }

        request_FollowData();
    }

    private void request_FollowData() {
        MyVolleyConnection myVolleyConnection = new MyVolleyConnection(1, aContext);
        myVolleyConnection.setURL("/login_sign/get_af_rcyv_follow_info.php");
        myVolleyConnection.addParams("object_person_id", follow_info.getObject_person_id());
        myVolleyConnection.addParams("client_id", follow_info.getClient_id());
        myVolleyConnection.addParams("follow_type", follow_info.getFollow_type());
        myVolleyConnection.addParams("page", String.valueOf(page));
        myVolleyConnection.addParams("limit", String.valueOf(limit));


        myVolleyConnection.setVolley(new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                response_FollowData(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        myVolleyConnection.requestVolley();
    }

    private void response_FollowData(String response) {
        Log.d(TAG, "response_FollowData: "+response);

        JSONObject entryJsonObject = null;
        try {
            entryJsonObject = new JSONObject(response);
            String success = entryJsonObject.getString("success");

            if(success.equals("false")) {
                String fail_reason = entryJsonObject.getString("reason");
                Toast.makeText(aContext, fail_reason, Toast.LENGTH_LONG).show();
            } else {
                JSONArray jsonArray = entryJsonObject.getJSONArray("data");

                Log.d(TAG, "response_FollowData: count"+jsonArray.length());

                for(int i = 0 ; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    Follow_For_RCYV follow_for_rcyv = new Follow_For_RCYV();

                    String user_id = jsonObject.getString("user_id");
                    String nickname = jsonObject.getString("nickname");
                    String profile_photo = jsonObject.getString("profile_photo");
                    String client_relationship = jsonObject.getString("client_relationship");

                    int review_count = Integer.parseInt(jsonObject.getString("review_count"));

                    follow_for_rcyv.setUser_id(user_id);
                    follow_for_rcyv.setUser_nickname(nickname);
                    follow_for_rcyv.setProfile_photo(profile_photo);
                    follow_for_rcyv.setReview_count(review_count);

                    if(client_relationship.equals("false")) {
                        follow_for_rcyv.setClient_relationship(false);
                    } else {
                        follow_for_rcyv.setClient_relationship(true);
                    }

                    adt_af_follow_people.addItem(follow_for_rcyv);
                }
                adt_af_follow_people.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

        } catch (JSONException e) {
            Log.d(TAG, "response_FollowData: "+e);
        }
    }


    private void setClickEvent() {
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        tl_follow_type.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                adt_af_follow_people.clearItem();
                adt_af_follow_people.notifyDataSetChanged();

                page = 1;
                if(tab.getPosition() == 0) {
                    follow_info.setFollow_type("follower");
                } else if(tab.getPosition() == 1) {
                    follow_info.setFollow_type("following");
                }
                request_FollowData();

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())
                {
                    Log.d(TAG, "스크롤... onScrollChange: ");
                    page++;
                    progressBar.setVisibility(View.VISIBLE);
                    request_FollowData();
                }

            }
        });



    }



}