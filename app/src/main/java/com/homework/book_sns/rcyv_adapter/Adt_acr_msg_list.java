package com.homework.book_sns.rcyv_adapter;

import android.content.Context;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.homework.book_sns.R;
import com.homework.book_sns.javaclass.Chatting_msg;
import com.homework.book_sns.javaclass.LoginSharedPref;
import com.homework.book_sns.javaclass.MyVolleyConnection;
import com.homework.book_sns.javaclass.ViewType_Chatting;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class Adt_acr_msg_list extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    String TAG = "hch";
    private ArrayList<Chatting_msg> items = new ArrayList<>();
    Context aContext;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = null;

        if(viewType == ViewType_Chatting.CLIENT) {
            itemView = inflater.inflate(R.layout.item_acr_rcyv_chat_msg_client, parent, false);
            return new Client_ViewHolder(itemView);
        } else if(viewType == ViewType_Chatting.OPPONENT) {
            itemView = inflater.inflate(R.layout.item_acr_rcyv_chat_msg_opponent, parent, false);
            return new Opponent_ViewHolder(itemView);
        } else {
            return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof Adt_acr_msg_list.Client_ViewHolder) {
            ((Adt_acr_msg_list.Client_ViewHolder) holder).setItem(items.get(position));
        } else if(holder instanceof Adt_acr_msg_list.Opponent_ViewHolder) {
            ((Adt_acr_msg_list.Opponent_ViewHolder) holder).setItem(items.get(position));
        } else {
            return;
        }
    }

    @Override
    public int getItemViewType(int position) {
        Chatting_msg chatting_msg = items.get(position);
        if(chatting_msg.getUser_info().getUser_id().
                equals(LoginSharedPref.getUserId(aContext))) { //채팅메시지를 보낸 사람이 자신이라면
            return ViewType_Chatting.CLIENT;
        } else {
            return ViewType_Chatting.OPPONENT;
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItem(Chatting_msg item) {
        if(items.size() != 0) {
            int pre_position = items.size() - 1;
            Chatting_msg pre_item =  items.get(pre_position);

            if(pre_item.getTime().equals(item.getTime()) &&
                    pre_item.getUser_info().getUser_id().equals(item.getUser_info().getUser_id())) {
                pre_item.remove_time();
            }
        }


        items.add(item);
    }

    public void setContext(Context aContext) {
        this.aContext = aContext;
    }

    public Chatting_msg getItem(int position) {
        return items.get(position);
    }

    public int getSize() {
        return items.size();
    }

    class Client_ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_read_status;
        TextView tv_msg_time;
        TextView tv_msg;

        public Client_ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_read_status = (TextView) itemView.findViewById(R.id.tv_iarcmc_read_status);
            tv_msg_time = (TextView) itemView.findViewById(R.id.tv_iarcmc_msg_time);
            tv_msg = (TextView) itemView.findViewById(R.id.tv_iarcmc_msg);
        }

        private void setItem(Chatting_msg item) {

            tv_read_status.setText(Integer.toString(item.getRead_count()));
            tv_msg_time.setText(item.getTime());
            tv_msg.setText(item.getMsg());
        }
    }

    class Opponent_ViewHolder extends RecyclerView.ViewHolder {
        Context mContext;

        CircleImageView civ_profile;
        TextView tv_nickname;

        TextView tv_read_status;
        TextView tv_msg_time;
        TextView tv_msg;

        public Opponent_ViewHolder(@NonNull View itemView) {
            super(itemView);

            mContext = itemView.getContext();

            civ_profile = (CircleImageView) itemView.findViewById(R.id.civ_iarcmo_photo);
            tv_nickname = (TextView) itemView.findViewById(R.id.tv_iarcmo_nickname);

            tv_read_status = (TextView) itemView.findViewById(R.id.tv_iarcmo_read_status);
            tv_msg_time = (TextView) itemView.findViewById(R.id.tv_iarcmo_msg_time);
            tv_msg = (TextView) itemView.findViewById(R.id.tv_iarcmo_msg);
        }
        private void setItem(Chatting_msg item) {
            int position = getAdapterPosition();
            if(position != 0) {
                Chatting_msg preChat_info = items.get(position-1);

                if(preChat_info.getUser_info().getUser_id().equals(item.getUser_info().getUser_id())) {
                    civ_profile.setVisibility(View.INVISIBLE);
                    tv_nickname.setVisibility(View.GONE);
                } else {
                    civ_profile.setVisibility(View.VISIBLE);
                    tv_nickname.setVisibility(View.VISIBLE);
                }
            }

            String image_url = "http://"+ MyVolleyConnection.IP
                    + item.getUser_info().getUser_profile();
            Glide.with(mContext)
                    .load(image_url)
                    .error(R.drawable.ic_baseline_error_24)
                    .into(civ_profile);
            tv_nickname.setText(item.getUser_info().getUser_nickname());
            tv_msg_time.setText(item.getTime());

            tv_read_status.setText(Integer.toString(item.getRead_count()));
            tv_msg.setText(item.getMsg());
        }
    }
}
