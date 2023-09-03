package com.homework.book_sns.search;

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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.homework.book_sns.R;
import com.homework.book_sns.javaclass.Follow_For_RCYV;
import com.homework.book_sns.javaclass.Group_info;
import com.homework.book_sns.javaclass.LoginSharedPref;
import com.homework.book_sns.javaclass.MyVolleyConnection;
import com.homework.book_sns.rcyv_adapter.Adt_af_follow_people;
import com.homework.book_sns.rcyv_adapter.Adt_fg_group_list;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragment_search_group#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment_search_group extends Fragment {

    private String TAG = "hch";
    private static String FRAGMENT_NAME = "fragment_search_people";
    private static String FRAGMENT_FUNCTION = "search";
    private static Activity activity;

    //뷰 객체들
    TextView tv_no_group; // 검색어가 존재하지 않을때 띄어주는 텍스트뷰
    NestedScrollView nestedScrollView;
    ProgressBar progressBar;
    RecyclerView rcyv_group;

    //각종 객체들
    Adt_fg_group_list adt_fg_group_list;
    String search_keyword; // 검색어

    int page = 1, limit = 10; // 1페이지에 2개씩 데이터를 불러온다

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public fragment_search_group() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragment_search_group.
     */
    // TODO: Rename and change types and number of parameters
    public static fragment_search_group newInstance(String param1, String param2) {
        fragment_search_group fragment = new fragment_search_group();
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
        return inflater.inflate(R.layout.fragment_search_group, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        set_fragment(view);
    }

    @Override
    public void onPause() {
        super.onPause();
        page = 1;
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
        nestedScrollView = rootView.findViewById(R.id.nscrv_fsr_group);
        rcyv_group = rootView.findViewById(R.id.rcyv_fsr_group);
        progressBar= rootView.findViewById(R.id.pgb_fsr_group);
        tv_no_group = rootView.findViewById(R.id.tv_fsr_no_group);
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
                    load_search_group();
                }
            }
        });
    }

    // onCreateView 시, view의 데이터를 초기화해주는 메서드
    private void my_initSetView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        rcyv_group.setLayoutManager(linearLayoutManager);

        adt_fg_group_list = new Adt_fg_group_list();
        rcyv_group.setAdapter(adt_fg_group_list);

        load_search_group(); // 리뷰를 검색한다.
    }

    public void load_search_group() {

        MyVolleyConnection myVolleyConnection = new MyVolleyConnection(1, activity);
        myVolleyConnection.setURL("/search/search_group.php");
        myVolleyConnection.addParams("client_id", LoginSharedPref.getUserId(activity));
        myVolleyConnection.addParams("search_keyword", search_keyword);
        myVolleyConnection.addParams("page", String.valueOf(page));
        myVolleyConnection.addParams("limit", String.valueOf(limit));

        myVolleyConnection.setVolley(new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                load_response_search_group(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        myVolleyConnection.requestVolley();
    }

    private void load_response_search_group(String response) {
        log_fragment(response);
        JSONObject entryJsonObject = null;
        try {
            entryJsonObject = new JSONObject(response);
            String success = entryJsonObject.getString("success");

            if(success.equals("false")) {
                String fail_reason = entryJsonObject.getString("reason");
            } else {

                JSONArray jsonDataArray = entryJsonObject.getJSONArray("data");

                for(int i =0; i< jsonDataArray.length(); i++) {

                    JSONObject jsonDataObject = jsonDataArray.getJSONObject(i);

                    int id = jsonDataObject.getInt("group_id");
                    String name = jsonDataObject.getString("group_name");
                    String category  = jsonDataObject.getString("group_category");
                    String explain = jsonDataObject.getString("group_explain");
                    String image = jsonDataObject.getString("group_image");

                    Group_info group_info = new Group_info(id, name, category, explain, image);
                    int isMember = jsonDataObject.getInt("member_status_ofClient");

                    if(isMember == 0) {
                        group_info.setMember(false);
                        group_info.setLeader(false);
                    } else {
                        int isLeader = jsonDataObject.getInt("leader_status_ofClient");
                        group_info.setMember(true);
                        group_info.setLeader(isLeader != 0);
                    }

                    int isApply = jsonDataObject.getInt("isApply");
                    if(isApply != 0) {
                        group_info.setApply(true);
                    } else {
                        group_info.setApply(false);
                    }

                    adt_fg_group_list.addItem(group_info);
                }
                adt_fg_group_list.notifyDataSetChanged();
            }
            progressBar.setVisibility(View.GONE);

        } catch (JSONException e) {
            Log.d(TAG, "JSONException: "+e);
        }
    }

    //재검색시, 액티비티에서 호출되는 메서드
    public void rebrowsing_group(String search_keyword) {
        this.search_keyword = search_keyword; // 검색어 저장
        adt_fg_group_list.cleatItem(); // 리싸이클러뷰 초기화
        page = 1; // 페이지 초기화
        load_search_group(); // 데이터 요청
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