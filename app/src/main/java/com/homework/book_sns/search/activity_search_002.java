package com.homework.book_sns.search;
import android.content.Context;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.homework.book_sns.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class activity_search_002 extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener {

    //기본 객체들
    private String TAG = "hch";
    private static String ACTIVITY_NAME = "activity_search_002";
    private static String ACTIVITY_FUNCTION = "search";
    private static Context aContext;

    //뷰 객체들
    Button btn_back; // 뒤로 가기 버튼
    EditText etv_search; // 검색 버튼
    ImageButton ibtn_cancel; // 검색어 취소 버튼
    TabLayout tl_search_type; // 검색 타입을 설정할 수 있는 탭 레이아웃 (전체, 리뷰, 사람, 그룹)

    FrameLayout frame_search; // tl_search_type에 따라서 바뀌는 프래그먼트

    //각종 객체들
    String search_keyword; // 전달받은 검색어

    fragment_search_entire fragment_search_entire;
    fragment_search_review fragment_search_review;
    fragment_search_people fragment_search_people;
    fragment_search_group fragment_search_group;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_002);

        set_activity(); //oncreate시, 초기화 해주는 역할을 하는 메서드
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    //oncreate시, 초기화 해주는 역할을 하는 메서드
    private void set_activity() {
        setObject(); //oncreate 시, 필수 객체 초기화 해주는 메소드
        my_findView(); //oncreate 시, findViewById() 해주는 메서드
        my_getIntent(); //intent 로부터 전달받은 값을 가져오는 메서드
        my_setClickListener(); //oncreate 시, onClickListener 를 등록해주는 메서드.
        my_initSetView(); // oncreate 시, view의 데이터를 초기화해주는 메서드

        ActionBar bar = getSupportActionBar();
        bar.hide(); // 액션바를 지워준다.
    }

    //oncreate 시, 필수 객체 초기화 해주는 메소드
    private void setObject() {
        aContext = this;
        
        // 프래그먼트 객체들 초기화
        fragment_search_entire = new fragment_search_entire();
        fragment_search_review = new fragment_search_review();
        fragment_search_people = new fragment_search_people();
        fragment_search_group = new fragment_search_group();
    }

    //intent 로부터 전달받은 값을 가져오는 메서드
    private void my_getIntent() {
        //activity_search_001로부터 검색어를 가져온다.
        Intent intent = getIntent();
        search_keyword = intent.getStringExtra("search_keyword");

        //etv_search에 해당 검색어를 설정해준다.
        etv_search.setText(search_keyword);
        // editText의 깜빡임을 제거한다. onClick 메서드에서 editText 클릭 시 다시 깜빡이도록 설정해준다.
        etv_search.setCursorVisible(false);
    }

    //oncreate 시, findViewById() 해주는 메소드
    private void my_findView() {
        btn_back = findViewById(R.id.btn_as2_back); // 뒤로 가기 버튼
        etv_search = findViewById(R.id.etv_as2_search);; // 검색 버튼
        ibtn_cancel = findViewById(R.id.ibtn_as2_cancel);; // 검색어 취소 버튼
        tl_search_type = findViewById(R.id.tl_as2_search_type);; // 검색 타입을 설정할 수 있는 탭 레이아웃 (전체, 리뷰, 사람, 그룹)

        frame_search = findViewById(R.id.frame_as2_search);; // tl_search_type에 따라서 바뀌는 프래그먼트

        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); // 키보드 객체 받아오기
        imm.hideSoftInputFromWindow(etv_search.getWindowToken(), 0 ); // 키보드 숨기기
    }

    //oncreate 시, onClickListener 를 등록해주는 메소드.
    // 클래스에 View.OnClickListener를 implements 시켜줘야한다.
    private void my_setClickListener() {
        btn_back.setOnClickListener(this); // 뒤로 가기 버튼
        etv_search.setOnClickListener(this); // 검색어 입력버튼 클릭했을 때 커서 활성화
        ibtn_cancel.setOnClickListener(this); // x 버튼 눌렀을때, 검색어 입력 창 값 초기화하는 기능

        etv_search.setOnKeyListener(this);

        tl_search_type.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Bundle bundle = new Bundle(); // 1) 번들 생성
                bundle.putString("search_keyword", search_keyword); // 2) 번들에 데이터 담기
                switch(tab.getPosition()){
                    case 0 :
                        fragment_search_review.setArguments(bundle); // 4) 프래그먼트에 번들 담기
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_as2_search, fragment_search_review).commit();
                        break;

                    case 1 :
                        fragment_search_people.setArguments(bundle); // 4) 프래그먼트에 번들 담기
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_as2_search, fragment_search_people).commit();
                        break;

                    case 2 :
                        fragment_search_group.setArguments(bundle); // 4) 프래그먼트에 번들 담기
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_as2_search, fragment_search_group).commit();
                        break;

                    default:
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
    
    //클릭 이벤트를 등록해주는 메서드
    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.btn_as2_back:
                finish();
                break;

            case R.id.etv_as2_search:
                etv_search.setCursorVisible(true);
                break;

            case R.id.ibtn_as2_cancel:
                etv_search.setText(null);
                break;

            default:
                break;
        }
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
                case R.id.etv_as2_search:
                    rebrowsing_search_keyword(); //검색어를 입력했을 때의 동작을 처리해주는 메서드
                    break;

                default:
                    break;
            }
            return true;
        }
        return false;
    }

    // oncreate 시, view의 데이터를 초기화해주는 메서드
    private void my_initSetView() {
        // 검색 시, 모든 카테고리의 데이터를 보여주는 프래그먼트(fragment_search_entire)를 띄어준다.

        Bundle bundle = new Bundle(); // 1) 번들 생성
        bundle.putString("search_keyword", search_keyword); // 2) 번들에 데이터 담기
        fragment_search_review.setArguments(bundle); // 4) 프래그먼트에 번들 담기
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_as2_search, fragment_search_review).commit(); // 5) 해당 프래그먼트로 이동
    }

    //검색어를 입력했을 때의 동작(재 검색)을 처리해주는 메서드
    private void rebrowsing_search_keyword() {

        // 1. etv_search 에 입력한 검색어 결과를 가져온다.
        this.search_keyword = etv_search.getText().toString();


        // 2. 입력값이 없을 시, 아무 행동도 하지 않는다.
        if(search_keyword.equals("")) {
            toast_activity("검색어를 입력해주세요.");
            return;
        }

        // 3. ShardPreference에 search_keyword를 담아준다.
        String key = getString(R.string.editorKeyOfShardPreference_searchKeyword);
        write_ShardPreference_byJsonArray(key, search_keyword);

        // 4. 현재 탭하고 있는 프래그먼트에 있는 결과를 갱신해준다.
        switch(tl_search_type.getSelectedTabPosition()){
            case 0:
                fragment_search_review.rebrowsing_review(search_keyword); // 리뷰에 대해서 재검색
                break;

            case 1:
                fragment_search_people.rebrowsing_people(search_keyword); // 사람에 대해서 재검색
                break;

            case 2:
                fragment_search_group.rebrowsing_group(search_keyword); // 그룹에 대해서 재검색
                break;

          default:
            break;
        }

        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); // 키보드 객체 받아오기
        imm.hideSoftInputFromWindow(etv_search.getWindowToken(), 0 ); // 키보드 숨기기
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

    //로그 찍는 메서드
    private void log_activity(String msg) {
        Log.d(TAG, "액티비티 이름: "+ACTIVITY_NAME +", 액티비티 기능 : "+ACTIVITY_FUNCTION
            +", 로그 내용 : "+msg);
    }

    //toast 찍는 메서드
    private void toast_activity(String msg) {
        Toast.makeText(aContext, msg ,Toast.LENGTH_SHORT).show();
    }


}