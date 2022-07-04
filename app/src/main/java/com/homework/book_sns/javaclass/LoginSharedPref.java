package com.homework.book_sns.javaclass;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class LoginSharedPref {

    static final String PREF_USER_ID = "user_id";
    static final String PREF_SIGN_TYPE = "sign_type";
    static final String PREF_NICKNAME = "nickname";
    static final String PREF_PROFILE_PHOTO = "profile_photo";
    static final String PREF_EMAIL = "email";

    static SharedPreferences getSharedPreferences (Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    // 계정 정보 저장
    public static void setUserInfo(Context ctx, String user_id, String sign_type, String nickname, String profile_photo, String email) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_USER_ID, user_id);
        editor.putString(PREF_SIGN_TYPE, sign_type);
        editor.putString(PREF_NICKNAME, nickname);
        editor.putString(PREF_PROFILE_PHOTO, profile_photo);
        editor.putString(PREF_EMAIL, email);

        editor.commit();
    }

    public static void updateUserNickname (Context context, String nickname) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(PREF_NICKNAME, nickname);
        editor.commit();
    }

    public static void updateUserImage (Context context, String profile_photo) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(PREF_PROFILE_PHOTO, profile_photo);
        editor.commit();
    }

    // 저장된 정보 가져오기
    public static String getUserId(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_USER_ID, "");
    }

    // 저장된 정보 가져오기
    public static String getSignType(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_SIGN_TYPE, "");
    }

    public static String getPrefNickname(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_NICKNAME, "");
    }

    public static String getPrefProfilePhoto(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_PROFILE_PHOTO, "");
    }

    public static String getPrefEmail(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_EMAIL, "");
    }

    // 로그아웃
    public static void clearUserId(Context ctx) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.clear();
        editor.commit();
    }


}
