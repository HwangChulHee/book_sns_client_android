package com.homework.book_sns.act_review;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.homework.book_sns.R;
import com.homework.book_sns.javaclass.Book_info;
import com.homework.book_sns.javaclass.Review_info;
import com.homework.book_sns.rcyv_adapter.Adt_rc2_photos;

import java.util.ArrayList;

public class activity_review_update extends AppCompatActivity {

    /* --------------------------- */
    // 액티비티 필수 객체들
    Context aContext;
    String TAG = "hch";
    /* --------------------------- */

    /* --------------------------- */
    // xml의 view 객체들

    Button btn_back;
    TextView tv_review_create;

    TextView tv_review_text;

    ImageView iv_cover;
    TextView tv_title;
    TextView tv_author;
    Button btn_book_update;

    LinearLayout ll_go_gallery;
    TextView tv_photo_count;
    RecyclerView rcy_photos;
    /* --------------------------- */

    /* --------------------------- */
    // 각종 객체들
    Book_info book_info;
    ArrayList<Uri> uriArrayList = new ArrayList<>();

    Adt_rc2_photos adt_photos;
    Review_info review_info;
    ProgressDialog progressDialog;

    String intentType;

    String direct_imageUri;
    String direct_title;
    String direct_author;

    String review_board_id;
    /* --------------------------- */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_create_002);

        aContext = this;
        setObject();
        setView();
    }

    private void setObject() {
        review_info = new Review_info();

        myGetIntent();
        progressDialog = new ProgressDialog(aContext);
    }

    private void myGetIntent() {
        Intent intent = getIntent();
        review_board_id = intent.getStringExtra("review_board_id");

    }

    private void setView() {
    }


}