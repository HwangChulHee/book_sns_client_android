package com.homework.book_sns.rcyv_adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.homework.book_sns.R;
import com.homework.book_sns.javaclass.Chatting_list_info;
import com.homework.book_sns.javaclass.Chatting_list_info_comparator;
import com.homework.book_sns.javaclass.MyVolleyConnection;
import com.homework.book_sns.javaclass.User_info;

import java.util.ArrayList;
import java.util.Collections;

import de.hdodenhof.circleimageview.CircleImageView;

public class Adt_acl_msg_list extends RecyclerView.Adapter<Adt_acl_msg_list.ViewHolder> {
    private ArrayList<Chatting_list_info> items = new ArrayList<>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = null;

        itemView = inflater.inflate(R.layout.item_acl_rcyv_chat_list, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Chatting_list_info item = items.get(position);
        holder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItem(Chatting_list_info item) {
        items.add(item);
    }

    public void sortItem() {
        Chatting_list_info_comparator comp = new Chatting_list_info_comparator();
        Collections.sort(items, comp);
    }

    public void clearItem() {
        items.clear();
    }

    public int getItemSize() {
        return items.size();
    }

    public Chatting_list_info getItem(int position) {
        return items.get(position);
    }

    public void removeItem(int position) {
        items.remove(position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        Context mContext;

        LinearLayout ll_entry;
        CircleImageView civ_opponent_profile;
        TextView tv_opponent_name;
        TextView tv_last_msg;
        TextView tv_last_msg_time;
        TextView tv_remain_msg_count;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mContext = itemView.getContext();

            ll_entry = itemView.findViewById(R.id.ll_iacl_rcyv);
            civ_opponent_profile = itemView.findViewById(R.id.civ_iacl_rcyv_opponent_profile);
            tv_opponent_name = itemView.findViewById(R.id.tv_iacl_rcyv_opponent_name);
            tv_last_msg = itemView.findViewById(R.id.tv_iacl_rcyv_last_msg);
            tv_last_msg_time = itemView.findViewById(R.id.tv_iacl_rcyv_last_msg_time);
            tv_remain_msg_count = itemView.findViewById(R.id.tv_iacl_rcyv_remain_read_count);
        }

        public void setItem(Chatting_list_info item){
            String image_url = "http://"+ MyVolleyConnection.IP
                    + item.getOpponent().getUser_profile();
            Glide.with(mContext)
                    .load(image_url)
                    .error(R.drawable.ic_baseline_error_24)
                    .into(civ_opponent_profile);

            tv_opponent_name.setText(item.getOpponent().getUser_nickname());
            tv_last_msg.setText(item.getLast_msg());
            tv_last_msg_time.setText(item.getChange_last_msg_time());
            tv_remain_msg_count.setText(Integer.toString(item.getRemain_msg_count()));

            if(item.getRemain_msg_count() == 0) {
                tv_remain_msg_count.setVisibility(View.INVISIBLE);
            } else {
                tv_remain_msg_count.setVisibility(View.VISIBLE);
            }

            ll_entry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    User_info user_info = new User_info(
                            item.getOpponent().getUser_id(),
                            item.getOpponent().getUser_nickname(),
                            item.getOpponent().getUser_profile());

                    Intent intent = new Intent(mContext, com.homework.book_sns.act_chatting.activity_chatting_room.class);
                    intent.putExtra("from", "list");
                    if(item.isGroup()) {
                        intent.putExtra("chat_type", "group");
                    } else {
                        intent.putExtra("chat_type", "one");
                    }
                    intent.putExtra("room_id", item.getRoom_id());
                    mContext.startActivity(intent);
                }
            });
        }
    }
}
