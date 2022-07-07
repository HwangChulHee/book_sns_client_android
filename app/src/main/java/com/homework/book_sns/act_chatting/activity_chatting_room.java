package com.homework.book_sns.act_chatting;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.homework.book_sns.R;
import com.homework.book_sns.javaclass.Chatting_msg;
import com.homework.book_sns.javaclass.LoginSharedPref;
import com.homework.book_sns.javaclass.MyVolleyConnection;
import com.homework.book_sns.javaclass.Review_Reply;
import com.homework.book_sns.javaclass.User_info;
import com.homework.book_sns.rcyv_adapter.Adt_acr_msg_list;
import com.kakao.sdk.user.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class activity_chatting_room extends AppCompatActivity {

    /* --------------------------- */
    // 액티비티 필수 객체들
    Context aContext;
    String TAG = "hch";
    /* --------------------------- */


    /* --------------------------- */
    // xml의 view 객체들
    Button btn_back;
    TextView tv_nickname;

    RecyclerView rcyv_msg;

    Button btn_camera;
    Button btn_photo;
    EditText etv_chat_input;
    Button btn_chat_send;
    /* --------------------------- */


    /* --------------------------- */
    // 각종 객체들
    User_info opponent_user;
    Adt_acr_msg_list adt_acr_msg_list;

    Socket socket;
    PrintWriter senWriter;
    BufferedReader receiveChat;
    Thread socketThread;

    int client_room_id;

    InputMethodManager imm;
    /* --------------------------- */

    @Override
    protected void onStop() {
        super.onStop();

        new Thread() {
            @Override
            public void run() {
                super.run();
                senWriter.println(-1);
                senWriter.flush();

                if(socketThread != null) {
                    socketThread.interrupt();
                }

                if(socket != null) {
                    try {
                        if(receiveChat != null) {
                            receiveChat.close();
                        }
                        socket.close();
                        senWriter.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        }.start();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting_room);
        
        myInit();
    }

    private void myInit() {
        aContext = this;

        myGetIntent();
        myInitView();
        loadView_And_connectChat();
        /*
        1. 채팅방의 정보를 가져온 다음 (채팅방이 있으면 채팅방의 id와 채팅 내역을 가져오고, 없으면 채팅방 생성 후 채팅방의 id만 가져온다.)
        2. 채팅서버에 전달할 소켓을 생성하며 채팅방의 id와 user_id를 넘긴다.
        */

    }

    private void myGetIntent() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        opponent_user = bundle.getParcelable("opponent_user");
    }

    private void myInitView() {
        ActionBar bar = getSupportActionBar();
        bar.hide();

        myFindView();
        mySetDataView();
        mySetClickView();
    }

    private void myFindView() {
        btn_back = (Button) findViewById(R.id.btn_acr_back);
        tv_nickname = (TextView) findViewById(R.id.tv_acr_nickname);

        rcyv_msg = (RecyclerView) findViewById(R.id.rcyv_acr_msg);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(aContext);
        rcyv_msg.setLayoutManager(linearLayoutManager);
        adt_acr_msg_list = new Adt_acr_msg_list();
        adt_acr_msg_list.setContext(aContext);
        rcyv_msg.setAdapter(adt_acr_msg_list);


        btn_camera = (Button) findViewById(R.id.btn_acr_camera);
        btn_photo = (Button) findViewById(R.id.btn_acr_photo);
        etv_chat_input = (EditText) findViewById(R.id.etv_acr_chat_input);
        btn_chat_send = (Button) findViewById(R.id.btn_acr_chat_send);

        btn_chat_send.setEnabled(false);
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
    }

    private void mySetDataView() {
        tv_nickname.setText(opponent_user.getUser_nickname());
    }

    private void mySetClickView() {
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        btn_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        etv_chat_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.length() == 0) {
                    btn_chat_send.setSelected(false);
                } else {
                    btn_chat_send.setSelected(true);
                }
            }
        });


        btn_chat_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_chat_send.setEnabled(false);
                imm.hideSoftInputFromWindow(etv_chat_input.getWindowToken(),0);
                sendJsonChat();
            }
        });
    }



    private void loadView_And_connectChat() {
        //client의 id와 opponent user의 id를 보내 둘의 채팅방이 있는지 확인한 후,
        // 있다면 채팅내역을 가져온다.
        Log.d(TAG, "loadView_And_connectChat: ");
        MyVolleyConnection myVolleyConnection = new MyVolleyConnection(1, aContext);
        myVolleyConnection.setURL("/chatting/ooo_msg_read.php");
        myVolleyConnection.addParams("client_id" , LoginSharedPref.getUserId(aContext));
        myVolleyConnection.addParams("opponent_id", opponent_user.getUser_id());
        myVolleyConnection.setVolley(new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                response_loadVew(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        myVolleyConnection.requestVolley();
    }

    private void response_loadVew(String response) {
        Log.d(TAG, "response_loadVew: "+response);
        JSONObject entryJsonObject = null;
        try {
            entryJsonObject = new JSONObject(response);
            String success = entryJsonObject.getString("success");

            if(success.equals("false")) {
                String fail_reason = entryJsonObject.getString("reason");
                Toast.makeText(aContext, fail_reason, Toast.LENGTH_LONG).show();

            } else {

                JSONArray jsonDataArray = entryJsonObject.getJSONArray("data");
                String isData = entryJsonObject.getString("isData");

                int room_id = entryJsonObject.getInt("room_id");

                if(isData.equals("false")) {
                } else {
                    response_loadView_parsing(jsonDataArray);
                }
                connectChat(Integer.toString(room_id) , LoginSharedPref.getUserId(aContext));
            }

        } catch (JSONException e) {
            Log.d(TAG, "JSONException: "+e);
        }
    }

    private void response_loadView_parsing(JSONArray jsonDataArray) throws JSONException {

        int room_id= 0;
        for(int i =0; i< jsonDataArray.length(); i++) {

            JSONObject jsonDataObject = jsonDataArray.getJSONObject(i);

            String user_id = jsonDataObject.getString("sender_id");
            String nickname  = jsonDataObject.getString("sender_nickname");
            String profile_photo = jsonDataObject.getString("sender_profile");

            User_info sender_info = new User_info(user_id, nickname, profile_photo);

             room_id = jsonDataObject.getInt("room_id");
            String chat_msg = jsonDataObject.getString("chat_msg");
            String chat_time = jsonDataObject.getString("chat_time");
            int read_count = jsonDataObject.getInt("read_count");

            Chatting_msg chatting_msg =
                    new Chatting_msg(sender_info,
                            room_id, chat_msg, chat_time,read_count);
            adt_acr_msg_list.addItem(chatting_msg);

        }
        adt_acr_msg_list.notifyDataSetChanged();
        if(adt_acr_msg_list.getSize() > 0) {
            rcyv_msg.scrollToPosition(adt_acr_msg_list.getSize()-1);
        }

    }

    private void connectChat(String room_id, String user_id) {
        client_room_id = Integer.parseInt(room_id);
        btn_chat_send.setEnabled(true);

        socketThread = new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    InetAddress severAddr = InetAddress.getByName(MyVolleyConnection.IP);
                    socket = new Socket(severAddr, MyVolleyConnection.CHAT_PORT);
                    senWriter = new PrintWriter(socket.getOutputStream());
                    senWriter.println(user_id);
                    senWriter.println(room_id);
                    senWriter.flush();

                    receiveChat = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    while (true) {
                        String jsonText = receiveChat.readLine();
                        //
                        if(jsonText != null) {
                            storeChat(jsonText); // 이게 누군지는 어떻게 파악하지.. 파싱해줘야하나..?
                        }
                    }

                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        socketThread.start();
    }

    private void storeChat(String jsonText) {
        Chatting_msg chatting_msg = new Chatting_msg();

        Gson gson = new Gson();
        chatting_msg = gson.fromJson(jsonText, Chatting_msg.class);
        adt_acr_msg_list.addItem(chatting_msg);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adt_acr_msg_list.notifyDataSetChanged();
                if(adt_acr_msg_list.getSize() > 0) {
                    rcyv_msg.scrollToPosition(adt_acr_msg_list.getSize()-1);
                }
            }
        });
    }

    private void sendJsonChat() {
        String sendText = etv_chat_input.getText().toString();
        SimpleDateFormat input_format    = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // 입력포멧
        Date now = new Date();
        String nowTime = input_format.format(now);


        User_info client_info = new User_info(
                LoginSharedPref.getUserId(aContext),
                LoginSharedPref.getPrefNickname(aContext),
                LoginSharedPref.getPrefProfilePhoto(aContext));
        Chatting_msg client_msg = new Chatting_msg(
                client_info,
                client_room_id,
                sendText,
                nowTime,
                0
                );
        Gson gson = new Gson();
        String jsonMsgInfo = gson.toJson(client_msg);

        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    senWriter.println(jsonMsgInfo);
                    senWriter.flush();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            etv_chat_input.setText(null);
                            btn_chat_send.setEnabled(true);
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }


}