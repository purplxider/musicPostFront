package com.example.musicpost;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapReverseGeoCoder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements MapReverseGeoCoder.ReverseGeoCodingResultListener {
    ImageView backgroundImage;
    TextView locationLabel;
    RelativeLayout currentPostCard;
    RelativeLayout newPostCard;
    TextView titleLabel;
    TextView newTitleLabel;
    TextView shortContentLabel;
    TextView newShortContentLabel;
    ImageButton musicPlayButton;
    ImageButton newMusicPlayButton;
    TextView musicTitleLabel;
    TextView newMusicTitleLabel;
    TextView musicArtistLabel;
    TextView newMusicArtistLabel;
    ImageButton addPostButton;
    TextView currentLocationLabel;
    TextView currentAddressLabel;
    LocationManager locationManager;
    LocationListener locationListener;
    MapReverseGeoCoder reverseGeoCoder;
    MediaPlayer mediaPlayer = null;
    private String musicURL = "";
    private Boolean touchEnabled = true;
    private String color = "yellow";
    private List<PostDto> posts;
    private String title = "";
    private String location = "";
    private String description = "";
    private String poster = "";
    private String musicTitle = "";
    private String musicArtist = "";


    View.OnClickListener postClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getApplicationContext(), PostActivity.class);
            intent.putExtra("source", "main");
            mediaPlayer.stop();
            startActivity(intent);
            musicPlayButton.setImageResource(R.drawable.play);
            newMusicPlayButton.setImageResource(R.drawable.play);
            overridePendingTransition(R.anim.vertical_enter, R.anim.none);
        }
    };

    View.OnClickListener detailedClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (touchEnabled) {
                Intent intent = new Intent(getApplicationContext(), DetailedPostActivity.class);
                intent.putExtra("title", title);
                intent.putExtra("location", location);
                intent.putExtra("description", description);
                intent.putExtra("poster", poster);
                intent.putExtra("color", color);
                intent.putExtra("musicURL", musicURL);
                intent.putExtra("musicTitle", musicTitle);
                intent.putExtra("musicArtist", musicArtist);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        }
    };

    View.OnClickListener musicPlay = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                if (currentPostCard.getVisibility() == View.VISIBLE) musicPlayButton.setImageResource(R.drawable.play);
                else newMusicPlayButton.setImageResource(R.drawable.play);
            } else {
                mediaPlayer.start();
                if (currentPostCard.getVisibility() == View.VISIBLE) musicPlayButton.setImageResource(R.drawable.stop);
                else newMusicPlayButton.setImageResource(R.drawable.stop);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        getLocation();
        posts = new ArrayList<>();
        //getPosts();
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                reverseGeoCoder = new MapReverseGeoCoder("76c2eeaa6f57d8057a0917641c853eb3", MapPoint.mapPointWithGeoCoord(location.getLatitude(), location.getLongitude()), MainActivity.this, MainActivity.this);
                reverseGeoCoder.startFindingAddress();
            }
        };

        bindComponents(); // 화면에 있는 component 가져오기
        cropBackgroundToDevice();
        setEventListeners(); // 이벤트 리스너 설정

        PostDto firstPost = new PostDto(1, new UserDto("yesiamok"), "마음을 편안하게 만드는 잠깐의 음악 여행", "안녕하세요! 과제를 하다가 잠시 쉴 때, 마음을 편안하게 만들어주는 음악을 소개해드리겠습니다. 저는 이 음악을 들으면 스트레스가 풀리더라고요. 잠시 동안 음악의 세계로 향해 함께 여행을 떠나볼까요?", 5, new MusicDto("Claude Debussy, Alexis Weissenberg", "Claire de lune", "https://p.scdn.co/mp3-preview/b10ad4af310158240448e5a63985f0ef8a0deca1?cid=48ec963edf6147b49c54370210e3b278"), new Point(126.95785760879518, 37.50360217972531), new ArrayList<CommentDto>(), "서울특별시 동작구 흑석로 84", "중앙대학교", new ArrayList<CommentDto>());
        posts.add(firstPost);
        setPost(0);
        playMusic();
    }

    public void bindComponents() {
        backgroundImage = (ImageView)findViewById(R.id.backgroundImage);
        locationLabel = (TextView)findViewById(R.id.locationLabel);
        currentPostCard = (RelativeLayout)findViewById(R.id.currentPostCard);
        titleLabel = (TextView)findViewById(R.id.titleLabel);
        currentLocationLabel = (TextView)findViewById(R.id.currentLocationLabel);
        currentAddressLabel = (TextView)findViewById(R.id.currentAddressLabel);
        shortContentLabel = (TextView)findViewById(R.id.shortContentLabel);
        musicPlayButton = (ImageButton)findViewById(R.id.musicPlayButton);
        musicTitleLabel = (TextView)findViewById(R.id.musicTitleLabel);
        musicArtistLabel = (TextView)findViewById(R.id.musicArtistLabel);
        addPostButton = (ImageButton)findViewById(R.id.addPostButton);
        newPostCard = (RelativeLayout)findViewById(R.id.newPostCard);
        newTitleLabel = (TextView)findViewById(R.id.newTitleLabel);
        newShortContentLabel = (TextView)findViewById(R.id.newShortContentLabel);
        newMusicPlayButton = (ImageButton)findViewById(R.id.newMusicPlayButton);
        newMusicTitleLabel = (TextView)findViewById(R.id.newMusicTitleLabel);
        newMusicArtistLabel = (TextView)findViewById(R.id.newMusicArtistLabel);
    }

    public void cropBackgroundToDevice() {
        backgroundImage.setImageResource(R.drawable.mainview1);
        backgroundImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
    }

    public void setEventListeners() {
        addPostButton.setOnClickListener(postClickListener);
        currentPostCard.setOnClickListener(detailedClickListener);
        currentPostCard.setOnTouchListener(cardFlipListener);
        musicPlayButton.setOnClickListener(musicPlay);
        newMusicPlayButton.setOnClickListener(musicPlay);
    }

    void playMusic() {
        //musicURL = "https://p.scdn.co/mp3-preview/c703198293891e3b276800ea6b187cf7951d3d7d?cid=48ec963edf6147b49c54370210e3b278"; // TODO: must remove!!
        mediaPlayer = new MediaPlayer();
        mediaPlayer.reset();
        musicPlayButton.setImageResource(R.drawable.stop);
        newMusicPlayButton.setImageResource(R.drawable.stop);
        if (musicURL != "") {
            try {
                mediaPlayer.setDataSource(musicURL);
                mediaPlayer.prepare();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            mediaPlayer.start();
        }
    }

    View.OnTouchListener cardFlipListener = new View.OnTouchListener() {
        private boolean isDragging = false;
        private float startX;
        private float startY;
        private float currentX;
        private float currentY;
        private final int clickThreshold = 10;
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (touchEnabled) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Save the start X and Y coordinates of the touch
                        startX = event.getX();
                        startY = event.getY();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        // Calculate the amount of drag in the X and Y directions
                        currentX = event.getX();
                        currentY = event.getY();
                        float deltaX = currentX - startX;
                        float deltaY = currentY - startY;

                        if (!isDragging && Math.abs(deltaX) > Math.abs(deltaY) && Math.abs(deltaX) > 10) {
                            // If the drag is mostly in the horizontal direction, start the curl animation
                            isDragging = true;
                            if (deltaX < 0) startLeftCurlAnimation(v);
                            else startRightCurlAnimation(v);
                            return true;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        // Check if the touch was a click
                        float endX = event.getX();
                        float endY = event.getY();
                        float distanceX = Math.abs(endX - startX);
                        float distanceY = Math.abs(endY - startY);

                        if (!isDragging && distanceX < clickThreshold && distanceY < clickThreshold) {
                            // Trigger a click event
                            v.performClick();
                            return true;
                        }
                        // Reset the dragging flag and touch event coordinates
                        isDragging = false;
                        startX = startY = 0f;
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        // Reset the dragging flag and touch event coordinates
                        isDragging = false;
                        startX = startY = currentX = currentY = 0f;
                        break;
                }
                return false;
            }
            return false;
        }
    };

    private void startLeftCurlAnimation(View postcard) {
        touchEnabled = false;
        postcard.setPivotX(0);
        postcard.setPivotY(0);
        View nextPostcard;
        if (currentPostCard.getVisibility() == View.VISIBLE) {
            nextPostcard = newPostCard;
            setPost(1);
        }
        else {
            nextPostcard = currentPostCard;
            setPost(0);
        }
        nextPostcard.setAlpha(0f);
        setColor((RelativeLayout) nextPostcard);
        nextPostcard.setVisibility(View.VISIBLE);
        ObjectAnimator animY = ObjectAnimator.ofFloat(postcard, "rotationY", 0, -90);
        ObjectAnimator animX = ObjectAnimator.ofFloat(postcard, "rotationX", 0, 60);
        ObjectAnimator moveX = ObjectAnimator.ofFloat(postcard, "translationX", -200);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(postcard, "alpha", 0f);
        ObjectAnimator alphaNext = ObjectAnimator.ofFloat(nextPostcard, "alpha", 1f);
        AnimatorSet animSet = new AnimatorSet();
        animSet.playTogether(animX, animY, moveX, alpha, alphaNext);
        animSet.setDuration(550);

        animSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // remove the old view
                if (nextPostcard == newPostCard) {
                    currentPostCard.setVisibility(View.GONE);
                    currentPostCard.setRotationX(0);
                    currentPostCard.setRotationY(0);
                    currentPostCard.setTranslationX(0);
                    currentPostCard.setAlpha(1f);
                    newPostCard.setOnTouchListener(cardFlipListener);
                    newPostCard.setOnClickListener(detailedClickListener);
                    touchEnabled = true;
                    mediaPlayer.release();
                    playMusic();
                } else {
                    newPostCard.setVisibility(View.GONE);
                    newPostCard.setRotationX(0);
                    newPostCard.setRotationY(0);
                    newPostCard.setTranslationX(0);
                    newPostCard.setAlpha(1f);
                    currentPostCard.setOnTouchListener(cardFlipListener);
                    currentPostCard.setOnClickListener(detailedClickListener);
                    touchEnabled = true;
                    mediaPlayer.release();
                    playMusic();
                }
            }
        });

        // Start the animation
        animSet.start();

    }

    private void startRightCurlAnimation(View postcard) {
        touchEnabled = false;
        postcard.setPivotX(postcard.getWidth());
        postcard.setPivotY(0);
        View nextPostcard;
        if (currentPostCard.getVisibility() == View.VISIBLE) {
            nextPostcard = newPostCard;
            setPost(1);
        }
        else {
            nextPostcard = currentPostCard;
            setPost(0);
        }
        nextPostcard.setAlpha(0f);
        setColor((RelativeLayout) nextPostcard);
        nextPostcard.setVisibility(View.VISIBLE);
        ObjectAnimator animY = ObjectAnimator.ofFloat(postcard, "rotationY", 0, 90);
        ObjectAnimator animX = ObjectAnimator.ofFloat(postcard, "rotationX", 0, 60);
        ObjectAnimator moveX = ObjectAnimator.ofFloat(postcard, "translationX", 200);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(postcard, "alpha", 0f);
        ObjectAnimator alphaNext = ObjectAnimator.ofFloat(nextPostcard, "alpha", 1f);
        AnimatorSet animSet = new AnimatorSet();
        animSet.playTogether(animX, animY, moveX, alpha, alphaNext);
        animSet.setDuration(550);

        animSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // remove the old view
                if (nextPostcard == newPostCard) {
                    currentPostCard.setVisibility(View.GONE);
                    currentPostCard.setRotationX(0);
                    currentPostCard.setRotationY(0);
                    currentPostCard.setTranslationX(0);
                    currentPostCard.setAlpha(1f);
                    newPostCard.setOnTouchListener(cardFlipListener);
                    newPostCard.setOnClickListener(detailedClickListener);
                    touchEnabled = true;
                    mediaPlayer.release();
                    playMusic();
                } else {
                    newPostCard.setVisibility(View.GONE);
                    newPostCard.setRotationX(0);
                    newPostCard.setRotationY(0);
                    newPostCard.setTranslationX(0);
                    newPostCard.setAlpha(1f);
                    currentPostCard.setOnTouchListener(cardFlipListener);
                    currentPostCard.setOnClickListener(detailedClickListener);
                    touchEnabled = true;
                    mediaPlayer.release();
                    playMusic();
                }
            }
        });

        // Start the animation
        animSet.start();
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

    @Override
    protected void onResume() {
        super.onResume();

        // Check for location updates
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30000, 0, locationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 30000, 0, locationListener);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Stop location updates
        locationManager.removeUpdates(locationListener);
    }

    @Override
    public void onReverseGeoCoderFoundAddress(MapReverseGeoCoder mapReverseGeoCoder, String s) {
        //currentLocationLabel.setText(s);
    }

    @Override
    public void onReverseGeoCoderFailedToFindAddress(MapReverseGeoCoder mapReverseGeoCoder) {

    }

    private void getLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastKnownLocation != null) {
                reverseGeoCoder = new MapReverseGeoCoder("76c2eeaa6f57d8057a0917641c853eb3", MapPoint.mapPointWithGeoCoord(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()), MainActivity.this, MainActivity.this);
                reverseGeoCoder.startFindingAddress();
            } else {
                lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (lastKnownLocation != null) {
                    reverseGeoCoder = new MapReverseGeoCoder("76c2eeaa6f57d8057a0917641c853eb3", MapPoint.mapPointWithGeoCoord(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()), MainActivity.this, MainActivity.this);
                    reverseGeoCoder.startFindingAddress();
                }
            }
        }
    }

    private void getPosts() {
        String username = "test";
        String password = "1234";
        String base = username + ":" + password;
        String authHeader = "Basic " + Base64.encodeToString(base.getBytes(), Base64.NO_WRAP);
        System.out.println(authHeader);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://music_post_backend")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        GetPostAPI getPostAPI = retrofit.create(GetPostAPI.class);
        Call<ResultGetPosts> call = getPostAPI.getPosts(authHeader, 0, 10); // TODO: 페이지 카운트 늘어나도록 변경해야함
        call.enqueue(new Callback<ResultGetPosts>() {
            @Override
            public void onResponse(Call<ResultGetPosts> call, Response<ResultGetPosts> response) {
                if(response.body() != null && response.body().getPosts() != null && !response.body().getPosts().isEmpty()){
                    for (PostDto post : response.body().getPosts()) {
                        posts.add(post);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResultGetPosts> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(MainActivity.this, "주변에 포스트가 없습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setPost(int i) {
        PostDto currentPost = posts.get(0);
        System.out.println(currentPost);
        //posts.remove(0);
        if(i == 0) {
            titleLabel.setText(currentPost.getTitle());
            shortContentLabel.setText(currentPost.getDescription());
            musicTitleLabel.setText(currentPost.getMusic().getSongName());
            musicArtistLabel.setText(currentPost.getMusic().getArtist());
        } else {
            newTitleLabel.setText(currentPost.getTitle());
            newShortContentLabel.setText(currentPost.getDescription());
            newMusicTitleLabel.setText(currentPost.getMusic().getSongName());
            newMusicArtistLabel.setText(currentPost.getMusic().getArtist());
        }

        currentLocationLabel.setText(currentPost.getLocation_name());
        currentAddressLabel.setText(currentPost.getAddress());
        musicURL = currentPost.getMusic().getMusic_url();

        title = currentPost.getTitle();
        description = currentPost.getDescription();
        location = currentPost.getLocation_name();
        poster = currentPost.getOriginalPoster().getUsername();
        musicArtist = currentPost.getMusic().getArtist();
        musicTitle = currentPost.getMusic().getSongName();
    }
}

