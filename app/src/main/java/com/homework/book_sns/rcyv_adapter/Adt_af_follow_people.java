package com.homework.book_sns.rcyv_adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.homework.book_sns.R;
import com.homework.book_sns.javaclass.Follow_For_RCYV;
import com.homework.book_sns.javaclass.LoginSharedPref;
import com.homework.book_sns.javaclass.MyVolleyConnection;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class Adt_af_follow_people extends RecyclerView.Adapter<Adt_af_follow_people.ViewHolder> {
    String TAG = "hch";
    private ArrayList<Follow_For_RCYV> items = new ArrayList<>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.item_activity_follow, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Follow_For_RCYV item = items.get(position);

        holder.setItem(item);
    }

    public void addItem(Follow_For_RCYV item) {
        items.add(item);
    }

    public void clearItem() {
        items.clear();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends  RecyclerView.ViewHolder {

        Context mContext;

        CircleImageView civ_profile_photo;
        TextView tv_nickname;
        TextView tv_review_count;
        AppCompatButton btn_follow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mContext = itemView.getContext();

            civ_profile_photo = itemView.findViewById(R.id.civ_iaf_profile_photo);
            tv_nickname = itemView.findViewById(R.id.tv_iaf_nickname);
            tv_review_count = itemView.findViewById(R.id.tv_iaf_review_count);
            btn_follow = itemView.findViewById(R.id.btn_iaf_follow);

            setClickEvent();
        }

        private void setClickEvent() {
            btn_follow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(btn_follow.isSelected()) {
                        btn_follow.setSelected(false);
                        btn_follow.setText("팔로잉");
                        follow(items.get(getAdapterPosition()));
                    } else {
                        btn_follow.setSelected(true);
                        btn_follow.setText("팔로우");
                        unfollow(items.get(getAdapterPosition()));
                    }
                }
            });
        }

        public void setItem(Follow_For_RCYV item) {
            String image_url = "http://"+ MyVolleyConnection.IP+item.getProfile_photo();
            Glide.with(mContext)
                    .load(image_url)
                    .error(R.drawable.ic_baseline_error_24)
                    .into(civ_profile_photo);
            tv_nickname.setText(item.getUser_nickname());
            tv_review_count.setText("리뷰 수 : "+Integer.toString(item.getReview_count()));

            if(item.getUser_id().equals(LoginSharedPref.getUserId(mContext))) {
                btn_follow.setVisibility(View.INVISIBLE); // 자기 자신이면 안보이게.
            } else {
                btn_follow.setVisibility(View.VISIBLE); // 자신이 아니면 다시 보이게.
            }

            if(item.isClient_relationship()) {
                btn_follow.setSelected(false);
                btn_follow.setText("팔로잉");
            } else {
                btn_follow.setSelected(true);
                btn_follow.setText("팔로우");
            }
        }

        private void follow(Follow_For_RCYV item) {
            MyVolleyConnection myVolleyConnection = new MyVolleyConnection(1, mContext);
            myVolleyConnection.setURL("/review/review_follow_request.php");
            myVolleyConnection.addParams("user_id", LoginSharedPref.getUserId(mContext));
            myVolleyConnection.addParams("following_id", item.getUser_id());
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
                            Toast.makeText(mContext, fail_reason, Toast.LENGTH_LONG).show();

                        } else {
                            Toast.makeText(mContext, item.getUser_nickname()+" 님을 팔로잉하였습니다.", Toast.LENGTH_SHORT).show();
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

        private void unfollow(Follow_For_RCYV item) {
            MyVolleyConnection myVolleyConnection = new MyVolleyConnection(1, mContext);
            myVolleyConnection.setURL("/review/review_follow_cancel.php");
            myVolleyConnection.addParams("user_id", LoginSharedPref.getUserId(mContext));
            myVolleyConnection.addParams("following_id", item.getUser_id());
            myVolleyConnection.setVolley(new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d(TAG, "onResponse: "+response);
                    Log.d(TAG, "onResponse: "+response);
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(response);
                        String success = jsonObject.getString("success");

                        if(success.equals("false")) {
                            String fail_reason = jsonObject.getString("reason");
                            Toast.makeText(mContext, fail_reason, Toast.LENGTH_LONG).show();

                        } else {
                            Toast.makeText(mContext, item.getUser_nickname()+" 님을 팔로잉 해제하였습니다.", Toast.LENGTH_SHORT).show();
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
    }
}
