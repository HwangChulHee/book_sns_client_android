package com.homework.book_sns.vp_adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.homework.book_sns.R;
import com.homework.book_sns.javaclass.MyVolleyConnection;

import java.util.ArrayList;

public class Adt_rrd_review_image extends PagerAdapter {
    String TAG = "hch";

    private Context mContext;
    private ArrayList<String> imageList;

    public Adt_rrd_review_image(Context mContext, ArrayList<String> imageList) {
        this.mContext = mContext;
        this.imageList = imageList;
    }

    public void addItem(String imageUrl) {
        imageList.add(imageUrl);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.view_rrd_viewpager_review_image, null);

        ImageView imageView = view.findViewById(R.id.iv_vrrd_review_image);
        String image_url = "http://"+ MyVolleyConnection.IP
                + imageList.get(position);
        Glide.with(mContext)
                .load(image_url)
                .error(R.drawable.ic_baseline_error_24)
                .into(imageView);
        Log.d(TAG, "instantiateItem: "+image_url);

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return imageList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return (view == (View) object);
    }
}
