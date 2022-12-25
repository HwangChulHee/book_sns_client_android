package com.homework.book_sns;
import com.google.gson.Gson;

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
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.homework.book_sns.javaclass.LoginSharedPref;
import com.homework.book_sns.javaclass.MyVolleyConnection;
import com.homework.book_sns.javaclass.Noti_info;
import com.homework.book_sns.rcyv_adapter.Adt_fr_review_simple;
import com.homework.book_sns.rcyv_adapter.Adt_rcy_noti;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragment_noti#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment_noti extends Fragment {

    public static Fragment fragment_noti;
    String TAG = "hch";
    Context aContext;

    /* --------------------------- */
    // 각종 객체들s

    NestedScrollView nestedScrollView;
    RecyclerView rcyv_noti;
    ProgressBar progressBar;

    // 1페이지에 10개씩 데이터를 불러온다
    int page = 1, limit = 9;

    Adt_rcy_noti adt_rcy_noti;

    /* --------------------------- */

    private static String FRAGMENT_NAME = "";
    private static String FRAGMENT_FUNCTION = "";

    private void log_fragment(String msg) {
            Log.d(TAG, "프래그먼트 이름: "+FRAGMENT_NAME +", 프래그먼트 기능 : "+FRAGMENT_FUNCTION
            +", 로그 내용 : "+msg);
    }


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public fragment_noti() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragment_noti.
     */
    // TODO: Rename and change types and number of parameters
    public static fragment_noti newInstance(String param1, String param2) {
        fragment_noti fragment = new fragment_noti();
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
        fragment_noti = com.homework.book_sns.fragment_noti.this;
        Log.d(TAG, "fragment_noti onCreateView: ");

        View view = inflater.inflate(R.layout.fragment_noti, container, false);

        aContext = getContext();

        setView(view);
        setHasOptionsMenu(true);
        request_notiData();

        return view;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "fragment_noti onDestroy: ");
        fragment_noti = null;
        super.onDestroy();
    }

    private void setView(View rootView) {

        nestedScrollView = (NestedScrollView) rootView.findViewById(R.id.nscrv_fn);
        progressBar = (ProgressBar) rootView.findViewById(R.id.pgb_fn);
        rcyv_noti = (RecyclerView) rootView.findViewById(R.id.rcyv_fn_noti_list);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(aContext);
        rcyv_noti.setLayoutManager(linearLayoutManager);

        adt_rcy_noti = new Adt_rcy_noti();
        rcyv_noti.setAdapter(adt_rcy_noti);

        setClickEvent();
    }

    private void setClickEvent() {
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())
                {
                    Log.d(TAG, "onScrollChange: 페이징 처리");
                    page++;
                    progressBar.setVisibility(View.VISIBLE);
                    request_notiData();
                }
            }
        });
    }

    private void request_notiData() {
        MyVolleyConnection myVolleyConnection = new MyVolleyConnection(1, aContext);
        myVolleyConnection.setURL("/noti/process_noti_list.php");
        myVolleyConnection.addParams("client_id", LoginSharedPref.getUserId(aContext));
        myVolleyConnection.addParams("page", String.valueOf(page));
        myVolleyConnection.addParams("limit", String.valueOf(limit));

        myVolleyConnection.setVolley(new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
//                Log.d(TAG, "fn onResponse: "+response);
                response_notiData(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        myVolleyConnection.requestVolley();
    }

    private void response_notiData(String response) {
        JSONObject entryJsonObject = null;
        JSONArray jsonArray = null;
//        Log.d(TAG, "fn response_notiData: ");

        try{
            entryJsonObject = new JSONObject(response);
            String success = entryJsonObject.getString("success");
//            Log.d(TAG, "response_notiData: "+entryJsonObject);

            if(success.equals("false")){
                String fail_reason = entryJsonObject.getString("reason");
                Toast.makeText(aContext, fail_reason, Toast.LENGTH_LONG).show();
            }else{
                progressBar.setVisibility(View.GONE);

                Gson gson = new Gson();

                JSONArray jsonArray1 = entryJsonObject.getJSONArray("data");
                for (int i = 0; i < jsonArray1.length(); i++){
                    String jsonText = jsonArray1.getJSONObject(i).toString();
                    Noti_info noti_info = gson.fromJson(jsonText, Noti_info.class);

                    log_fragment("읽음 상태 : "+String.valueOf(noti_info.isRead_status()));
                    adt_rcy_noti.addItem(noti_info);
                }
                adt_rcy_noti.notifyDataSetChanged();
            }

        }catch (Exception e){
            e.printStackTrace();
        }


    }

    public void addNoti(String json_noti_info) {
        Gson gson = new Gson();
        Noti_info noti_info = gson.fromJson(json_noti_info, Noti_info.class);
        adt_rcy_noti.aheadAddItem(noti_info);
        Log.d(TAG, "addNoti: "+noti_info.getUser_profile());
        adt_rcy_noti.notifyDataSetChanged();
    }



    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_noti, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int curId = item.getItemId();
        switch (curId) {
            case R.id.menu_noti_setting:
                break;

        }
        return super.onOptionsItemSelected(item);

    }
}