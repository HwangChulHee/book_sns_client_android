package com.homework.book_sns.rcyv_adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.homework.book_sns.R;
import com.homework.book_sns.javaclass.LoginSharedPref;
import com.homework.book_sns.javaclass.Member_info;
import com.homework.book_sns.javaclass.MyBasicFunc;
import com.homework.book_sns.javaclass.MyVolleyConnection;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class Adt_agmm_member_list extends RecyclerView.Adapter<Adt_agmm_member_list.ViewHolder>{
    private ArrayList<Member_info> items = new ArrayList<>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.item_agmm_member, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Member_info item = items.get(position);
        holder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItem(Member_info item) {
        items.add(item);
    }

    public void removeItem(int position) {
        items.remove(position);
    }

    public void clearItem() {
        items.clear();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView civ_profile;
        TextView tv_nickname;
        Button btn_sign;
        Button btn_sign_reject;
        
        Context mContext;
        
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mContext = itemView.getContext();
            
            civ_profile = itemView.findViewById(R.id.civ_iagmm_profile_photo);
            tv_nickname = itemView.findViewById(R.id.tv_iagmm_nickname);
            btn_sign = itemView.findViewById(R.id.btn_iagmm_sign);
            btn_sign_reject = itemView.findViewById(R.id.btn_iagmm_sign_reject);
        }

        public void setItem(Member_info item) {
            String image_url = "http://"+ MyVolleyConnection.IP+item.getMember_image();
            Glide.with(mContext)
                    .load(image_url)
                    .error(R.drawable.ic_baseline_error_24)
                    .into(civ_profile);
            tv_nickname.setText(item.getMember_name());

            if(item.getMember_id() == Integer.parseInt(LoginSharedPref.getUserId(mContext))) {
                btn_sign.setVisibility(View.INVISIBLE);
            } else {
                btn_sign.setVisibility(View.VISIBLE);
            }

            if(item.isApply()) {
                btn_sign.setText("요청 승인");
                btn_sign.setSelected(true);
                btn_sign_reject.setVisibility(View.VISIBLE);
            } else {
                btn_sign.setText("모임 강퇴");
                btn_sign.setSelected(false);
                btn_sign_reject.setVisibility(View.GONE);
            }

            btn_sign.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(item.isApply()){
                        approve(item);
                        btn_sign.setText("모임 강퇴");
                        btn_sign.setSelected(false);
                        btn_sign_reject.setVisibility(View.GONE);
                        notifyItemChanged(getAdapterPosition());
                        item.setApply(false);
                    } else {
                        ban(item);
                    }

                }
            });

            btn_sign_reject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    reject(item);
                }
            });
        }

        private void approve(Member_info item) {
            MyVolleyConnection myVolleyConnection = new MyVolleyConnection(1, mContext);
            myVolleyConnection.setURL("/group/group_member_approve.php");
            myVolleyConnection.addParams("member_id", Integer.toString(item.getMember_id()));
            myVolleyConnection.addParams("group_id", Integer.toString(item.getGroup_id()));
            myVolleyConnection.setVolley(new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    MyBasicFunc.showToast(mContext, item.getMember_name()+" 님을 가입 승인하셨습니다.");
                    Log.d("hch", "onResponse: "+response);

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            myVolleyConnection.requestVolley();

        }

        private void reject(Member_info item) {
            MyVolleyConnection myVolleyConnection = new MyVolleyConnection(1, mContext);
            myVolleyConnection.setURL("/group/group_member_reject.php");
            myVolleyConnection.addParams("member_id", Integer.toString(item.getMember_id()));
            myVolleyConnection.addParams("group_id", Integer.toString(item.getGroup_id()));
            myVolleyConnection.setVolley(new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    MyBasicFunc.showToast(mContext, item.getMember_name()+" 님을 가입 거절하셨습니다.");
                    Log.d("hch", "onResponse: "+response);
                    removeItem(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            myVolleyConnection.requestVolley();

        }

        private void ban(Member_info item) {
            MyVolleyConnection myVolleyConnection = new MyVolleyConnection(1, mContext);
            myVolleyConnection.setURL("/group/group_member_banish.php");
            myVolleyConnection.addParams("member_id", Integer.toString(item.getMember_id()));
            myVolleyConnection.addParams("group_id", Integer.toString(item.getGroup_id()));
            myVolleyConnection.setVolley(new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("hch", "onResponse: "+response);
                    removeItem(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());
                    MyBasicFunc.showToast(mContext, item.getMember_name()+" 님을 추방하셨습니다.");

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
