package com.homework.book_sns.search;
import android.content.Context;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.homework.book_sns.R;
import com.homework.book_sns.javaclass.Book_info;
import com.homework.book_sns.javaclass.LoginSharedPref;
import com.homework.book_sns.javaclass.MyVolleyConnection;
import com.homework.book_sns.javaclass.Noti_info;
import com.homework.book_sns.javaclass.Review_list_simple_info;
import com.homework.book_sns.javaclass.User_info;
import com.homework.book_sns.rcyv_adapter.Adt_fr_review_simple;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragment_search_review#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment_search_review extends Fragment {

   private String TAG = "hch";
   private static String FRAGMENT_NAME = "fragment_search_review";
   private static String FRAGMENT_FUNCTION = "search";
   private static Activity activity;

   //뷰 객체들
    TextView tv_no_review; // 검색어가 존재하지 않을때 띄어주는 텍스트뷰
    NestedScrollView nestedScrollView;
    ProgressBar progressBar;
    RecyclerView rcyv_review;

   //각종 객체들
    Adt_fr_review_simple adt_fr_review_simple; // 검색 결과를 보여주는 리싸이클러뷰
    String search_keyword; // 검색어

    int page = 1, limit = 2; // 1페이지에 2개씩 데이터를 불러온다

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public fragment_search_review() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragment_search_review.
     */
    // TODO: Rename and change types and number of parameters
    public static fragment_search_review newInstance(String param1, String param2) {
        fragment_search_review fragment = new fragment_search_review();
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
    public void onPause() {
        page = 1;
        super.onPause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_search_review, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        set_fragment(view);
    }

    //onCreateView시, 초기화 해주는 역할을 하는 메서드
    private void set_fragment(View rootView) {
        setObject(); //onCreateView 시, 필수 객체 초기화 해주는 메소드
        my_findView(rootView); //onCreateView 시, findViewById() 해주는 메소드
        my_setClickEvent(); // onCreateView 시, click 연관 이벤트를 처리해주는 메서드
        my_initSetView(); // onCreateView 시, view의 데이터를 초기화해주는 메서드
    }

    //onCreateView 시, 필수 객체 초기화 해주는 메소드
    private void setObject() {
        activity = getActivity();
        search_keyword = this.getArguments().getString("search_keyword"); // 프래그먼트에서 데이터를 받는 메서드.
    }

    //oncreate 시, findViewById() 해주는 메소드
    private void my_findView(View rootView) {
        nestedScrollView = rootView.findViewById(R.id.nscrv_fsr_review);
        rcyv_review = rootView.findViewById(R.id.rcyv_fsr_review);
        progressBar= rootView.findViewById(R.id.pgb_fsr_review);
        tv_no_review = rootView.findViewById(R.id.tv_fsr_no_review);
    }

    // onCreateView 시, click 연관 이벤트를 처리해주는 메서드
    private void my_setClickEvent() {

        // 페이징 관련 nestedScrollview 처리
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())
                {
                    page++;
                    progressBar.setVisibility(View.VISIBLE);
                    load_search_review();
                }
            }
        });
    }

    // onCreateView 시, view의 데이터를 초기화해주는 메서드
    private void my_initSetView() {
         LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
         rcyv_review.setLayoutManager(linearLayoutManager);

         adt_fr_review_simple = new Adt_fr_review_simple();
         rcyv_review.setAdapter(adt_fr_review_simple);

        load_search_review(); // 리뷰를 검색한다.
    }

    public void load_search_review() {

        //volley를 통한 HTTP 통신
        MyVolleyConnection myVolleyConnection = new MyVolleyConnection(1, activity);
        myVolleyConnection.setURL("/search/search_review.php"); // 통신할 URL 설정
        myVolleyConnection.addParams("object_person_id", LoginSharedPref.getUserId(activity));
        myVolleyConnection.addParams("client_id", LoginSharedPref.getUserId(activity));
        myVolleyConnection.addParams("page", String.valueOf(page));
        myVolleyConnection.addParams("limit", String.valueOf(limit));
        myVolleyConnection.addParams("search_keyword", search_keyword);
        
        //응답시, 동작할 code 작성
        myVolleyConnection.setVolley(new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                load_response_search_review(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                
            }
        });
        myVolleyConnection.requestVolley(); // 최종 요청
    }

    private void load_response_search_review(String response) {
        JSONObject entryJsonObject = null;
        JSONArray jsonArray = null;

        try{
            entryJsonObject = new JSONObject(response);
            String success = entryJsonObject.getString("success");

            if(success.equals("false")){
                String fail_reason = entryJsonObject.getString("reason");
                toast_fragment("false");
            }else{
                progressBar.setVisibility(View.GONE);

                JSONArray jsonArray1 = entryJsonObject.getJSONArray("data");

                //검색결과가 있느냐 없으냐에 따라서 검색결과가 없다는 textView를 활성화 / 비활성화 시켜준다.
                if(jsonArray1.length() == 0 && adt_fr_review_simple.getItemSize() == 0) {
                    tv_no_review.setVisibility(View.VISIBLE);
                } else {
                    tv_no_review.setVisibility(View.GONE);
                }

                for (int i = 0; i < jsonArray1.length(); i++){

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

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    //재검색시, 액티비티에서 호출되는 메서드
   public void rebrowsing_review(String search_keyword) {
        this.search_keyword = search_keyword; // 검색어 저장
        adt_fr_review_simple.clearItem(); // 리싸이클러뷰 초기화
        page = 1; // 페이지 초기화
        load_search_review(); // 데이터 요청
    }



    //로그 찍는 메서드
    private void log_fragment(String msg) {
        Log.d(TAG, "프래그먼트 이름: "+FRAGMENT_NAME +", 프래그먼트 기능 : "+FRAGMENT_FUNCTION
                +", 로그 내용 : "+msg);
    }

    //toast 찍는 메서드
    private void toast_fragment(String msg) {
        Toast.makeText(activity, msg ,Toast.LENGTH_SHORT).show();
    }
}