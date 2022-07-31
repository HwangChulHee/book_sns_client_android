package com.homework.book_sns.javaclass;

import android.content.Context;
import android.widget.Toast;

public class MyBasicFunc {
    public static void showToast (Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}
