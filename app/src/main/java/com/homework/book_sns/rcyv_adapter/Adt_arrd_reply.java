package com.homework.book_sns.rcyv_adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.homework.book_sns.R;
import com.homework.book_sns.javaclass.LoginSharedPref;
import com.homework.book_sns.javaclass.MyVolleyConnection;
import com.homework.book_sns.javaclass.Review_Reply;
import com.homework.book_sns.javaclass.User_info;
import com.homework.book_sns.javaclass.ViewType_Reply;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class Adt_arrd_reply extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    String TAG = "hch";
    private ArrayList<Review_Reply> items = new ArrayList<>();

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = null;

        if(viewType == ViewType_Reply.REPLY) { // 댓글이면
            itemView = inflater.inflate(R.layout.item_rrd_rcyv_reply, parent, false);
            return new ReplyViewHolder(itemView);
        } else if(viewType == ViewType_Reply.RE_REPLY) { // 답글이면
            itemView = inflater.inflate(R.layout.item_rrd_rcyv_reply2, parent, false);
            return new Re_ReplyViewHolder(itemView);
        } else {
            return null;
        }


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if(holder instanceof ReplyViewHolder) {
            ((ReplyViewHolder) holder).setItem(items.get(position));
        } else if(holder instanceof Re_ReplyViewHolder) {
            ((Re_ReplyViewHolder) holder).setItem(items.get(position));
        } else {
            return;
        }

    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemViewType(int position) {
        return Integer.parseInt(items.get(position).getReply_class());
    }

    public void addItem(Review_Reply item) {
        items.add(item);
    }

    public void insertItem(Review_Reply item, int position) {
        items.add(position, item);
    }

    public Review_Reply getItem(int position) {
        Review_Reply item = items.get(position);
        return item;
    }

    public void removeGroup(String groupNum) {
        for(int i =0; i < items.size(); i++) {
            if(items.get(i).getGroup_num().equals(groupNum)) {
                items.remove(i);
                notifyItemRemoved(i);
            }
        }
    }



    public void reply_delete(Review_Reply item, int position, Context context){
        MyVolleyConnection myVolleyConnection = new MyVolleyConnection(1, context);
        myVolleyConnection.setURL("/review/review_reply_delete.php");
        myVolleyConnection.addParams("reply_id", item.getReply_id());
        myVolleyConnection.addParams("review_board_id", item.getReview_board_id());
        myVolleyConnection.addParams("group_num", item.getGroup_num());
        myVolleyConnection.addParams("reply_class", item.getReply_class());
        myVolleyConnection.setVolley(new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                reply_delete_response(response, position, context);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        myVolleyConnection.requestVolley();
    }

    private void reply_delete_response(String response, int position, Context context) {
        Log.d(TAG, "reply_delete_response: "+response);
        JSONObject entryJsonObject = null;
        try {
            entryJsonObject = new JSONObject(response);
            String success = entryJsonObject.getString("success");

            if(success.equals("false")) {
                String fail_reason = entryJsonObject.getString("reason");
                Log.d(TAG, "reply_delete_response: "+fail_reason);

            } else {
                JSONArray jsonDataArray = entryJsonObject.getJSONArray("data");

                for(int i =0; i< jsonDataArray.length(); i++) {
                    JSONObject jsonDataObject = jsonDataArray.getJSONObject(i);

                    String reply_class = jsonDataObject.getString("reply_class");
                    String group_num = jsonDataObject.getString("group_num");
                    String isReplyDBRemoved = jsonDataObject.getString("isReplyDBRemoved");
                    String remove_content = jsonDataObject.getString("remove_content");
                    int isParentRemoved = jsonDataObject.getInt("isParentRemoved");

                    if(reply_class.equals("2")) {
                        items.remove(position);
                        notifyItemRemoved(position);
                        
                        Toast.makeText(context, "답글이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                        
                    } else if(reply_class.equals("1")) {
                        if(isReplyDBRemoved.equals("true")) {
                            items.remove(position);
                            notifyItemRemoved(position);
                        } else if(isReplyDBRemoved.equals("false")) {
                            items.get(position).setRemoved(true);
                            items.get(position).setReply_content(remove_content);
                            notifyItemChanged(position);
                        }
                        Toast.makeText(context, "댓글이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                    }

                    if(isParentRemoved == 1) {
                        removeGroup(group_num);
                    }
                }

            }

        } catch (JSONException e) {
            Log.d(TAG, "JSONException: "+e);
        }

    }

    private void recommendation_doing(int position, Button btn, Context context) {

        String reply_id = items.get(position).getReply_id();
        String client_id = LoginSharedPref.getUserId(context);

        MyVolleyConnection myVolleyConnection = new MyVolleyConnection(1, context);
        myVolleyConnection.setURL("/review/review_reply_recommendation_doing.php");
        myVolleyConnection.addParams("reply_id", reply_id);
        myVolleyConnection.addParams("client_id", client_id);
        myVolleyConnection.setVolley(new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                recommendation_doing_response(position, response, btn);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        myVolleyConnection.requestVolley();
    }

    private void recommendation_doing_response(int position, String response , Button btn) {
//        Log.d(TAG, "recommendation_doing_response: "+response);
        JSONObject entryJsonObject = null;
        try {
            entryJsonObject = new JSONObject(response);
            String success = entryJsonObject.getString("success");

            if(success.equals("false")) {
                String fail_reason = entryJsonObject.getString("reason");
//                Toast.makeText(aContext, fail_reason, Toast.LENGTH_LONG).show();

            } else {

                JSONArray jsonDataArray = entryJsonObject.getJSONArray("data");

                for(int i =0; i< jsonDataArray.length(); i++) {

                    JSONObject jsonDataObject = jsonDataArray.getJSONObject(i);
                }

//                btn.setSelected(true);
                btn.setEnabled(true);
                items.get(position).setClient_recommendation(true);

                int rCount = Integer.parseInt(items.get(position).getRecommendation_count()) + 1 ;
                items.get(position).setRecommendation_count(Integer.toString(rCount));

                notifyItemChanged(position);
            }

        } catch (JSONException e) {
            Log.d(TAG, "JSONException update_reply: "+e);
        }
    }

    private void recommendation_cancel(int position, Button btn, Context context) {
        String reply_id = items.get(position).getReply_id();

        MyVolleyConnection myVolleyConnection = new MyVolleyConnection(1, context);
        myVolleyConnection.setURL("/review/review_reply_recommendation_cancel.php");
        myVolleyConnection.addParams("reply_id", reply_id);
        myVolleyConnection.addParams("client_id", LoginSharedPref.getUserId(context));
        myVolleyConnection.setVolley(new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                recommendation_cancel_response(position, response, btn);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        myVolleyConnection.requestVolley();

    }

    private void recommendation_cancel_response(int position, String response, Button btn) {
        //        Log.d(TAG, "recommendation_doing_response: "+response);
        JSONObject entryJsonObject = null;
        try {
            entryJsonObject = new JSONObject(response);
            String success = entryJsonObject.getString("success");

            if(success.equals("false")) {
                String fail_reason = entryJsonObject.getString("reason");
//                Toast.makeText(aContext, fail_reason, Toast.LENGTH_LONG).show();

            } else {

                JSONArray jsonDataArray = entryJsonObject.getJSONArray("data");

                for(int i =0; i< jsonDataArray.length(); i++) {

                    JSONObject jsonDataObject = jsonDataArray.getJSONObject(i);
                }

//                btn.setSelected(false);
                btn.setEnabled(true);

                items.get(position).setClient_recommendation(false);

                int rCount = Integer.parseInt(items.get(position).getRecommendation_count()) - 1 ;
                items.get(position).setRecommendation_count(Integer.toString(rCount));

                notifyItemChanged(position);
            }

        } catch (JSONException e) {
            Log.d(TAG, "JSONException update_reply: "+e);
        }
    }




    @Override
    public int getItemCount() {
        return items.size();
    }


    public interface OnItemClickListener {
        void onCreate_Re_Reply(View v, int pos);
        void onUpdate_Reply(View v, int pos);
    }

    private OnItemClickListener mListener = null;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    class ReplyViewHolder extends RecyclerView.ViewHolder {

        Context mContext;

        LinearLayout ll_reply;
        CircleImageView civ_profile_photo;
        TextView tv_nickname;
        TextView tv_tag_user_nickname;
        TextView tv_content;
        TextView tv_register_date;
        TextView tv_create_re_reply;

        TextView tv_recommendation_count;
        Button btn_recommendation;

        public ReplyViewHolder(@NonNull View itemView) {
            super(itemView);
            mContext = itemView.getContext();

            ll_reply = (LinearLayout) itemView.findViewById(R.id.ll_irrd_reply);
            civ_profile_photo = (CircleImageView) itemView.findViewById(R.id.civ_irrr_profile_photo);
            tv_nickname = (TextView) itemView.findViewById(R.id.tv_irrr_nickname);
            tv_tag_user_nickname = (TextView) itemView.findViewById(R.id.tv_irrr_tag_nickname);
            tv_content = (TextView) itemView.findViewById(R.id.tv_irrr_reply_content);
            tv_register_date = (TextView) itemView.findViewById(R.id.tv_irrr_register_date);
            tv_create_re_reply = (TextView) itemView.findViewById(R.id.tv_irrr_re_reply);

            tv_recommendation_count = (TextView) itemView.findViewById(R.id.tv_irrr_recommendation_count);
            btn_recommendation = (Button) itemView.findViewById(R.id.btn_irrr_recommendation);
        }

        private void setItem(Review_Reply item) {
//            Log.d(TAG, "setItem: "+item.isRemoved());

            civ_profile_photo.setVisibility(View.VISIBLE);
            tv_nickname.setVisibility(View.VISIBLE);
            tv_tag_user_nickname.setVisibility(View.VISIBLE);
            tv_content.setVisibility(View.VISIBLE);
            tv_register_date.setVisibility(View.VISIBLE);
            tv_create_re_reply.setVisibility(View.VISIBLE);

            tv_recommendation_count.setVisibility(View.VISIBLE);
            btn_recommendation.setVisibility(View.VISIBLE);

            if(item.isRemoved()) {
                civ_profile_photo.setVisibility(View.GONE);
                tv_nickname.setVisibility(View.GONE);
                tv_tag_user_nickname.setVisibility(View.GONE);
                tv_content.setVisibility(View.VISIBLE);
                tv_register_date.setVisibility(View.GONE);
                tv_create_re_reply.setVisibility(View.GONE);

                tv_recommendation_count.setVisibility(View.GONE);
                btn_recommendation.setVisibility(View.GONE);

                tv_content.setText(item.getReply_content());
                Log.d(TAG, "setItem: 이거 떠야되는거 아님?");
            } else {
                String image_url = item.getUser_info().getUser_profile();
                Glide.with(mContext)
                        .load(image_url)
                        .error(R.drawable.ic_baseline_error_24)
                        .into(civ_profile_photo);
                tv_nickname.setText(item.getUser_info().getUser_nickname());
                tv_tag_user_nickname.setText("@"+item.getTag_user_nickname());
                tv_content.setText(item.getReply_content());
                tv_register_date.setText(item.getReply_register_date());

                if(item.getTag_user_id().equals("null")) {
                    tv_tag_user_nickname.setVisibility(View.INVISIBLE);
                } else {
                    tv_tag_user_nickname.setVisibility(View.VISIBLE);
                }


                ll_reply.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        int position = getAdapterPosition();
                        Review_Reply item = items.get(position);

                        if(!item.getUser_info().getUser_id().equals(LoginSharedPref.getUserId(mContext))) {
                            return false;
                        }

                        String[] reply_option = mContext.getResources().getStringArray(R.array.review_reply_option);
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setItems(reply_option, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(reply_option[i].equals("댓글 수정")) {
                                    int position = getAdapterPosition();
                                    if(position != RecyclerView.NO_POSITION) {
                                        mListener.onUpdate_Reply(view, position);
                                    }
                                } else if(reply_option[i].equals("댓글 삭제")) {
                                    reply_delete(item, position, mContext);
                                }
                            }
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();

                        return true;
                    }
                });

                tv_create_re_reply.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION) {
                            mListener.onCreate_Re_Reply(view, position);
                        }

                    }
                });

                if(item.getRecommendation_count().equals("0")) {
                    tv_recommendation_count.setText(null);
                    tv_recommendation_count.setVisibility(View.GONE);
                } else {
                    tv_recommendation_count.setText("추천 "+item.getRecommendation_count()+" 개");
                    tv_recommendation_count.setVisibility(View.VISIBLE);
                }
                tv_recommendation_count.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d(TAG, "onClick: ");
                        Intent intent = new Intent(mContext, com.homework.book_sns.act_review.activity_reply_recommendation.class);
                        intent.putExtra("reply_id", item.getReply_id());
                        mContext.startActivity(intent);

                    }
                });

                btn_recommendation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int position = getAdapterPosition();

                        if(btn_recommendation.isSelected()) { //추천한 상태
                            btn_recommendation.setEnabled(false);
                            recommendation_cancel(position , btn_recommendation, mContext);
                        } else { // 추천하지 않은 상태
                            btn_recommendation.setEnabled(false);
                            recommendation_doing(position, btn_recommendation, mContext);

                        }
                    }
                });

                if(item.isClient_recommendation()) {
                    Log.d(TAG, "setItem: "+getAdapterPosition());
                    btn_recommendation.setSelected(true);
                } else {
                    btn_recommendation.setSelected(false);
                }


            }

        }
    }

    class Re_ReplyViewHolder extends RecyclerView.ViewHolder {

        Context mContext;

        LinearLayout ll_reply;
        CircleImageView civ_profile_photo;
        TextView tv_nickname;
        TextView tv_tag_user_nickname;
        TextView tv_content;
        TextView tv_register_date;
        TextView tv_create_re_reply;

        TextView tv_recommendation_count;
        Button btn_recommendation;

        public Re_ReplyViewHolder(@NonNull View itemView) {
            super(itemView);
            mContext = itemView.getContext();

            ll_reply = (LinearLayout) itemView.findViewById(R.id.ll_irrd2_reply);
            civ_profile_photo = (CircleImageView) itemView.findViewById(R.id.civ_irrr2_profile_photo);
            tv_nickname = (TextView) itemView.findViewById(R.id.tv_irrr2_nickname);
            tv_tag_user_nickname = (TextView) itemView.findViewById(R.id.tv_irrr2_tag_nickname);
            tv_content = (TextView) itemView.findViewById(R.id.tv_irrr2_reply_content);
            tv_register_date = (TextView) itemView.findViewById(R.id.tv_irrr2_register_date);
            tv_create_re_reply = (TextView) itemView.findViewById(R.id.tv_irrr2_re_reply);

            tv_recommendation_count = (TextView) itemView.findViewById(R.id.tv_irrr2_recommendation_count);
            btn_recommendation = (Button) itemView.findViewById(R.id.btn_irrr2_recommendation);
        }

        private void setItem(Review_Reply item) {
            String image_url = item.getUser_info().getUser_profile();
            Glide.with(mContext)
                    .load(image_url)
                    .error(R.drawable.ic_baseline_error_24)
                    .into(civ_profile_photo);
            tv_nickname.setText(item.getUser_info().getUser_nickname());
            tv_tag_user_nickname.setText("@"+item.getTag_user_nickname());
            tv_content.setText(item.getReply_content());
            tv_register_date.setText(item.getReply_register_date());

            tv_create_re_reply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION) {
                        mListener.onCreate_Re_Reply(view, position);
                    }

                }
            });

            if(item.getRecommendation_count().equals("0")) {
                tv_recommendation_count.setText(null);
                tv_recommendation_count.setVisibility(View.GONE);
            } else {
                tv_recommendation_count.setText("추천 "+item.getRecommendation_count()+" 개");
                tv_recommendation_count.setVisibility(View.VISIBLE);
            }
            tv_recommendation_count.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "onClick: ");
                    Intent intent = new Intent(mContext, com.homework.book_sns.act_review.activity_reply_recommendation.class);
                    intent.putExtra("reply_id", item.getReply_id());
                    mContext.startActivity(intent);
                }
            });

            ll_reply.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    int position = getAdapterPosition();
                    Review_Reply item = items.get(position);

                    if(!item.getUser_info().getUser_id().equals(LoginSharedPref.getUserId(mContext))) {
                        return false;
                    }

                    String[] reply_option = mContext.getResources().getStringArray(R.array.review_reply2_option);
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setItems(reply_option, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if(reply_option[i].equals("답글 수정")) {
                                int position = getAdapterPosition();
                                if(position != RecyclerView.NO_POSITION) {
                                    mListener.onUpdate_Reply(view, position);
                                }
                            } else if(reply_option[i].equals("답글 삭제")) {
                                reply_delete(item, position, mContext);
                            }
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                    return true;
                }
            });

            btn_recommendation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();

                    if(btn_recommendation.isSelected()) { //추천한 상태
                        btn_recommendation.setEnabled(false);
                        recommendation_cancel(position , btn_recommendation, mContext);
                    } else { // 추천하지 않은 상태
                        btn_recommendation.setEnabled(false);
                        recommendation_doing(position, btn_recommendation, mContext);

                    }
                }
            });

            if(item.isClient_recommendation()) {
                btn_recommendation.setSelected(true);
            } else {
                btn_recommendation.setSelected(false);
            }

        }
    }
}
