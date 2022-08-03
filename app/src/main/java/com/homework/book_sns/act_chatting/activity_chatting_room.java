package com.homework.book_sns.act_chatting;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.homework.book_sns.R;
import com.homework.book_sns.javaclass.Chatting_msg;
import com.homework.book_sns.javaclass.Chatting_roomList_ofClient;
import com.homework.book_sns.javaclass.LoginSharedPref;
import com.homework.book_sns.javaclass.MyImageFunc;
import com.homework.book_sns.javaclass.MyVolleyConnection;
import com.homework.book_sns.javaclass.User_info;
import com.homework.book_sns.rcyv_adapter.Adt_acr_msg;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class activity_chatting_room extends AppCompatActivity {

    /* --------------------------- */
    // 액티비티 필수 객체들
    Context aContext;
    String TAG = "hch";
    /* --------------------------- */


    /* --------------------------- */
    // xml의 view 객체들
    LinearLayout rootView;
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
    Adt_acr_msg adt_acr_msg_;

//    Socket socket;
//    PrintWriter senWriter;
//    BufferedReader receiveChat;
//    Thread socketThread;

    int client_room_id;
    int room_of_people = 1;
    int max_read_count = 1;
    public static Activity act_chatting_room = null;

    InputMethodManager imm;
    int viewHeight = -1;

    int preViewHeight = -1;
    boolean isInitialOriSize = false;
    boolean isInitialKeyboardSize = false;
    boolean activeKeyboard = false;
    int initialSize = -1;
    int initialKeyboardSize = -1;
    int scrollSize = -1;
    int minusScrollSize = -1;

    String fromType = null;
    String chatType = " ";

    String groupName;
    String groupImage;

    /* --------------------------- */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting_room);
        
        myInit();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        act_chatting_room =  null;

        new Thread(new Runnable() {
            @Override
            public void run() {
                service_chatting.senWriter.println("get_out_room"); // 채팅방 나가기
                service_chatting.senWriter.println(LoginSharedPref.getUserId(aContext));
                service_chatting.senWriter.println(client_room_id);
                service_chatting.senWriter.flush();
            }
        }).start();
    }



    private void myInit() {
        aContext = this;
        ActionBar bar = getSupportActionBar();
        bar.hide();

        act_chatting_room =  activity_chatting_room.this;

        myGetIntent();
        /*
        1. 채팅방의 정보를 가져온 다음 (채팅방이 있으면 채팅방의 id와 채팅 내역을 가져오고, 없으면 채팅방 생성 후 채팅방의 id만 가져온다.)
        2. 채팅서버에 전달할 소켓을 생성하며 채팅방의 id와 user_id를 넘긴다.
        */

    }

    private void myGetIntent() {
        Intent intent = getIntent();

        fromType = intent.getStringExtra("from");

        if(intent.getStringExtra("from").equals("member_page")) {
            Bundle bundle = intent.getExtras();
            opponent_user = bundle.getParcelable("opponent_user");

            myInitView();
            loadView_And_connectChat();
        } else if(intent.getStringExtra("from").equals("list")) {

            chatType = intent.getStringExtra("chat_type");

            client_room_id = intent.getIntExtra("room_id", 0);
            bring_opponent_info(client_room_id);
        } else if(intent.getStringExtra("from").equals("group_page")) {
            
            int group_id = intent.getIntExtra("group_id", 0);
            groupName = intent.getStringExtra("group_name");

            myInitView();
            loadView_and_connectChat_group(group_id);
        }

    }

    private void myInitView() {

        myFindView();
        mySetDataView();
        mySetClickView();
    }

    private void myFindView() {
        rootView = findViewById(R.id.ll_acr_rootView);

        btn_back = (Button) findViewById(R.id.btn_acr_back);
        tv_nickname = (TextView) findViewById(R.id.tv_acr_nickname);

        rcyv_msg = (RecyclerView) findViewById(R.id.rcyv_acr_msg);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(aContext);
        rcyv_msg.setLayoutManager(linearLayoutManager);
        adt_acr_msg_ = new Adt_acr_msg();
        adt_acr_msg_.setContext(aContext);
        rcyv_msg.setAdapter(adt_acr_msg_);


        btn_camera = (Button) findViewById(R.id.btn_acr_camera);
        btn_photo = (Button) findViewById(R.id.btn_acr_photo);
        etv_chat_input = (EditText) findViewById(R.id.etv_acr_chat_input);
        btn_chat_send = (Button) findViewById(R.id.btn_acr_chat_send);

        btn_chat_send.setEnabled(false);
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

//        if (imm.isAcceptingText()) {
//            Toast.makeText(aContext, "Software Keyboard was shown", Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(aContext, "Software Keyboard was not shown", Toast.LENGTH_SHORT).show();
//        }
    }

    private void mySetDataView() {
        if(fromType.equals("member_page") || chatType.equals("one")) {
            tv_nickname.setText(opponent_user.getUser_nickname());
        } else {
            tv_nickname.setText(groupName);
        }

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
                Intent intent = MyImageFunc.upload_multi_photo();
                startActivityForResult(intent, 2222);
            }
        });


        rcyv_msg.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int currentViewHeight = rcyv_msg.getHeight();
                if (currentViewHeight > viewHeight && preViewHeight != currentViewHeight ) {

                    if(!isInitialOriSize) {
                        initialSize = currentViewHeight;
                        isInitialOriSize = true;

                    } else {
                        if(!isInitialKeyboardSize) {
                            isInitialKeyboardSize = true;
                            initialKeyboardSize = currentViewHeight;
                        }


                        if(!activeKeyboard) {
                            scrollSize = initialSize - initialKeyboardSize;
                            rcyv_msg.scrollBy(0, scrollSize);
                            activeKeyboard = true;
//                            Log.d(TAG, "onGlobalLayout 1 :"+scrollSize);
//                            Log.d(TAG, "onGlobalLayout: entry" + initialSize);
//                            Log.d(TAG, "onGlobalLayout: entry" + initialKeyboardSize);
                        } else {
                            minusScrollSize = -1 * scrollSize;
                            rcyv_msg.scrollBy(0, minusScrollSize);
                            activeKeyboard = false;
//                            Log.d(TAG, "onGlobalLayout 2 :"+minusScrollSize);
//                            Log.d(TAG, "onGlobalLayout: "+currentViewHeight);
                        }
                    }

                }
                preViewHeight = currentViewHeight;
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
                sendJsonChat(false, null);
            }
        });
    }

    private void bring_opponent_info(int client_room_id) {
        MyVolleyConnection myVolleyConnection = new MyVolleyConnection(1, aContext);
        myVolleyConnection.setURL("/chatting/ooo_bring_opponentinfo.php");
        myVolleyConnection.addParams("client_id", LoginSharedPref.getUserId(aContext));
        myVolleyConnection.addParams("room_id", Integer.toString(client_room_id));
        myVolleyConnection.setVolley(new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                response_bring_opponent_info(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        myVolleyConnection.requestVolley();

    }

    private void response_bring_opponent_info(String response) {
        Log.d(TAG, "response_bring_opponent_info: "+response);
        JSONObject entryJsonObject = null;
        try {
            entryJsonObject = new JSONObject(response);
            String success = entryJsonObject.getString("success");

            if(success.equals("false")) {
                String fail_reason = entryJsonObject.getString("reason");
                Toast.makeText(aContext, fail_reason, Toast.LENGTH_LONG).show();

            } else {

                JSONArray jsonDataArray = entryJsonObject.getJSONArray("data");


                String user_id;
                String user_nickname;
                String user_profile;

                for(int i =0; i< 1; i++) {
                    JSONObject jsonDataObject = jsonDataArray.getJSONObject(i);
                    user_id = jsonDataObject.getString("opponent_id");
                    user_nickname = jsonDataObject.getString("opponent_nickname");
                    user_profile = jsonDataObject.getString("opponent_profile");
                    opponent_user = new User_info(user_id, user_nickname, user_profile);
                }


                loadView_And_connectChat();
                myInitView();
            }

        } catch (JSONException e) {
            Log.d(TAG, "JSONException: "+e);
        }

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
                int read_status = entryJsonObject.getInt("read_status");

                if(isData.equals("false")) {
                } else {
                    response_loadView_parsing(jsonDataArray);
                }

                String str_isNew = entryJsonObject.getString("isNew");
                int isNew = -1;
                if(str_isNew.equals("true")) {
                    isNew = 1;
                } else {
                    isNew = 0;
                }

                String str_isEnter = entryJsonObject.getString("isEnter");
                int isEnter = -1;
                if(str_isEnter.equals("true")) {
                    isEnter = 1;
                } else {
                    isEnter = 0;
                }

                client_room_id = room_id;
                enter_chatRoom(Integer.toString(room_id) , LoginSharedPref.getUserId(aContext), read_status, isNew, isEnter);
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
            int max_count = jsonDataObject.getInt("max_read_count");
            int room_numOfPeople = jsonDataObject.getInt("room_of_people");
            int isImage = jsonDataObject.getInt("isImage");

            ArrayList<String> images = new ArrayList<>();
            if(isImage != 0) {
                JSONArray jsonArray = jsonDataObject.getJSONArray("img_src");
                for(int j=0; j < jsonArray.length(); j++ ) {
                    images.add(jsonArray.getString(j));
                }
            }

//            Log.d(TAG, "채팅 카운트: "+read_count);
            max_read_count = max_count;
            room_of_people = room_numOfPeople;
            Chatting_msg chatting_msg =
                    new Chatting_msg(sender_info,
                            room_id, chat_msg, chat_time,read_count, room_numOfPeople);
            chatting_msg.setMax_read_count(max_read_count);
            chatting_msg.setImage(isImage != 0);
            chatting_msg.setImages(images);
            chatting_msg.setMax_read_count(max_read_count);

//            Log.d(TAG, "response_loadView_parsing: 채팅 파싱.. "+chatting_msg);
            adt_acr_msg_.addItem(chatting_msg);

        }
        adt_acr_msg_.notifyDataSetChanged();
        if(adt_acr_msg_.getSize() > 0) {
            rcyv_msg.scrollToPosition(adt_acr_msg_.getSize()-1);
        }

    }

    private void loadView_and_connectChat_group(int group_id) {
        MyVolleyConnection myVolleyConnection = new MyVolleyConnection(1, aContext);
        myVolleyConnection.setURL("/chatting/ooo_msg_read_group.php");
        myVolleyConnection.addParams("client_id" , LoginSharedPref.getUserId(aContext));
        myVolleyConnection.addParams("group_id", Integer.toString(group_id));
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

    private void participate_newPeople() {

    }



    private void enter_chatRoom(String room_id, String user_id, int read_status, int isNew, int isEnter) {
        btn_chat_send.setEnabled(true);

//        Log.d(TAG, "enter_chatRoom: room_id"+room_id);

        sendRoomList_toChattingServer(room_id, user_id, read_status, isNew, isEnter);
    }

    private void sendRoomList_toChattingServer(String room_id, String user_id, int read_status, int isNew, int isEnter)  {

        MyVolleyConnection myVolleyConnection = new MyVolleyConnection(1, aContext);
        myVolleyConnection.setURL("/chatting/ooo_user_roomlist.php");
        myVolleyConnection.addParams("client_id", LoginSharedPref.getUserId(aContext));
        myVolleyConnection.setVolley(new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                response_roomList(response, room_id, user_id, read_status, isNew, isEnter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        myVolleyConnection.requestVolley();
    }

    private void response_roomList(String response, String room_id, String user_id, int read_status, int isNew, int isEnter) {
        Log.d(TAG, "response_roomList: "+response);
        JSONObject entryJsonObject = null;
        try {
            entryJsonObject = new JSONObject(response);
            String success = entryJsonObject.getString("success");

            if(success.equals("false")) {
                String fail_reason = entryJsonObject.getString("reason");
                Toast.makeText(aContext, fail_reason, Toast.LENGTH_LONG).show();

            } else {

                Chatting_roomList_ofClient roomList_ofClient = new Chatting_roomList_ofClient();
                roomList_ofClient.setUser_id(Integer.parseInt(LoginSharedPref.getUserId(aContext)));

                JSONArray jsonDataArray = entryJsonObject.getJSONArray("data");

                for(int i =0; i< jsonDataArray.length(); i++) {
                    JSONObject jsonDataObject = jsonDataArray.getJSONObject(i);

                    int current_room_id = jsonDataObject.getInt("room_id");
                    JSONArray room_users = jsonDataObject.getJSONArray("room_users");

                    for(int j =0; j<room_users.length(); j++) {
                        int user_id_ofRoom = room_users.getInt(j);
                        roomList_ofClient.addRoomUser(current_room_id, user_id_ofRoom);
                    }

                }

                Gson gson = new Gson();
                String json_roomList = gson.toJson(roomList_ofClient);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        service_chatting.senWriter.println("join_room");
                        service_chatting.senWriter.println(json_roomList);
                        service_chatting.senWriter.println(user_id);
                        service_chatting.senWriter.println(room_id);
//                        Log.d(TAG, "run: adfadsfasd "+room_id);
                        service_chatting.senWriter.println(read_status);
                        service_chatting.senWriter.println(isNew);
                        if(isEnter != 0) {
                            SimpleDateFormat input_format    = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // 입력포멧
                            Date now = new Date();
                            String nowTime = input_format.format(now);

                            User_info user_info = new User_info(
                                    LoginSharedPref.getUserId(aContext),
                                    LoginSharedPref.getPrefNickname(aContext),
                                    LoginSharedPref.getPrefProfilePhoto(aContext)
                            );
                            Chatting_msg chatting_msg = new Chatting_msg(
                                    user_info, Integer.parseInt(room_id), null, nowTime, 0, room_of_people
                            );
                            chatting_msg.setMax_read_count(max_read_count);
                            chatting_msg.setEnter(true);
                            Gson gson = new Gson();
                            String jsonMsgInfo = gson.toJson(chatting_msg);
                            service_chatting.senWriter.println(jsonMsgInfo);

                        } else {
                            //아무것도 안함.
                        }
                        service_chatting.senWriter.flush();

                    }
                }).start();


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
            Log.d(TAG, "processChat: "+jsonText);

            if(jsonText.equals("read_count_plus")) {
                adt_acr_msg_.plus_all_read_count();
                adt_acr_msg_.notifyDataSetChanged();
            } else if(jsonText.equals("new_people")){
                room_of_people++;
            } else {
                Chatting_msg chatting_msg = new Chatting_msg();
                Gson gson = new Gson();
                chatting_msg = gson.fromJson(jsonText, Chatting_msg.class);
                if(chatting_msg.getMsg_type().equals("chat")) {
                    showChat(chatting_msg);
                }

            }
        }
    }

    private void showChat(Chatting_msg chatting_msg) {
        adt_acr_msg_.addItem(chatting_msg);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adt_acr_msg_.notifyDataSetChanged();
                if(adt_acr_msg_.getSize() > 0) {
                    rcyv_msg.scrollToPosition(adt_acr_msg_.getSize()-1);
                }
            }
        });
    }


    private void sendJsonChat(boolean isImage, ArrayList<String> images) {
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
                0,
                room_of_people
                );

        if (isImage) {
            client_msg.setImage(true);
            client_msg.setImages(images);
            client_msg.setMsg("사진 "+images.size()+" 장을 보냈습니다.");
        } else {
            client_msg.setImage(false);
        }

        Gson gson = new Gson();
        String jsonMsgInfo = gson.toJson(client_msg);

        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    service_chatting.senWriter.println(jsonMsgInfo);
                    service_chatting.senWriter.flush();
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

    private void showToast(String text) {
        Toast.makeText(aContext, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 2222) {
            ArrayList<Uri> images = new ArrayList<Uri>();
            ArrayList<String> base64Images = new ArrayList<>();
            images = MyImageFunc.result_multi_photo(data, images, aContext);
            for(int i =0; i<images.size(); i++) {
                Bitmap bitmap = MyImageFunc.getBitmapImage_FromUri(images.get(i), aContext);
                String base64 = MyImageFunc.getBase64Image_FromBitmap(bitmap);
                base64Images.add(base64);
            }

            if(images.size() != 0) {
                sendJsonChat(true, base64Images);
            }
            Log.d(TAG, "onActivityResult: 이미지 개수 "+base64Images.size());
        }

    }
}