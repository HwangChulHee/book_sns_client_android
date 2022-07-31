package com.homework.book_sns;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
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
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.material.tabs.TabLayout;
import com.homework.book_sns.javaclass.Group_info;
import com.homework.book_sns.javaclass.LoginSharedPref;
import com.homework.book_sns.javaclass.MyVolleyConnection;
import com.homework.book_sns.rcyv_adapter.Adt_fg_group_list;
import com.homework.book_sns.rcyv_adapter.Adt_fr_review_simple;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragment_group#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment_group extends Fragment {

    /* --------------------------- */
    // 액티비티 필수 객체들
    Context aContext;
    String TAG = "hch";
    /* --------------------------- */

    /* --------------------------- */
    // xml의 view 객체들

    TabLayout tl_group_type;
    RecyclerView rcyv_group_list;

    /* --------------------------- */

    /* --------------------------- */
    // 각종 객체들
    Adt_fg_group_list adt_fg_group_list;
    String group_type = "my";

    /* --------------------------- */

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public fragment_group() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragment_group.
     */
    // TODO: Rename and change types and number of parameters
    public static fragment_group newInstance(String param1, String param2) {
        fragment_group fragment = new fragment_group();
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
    public void onResume() {
        super.onResume();
        request_loadGroupData(group_type);
        Log.d(TAG, "onResume: "+group_type);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_group, container, false);
        myInit(view);

        return view;
    }

    private void myInit(View rootView) {
        aContext = getActivity();

        setHasOptionsMenu(true);
        myFindView(rootView);
        mySetClickView();
    }


    private void myFindView(View rootView) {
        tl_group_type = rootView.findViewById(R.id.tl_fr_group_type);
        rcyv_group_list = rootView.findViewById(R.id.rcyv_fg_group_list);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(aContext);
        rcyv_group_list.setLayoutManager(linearLayoutManager);

        adt_fg_group_list = new Adt_fg_group_list();
        rcyv_group_list.setAdapter(adt_fg_group_list);

    }

    private void mySetClickView() {
        tl_group_type.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                if(tab.getPosition() == 0) {
                    request_loadGroupData("my");
                    group_type = "my";
                } else if(tab.getPosition() == 1) {
                    request_loadGroupData("entry");
                    group_type = "entry";
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void request_loadGroupData(String group_type) {
        adt_fg_group_list.cleatItem();
        adt_fg_group_list.notifyDataSetChanged();

        MyVolleyConnection myVolleyConnection = new MyVolleyConnection(1, aContext);
        myVolleyConnection.setURL("/group/group_load_list.php");
        myVolleyConnection.addParams("client_id", LoginSharedPref.getUserId(aContext));
        myVolleyConnection.addParams("group_type", group_type);
        myVolleyConnection.setVolley(new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                response_loadGroupData(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        myVolleyConnection.requestVolley();
    }

    private void response_loadGroupData(String response) {
        Log.d(TAG, "response_loadGroupData: "+response);
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

                    if(group_type == "entry") {
                        int isApply = jsonDataObject.getInt("isApply");
                        if(isApply != 0) {
                            group_info.setApply(true);
                        } else {
                            group_info.setApply(false);
                        }
                    }


                    adt_fg_group_list.addItem(group_info);
                }
                adt_fg_group_list.notifyDataSetChanged();
            }

        } catch (JSONException e) {
            Log.d(TAG, "JSONException: "+e);
        }
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_group, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int curId = item.getItemId();
        switch (curId) {
            case R.id.menu_make_group:
                Intent intent = new Intent(getActivity(),
                        com.homework.book_sns.act_group.activity_group_make.class);
                startActivity(intent);
                break;

        }
        return super.onOptionsItemSelected(item);
    }
}