package com.homework.book_sns.rcyv_adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.homework.book_sns.R;
import com.homework.book_sns.javaclass.Group_info;
import com.homework.book_sns.javaclass.LoginSharedPref;
import com.homework.book_sns.javaclass.MyBasicFunc;
import com.homework.book_sns.javaclass.MyVolleyConnection;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class Adt_fg_group_list extends RecyclerView.Adapter<Adt_fg_group_list.ViewHolder>{
    private ArrayList<Group_info> items = new ArrayList<>();


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.item_fg_group_list, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Group_info group_info = items.get(position);
        holder.setItem(group_info);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItem(Group_info item) {
        items.add(item);
    }

    public void cleatItem() {
        items.clear();
    }

    public int getSize() {return items.size();}


    class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout ll_layout;
        CircleImageView civ_group_image;
        TextView tv_group_name;
        TextView tv_group_category;
        TextView tv_group_explain;
        Button btn_group_sign;

        Context mContext;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mContext = itemView.getContext();

            ll_layout = itemView.findViewById(R.id.ll_ifgl);
            civ_group_image = itemView.findViewById(R.id.civ_ifgl_group_image);
            tv_group_name = itemView.findViewById(R.id.tv_ifgl_group_name);
            tv_group_category = itemView.findViewById(R.id.tv_ifgl_group_category);
            tv_group_explain = itemView.findViewById(R.id.tv_ifgl_group_explain);
            btn_group_sign = itemView.findViewById(R.id.btn_ifgl_sign_request);
        }

        public void setItem(Group_info item) {
            Glide.with(mContext)
                    .load(item.getGroup_image())
                    .error(R.drawable.ic_baseline_error_24)
                    .into(civ_group_image);
            tv_group_name.setText(item.getGroup_name());
            tv_group_category.setText(item.getGroup_category());
            tv_group_explain.setText(item.getGroup_explain());

            if(item.isMember()) {
                btn_group_sign.setVisibility(View.INVISIBLE);
            } else {
                btn_group_sign.setVisibility(View.VISIBLE);
                if(item.isApply()) {
                    btn_group_sign.setSelected(false);
                    btn_group_sign.setText("가입 취소");
                } else {
                    btn_group_sign.setSelected(true);
                    btn_group_sign.setText("가입 신청");
                }
            }

            ll_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, com.homework.book_sns.act_group.activity_group_page.class);
                    intent.putExtra("group_id", item.getGroup_id());
                    mContext.startActivity(intent);
                }
            });

            btn_group_sign.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (item.isApply()) {
                        request_sign_cancel(item);
                    } else {
                        request_sign(item);
                    }

                }
            });
        }

        private void request_sign(Group_info item) {
            MyVolleyConnection myVolleyConnection = new MyVolleyConnection(1, mContext);
            myVolleyConnection.setURL("/group/group_request_sign.php");
            myVolleyConnection.addParams("client_id", LoginSharedPref.getUserId(mContext));
            myVolleyConnection.addParams("group_id", Integer.toString(item.getGroup_id()));
            myVolleyConnection.setVolley(new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    btn_group_sign.setSelected(false);
                    btn_group_sign.setText("가입 취소");
                    item.setApply(true);
                    MyBasicFunc.showToast(mContext, "가입 신청이 되었습니다.");
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            myVolleyConnection.requestVolley();
        }

        private void request_sign_cancel(Group_info item) {
            MyVolleyConnection myVolleyConnection = new MyVolleyConnection(1, mContext);
            myVolleyConnection.setURL("/group/group_request_sign_cancel.php");
            myVolleyConnection.addParams("client_id", LoginSharedPref.getUserId(mContext));
            myVolleyConnection.addParams("group_id", Integer.toString(item.getGroup_id()));
            myVolleyConnection.setVolley(new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    btn_group_sign.setSelected(true);
                    btn_group_sign.setText("가입 신청");
                    item.setApply(false);
                    MyBasicFunc.showToast(mContext, "가입 신청이 취소되었습니다.");
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
