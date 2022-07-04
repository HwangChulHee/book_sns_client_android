package com.homework.book_sns.act_review;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.homework.book_sns.R;
import com.homework.book_sns.javaclass.Book_info;
import com.homework.book_sns.javaclass.LoginSharedPref;
import com.homework.book_sns.javaclass.MyImageFunc;
import com.homework.book_sns.javaclass.MyVolleyConnection;
import com.homework.book_sns.javaclass.Review_info;
import com.homework.book_sns.javaclass.Review_list_simple_info;
import com.homework.book_sns.javaclass.User_info;
import com.homework.book_sns.rcyv_adapter.Adt_rc2_photos;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class activity_review_create_002 extends AppCompatActivity {

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
    ArrayList<Bitmap> bimapArrayList = new ArrayList<>();

    Adt_rc2_photos adt_photos;
    Review_info review_info;
    ProgressDialog progressDialog;

    String book_inputType;
    String intentType;

    String direct_imageBase64;
    String direct_title;
    String direct_author;
    
    String review_board_id; // 수정 시 필요한 객체
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
        adt_photos = new Adt_rc2_photos();

        myGetIntent();
        progressDialog = new ProgressDialog(aContext);
    }

    private void myGetIntent() {
        Intent intent = getIntent();
        intentType = intent.getStringExtra("intentType");
        book_inputType = intent.getStringExtra("inputType");

        if(intentType.equals("create")) {

            if(book_inputType.equals("search")) {

                Bundle bundle = intent.getExtras();
                book_info = bundle.getParcelable("book_info");

            } else if(book_inputType.equals("direct")) {

                Log.d(TAG, "myGetIntent: ");
                direct_title = intent.getStringExtra("title");
                direct_author = intent.getStringExtra("author");
                direct_imageBase64 = intent.getStringExtra("cover");
            }

        } else if(intentType.equals("update")) {
            review_board_id = intent.getStringExtra("review_board_id");
        }




    }


    private void setView() {
        ActionBar bar = getSupportActionBar();
        bar.hide();


        /* --------------------------- */
        // 앱바 상단
        btn_back = (Button) findViewById(R.id.btn_rc2_back);
        tv_review_create = (TextView) findViewById(R.id.tv_rc2_review_complete);

        /* --------------------------- */


        /* --------------------------- */
        //책 표지
        iv_cover = findViewById(R.id.iv_rc2_cover);
        tv_title = findViewById(R.id.tv_rc2_title);
        tv_author = findViewById(R.id.tv_rc2_author);

        btn_book_update = findViewById(R.id.btn_rc2_book_update);
        /* --------------------------- */


        /* --------------------------- */
        //리뷰 관련
        tv_review_text = (TextView) findViewById(R.id.tv_rc2_review_text);

        /* --------------------------- */


        /* --------------------------- */
        //사진 관련
        ll_go_gallery = findViewById(R.id.ll_rc2_go_gallery);
        tv_photo_count = (TextView) findViewById(R.id.tv_rc2_count);
        rcy_photos = (RecyclerView) findViewById(R.id.rcyv_rc2_photos);
        /* --------------------------- */

        setClickEvent();

        if(intentType.equals("create")) {
            setViewData();
        } else if(intentType.equals("update")) {
            setViewLoad();
        }

    }

    private void setViewData() {
        /* --------------------------- */
        //책 표지
        if(book_inputType.equals("search")) {
            Glide.with(aContext)
                    .load(book_info.getCover())
                    .error(R.drawable.ic_baseline_error_24)
                    .into(iv_cover);
            tv_title.setText(book_info.getTitle());
            tv_author.setText(book_info.getAuthor());

        } else if(book_inputType.equals("direct")) {

            Bitmap imageBitmap = MyImageFunc.getBitmap_FromBase64Image(direct_imageBase64);
            iv_cover.setImageBitmap(imageBitmap);
            tv_title.setText(direct_title);
            tv_author.setText(direct_author);
        }
        /* --------------------------- */
    }

    private void setViewLoad() {

        MyVolleyConnection myVolleyConnection = new MyVolleyConnection(1, aContext);
        myVolleyConnection.setURL("/review/review_update_upload.php");
        myVolleyConnection.addParams("review_board_id", review_board_id);
        myVolleyConnection.setVolley(new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                response_setViewLoad(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        myVolleyConnection.requestVolley();

    }

    private void response_setViewLoad(String response) {
        Log.d(TAG, "response_setViewLoad: "+response);
        JSONObject entryJsonObject = null;
        try {
            entryJsonObject = new JSONObject(response);
            String success = entryJsonObject.getString("success");

            if(success.equals("false")) {
                String fail_reason = entryJsonObject.getString("reason");
                Toast.makeText(aContext, fail_reason, Toast.LENGTH_LONG).show();

            } else {

                JSONArray jsonArray1 = entryJsonObject.getJSONArray("data");

                for(int i =0; i< jsonArray1.length(); i++) {

                    JSONObject jsonObject = jsonArray1.getJSONObject(i);

                    review_info = new Review_info();
                    User_info user_info = new User_info();
                    book_info = new Book_info();
                    ArrayList<String> review_images = new ArrayList<>();


                    String user_id = jsonObject.getString("user_id");
                    String profile_photo = jsonObject.getString("profile_photo");
                    String nickname = jsonObject.getString("nickname");

                    String isbn13 = jsonObject.getString("isbn13");
                    String title = jsonObject.getString("title");
                    String author = jsonObject.getString("author");
                    String pubDate = jsonObject.getString("pubDate");
                    String publisher = jsonObject.getString("publisher");
                    String cover = jsonObject.getString("cover");
                    book_inputType = jsonObject.getString("book_input_type");

                    String review_id = jsonObject.getString("review_id");
                    String register_date = jsonObject.getString("register_date");
//                    String recommendation = jsonObject.getString("recommendation");
                    String content = jsonObject.getString("content");

                    JSONArray imageArray = jsonObject.getJSONArray("review_images");
                    for(int j=0; j<imageArray.length(); j++) {

                        String image = imageArray.getString(j);
                        review_images.add(image);
                    }


                    user_info.setUser_id(user_id);
                    user_info.setUser_nickname(nickname);
                    user_info.setUser_profile(profile_photo);

                    book_info.setIsbn13(isbn13);
                    book_info.setTitle(title);
                    book_info.setAuthor(author);
                    book_info.setPubDate(pubDate);
                    book_info.setPublisher(publisher);
                    book_info.setCover(cover);


                    review_info.setUser_info(user_info);
                    review_info.setBook_info(book_info);

                    review_info.setReview_id(review_id);
                    review_info.setWriteDate(register_date);
                    review_info.setReview_text(content);
                    review_info.setReview_images(review_images);

                    final_setViewLoad();

                }

            }


        } catch (JSONException e) {
            Log.d(TAG, "JSONException: "+e);
        }
    }

    private void final_setViewLoad() {
        /* --------------------------- */
        //도서 관련
        if(book_inputType.equals("search")) {
            Glide.with(aContext)
                    .load(review_info.getBook_info().getCover())
                    .error(R.drawable.ic_baseline_error_24)
                    .into(iv_cover);
            tv_title.setText(review_info.getBook_info().getTitle());
            tv_author.setText(review_info.getBook_info().getAuthor());

        } else if(book_inputType.equals("direct")) {
            setBookCoverBitmap_FromURL(review_info.getBook_info().getCover());

            direct_title = review_info.getBook_info().getTitle();
            direct_author = review_info.getBook_info().getAuthor();

            tv_title.setText(direct_title);
            tv_author.setText(direct_author);
        }


        /* --------------------------- */


        /* --------------------------- */
        //리뷰 관련
        tv_review_text.setText(review_info.getReview_text());
        /* --------------------------- */


        /* --------------------------- */
        //사진 관련
        for(int i=0; i<review_info.getReview_images().size(); i++) {
            String imageUrl = review_info.getReview_images().get(i);
            setBitmap_FromURL(imageUrl);
        }

        /* --------------------------- */

    }


    private void setBookCoverBitmap_FromURL(String realImageURL) {
        Glide.with(aContext).asBitmap().load(realImageURL)
                .into(new CustomTarget<Bitmap>() {
                          @Override
                          public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                              iv_cover.setImageBitmap(resource);
                              direct_imageBase64 = MyImageFunc.getBase64Image_FromBitmap(resource);
                          }

                          @Override
                          public void onLoadCleared(@Nullable Drawable placeholder) {

                          }
                      }
                );

    }

    private void setBitmap_FromURL(String imageURL) {
        String realImageURL = "http://"+MyVolleyConnection.IP+imageURL;
        Glide.with(aContext).asBitmap().load(realImageURL)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        adt_photos.addItem(resource);
                        bimapArrayList.add(resource);
                        adt_photos.notifyDataSetChanged();
                        tv_photo_count.setText(Integer.toString(bimapArrayList.size()) + " / 10");
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                }
                );
    }

    private void setClickEvent() {
        /* --------------------------- */
        // 앱바 상단
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        tv_review_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(intentType.equals("create")) {
                    upload_review();
                } else if(intentType.equals("update")) {
                    update_review();
                }

            }

        });
        /* --------------------------- */


        /* --------------------------- */
        //책 표지
        btn_book_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(intentType.equals("create")) {

                    finish();

                } else if(intentType.equals("update")) {

                    Intent intent = null;
                    if(book_inputType.equals("search")) {
                        intent = new Intent(aContext, activity_review_create_001.class);
                    } else if(book_inputType.equals("direct")) {
                        intent = new Intent(aContext, activity_review_create_001_direct.class);

                        intent.putExtra("update_cover", direct_imageBase64);
                        intent.putExtra("update_title", review_info.getBook_info().getTitle());
                        intent.putExtra("update_author", review_info.getBook_info().getAuthor());
                    }

                    intent.putExtra("update", "true");
                    startActivityForResult(intent, 3);

                }

            }
        });
        /* --------------------------- */


        /* --------------------------- */
        //리뷰 관련
        tv_review_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(aContext, activity_review_create_002_text_input.class);
                review_info.setReview_text(tv_review_text.getText().toString());
                intent.putExtra("ori_text", review_info.getReview_text());
                startActivityForResult(intent, 2);
            }
        });

        ll_go_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upload_multi_photo();
            }
        });
        /* --------------------------- */


        /* --------------------------- */
        //사진 관련
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(aContext, RecyclerView.HORIZONTAL, false);
        rcy_photos.setLayoutManager(linearLayoutManager);

        adt_photos = new Adt_rc2_photos();
        adt_photos.setOnItemClickListener(new Adt_rc2_photos.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                Log.d(TAG, "onItemClick: "+pos);
                bimapArrayList.remove(pos);
                tv_photo_count.setText(Integer.toString(bimapArrayList.size())+" / 10" );
            }
        });
        rcy_photos.setAdapter(adt_photos);
        /* --------------------------- */
    }


    private void upload_review() {

        String input_text = tv_review_text.getText().toString();
        if(input_text.length() == 0) {
            Toast.makeText(aContext, "리뷰를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        review_info.setReview_text(input_text);
        review_info.setBook_info(book_info);
        review_info.setBitmapArrayList(bimapArrayList);

        //서버로 보내주면 된다.

        MyVolleyConnection myVolleyConnection = new MyVolleyConnection(1, aContext);
        myVolleyConnection.setURL("/review/review_create.php");

        myVolleyConnection.addParams("user_id", LoginSharedPref.getUserId(aContext));
        myVolleyConnection.addParams("text", review_info.getReview_text());

        if(book_inputType.equals("search")) {
            myVolleyConnection.addParams("type", "search");
            myVolleyConnection.addParams("book_info", book_info.getJsonString());

        } else if(book_inputType.equals("direct")) {
            myVolleyConnection.addParams("type", "direct");
            myVolleyConnection.addParams("direct_title", direct_title);
            myVolleyConnection.addParams("direct_author", direct_author);
            myVolleyConnection.addParams("direct_imageBase64", direct_imageBase64);
        }


        // 배열 안 uri를 base 64의 string으로 변환시키고, 그걸 json오로 만들어서 전송한다.
        ArrayList<String> stringArrayList = MyImageFunc.getBase64ImageArray_FromBitmapArray(review_info.getBitmapArrayList(), aContext);
        String json_base64_images = getJsonStringFromArrayList(stringArrayList);
        myVolleyConnection.addParams("images", json_base64_images);

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
                        String review_board_id = jsonObject.getString("review_board_id");
                        Log.d(TAG, "onResponse: "+review_board_id);

                        goReadDetail(review_board_id);
                        progressDialog.dismiss();
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

        progressDialog.setMessage("글 게시 중 입니다.");
        progressDialog.show();
    }

    private void update_review() {
        String input_text = tv_review_text.getText().toString();
        if(input_text.length() == 0) {
            Toast.makeText(aContext, "리뷰를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        review_info.setReview_text(input_text);
        review_info.setBook_info(book_info);
        Log.d(TAG, "update_review: "+review_info.getBook_info().getJsonString());
        review_info.setBitmapArrayList(bimapArrayList);

        //서버로 보내주면 된다.

        MyVolleyConnection myVolleyConnection = new MyVolleyConnection(1, aContext);
        myVolleyConnection.setURL("/review/review_update.php");

        myVolleyConnection.addParams("user_id", LoginSharedPref.getUserId(aContext));
        myVolleyConnection.addParams("text", review_info.getReview_text());
        myVolleyConnection.addParams("review_board_id", review_board_id);

        if(book_inputType.equals("search")) {
            myVolleyConnection.addParams("type", "search");
            myVolleyConnection.addParams("book_info", review_info.getBook_info().getJsonString());

        } else if(book_inputType.equals("direct")) {
            myVolleyConnection.addParams("type", "direct");
            myVolleyConnection.addParams("direct_title", direct_title);
            myVolleyConnection.addParams("direct_author", direct_author);
            myVolleyConnection.addParams("direct_imageBase64", direct_imageBase64);
        }


        // 배열 안 bitmapd을 base 64의 string으로 변환시키고, 그걸 json오로 만들어서 전송한다.
        ArrayList<String> stringArrayList = MyImageFunc.getBase64ImageArray_FromBitmapArray(review_info.getBitmapArrayList(), aContext);
        String json_base64_images = getJsonStringFromArrayList(stringArrayList);
        myVolleyConnection.addParams("images", json_base64_images);

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
                        String review_board_id = jsonObject.getString("review_board_id");
                        Log.d(TAG, "onResponse: "+review_board_id);

                        goReadDetail(review_board_id);
                        progressDialog.dismiss();
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

        progressDialog.setMessage("글 수정 중 입니다.");
        progressDialog.show();

    }

    private void goReadDetail(String board_id) {
        Intent intent = new Intent(aContext, activity_review_read_detail.class);
        intent.putExtra("review_board_id", board_id);

        if(activity_review_create_001.act_rc1 != null) {
            activity_review_create_001.act_rc1.finish();
        }

        if(activity_review_create_001_direct.act_rc1_direct != null) {
            activity_review_create_001_direct.act_rc1_direct.finish();
        }

        startActivity(intent);
        finish();
    }

    private void goReadDetailFromUpdate(String board_id) {
        Intent intent = new Intent(aContext, activity_review_read_detail.class);
        intent.putExtra("review_board_id", board_id);

        if(activity_review_create_001.act_rc1 != null) {
            activity_review_create_001.act_rc1.finish();
        }

        if(activity_review_create_001_direct.act_rc1_direct != null) {
            activity_review_create_001_direct.act_rc1_direct.finish();
        }

        startActivity(intent);
        finish();
    }





    private void upload_multi_photo() {

        if(bimapArrayList.size() >= 10) {
            Toast.makeText(aContext, "총 10장까지만 입력됩니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = MyImageFunc.upload_multi_photo();
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {

                if(data == null) { // 선택 안했을 때

                } else {
                    if(data.getClipData() == null) { //한 장
                        Uri imageUri = data.getData();
                        Bitmap imageBitmap = MyImageFunc.getBitmapImage_FromUri(imageUri, aContext);

                        if(MyImageFunc.dpCheck_bitmapList(bimapArrayList, imageBitmap)) { // 중복 체크
                            Toast.makeText(aContext, "중복된 사진이 존재합니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        adt_photos.addItem(imageBitmap);
                    } else { //여러장

                        ClipData clipData = data.getClipData();

                        int upload_count;
                        int max_count = 10 - bimapArrayList.size();

                        if(clipData.getItemCount() > max_count) {
                            Toast.makeText(aContext, "총 10장까지만 입력됩니다.", Toast.LENGTH_SHORT).show();
                            upload_count = max_count;
                        } else {
                            upload_count = clipData.getItemCount();
                        }

                        for (int i=0; i<upload_count; i++) {
                            Uri imageUri = clipData.getItemAt(i).getUri();
                            Bitmap imageBitmap = MyImageFunc.getBitmapImage_FromUri(imageUri, aContext);

                            try {
                                if(MyImageFunc.dpCheck_bitmapList(bimapArrayList, imageBitmap)) { // 중복 체크
                                    Toast.makeText(aContext, "중복된 사진이 존재합니다.", Toast.LENGTH_SHORT).show();
                                    continue;
                                }
                                adt_photos.addItem(imageBitmap);
                            } catch (Exception e) {
                                Log.d(TAG, "onActivityResult: 파일 에러"+ e);
                            }
                        }
                    }
                    tv_photo_count.setText(Integer.toString(bimapArrayList.size())+" / 10" );
                    adt_photos.notifyDataSetChanged();
                }


            }
        } else if (requestCode == 2) {
            if(resultCode == RESULT_OK) {
                String text = data.getStringExtra("new_text");
                review_info.setReview_text(text);
                tv_review_text.setText(text);
            }
        } else if(requestCode == 3) {
            if(resultCode == RESULT_OK) {

                if(book_inputType.equals("search")) {

                    Bundle bundle = data.getExtras();
                    book_info = bundle.getParcelable("book_info");

                    Glide.with(aContext)
                            .load(book_info.getCover())
                            .error(R.drawable.ic_baseline_error_24)
                            .into(iv_cover);
                    tv_title.setText(book_info.getTitle());
                    tv_author.setText(book_info.getAuthor());

                } else if(book_inputType.equals("direct")) {

                    direct_title = data.getStringExtra("update_title");
                    direct_author = data.getStringExtra("update_author");


//                    byte[] byteArray = getIntent().getByteArrayExtra("update_cover");
//                    Bitmap imageBitmap = MyImageFunc.getBitmap_FromByteArrayImage(byteArray);

                    direct_imageBase64 = data.getStringExtra("update_cover");
                    Bitmap imageBitmap = MyImageFunc.getBitmap_FromBase64Image(direct_imageBase64);
                    iv_cover.setImageBitmap(imageBitmap);
                    tv_title.setText(direct_title);
                    tv_author.setText(direct_author);

                }
            }
        }
    }


    public String getJsonStringFromArrayList(ArrayList<String> stringArrayList) {
        JSONObject jsonObject = new JSONObject();

        try {
            for (int i =0; i < stringArrayList.size(); i++) {
                String index = Integer.toString(i);
                jsonObject.put(index, stringArrayList.get(i));
            }
        } catch (Exception e) {

        }

        return jsonObject.toString();
    }


}