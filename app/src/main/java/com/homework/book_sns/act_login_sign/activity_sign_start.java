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
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.homework.book_sns.R;
import com.homework.book_sns.javaclass.GoogleLoginApi;
import com.homework.book_sns.javaclass.MyVolleyConnection;
import com.homework.book_sns.javaclass.NaverLoginApi;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.User;
import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;

import org.json.JSONException;
import org.json.JSONObject;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;

public class activity_sign_start extends AppCompatActivity {

    String TAG = "hch";
    final String IP = "15.164.105.239";

    /* --------------------------- */
    // 네이버 API 관련 변수들
    private static String OAUTH_CLIENT_ID = "y0rDed_1HDjymvGq59Dx";
    private static String OAUTH_CLIENT_SECRET = "ZUxGyvWj89";
    private static String OAUTH_CLIENT_NAME = "네이버 아이디로 로그인";
    private static Context mContext;
    /* --------------------------- */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_start);

        mContext = this;
        NaverLoginApi.mOAuthLoginInstance = OAuthLogin.getInstance();
        NaverLoginApi.mOAuthLoginInstance.init(mContext, OAUTH_CLIENT_ID, OAUTH_CLIENT_SECRET, OAUTH_CLIENT_NAME);
        setView();
    }

    private void setView() {
        ActionBar bar = getSupportActionBar();
        bar.hide();

        Button button = (Button) findViewById(R.id.btn_normal_sign);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity_sign_start.this, activity_sign_method_normal_001.class);
                startActivity(intent);
            }
        });

        ImageButton imageButton = (ImageButton) findViewById(R.id.ibtn_login_google);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleLogin();
            }
        });

        ImageButton imageButton1 = (ImageButton) findViewById(R.id.ibtn_login_naver);
        imageButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                naverLogin();
            }
        });

        ImageButton imageButton2 = (ImageButton) findViewById(R.id.ibtn_login_kakao);
        imageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                kakaoLogin();
            }
        });
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
                    check_email_overlap(mail, "kakao");

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
        NaverLoginApi.mOAuthLoginInstance.startOauthLoginActivity(activity_sign_start.this, new OAuthLoginHandler() {
            @Override
            public void run(boolean success) {
                if(success) {
                    naverInfoUpdate();
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
                    check_email_overlap(email, "naver");
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
                check_email_overlap(personEmail, "google");

            }
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.e(TAG, "signInResult:failed code=" + e.getStatusCode());

        }

    }

    private void check_email_overlap (String email, String sign_type) {
        String url = "http://"+IP+"/book_sns/login_sign/check_api_email_overlap.php";
        MyVolleyConnection myVolleyConnection = new MyVolleyConnection(url, 1, getApplicationContext());
        myVolleyConnection.addParams("email", email);
        myVolleyConnection.addParams("sign_type", sign_type);
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
                        return;
                    } else {
                        Intent intent = new Intent(activity_sign_start.this, activity_sign_method_api_001.class);
                        intent.putExtra("api_type", sign_type);

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
}