package com.example.musicpost;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.util.Random;

public class DetailedPostActivity extends AppCompatActivity {

    RelativeLayout musicPlayer;
    RelativeLayout detailedPostCard;
    RelativeLayout recommendedPostCard;
    ImageButton backButton;
    ImageButton musicPlayButton;
    String color = "yellow";
    String musicURL = "";
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_post);
        bindComponents(); // 화면에 있는 component 가져오기
        mediaPlayer = new MediaPlayer();
            color = getIntent().getStringExtra("color");
            musicURL = getIntent().getStringExtra("musicURL") != null ? getIntent().getStringExtra("musicURL") : "";
            if (color.equals("yellow")) {
                musicPlayer.setBackgroundResource(R.drawable.rounded_rectangle);
                musicPlayer.setBackgroundColor(ContextCompat.getColor(this, R.color.yellow));
                detailedPostCard.setBackgroundColor(ContextCompat.getColor(this, R.color.yellow));
            } else if (color.equals("blue")) {
                musicPlayer.setBackgroundResource(R.drawable.rounded_rectangle);
                musicPlayer.setBackgroundColor(ContextCompat.getColor(this, R.color.blue));
                detailedPostCard.setBackgroundColor(ContextCompat.getColor(this, R.color.blue));
            } else if (color.equals("green")) {
                musicPlayer.setBackgroundResource(R.drawable.rounded_rectangle);
                musicPlayer.setBackgroundColor(ContextCompat.getColor(this, R.color.green));
                detailedPostCard.setBackgroundColor(ContextCompat.getColor(this, R.color.green));
            }

            System.out.println(musicURL);
    }

    public void bindComponents() {
        musicPlayer = (RelativeLayout)findViewById(R.id.musicPlayer);
        detailedPostCard = (RelativeLayout)findViewById(R.id.detailedPostCard);
        recommendedPostCard = (RelativeLayout)findViewById(R.id.recommendedPostCard);
        setColor(recommendedPostCard);
        musicPlayButton = (ImageButton)findViewById(R.id.musicPlayButton);
        backButton = (ImageButton)findViewById(R.id.backButton);
        setEventListeners(); // 이벤트 리스너 설정
    }

    public void setEventListeners() {
        backButton.setOnClickListener(backListener);
        musicPlayButton.setOnClickListener(musicPlayListener);
    }

    void playMusic() {
        mediaPlayer.reset();
        if (musicURL != "" && musicURL != null) {
            try {
                mediaPlayer.setDataSource(musicURL);
                mediaPlayer.prepare();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            mediaPlayer.start();
        }
    }

    View.OnClickListener backListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mediaPlayer.release();
            DetailedPostActivity.super.onBackPressed();
        }
    };

    View.OnClickListener musicPlayListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                musicPlayButton.setImageResource(R.drawable.play);
            } else if (musicURL != "") {
                try {
                    mediaPlayer.setDataSource(musicURL);
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                mediaPlayer.start();
                musicPlayButton.setImageResource(R.drawable.stop);
            }
        }
    };

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void setColor(RelativeLayout postcard) {
        Random random = new Random();
        int randomNumber = random.nextInt(3);
        if (randomNumber == 0) {
            postcard.setBackgroundColor(ContextCompat.getColor(this, R.color.yellow));
            color = "yellow";
        }
        else if (randomNumber == 1) {
            postcard.setBackgroundColor(ContextCompat.getColor(this, R.color.blue));
            color = "blue";
        }
        else if (randomNumber == 2) {
            postcard.setBackgroundColor(ContextCompat.getColor(this, R.color.green));
            color = "green";
        }
    }
}