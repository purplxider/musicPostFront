package com.example.musicpost;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class PostActivity extends AppCompatActivity {
    Button backButton;
    Button saveButton;
    EditText titleTextBox;
    EditText postContentTextBox;
    Button searchMusicButton;
    Button searchLocationButton;
    TextView selectedLocationLabel;
    TextView detailedLocationLabel;
    Button musicPlayButton;
    TextView musicTitleLabel;
    TextView musicArtistLabel;
    MediaPlayer mediaPlayer = null;
    String musicURL = "";
    ActivityResultLauncher<Intent> searchActivityResultLauncher;
    private String address = "";
    private String name = "위치를 설정해주세요";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        mediaPlayer = new MediaPlayer();
        bindComponents();
        setEventListeners();
        searchActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            String address = "";
                            String name = "위치를 설정하세요";
                            String musicTitle = "음악을 설정하세요";
                            String musicArtists = "";

                            if (data.getStringExtra("address") != null) address = data.getStringExtra("address");
                            if (data.getStringExtra("name") != null) name = data.getStringExtra("name");
                            if(data.getStringExtra("musicURL") != null) musicURL = data.getStringExtra("musicURL");
                            if(data.getStringExtra("musicTitle") != null) musicTitle = data.getStringExtra("musicTitle");
                            if(data.getStringExtra("musicArtists") != null) musicArtists = data.getStringExtra("musicArtists");

                            selectedLocationLabel.setText(name);
                            detailedLocationLabel.setText(address);
                            musicTitleLabel.setText(musicTitle);
                            musicArtistLabel.setText(musicArtists);
                        }
                    }
                });
    }

    @Override
    public void finish() {
        super.finish();
        mediaPlayer.release();
        overridePendingTransition(R.anim.none, R.anim.vertical_exit);
    }

    public void bindComponents() {
        searchMusicButton = (Button) findViewById(R.id.searchMusicButton);
        searchLocationButton = (Button) findViewById(R.id.searchLocationButton);
        selectedLocationLabel = (TextView) findViewById(R.id.selectedLocationLabel);
        detailedLocationLabel = (TextView) findViewById(R.id.detailedLocationLabel);
        backButton = (Button) findViewById(R.id.backButton);
        saveButton = (Button) findViewById(R.id.saveButton);
        titleTextBox = (EditText) findViewById(R.id.titleTextBox);
        postContentTextBox = (EditText) findViewById(R.id.postContentTextBox);
        musicPlayButton = (Button) findViewById(R.id.musicPlayButton);
        musicTitleLabel = (TextView) findViewById(R.id.musicTitleLabel);
        musicArtistLabel = (TextView) findViewById(R.id.musicArtistLabel);
    }

    View.OnClickListener searchMusicListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getApplicationContext(), MusicSearchActivity.class);
            intent.putExtra("source", "post");
            searchActivityResultLauncher.launch(intent);
            overridePendingTransition(R.anim.horizontal_enter, R.anim.none);
        }
    };

    View.OnClickListener backListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            PostActivity.super.onBackPressed();
        }
    };

    View.OnClickListener musicPlay = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (musicURL == null) {

            } else {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                } else {
                    mediaPlayer.reset();
                    try {
                        mediaPlayer.setDataSource(musicURL);
                        mediaPlayer.prepare();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    mediaPlayer.start();
                }
            }
        }
    };

    View.OnClickListener searchLocationListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getApplicationContext(), LocationSearchActivity.class);
            intent.putExtra("source", "post");
            searchActivityResultLauncher.launch(intent);
            overridePendingTransition(R.anim.horizontal_enter, R.anim.none);
        }
    };

    public void setEventListeners() {
        searchMusicButton.setOnClickListener(searchMusicListener);
        searchLocationButton.setOnClickListener(searchLocationListener);
        backButton.setOnClickListener(backListener);
        musicPlayButton.setOnClickListener(musicPlay);
    }

}
