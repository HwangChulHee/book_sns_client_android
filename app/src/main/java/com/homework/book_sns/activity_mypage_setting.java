package com.homework.book_sns;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.homework.book_sns.javaclass.GoogleLoginApi;
import com.homework.book_sns.javaclass.LoginSharedPref;
import com.homework.book_sns.javaclass.MyImageFunc;
import com.homework.book_sns.javaclass.MyVolleyConnection;
import com.homework.book_sns.javaclass.NaverLoginApi;
import com.kakao.sdk.user.UserApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class activity_mypage_setting extends AppCompatActivity {

    String TAG = "hch";
    Context aContext;

    /* --------------------------- */
    // xml의 view 객체들
    CircleImageView civ_profile;
    TextView tv_email;
    EditText etv_nickname;
    Button btn_change_nickname;

    TextView tv_logout;
    TextView tv_delete_sign;

    LinearLayout ly_password;
    /* --------------------------- */

    /* --------------------------- */
    // view_mypage_setting_password.xml의 view 객체들
    EditText etv_view_setting_pass;
    EditText etv_view_setting_pass_check;

    Button btn_view_setting_change;
    /* --------------------------- */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage_setting);

        aContext = this;
        setView();
    }

    private void setView() {
        ActionBar bar = getSupportActionBar();
        bar.setTitle("환경설정");


        civ_profile = (CircleImageView) findViewById(R.id.civ_mypage_setting_photo);
        tv_email = (TextView) findViewById(R.id.tv_mypage_setting_email);
        etv_nickname = (EditText) findViewById(R.id.etv_mypage_setting_nickname);
        btn_change_nickname = (Button) findViewById(R.id.btn_mypage_setting_nickname_change);
        tv_logout = (TextView) findViewById(R.id.tv_mypage_setting_logout);
        tv_delete_sign = (TextView) findViewById(R.id.tv_mypage_setting_sign_delete);

        String image_url = "http://"+MyVolleyConnection.IP
                +LoginSharedPref.getPrefProfilePhoto(getApplicationContext());
        Glide.with(getApplicationContext())
                .load(image_url)
                .error(R.drawable.ic_baseline_error_24)
                .override(80,80).into(civ_profile);

        tv_email.setText(LoginSharedPref.getPrefEmail(getApplicationContext()));
        etv_nickname.setText(LoginSharedPref.getPrefNickname(getApplicationContext()));

        setClickEvent();
        setPasswordView();
    }

    private void setPasswordView() {
        if(LoginSharedPref.getSignType(getApplicationContext()).equals("normal")) {
            ly_password = findViewById(R.id.layout_password);

            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.view_mypage_setting_password, ly_password, true);

            etv_view_setting_pass = view.findViewById(R.id.etv_view_setting_pass);
            etv_view_setting_pass_check = view.findViewById(R.id.etv_view_setting_pass_check);
            btn_view_setting_change = view.findViewById(R.id.btn_view_setting_change);

            btn_view_setting_change.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    changePassword();
                }
            });

        } else {
            return;
        }
    }

    private void setClickEvent() {
        civ_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                change_profile();
            }
        });


        btn_change_nickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nickname = etv_nickname.getText().toString();
                change_nickname(nickname);
            }
        });

        tv_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(activity_mypage_setting.this);
                builder.setMessage("로그아웃 하시겠습니까?");
                builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        logout();
                    }
                });
                builder.setNegativeButton("아니오", null);
                builder.create().show();
            }
        });

        tv_delete_sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(activity_mypage_setting.this);
                builder.setMessage("회원탈퇴 하시겠습니까?");
                builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        sign_delete();
                    }
                });
                builder.setNegativeButton("아니오", null);
                builder.create().show();

            }
        });
    }

    private void change_nickname (String nickname) {
        String url = "http://"+MyVolleyConnection.IP+"/book_sns/login_sign/change_nickname.php";
        MyVolleyConnection myVolleyConnection = new MyVolleyConnection(url, 1, getApplicationContext());
        myVolleyConnection.addParams("user_id", LoginSharedPref.getUserId(getApplicationContext()));
        myVolleyConnection.addParams("nickname", nickname);
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
                        LoginSharedPref.updateUserNickname(getApplicationContext() , nickname);
                        Toast.makeText(getApplicationContext(), "닉네임이 변경되었습니다.", Toast.LENGTH_SHORT).show();
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

    private void change_profile() {
//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);

        Intent intent = MyImageFunc.upload_single_photo();
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                Uri imageUri = MyImageFunc.result_single_photo(data);

                Bitmap imageBitmap = MyImageFunc.getBitmapImage_FromUri(imageUri, aContext);
                String imageBase64 = MyImageFunc.getBase64Image_FromBitmap(imageBitmap);
                change_profile_upload(imageBase64);
            }
        }
    }

    private void change_profile_upload(String profile_img) {
        MyVolleyConnection myVolleyConnection = new MyVolleyConnection(1, getApplicationContext());
        myVolleyConnection.setURL("/login_sign/change_profile_photo.php");
        myVolleyConnection.addParams("user_id", LoginSharedPref.getUserId(getApplicationContext()));
        myVolleyConnection.addParams("past_img_src", LoginSharedPref.getPrefProfilePhoto(getApplicationContext()));
        myVolleyConnection.addParams("profile_img", profile_img);
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
                        String img_src = jsonObject.getString("dbpath");
                        LoginSharedPref.updateUserImage(getApplicationContext(), img_src);
                        Log.d(TAG, "onResponse: 123"+LoginSharedPref.getPrefProfilePhoto(getApplicationContext()));

                        String image_url = "http://"+MyVolleyConnection.IP
                                +LoginSharedPref.getPrefProfilePhoto(getApplicationContext());
                        Glide.with(getApplicationContext())
                                .load(image_url)
                                .error(R.drawable.ic_baseline_error_24)
                                .override(80,80)
                                .into(civ_profile);
                        Toast.makeText(getApplicationContext(), "프로필 사진이 변경되었습니다.", Toast.LENGTH_SHORT).show();

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


    private void changePassword() {
        String password = etv_view_setting_pass.getText().toString();

        if(password.length() == 0) {
            Toast.makeText(getApplicationContext(), "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        String password_check = etv_view_setting_pass_check.getText().toString();

        if(!password.equals(password_check)) {
            Toast.makeText(getApplicationContext(), "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        MyVolleyConnection myVolleyConnection = new MyVolleyConnection(1, getApplicationContext());
        myVolleyConnection.setURL("/login_sign/change_password.php");
        myVolleyConnection.addParams("user_id", LoginSharedPref.getUserId(getApplicationContext()));
        myVolleyConnection.addParams("new_password", password);
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
                        String password = jsonObject.getString("password");
                        Log.d(TAG, "onResponse: password"+password);
                        Toast.makeText(getApplicationContext(), "비밀번호가 변경되었습니다.", Toast.LENGTH_LONG).show();
                        etv_view_setting_pass.setText(null);
                        etv_view_setting_pass.clearFocus();

                        etv_view_setting_pass_check.setText(null);
                        etv_view_setting_pass_check.clearFocus();
                    }

                } catch (JSONException e) {
                    Log.d(TAG, "onResponse: "+response);
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



    private void logout() {
        String signType = LoginSharedPref.getSignType(activity_mypage_setting.this);
        Log.d(TAG, "onClick: "+signType);

        if(signType.equals("kakao")) {
            kakaoLogout();
        } else if(signType.equals("naver")) {
            naverLogout();
        } else if(signType.equals("google")) {
            googleLogout();
        } else if(signType.equals("normal")) {

        } else {
            Toast.makeText(getApplicationContext(), "로그아웃 오류", Toast.LENGTH_LONG).show();
            return;
        }

        Intent intent = new Intent(activity_mypage_setting.this, com.homework.book_sns.act_login_sign.activity_login_main.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();

        LoginSharedPref.clearUserId(getApplicationContext());
        Toast.makeText(getApplicationContext(), "로그아웃 되었습니다.", Toast.LENGTH_LONG).show();
    }

    private void sign_delete() {
        String user_id = LoginSharedPref.getUserId(getApplicationContext());
        String signType = LoginSharedPref.getSignType(activity_mypage_setting.this);

        if(signType.equals("kakao")) {
            kakaoDeleteToken();
        } else if(signType.equals("naver")) {
            naverSignOut();
        } else if(signType.equals("google")) {
            googleSignOut();
        } else if(signType.equals("normal")) {

        } else {
            Toast.makeText(getApplicationContext(), "로그아웃 오류", Toast.LENGTH_LONG).show();
            return;
        }



        String url = "http://"+MyVolleyConnection.IP+"/book_sns/login_sign/sign_delete.php";
        MyVolleyConnection myVolleyConnection = new MyVolleyConnection(url, 1, activity_mypage_setting.this);
        myVolleyConnection.addParams("user_id", user_id);
        myVolleyConnection.setVolley(new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");

                    if(success.equals("false")) {
                        String fail_reason = jsonObject.getString("reason");
                        Toast.makeText(getApplicationContext(), fail_reason, Toast.LENGTH_LONG).show();
                    } else {
                        Intent intent = new Intent(activity_mypage_setting.this, com.homework.book_sns.act_login_sign.activity_login_main.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();

                        LoginSharedPref.clearUserId(getApplicationContext());
                        Toast.makeText(getApplicationContext(), "회원탈퇴 되었습니다.", Toast.LENGTH_LONG).show();
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


    private void kakaoLogout() {
        UserApiClient.getInstance().logout(new Function1<Throwable, Unit>() {
            @Override
            public Unit invoke(Throwable throwable) {
                if(throwable != null) {
                    Log.d(TAG, "logout");
                }
                return null;
            }
        });
    }

    private void kakaoDeleteToken() {
        UserApiClient.getInstance().unlink(new Function1<Throwable, Unit>() {
            @Override
            public Unit invoke(Throwable throwable) {
                if(throwable != null) {
                    Log.d(TAG, "unlink");
                }
                return null;
            }
        });
    }

    private void naverLogout() {
        String accessToken = NaverLoginApi.mOAuthLoginInstance.getAccessToken(getApplicationContext());
        Log.d(TAG, "naverLogout: "+accessToken);

        NaverLoginApi.mOAuthLoginInstance.logout(getApplicationContext());
        accessToken = NaverLoginApi.mOAuthLoginInstance.getAccessToken(getApplicationContext());
        Log.d(TAG, "naverLogout: "+accessToken);
//        NaverLoginApi.mOAuthLoginInstance = null;
    }

    private void naverSignOut() {
        new DeleteTokenTask().execute();
    }
    private class DeleteTokenTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            boolean isSuccessDeleteToken = NaverLoginApi.mOAuthLoginInstance.logoutAndDeleteToken(getApplicationContext());

            if (!isSuccessDeleteToken) {
                // 서버에서 token 삭제에 실패했어도 클라이언트에 있는 token 은 삭제되어 로그아웃된 상태이다
                // 실패했어도 클라이언트 상에 token 정보가 없기 때문에 추가적으로 해줄 수 있는 것은 없음

            }

            return null;
        }

        protected void onPostExecute(Void v) {

        }
    }

    private void googleLogout() {
        GoogleLoginApi.googleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });

    }

    private void googleSignOut() {
        GoogleLoginApi.googleSignInClient.revokeAccess()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });
    }


}