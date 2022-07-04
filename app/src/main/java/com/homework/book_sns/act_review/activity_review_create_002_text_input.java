package com.homework.book_sns.act_review;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.homework.book_sns.R;

public class activity_review_create_002_text_input extends AppCompatActivity {

    /* --------------------------- */
    // 액티비티 필수 객체들
    Context aContext;
    String TAG = "hch";
    /* --------------------------- */

    /* --------------------------- */
    // xml의 view 객체들
    TextView tv_input_cancel;
    TextView tv_input_complete;

    TextView tv_max_text;
    EditText ev_text_input;
    /* --------------------------- */

    /* --------------------------- */
    //필요한 객체들
    String ori_text;
    /* --------------------------- */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_create_002_text_input);

        aContext = this;

        setObject();
        setView();
    }

    private void setObject() {
        getMyIntent();
    }

    private void getMyIntent() {
        Intent intent = getIntent();
        ori_text = intent.getStringExtra("ori_text");
    }


    private void setView() {
        ActionBar bar = getSupportActionBar();
        bar.hide();

        tv_input_cancel = (TextView) findViewById(R.id.tv_rc2_text_cancel);
        tv_input_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        tv_input_complete = (TextView) findViewById(R.id.tv_rc2_text_complete);
        tv_input_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("new_text", ev_text_input.getText().toString());
                setResult(RESULT_OK, intent);
                finish();
            }
        });


        tv_max_text = (TextView) findViewById(R.id.tv_rc2_text_max);
        ev_text_input = (EditText) findViewById(R.id.etv_rc2_text_input);

        ev_text_input.setText(ori_text);

        ev_text_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                tv_max_text.setText(editable.length()+" / 21844 자");
            }
        });

    }
}