package com.homework.book_sns.act_review;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.homework.book_sns.R;
import com.homework.book_sns.javaclass.Book_info;
import com.homework.book_sns.javaclass.LoginSharedPref;
import com.homework.book_sns.javaclass.MyVolleyConnection;
import com.homework.book_sns.javaclass.Review_Reply;
import com.homework.book_sns.javaclass.Review_info;
import com.homework.book_sns.javaclass.Review_list_simple_info;
import com.homework.book_sns.javaclass.User_info;
import com.homework.book_sns.rcyv_adapter.Adt_arrd_reply;
import com.homework.book_sns.rcyv_adapter.Adt_rc2_photos;
import com.homework.book_sns.vp_adapter.Adt_rrd_review_image;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import me.relex.circleindicator.CircleIndicator;
import me.relex.circleindicator.CircleIndicator3;

public class activity_review_read_detail extends AppCompatActivity {

    /* --------------------------- */
    // 액티비티 필수 객체들
    Context aContext;
    String TAG = "hch";
    /* --------------------------- */

    /* --------------------------- */
    // xml의 view 객체들

    Button btn_back;

    CircleImageView civ_profile_photo;
    TextView tv_nickname;
    TextView tv_follow;
    TextView tv_review_date;

    Button btn_option;

    NestedScrollView nscrv_main;

    TextView tv_review_text;

    ViewPager vp_review_image;
    CircleIndicator indicator;
    LinearLayout ll_review_image;

    ImageView iv_cover;
    TextView tv_title;
    TextView tv_author;

    TextView tv_recommendation_count;
    TextView tv_reply_count;

    Button btn_recommendation;

    LinearLayout ll_re_reply;
    TextView tv_re_reply_status;
    Button btn_re_reply_cancel;

    LinearLayout ll_reply_update;
    TextView tv_reply_update_status;
    Button btn_reply_update_cancel;

    EditText etv_reply;
    Button btn_reply;

    RecyclerView rcyv_reply;

    /* --------------------------- */

    /* --------------------------- */
    // 각종 객체들
    String review_board_id;

    ArrayList<String> review_imageList;
    Adt_rrd_review_image adt_rrd_review_image;

    Review_list_simple_info review_info;
    Adt_arrd_reply adt_arrd_reply;
    InputMethodManager imm;

    boolean isRe_Reply = false; // 댓글과 답글을 구분하는 변수
    int re_reply_position; // 답글을 클릭한 아이템의 위치
    
