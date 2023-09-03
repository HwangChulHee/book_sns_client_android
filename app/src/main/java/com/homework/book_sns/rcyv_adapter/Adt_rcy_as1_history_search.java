package com.homework.book_sns.rcyv_adapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.homework.book_sns.R;
import com.homework.book_sns.search.activity_search_002;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;


//액티비티에 다음과 같은 문구를 추가해준다.
// LinearLayoutManager linearLayoutManager = new LinearLayoutManager(aContext);
// rcyv_.setLayoutManager(linearLayoutManager);

// adt_rcy_ = new Adt_rcy_(this, ACTIVITY_NAME, );
// rcyv_.setAdapter(adt_rcy_);

public class Adt_rcy_as1_history_search extends RecyclerView.Adapter<Adt_rcy_as1_history_search.ViewHolder> {

    private String TAG = "hch";
    private String RCY_NAME = "Adt_rcy_as1_history_search";
    private String ACTIVITY_NAME;

    private Activity activity;

    private ArrayList<String> items = new ArrayList<>();

    public Adt_rcy_as1_history_search(Activity activity, String activity_name)
    {
        this.activity = activity;
        this.ACTIVITY_NAME = activity_name;
    }

    public Adt_rcy_as1_history_search(Activity activity , String activity_name, ArrayList<String> items) {
        this.items = items;
        this.activity = activity;
        this.ACTIVITY_NAME = activity_name;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Adt_rcy_as1_history_search.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = null;

        itemView = inflater.inflate(R.layout.item_rcy_as1_history_search, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull Adt_rcy_as1_history_search.ViewHolder holder, int position)
    {
        String item = items.get(position);
        holder.setItem(item);
    }

    @Override
    public int getItemCount()
    {
        return items.size();
    }

    public void addItem(String item) {
        items.add(item);
    }

    public void setItemArrayList(ArrayList<String> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public void removeItem(String item) {
        items.remove(item);
    }


    public class ViewHolder extends RecyclerView.ViewHolder

    {
        TextView tv_history_of_searchKeyword;
        ImageView iv_remove_btn;

        public ViewHolder(@NonNull View view)
        {
            super(view);

            tv_history_of_searchKeyword = view.findViewById(R.id.tv_irahs_history_search_keyword);
            iv_remove_btn = view.findViewById(R.id.iv_irahs_remove_btn);
        }

        public void setItem(String item) {
           tv_history_of_searchKeyword.setText(item);

           tv_history_of_searchKeyword.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   // 이전 검색어 클릭 시, activity_search_002(검색 결과 페이지)로 액티비티 이동한다
                   // intent 에 검색어(item)를 담아준다.
                   Intent intent = new Intent(activity, activity_search_002.class);
                   intent.putExtra("search_keyword" , item);
                   activity.startActivity(intent);
               }
           });

           iv_remove_btn.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   removeItem(item); // 1. 리싸이클러뷰의 배열에서 삭제하고

                   String key = activity.getString(R.string.editorKeyOfShardPreference_searchKeyword);
                   remove_ShardPreference_byJson(key, item); // 2. ShardPreference에서도 삭제해준다.
                   notifyDataSetChanged();
               }
           });
        }
    }

    //ShardPreference 값을 삭제하는 메서드.
    //받는 인자로 R.string.editorKeyOfShardPreference 에 저장되어 있는 값을 받는다.
    private void remove_ShardPreference_byJson(String editorKeyOfShardPreference, String remove_value) {
        // 1. SharedPreference 객체 얻기
        //strings.xml에 keyOfShardPreference를 입력해줘야한다.
        SharedPreferences sharedPref = activity.getSharedPreferences(
                 activity.getString(R.string.keyOfShardPreference), Context.MODE_PRIVATE);

        String defaultValue = activity.getString(R.string.defaultValue_ofShardPreference); //sharedPreference의 default 값

        // 2. SharedPreferences 객체를 통해 값을 가져와준다.
        String string_ofShardPreference = sharedPref.getString(editorKeyOfShardPreference, defaultValue); // 기존의 key 값에 해당하는 값들이 있는 json화 되어있는 string 데이터
        ArrayList<String> values = new ArrayList<String>(); // 기존의 key 값에 해당하는 값들을 저장할 ArrayList
        if (string_ofShardPreference != null) {
            try {
                JSONArray jsonArray_ofShardPreference = new JSONArray(string_ofShardPreference); // string을 jsonArray로 변환해준다.
                //values에 있는 값들을 넣어주는 반복문
                for (int i = 0; i < jsonArray_ofShardPreference.length(); i++) {
                    String value = jsonArray_ofShardPreference.optString(i); // jsonArray에 있는 값들을 하나씩 추출해주고
                    values.add(value); // 그 값을 values에 저장해준다.
                }

                //values에 있는 값들 중, remove_value와 겹치는 값들을 제거해주는 반복문
                for (int i = 0; i < values.size(); i++){
                    String value = values.get(i); // values의 값들을 하나씩 가져온 뒤,
                    if(value.equals(remove_value)) { // remove_value와 값이 중복되면
                        values.remove(i);  // 제거해준다.
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // 3. values를 json화 시켜준다.
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < values.size(); i++) {
            jsonArray.put(values.get(i));
        }

        // 4. SharedPreferences 객체를 통해 editor를 생성해주고 값을 넣어준다.
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(editorKeyOfShardPreference, jsonArray.toString());
        editor.apply();
    }

    private void toast_rcyv(String msg) {
        Toast.makeText(activity,
                "액티비티 이름: "+ACTIVITY_NAME + " , "
                        +"리싸이클러뷰 : "+ RCY_NAME + " , " +msg
                ,Toast.LENGTH_LONG).show();
    }
    private void log_activity(String msg) {
        Log.d(TAG, "액티비티 이름: "+ACTIVITY_NAME +", 리싸이클러뷰 : "+RCY_NAME
                +", 로그 내용 : "+msg);
    }
}
