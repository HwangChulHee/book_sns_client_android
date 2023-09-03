package com.homework.book_sns.search;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.homework.book_sns.R;
import com.homework.book_sns.rcyv_adapter.Adt_rcy_as1_history_search;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class activity_search_001 extends AppCompatActivity
        implements View.OnClickListener, View.OnKeyListener{
    
    //기본 객체들
    private String TAG = "hch";
    private String ACTIVITY_NAME = "activity_search_001";
    private String ACTIVITY_FUNCTION = "search";
    private Context aContext;
    
    //뷰 객체들
    Button btn_back;
    EditText etv_search;
    ImageButton ibtn_cancel;
    RecyclerView rcyv_recent_search;

    //각종 객체들
    boolean isOnResume = false; // onResume이 발생했었는지에 대한 여부를 알려준다.
    Adt_rcy_as1_history_search adt_rcy_as1_history_search;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_001);

        set_activity();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isOnResume) { // onResume이 발생한 적이 있었는가?
            renew_rcyv_search_keyword(); // 리싸이클러뷰를 갱신해준다.
        } else {
            isOnResume = true; // onResume이 발생한 적이 없었으면 isOnResume을 true로 바꾸어준다.
        }

    }

    //oncreate시, 초기화 해주는 역할을 하는 메서드
    private void set_activity() {
        setObject(); //oncreate 시, 필수 객체 초기화 해주는 메소드
        my_findView(); //oncreate 시, findViewById() 해주는 메소드
        my_setClickListener(); //oncreate 시, onClickListener 를 등록해주는 메소드.
        my_setView();

        ActionBar bar = getSupportActionBar();
        bar.hide(); // 액션바를 지워준다.
    }

    //oncreate 시, 필수 객체 초기화 해주는 메소드
    private void setObject() {
        aContext = this;
    }

    //oncreate 시, findViewById() 해주는 메소드
    private void my_findView() {
        btn_back = findViewById(R.id.btn_as1_back);
        etv_search = findViewById(R.id.etv_as1_search);
        ibtn_cancel = findViewById(R.id.ibtn_as1_cancel);
        rcyv_recent_search = findViewById(R.id.rcvy_as1_recent_search);
    }

    //oncreate 시, onClickListener 를 등록해주는 메소드.
    // 클래스에 View.OnClickListener를 implements 시켜줘야한다.
    private void my_setClickListener() {
        btn_back.setOnClickListener(this);
        ibtn_cancel.setOnClickListener(this); // 오버라이딩한 onClick 참고
        
        etv_search.setOnKeyListener(this); // 오버라이딩한 onKey 참고
    }

    //onClick 메서드 - 클릭 이벤트를 등록해주는 메서드
    // View.OnKeyListener 를 implements 해줘야함.
    // 그리고 setOnClickListener 를 등록해줘야함.
    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.btn_as1_back:
                finish();
                break;

            case R.id.ibtn_as1_cancel:
                etv_search.setText(null);
                break;

            default:
                break;
        }
    }

    private void my_setView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(aContext);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true); // 리싸이클러뷰의 아이템을 역순으로 보여주는 기능을 한다.
        
         rcyv_recent_search.setLayoutManager(linearLayoutManager);


        // 1. ShardPreference를 통해 검색 기록을 가져온다.
        String editorKeyOfShardPreference = getString(R.string.editorKeyOfShardPreference_searchKeyword);
        ArrayList<String> search_keywords = read_ShardPreference_byJson(editorKeyOfShardPreference);

        // 2. 검색기록이 표시되는 리싸이클러뷰를 설정해준다.
        adt_rcy_as1_history_search = new Adt_rcy_as1_history_search(this, ACTIVITY_NAME, search_keywords);
        rcyv_recent_search.setAdapter(adt_rcy_as1_history_search);
    }
    
    // 다른 액티비티를 갔다가 왔을 때 (onResume) 발생하는 리싸이클러뷰 갱신 이벤트
    private void renew_rcyv_search_keyword() {
        // 1. ShardPreference를 통해 검색 기록을 가져온다.
        String editorKeyOfShardPreference = getString(R.string.editorKeyOfShardPreference_searchKeyword);
        ArrayList<String> search_keywords = read_ShardPreference_byJson(editorKeyOfShardPreference);

        adt_rcy_as1_history_search.setItemArrayList(search_keywords);
        
    }

    //onKey 메서드 - editText 의 입력을 감지한다.
    // View.OnKeyListener 를 implements 해줘야함.
    // 그리고 setOnKeyListener 를 등록해줘야함.
    @Override
    public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
        //OnKey 는 두 번 실행됩니다. 첫 번째는 키 다운, 두 번째는 키 업이므로 다음을 필터링해야합니다.
        if (keyEvent.getAction()!=KeyEvent.ACTION_DOWN)
            return true;

        if(keyCode == KeyEvent.KEYCODE_ENTER) {
            switch (view.getId()) {
                case R.id.etv_as1_search:
                    enter_search_keyword(); //검색어를 입력했을 때의 동작을 처리해주는 메서드
                    break;

                default:
                    break;
            }
            return true;
        }
        return false;
    }

    //검색어를 입력했을 때의 동작(검색 결과 페이지로의 이동)을 처리해주는 메서드
    private void enter_search_keyword() {

        // 1. etv_search 에 입력한 검색어 결과를 가져온다.
        String search_keyword = etv_search.getText().toString();

        // 2. 입력값이 없을 시, 액티비티를 이동하지 않는다.
        if(search_keyword.equals("")) {
            toast_activity("검색어를 입력해주세요.");
            return;
        }

        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); // 키보드 객체 받아오기
        imm.hideSoftInputFromWindow(etv_search.getWindowToken(), 0 ); // 키보드 숨기기

        // 3. ShardPreference에 search_keyword를 담아준다.
        String key = getString(R.string.editorKeyOfShardPreference_searchKeyword);
        write_ShardPreference_byJsonArray(key, search_keyword);

        // 4. activity_search_002(검색 결과 페이지)로 액티비티 이동한다
        // intent 에 검색어를 담아준다.
        Intent intent = new Intent(this, activity_search_002.class);
        intent.putExtra("search_keyword" , search_keyword);
        startActivity(intent);
    }

    //ShardPreference 값을 write하는 메서드. key와 value를 입력받는다.
    private void write_ShardPreference_byJsonArray(String key, String value ) {
        // 1. SharedPreference 객체 얻기
        //strings.xml에 keyOfShardPreference를 입력해줘야한다.
        SharedPreferences sharedPref = this.getSharedPreferences(
                getString(R.string.keyOfShardPreference), Context.MODE_PRIVATE);


        String defaultValue = getString(R.string.keyOfShardPreference); //sharedPreference의 default 값

        // 2. 기존에 해당 key값에 값이 있는지 확인하는 로직
        String string_ofShardPreference = sharedPref.getString(key, defaultValue); // 기존의 key 값에 해당하는 값들이 있는 json화 되어있는 string 데이터
        ArrayList<String> previous_values = new ArrayList<String>(); // 기존의 key 값에 해당하는 값들을 저장할 ArrayList
        if (string_ofShardPreference != null) {
            try {
                JSONArray jsonArray_ofShardPreference = new JSONArray(string_ofShardPreference); // string을 jsonArray로 변환해준다.
                int arrayLength = jsonArray_ofShardPreference.length(); // array의 길이
                for (int i = 0; i < arrayLength; i++) {
                    String previous_value = jsonArray_ofShardPreference.optString(i); // jsonArray에 있는 값들을 하나씩 추출해주고

                    if(!previous_value.equals(value)) { // (중복 제거) value 값과 겹치지 않는다면
                        previous_values.add(previous_value); // 그 값을 previous_values에 저장해준다.
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // 3. 이전 값들(previous_values)을 현재 arrayList(values)에 저장하고, 인자로 받아온 value 값을 추가해준다.
        ArrayList<String> values = previous_values;
        values.add(value);

        // 4. 검색어가 30개를 넘어갈 시, 가장 오래된 검색어를 삭제해준다.
        if(values.size() > 30) {
            values.remove(0);
        }

        // 5. 이전 값들에 인자를 추가한 values를 json화 시켜준다.
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < values.size(); i++) {
            jsonArray.put(values.get(i));
        }

        // 6. SharedPreferences 객체를 통해 editor를 생성해주고 값을 넣어준다.
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, jsonArray.toString());
        editor.apply();
    }

    //ShardPreference 값을 read하는 메서드
    //받는 인자로 R.string.editorKeyOfShardPreference 에 저장되어 있는 값을 받는다.
    private ArrayList<String> read_ShardPreference_byJson(String editorKeyOfShardPreference) {
        // 1. SharedPreference 객체 얻기
        //strings.xml에 keyOfShardPreference를 입력해줘야한다.
        SharedPreferences sharedPref = this.getSharedPreferences(
                getString(R.string.keyOfShardPreference), Context.MODE_PRIVATE);

        String defaultValue = getString(R.string.defaultValue_ofShardPreference); //sharedPreference의 default 값

        // 2. SharedPreferences 객체를 통해 값을 가져와준다.
        String string_ofShardPreference = sharedPref.getString(editorKeyOfShardPreference, defaultValue); // 기존의 key 값에 해당하는 값들이 있는 json화 되어있는 string 데이터
        ArrayList<String> values = new ArrayList<String>(); // 기존의 key 값에 해당하는 값들을 저장할 ArrayList
        if (string_ofShardPreference != null) {
            try {
                JSONArray jsonArray_ofShardPreference = new JSONArray(string_ofShardPreference); // string을 jsonArray로 변환해준다.
                for (int i = 0; i < jsonArray_ofShardPreference.length(); i++) {
                    String value = jsonArray_ofShardPreference.optString(i); // jsonArray에 있는 값들을 하나씩 추출해주고
                    values.add(value); // 그 값을 values에 저장해준다.
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // 3. 생성된 values 를 반환해준다.
        return  values;
    }

    //ShardPreference 값을 삭제하는 메서드.
    //받는 인자로 R.string.editorKeyOfShardPreference 에 저장되어 있는 값을 받는다.
    private void remove_ShardPreference_byJson(String editorKeyOfShardPreference, String remove_value) {
        // 1. SharedPreference 객체 얻기
        //strings.xml에 keyOfShardPreference를 입력해줘야한다.
        SharedPreferences sharedPref = this.getSharedPreferences(
                getString(R.string.keyOfShardPreference), Context.MODE_PRIVATE);

        String defaultValue = getString(R.string.defaultValue_ofShardPreference); //sharedPreference의 default 값

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


    //로그 찍는 메서드
    private void log_activity(String msg) {
        Log.d(TAG, "액티비티 이름: "+ACTIVITY_NAME +", 액티비티 기능 : "+ACTIVITY_FUNCTION
            +", 로그 내용 : "+msg);
    }

    //toast 찍는 메서드
    private void toast_activity(String msg) {
        Toast.makeText(aContext, msg ,Toast.LENGTH_LONG).show();
    }


}