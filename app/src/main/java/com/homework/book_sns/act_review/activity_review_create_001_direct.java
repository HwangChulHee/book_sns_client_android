package com.homework.book_sns.act_review;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.homework.book_sns.R;
import com.homework.book_sns.activity_mypage_setting;
import com.homework.book_sns.javaclass.Book_info;
import com.homework.book_sns.javaclass.MyImageFunc;
import com.homework.book_sns.rcyv_adapter.Adt_rc1_searched_book;

import java.net.URI;

public class activity_review_create_001_direct extends AppCompatActivity {

    public static Activity act_rc1_direct = null;

    /* --------------------------- */
    // 액티비티 필수 객체들
    Context aContext;
    String TAG = "hch";
    /* --------------------------- */

    /* --------------------------- */
    // xml의 view 객체들
    Button btn_close;

    ImageView iv_cover;
    EditText etv_title;
    EditText etv_author;

    Button btn_input;
    /* --------------------------- */

    /* --------------------------- */
    // 각종 객체들
    Book_info book_info;

    Uri imageCoverUri;
    Bitmap imageCoverBitmap;
    String title;
    String author;

    boolean isUpdate = false;
    /* --------------------------- */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_create_001_direct);

        aContext = this;
        act_rc1_direct = this;

        setObject();
        setView();
    }

    private void setObject() {
        book_info = new Book_info();
        getMyIntent();
    }

    private void getMyIntent() {
        Intent intent = getIntent();
        if(intent.getStringExtra("update").equals("true")) {
            isUpdate = true;

            imageCoverBitmap = MyImageFunc.getBitmap_FromBase64Image(intent.getStringExtra("update_cover"));
            title = intent.getStringExtra("update_title");
            author = intent.getStringExtra("update_author");

        }
    }

    private void setView() {
        ActionBar bar = getSupportActionBar();
        bar.hide();

        btn_close = (Button) findViewById(R.id.btn_rc1_direct_close);

        iv_cover = (ImageView) findViewById(R.id.iv_rc1_direct_cover);
        etv_title = (EditText) findViewById(R.id.etv_rc1_direct_title);
        etv_author = (EditText) findViewById(R.id.etv_rc1_direct_author);

        btn_input = (Button) findViewById(R.id.btn_rc1_direct_input);

        setClickEvent();
        if(isUpdate) {
            setViewData();
        }
    }

    private void setViewData() {
        iv_cover.setImageBitmap(imageCoverBitmap);
        etv_title.setText(title);
        etv_author.setText(author);
    }

    private void setClickEvent() {
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        iv_cover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(aContext, view);
                popupMenu.getMenuInflater().inflate(R.menu.menu_popup_rc1_direct_image, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        Log.d(TAG, "onMenuItemClick: ");
                        switch (menuItem.getItemId()) {
                            case R.id.menu_rc1_direct_1:
                                Intent intent1 = MyImageFunc.take_picture(aContext, activity_review_create_001_direct.this);


                                startActivityForResult(intent1, 1);
                                return true;
                            case R.id.menu_rc1_direct_2:
                                Intent intent2 = MyImageFunc.upload_single_photo();
                                startActivityForResult(intent2, 2);
                                return true;

                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });

        btn_input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(imageCoverBitmap == null) {
                    Toast.makeText(aContext, "이미지를 입력해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                String title = etv_title.getText().toString();
                if(title.length() == 0) {
                    Toast.makeText(aContext, "도서명을 입력해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                String author = etv_author.getText().toString();
                if(author.length() == 0) {
                    Toast.makeText(aContext, "저자명을 입력해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!isUpdate) {
                    Intent intent = new Intent(activity_review_create_001_direct.this, activity_review_create_002.class);
                    intent.putExtra("inputType", "direct");
                    intent.putExtra("intentType", "create");

                    intent.putExtra("title", title);
                    intent.putExtra("author", author);

                    String imageBase64 = MyImageFunc.getBase64Image_FromBitmap(imageCoverBitmap);
                    intent.putExtra("cover", imageBase64);

                    Log.d(TAG, "onClick: ");
                    startActivity(intent);

                } else {
                    Intent intent = new Intent();

                    String imageBase64 = MyImageFunc.getBase64Image_FromBitmap(imageCoverBitmap);
                    intent.putExtra("update_cover", imageBase64);

//                    byte[] imageByteArray = MyImageFunc.getByteArray_FromBimapImage(imageCoverBitmap);
//                    intent.putExtra("update_cover", imageByteArray);
                    intent.putExtra("update_title", title);
                    intent.putExtra("update_author", author);

                    setResult(RESULT_OK, intent);
                    finish();
                }


            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1 && resultCode == RESULT_OK) {
            Log.d(TAG, "onActivityResult: ");

            Uri uri = MyImageFunc.take_picture_result();
            imageCoverBitmap = MyImageFunc.getBitmapImage_FromUri(uri, aContext);
            iv_cover.setImageBitmap(imageCoverBitmap);
//            iv_cover.setImageURI(uri);

        } else if(requestCode == 2 && resultCode == RESULT_OK) {

            Uri imageUri = MyImageFunc.result_single_photo(data);
            imageCoverBitmap = MyImageFunc.getBitmapImage_FromUri(imageUri, aContext);
            iv_cover.setImageBitmap(imageCoverBitmap);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 1001: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(this,"승인이 허가되어 있습니다.",Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(this,"아직 승인받지 않았습니다.",Toast.LENGTH_LONG).show();
                }
                return;
            }

        }
    }
}