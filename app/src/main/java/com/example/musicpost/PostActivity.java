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
import android.widget.Toast;

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

public class PostActivity extends AppCompatActivity implements ShakeDetector.OnShakeListener {
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
    Button changeLocationButton;
    Button pinnedPostButton;
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
    private String source = "";
    private ShakeDetector shakeDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        mediaPlayer = new MediaPlayer();
        shakeDetector = new ShakeDetector(this);
        shakeDetector.setOnShakeListener(this);
        bindComponents();
        setEventListeners();
        Intent intent = getIntent();
        String userLocation = intent.getStringExtra("userLocation");
        if (!userLocation.equals("")) {
            selectedLocationLabel.setText(userLocation);
        }
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
                            } else if (data.getStringExtra("source").equals("pinnedPost")) {
                                source = "pin";
                                address = data.getStringExtra("address");
                                name = data.getStringExtra("location");
                                musicURL = data.getStringExtra("musicURL");
                                musicTitle = data.getStringExtra("musicTitle");
                                musicArtists = data.getStringExtra("musicArtists");
                                longitude = data.getDoubleExtra("longitude", 0);
                                latitude = data.getDoubleExtra("latitude", 0);
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
    protected void onResume() {
        super.onResume();
        shakeDetector.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        shakeDetector.stop();
    }

    @Override
    public void finish() {
        super.finish();
        mediaPlayer.release();
        overridePendingTransition(R.anim.none, R.anim.vertical_exit);
    }

    @Override
    public void onShake() {
        if (musicURL.equals("")) {
            Toast.makeText(this, "음악을 설정해주세요", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedLocationLabel.getText().toString().equals("")) {
            Toast.makeText(this, "위치를 설정해주세요", Toast.LENGTH_SHORT).show();
            return;
        }
        if (source.equals("pin")) {
            return;
        }

        String username = savedUsername;
        String password = savedPassword;
        String base = username + ":" + password;
        String authHeader = "Basic " + Base64.encodeToString(base.getBytes(), Base64.NO_WRAP);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://ec2-52-91-17-50.compute-1.amazonaws.com:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        PostPinAPI postPinAPI = retrofit.create(PostPinAPI.class);
        PinRequestModel pinRequestModel = new PinRequestModel(new UserDto(savedUsername), new MusicDto(musicArtists, musicTitle, musicURL), detailedLocationLabel.getText().toString(), selectedLocationLabel.getText().toString(), new PointDto(longitude, latitude));
        Call<PostResponseModel> call = postPinAPI.pinPost(authHeader, pinRequestModel);

        call.enqueue(new Callback<PostResponseModel>() {
            @Override
            public void onResponse(Call<PostResponseModel> call, Response<PostResponseModel> response) {
                System.out.println("here " + response.code());
            }

            @Override
            public void onFailure(Call<PostResponseModel> call, Throwable t) {
                t.printStackTrace();
                return;
            }
        });
        Toast.makeText(this, "포스트가 임시저장 되었습니다", Toast.LENGTH_SHORT).show();
        PostActivity.super.onBackPressed();
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
        changeLocationButton = (Button) findViewById(R.id.changeLocationButton);
        pinnedPostButton = (Button) findViewById(R.id.pinnedPostButton);
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
            if (musicURL == "") {

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
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            musicPlayButton.setImageResource(R.drawable.play);
                        }
                    });
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

    View.OnClickListener pinnedPostListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getApplicationContext(), PinnedPostActivity.class);
            searchActivityResultLauncher.launch(intent);
        }
    };

    View.OnClickListener savePostListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {

            String username = savedUsername;
            String password = savedPassword;
            String base = username + ":" + password;
            String authHeader = "Basic " + Base64.encodeToString(base.getBytes(), Base64.NO_WRAP);
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://ec2-52-91-17-50.compute-1.amazonaws.com:8080/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            PostPostAPI postPostAPI = retrofit.create(PostPostAPI.class);
            PostRequestModel postRequestModel = new PostRequestModel(new UserDto(savedUsername), titleTextBox.getText().toString(), postContentTextBox.getText().toString(), new MusicDto(musicArtists, musicTitle, musicURL), new PointDto(longitude, latitude), selectedLocationLabel.getText().toString(), detailedLocationLabel.getText().toString());
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

    View.OnClickListener changeLocationListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            longitude = 126.93537476435486;
            latitude = 37.52643919175901;
            selectedLocationLabel.setText("여의도 한강공원");
            detailedLocationLabel.setText("서울 영등포구 여의동로 330");
        }
    };

    public void setEventListeners() {
        searchMusicButton.setOnClickListener(searchMusicListener);
        searchLocationButton.setOnClickListener(searchLocationListener);
        backButton.setOnClickListener(backListener);
        musicPlayButton.setOnClickListener(musicPlay);
        saveButton.setOnClickListener(savePostListener);
        changeLocationButton.setOnClickListener(changeLocationListener);
        pinnedPostButton.setOnClickListener(pinnedPostListener);
    }

    private String[] getCredentials() {
        SharedPreferences sharedPref = getSharedPreferences("Credentials", Context.MODE_PRIVATE);
        String username = sharedPref.getString("username", "");
        String password = sharedPref.getString("password", "");
        return new String[]{username, password};
    }

}
