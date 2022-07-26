package com.homework.book_sns.rcyv_adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.homework.book_sns.R;

import java.util.ArrayList;

public class Adt_acr_msg_image extends RecyclerView.Adapter<Adt_acr_msg_image.ViewHolder> {
    private ArrayList<String> items = new ArrayList<>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.item_acr_rcyv_chat_msg_image, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setItem(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(ArrayList<String> arrayList) {
        items = arrayList;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        Context mContext;
        ImageView iv_chat_image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_chat_image = itemView.findViewById(R.id.iv_iarcmi_chat_image);
            mContext = itemView.getContext();
        }

        private void setItem(String item) {
            Glide.with(mContext)
                    .load(item)
                    .error(R.drawable.ic_baseline_error_24)
                    .into(iv_chat_image);
            Log.d("hch", "setItem: 채팅 이미지"+item);
        }
    }
}
