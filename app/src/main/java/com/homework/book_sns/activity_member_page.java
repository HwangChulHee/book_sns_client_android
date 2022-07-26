package com.homework.book_sns;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.homework.book_sns.javaclass.Book_info;
import com.homework.book_sns.javaclass.Follow;
import com.homework.book_sns.javaclass.LoginSharedPref;
import com.homework.book_sns.javaclass.MyVolleyConnection;
import com.homework.book_sns.javaclass.Review_list_simple_info;
import com.homework.book_sns.javaclass.SignInfo;
import com.homework.book_sns.javaclass.User_info;
import com.homework.book_sns.rcyv_adapter.Adt_fr_review_simple;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class activity_member_page extends AppCompatActivity {

    String TAG = "hch";
    String IP = "3.34.198.177";
    Context aContext;

    /* --------------------------- */
    // xml의 view 객체들

    Button btn_back;
    TextView tv_follow_nickname;

    CircleImageView civ_profile;
    TextView tv_nickname;

    Button btn_follow;
    Button btn_chat;

    LinearLayout ll_follower;
    LinearLayout ll_following;
    TextView tv_follower_count;
    TextView tv_following_count;

    RadioGroup rg_review_sort;

    /* --------------------------- */

    SignInfo signInfo;
    User_info user_info;

    RecyclerView rcyv_my_review;
    Adt_fr_review_simple adt_fr_mypage_simple;

    String review_sort = "recent";
    /* --------------------------- */

    @Override
    protected void onResume() {
        super.onResume();

        loadData(review_sort);
    }

    @Override
    protected void onPause() {
        super.onPause();
        adt_fr_mypage_simple.clearItem();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_page);

        myInit();
    }

    private void myInit() {
        aContext = this;
        ActionBar bar = getSupportActionBar();
        bar.hide();

        getMyIntent();
        setView();
    }

    private void getMyIntent() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        user_info = bundle.getParcelable("user_info");
    }


    private void setView() {
        btn_back = findViewById(R.id.btn_amp_follow_back);
        tv_follow_nickname = findViewById(R.id.tv_amp_follow_nickname);

        civ_profile = findViewById(R.id.civ_amp_profile_photo);
        tv_nickname = findViewById(R.id.tv_amp_nickname);


        btn_follow = findViewById(R.id.btn_amp_follow);
        btn_chat = findViewById(R.id.btn_amp_chatting);

        ll_follower = findViewById(R.id.ll_amp_follower);
        ll_following = findViewById(R.id.ll_amp_following);
        tv_follower_count = findViewById(R.id.tv_amp_follower_count);
        tv_following_count = findViewById(R.id.tv_amp_following_count);
        

        String image_url = "http://"+IP+user_info.getUser_profile();
        Glide.with(aContext).load(image_url).error(R.drawable.ic_baseline_error_24).into(civ_profile);
        tv_follow_nickname.setText(user_info.getUser_nickname());
        tv_nickname.setText(user_info.getUser_nickname());

        rg_review_sort = findViewById(R.id.rg_amp_review_sort);

        rcyv_my_review = (RecyclerView) findViewById(R.id.rcyv_amp_mypage);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(aContext);
        rcyv_my_review.setLayoutManager(linearLayoutManager);

        adt_fr_mypage_simple = new Adt_fr_review_simple();
        rcyv_my_review.setAdapter(adt_fr_mypage_simple);


        setClickEvent();
    }

    private void setClickEvent() {

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btn_follow.isSelected()) { // 팔로잉 상태가 아닐때 (팔로우가 가능할때)
                    set_follow_btn();
                    btn_follow.setSelected(false); // select = false, 팔로잉 상태로 바꾸어준다.
                } else {
                    set_follow_cancel_btn();
                    btn_follow.setSelected(true);
                }
            }
        });

        btn_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(aContext
                        , com.homework.book_sns.act_chatting.activity_chatting_room.class);
                intent.putExtra("from", "member_page");
                intent.putExtra("opponent_user", user_info);
                startActivity(intent);
            }
        });

        ll_follower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Follow follow_info = new Follow();
                follow_info.setObject_person_id(user_info.getUser_id());
                follow_info.setClient_id(LoginSharedPref.getUserId(aContext));
                follow_info.setUser_nickname(user_info.getUser_nickname());
                follow_info.setFollow_type("follower");

                Intent intent = new Intent(aContext, com.homework.book_sns.act_login_sign.activity_follow.class);
                intent.putExtra("follow_info", follow_info);
                startActivity(intent);
            }
        });

        ll_following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Follow follow_info = new Follow();
                follow_info.setObject_person_id(user_info.getUser_id());
                follow_info.setClient_id(LoginSharedPref.getUserId(aContext));
                follow_info.setUser_nickname(user_info.getUser_nickname());
                follow_info.setFollow_type("following");

                Intent intent = new Intent(aContext, com.homework.book_sns.act_login_sign.activity_follow.class);
                intent.putExtra("follow_info", follow_info);
                startActivity(intent);
            }
        });

        rg_review_sort.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(i == R.id.rb_review_sort_1) {


                } else  if(i == R.id.rb_review_sort_2) {


                }
            }
        });

        adt_fr_mypage_simple.setOnItemClickListener(new Adt_fr_review_simple.OnItemClickListener() {
            @Override
            public void onReviewOptionClick(View v, int pos) {
                PopupMenu popupMenu = new PopupMenu(aContext, v);
                popupMenu.getMenuInflater().inflate(R.menu.review_option_popup, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        Log.d(TAG, "onMenuItemClick: ");
                        switch (menuItem.getItemId()) {
                            case R.id.review_option_menu1:
                                Intent intent = new Intent(aContext, com.homework.book_sns.act_review.activity_review_create_002.class);
                                intent.putExtra("intentType", "update");

                                String review_board_id = adt_fr_mypage_simple.getReviewBoardId(pos);
                                intent.putExtra("review_board_id", review_board_id);
                                startActivity(intent);
                                return true;
                            case R.id.review_option_menu2:
                                removeReviewDate(adt_fr_mypage_simple.getReviewBoardId(pos));
                                adt_fr_mypage_simple.removeItem(pos);
                                adt_fr_mypage_simple.notifyItemRemoved(pos);
                                return true;
                        }
                        return false;
                    }
                });
                popupMenu.show();

            }

            @Override
            public void onFollow(View v, int pos) {
                btn_follow.setSelected(false);
            }

            @Override
            public void onFollowCancel(View v, int pos) {
                btn_follow.setSelected(true);
            }
        });
    }


    private void loadData(String review_sort) {
        loadFollowData();
        loadMyReviewData(review_sort);
    }

    private void loadFollowData() {

        MyVolleyConnection myVolleyConnection = new MyVolleyConnection(1, aContext);
        myVolleyConnection.setURL("/login_sign/get_user_follow_info.php");
        myVolleyConnection.addParams("user_id", user_info.getUser_id());
        myVolleyConnection.setVolley(new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "response_loadFollowData: "+response);
                response_loadFollowData(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        myVolleyConnection.requestVolley();
    }

    private void response_loadFollowData(String response) {

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(response);
            String success = jsonObject.getString("success");

            if(success.equals("false")) {
                String fail_reason = jsonObject.getString("reason");
            } else {
                String follower_count = jsonObject.getString("follower");
                String following_count = jsonObject.getString("following");

                tv_follower_count.setText(follower_count);
                tv_following_count.setText(following_count);
            }


        } catch (JSONException e) {
            Log.d(TAG, "JSONException: ");
        }

    }


    private void loadMyReviewData(String review_sort) {

        MyVolleyConnection myVolleyConnection = new MyVolleyConnection(1, aContext);
        myVolleyConnection.setURL("/review/review_read_simple_list.php");
        myVolleyConnection.addParams("review_page", "member");
        myVolleyConnection.addParams("review_type", "entry");
        myVolleyConnection.addParams("review_sort", review_sort);
        myVolleyConnection.addParams("object_person_id", user_info.getUser_id());
        myVolleyConnection.addParams("client_id", LoginSharedPref.getUserId(aContext));
        myVolleyConnection.addParams("review_sort", review_sort);
        myVolleyConnection.setVolley(new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                responseLoadMyReviewData(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        myVolleyConnection.requestVolley();
    }

    private void responseLoadMyReviewData(String response) {
        Log.d(TAG, "responseLoadMyReviewData: "+response);

        JSONObject entryJsonObject = null;
        JSONArray jsonArray = null;
        try {
            entryJsonObject = new JSONObject(response);
            String success = entryJsonObject.getString("success");

            if(success.equals("false")) {
                String fail_reason = entryJsonObject.getString("reason");
                Toast.makeText(aContext, fail_reason, Toast.LENGTH_LONG).show();

            } else {

                JSONArray jsonArray1 = entryJsonObject.getJSONArray("data");

                for(int i =0; i< jsonArray1.length(); i++) {

                    JSONObject jsonObject = jsonArray1.getJSONObject(i);

                    Review_list_simple_info review_list_simple_info = new Review_list_simple_info();
                    User_info user_info = new User_info();
                    Book_info book_info = new Book_info();
                    ArrayList<String> review_images = new ArrayList<>();


                    String user_id = jsonObject.getString("user_id");
                    String profile_photo = jsonObject.getString("profile_photo");
                    String nickname = jsonObject.getString("nickname");
                    if(jsonObject.getString("following").equals("true")) {
                        review_list_simple_info.setFollowing(true);
                        btn_follow.setSelected(false);
                        btn_follow.setText("팔로잉");
                    } else {
                        review_list_simple_info.setFollowing(false);
                        btn_follow.setSelected(true);
                        btn_follow.setText("팔로우");

                    }

                    String title = jsonObject.getString("title");
                    String author = jsonObject.getString("author");
                    String publisher = jsonObject.getString("publisher");
                    String cover = jsonObject.getString("cover");

                    String review_id = jsonObject.getString("review_id");
                    String register_date = jsonObject.getString("register_date");
//                    String recommendation = jsonObject.getString("recommendation");
                    String content = jsonObject.getString("content");

                    JSONArray imageArray = jsonObject.getJSONArray("review_images");
                    for(int j=0; j<imageArray.length(); j++) {

                        String image = imageArray.getString(j);
                        review_images.add(image);
                    }


                    user_info.setUser_id(user_id);
                    user_info.setUser_nickname(nickname);
                    user_info.setUser_profile(profile_photo);

                    book_info.setTitle(title);
                    book_info.setAuthor(author);
                    book_info.setPublisher(publisher);
                    book_info.setCover(cover);


                    review_list_simple_info.setUser_info(user_info);
                    review_list_simple_info.setBook_info(book_info);

                    review_list_simple_info.setReview_id(review_id);
                    review_list_simple_info.setWriteDate(register_date);
//                    review_list_simple_info.setRecommendCount(recommendation);
                    review_list_simple_info.setReview_text(content);
                    review_list_simple_info.setReview_images(review_images);

                    adt_fr_mypage_simple.addItem(review_list_simple_info);
                }
                adt_fr_mypage_simple.notifyDataSetChanged();


            }


        } catch (JSONException e) {
            Log.d(TAG, "JSONException: "+e);
        }

    }

    private void removeReviewDate(String review_board_id) {
        Log.d(TAG, "removeReviewDate: "+review_board_id);
        MyVolleyConnection myVolleyConnection = new MyVolleyConnection(1, aContext);
        myVolleyConnection.setURL("/review/review_delete.php");
        myVolleyConnection.addParams("review_board_id", review_board_id);
        myVolleyConnection.setVolley(new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse: "+response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        myVolleyConnection.requestVolley();
    }

    private void set_follow_btn() {
        MyVolleyConnection myVolleyConnection = new MyVolleyConnection(1, aContext);
        myVolleyConnection.setURL("/review/review_follow_request.php");
        myVolleyConnection.addParams("user_id", LoginSharedPref.getUserId(aContext));
        myVolleyConnection.addParams("following_id", user_info.getUser_id());
        myVolleyConnection.setVolley(new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                response_set_follow_btn(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        myVolleyConnection.requestVolley();
    }

    private void response_set_follow_btn(String response) {
        Log.d(TAG, "response_set_follow_btn: "+response);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(response);
            String success = jsonObject.getString("success");

            if(success.equals("false")) {
                String fail_reason = jsonObject.getString("reason");
                Toast.makeText(aContext, fail_reason, Toast.LENGTH_LONG).show();

            } else {
                Toast.makeText(aContext, user_info.getUser_nickname()+"님과 팔로우 되었습니다.", Toast.LENGTH_SHORT).show();
                adt_fr_mypage_simple.change_followAllState();
                adt_fr_mypage_simple.notifyDataSetChanged();
                btn_follow.setText("팔로잉");
                loadFollowData();
            }


        } catch (JSONException e) {
            Log.d(TAG, "JSONException: ");
        }
    }

    private void set_follow_cancel_btn() {
        MyVolleyConnection myVolleyConnection = new MyVolleyConnection(1, aContext);
        myVolleyConnection.setURL("/review/review_follow_cancel.php");
        myVolleyConnection.addParams("user_id", LoginSharedPref.getUserId(aContext));
        myVolleyConnection.addParams("following_id", user_info.getUser_id());
        myVolleyConnection.setVolley(new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                response_set_follow_cancel_btn(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        myVolleyConnection.requestVolley();
    }

    private void response_set_follow_cancel_btn(String response) {
        Log.d(TAG, "response_set_follow_btn: "+response);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(response);
            String success = jsonObject.getString("success");

            if(success.equals("false")) {
                String fail_reason = jsonObject.getString("reason");
                Toast.makeText(aContext, fail_reason, Toast.LENGTH_LONG).show();

            } else {
                Toast.makeText(aContext, user_info.getUser_nickname()+"님과 팔로우가 취소되었습니다.", Toast.LENGTH_SHORT).show();
                adt_fr_mypage_simple.change_unFollowAllState();
                adt_fr_mypage_simple.notifyDataSetChanged();
                btn_follow.setText("팔로우");
                loadFollowData();
            }


        } catch (JSONException e) {
            Log.d(TAG, "JSONException: ");
        }
    }
}