package com.homework.book_sns;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragment_mypage#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment_mypage extends Fragment {

    String TAG = "hch";
    String IP = "15.164.105.239";
    Context aContext;

    /* --------------------------- */
    // xml의 view 객체들
    CircleImageView civ_profile;
    TextView tv_nickname;
    Button btn_option;

    LinearLayout ll_follower;
    LinearLayout ll_following;
    TextView tv_follower_count;
    TextView tv_following_count;

    RadioGroup rg_review_sort;

    /* --------------------------- */

    SignInfo signInfo;

    RecyclerView rcyv_my_review;
    Adt_fr_review_simple adt_fr_mypage_simple;

    String review_sort = "recent";
    /* --------------------------- */




    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public fragment_mypage() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragment_mypage.
     */
    // TODO: Rename and change types and number of parameters
    public static fragment_mypage newInstance(String param1, String param2) {
        fragment_mypage fragment = new fragment_mypage();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        aContext = getActivity();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mypage, container, false);
        setView(view);
        
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        String prefNickname = LoginSharedPref.getPrefNickname(getActivity());
        if (!tv_nickname.getText().toString().equals(prefNickname)) {
            tv_nickname.setText(prefNickname);
        }

        if(!signInfo.getImg().equals(LoginSharedPref.getPrefProfilePhoto(getActivity()))) {
            String image_url = LoginSharedPref.getPrefProfilePhoto(getActivity());
            Glide.with(getActivity()).load(image_url).error(R.drawable.ic_baseline_error_24).into(civ_profile);
        }

        loadData(review_sort);

        Log.d(TAG, "onResume: ");
    }

    @Override
    public void onPause() {
        super.onPause();
        adt_fr_mypage_simple.clearItem();
    }

    private void setView(View rootView) {

        civ_profile = rootView.findViewById(R.id.civ_frag_mypage_profile_photo);
        tv_nickname = rootView.findViewById(R.id.tv_frag_mypage_nickname);
        btn_option = rootView.findViewById(R.id.btn_frag_mypage_option);

        ll_follower = rootView.findViewById(R.id.ll_fr_follower);
        ll_following = rootView.findViewById(R.id.ll_fr_following);
        tv_follower_count = rootView.findViewById(R.id.tv_fm_follower_count);
        tv_following_count = rootView.findViewById(R.id.tv_fm_following_count);

        Log.d(TAG, "setView: "+LoginSharedPref.getPrefProfilePhoto(getActivity()));
        signInfo = new SignInfo();
        signInfo.setImg(LoginSharedPref.getPrefProfilePhoto(getActivity()));
        String image_url = LoginSharedPref.getPrefProfilePhoto(getActivity());
        Log.d(TAG, "setView: "+image_url);
        Glide.with(getActivity()).load(image_url).error(R.drawable.ic_baseline_error_24).into(civ_profile);
        tv_nickname.setText(LoginSharedPref.getPrefNickname(getActivity()));

        rg_review_sort = rootView.findViewById(R.id.rg_frm_review_sort);

        rcyv_my_review = (RecyclerView) rootView.findViewById(R.id.rcyv_fr_mypage);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(aContext);
        rcyv_my_review.setLayoutManager(linearLayoutManager);

        adt_fr_mypage_simple = new Adt_fr_review_simple();
        rcyv_my_review.setAdapter(adt_fr_mypage_simple);


        setClickEvent();
    }

    private void setClickEvent() {
        btn_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: ");
                PopupMenu popupMenu = new PopupMenu(getActivity(), view);
                popupMenu.getMenuInflater().inflate(R.menu.mypage_popup, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        Log.d(TAG, "onMenuItemClick: ");
                        switch (menuItem.getItemId()) {
                            case R.id.mypage_menu1:
//                                Toast.makeText(getActivity(), "북마크", Toast.LENGTH_LONG).show();
                                return true;
                            case R.id.mypage_menu2:
//                                Toast.makeText(getActivity(), "환경설정", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getActivity(), activity_mypage_setting.class);
                                startActivity(intent);
                                return true;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });

        ll_follower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Follow follow_info = new Follow();
                follow_info.setObject_person_id(LoginSharedPref.getUserId(aContext));
                follow_info.setClient_id(LoginSharedPref.getUserId(aContext));
                follow_info.setUser_nickname(LoginSharedPref.getPrefNickname(aContext));
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
                follow_info.setObject_person_id(LoginSharedPref.getUserId(aContext));
                follow_info.setClient_id(LoginSharedPref.getUserId(aContext));
                follow_info.setUser_nickname(LoginSharedPref.getPrefNickname(aContext));
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
                PopupMenu popupMenu = new PopupMenu(getActivity(), v);
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

            }

            @Override
            public void onFollowCancel(View v, int pos) {

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
        myVolleyConnection.addParams("user_id", LoginSharedPref.getUserId(aContext));

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
        myVolleyConnection.setURL("/review/review_read_mypage_simple_list.php");
        myVolleyConnection.addParams("user_id", LoginSharedPref.getUserId(aContext));
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

                    String title = jsonObject.getString("title");
                    String author = jsonObject.getString("author");
                    String publisher = jsonObject.getString("publisher");
                    String cover = jsonObject.getString("cover");

                    String review_id = jsonObject.getString("review_id");
                    String register_date = jsonObject.getString("register_date");
                    String recommendation_count = jsonObject.getString("recommendation_count");
                    String reply_count = jsonObject.getString("reply_count");
                    String content = jsonObject.getString("content");

                    JSONArray imageArray = jsonObject.getJSONArray("review_images");
                    for(int j=0; j<imageArray.length(); j++) {

                        String image = imageArray.getString(j);
                        review_images.add(image);
                    }

                    String isClient_recommendation = jsonObject.getString("isClient_recommendation");
                    if(isClient_recommendation.equals("true")) {
                        review_list_simple_info.setClient_recommendation(true);
                    } else if(isClient_recommendation.equals("false")) {
                        review_list_simple_info.setClient_recommendation(false);
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
                    review_list_simple_info.setRecommendCount(Integer.parseInt(recommendation_count));
                    review_list_simple_info.setReplyCount(Integer.parseInt(reply_count));
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



}