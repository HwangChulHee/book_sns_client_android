package com.homework.book_sns.rcyv_adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.homework.book_sns.R;
import com.homework.book_sns.javaclass.Book_info;

import java.util.ArrayList;

public class Adt_rc1_searched_book extends RecyclerView.Adapter<Adt_rc1_searched_book.ViewHoder> {

    private ArrayList<Book_info> items = new ArrayList<>();

    @NonNull
    @Override
    public ViewHoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.item_rc1_rcyv_searched_book, parent, false);

        return new ViewHoder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHoder holder, int position) {
        Book_info item = items.get(position);
        holder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItem(Book_info book_info) {
        items.add(book_info);
    }

    public void clearItem() {
        items.clear();
    }

    public Book_info getItem(int position) {
        return items.get(position);
    }


    public interface OnItemClickListener {
        void onItemClick(View v, int pos);
    }

    private OnItemClickListener mListener = null;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    class ViewHoder extends RecyclerView.ViewHolder {

        ImageView iv_cover;

        TextView tv_title;
        TextView tv_author;
        TextView tv_review_count;

        Button btn_choice;

        Context context;

        public ViewHoder(@NonNull View itemView) {
            super(itemView);

            context = itemView.getContext();

            iv_cover = itemView.findViewById(R.id.iv_item_rc_cover);
            tv_title = itemView.findViewById(R.id.tv_item_rc_title);
            tv_author = itemView.findViewById(R.id.tv_item_rc_author);
            tv_review_count = itemView.findViewById(R.id.tv_item_rc_review_count);
            btn_choice = itemView.findViewById(R.id.btn_item_rc_choice);

            setClickEvent();
        }

        private void setClickEvent() {
            btn_choice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION) {
                        mListener.onItemClick(view, position);
                    }
                }
            });
        }

        public void setItem(Book_info item) {
            Glide.with(context)
                    .load(item.getCover())
                    .error(R.drawable.ic_baseline_error_24)
                    .into(iv_cover);
            tv_title.setText(item.getTitle());
            tv_author.setText(item.getAuthor());
        }
    }
}
