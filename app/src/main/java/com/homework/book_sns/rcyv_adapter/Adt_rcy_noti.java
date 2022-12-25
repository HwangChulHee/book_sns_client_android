package com.homework.book_sns.rcyv_adapter;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.homework.book_sns.R;
import com.homework.book_sns.javaclass.LoginSharedPref;
import com.homework.book_sns.javaclass.MyVolleyConnection;
import com.homework.book_sns.javaclass.Noti_info;
import com.homework.book_sns.javaclass.User_info;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class Adt_rcy_noti extends RecyclerView.Adapter<Adt_rcy_noti.ViewHolder> {

    private String TAG = "hch";
    private String RCY_NAME = "직접 입력";
    private String ACTIVITY_NAME;

    private Activity activity;

    private ArrayList<Noti_info> items = new ArrayList<>();

    public Adt_rcy_noti() {
    }

    public Adt_rcy_noti(Activity activity, String activity_name)
    {
        this.activity = activity;
        this.ACTIVITY_NAME = activity_name;
    }

    public Adt_rcy_noti(Activity activity , ArrayList<Noti_info> items) {
        this.items = items;
        this.activity = activity;
    }

    @NonNull
    @Override
    public Adt_rcy_noti.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = null;

        itemView = inflater.inflate(R.layout.item_fn_rcyv_noti, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull Adt_rcy_noti.ViewHolder holder, int position)
    {
        Noti_info item = items.get(position);
        holder.setItem(item);
    }

    @Override
    public int getItemCount()
    {
        return items.size();
    }

    public void aheadAddItem(Noti_info item) {
        for (int i = 0; i < items.size(); i++){
            Noti_info ni = items.get(i);
            if(item.isSame(ni)) {
                ni.setNoti_date(item.getNoti_date());
                ni.setRead_status(false);
                items.set(i, ni);
                return;
            }
        }

        items.add(0,item);
    }

    public void addItem(Noti_info item) {
        for (int i = 0; i < items.size(); i++){
            Noti_info ni = items.get(i);
            if(item.isSame(ni)) {
                ni.setNoti_date(item.getNoti_date());
                ni.setRead_status(false);
                items.set(i, ni);
                return;
            }
        }

        items.add(item);
    }


    public class ViewHolder extends RecyclerView.ViewHolder
    {
        Context mContext;
        LinearLayout linearLayout;
        CardView cardView;
        CircleImageView imageView;
        TextView tv_name;
        TextView tv_content;
        TextView tv_date;

        public ViewHolder(@NonNull View view)
        {
            super(view);
            mContext = view.getContext();
            linearLayout = (LinearLayout) view.findViewById(R.id.ll_fn_rcyv);
            cardView = (CardView) view.findViewById(R.id.fn_cardview);
            imageView = (CircleImageView) view.findViewById(R.id.civ_fn_profile_photo);
            tv_content = (TextView) view.findViewById(R.id.tv_fn_content);
            tv_date = (TextView) view.findViewById(R.id.tv_fn_date);

        }

        public void setItem(Noti_info item) {
            Glide.with(mContext)
                    .load(item.getUser_profile())
                    .into(imageView);

           tv_content.setText(item.getNoti_content());
           tv_date.setText(item.getNoti_date());

           if(item.isRead_status()) {
               cardView.setBackgroundColor(Color.rgb(211, 211, 211));
           } else {
               cardView.setBackgroundColor(Color.WHITE);
           }

            setClickEvent(item);
        }
        private void setClickEvent(Noti_info item) {
            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    log_rcyv("setClickEvent의 onClick 이벤트..");
                    cardView.setBackgroundColor(Color.rgb(211, 211, 211));
                    
                    String noti_type = item.getNoti_type();
                    Intent intent = null;

                    //noti_type에 따라서 다른 액티비티를 띄어준다.
                    if(noti_type.equals("추천")){

                        intent = new Intent(mContext, com.homework.book_sns.act_review.activity_review_read_detail.class);
                        intent.putExtra("review_board_id", String.valueOf(item.getNoti_page_id()));
                        intent.putExtra("type", "noti");
                        intent.putExtra("noti_info", item);

                    } else if(noti_type.equals("팔로우")){

                        intent = new Intent(mContext, com.homework.book_sns.activity_member_page.class);
                        User_info user_info = new User_info(String.valueOf(item.getUser_id()),
                                item.getUser_name(), item.getUser_profile());
                        intent.putExtra("user_info", user_info);
                        intent.putExtra("type", "noti");
                        intent.putExtra("noti_info", item);

                    } else if(noti_type.equals("댓글")){

                    } else if(noti_type.equals("답글")){

                    } else if(noti_type.equals("모임신청")){

                    } else if(noti_type.equals("모임수락")){

                    } else if(noti_type.equals("모임거절")){

                    } else {
                        myRcyvToast("type 오류");
                        return;
                    }

                    log_rcyv("item의 user_id 값 : "+item.getUser_id());
                    intent.putExtra("type", "noti");
                    mContext.startActivity(intent);
                    process_noti_read(item);
                }
            });
        }

        private void process_noti_read(Noti_info item) {
            MyVolleyConnection myVolleyConnection = new MyVolleyConnection(1, mContext);
            myVolleyConnection.setURL("/noti/process_noti_read.php");
            myVolleyConnection.addParams("user_id", String.valueOf(item.getUser_id()));
            myVolleyConnection.addParams("target_user_id", LoginSharedPref.getUserId(mContext));
            myVolleyConnection.addParams("noti_date", item.getNoti_date());
            myVolleyConnection.addParams("noti_type", item.getNoti_type());
            myVolleyConnection.addParams("noti_page_id", String.valueOf(item.getNoti_page_id()));
            myVolleyConnection.addParams("noti_reply_id", String.valueOf(item.getNoti_reply_id()));

            myVolleyConnection.setVolley(new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    response_noti_read(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            myVolleyConnection.requestVolley();
        }

        private void response_noti_read(String response) {
            JSONObject entryJsonObject = null;
            JSONArray jsonArray = null;
//        Log.d(TAG, "fn response_notiData: ");

            try{
                entryJsonObject = new JSONObject(response);
                String success = entryJsonObject.getString("success");

                if(success.equals("false")){
                    String fail_reason = entryJsonObject.getString("reason");
                }else{

                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    private void myRcyvToast(String msg) {
        Toast.makeText(activity,
                "액티비티 이름: "+ACTIVITY_NAME + " , "
                    +"리싸이클러뷰 : "+ RCY_NAME + " , " +msg
                ,Toast.LENGTH_LONG).show();
    }
    private void log_rcyv(String msg) {
        Log.d(TAG, "액티비티 이름: "+ACTIVITY_NAME +", 리싸이클러뷰 : "+RCY_NAME
                +", 로그 내용 : "+msg);
    }

}
