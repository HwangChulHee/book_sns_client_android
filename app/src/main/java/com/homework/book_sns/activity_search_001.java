package com.homework.book_sns;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

public class activity_search_001 extends AppCompatActivity {

    private String TAG = "hch";
    private static String ACTIVITY_NAME = "activity_search_001";
    private static String ACTIVITY_FUNCTION = "search";
    private Context aContext;

    EditText etv_search;
    ImageButton ibtn_cancel;
    NestedScrollView nscrv;
    RecyclerView rcyv_recent_search;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_001);

        setObject();
        setView();
    }

    private void setObject() {
        aContext = this;
    }

    private void setView() {
        ActionBar bar = getSupportActionBar();
        bar.hide();

        etv_search = findViewById(R.id.etv_as1_search);
        ibtn_cancel = findViewById(R.id.ibtn_as1_cancel);
        nscrv = findViewById(R.id.nscrv_as1);
        rcyv_recent_search = findViewById(R.id.rcvy_as1_recent_search);
        progressBar = findViewById(R.id.pgb_as1);

    }

    private void log_activity(String msg) {
        Log.d(TAG, "액티비티 이름: "+ACTIVITY_NAME +", 액티비티 기능 : "+ACTIVITY_FUNCTION
                +", 로그 내용 : "+msg);
    }

    private void toast_activity(String msg) {
        Toast.makeText(aContext, msg ,Toast.LENGTH_LONG).show();
    }


}