    boolean isReply_update = false; // 수정을 구분하는 변수
    int reply_update_position; //수정을 클릭한 아이템의 위치
    /* --------------------------- */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_read_detail);

        aContext = this;

        setObject();
        setView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        imm.hideSoftInputFromWindow(etv_reply.getWindowToken(), 0);
    }

    private void setObject() {
        Intent intent = getIntent();
        review_board_id = intent.getStringExtra("review_board_id");

        review_imageList = new ArrayList<>();
        adt_rrd_review_image = new Adt_rrd_review_image(this, review_imageList);

        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
    }

    private void setView() {
        ActionBar bar = getSupportActionBar();
        bar.hide();

        btn_back = (Button) findViewById(R.id.btn_rr_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        civ_profile_photo = (CircleImageView) findViewById(R.id.civ_rr_profile_photo);
        tv_nickname = (TextView) findViewById(R.id.tv_rr_nickname);
        tv_follow = (TextView) findViewById(R.id.tv_rr_follow);
        tv_review_date = (TextView) findViewById(R.id.tv_rr_review_date);

        btn_option = (Button) findViewById(R.id.btn_rr_plus_option);

        nscrv_main = (NestedScrollView) findViewById(R.id.nscrv_arrd);

        tv_review_text = (TextView) findViewById(R.id.tv_rr_review_text);

        vp_review_image = (ViewPager) findViewById(R.id.vp_rr_review_image);
        vp_review_image.setAdapter(adt_rrd_review_image);
        indicator = (CircleIndicator) findViewById(R.id.cid_rr_review_image);

        ll_review_image = (LinearLayout) findViewById(R.id.ll_rr_review_image);

        iv_cover = (ImageView) findViewById(R.id.iv_rr_book_cover);
        tv_title = (TextView) findViewById(R.id.tv_rr_book_title);
        tv_author = (TextView) findViewById(R.id.tv_rr_book_author);

        tv_recommendation_count = (TextView) findViewById(R.id.tv_arrd_recommendation_count);
        tv_reply_count = (TextView) findViewById(R.id.tv_arrd_reply_count);

        btn_recommendation = (Button) findViewById(R.id.btn_arrd_recommendation);


        ll_re_reply = (LinearLayout) findViewById(R.id.ll_arrd_re_reply);
        tv_re_reply_status = (TextView) findViewById(R.id.tv_arrd_re_reply_status);
        btn_re_reply_cancel = (Button) findViewById(R.id.btn_arrd_re_reply_cancel);

        ll_reply_update = (LinearLayout) findViewById(R.id.ll_arrd_reply_update);
        tv_reply_update_status = (TextView) findViewById(R.id.tv_arrd_reply_update_status);
        btn_reply_update_cancel = (Button) findViewById(R.id.btn_arrd_reply_update_cancel);

        etv_reply = (EditText) findViewById(R.id.etv_arrd_reply);
        btn_reply = (Button) findViewById(R.id.btn_arrd_reply);

        rcyv_reply = (RecyclerView) findViewById(R.id.rcyv_arrd_reply);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(aContext);
        rcyv_reply.setLayoutManager(linearLayoutManager);

        adt_arrd_reply = new Adt_arrd_reply();
        rcyv_reply.setAdapter(adt_arrd_reply);

        requestLoadData();
        request_loadReplyData();
    }

    private void requestLoadData() {
        MyVolleyConnection myVolleyConnection = new MyVolleyConnection(1, aContext);
        myVolleyConnection.setURL("/review/review_read_request.php");
        myVolleyConnection.addParams("review_board_id", review_board_id);
        myVolleyConnection.addParams("client_id", LoginSharedPref.getUserId(aContext));
        myVolleyConnection.setVolley(new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse: "+response);
                loadView(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        myVolleyConnection.requestVolley();
    }

    private void loadView(String response) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(response);
            String success = jsonObject.getString("success");

            if(success.equals("false")) {
                String fail_reason = jsonObject.getString("reason");
                Toast.makeText(getApplicationContext(), fail_reason, Toast.LENGTH_LONG).show();

            } else {

                review_info = new Review_list_simple_info();

                String user_id = jsonObject.getString("user_id");
                String profile_photo = jsonObject.getString("profile_photo");
                String nickname = jsonObject.getString("nickname");

                User_info user_info = new User_info();
                user_info.setUser_nickname(nickname);
                user_info.setUser_id(user_id);

                review_info.setUser_info(user_info);

                if(jsonObject.getString("following").equals("true")) {
                    tv_follow.setVisibility(View.INVISIBLE); // 이미 팔로잉 되있으면 팔로우 버튼을 없앰.
                    review_info.setFollowing(true);
                } else  {
                    if(LoginSharedPref.getUserId(aContext).equals(user_id)) {
                        tv_follow.setVisibility(View.INVISIBLE); // 자기 자신이라면..
                        review_info.setFollowing(true);
                    } else {
                        tv_follow.setVisibility(View.VISIBLE);
                        review_info.setFollowing(false);
                    }
                }



                String register_date = jsonObject.getString("register_date");
                String recommendation_count = jsonObject.getString("recommendation_count");
                String reply_count = jsonObject.getString("reply_count");
                String content = jsonObject.getString("content");

                String title = jsonObject.getString("title");
                String author = jsonObject.getString("author");
                String publisher = jsonObject.getString("publisher");
                String cover = jsonObject.getString("cover");

                String isClient_recommendation = jsonObject.getString("isClient_recommendation");
                if(isClient_recommendation.equals("true")) {
                    btn_recommendation.setSelected(true);
                    review_info.setClient_recommendation(true);
                } else if(isClient_recommendation.equals("false")) {
                    btn_recommendation.setSelected(false);
                    review_info.setClient_recommendation(false);
                }

                JSONArray imageArray = jsonObject.getJSONArray("review_images");
                Log.d(TAG, "loadView: "+imageArray.length());
                for(int i=0; i<imageArray.length(); i++) {

                    String image = imageArray.getString(i);

                    adt_rrd_review_image.addItem(image);

                }
                adt_rrd_review_image.notifyDataSetChanged();
                indicator.setViewPager(vp_review_image);

                if(imageArray.length() == 0) {
                    vp_review_image.setVisibility(View.GONE);
                    indicator.setVisibility(View.GONE);
                }




                String image_url = "http://"+MyVolleyConnection.IP
                        + profile_photo;
                Glide.with(getApplicationContext())
                        .load(image_url)
                        .error(R.drawable.ic_baseline_error_24)
                        .into(civ_profile_photo);

                tv_nickname.setText(nickname);
                tv_review_date.setText(register_date);
                tv_review_text.setText(content);



                tv_title.setText(title);
                tv_author.setText(author);
                Glide.with(getApplicationContext())
                        .load(cover)
                        .error(R.drawable.ic_baseline_error_24)
                        .into(iv_cover);

                int int_recommendation_count = Integer.parseInt(recommendation_count);
                int int_reply_count = Integer.parseInt(reply_count);

                review_info.setRecommendCount(int_recommendation_count);
                review_info.setReplyCount(int_reply_count);

                if(int_recommendation_count == 0 && int_reply_count == 0) {
                    tv_recommendation_count.setVisibility(View.GONE);
                    tv_reply_count.setVisibility(View.GONE);
                } else {
                    if(int_recommendation_count == 0) {
                        tv_recommendation_count.setVisibility(View.INVISIBLE);
                    } else {
                        tv_recommendation_count.setText("추천 "+recommendation_count+" 개");
                        tv_recommendation_count.setVisibility(View.VISIBLE);
                    }

                    if(int_reply_count == 0) {
                        tv_reply_count.setVisibility(View.INVISIBLE);
                    } else {
                        tv_reply_count.setText("댓글 "+reply_count+" 개");
                        tv_reply_count.setVisibility(View.VISIBLE);
                    }
                }
            }

            setClickEvent();


        } catch (JSONException e) {
            Log.d(TAG, "JSONException: "+e);
        }
    }

    private void setClickEvent() {

        tv_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                set_follow_btn();
            }
        });

        btn_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                set_review_option(view);
            }
        });

        tv_recommendation_count.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(aContext, com.homework.book_sns.act_review.activity_recommendation.class);
                intent.putExtra("review_board_id", review_board_id);
                startActivity(intent);
            }
        });

        btn_recommendation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btn_recommendation.isSelected()) { // 추천된 상태 => 추천 취소
                    btn_recommendation.setSelected(false);
                    set_recommendation_cancel_btn();

                } else { // 추천하지 않은 상태 => 추천
                    btn_recommendation.setSelected(true);
                    set_recommendation_btn();
                }
            }
        });

        etv_reply.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.length() == 0) {
                    btn_reply.setSelected(false);
                } else {
                    btn_reply.setSelected(true);
                }
            }
        });

        btn_reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: "+isRe_Reply + " dd  "+isReply_update);
                if(btn_reply.isSelected()) {
                    if(!isRe_Reply && !isReply_update) {
                        reply_create();
                    } else if(isRe_Reply && !isReply_update) {
                        re_reply_create(re_reply_position);
                    } else if(!isRe_Reply && isReply_update) {
                        reply_update();
                    } else {
                        Toast.makeText(aContext, "에러", Toast.LENGTH_SHORT).show();
                    }


                } else {
                    Toast.makeText(aContext, "내용을 입력해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

        btn_re_reply_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isRe_Reply = false;
                ll_re_reply.setVisibility(View.GONE);
                etv_reply.setText(null);
                imm.hideSoftInputFromWindow(etv_reply.getWindowToken(), 0);
            }
        });

        btn_reply_update_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isReply_update = false;
                ll_reply_update.setVisibility(View.GONE);
                etv_reply.setText(null);
                imm.hideSoftInputFromWindow(etv_reply.getWindowToken(), 0);
            }
        });

        adt_arrd_reply.setOnItemClickListener(new Adt_arrd_reply.OnItemClickListener() {
            @Override
            public void onCreate_Re_Reply(View v, int pos) {
                re_reply_create_prepare(pos);
            }

            @Override
            public void onUpdate_Reply(View v, int pos) {
                reply_update_prepare(pos);
            }
        });
    }

    private void request_loadReplyData() {
        MyVolleyConnection myVolleyConnection = new MyVolleyConnection(1, aContext);
        myVolleyConnection.setURL("/review/review_reply_read.php");
        myVolleyConnection.addParams("client_id" , LoginSharedPref.getUserId(aContext));
        myVolleyConnection.addParams("review_board_id", review_board_id);
        myVolleyConnection.setVolley(new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                response_loadReplyData(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        myVolleyConnection.requestVolley();
    }

    private void response_loadReplyData(String response) {
        JSONObject entryJsonObject = null;
        try {
            entryJsonObject = new JSONObject(response);
            String success = entryJsonObject.getString("success");

            if(success.equals("false")) {
                String fail_reason = entryJsonObject.getString("reason");
                Toast.makeText(aContext, fail_reason, Toast.LENGTH_LONG).show();

            } else {

                JSONArray jsonDataArray = entryJsonObject.getJSONArray("data");

                for(int i =0; i< jsonDataArray.length(); i++) {

                    JSONObject jsonDataObject = jsonDataArray.getJSONObject(i);

                    Review_Reply review_reply = new Review_Reply();
                    User_info user_info = new User_info();

                    String user_id = jsonDataObject.getString("user_id");
                    String nickname  = jsonDataObject.getString("user_nickname");
                    String profile_photo = jsonDataObject.getString("user_profile");

                    String review_board_id = jsonDataObject.getString("review_board_id");
                    String reply_content = jsonDataObject.getString("reply_content");
                    String reply_class = jsonDataObject.getString("reply_class");
                    String group_num = jsonDataObject.getString("group_num");
                    String tag_user_id = jsonDataObject.getString("tag_user_id");
                    String tag_user_nickname = jsonDataObject.getString("tag_user_nickname");

                    String reply_id = jsonDataObject.getString("reply_id");
                    String recommendation_count = jsonDataObject.getString("recommendation_count");
                    String reply_register_date = jsonDataObject.getString("reply_register_date");
                    String isClient_recommendation = jsonDataObject.getString("isClient_recommendation");

                    int isRemoved = jsonDataObject.getInt("isRemoved");


                    user_info.setUser_id(user_id);
                    user_info.setUser_nickname(nickname);
                    user_info.setUser_profile(profile_photo);
                    review_reply.setUser_info(user_info);

                    review_reply.setReview_board_id(review_board_id);
                    review_reply.setReply_content(reply_content);
                    review_reply.setReply_class(reply_class);
                    review_reply.setGroup_num(group_num);
                    review_reply.setTag_user_id(tag_user_id);
                    review_reply.setTag_user_nickname(tag_user_nickname);

                    review_reply.setReply_id(reply_id);
                    review_reply.setRecommendation_count(recommendation_count);
                    review_reply.setReply_register_date(reply_register_date);
                    if(isClient_recommendation.equals("true")) {
                        review_reply.setClient_recommendation(true);
                    } else {
                        review_reply.setClient_recommendation(false);
                    }

                    Log.d(TAG, "response_loadReplyData: "+isRemoved);

                    if(isRemoved == 1) {
                        review_reply.setRemoved(true);
                    } else {
                        review_reply.setRemoved(false);
                    }


                    adt_arrd_reply.addItem(review_reply);
                }
                adt_arrd_reply.notifyDataSetChanged();
            }

        } catch (JSONException e) {
            Log.d(TAG, "JSONException: "+e);
        }

    }

    private void reply_create() {
        String reply_content = etv_reply.getText().toString();

        MyVolleyConnection myVolleyConnection = new MyVolleyConnection(1, aContext);
        myVolleyConnection.setURL("/review/review_reply_create.php");
        myVolleyConnection.addParams("client_id" , LoginSharedPref.getUserId(aContext));
        myVolleyConnection.addParams("review_board_id", review_board_id);
        myVolleyConnection.addParams("reply_content", reply_content);
        myVolleyConnection.addParams("reply_class", "1");
        myVolleyConnection.addParams("group_num", "null");
        myVolleyConnection.addParams("tag_user_id", "null");
        myVolleyConnection.addParams("tag_user_nickname", "null");
        myVolleyConnection.setVolley(new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                reply_create_response(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        myVolleyConnection.requestVolley();
    }

    private void reply_create_response(String response) {
        Log.d(TAG, "reply_create_response: "+response);
        JSONObject entryJsonObject = null;
        try {
            entryJsonObject = new JSONObject(response);
            String success = entryJsonObject.getString("success");

            if(success.equals("false")) {
                String fail_reason = entryJsonObject.getString("reason");
                Toast.makeText(aContext, fail_reason, Toast.LENGTH_LONG).show();

            } else {

                JSONArray jsonDataArray = entryJsonObject.getJSONArray("data");

                for(int i =0; i< jsonDataArray.length(); i++) {

                    JSONObject jsonDataObject = jsonDataArray.getJSONObject(i);

                    Review_Reply review_reply = new Review_Reply();
                    User_info user_info = new User_info();

                    String user_id = jsonDataObject.getString("user_id");
                    String nickname  = jsonDataObject.getString("user_nickname");
                    String profile_photo = jsonDataObject.getString("user_profile");

                    String review_board_id = jsonDataObject.getString("review_board_id");
                    String reply_content = jsonDataObject.getString("reply_content");
                    String reply_class = jsonDataObject.getString("reply_class");
                    String group_num = jsonDataObject.getString("group_num");
                    String tag_user_id = jsonDataObject.getString("tag_user_id");
                    String tag_user_nickname = jsonDataObject.getString("tag_user_nickname");

                    String reply_id = jsonDataObject.getString("reply_id");
                    String recommendation_count = jsonDataObject.getString("recommendation_count");
                    String reply_register_date = jsonDataObject.getString("reply_register_date");
                    String isClient_recommendation = jsonDataObject.getString("isClient_recommendation");


                    user_info.setUser_id(user_id);
                    user_info.setUser_nickname(nickname);
                    user_info.setUser_profile(profile_photo);
                    review_reply.setUser_info(user_info);

                    review_reply.setReview_board_id(review_board_id);
                    review_reply.setReply_content(reply_content);
                    review_reply.setReply_class(reply_class);
                    review_reply.setGroup_num(group_num);
                    review_reply.setTag_user_id(tag_user_id);
                    review_reply.setTag_user_nickname(tag_user_nickname);

                    review_reply.setReply_id(reply_id);
                    review_reply.setRecommendation_count(recommendation_count);
                    review_reply.setReply_register_date(reply_register_date);
                    if(isClient_recommendation.equals("true")) {
                        review_reply.setClient_recommendation(true);
                    } else {
                        review_reply.setClient_recommendation(false);
                    }

                    adt_arrd_reply.addItem(review_reply);
                }
                adt_arrd_reply.notifyItemInserted(adt_arrd_reply.getItemCount()-1);
                etv_reply.setText(null);
                imm.hideSoftInputFromWindow(etv_reply.getWindowToken(), 0);

                nscrv_main.post(new Runnable() {
                    @Override
                    public void run() {
                        nscrv_main.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });
            }

        } catch (JSONException e) {
            Log.d(TAG, "JSONException: "+e);
        }

    }

    private void re_reply_create_prepare(int position) {
        etv_reply.requestFocus();
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

        Review_Reply item = adt_arrd_reply.getItem(position);

        tv_re_reply_status.setText(item.getUser_info().getUser_nickname()+" 님에게 답글다는 중..");
        ll_re_reply.setVisibility(View.VISIBLE);

        re_reply_position = position;
        isRe_Reply = true;
    }

    private void re_reply_create(int position) {

        String reply_content = etv_reply.getText().toString();

        Review_Reply item = adt_arrd_reply.getItem(position);
        String group_num = item.getGroup_num();
        String tag_user_id = item.getUser_info().getUser_id();
        String tag_user_nickname = item.getUser_info().getUser_nickname();

        Log.d(TAG, "re_reply_create: "+group_num+tag_user_id+tag_user_nickname);

        MyVolleyConnection myVolleyConnection = new MyVolleyConnection(1, aContext);
        myVolleyConnection.setURL("/review/review_reply_create.php");
        myVolleyConnection.addParams("client_id" , LoginSharedPref.getUserId(aContext));
        myVolleyConnection.addParams("review_board_id", review_board_id);
        myVolleyConnection.addParams("reply_content", reply_content);
        myVolleyConnection.addParams("reply_class", "2");
        myVolleyConnection.addParams("group_num", group_num);
        myVolleyConnection.addParams("tag_user_id", tag_user_id);
        myVolleyConnection.addParams("tag_user_nickname", tag_user_nickname);
        myVolleyConnection.setVolley(new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // 양치한다음 이 부분 만들어주자. 같은 group_num을 탐색하고, 맨 마지막에 넣어주면 된다.
                response_re_reply_create(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        myVolleyConnection.requestVolley();
    }

    private void response_re_reply_create(String response) {
        Log.d(TAG, "response_re_reply_create: "+response);
        JSONObject entryJsonObject = null;
        try {
            entryJsonObject = new JSONObject(response);
            String success = entryJsonObject.getString("success");

            if(success.equals("false")) {
                String fail_reason = entryJsonObject.getString("reason");
                Toast.makeText(aContext, fail_reason, Toast.LENGTH_LONG).show();

            } else {

                JSONArray jsonDataArray = entryJsonObject.getJSONArray("data");

                for(int i =0; i< jsonDataArray.length(); i++) {

                    JSONObject jsonDataObject = jsonDataArray.getJSONObject(i);

                    Review_Reply review_reply = new Review_Reply();
                    User_info user_info = new User_info();

                    String user_id = jsonDataObject.getString("user_id");
                    String nickname  = jsonDataObject.getString("user_nickname");
                    String profile_photo = jsonDataObject.getString("user_profile");

                    String review_board_id = jsonDataObject.getString("review_board_id");
                    String reply_content = jsonDataObject.getString("reply_content");
                    String reply_class = jsonDataObject.getString("reply_class");
                    String group_num = jsonDataObject.getString("group_num");
                    String tag_user_id = jsonDataObject.getString("tag_user_id");
                    String tag_user_nickname = jsonDataObject.getString("tag_user_nickname");

                    String reply_id = jsonDataObject.getString("reply_id");
                    String recommendation_count = jsonDataObject.getString("recommendation_count");
                    String reply_register_date = jsonDataObject.getString("reply_register_date");
                    String isClient_recommendation = jsonDataObject.getString("isClient_recommendation");


                    user_info.setUser_id(user_id);
                    user_info.setUser_nickname(nickname);
                    user_info.setUser_profile(profile_photo);
                    review_reply.setUser_info(user_info);

                    review_reply.setReview_board_id(review_board_id);
                    review_reply.setReply_content(reply_content);
                    review_reply.setReply_class(reply_class);
                    review_reply.setGroup_num(group_num);
                    review_reply.setTag_user_id(tag_user_id);
                    review_reply.setTag_user_nickname(tag_user_nickname);

                    review_reply.setReply_id(reply_id);
                    review_reply.setRecommendation_count(recommendation_count);
                    review_reply.setReply_register_date(reply_register_date);
                    if(isClient_recommendation.equals("true")) {
                        review_reply.setClient_recommendation(true);
                    } else {
                        review_reply.setClient_recommendation(false);
                    }

                    int reply_position = 0;
                    ArrayList<Review_Reply> sameGroup_replyArray = new ArrayList<>();
                    for(int j = 0; j < adt_arrd_reply.getItemCount(); j++) {
                        Review_Reply item = adt_arrd_reply.getItem(j);
                        if(item.getGroup_num().equals(group_num)) {
                            sameGroup_replyArray.add(item);
                            if(item.getReply_class().equals("1")) {
                                reply_position = j;
                            }
                        }
                    }

                    //댓글 1개 => 댓글 위치(items _ position) + 1
                    int re_reply_position = reply_position + sameGroup_replyArray.size();
                    adt_arrd_reply.insertItem(review_reply , re_reply_position);
                    adt_arrd_reply.notifyItemInserted(re_reply_position);
                }


                etv_reply.setText(null);
                imm.hideSoftInputFromWindow(etv_reply.getWindowToken(), 0);
                isRe_Reply = false;
                ll_re_reply.setVisibility(View.GONE);

            }

        } catch (JSONException e) {
            Log.d(TAG, "JSONException re_reply: "+e);
        }

    }

    private void reply_update_prepare(int position) {
        etv_reply.requestFocus();
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

        Review_Reply item = adt_arrd_reply.getItem(position);

        if(item.getReply_class().equals("1")) {
            tv_reply_update_status.setText(item.getUser_info().getUser_nickname()+" 님의 댓글 수정중..");
        } else if(item.getReply_class().equals("2")) {
            tv_reply_update_status.setText(item.getUser_info().getUser_nickname()+" 님의 답글 수정중..");
        }

        etv_reply.setText(adt_arrd_reply.getItem(position).getReply_content());
        etv_reply.setSelection(etv_reply.length());

        ll_reply_update.setVisibility(View.VISIBLE);

        reply_update_position = position;
        isReply_update = true;
    }

    private void reply_update() {
        String reply_id = adt_arrd_reply.getItem(reply_update_position).getReply_id();
        String reply_content = etv_reply.getText().toString();

        MyVolleyConnection myVolleyConnection = new MyVolleyConnection(1, aContext);
        myVolleyConnection.setURL("/review/review_reply_update.php");
        myVolleyConnection.addParams("reply_id", reply_id);
        myVolleyConnection.addParams("reply_content", reply_content);
        myVolleyConnection.setVolley(new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                reply_update_response(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        myVolleyConnection.requestVolley();
    }

    private void reply_update_response(String response) {
        Log.d(TAG, "reply_update_response: "+response);
        JSONObject entryJsonObject = null;
        try {
            entryJsonObject = new JSONObject(response);
            String success = entryJsonObject.getString("success");

            if(success.equals("false")) {
                String fail_reason = entryJsonObject.getString("reason");
                Toast.makeText(aContext, fail_reason, Toast.LENGTH_LONG).show();

            } else {

                JSONArray jsonDataArray = entryJsonObject.getJSONArray("data");

                for(int i =0; i< jsonDataArray.length(); i++) {

                    JSONObject jsonDataObject = jsonDataArray.getJSONObject(i);
                    String reply_content = jsonDataObject.getString("update_content");

                    adt_arrd_reply.getItem(reply_update_position).setReply_content(reply_content);
                    adt_arrd_reply.notifyItemChanged(reply_update_position);
                }


                etv_reply.setText(null);
                imm.hideSoftInputFromWindow(etv_reply.getWindowToken(), 0);
                isReply_update = false;
                ll_reply_update.setVisibility(View.GONE);
                
                if(adt_arrd_reply.getItem(reply_update_position).getReply_class().equals("1")) {
                    Toast.makeText(aContext, "댓글이 수정되었습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(aContext, "답글이 수정되었습니다.", Toast.LENGTH_SHORT).show();                    
                }

            }

        } catch (JSONException e) {
            Log.d(TAG, "JSONException update_reply: "+e);
        }
    }


    private void set_review_option(View clickView) {
        String writer_id = review_info.getUser_info().getUser_id();
        PopupMenu popupMenu = new PopupMenu(aContext, clickView);

        if(writer_id.equals(LoginSharedPref.getUserId(aContext))) {
            popupMenu.getMenuInflater().inflate(R.menu.review_option_popup, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    Log.d(TAG, "onMenuItemClick: ");
                    switch (menuItem.getItemId()) {
                        case R.id.review_option_menu1:
                            Intent intent = new Intent(aContext, com.homework.book_sns.act_review.activity_review_create_002.class);
                            intent.putExtra("intentType", "update");

                            intent.putExtra("review_board_id", review_board_id);
                            startActivity(intent);
                            return true;
                        case R.id.review_option_menu2:
                            removeReviewDate(review_board_id);
                            finish();
                            return true;
                    }
                    return false;
                }
            });

        } else {
            if(!review_info.isFollowing()) {
                popupMenu.getMenuInflater().inflate(R.menu.review_option_popup2, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        Log.d(TAG, "onMenuItemClick: ");
                        switch (menuItem.getItemId()) {
                            case R.id.review_option2_menu1:
                                return true;
                        }
                        return false;
                    }
                });
            } else {
                popupMenu.getMenuInflater().inflate(R.menu.review_option_popup3, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        Log.d(TAG, "onMenuItemClick: ");
                        switch (menuItem.getItemId()) {
                            case R.id.review_option3_menu1:
                                return true;
                            case R.id.review_option3_menu2:
                                set_follow_cancel_popup(); // 따로 절차를 밟아야..
                                return true;
                        }
                        return false;
                    }
                });

            }
        }
        popupMenu.show();
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
                Toast.makeText(aContext, "게시글이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        myVolleyConnection.requestVolley();

    }





    private void set_recommendation_btn () {
        MyVolleyConnection myVolleyConnection = new MyVolleyConnection(1, aContext);
        myVolleyConnection.setURL("/review/review_recommendation.php");
        myVolleyConnection.addParams("review_board_id", review_board_id);
        myVolleyConnection.addParams("client_id", LoginSharedPref.getUserId(aContext));
        myVolleyConnection.setVolley(new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "response_set_follow_btn: "+response);
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");

                    if(success.equals("false")) {
                        String fail_reason = jsonObject.getString("reason");
                        Toast.makeText(aContext, fail_reason, Toast.LENGTH_LONG).show();

                    } else {
                        Toast.makeText(aContext, "추천하였습니다", Toast.LENGTH_SHORT).show();
                        review_info.setRecommendCount(review_info.getRecommendCount() + 1);
                        tv_recommendation_count.setText("추천 "+Integer.toString(review_info.getRecommendCount()) + " 개");
                    }


                } catch (JSONException e) {
                    Log.d(TAG, "JSONException: ");
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        myVolleyConnection.requestVolley();
    }

    private void set_recommendation_cancel_btn () {
        MyVolleyConnection myVolleyConnection = new MyVolleyConnection(1, aContext);
        myVolleyConnection.setURL("/review/review_recommendation_cancel.php");
        myVolleyConnection.addParams("review_board_id", review_board_id);
        myVolleyConnection.addParams("client_id", LoginSharedPref.getUserId(aContext));
        myVolleyConnection.setVolley(new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "response_set_follow_btn: "+response);
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");

                    if(success.equals("false")) {
                        String fail_reason = jsonObject.getString("reason");
                        Toast.makeText(aContext, fail_reason, Toast.LENGTH_LONG).show();

                    } else {
                        Toast.makeText(aContext, "추천 취소하였습니다", Toast.LENGTH_SHORT).show();
                        review_info.setRecommendCount(review_info.getRecommendCount() - 1);
                        tv_recommendation_count.setText("추천 "+Integer.toString(review_info.getRecommendCount()) + " 개");
                    }

                } catch (JSONException e) {
                    Log.d(TAG, "JSONException: ");
                }

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
        myVolleyConnection.addParams("following_id", review_info.getUser_info().getUser_id());
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
                Toast.makeText(aContext, review_info.getUser_info().getUser_nickname()+"님과 팔로우 되었습니다.", Toast.LENGTH_SHORT).show();
                review_info.setFollowing(true);
                tv_follow.setVisibility(View.INVISIBLE);
            }


        } catch (JSONException e) {
            Log.d(TAG, "JSONException: ");
        }
    }

    private void set_follow_cancel_popup() {
        MyVolleyConnection myVolleyConnection = new MyVolleyConnection(1, aContext);
        myVolleyConnection.setURL("/review/review_follow_cancel.php");
        myVolleyConnection.addParams("user_id", LoginSharedPref.getUserId(aContext));
        myVolleyConnection.addParams("following_id", review_info.getUser_info().getUser_id());
        myVolleyConnection.setVolley(new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                response_set_follow_cancel_popup(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        myVolleyConnection.requestVolley();
    }

    private void response_set_follow_cancel_popup(String response) {
        Log.d(TAG, "response_set_follow_btn: "+response);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(response);
            String success = jsonObject.getString("success");

            if(success.equals("false")) {
                String fail_reason = jsonObject.getString("reason");
                Toast.makeText(aContext, fail_reason, Toast.LENGTH_LONG).show();

            } else {
                Toast.makeText(aContext, review_info.getUser_info().getUser_nickname()+"님과 팔로잉이 취소되었습니다.", Toast.LENGTH_SHORT).show();
                review_info.setFollowing(false); // 해답은 이거.
                tv_follow.setVisibility(View.VISIBLE);
            }


        } catch (JSONException e) {
            Log.d(TAG, "JSONException: ");
        }
    }


}