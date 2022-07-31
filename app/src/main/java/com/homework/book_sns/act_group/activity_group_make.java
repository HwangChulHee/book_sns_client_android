package com.homework.book_sns.act_group;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.homework.book_sns.R;
import com.homework.book_sns.act_review.activity_review_create_001_direct;
import com.homework.book_sns.javaclass.LoginSharedPref;
import com.homework.book_sns.javaclass.MyBasicFunc;
import com.homework.book_sns.javaclass.MyImageFunc;
import com.homework.book_sns.javaclass.MyVolleyConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class activity_group_make extends AppCompatActivity {

    /* --------------------------- */
    // 액티비티 필수 객체들
    Context aContext;
    String TAG = "hch";
    /* --------------------------- */


    /* --------------------------- */
    // xml의 view 객체들
    Button btn_back;
    TextView tv_complete;

    ImageView iv_group_image;
    Spinner sp_book_category;
    EditText etv_group_name;
    EditText etv_group_explain;
    /* --------------------------- */


    /* --------------------------- */
    // 각종 객체들
    ArrayAdapter<String> book_category_adapter;
    int category_num = 1;
    String[] items;

    /* --------------------------- */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_make);

        myInit();
    }

    private void myInit() {
        aContext = this;
        ActionBar bar = getSupportActionBar();
        bar.hide();

        myInitView();
    }

    private void myInitView() {
        myFindView();
        mySetClickView();
    }

    private void myFindView() {
        btn_back = findViewById(R.id.btn_agm_back);
        tv_complete = findViewById(R.id.tv_agm_complete);

        iv_group_image = findViewById(R.id.iv_agm_group_image);
        sp_book_category = findViewById(R.id.sp_agm_book_category);

        items = getResources().getStringArray(R.array.book_category);
        book_category_adapter = new ArrayAdapter<String>
                (aContext, R.layout.support_simple_spinner_dropdown_item
                ,items);
        sp_book_category.setAdapter(book_category_adapter);

        etv_group_name = findViewById(R.id.etv_agm_name);
        etv_group_explain = findViewById(R.id.etv_agm_explain);
    }

    private void mySetClickView() {

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.btn_agm_back:
                        finish();
                        break;

                    case R.id.tv_agm_complete:
                        String group_name = etv_group_name.getText().toString();
                        String group_explain = etv_group_explain.getText().toString();
                        request_make_group(items[category_num], group_name, group_explain);
                        break;

                    case R.id.iv_agm_group_image:
                        PopupMenu popupMenu = new PopupMenu(aContext, view);
                        popupMenu.getMenuInflater().inflate(R.menu.menu_popup_rc1_direct_image, popupMenu.getMenu());
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                Log.d(TAG, "onMenuItemClick: ");
                                switch (menuItem.getItemId()) {
                                    case R.id.menu_rc1_direct_1:
                                        Intent intent1 = MyImageFunc.take_picture(aContext, activity_group_make.this);
                                        startActivityForResult(intent1, 1);
                                        return true;
                                    case R.id.menu_rc1_direct_2:
                                        Intent intent2 = MyImageFunc.upload_single_photo();
                                        startActivityForResult(intent2, 2);
                                        return true;

                                }
                                return false;
                            }
                        });
                        popupMenu.show();

                        break;
                }

            }
        };

        btn_back.setOnClickListener(listener);
        tv_complete.setOnClickListener(listener);
        iv_group_image.setOnClickListener(listener);

        sp_book_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                category_num = i;
//                MyBasicFunc.showToast(aContext, items[i]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void request_make_group(String category, String group_name, String group_explain) {

        if(group_name.length() == 0) {
            MyBasicFunc.showToast(aContext, "모임명을 적어주세요.");
            return;
        } else if(group_explain.length() == 0) {
            MyBasicFunc.showToast(aContext, "모임 설명을 적어주세요.");
            return;
        }

        BitmapDrawable drawable = (BitmapDrawable) iv_group_image.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        String image_base64 = MyImageFunc.getBase64Image_FromBitmap(bitmap);

        MyVolleyConnection myVolleyConnection = new MyVolleyConnection(1, aContext);
        myVolleyConnection.setURL("/group/group_make.php");
        myVolleyConnection.addParams("client_id", LoginSharedPref.getUserId(aContext));
        myVolleyConnection.addParams("image", image_base64);
        myVolleyConnection.addParams("category", category);
        myVolleyConnection.addParams("group_name", group_name);
        myVolleyConnection.addParams("group_explain", group_explain);

        myVolleyConnection.setVolley(new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                response_make_group(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        myVolleyConnection.requestVolley();
    }

    private void response_make_group(String response) {
        Log.d(TAG, "response_make_group: "+response);
        JSONObject entryJsonObject = null;
        try {
            entryJsonObject = new JSONObject(response);
            String success = entryJsonObject.getString("success");

            if(success.equals("false")) {
                String fail_reason = entryJsonObject.getString("reason");
                Toast.makeText(aContext, fail_reason, Toast.LENGTH_LONG).show();

            } else {
                int group_id = entryJsonObject.getInt("group_id");
                Intent intent = new Intent(aContext, activity_group_page.class);
                intent.putExtra("group_id", group_id);
                startActivity(intent);
                finish();
            }

        } catch (JSONException e) {
            Log.d(TAG, "JSONException: "+e);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK) {
            if(requestCode == 1) {
                Uri uri = MyImageFunc.take_picture_result();
                iv_group_image.setImageURI(uri);

            } else if(requestCode == 2) {
                Uri uri = MyImageFunc.result_single_photo(data);
                iv_group_image.setImageURI(uri);
            }
        }

    }
}