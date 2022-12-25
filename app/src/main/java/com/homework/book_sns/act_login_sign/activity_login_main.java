package com.homework.book_sns.act_login_sign;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.homework.book_sns.R;
import com.homework.book_sns.javaclass.GoogleLoginApi;
import com.homework.book_sns.javaclass.LoginSharedPref;
import com.homework.book_sns.javaclass.MyVolleyConnection;
import com.homework.book_sns.javaclass.NaverLoginApi;
import com.homework.book_sns.javaclass.SignInfo;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.User;
import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;

public class activity_login_main extends AppCompatActivity {

    final String IP = "15.164.105.239";

    private String TAG = "hch";
    private static String ACTIVITY_NAME = "activity_login_main";
    private static String ACTIVITY_FUNCTION = "login";

    /* --------------------------- */
    //회원가입 요청 변수들
    RequestQueue requestQueue; // 요청이 담길 queue
    Map<String, String> params = new HashMap<String, String>(); // 요청하면서 보낼 값들을 담아두는 map
    /* --------------------------- */

    /* --------------------------- */
    // xml의 view 객체들
    EditText etv_email;
    EditText etv_pass;
    Button btn_login;

    TextView tv_findpass;
    TextView tv_sign;

    ImageButton ibtn_login_google;
    ImageButton ibtn_login_naver;
    ImageButton ibtn_login_kakao;
    /* --------------------------- */

    /* --------------------------- */
    // 네이버 API 관련 변수들
    private static String OAUTH_CLIENT_ID = "y0rDed_1HDjymvGq59Dx";
    private static String OAUTH_CLIENT_SECRET = "ZUxGyvWj89";
    private static String OAUTH_CLIENT_NAME = "네이버 아이디로 로그인";
    private static Context mContext;
    /* --------------------------- */

