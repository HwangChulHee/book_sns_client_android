package com.homework.book_sns.act_review;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.homework.book_sns.R;
import com.homework.book_sns.javaclass.Book_info;
import com.homework.book_sns.javaclass.MyVolleyConnection;
import com.homework.book_sns.rcyv_adapter.Adt_rc1_searched_book;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class activity_review_create_001 extends AppCompatActivity {

    public static Activity act_rc1 = null;

    /* --------------------------- */
    // 액티비티 필수 객체들
    Context aContext;
    String TAG = "hch";
    /* --------------------------- */

    /* --------------------------- */
    // xml의 view 객체들
    Button btn_close;

    EditText etv_book_input;
    Button btn_book_search;

    LinearLayout ll_direct_input;
    TextView tv_no_search;
    Button btn_direct_input;

    RecyclerView rcy_book_result;
    /* --------------------------- */

    /* --------------------------- */
    // 각종 객체들
    Book_info book_info;
    Adt_rc1_searched_book adt_book;

    ProgressDialog progressDialog;
    boolean isUpdate = false;
    /* --------------------------- */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_create_001);

        this.aContext = activity_review_create_001.this;
        act_rc1 = activity_review_create_001.this;

        getMyIntent();
        setView();
    }

    private void getMyIntent() {
        Intent intent = getIntent();
        if(intent.getStringExtra("update").equals("true")) {
            isUpdate = true;
        }
    }

    private void setView() {
        ActionBar bar = getSupportActionBar();
        bar.hide();

        btn_close = (Button) findViewById(R.id.btn_review_create_close);

        etv_book_input = (EditText) findViewById(R.id.etv_review_create_book_input);
        btn_book_search = (Button) findViewById(R.id.btn_review_create_search);

        ll_direct_input = (LinearLayout) findViewById(R.id.ll_review_create_direct_input);
        tv_no_search = (TextView) findViewById(R.id.tv_review_create_no_content);
        btn_direct_input = (Button) findViewById(R.id.btn_review_create_direct);

        rcy_book_result = (RecyclerView) findViewById(R.id.rcy_review_create_book_result);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcy_book_result.setLayoutManager(linearLayoutManager);

        adt_book = new Adt_rc1_searched_book();
        adt_book.setOnItemClickListener(new Adt_rc1_searched_book.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                if(!isUpdate) {
                    Intent intent = new Intent(aContext, activity_review_create_002.class);

                    Book_info book_info = adt_book.getItem(pos);
                    intent.putExtra("book_info", book_info);
                    intent.putExtra("inputType", "search");
                    intent.putExtra("intentType", "create");


                    startActivity(intent);
                } else {
                    Intent intent = new Intent();

                    Book_info book_info = adt_book.getItem(pos);
                    intent.putExtra("book_info", book_info);
                    intent.putExtra("inputType", "search");
                    intent.putExtra("intentType", "create");

                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });

        rcy_book_result.setAdapter(adt_book);

        progressDialog = new ProgressDialog(aContext);
        setClickEvent();


        rcy_book_result.setVisibility(View.INVISIBLE);
    }

    private void setClickEvent() {
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        etv_book_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.length() == 0) {
                    ll_direct_input.setVisibility(View.VISIBLE);
                    rcy_book_result.setVisibility(View.INVISIBLE);
                }
            }
        });

        btn_book_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                book_search();
            }
        });

        btn_direct_input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(aContext, activity_review_create_001_direct.class);
                intent.putExtra("update", "false");
                startActivityForResult(intent, 1);
            }
        });
    }

    private void book_search() {
        adt_book.clearItem();
        adt_book.notifyDataSetChanged();

        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etv_book_input.getWindowToken(), 0);

        // 1. 검색어로 알라딘 API 요청

        String search_keyword = etv_book_input.getText().toString();
        if(search_keyword.length() == 0) {
            Toast.makeText(aContext, "검색어를 입력해주세요", Toast.LENGTH_SHORT).show();
            return;
        }

        MyVolleyConnection myVolleyConnection = new MyVolleyConnection(1, aContext);
        myVolleyConnection.setURL("/review/request_aladin_api.php");
        myVolleyConnection.addParams("search_keyword", search_keyword);
        myVolleyConnection.setVolley(new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse: "+response);

                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");

                    if(success.equals("false")) {
                        String fail_reason = jsonObject.getString("reason");
                        Toast.makeText(getApplicationContext(), fail_reason, Toast.LENGTH_LONG).show();

                        ll_direct_input.setVisibility(View.VISIBLE);
                        rcy_book_result.setVisibility(View.INVISIBLE);
                    } else {
                        ll_direct_input.setVisibility(View.INVISIBLE);
                        rcy_book_result.setVisibility(View.VISIBLE);

                        JSONArray jsonArray = jsonObject.getJSONArray("result");
                        for (int i=0; i < jsonArray.length(); i++)
                        {
                            try {
                                JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                                // Pulling items from the array
                                String item = jsonObject2.getString("title");
                                String item2 = jsonObject2.getString("author");
                                String item3 = jsonObject2.getString("publisher");
                                String item4 = jsonObject2.getString("pubDate");
                                String item5 = jsonObject2.getString("description");
                                String item6 = jsonObject2.getString("cover");
                                String item7 = jsonObject2.getString("isbn13");

                                book_info = new Book_info(item, item2, item3, item4, item5, item6, item7);
                                adt_book.addItem(book_info);
                            } catch (JSONException e) {
                                Log.d(TAG, "jsonException "+e);

                            }
                        }
                        adt_book.notifyDataSetChanged();
                        Log.d(TAG, "onResponse: result"+jsonArray.length());
                    }


                } catch (JSONException e) {
                    Log.d(TAG, "JSONException: ");
                }

                progressDialog.dismiss();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        myVolleyConnection.requestVolley();

        progressDialog.setMessage("검색 중입니다.");
        progressDialog.show();

        // 2. 응답값에 따라 리싸이클러뷰 보여주거나 검색 결과 없다고 보여줌. (없다면 ll_direct_input hidden)



    }
}