package com.example.musicpost;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.security.MessageDigest;

public class MainActivity extends AppCompatActivity {
    TextView locationLabel;
    RelativeLayout currentPostCard;
    TextView titleLabel;
    TextView shortContentLabel;
    Button musicPlayButton;
    TextView musicTitleLabel;
    TextView musicArtistLabel;
    TextView postLocationLabel;
    Button addPostButton;

    View.OnClickListener postClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getApplicationContext(), PostActivity.class);
            intent.putExtra("source", "main");
            startActivity(intent);
            overridePendingTransition(R.anim.vertical_enter, R.anim.none);
        }
    };

    View.OnClickListener detailedClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getApplicationContext(), DetailedPostActivity.class);
            intent.putExtra("source", "main");
            startActivity(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bindComponents(); // 화면에 있는 component 가져오기
        setEventListeners(); // 이벤트 리스너 설정
        getAppKeyHash();
    }

    public void bindComponents() {
        locationLabel = (TextView)findViewById(R.id.locationLabel);
        currentPostCard = (RelativeLayout)findViewById(R.id.currentPostCard);
        titleLabel = (TextView)findViewById(R.id.titleLabel);
        shortContentLabel = (TextView)findViewById(R.id.shortContentLabel);
        musicPlayButton = (Button)findViewById(R.id.musicPlayButton);
        musicTitleLabel = (TextView)findViewById(R.id.musicTitleLabel);
        musicArtistLabel = (TextView)findViewById(R.id.musicArtistLabel);
        postLocationLabel = (TextView)findViewById(R.id.postLocationLabel);
        addPostButton = (Button)findViewById(R.id.addPostButton);
    }

    public void setEventListeners() {
        addPostButton.setOnClickListener(postClickListener);
        currentPostCard.setOnClickListener(detailedClickListener);
    }

    private void getAppKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());

                String something = Base64.encodeToString(md.digest(), Base64.NO_WRAP);
                Log.e("Debug key", something);
            }
        } catch (Exception e) {
            Log.e("Not found", e.toString());
        }
    }
}

