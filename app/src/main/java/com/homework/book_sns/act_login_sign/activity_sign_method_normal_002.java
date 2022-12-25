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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.homework.book_sns.R;
import com.homework.book_sns.javaclass.SignInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class activity_sign_method_normal_002 extends AppCompatActivity {

    String TAG = "activity_sign_method_normal_002";

    /* --------------------------- */
    // xml의 view 객체들
    EditText etv_email;

    EditText etv_password;
    EditText etv_password_check;

    Button btn_pre_002;
    Button btn_make_sign;
    /* --------------------------- */

    /* --------------------------- */
    //회원가입 요청 변수들
    RequestQueue requestQueue; // 요청이 담길 queue
    Map<String, String> params = new HashMap<String, String>(); // 요청하면서 보낼 값들을 담아두는 map
    /* --------------------------- */

    /* --------------------------- */
    //회원 가입 클래스 (SignInfo)
    SignInfo signInfo;

    /* --------------------------- */



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_method_normal_002);

        setView();
        setObject();
    }

    private void setObject() {
        signInfo = new SignInfo();
        requestQueue = Volley.newRequestQueue(getApplicationContext());

        //인텐트로 받아온 이메일 주소를 입력
        Intent intent = getIntent();
        String email = intent.getStringExtra("email");
//        Toast.makeText(getApplicationContext(), email, Toast.LENGTH_LONG).show();

        etv_email.setText(email);
        signInfo.setEmail(email);
    }

    private void setView() {
        ActionBar bar = getSupportActionBar();
        bar.hide();

        etv_email = (EditText) findViewById(R.id.etv_email_pass);

        etv_password = (EditText) findViewById(R.id.etv_password);
        etv_password_check = (EditText) findViewById(R.id.etv_password_check);

        btn_pre_002 = (Button) findViewById(R.id.btn_pre_002);
        btn_make_sign = (Button) findViewById(R.id.btn_make_sign);

        setClickEvent();
    }

    private void setClickEvent() {
        btn_pre_002.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn_make_sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String password = etv_password.getText().toString();
                String password_check = etv_password_check.getText().toString();
                
                if(!examinePassword(password, password_check)) {
                    return; // 비밀번호 입력 여부, 일치여부 검사
                }

                signInfo.setPassword(password);
                makeSign();
            }
        });
    }

    private boolean examinePassword(String pass, String pass_check) {

        Log.d(TAG, "examinePassword: "+pass);
        Log.d(TAG, "examinePassword: "+pass_check);


        if(pass.length() == 0) {
            Toast.makeText(getApplicationContext(), "비밀먼호를 입력해주세요", Toast.LENGTH_LONG).show();
            etv_password.requestFocus();
            return false;            
        }

        if(!pass_check.equals(pass)) {
            Toast.makeText(getApplicationContext(), "비밀번호가 일치하지 않습니다.", Toast.LENGTH_LONG).show();
            etv_password_check.requestFocus();
            return false;
        } else {
            return true;
        }

        

    }

    private void makeSign() {
        String ip = "15.164.105.239";
        String url = "http://"+ip+"/book_sns/login_sign/make_user.php";


        params.put("email", signInfo.getEmail());
        params.put("sign_type", "normal");
        params.put("password", signInfo.getPassword());
        requestMakeSign(url);

    }

    public void requestMakeSign(String url) {
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
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
                                String user_id = jsonObject.getString("user_id");

                                Intent intent = new Intent(activity_sign_method_normal_002.this, activity_sign_method_normal_003.class);
                                intent.putExtra("user_id", user_id);

                                startActivity(intent);
                                finish();
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
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };
        requestQueue.add(request);
    }


}