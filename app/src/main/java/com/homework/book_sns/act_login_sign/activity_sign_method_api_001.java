package com.homework.book_sns.act_login_sign;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.homework.book_sns.R;
import com.homework.book_sns.javaclass.GoogleLoginApi;
import com.homework.book_sns.javaclass.MyVolleyConnection;
import com.homework.book_sns.javaclass.NaverLoginApi;
import com.homework.book_sns.javaclass.SignInfo;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;

public class activity_sign_method_api_001 extends AppCompatActivity {

    String TAG = "hch";
    final String IP = "3.34.198.177";

    /* --------------------------- */
    // xml의 view 객체들
    CircleImageView civ_profile;
    Button btn_upload;
    EditText etv_nickname;

    Button btn_api_sign;
    /* --------------------------- */

    SignInfo signInfo;
    String api_type; // api type..
    Bitmap bitmap_img = null;
    String string_img = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_method_api_001);

        signInfo = new SignInfo();
        setView();
        setObject();
    }

    private void setObject() {
        Intent intent = getIntent();
        api_type = intent.getStringExtra("api_type");

        if(api_type.equals("kakao")) {
            kakaoInfoUpdate();
        } else if(api_type.equals("naver")) {
            naverInfoUpdate();
        } else if(api_type.equals("google")) {
            googleInfoUpdate();
        } else {
            Toast.makeText(getApplicationContext(), "api_type 오류", Toast.LENGTH_SHORT).show();
            return;
        }

    }

    private void setView() {
        ActionBar bar = getSupportActionBar();
        bar.hide();


        civ_profile = (CircleImageView) findViewById(R.id.civ_api_profile_img);
        btn_upload = (Button) findViewById(R.id.btn_api_img_upload);
        etv_nickname = (EditText) findViewById(R.id.etv_api_nickname);
        btn_api_sign = (Button) findViewById(R.id.btn_api_sign_profile);

        setClickEvent();
    }

    private void setClickEvent() {
        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        btn_api_sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "http://"+IP+"/book_sns/login_sign/make_api_user.php";
                requestApiSign(url, api_type);
            }
        });
    }
    
    private void requestApiSign(String url, String api_type) {

        signInfo.setNickname(etv_nickname.getText().toString());

        MyVolleyConnection myVolleyConnection = new MyVolleyConnection(url, 1, getApplicationContext());
        myVolleyConnection.addParams("email", signInfo.getEmail());
        myVolleyConnection.addParams("sign_type", api_type);
        myVolleyConnection.addParams("nickname", signInfo.getNickname());
        myVolleyConnection.addParams("image", signInfo.getImg());
        myVolleyConnection.setVolley(new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObject = null;
                Log.d(TAG, "onResponse: "+response);
                try {
                    jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");

                    if(success.equals("false")) {
                        String fail_reason = jsonObject.getString("reason");
                        Toast.makeText(getApplicationContext(), fail_reason, Toast.LENGTH_LONG).show();
                    } else {
                        String user_id = jsonObject.getString("user_id");
                        String user_nickname = jsonObject.getString("user_nickname");
                        String email = jsonObject.getString("email");
                        String dbpath = jsonObject.getString("img_src");
                        Toast.makeText(getApplicationContext(), user_nickname+" 님 반갑습니다.", Toast.LENGTH_SHORT).show();

                        signInfo.setId(user_id);
                        signInfo.setNickname(user_nickname);
                        signInfo.setEmail(email);
                        signInfo.setImg(dbpath);

                        Intent intent = new Intent(activity_sign_method_api_001.this, activity_sign_finish.class);
                        intent.putExtra("user_id", user_id);
                        intent.putExtra("nickname",signInfo.getNickname());
                        intent.putExtra("email", signInfo.getEmail());
                        intent.putExtra("sign_type" , api_type);
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

            }
        });
        myVolleyConnection.requestVolley();
    }


    private void kakaoInfoUpdate() {
        UserApiClient.getInstance().me(new Function2<User, Throwable, Unit>() {
            @Override
            public Unit invoke(User user, Throwable throwable) {
                if(user != null) {
                    String name = user.getKakaoAccount().getProfile().getNickname();
                    String mail = user.getKakaoAccount().getEmail();
                    String url_image = user.getKakaoAccount().getProfile().getProfileImageUrl();

                    signInfo.setNickname(name);
                    signInfo.setEmail(mail);

                    Glide.with(getApplicationContext())
                            .asBitmap()
                            .load(url_image)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(new CustomTarget<Bitmap> () {
                                @Override
                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                    civ_profile.setImageBitmap(resource);
                                    bitmap_img = resource;
                                    string_img = getStringImage(bitmap_img);
                                    signInfo.setImg(string_img); //이미지 저장
//                                    Log.d(TAG, "onResourceReady: "+string_img);
                                }
                                @Override
                                public void onLoadCleared(@Nullable Drawable placeholder) {

                                }
                            });

                    etv_nickname.setText(name);

                } else {
                    Log.d(TAG, "invoke: 유저정보 불러오기 에러..");
                }

                if(throwable != null) {
                    Log.d(TAG, "invoke: "+throwable.getLocalizedMessage());
                }

                return null;
            }
        });

    }

    private void naverInfoUpdate() {
        NaverRequestApiTask naverRequestApiTask = new NaverRequestApiTask();
        naverRequestApiTask.execute();
    }

    private class NaverRequestApiTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(Void... voids) {
            String url = "https://openapi.naver.com/v1/nid/me";
            String at = NaverLoginApi.mOAuthLoginInstance.getAccessToken(getApplicationContext());
            return NaverLoginApi.mOAuthLoginInstance.requestApi(getApplicationContext(), at, url);
        }

        @Override
        protected void onPostExecute(String content) {
            super.onPostExecute(content);
            try {
                JSONObject loginResult = new JSONObject(content);
                if (loginResult.getString("resultcode").equals("00")){
                    JSONObject response = loginResult.getJSONObject("response");

                    String email = response.getString("email");
                    String nickName = response.getString("nickname");
                    String image_url = response.getString("profile_image");

                    signInfo.setEmail(email);
                    signInfo.setNickname(nickName);
                    signInfo.setImg(image_url);

                    etv_nickname.setText(signInfo.getNickname());
                    Glide.with(getApplicationContext())
                            .asBitmap()
                            .load(image_url)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(new CustomTarget<Bitmap> () {
                                @Override
                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                    civ_profile.setImageBitmap(resource);
                                    bitmap_img = resource;
                                    string_img = getStringImage(bitmap_img);
                                    signInfo.setImg(string_img); //이미지 저장
//                                    Log.d(TAG, "onResourceReady: "+string_img);
                                }
                                @Override
                                public void onLoadCleared(@Nullable Drawable placeholder) {

                                }
                            });
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private void googleInfoUpdate() {
        try {
            GoogleSignInAccount acct = GoogleLoginApi.acct.getResult(ApiException.class);

            if (acct != null) {
                String personName = acct.getDisplayName();
                String personEmail = acct.getEmail();
                Uri personPhoto = acct.getPhotoUrl();

                Log.d(TAG, "handleSignInResult:personName "+personName);
                Log.d(TAG, "handleSignInResult:personEmail "+personEmail);
                Log.d(TAG, "handleSignInResult:personPhoto "+personPhoto);

                signInfo.setEmail(personEmail);
                signInfo.setNickname(personName);

                etv_nickname.setText(personName);
                Glide.with(getApplicationContext())
                        .asBitmap()
                        .load(personPhoto)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(new CustomTarget<Bitmap> () {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                civ_profile.setImageBitmap(resource);
                                bitmap_img = resource;
                                string_img = getStringImage(bitmap_img);
                                signInfo.setImg(string_img); //이미지 저장

//                                    Log.d(TAG, "onResourceReady: "+string_img);
                            }
                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {

                            }
                        });


            }
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.e(TAG, "signInResult:failed code=" + e.getStatusCode());

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



}