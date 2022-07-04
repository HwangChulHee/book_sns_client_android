package com.homework.book_sns.act_review;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.material.tabs.TabLayout;
import com.homework.book_sns.R;
import com.homework.book_sns.javaclass.Follow;
import com.homework.book_sns.javaclass.Follow_For_RCYV;
import com.homework.book_sns.javaclass.LoginSharedPref;
import com.homework.book_sns.javaclass.MyVolleyConnection;
import com.homework.book_sns.rcyv_adapter.Adt_af_follow_people;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class activity_recommendation extends AppCompatActivity {

    /* --------------------------- */
    // 액티비티 필수 객체들
    Context aContext;
    String TAG = "hch";
    /* --------------------------- */

    /* --------------------------- */
    // xml의 view 객체들

    Button btn_back;

    RecyclerView rcyv_recommendation_people;

    /* --------------------------- */

    /* --------------------------- */
    // 각종 객체들
    String review_board_id;
    Follow follow_info;
    Adt_af_follow_people adt_ar_recommendation_people;
    /* --------------------------- */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendation);

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
        review_board_id = intent.getStringExtra("review_board_id");
    }

    private void setView() {
        btn_back = findViewById(R.id.btn_ar_recommendation_back);

        rcyv_recommendation_people = findViewById(R.id.rcyv_ar_recommendation);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(aContext);
        rcyv_recommendation_people.setLayoutManager(linearLayoutManager);

        adt_ar_recommendation_people = new Adt_af_follow_people();
        rcyv_recommendation_people.setAdapter(adt_ar_recommendation_people);

        setViewData();
        setClickEvent();
    }


    private void setViewData() {
        request_recommendation_data();
    }

    private void request_recommendation_data() {
        MyVolleyConnection myVolleyConnection = new MyVolleyConnection(1, aContext);
        myVolleyConnection.setURL("/review/get_ar_rcyv_recommendation_info.php");
        myVolleyConnection.addParams("review_board_id", review_board_id);
        myVolleyConnection.addParams("client_id", LoginSharedPref.getUserId(aContext));
        myVolleyConnection.setVolley(new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                response_recommendation_data(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        myVolleyConnection.requestVolley();
    }

    private void response_recommendation_data(String response) {
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

                    adt_ar_recommendation_people.addItem(follow_for_rcyv);
                }
                adt_ar_recommendation_people.notifyDataSetChanged();
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
    }

}