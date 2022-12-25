package com.homework.book_sns.act_login_sign;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.homework.book_sns.R;
import com.homework.book_sns.javaclass.AuthInfo;
import com.homework.book_sns.javaclass.MyVolleyConnection;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class activity_sign_method_normal_001 extends AppCompatActivity {

    String TAG = "activity_sign_method_normal_001";

    /* --------------------------- */
    AuthInfo authInfo;

    /* --------------------------- */


    /* --------------------------- */
    // xml의 view 객체들
    EditText etv_email;
    Button btn_email_request;

    EditText etv_auth_num;
    Button btn_auth_check;

    Button btn_next_001;
    Button btn_pre_001;
    /* --------------------------- */


    /* --------------------------- */
    //인증 번호 확인 관련 변수들
    RequestQueue requestQueue; // 요청이 담길 queue
    Map<String, String> params = new HashMap<String, String>(); // 요청하면서 보낼 값들을 담아두는 map

    /* --------------------------- */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_method_normal_001);

        setView();
        setObject();
    }

    private void setObject() {
        authInfo = new AuthInfo();
        requestQueue = Volley.newRequestQueue(getApplicationContext());
    }

    private void setView() {
        ActionBar bar = getSupportActionBar();
        bar.hide();

        etv_email =  (EditText) findViewById(R.id.etv_email_pass);
        btn_email_request = (Button) findViewById(R.id.btn_email_request);

        etv_auth_num = (EditText) findViewById(R.id.etv_auth_num);
        btn_auth_check = (Button) findViewById(R.id.btn_auth_check);

        btn_next_001 = (Button) findViewById(R.id.btn_next_001);
        btn_pre_001 = (Button) findViewById(R.id.btn_pre_001);

        btn_auth_check.setEnabled(false);
        btn_next_001.setEnabled(false);
        setClickEvent();
    }

    private void setClickEvent () {
        btn_email_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!requestEmailAuth()) {
                    return; // 인증번호 요청. 만약 이메일이 유효하지 않으면 나감.
                }
                btn_email_request.setText("인증번호 재전송");
                btn_auth_check.setEnabled(true);
            }
        });

        btn_auth_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkEmailAuth(); // 인증번호를 확인함. SignInfo 에 저장된 이메일 주소와 시간을 통해 요청
            }
        });


        btn_pre_001.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn_next_001.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity_sign_method_normal_001.this, activity_sign_method_normal_002.class);
                intent.putExtra("email", authInfo.getEmail());

                startActivity(intent);
                finish();
            }
        });

    }

    private boolean requestEmailAuth () {
        String ip = "15.164.105.239";
        String url = "http://"+ip+"/book_sns/login_sign/email_auth_request.php";

        String email_address = etv_email.getText().toString();
        Pattern pattern = android.util.Patterns.EMAIL_ADDRESS;

        // 이메일 형식 검사
        if(!pattern.matcher(email_address).matches()){
            Toast.makeText(getApplicationContext(), "이메일 형식이 맞지 않습니다.", Toast.LENGTH_LONG).show();
            return false;
        }


        long time = System.currentTimeMillis();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd k:mm:ss");
        Date date = new Date(time);
        String curTime = dateFormat.format(date);

        authInfo.set_info(email_address, curTime);


        MyVolleyConnection myVolleyConnection = new MyVolleyConnection(url, 1, getApplicationContext());
        myVolleyConnection.addParams("email", email_address);
        myVolleyConnection.addParams("auth_time", curTime);
        myVolleyConnection.setVolley(new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, response);
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");

                    if(success.equals("false")) {
                        String fail_reason = jsonObject.getString("reason");
                        Toast.makeText(getApplicationContext(), fail_reason, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "이메일이 전송되었습니다.", Toast.LENGTH_LONG).show();
                    }


                } catch (JSONException e) {
                    Log.d(TAG, "JSONException: ");
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: "+error);
            }
        });
        myVolleyConnection.requestVolley();


        return true;
    }

    private void checkEmailAuth () {
        String ip = "15.164.105.239";
        String url = "http://"+ip+"/book_sns/login_sign/email_auth_check.php";

        String auth_num = etv_auth_num.getText().toString();

//        params.put("email", authInfo.getEmail());
//        params.put("auth_time", authInfo.getSignTime());
//        params.put("auth_num", auth_num);
//        requestAuthCheckVolley(url);

        MyVolleyConnection myVolleyConnection = new MyVolleyConnection(url, 1, getApplicationContext());
        myVolleyConnection.addParams("email", authInfo.getEmail());
        myVolleyConnection.addParams("auth_time", authInfo.getSignTime());
        myVolleyConnection.addParams("auth_num", auth_num);
        myVolleyConnection.setVolley(new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                checkAuthNumResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        myVolleyConnection.requestVolley();
    }


    private void checkAuthNumResponse(String response) {
        response = response.trim();
        if(response.equals("유효")) {
            btn_next_001.setEnabled(true);
            Toast.makeText(getApplicationContext(), "인증이 확인되었습니다.", Toast.LENGTH_LONG).show();
        } else if(response.equals("시간만료")) {
            Toast.makeText(getApplicationContext(), "인증시간이 만료되었습니다. 인증번호를 다시 전송해주세요.", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "인증번호가 유효하지 않습니다.", Toast.LENGTH_LONG).show();
        }
    }
}