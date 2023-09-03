package com.homework.book_sns;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.material.tabs.TabLayout;
import com.homework.book_sns.javaclass.Book_info;
import com.homework.book_sns.javaclass.LoginSharedPref;
import com.homework.book_sns.javaclass.MyVolleyConnection;
import com.homework.book_sns.javaclass.Review_list_simple_info;
import com.homework.book_sns.javaclass.User_info;
import com.homework.book_sns.rcyv_adapter.Adt_fr_review_simple;
import com.homework.book_sns.search.activity_search_001;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragment_review#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment_review extends Fragment {

    /* --------------------------- */
    // 액티비티 필수 객체들
    Context aContext;
    String TAG = "hch";
    /* --------------------------- */

    /* --------------------------- */
    // xml의 view 객체들

    TabLayout tl_review_type;

    /* --------------------------- */

    /* --------------------------- */
    // 각종 객체들
    Spinner sp_review_sort;

    NestedScrollView nestedScrollView;
    RecyclerView rcyv_review;
    ProgressBar progressBar;

    // 1페이지에 10개씩 데이터를 불러온다
    int page = 1, limit = 2;

    Adt_fr_review_simple adt_fr_review_simple;
    int tabPosition;
    String review_sort = "recent";

    /* --------------------------- */


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public fragment_review() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragment_view.
     */
    // TODO: Rename and change types and number of parameters
    public static fragment_review newInstance(String param1, String param2) {
        fragment_review fragment = new fragment_review();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_review, container, false);

        aContext = getContext();

        setObject();
        setView(view);
        setHasOptionsMenu(true);


        return view;

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");

        if(tabPosition == 0) {
            loadReviewData("following", review_sort);
        } else if (tabPosition == 1) {
            loadReviewData("entry", review_sort);
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        page = 1;
        adt_fr_review_simple.clearItem();
    }

    private void setObject() {
        tabPosition = 0;
    }

    private void setView(View rootView) {

        tl_review_type = (TabLayout) rootView.findViewById(R.id.tl_fr_review_type);

        sp_review_sort = rootView.findViewById(R.id.home_spinner);

        nestedScrollView = (NestedScrollView) rootView.findViewById(R.id.nsv_fr_review);
        progressBar = (ProgressBar) rootView.findViewById(R.id.pgb_fr_review);
        rcyv_review = (RecyclerView) rootView.findViewById(R.id.rcyv_fr_review_simple);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(aContext);
        rcyv_review.setLayoutManager(linearLayoutManager);

        adt_fr_review_simple = new Adt_fr_review_simple();
        rcyv_review.setAdapter(adt_fr_review_simple);


        setClickEvent();
    }



    private void setClickEvent() {
        tl_review_type.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                adt_fr_review_simple.clearItem();
                page = 1;

                if(tab.getPosition() == 0) {
                    loadReviewData("following", review_sort);
                    tabPosition = 0;
                } else if(tab.getPosition() == 1) {
                    loadReviewData("entry", review_sort);
                    tabPosition = 1;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        String[] items = {"최신순", "추천순"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getActivity(), android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        sp_review_sort.setAdapter(adapter);
        sp_review_sort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                Toast.makeText(getApplicationContext(), items[i], Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        adt_fr_review_simple.setOnItemClickListener(new Adt_fr_review_simple.OnItemClickListener() {
            @Override
            public void onReviewOptionClick(View v, int pos) {

            }

            @Override
            public void onFollow(View v, int pos) {

            }

            @Override
            public void onFollowCancel(View v, int pos) {

            }
        });

        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                Log.d(TAG, "onScrollChange 1: "+scrollY);
                Log.d(TAG, "onScrollChange 2: "+v.getChildAt(0).getMeasuredHeight());
                Log.d(TAG, "onScrollChange 3: "+v.getMeasuredHeight());
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())
                {
                    Log.d(TAG, "onScrollChange: 페이징 처리");
                    page++;
                    progressBar.setVisibility(View.VISIBLE);

                    if(tabPosition == 0) {
                        loadReviewData("following", review_sort);
                    } else if(tabPosition == 1) {
                        loadReviewData("entry", review_sort);
                    }
                }

            }
        });
    }

    private void loadReviewData(String review_type, String review_sort) { // recent (최근) 또는 popularity (인기) 순
        MyVolleyConnection myVolleyConnection = new MyVolleyConnection(1, aContext);
        myVolleyConnection.setURL("/review/review_read_simple_list.php");
        myVolleyConnection.addParams("review_page", "normal");
        myVolleyConnection.addParams("review_type", review_type);
        myVolleyConnection.addParams("review_sort", review_sort);
        myVolleyConnection.addParams("object_person_id", LoginSharedPref.getUserId(aContext));
        myVolleyConnection.addParams("client_id", LoginSharedPref.getUserId(aContext));
        myVolleyConnection.addParams("page", String.valueOf(page));
        myVolleyConnection.addParams("limit", String.valueOf(limit));

        myVolleyConnection.setVolley(new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                responseLoadReviewData(response, review_type);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        myVolleyConnection.requestVolley();
    }

    private void responseLoadReviewData(String response, String review_type) {

        Log.d(TAG, "responseLoadReviewData: "+response);


        JSONObject entryJsonObject = null;
        JSONArray jsonArray = null;
        try {
            entryJsonObject = new JSONObject(response);
            String success = entryJsonObject.getString("success");

            if(success.equals("false")) {
                String fail_reason = entryJsonObject.getString("reason");
                Toast.makeText(aContext, fail_reason, Toast.LENGTH_LONG).show();

            } else {

                progressBar.setVisibility(View.GONE);

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
                    } else {
                        review_list_simple_info.setFollowing(false);
                    }


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


                    adt_fr_review_simple.addItem(review_list_simple_info);
                }
                adt_fr_review_simple.notifyDataSetChanged();


            }


        } catch (JSONException e) {
            Log.d(TAG, "JSONException: "+e);
        }
        
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int curId = item.getItemId();
        switch (curId) {
            case R.id.menu_search:
                Intent sIntent = new Intent(getActivity(),
                        activity_search_001.class);
                startActivity(sIntent);
                break;
            case R.id.menu_chatting:
                Intent intent = new Intent(getActivity(),
                        com.homework.book_sns.act_chatting.activity_chatting_list.class);
                startActivity(intent);
                break;

        }
        return super.onOptionsItemSelected(item);
    }


}