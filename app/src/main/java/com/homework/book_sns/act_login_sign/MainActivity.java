package com.homework.book_sns.act_login_sign;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.homework.book_sns.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    String TAG = "temp";
    final String IP = "15.164.105.239";
    /* --------------------------- */
    //회원가입 요청 변수들
    RequestQueue requestQueue; // 요청이 담길 queue
    Map<String, String> params = new HashMap<String, String>(); // 요청하면서 보낼 값들을 담아두는 map
    /* --------------------------- */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.temp);

        String user_id;

        Intent intent = getIntent();
        user_id = intent.getStringExtra("user_id");

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        params.put("user_id", user_id);

        String ip = "15.164.105.239";
        String url = "http://"+ip+"/book_sns/login_sign/temp.php";

        requestMakeSign(url);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, activity_login_main.class);
                startActivity(intent);
                finish();
            }
        });
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
                            String js_nickname = jsonObject.getString("nickname");
                            String js_img_src = jsonObject.getString("img_src");
                            String js_email = jsonObject.getString("email");

                            TextView textView = (TextView) findViewById(R.id.textView16);
                            TextView textView1 = (TextView) findViewById(R.id.textView17);
                            ImageView imageView = (ImageView) findViewById(R.id.imageView);

                            textView.setText(js_email);
                            textView1.setText(js_nickname);

                            String ip = "15.164.105.239";
                            String image_url = "http://"+ip+js_img_src;
                            Glide.with(getApplicationContext()).load(image_url).error(R.drawable.ic_baseline_error_24).into(imageView);

                        } catch (JSONException e) {
                            e.printStackTrace();
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