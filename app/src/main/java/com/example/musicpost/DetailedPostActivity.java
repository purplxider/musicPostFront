package com.example.musicpost;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.util.Random;

public class DetailedPostActivity extends AppCompatActivity {

    RelativeLayout musicPlayer;
    RelativeLayout detailedPostCard;
    RelativeLayout recommendedPostCard;
    ImageButton backButton;
    String color = "yellow";
    String musicURL = "";
    MediaPlayer mediaPlayer;
    ImageButton recommendedPostMusicPlayButton;
    TextView titleLabel;
    TextView currentLocationLabel;
    TextView originalPosterLabel;
    TextView detailedContentLabel;
    TextView musicTitleLabel;
    TextView musicArtistLabel;
    ImageButton likeButton;
    Boolean liked = false;

    private String title = "";
    private String location = "";
    private String description = "";
    private String poster = "";
    private String musicTitle = "";
    private String musicArtist = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_post);
        bindComponents(); // 화면에 있는 component 가져오기
        mediaPlayer = new MediaPlayer();
        title = getIntent().getStringExtra("title");
        titleLabel.setText(title);
        location = getIntent().getStringExtra("location");
        currentLocationLabel.setText(location);
        description = getIntent().getStringExtra("description");
        detailedContentLabel.setText(description);
        poster = getIntent().getStringExtra("poster");
        originalPosterLabel.setText("작성자: "+ poster);
        musicArtist = getIntent().getStringExtra("musicArtist");
        musicArtistLabel.setText(musicArtist);
        musicTitle = getIntent().getStringExtra("musicTitle");
        musicTitleLabel.setText(musicTitle);
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
        setEventListeners(); // 이벤트 리스너 설정
    }

    public void bindComponents() {
        musicPlayer = (RelativeLayout)findViewById(R.id.musicPlayer);
        detailedPostCard = (RelativeLayout)findViewById(R.id.detailedPostCard);
        recommendedPostCard = (RelativeLayout)findViewById(R.id.recommendedPostCard);
        setColor(recommendedPostCard);
        backButton = (ImageButton)findViewById(R.id.backButton);
        recommendedPostMusicPlayButton = (ImageButton)findViewById(R.id.recommendedPostMusicPlayButton);
        titleLabel = (TextView)findViewById(R.id.titleLabel);
        currentLocationLabel = (TextView)findViewById(R.id.currentLocationLabel);
        detailedContentLabel = (TextView)findViewById(R.id.detailedContentLabel);
        originalPosterLabel = (TextView) findViewById(R.id.originalPosterLabel);
        musicTitleLabel = (TextView)findViewById(R.id.musicTitleLabel);
        musicArtistLabel = (TextView)findViewById(R.id.musicArtistLabel);
        likeButton = (ImageButton) findViewById(R.id.likeButton);
    }

    public void setEventListeners() {
        backButton.setOnClickListener(backListener);
        //recommendedPostMusicPlayButton.setOnClickListener(musicPlayListener);
        likeButton.setOnClickListener(likeListener);
    }

    void playMusic() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        } else if (musicURL != "") {
            try {
                mediaPlayer.setDataSource(musicURL);
                mediaPlayer.prepare();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    recommendedPostMusicPlayButton.setImageResource(R.drawable.play);
                }
            });
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
            playMusic();
        }
    };

    View.OnClickListener likeListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (liked == false) {
                likeButton.setImageResource(R.drawable.favorite_fill);
                liked = true;
            } else {
                likeButton.setImageResource(R.drawable.favorite);
                liked = false;
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