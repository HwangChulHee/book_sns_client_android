package com.homework.book_sns.act_group;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.homework.book_sns.R;
import com.homework.book_sns.javaclass.Group_info;
import com.homework.book_sns.javaclass.LoginSharedPref;
import com.homework.book_sns.javaclass.MyVolleyConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;

public class activity_group_page extends AppCompatActivity {

    /* --------------------------- */
    // 액티비티 필수 객체들
    Context aContext;
    String TAG = "hch";
    /* --------------------------- */


    /* --------------------------- */
    // xml의 view 객체들
    Button btn_back;
    TextView tv_group_name;
    Button btn_group_setting;

    CircleImageView civ_group_image;
    TextView tv_group_category;
    TextView tv_group_explain;

    Button btn_board_write;
    Button btn_group_chatting;

    RecyclerView rcyv_group_board;

    /* --------------------------- */


    /* --------------------------- */
    // 각종 객체들
    ArrayAdapter<String> book_category_adapter;
    int category_num = 1;
    String[] items;

    int group_id;
    Group_info group_info;

    /* --------------------------- */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_page);
        
        myInit();
    }

    private void myInit() {
        aContext = this;
        ActionBar bar = getSupportActionBar();
        bar.hide();

        myGetIntent();
        myInitView();
    }

    private void myGetIntent() {
        Intent intent = getIntent();
        group_id = intent.getIntExtra("group_id", 0);
    }

    private void myInitView() {
        myFindView();
        request_loadGroupInfo();
    }


    private void myFindView() {
        btn_back = findViewById(R.id.btn_agp_back);
        tv_group_name = findViewById(R.id.tv_agp_group_name);
        btn_group_setting = findViewById(R.id.btn_agp_group_setting);

        civ_group_image = findViewById(R.id.civ_agp_group_image);
        tv_group_category = findViewById(R.id.tv_agp_group_category);
        tv_group_explain = findViewById(R.id.tv_agp_group_explain);

        btn_board_write = findViewById(R.id.btn_agp_write_board);
        btn_group_chatting = findViewById(R.id.btn_agp_group_chatting);
    }

    private void request_loadGroupInfo() {
        MyVolleyConnection myVolleyConnection = new MyVolleyConnection(1, aContext);
        myVolleyConnection.setURL("/group/group_load_info.php");
        myVolleyConnection.addParams("client_id", LoginSharedPref.getUserId(aContext));
        myVolleyConnection.addParams("group_id", Integer.toString(group_id));
        myVolleyConnection.setVolley(new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                response_loadGroupInfo(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        myVolleyConnection.requestVolley();
    }

    private void response_loadGroupInfo(String response) {
//        Log.d(TAG, "response_loadGroupInfo: "+response);

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

                    String name = jsonDataObject.getString("group_name");
                    String category  = jsonDataObject.getString("group_category");
                    String explain = jsonDataObject.getString("group_explain");
                    String image = jsonDataObject.getString("group_image");

                    group_info = new Group_info(group_id, name, category, explain, image);

                    int isMember = jsonDataObject.getInt("member_status_ofClient");
                    if(isMember == 0) {
                        group_info.setMember(false);
                        group_info.setLeader(false);
                        btn_group_setting.setVisibility(View.INVISIBLE);
                    } else {
                        int isLeader = jsonDataObject.getInt("leader_status_ofClient");
                        group_info.setMember(true);
                        group_info.setLeader(isLeader != 0);
                    }

                }
                mySetClickView();
                myLoadDateView();
            }

        } catch (JSONException e) {
            Log.d(TAG, "JSONException: "+e);
        }

    }


    private void mySetClickView() {

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.btn_agp_back:
                        finish();
                        break;

                    case R.id.btn_agp_group_setting:
                        setGroupSetting(view);
                        break;

                    case R.id.btn_agp_write_board:
                        break;

                    case R.id.btn_agp_group_chatting:
                        Intent intent = new Intent(aContext, com.homework.book_sns.act_chatting.activity_chatting_room.class);
                        intent.putExtra("from", "group_page");
                        intent.putExtra("group_id", group_id);
                        intent.putExtra("group_name", group_info.getGroup_name());
                        startActivity(intent);
                        break;
                }

            }
        };

        btn_back.setOnClickListener(listener);
        btn_group_setting.setOnClickListener(listener);
        btn_board_write.setOnClickListener(listener);
        btn_group_chatting.setOnClickListener(listener);
    }

    private void setGroupSetting(View view) {
        PopupMenu popupMenu = new PopupMenu(aContext, view);
        if(!group_info.isMember()) {
            return;
        }

        if(group_info.isLeader()) {
            popupMenu.getMenuInflater().inflate(R.menu.menu_group_page_setting_leader, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    Log.d(TAG, "onMenuItemClick: ");
                    switch (menuItem.getItemId()) {
                        case R.id.menu_gs_update:
//                                Toast.makeText(getActivity(), "북마크", Toast.LENGTH_LONG).show();
                            return true;

                        case R.id.menu_gs_remove:
//                                Toast.makeText(getActivity(), "환경설정", Toast.LENGTH_LONG).show();
                            return true;

                        case R.id.menu_gs_member:
                            Intent intent = new Intent(aContext, activity_group_member_management.class);
                            intent.putExtra("group_id", group_id);
                            startActivity(intent);
                            return true;
                    }
                    return false;
                }
            });
        } else {
            popupMenu.getMenuInflater().inflate(R.menu.menu_group_page_setting_member, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    Log.d(TAG, "onMenuItemClick: ");
                    switch (menuItem.getItemId()) {
                        case R.id.menu_gs_getout:
//                                Toast.makeText(getActivity(), "북마크", Toast.LENGTH_LONG).show();
                            return true;

                    }
                    return false;
                }
            });

        }
        popupMenu.show();
    }

    private void myLoadDateView() {
        tv_group_name.setText(group_info.getGroup_name());
        tv_group_category.setText(group_info.getGroup_category());
        tv_group_explain.setText(group_info.getGroup_explain());
        Glide.with(aContext)
                .load(group_info.getGroup_image())
                .error(R.drawable.ic_baseline_error_24)
                .into(civ_group_image);


    }
}