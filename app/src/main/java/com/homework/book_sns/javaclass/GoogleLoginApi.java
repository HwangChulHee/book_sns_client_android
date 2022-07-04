package com.homework.book_sns.javaclass;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.Task;

public class GoogleLoginApi {
    static public Task<GoogleSignInAccount> acct;
    static public GoogleSignInClient googleSignInClient;
}
