package com.homework.book_sns;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.homework.book_sns.R;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class experient extends AppCompatActivity {

    String TAG = "hch";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experient);

        Button button = findViewById(R.id.btn_ex);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: ");
                PopupMenu popupMenu = new PopupMenu(getApplicationContext(), view);
                getMenuInflater().inflate(R.menu.mypage_popup, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        Log.d(TAG, "onMenuItemClick: ");
                        switch (menuItem.getItemId()) {
                            case R.id.mypage_menu1:
                                Toast.makeText(getApplicationContext(), "북마크", Toast.LENGTH_LONG).show();
                                return true;
                            case R.id.mypage_menu2:
                                Toast.makeText(getApplicationContext(), "환경설정", Toast.LENGTH_LONG).show();
                                return true;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });

        getHashKey();
    }

    private void getHashKey(){
        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageInfo == null)
            Log.e("KeyHash", "KeyHash:null");

        for (Signature signature : packageInfo.signatures) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            } catch (NoSuchAlgorithmException e) {
                Log.e("KeyHash", "Unable to get MessageDigest. signature=" + signature, e);
            }
        }
    }
}