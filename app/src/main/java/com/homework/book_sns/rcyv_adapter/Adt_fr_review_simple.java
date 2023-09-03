package com.homework.book_sns.rcyv_adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.homework.book_sns.R;
import com.homework.book_sns.javaclass.Book_info;
import com.homework.book_sns.javaclass.LoginSharedPref;
import com.homework.book_sns.javaclass.MyVolleyConnection;
import com.homework.book_sns.javaclass.Noti_info;
import com.homework.book_sns.javaclass.Noti_msg;
import com.homework.book_sns.javaclass.Review_info;
import com.homework.book_sns.javaclass.Review_list_simple_info;
import com.homework.book_sns.service_noti;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class Adt_fr_review_simple extends RecyclerView.Adapter<Adt_fr_review_simple.ViewHoder> {

    String TAG = "hch";

    private ArrayList<Review_list_simple_info> items = new ArrayList<>();

    @NonNull
    @Override
    public ViewHoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.item_review_read_simple, parent, false);

        return new ViewHoder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHoder holder, int position) {
        Review_list_simple_info item = items.get(position);

        holder.ll_review_image.removeAllViews(); // 이미지 뷰 모두 삭제
        holder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public void addItem(Review_list_simple_info item) {
        items.add(item);
    }


    public void removeItem(int position) {
        items.remove(position);
    }

    public int getItemSize() {
        return items.size();
    }

    public void clearItem() {
        items.clear();
        notifyDataSetChanged();
    }

    public void change_followAllState() {
        for(int i =0; i < items.size(); i++) {
            items.get(i).setFollowing(true);
        }
    }

    public void change_unFollowAllState() {
        for(int i =0; i < items.size(); i++) {
            items.get(i).setFollowing(false);
        }
    }

    public String getReviewBoardId (int position) {
        return items.get(position).getReview_id();
    }

    public String getWriterId(int position) {
        return  items.get(position).getUser_info().getUser_id();
    }


    public interface OnItemClickListener {
        void onReviewOptionClick(View v, int pos);
        void onFollow(View v, int pos);
        void onFollowCancel(View v, int pos);
    }

    private OnItemClickListener mListener = null;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }


    class ViewHoder extends RecyclerView.ViewHolder {

        Context mContext;
        ViewGroup rootView;

        CircleImageView civ_profile_photo;
        TextView tv_nickname;
        TextView tv_follow;
        TextView tv_review_date;

        Button btn_review_option;

        TextView tv_review_text;
        LinearLayout ll_review_image;

        ImageView iv_cover;
        TextView tv_title;
        TextView tv_author;

        LinearLayout ll_count;
        TextView tv_recommendation_count;
        TextView tv_reply_count;

        Button btn_recommendation;
        Button btn_reply;

        public ViewHoder(@NonNull View itemView) {
            super(itemView);

            mContext = itemView.getContext();

            civ_profile_photo = (CircleImageView) itemView.findViewById(R.id.civ_irrs_profile_photo);
            tv_nickname = (TextView) itemView.findViewById(R.id.tv_irrs_nickname);

            tv_follow = (TextView) itemView.findViewById(R.id.tv_irrs_follow);
            tv_review_date = (TextView) itemView.findViewById(R.id.tv_irrs_review_date);

            btn_review_option = (Button) itemView.findViewById(R.id.btn_irrs_plus_option);

            tv_review_text = (TextView) itemView.findViewById(R.id.tv_irrs_review_text);
            ll_review_image = (LinearLayout) itemView.findViewById(R.id.ll_irrs_review_image);

            iv_cover = (ImageView) itemView.findViewById(R.id.iv_irrs_book_cover);
            tv_title = (TextView) itemView.findViewById(R.id.tv_irrs_book_title);
            tv_author = (TextView) itemView.findViewById(R.id.tv_irrs_book_author);

            ll_count = (LinearLayout) itemView.findViewById(R.id.ll_irrs_count);
            tv_recommendation_count = (TextView) itemView.findViewById(R.id.tv_irrs_recommendation_count);
            tv_reply_count = (TextView) itemView.findViewById(R.id.tv_irrs_reply_count);

            btn_recommendation = (Button) itemView.findViewById(R.id.btn_irrs_recommendation);
            btn_reply = (Button) itemView.findViewById(R.id.btn_irrs_reply);

            rootView = (ViewGroup) itemView;
        }

        public void setItem(Review_list_simple_info item) {
            String image_url = item.getUser_info().getUser_profile();
            Glide.with(mContext)
                    .load(image_url)
                    .error(R.drawable.ic_baseline_error_24)
                    .into(civ_profile_photo);
            tv_nickname.setText(item.getUser_info().getUser_nickname());

            tv_review_date.setText(item.getWriteDate());
            set_review_text(item.getReview_text(), tv_review_text); // 리뷰 컨텐츠 설정
            
            setReviewImage(item); // 리뷰 이미지 설정
                        

            Glide.with(mContext)
                    .load(item.getBook_info().getCover())
                    .error(R.drawable.ic_baseline_error_24)
                    .into(iv_cover);
            tv_title.setText(item.getBook_info().getTitle());
            tv_author.setText(item.getBook_info().getAuthor());


            if(item.isFollowing()) {
                tv_follow.setVisibility(View.INVISIBLE); // 이미 팔로잉 되있으면 팔로우 버튼을 없앰.
            } else  {
                tv_follow.setVisibility(View.VISIBLE);
            }

            if(item.getUser_info().getUser_id().equals(LoginSharedPref.getUserId(mContext))) {
                tv_follow.setVisibility(View.INVISIBLE);
            } // 자기 자신이면 팔로우 버튼을 없앰.

            // 여기에 item의 추천 상태에 따라서 selector를 변화시켜야한다..


            if(item.getRecommendCount() == 0 && item.getReplyCount() == 0) {
                tv_recommendation_count.setVisibility(View.GONE);
                tv_reply_count.setVisibility(View.GONE);
            } else {
                if(item.getRecommendCount() == 0) {
                    tv_recommendation_count.setVisibility(View.INVISIBLE);
                } else {
                    tv_recommendation_count.setText("추천 "+Integer.toString(item.getRecommendCount())+" 개");
                    tv_recommendation_count.setVisibility(View.VISIBLE);
                }

                if(item.getReplyCount() == 0) {
                    tv_reply_count.setVisibility(View.INVISIBLE);
                } else {
                    tv_reply_count.setText("댓글 "+Integer.toString(item.getReplyCount())+" 개");
                    tv_reply_count.setVisibility(View.VISIBLE);
                }
            }

            // client의 추천 상태에 따라 버튼의 활성의 여부를 판단해야한다.
            btn_recommendation.setEnabled(true);
            if(items.get(getAdapterPosition()).isClient_recommendation()) {
                btn_recommendation.setSelected(true);
            } else {
                btn_recommendation.setSelected(false);
            }



            setClickEvent(item);

        }

        private void setClickEvent(Review_list_simple_info item) {
            civ_profile_photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, com.homework.book_sns.activity_member_page.class);
                    item.getUser_info().setFollowing(item.isFollowing());
                    intent.putExtra("user_info", item.getUser_info());
                    intent.putExtra("type", "review");
                    mContext.startActivity(intent);
                }
            });

            tv_nickname.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, com.homework.book_sns.activity_member_page.class);
                    item.getUser_info().setFollowing(item.isFollowing());
                    intent.putExtra("user_info", item.getUser_info());
                    intent.putExtra("type", "review");
                    mContext.startActivity(intent);
                }
            });

            tv_review_text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, com.homework.book_sns.act_review.activity_review_read_detail.class);
                    intent.putExtra("review_board_id",item.getReview_id());
                    intent.putExtra("type", "normal");
                    mContext.startActivity(intent);
                }
            });

            btn_review_option.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION) {
//                        mListener.onReviewOptionClick(view, position);
                        set_review_option(position, view);
                    }
                }
            });

            tv_follow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    set_follow_btn(item, getAdapterPosition());
                    mListener.onFollow(view, getAdapterPosition());
                }
            });


            ll_review_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, com.homework.book_sns.act_review.activity_review_read_detail.class);
                    intent.putExtra("review_board_id",item.getReview_id());
                    mContext.startActivity(intent);
                }
            });

            tv_recommendation_count.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, com.homework.book_sns.act_review.activity_recommendation.class);
                    intent.putExtra("review_board_id",item.getReview_id());
                    mContext.startActivity(intent);
                }
            });

            btn_recommendation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int position = getAdapterPosition();

                    if(btn_recommendation.isSelected()) { // 추천된 상태 => 추천 취소
//                        btn_recommendation.setSelected(false);
                        btn_recommendation.setEnabled(false);
                        set_recommendation_cancel_btn(position);


                    } else { // 추천하지 않은 상태 => 추천
//                        btn_recommendation.setSelected(true);
                        btn_recommendation.setEnabled(false);
                        set_recommendation_btn(position);
                    }
                }
            });

            btn_reply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    
                }
            });
        }

        private void set_recommendation_btn (int position) {
            MyVolleyConnection myVolleyConnection = new MyVolleyConnection(1, mContext);
            myVolleyConnection.setURL("/review/review_recommendation.php");
            myVolleyConnection.addParams("review_board_id", items.get(position).getReview_id());
            myVolleyConnection.addParams("client_id", LoginSharedPref.getUserId(mContext));
            myVolleyConnection.setVolley(new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d(TAG, "response_set_follow_btn: "+response);
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(response);
                        String success = jsonObject.getString("success");

                        if(success.equals("false")) {
                            String fail_reason = jsonObject.getString("reason");
                            Toast.makeText(mContext, fail_reason, Toast.LENGTH_LONG).show();

                        } else {
//                            Toast.makeText(mContext, "추천하였습니다", Toast.LENGTH_SHORT).show();
                            items.get(position).setClient_recommendation(true);
                            items.get(position).addRecommendCount();

                            send_recommendation_noti(position); // 추천 전송
                            btn_recommendation.setSelected(true);
                            notifyItemChanged(position);
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

        private void send_recommendation_noti (int position) {
            Review_list_simple_info review_info = items.get(position);

            Noti_info noti_info = new Noti_info
                    (Integer.parseInt(LoginSharedPref.getUserId(mContext)),
                            LoginSharedPref.getPrefNickname(mContext),
                            LoginSharedPref.getPrefProfilePhoto(mContext),
                            Integer.parseInt(review_info.getUser_info().getUser_id()),
                            "추천",
                            Integer.parseInt(review_info.getReview_id()),
                            -9999
                    );
            String jsonContent = noti_info.toJsonString();
            Noti_msg noti_msg = new Noti_msg("noti", jsonContent);
            String jsonMsg = noti_msg.toJsonString();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        Log.d(TAG, "noti (추천) 발송: ");
                        service_noti.notiWriter.println(jsonMsg);
                        service_noti.notiWriter.flush();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        private void set_recommendation_cancel_btn (int position) {
            MyVolleyConnection myVolleyConnection = new MyVolleyConnection(1, mContext);
            myVolleyConnection.setURL("/review/review_recommendation_cancel.php");
            myVolleyConnection.addParams("review_board_id", items.get(position).getReview_id());
            myVolleyConnection.addParams("client_id", LoginSharedPref.getUserId(mContext));
            myVolleyConnection.setVolley(new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d(TAG, "response_set_follow_btn: "+response);
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(response);
                        String success = jsonObject.getString("success");

                        if(success.equals("false")) {
                            String fail_reason = jsonObject.getString("reason");
                            Toast.makeText(mContext, fail_reason, Toast.LENGTH_LONG).show();

                        } else {
//                            Toast.makeText(mContext, "추천 취소하였습니다", Toast.LENGTH_SHORT).show();
                            items.get(position).setClient_recommendation(false);
                            items.get(position).minusRecommendCount();
//                            notifyItemChanged(position);
                            btn_recommendation.setSelected(false);

                            notifyItemChanged(position);
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




        private void set_review_option(int pos, View clickView) {
            String writer_id = items.get(pos).getUser_info().getUser_id();
            PopupMenu popupMenu = new PopupMenu(mContext, clickView);

            if(writer_id.equals(LoginSharedPref.getUserId(mContext))) {
                popupMenu.getMenuInflater().inflate(R.menu.review_option_popup, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        Log.d(TAG, "onMenuItemClick: ");
                        switch (menuItem.getItemId()) {
                            case R.id.review_option_menu1:
                                Intent intent = new Intent(mContext, com.homework.book_sns.act_review.activity_review_create_002.class);
                                intent.putExtra("intentType", "update");

                                String review_board_id = items.get(pos).getReview_id();
                                intent.putExtra("review_board_id", review_board_id);
                                mContext.startActivity(intent);
                                return true;
                            case R.id.review_option_menu2:
                                removeReviewDate(items.get(pos).getReview_id());
                                items.remove(pos);
                                notifyItemRemoved(pos);

                                return true;
                        }
                        return false;
                    }
                });

            } else {
                if(!items.get(pos).isFollowing()) {
                    popupMenu.getMenuInflater().inflate(R.menu.review_option_popup2, popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            Log.d(TAG, "onMenuItemClick: ");
                            switch (menuItem.getItemId()) {
                                case R.id.review_option2_menu1:
                                    return true;
                            }
                            return false;
                        }
                    });
                } else {
                    popupMenu.getMenuInflater().inflate(R.menu.review_option_popup3, popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            Log.d(TAG, "onMenuItemClick: ");
                            switch (menuItem.getItemId()) {
                                case R.id.review_option3_menu1:
                                    return true;
                                case R.id.review_option3_menu2:
                                    set_follow_cancel_popup(items.get(pos), pos);
                                    mListener.onFollowCancel(clickView, pos);
                                    return true;
                            }
                            return false;
                        }
                    });

                }
            }
            popupMenu.show();
        }

        private void set_follow_btn(Review_list_simple_info item, int pos) {
            MyVolleyConnection myVolleyConnection = new MyVolleyConnection(1, mContext);
            myVolleyConnection.setURL("/review/review_follow_request.php");
            myVolleyConnection.addParams("user_id", LoginSharedPref.getUserId(mContext));
            myVolleyConnection.addParams("following_id", item.getUser_info().getUser_id());
            myVolleyConnection.setVolley(new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    response_set_follow_btn(response, pos);
                    Toast.makeText(mContext, item.getUser_info().getUser_nickname()+"님과 팔로우 되었습니다.", Toast.LENGTH_SHORT).show();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            myVolleyConnection.requestVolley();
        }

        private void response_set_follow_btn(String response, int pos) {
            Log.d(TAG, "response_set_follow_btn: "+response);
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(response);
                String success = jsonObject.getString("success");

                if(success.equals("false")) {
                    String fail_reason = jsonObject.getString("reason");
                    Toast.makeText(mContext, fail_reason, Toast.LENGTH_LONG).show();

                } else {
                    apply_follow(items.get(pos).getUser_info().getUser_id());
                }


            } catch (JSONException e) {
                Log.d(TAG, "JSONException: ");
            }
        }

        private void apply_follow(String following_cancel_id) {
            for(int i = 0; i < items.size(); i++) {
                if(items.get(i).getUser_info().getUser_id().equals(following_cancel_id)) {
                    items.get(i).setFollowing(true);
                }
            }
            notifyDataSetChanged();
        }

        private void set_follow_cancel_popup(Review_list_simple_info item, int position) {
            MyVolleyConnection myVolleyConnection = new MyVolleyConnection(1, mContext);
            myVolleyConnection.setURL("/review/review_follow_cancel.php");
            myVolleyConnection.addParams("user_id", LoginSharedPref.getUserId(mContext));
            myVolleyConnection.addParams("following_id", item.getUser_info().getUser_id());
            myVolleyConnection.setVolley(new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    response_set_follow_cancel_popup(response, position);
                    Toast.makeText(mContext, item.getUser_info().getUser_nickname()+"님과의 팔로잉이 취소되었습니다.", Toast.LENGTH_SHORT).show();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            myVolleyConnection.requestVolley();
        }

        private void response_set_follow_cancel_popup(String response, int position) {
            Log.d(TAG, "response_set_follow_btn: "+response);
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(response);
                String success = jsonObject.getString("success");

                if(success.equals("false")) {
                    String fail_reason = jsonObject.getString("reason");
                    Toast.makeText(mContext, fail_reason, Toast.LENGTH_LONG).show();

                } else {
                    apply_follow_cancel(items.get(position).getUser_info().getUser_id());
                }


            } catch (JSONException e) {
                Log.d(TAG, "JSONException: ");
            }
        }

        private void apply_follow_cancel(String following_cancel_id) {
            for(int i = 0; i < items.size(); i++) {
                if(items.get(i).getUser_info().getUser_id().equals(following_cancel_id)) {
                    items.get(i).setFollowing(false);
                }
            }
            notifyDataSetChanged();
        }


        private void removeReviewDate(String review_board_id) {
            Log.d(TAG, "removeReviewDate: "+review_board_id);
            MyVolleyConnection myVolleyConnection = new MyVolleyConnection(1, mContext);
            myVolleyConnection.setURL("/review/review_delete.php");
            myVolleyConnection.addParams("review_board_id", review_board_id);
            myVolleyConnection.setVolley(new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d(TAG, "onResponse: "+response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            myVolleyConnection.requestVolley();

        }


        private void set_review_text(String target, TextView review_text) {
            String[] split_target = target.split("\\n");

            String change_target_str = "";
            SpannableString change_target_spanStr;


            int str_length = split_target.length;
            if(str_length > 4) {
                str_length = 4;
                change_target_spanStr = make_4overString(split_target, str_length);
                review_text.setText(change_target_spanStr);
            } else {
                change_target_str = make_4underString(split_target, str_length);
                review_text.setText(change_target_str);
            }
            
            
        }

        private String make_4underString(String[] split_target, int str_length) {

            String change_target = "";

            for(int i=0; i<str_length; i++) {
                change_target += split_target[i];

                if(i != 3) {
                    change_target += "\n";
                }
            }

            return change_target;

        }

        private SpannableString make_4overString(String[] split_target, int str_length) {

            String change_target = "";

            for(int i=0; i<str_length; i++) {
                change_target += split_target[i];

                if(i != 3) {
                    change_target += "\n";
                }
            }

            change_target += "... 더보기";

            SpannableString spannableString = new SpannableString(change_target);

            int start = change_target.indexOf(change_target);
            int end = start + change_target.length();
            spannableString.setSpan(
                    new ForegroundColorSpan(Color.parseColor("#0000FF")),
                    end-6, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(
                    new StyleSpan(Typeface.BOLD),
                    end-6, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


            return spannableString;
        }
        
        private void setReviewImage(Review_list_simple_info item) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);

            View ll_imageView;
            ImageView imageView1;
            ImageView imageView2;
            ImageView imageView3;
            ImageView imageView4;

            ll_review_image.setVisibility(View.VISIBLE);
            int size_review_images = item.getReview_images().size();
            if(size_review_images == 1) {
                if(item.getReview_images().get(0) != "null") {
                    ll_imageView = inflater.inflate(R.layout.view_irrs_image1, ll_review_image, true);
                    imageView1 = (ImageView) ll_imageView.findViewById(R.id.iv_virrs_i1);

                    String image1_url = "http://"+MyVolleyConnection.IP
                            + item.getReview_images().get(0);
                    Glide.with(mContext)
                            .load(image1_url)
                            .error(R.drawable.ic_baseline_error_24)
                            .into(imageView1);

                } else {
                    ll_review_image.setVisibility(View.GONE);
                }
            } else if(size_review_images == 2) {

                ll_imageView = inflater.inflate(R.layout.view_irrs_image2, ll_review_image, true);
                imageView1 = (ImageView) ll_imageView.findViewById(R.id.iv_virrs_i2_1);
                imageView2 = (ImageView) ll_imageView.findViewById(R.id.iv_virrs_i2_2);

                String image1_url = "http://"+MyVolleyConnection.IP
                        + item.getReview_images().get(0);
                Glide.with(mContext)
                        .load(image1_url)
                        .error(R.drawable.ic_baseline_error_24)
                        .into(imageView1);

                String image2_url = "http://"+MyVolleyConnection.IP
                        + item.getReview_images().get(1);
                Glide.with(mContext)
                        .load(image2_url)
                        .error(R.drawable.ic_baseline_error_24)
                        .into(imageView2);

            } else if(size_review_images == 3) {

                ll_imageView = inflater.inflate(R.layout.view_irrs_image3, ll_review_image, true);
                imageView1 = (ImageView) ll_imageView.findViewById(R.id.iv_virrs_i3_1);
                imageView2 = (ImageView) ll_imageView.findViewById(R.id.iv_virrs_i3_2);
                imageView3 = (ImageView) ll_imageView.findViewById(R.id.iv_virrs_i3_3);

                String image1_url = "http://"+MyVolleyConnection.IP
                        + item.getReview_images().get(0);
                Glide.with(mContext)
                        .load(image1_url)
                        .error(R.drawable.ic_baseline_error_24)
                        .into(imageView1);

                String image2_url = "http://"+MyVolleyConnection.IP
                        + item.getReview_images().get(1);
                Glide.with(mContext)
                        .load(image2_url)
                        .error(R.drawable.ic_baseline_error_24)
                        .into(imageView2);

                String image3_url = "http://"+MyVolleyConnection.IP
                        + item.getReview_images().get(2);
                Glide.with(mContext)
                        .load(image3_url)
                        .error(R.drawable.ic_baseline_error_24)
                        .into(imageView3);

            } else if(size_review_images == 4) {
                ll_imageView = inflater.inflate(R.layout.view_irrs_image4, ll_review_image, true);
                imageView1 = (ImageView) ll_imageView.findViewById(R.id.iv_virrs_i4_1);
                imageView2 = (ImageView) ll_imageView.findViewById(R.id.iv_virrs_i4_2);
                imageView3 = (ImageView) ll_imageView.findViewById(R.id.iv_virrs_i4_3);
                imageView4 = (ImageView) ll_imageView.findViewById(R.id.iv_virrs_i4_4);

                String image1_url = "http://"+MyVolleyConnection.IP
                        + item.getReview_images().get(0);
                Glide.with(mContext)
                        .load(image1_url)
                        .error(R.drawable.ic_baseline_error_24)
                        .into(imageView1);

                String image2_url = "http://"+MyVolleyConnection.IP
                        + item.getReview_images().get(1);
                Glide.with(mContext)
                        .load(image2_url)
                        .error(R.drawable.ic_baseline_error_24)
                        .into(imageView2);

                String image3_url = "http://"+MyVolleyConnection.IP
                        + item.getReview_images().get(2);
                Glide.with(mContext)
                        .load(image3_url)
                        .error(R.drawable.ic_baseline_error_24)
                        .into(imageView3);

                String image4_url = "http://"+MyVolleyConnection.IP
                        + item.getReview_images().get(3);
                Glide.with(mContext)
                        .load(image4_url)
                        .error(R.drawable.ic_baseline_error_24)
                        .into(imageView4);

            } else if(size_review_images >= 5) {

                ll_imageView = inflater.inflate(R.layout.view_irrs_image5, ll_review_image, true);
                imageView1 = (ImageView) ll_imageView.findViewById(R.id.iv_virrs_i5_1);
                imageView2 = (ImageView) ll_imageView.findViewById(R.id.iv_virrs_i5_2);
                imageView3 = (ImageView) ll_imageView.findViewById(R.id.iv_virrs_i5_3);
                imageView4 = (ImageView) ll_imageView.findViewById(R.id.iv_virrs_i5_4);

                String image1_url = "http://"+MyVolleyConnection.IP
                        + item.getReview_images().get(0);
                Glide.with(mContext)
                        .load(image1_url)
                        .error(R.drawable.ic_baseline_error_24)
                        .into(imageView1);

                String image2_url = "http://"+MyVolleyConnection.IP
                        + item.getReview_images().get(1);
                Glide.with(mContext)
                        .load(image2_url)
                        .error(R.drawable.ic_baseline_error_24)
                        .into(imageView2);

                String image3_url = "http://"+MyVolleyConnection.IP
                        + item.getReview_images().get(2);
                Glide.with(mContext)
                        .load(image3_url)
                        .error(R.drawable.ic_baseline_error_24)
                        .into(imageView3);

                String image4_url = "http://"+MyVolleyConnection.IP
                        + item.getReview_images().get(3);
                Glide.with(mContext)
                        .load(image4_url)
                        .error(R.drawable.ic_baseline_error_24)
                        .into(imageView4);

            }
            
        }
    }


}
