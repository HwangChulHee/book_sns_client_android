package com.homework.book_sns.rcyv_adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
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

public class Adt_rc2_photos extends RecyclerView.Adapter<Adt_rc2_photos.ViewHoder> {

    String TAG = "hch";
    private ArrayList<Bitmap> items = new ArrayList<>();


    @NonNull
    @Override
    public ViewHoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.item_rc2_rcyv_photos, parent, false);

        return new ViewHoder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHoder holder, int position) {
        Bitmap bitmap = items.get(position);
        holder.setItem(bitmap);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItem(Bitmap bitmap) {
        items.add(bitmap);
    }

    public void removeItem(int position) {
        items.remove(position);
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int pos);
    }

    private OnItemClickListener mListener = null;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }


    class ViewHoder extends RecyclerView.ViewHolder {

        ImageView iv_photo;
        ImageView iv_remove;

        Context context;

        public ViewHoder(@NonNull View itemView) {
            super(itemView);

            iv_photo = itemView.findViewById(R.id.iv_item_rc2_rcyv_photo);
            iv_remove = itemView.findViewById(R.id.iv_item_rc2_rcyv_remove);

            context = itemView.getContext();
            setClickEvent();
        }

        private void setClickEvent() {
            iv_remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();


                    if(position != RecyclerView.NO_POSITION) {
                        items.remove(position);
                        notifyItemRemoved(position);
                        Log.d(TAG, "onClick: "+items.size());
                        mListener.onItemClick(view, position);
                    }

                }
            });
        }

        public void setItem(Bitmap bitmap) {
            Glide.with(context)
                    .load(bitmap)
                    .error(R.drawable.ic_baseline_error_24)
                    .into(iv_photo);
        }
    }
}
