package com.example.musicpost;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PostActivity extends AppCompatActivity {
    Button searchMusicButton;
    View.OnClickListener searchMusicListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getApplicationContext(), MusicSearchActivity.class);
            startActivity(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.vertical_enter, R.anim.none);
        setContentView(R.layout.activity_post);
        bindComponents();
        setEventListeners();
    }

    public void bindComponents() {
        searchMusicButton = (Button)findViewById(R.id.searchMusicButton);
    }

    public void setEventListeners() {
        searchMusicButton.setOnClickListener(searchMusicListener);
    }
}
