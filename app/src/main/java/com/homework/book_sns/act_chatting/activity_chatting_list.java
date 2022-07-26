package com.homework.book_sns.act_chatting;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
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
import com.google.gson.Gson;
import com.homework.book_sns.R;
import com.homework.book_sns.javaclass.Chatting_list_info;
import com.homework.book_sns.javaclass.Chatting_msg;
import com.homework.book_sns.javaclass.Chatting_roomList_ofClient;
import com.homework.book_sns.javaclass.LoginSharedPref;
import com.homework.book_sns.javaclass.MyVolleyConnection;
import com.homework.book_sns.javaclass.User_info;
import com.homework.book_sns.rcyv_adapter.Adt_acl_msg_list;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class activity_chatting_list extends AppCompatActivity {

    /* --------------------------- */
    // 액티비티 필수 객체들
    Context aContext;
    String TAG = "hch";
    /* --------------------------- */

    /* --------------------------- */
    // xml의 view 객체들
    Button btn_back;
    RecyclerView rcyv_msg_list;
    /* --------------------------- */

    /* --------------------------- */
    // 각종 객체들
    Adt_acl_msg_list adt_acl_msg_list;
    public static Activity act_chatting_list = null;

    /* --------------------------- */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting_list);

        myInit();

    }

    @Override
    protected void onStart() {
        super.onStart();
        act_chatting_list = activity_chatting_list.this;
        adt_acl_msg_list.clearItem();
        myLoadChatList();
    }


    @Override
    protected void onStop() {
        super.onStop();
        act_chatting_list = null;
    }

    private void myInit() {
        aContext = this;

        myInitView();
    }

    private void myInitView() {
        ActionBar bar = getSupportActionBar();
        bar.hide();

        myFindView();
        mySetDataView();
        mySetClickView();
    }

    private void myFindView() {
        btn_back = findViewById(R.id.btn_acl_back);
        rcyv_msg_list = findViewById(R.id.rcyv_ahl_chatList);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(aContext);
        rcyv_msg_list.setLayoutManager(linearLayoutManager);
        adt_acl_msg_list = new Adt_acl_msg_list();
        rcyv_msg_list.setAdapter(adt_acl_msg_list);
    }

    private void mySetDataView() {
    }

    private void mySetClickView() {
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void myLoadChatList() {
        MyVolleyConnection myVolleyConnection = new MyVolleyConnection(1, aContext);
        myVolleyConnection.setURL("/chatting/ooo_msglist_load.php");
        myVolleyConnection.addParams("client_id", LoginSharedPref.getUserId(aContext));
        myVolleyConnection.setVolley(new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                response_loadView(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        myVolleyConnection.requestVolley();
    }

    private void response_loadView(String response) {
        Log.d(TAG, "response_loadView: "+response);

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

                    String user_id = jsonDataObject.getString("opponent_id");
                    String nickname  = jsonDataObject.getString("opponent_nickname");
                    String profile_photo = jsonDataObject.getString("opponent_profile");

                    User_info opponent_info = new User_info(user_id, nickname, profile_photo);

                    int room_id = jsonDataObject.getInt("room_id");
                    int sender_id = jsonDataObject.getInt("sender_id");
                    String last_msg = jsonDataObject.getString("last_msg");
                    String last_msg_time = jsonDataObject.getString("last_msg_time");
                    int remain_msg_count = jsonDataObject.getInt("remain_msg_count");

                    Chatting_list_info chatting_list_info = new Chatting_list_info(
                            opponent_info, room_id, sender_id, last_msg, last_msg_time,
                            remain_msg_count
                    );
                    adt_acl_msg_list.addItem(chatting_list_info);
                }
                adt_acl_msg_list.sortItem();
                adt_acl_msg_list.notifyDataSetChanged();

            }

        } catch (JSONException e) {
            Log.d(TAG, "JSONException: "+e);
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        processChat(intent);
    }

    private void processChat(Intent intent) {
        if(intent != null) {
            String jsonText = intent.getStringExtra("msg_from_service");

            Chatting_msg chatting_msg = new Chatting_msg();
            Gson gson = new Gson();
            chatting_msg = gson.fromJson(jsonText, Chatting_msg.class);

            User_info opponent_info = new User_info(
                    chatting_msg.getUser_info().getUser_id(),
                    chatting_msg.getUser_info().getUser_nickname(),
                    chatting_msg.getUser_info().getUser_profile() );

            int remain_msg_count = 1;
            for(int i = 0; i < adt_acl_msg_list.getItemSize(); i++) {
                if(chatting_msg.getRoom_id() ==
                adt_acl_msg_list.getItem(i).getRoom_id()) {
                    remain_msg_count = adt_acl_msg_list.getItem(i).getRemain_msg_count() + 1;
                    adt_acl_msg_list.removeItem(i);
                }
            }

            Chatting_list_info chatting_list_info = new Chatting_list_info(
                    opponent_info, chatting_msg.getRoom_id(),
                    Integer.parseInt(chatting_msg.getUser_info().getUser_id()),
                    chatting_msg.getMsg(), chatting_msg.getOriginal_time(),
                    remain_msg_count
            );

            adt_acl_msg_list.addItem(chatting_list_info);
            adt_acl_msg_list.sortItem();
            adt_acl_msg_list.notifyDataSetChanged();
        }
    }

}