package com.example.musicpost;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class DetailedPostActivity extends AppCompatActivity {
    Button backButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detailed_post);
        bindComponents(); // 화면에 있는 component 가져오기
    }

    public void bindComponents() {
        backButton = (Button)findViewById(R.id.backButton);
        setEventListeners(); // 이벤트 리스너 설정
    }

    public void setEventListeners() {
        backButton.setOnClickListener(backListener);
    }

    View.OnClickListener backListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            DetailedPostActivity.super.onBackPressed();
        }
    };

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}