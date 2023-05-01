package com.example.musicpost;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class PostActivity extends AppCompatActivity {
    Button searchMusicButton;
    Button searchLocationButton;
    View.OnClickListener searchMusicListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getApplicationContext(), MusicSearchActivity.class);
            startActivity(intent);
        }
    };

    View.OnClickListener searchLocationListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getApplicationContext(), LocationSearchActivity.class);
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
        searchLocationButton = (Button)findViewById(R.id.searchLocationButton);
    }

    public void setEventListeners() {
        searchMusicButton.setOnClickListener(searchMusicListener);
        searchLocationButton.setOnClickListener(searchLocationListener);
    }
}
