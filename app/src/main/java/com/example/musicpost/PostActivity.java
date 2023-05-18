package com.example.musicpost;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PostActivity extends AppCompatActivity {
    ImageButton backButton;
    Button saveButton;
    EditText titleTextBox;
    EditText postContentTextBox;
    ImageButton searchMusicButton;
    ImageButton searchLocationButton;
    TextView selectedLocationLabel;
    TextView detailedLocationLabel;
    ImageButton musicPlayButton;
    TextView musicTitleLabel;
    TextView musicArtistLabel;
    MediaPlayer mediaPlayer = null;
    String musicURL = "";
    Double longitude = 0.0;
    Double latitude = 0.0;
    ActivityResultLauncher<Intent> searchActivityResultLauncher;
    private String address = "";
    private String name = "위치를 설정해주세요";
    private String musicTitle = "음악을 설정하세요";
    private String musicArtists = "";
    private String savedUsername = "";
    private String savedPassword = "";

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

                            if (data.getStringExtra("source").equals("location")) {
                                if (data.getStringExtra("address") != null) address = data.getStringExtra("address");
                                if (data.getStringExtra("name") != null) name = data.getStringExtra("name");
                                longitude = data.getDoubleExtra("longitude", 0);
                                latitude = data.getDoubleExtra("latitude", 0);
                            } else if (data.getStringExtra("source").equals("music")) {
                                if (data.getStringExtra("musicURL") != null)
                                    musicURL = data.getStringExtra("musicURL");
                                if (data.getStringExtra("musicTitle") != null)
                                    musicTitle = data.getStringExtra("musicTitle");
                                if (data.getStringExtra("musicArtists") != null)
                                    musicArtists = data.getStringExtra("musicArtists");
                            }
                            selectedLocationLabel.setText(name);
                            detailedLocationLabel.setText(address);
                            musicTitleLabel.setText(musicTitle);
                            musicArtistLabel.setText(musicArtists);
                        }
                    }
                });

        String[] credentials = getCredentials();
        savedUsername = credentials[0];
        savedPassword = credentials[1];
    }

    @Override
    public void finish() {
        super.finish();
        mediaPlayer.release();
        overridePendingTransition(R.anim.none, R.anim.vertical_exit);
    }

    public void bindComponents() {
        searchMusicButton = (ImageButton) findViewById(R.id.searchMusicButton);
        searchLocationButton = (ImageButton) findViewById(R.id.searchLocationButton);
        selectedLocationLabel = (TextView) findViewById(R.id.selectedLocationLabel);
        detailedLocationLabel = (TextView) findViewById(R.id.detailedLocationLabel);
        backButton = (ImageButton) findViewById(R.id.backButton);
        saveButton = (Button) findViewById(R.id.saveButton);
        titleTextBox = (EditText) findViewById(R.id.titleTextBox);
        postContentTextBox = (EditText) findViewById(R.id.postContentTextBox);
        musicPlayButton = (ImageButton) findViewById(R.id.musicPlayButton);
        musicTitleLabel = (TextView) findViewById(R.id.musicTitleLabel);
        musicArtistLabel = (TextView) findViewById(R.id.musicArtistLabel);
    }

    View.OnClickListener searchMusicListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getApplicationContext(), MusicSearchActivity.class);
            intent.putExtra("source", "post");
            searchActivityResultLauncher.launch(intent);
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
                    musicPlayButton.setImageResource(R.drawable.play);
                } else {
                    mediaPlayer.reset();
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
        }
    };

    View.OnClickListener searchLocationListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getApplicationContext(), LocationSearchActivity.class);
            intent.putExtra("source", "post");
            searchActivityResultLauncher.launch(intent);
        }
    };

    View.OnClickListener savePostListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            String username = "timmy";
            String password = "timmy";
            String base = username + ":" + password;
            String authHeader = "Basic " + Base64.encodeToString(base.getBytes(), Base64.NO_WRAP);
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://music-post-backend-14235148.df.r.appspot.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            PostPostAPI postPostAPI = retrofit.create(PostPostAPI.class);
            PostRequestModel postRequestModel = new PostRequestModel(new UserDto(username), titleTextBox.getText().toString(), postContentTextBox.getText().toString(), new MusicDto(musicArtists, musicTitle, musicURL), new Point(longitude, latitude), name, address);
            Call<PostResponseModel> call = postPostAPI.postPost(authHeader, postRequestModel);

            call.enqueue(new Callback<PostResponseModel>() {
                @Override
                public void onResponse(Call<PostResponseModel> call, Response<PostResponseModel> response) {
                    System.out.println(response.code());
                }

                @Override
                public void onFailure(Call<PostResponseModel> call, Throwable t) {
                    t.printStackTrace();
                }
            });
            PostActivity.super.onBackPressed();
        }
    };

    public void setEventListeners() {
        searchMusicButton.setOnClickListener(searchMusicListener);
        searchLocationButton.setOnClickListener(searchLocationListener);
        backButton.setOnClickListener(backListener);
        musicPlayButton.setOnClickListener(musicPlay);
        saveButton.setOnClickListener(savePostListener);
    }

    private String[] getCredentials() {
        SharedPreferences sharedPref = getSharedPreferences("Credentials", Context.MODE_PRIVATE);
        String username = sharedPref.getString("username", "");
        String password = sharedPref.getString("password", "");
        return new String[]{username, password};
    }

}