    SignInfo signInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_main);

        requestQueue = Volley.newRequestQueue(getApplicationContext());


        setView();
    }

    private void setView() {
        ActionBar bar = getSupportActionBar();
        bar.hide();

        etv_email = (EditText) findViewById(R.id.etv_login_email);
        etv_pass = (EditText) findViewById(R.id.etv_login_pass);
        btn_login = (Button) findViewById(R.id.btn_login); //로그인 버튼

        tv_findpass = (TextView) findViewById(R.id.tv_findPass); // 비밀번호 찾기
        tv_sign = (TextView) findViewById(R.id.tv_sign); // 회원가입

        ibtn_login_google = (ImageButton) findViewById(R.id.ibtn_login_google);
        ibtn_login_naver = (ImageButton) findViewById(R.id.ibtn_login_naver);
        ibtn_login_kakao = (ImageButton) findViewById(R.id.ibtn_login_kakao);

        setClickEvent();
    }

    private void setClickEvent() {

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String url = "http://"+IP+"/book_sns/login_sign/login.php";
                requestLogin(url, etv_email.getText().toString(), etv_pass.getText().toString());
            }
        });

        tv_findpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity_login_main.this, activity_findpass_start.class);
                startActivity(intent);
            }
        });

        tv_sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity_login_main.this, activity_sign_start.class);
                startActivity(intent);
            }
        });

        ibtn_login_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleLogin();
            }
        });

        ibtn_login_naver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                naverLogin();
            }
        });

        ibtn_login_kakao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                kakaoLogin();
            }
        });
    }


    private void requestLogin(String url, String email, String pass) {
        MyVolleyConnection myVolleyConnection = new MyVolleyConnection(url, 1, getApplicationContext());
        myVolleyConnection.addParams("email", email);
        myVolleyConnection.addParams("password", pass);
        myVolleyConnection.addParams("sign_type", "normal");
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
                    } else {
                        String user_id = jsonObject.getString("user_id");
                        String user_nickname = jsonObject.getString("user_nickname");
                        String sign_type = jsonObject.getString("sign_type");
                        String nickname = jsonObject.getString("user_nickname");
                        String profile_photo = jsonObject.getString("profile_photo");
                        String email = jsonObject.getString("user_email");

                        Toast.makeText(getApplicationContext(), user_nickname+" 님 반갑습니다.", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(activity_login_main.this, com.homework.book_sns.activity_home_main.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        LoginSharedPref.setUserInfo(activity_login_main.this, user_id, sign_type, nickname, profile_photo, email);
                        Log.d(TAG, "액티비티 이름: "+ACTIVITY_NAME +", 액티비티 기능 : "+ACTIVITY_FUNCTION
                                +", 로그 내용 : "+"로그인 처리");
                        intent.putExtra("type", "start");
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
        });
        myVolleyConnection.requestVolley();
    }

    private void requestApiLogin(String email, String sign_type) {
        String url = "http://"+IP+"/book_sns/login_sign/login_api.php";
        MyVolleyConnection myVolleyConnection = new MyVolleyConnection(url, 1, getApplicationContext());
        myVolleyConnection.addParams("email", email);
        myVolleyConnection.addParams("sign_type", sign_type);
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

                        Intent intent = new Intent(activity_login_main.this, activity_sign_method_api_001.class);
                        intent.putExtra("api_type", sign_type);

                        startActivity(intent);
                    } else {
                        String user_id = jsonObject.getString("user_id");
                        String user_nickname = jsonObject.getString("user_nickname");
                        String sign_type = jsonObject.getString("sign_type");
                        String nickname = jsonObject.getString("user_nickname");
                        String profile_photo = jsonObject.getString("profile_photo");
                        String email = jsonObject.getString("user_email");

                        Toast.makeText(getApplicationContext(), user_nickname+" 님 반갑습니다.", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(activity_login_main.this, com.homework.book_sns.activity_home_main.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        LoginSharedPref.setUserInfo(activity_login_main.this, user_id, sign_type, nickname, profile_photo, email);
                        intent.putExtra("type", "start");
                        Log.d(TAG, "type - start 로그 onResponse: ");
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


    private void kakaoLogin() {
        UserApiClient.getInstance().loginWithKakaoAccount(getApplicationContext(), new Function2<OAuthToken, Throwable, Unit>() {
            @Override
            public Unit invoke(OAuthToken oAuthToken, Throwable throwable) {

                if(throwable != null) {
                    Log.d(TAG, "invoke: "+throwable.getLocalizedMessage());
                }

                if(oAuthToken != null) {
                    kakaoInfoUpdate();
                }

                Log.d(TAG, "invoke: ");
                return null;
            }
        });
    }

    private void kakaoInfoUpdate() {
        UserApiClient.getInstance().me(new Function2<User, Throwable, Unit>() {
            @Override
            public Unit invoke(User user, Throwable throwable) {
                if(user != null) {
                    String mail = user.getKakaoAccount().getEmail();
                    requestApiLogin(mail, "kakao");

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

    private void naverLogin() {
        String accessToken = NaverLoginApi.mOAuthLoginInstance.getAccessToken(getApplicationContext());
        Log.d(TAG, "naverLogout: "+accessToken);
        mContext = this;
        NaverLoginApi.mOAuthLoginInstance = OAuthLogin.getInstance();
        NaverLoginApi.mOAuthLoginInstance.init(mContext, OAUTH_CLIENT_ID, OAUTH_CLIENT_SECRET, OAUTH_CLIENT_NAME);

        NaverLoginApi.mOAuthLoginInstance.startOauthLoginActivity(activity_login_main.this, new OAuthLoginHandler() {
            @Override
            public void run(boolean success) {
                if(success) {
                    naverInfoUpdate();
                    String accessToken = NaverLoginApi.mOAuthLoginInstance.getAccessToken(getApplicationContext());
                    Log.d(TAG, "naverLogout: "+accessToken);
                }
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
                    requestApiLogin(email, "naver");

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private void googleLogin() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        GoogleLoginApi.googleSignInClient = GoogleSignIn.getClient(this, gso);

        Intent intent = GoogleLoginApi.googleSignInClient.getSignInIntent();
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == 1) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            GoogleLoginApi.acct = task;

            googleInfoUpdate();
        }

    }

    private void googleInfoUpdate() {
        try {
            GoogleSignInAccount acct = GoogleLoginApi.acct.getResult(ApiException.class);

            if (acct != null) {
                String personEmail = acct.getEmail();
                requestApiLogin(personEmail, "google");
            }
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.e(TAG, "signInResult:failed code=" + e.getStatusCode());

        }

    }




}