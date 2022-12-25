package com.homework.book_sns.rcyv_adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.homework.book_sns.R;
import com.homework.book_sns.javaclass.Chatting_msg;
import com.homework.book_sns.javaclass.GridAutofitLayoutManager;
import com.homework.book_sns.javaclass.LoginSharedPref;
import com.homework.book_sns.javaclass.MyVolleyConnection;
import com.homework.book_sns.javaclass.ViewType_Chatting;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class Adt_acr_msg extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
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
        } else if(viewType == ViewType_Chatting.ENTER) {
            itemView = inflater.inflate(R.layout.item_acr_rcyv_chat_msg_enter, parent, false);
            return new Enter_ViewHolder(itemView);
        } else {
            return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof Adt_acr_msg.Client_ViewHolder) {
            ((Adt_acr_msg.Client_ViewHolder) holder).setItem(items.get(position));
        } else if(holder instanceof Adt_acr_msg.Opponent_ViewHolder) {
            ((Adt_acr_msg.Opponent_ViewHolder) holder).setItem(items.get(position));
        } else if(holder instanceof Adt_acr_msg.Enter_ViewHolder) {
            ((Adt_acr_msg.Enter_ViewHolder) holder).setItem(items.get(position));
        } else {
            return;
        }
    }

    @Override
    public int getItemViewType(int position) {
        Chatting_msg chatting_msg = items.get(position);

        if(chatting_msg.isEnter()) {
            return ViewType_Chatting.ENTER;
        }

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

    public void clearItem() {
        items.clear();
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

    public void set_all_read_count(int read_count) {
        for (int i =0; i < items.size(); i++) {
            items.get(i).setRead_count(read_count);
        }
    }

    public void plus_specific_read_count(String time) {
        SimpleDateFormat input_format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // 입력포멧
        try {
            Date last_readTime = input_format.parse(time); //해당 클라이언트가 채팅을 읽은 마지막 시간.

            for (int i =0; i < items.size(); i++) {
                Chatting_msg item = items.get(i);
                Date itemTime = input_format.parse(item.getOriginal_time());

                if(itemTime.compareTo(last_readTime) > 0) { // 채팅들이 기준시간 보다 크다면 읽음 처리를 해준다.
                    item.plusRead_count();
                    Log.d(TAG, "plus_specific_read_count: last_time "+last_readTime);
                    Log.d(TAG, "plus_specific_read_count: item_time"+itemTime);
                }
            }


        } catch (ParseException e) {
            e.printStackTrace();
        }

    }


    class Client_ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_read_status;
        TextView tv_msg_time;
        TextView tv_msg;
        RecyclerView rcyv_image;
        Adt_acr_msg_image adt_acr_msg_image;

        Context mContext;

        public Client_ViewHolder(@NonNull View itemView) {
            super(itemView);
            mContext = itemView.getContext();

            tv_read_status = (TextView) itemView.findViewById(R.id.tv_iarcmc_read_status);
            tv_msg_time = (TextView) itemView.findViewById(R.id.tv_iarcmc_msg_time);
            tv_msg = (TextView) itemView.findViewById(R.id.tv_iarcmc_msg);

            rcyv_image = (RecyclerView) itemView.findViewById(R.id.rcvy_iarcmc_image);
            adt_acr_msg_image = new Adt_acr_msg_image();
        }

        private void setItem(Chatting_msg item) {

            tv_read_status.setText(item.getRead_status());
            tv_msg_time.setText(item.getTime());
            tv_msg.setText(item.getMsg());

            if(item.isImage()) {
                int count_img = item.getImages().size();

                GridLayoutManager gridLayoutManager;
                gridLayoutManager= new GridLayoutManager(aContext, 6);
                gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        int imageCount = item.getImages().size();


                        if(imageCount % 3 == 0 || imageCount <= 4) {
                            if(imageCount == 1) {
                                return 6;
                            } else if(imageCount == 2) {
                                return 3;
                            } else if(imageCount == 3) {
                                return 2;
                            } else if(imageCount == 4) {
                                return 3;
                            } else {
                                return 2;
                            }

                        } else if(imageCount % 3 == 1) { // 나머지가 1이면 마지막 줄로부터 2줄이 2열이다. ex) 4, 7, 10
                            if(position >= imageCount - 4) {
                                return 3;
                            } else {
                                return 2;
                            }
                        } else { // 나머지가 2이면 마지막 줄로부터 1줄이 2열이다. ex) 5, 8, 11
                            if(position >= imageCount - 2) {
                                return 3;
                            } else {
                                return 2;
                            }
                        }
                    }
                });


                rcyv_image.setLayoutManager(gridLayoutManager);
                rcyv_image.setAdapter(adt_acr_msg_image);

                adt_acr_msg_image.setItems(item.getImages());
                adt_acr_msg_image.notifyDataSetChanged();

                tv_msg.setVisibility(View.GONE);
                rcyv_image.setVisibility(View.VISIBLE);
            } else {
                tv_msg.setVisibility(View.VISIBLE);
                rcyv_image.setVisibility(View.GONE);
            }
        }
    }

    class Opponent_ViewHolder extends RecyclerView.ViewHolder {
        Context mContext;

        CircleImageView civ_profile;
        TextView tv_nickname;

        TextView tv_read_status;
        TextView tv_msg_time;
        TextView tv_msg;
        RecyclerView rcyv_image;
        Adt_acr_msg_image adt_acr_msg_image;

        public Opponent_ViewHolder(@NonNull View itemView) {
            super(itemView);

            mContext = itemView.getContext();

            civ_profile = (CircleImageView) itemView.findViewById(R.id.civ_iarcmo_photo);
            tv_nickname = (TextView) itemView.findViewById(R.id.tv_iarcmo_nickname);

            tv_read_status = (TextView) itemView.findViewById(R.id.tv_iarcmo_read_status);
            tv_msg_time = (TextView) itemView.findViewById(R.id.tv_iarcmo_msg_time);
            tv_msg = (TextView) itemView.findViewById(R.id.tv_iarcmo_msg);

            rcyv_image = (RecyclerView) itemView.findViewById(R.id.rcvy_iarcmo_image);
            adt_acr_msg_image = new Adt_acr_msg_image();
        }
        private void setItem(Chatting_msg item) {
            int position = getAdapterPosition();
            if(position != 0) {
                Chatting_msg preChat_info = items.get(position-1);

                if(preChat_info.getUser_info().getUser_id().equals(item.getUser_info().getUser_id())
                    && !preChat_info.isEnter()) {
                    civ_profile.setVisibility(View.INVISIBLE);
                    tv_nickname.setVisibility(View.GONE);
                } else {
                    civ_profile.setVisibility(View.VISIBLE);
                    tv_nickname.setVisibility(View.VISIBLE);
                }
            } else {
                civ_profile.setVisibility(View.VISIBLE);
                tv_nickname.setVisibility(View.VISIBLE);
            }


            String image_url = "http://"+ MyVolleyConnection.IP
                    + item.getUser_info().getUser_profile();
            Glide.with(mContext)
                    .load(image_url)
                    .error(R.drawable.ic_baseline_error_24)
                    .into(civ_profile);
            tv_nickname.setText(item.getUser_info().getUser_nickname());
            tv_msg_time.setText(item.getTime());

            tv_read_status.setText(item.getRead_status());
            tv_msg.setText(item.getMsg());

            if(item.isImage()) {
                int count_img = item.getImages().size();

                GridLayoutManager gridLayoutManager;
                gridLayoutManager= new GridLayoutManager(aContext, 6);
                gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        int imageCount = item.getImages().size();


                        if(imageCount % 3 == 0 || imageCount <= 4) {
                            if(imageCount == 1) {
                                return 6;
                            } else if(imageCount == 2) {
                                return 3;
                            } else if(imageCount == 3) {
                                return 2;
                            } else if(imageCount == 4) {
                                return 3;
                            } else {
                                return 2;
                            }

                        } else if(imageCount % 3 == 1) { // 나머지가 1이면 마지막 줄로부터 2줄이 2열이다. ex) 4, 7, 10
                            if(position >= imageCount - 4) {
                                return 3;
                            } else {
                                return 2;
                            }
                        } else { // 나머지가 2이면 마지막 줄로부터 1줄이 2열이다. ex) 5, 8, 11
                            if(position >= imageCount - 2) {
                                return 3;
                            } else {
                                return 2;
                            }
                        }
                    }
                });


                rcyv_image.setLayoutManager(gridLayoutManager);
                rcyv_image.setAdapter(adt_acr_msg_image);

                adt_acr_msg_image.setItems(item.getImages());
                adt_acr_msg_image.notifyDataSetChanged();

                tv_msg.setVisibility(View.GONE);
                rcyv_image.setVisibility(View.VISIBLE);
            } else {
                tv_msg.setVisibility(View.VISIBLE);
                rcyv_image.setVisibility(View.GONE);
            }


        }
    }

    class Enter_ViewHolder extends RecyclerView.ViewHolder {

        TextView textView;

        public Enter_ViewHolder(@NonNull View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.tv_iarcme_msg);

        }
        private void setItem(Chatting_msg item) {
            textView.setText(item.getMsg());
        }
    }
}
