package com.homework.book_sns;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.homework.book_sns.javaclass.GoogleLoginApi;
import com.homework.book_sns.javaclass.LoginSharedPref;
import com.homework.book_sns.javaclass.NaverLoginApi;
import com.nhn.android.naverlogin.OAuthLogin;

public class activity_app_start extends AppCompatActivity {

    private Intent intent;

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
        setContentView(R.layout.activity_app_start);

        if(LoginSharedPref.getUserId(activity_app_start.this).length() == 0) {
            intent = new Intent(activity_app_start.this, com.homework.book_sns.act_login_sign.activity_login_main.class);
            startActivity(intent);
            this.finish();
        } else {

            if(LoginSharedPref.getSignType(getApplicationContext()).equals("google")) {
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .build();
                // Build a GoogleSignInClient with the options specified by gso.
                GoogleLoginApi.googleSignInClient = GoogleSignIn.getClient(this, gso);
            } else if(LoginSharedPref.getSignType(getApplicationContext()).equals("naver")) {
                mContext = this;
                NaverLoginApi.mOAuthLoginInstance = OAuthLogin.getInstance();
                NaverLoginApi.mOAuthLoginInstance.init(mContext, OAUTH_CLIENT_ID, OAUTH_CLIENT_SECRET, OAUTH_CLIENT_NAME);
            }


            intent = new Intent(activity_app_start.this, activity_home_main.class);
            intent.putExtra("user_id", LoginSharedPref.getUserId(this).toString());
            startActivity(intent);
            this.finish();
        }
    }
}