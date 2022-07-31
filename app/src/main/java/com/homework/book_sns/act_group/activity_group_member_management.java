package com.homework.book_sns.act_group;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.material.tabs.TabLayout;
import com.homework.book_sns.R;
import com.homework.book_sns.javaclass.Group_info;
import com.homework.book_sns.javaclass.LoginSharedPref;
import com.homework.book_sns.javaclass.Member_info;
import com.homework.book_sns.javaclass.MyBasicFunc;
import com.homework.book_sns.javaclass.MyVolleyConnection;
import com.homework.book_sns.rcyv_adapter.Adt_af_follow_people;
import com.homework.book_sns.rcyv_adapter.Adt_agmm_member_list;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;

public class activity_group_member_management extends AppCompatActivity {

    /* --------------------------- */
    // 액티비티 필수 객체들
    Context aContext;
    String TAG = "hch";
    /* --------------------------- */


    /* --------------------------- */
    // xml의 view 객체들
    Button btn_back;
    TabLayout tl_member_state;

    RecyclerView rcyv_member_list;

    /* --------------------------- */


    /* --------------------------- */
    // 각종 객체들
    Adt_agmm_member_list adt_agmm_member_list;

    int group_id;
    String tab_type = "member";
    /* --------------------------- */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_member_management);

        myInit();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void myInit() {
        aContext = this;
        ActionBar bar = getSupportActionBar();
        bar.hide();

        myGetIntent();
        myInitView();
    }

    private void myGetIntent() {
        Intent intent = getIntent();
        group_id = intent.getIntExtra("group_id", 0);
    }

    private void myInitView() {
        myFindView();
        mySetClickEvent();
        myLoadView(tab_type);
    }


    private void myFindView() {
        btn_back = findViewById(R.id.btn_agmm_back);
        tl_member_state = findViewById(R.id.tl_agmm_member_state);
        rcyv_member_list = findViewById(R.id.rcyv_agmm_member_list);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(aContext);
        rcyv_member_list.setLayoutManager(linearLayoutManager);

        adt_agmm_member_list = new Adt_agmm_member_list();
        rcyv_member_list.setAdapter(adt_agmm_member_list);
    }

    private void mySetClickEvent() {
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        tl_member_state.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition() == 0) {
                    tab_type = "member";
                    myLoadView(tab_type);

                } else if(tab.getPosition() == 1) {
                    tab_type = "apply";
                    myLoadView(tab_type);
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void myLoadView(String tab_type) {
        adt_agmm_member_list.clearItem();
        adt_agmm_member_list.notifyDataSetChanged();
        MyVolleyConnection myVolleyConnection = new MyVolleyConnection(1, aContext);
        myVolleyConnection.setURL("/group/group_member_load.php");
        myVolleyConnection.addParams("client_id", LoginSharedPref.getUserId(aContext));
        myVolleyConnection.addParams("group_id", Integer.toString(group_id));
        myVolleyConnection.addParams("tab_type", tab_type);
        myVolleyConnection.setVolley(new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                response_myLoadView(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        myVolleyConnection.requestVolley();

    }

    private void response_myLoadView(String response) {
        Log.d(TAG, "response_myLoadView: "+response);
        JSONObject entryJsonObject = null;
        try {
            entryJsonObject = new JSONObject(response);
            String success = entryJsonObject.getString("success");

            if(success.equals("false")) {
                String fail_reason = entryJsonObject.getString("reason");
//                Toast.makeText(aContext, fail_reason, Toast.LENGTH_LONG).show();

            } else {

                JSONArray jsonDataArray = entryJsonObject.getJSONArray("data");

                for(int i =0; i< jsonDataArray.length(); i++) {

                    JSONObject jsonDataObject = jsonDataArray.getJSONObject(i);

                    int id = jsonDataObject.getInt("member_id");
                    String name = jsonDataObject.getString("member_name");
                    String image = jsonDataObject.getString("member_image");

                    Member_info member_info = new Member_info(id, name, image);
                    int isApply = jsonDataObject.getInt("isApply");
                    member_info.setGroup_id(group_id);

                    member_info.setApply(isApply != 0);

                    adt_agmm_member_list.addItem(member_info);
                }
                adt_agmm_member_list.notifyDataSetChanged();
            }

        } catch (JSONException e) {
            Log.d(TAG, "JSONException: "+e);
        }
    }
}