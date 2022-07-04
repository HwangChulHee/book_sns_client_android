package com.homework.book_sns.act_login_sign;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.homework.book_sns.R;
import com.homework.book_sns.javaclass.MyVolleyConnection;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

public class activity_findpass_start extends AppCompatActivity {

    String TAG = "hch";

    /* --------------------------- */
    // xml의 view 객체들
    EditText etv_email;
    TextView tv_notice;
    Button btn_request;
    /* --------------------------- */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findpass_start);

        setView();
    }

    private void setView() {
        ActionBar bar = getSupportActionBar();
        bar.setTitle("비밀번호 찾기");

        etv_email = findViewById(R.id.etv_findpass_email);
        tv_notice = findViewById(R.id.tv_findpass_notice);
        btn_request = findViewById(R.id.btn_findpass_request);

        btn_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                request_temp_password();
            }
        });
    }

    private void request_temp_password () {

        String email = etv_email.getText().toString();
        Pattern pattern = android.util.Patterns.EMAIL_ADDRESS;

        if(email.length() == 0) {
            Toast.makeText(getApplicationContext(), "이메일을 입력해주세요.", Toast.LENGTH_LONG).show();
            return;
        }

        // 이메일 형식 검사
        if(!pattern.matcher(email).matches()){
            Toast.makeText(getApplicationContext(), "이메일 형식이 맞지 않습니다.", Toast.LENGTH_LONG).show();
            return;
        }

        MyVolleyConnection myVolleyConnection = new MyVolleyConnection(1, getApplicationContext());
        myVolleyConnection.setURL("/login_sign/find_password.php");
        myVolleyConnection.addParams("email", email);
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
                        etv_email.clearFocus();
                        tv_notice.setText("임시비밀번호가 메일로 발급되었습니다. 해당 번호로 로그인해주세요.");
                        Toast.makeText(getApplicationContext(), "임시비밀번호가 발급되었습니다.", Toast.LENGTH_LONG).show();
                    }


                } catch (JSONException e) {
                    Log.d(TAG, "JSONException: ");
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        myVolleyConnection.requestVolley();
    }
}