package com.homework.book_sns.act_login_sign;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
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
import com.bumptech.glide.Glide;
import com.homework.book_sns.R;
import com.homework.book_sns.javaclass.SignInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class activity_sign_method_normal_003 extends AppCompatActivity {
    String TAG = "activity_sign_method_normal_003";

    /* --------------------------- */
    // xml의 view 객체들
    CircleImageView civ_profile;
    Button btn_upload;
    EditText etv_nickname;

    Button btn_pass;
    Button btn_next;
    /* --------------------------- */

    /* --------------------------- */
    //회원 정보 업로드 변수들
    RequestQueue requestQueue; // 요청이 담길 queue
    Map<String, String> params = new HashMap<String, String>(); // 요청하면서 보낼 값들을 담아두는 map
    /* --------------------------- */

    SignInfo signInfo;
    String profile_img;
    String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_method_normal_003);

        setView();
        setObject();
    }

    private void setObject() {
        signInfo = new SignInfo();

    }

    private void setView() {
        ActionBar bar = getSupportActionBar();
        bar.hide();


        civ_profile = (CircleImageView) findViewById(R.id.civ_profile_img);
        btn_upload = (Button) findViewById(R.id.btn_img_upload);
        etv_nickname = (EditText) findViewById(R.id.etv_nickname);
        btn_pass = (Button) findViewById(R.id.btn_pass_profile);
        btn_next = (Button) findViewById(R.id.btn_next_profile);
        
        setClickEvent();
        setUserInfo();

    }

    private void setUserInfo() {
        requestQueue = Volley.newRequestQueue(getApplicationContext());

        String ip = "3.34.198.177";
        String url = "http://"+ip+"/book_sns/login_sign/upload_user_info.php";

        Intent intent = getIntent();
         user_id = intent.getStringExtra("user_id");
//        Toast.makeText(getApplicationContext(), user_id, Toast.LENGTH_LONG).show();

        params.put("user_id", user_id);
        requestUploadUserInfo(ip, url);
    }

    private void requestUploadUserInfo(String ip, String url) {
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, response);

                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            String js_user_id = jsonObject.getString("user_id");
                            String js_email = jsonObject.getString("email");
                            String js_nickname = jsonObject.getString("nickname");
                            String js_img_src = jsonObject.getString("img_src");
                            js_img_src = js_img_src.replace("\\\\", "");
                            Log.d(TAG, "onResponse: json 이미지 경로 "+js_img_src);

                            signInfo.setId(js_user_id);
                            signInfo.setEmail(js_email);
                            signInfo.setNickname(js_nickname);
                            signInfo.setImg(js_img_src);

                            String image_url = "http://"+ip+js_img_src;
                            Glide.with(getApplicationContext()).load(image_url).error(R.drawable.ic_baseline_error_24).into(civ_profile);

                            etv_nickname.setText(js_nickname);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d(TAG, "onResponse: "+e);
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

    private void setClickEvent() {
        
        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 0);
            }
        });

        btn_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity_sign_method_normal_003.this, activity_sign_finish.class);
                intent.putExtra("user_id", signInfo.getId());
                intent.putExtra("nickname",signInfo.getNickname());
                intent.putExtra("email", signInfo.getEmail());
                intent.putExtra("sign_type" , "normal");
                intent.putExtra("profile_photo", signInfo.getImg());
                
                startActivity(intent);
                finish();
            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInfo.setNickname(etv_nickname.getText().toString());
                signInfo.setImg(profile_img); // 변경 시 데이터 변경

//                Log.d(TAG, "onClick: 이미지 uri "+ profile_img);

                params.clear();

                String ip = "3.34.198.177";
                String url = "http://"+ip+"/book_sns/login_sign/update_user_info.php";

                params.put("user_id", user_id);
                params.put("nickname", signInfo.getNickname());
                params.put("img", signInfo.getImg());
                requestUpdateUserInfo(ip, url);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                Glide.with(getApplicationContext()).load(data.getData()).override(80,100).into(civ_profile);
                btn_pass.setVisibility(View.INVISIBLE);
                btn_next.setVisibility(View.VISIBLE);

                Uri uri = data.getData();

                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    profile_img = getStringImage(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public String getStringImage(Bitmap bitmap)
    {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageByteArray = byteArrayOutputStream.toByteArray();
        String encodedImage = Base64.encodeToString(imageByteArray, Base64.DEFAULT);
        return encodedImage;
    }


    private void requestUpdateUserInfo(String ip, String url) {
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "update ㅇㅇ"+response);
                        Log.d(TAG, response);
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");

                            if(success.equals("false")) {
                                String fail_reason = jsonObject.getString("reason");
                                Toast.makeText(getApplicationContext(), fail_reason, Toast.LENGTH_LONG).show();
                            } else {
                                String db_path = jsonObject.getString("db_path");
                                signInfo.setImg(db_path);

                                Intent intent = new Intent(activity_sign_method_normal_003.this, activity_sign_finish.class);
                                intent.putExtra("user_id", signInfo.getId());
                                intent.putExtra("nickname",signInfo.getNickname());
                                intent.putExtra("email", signInfo.getEmail());
                                intent.putExtra("sign_type" , "normal");
                                intent.putExtra("profile_photo", signInfo.getImg());

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