package com.example.musicpost;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
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
    Button locationChangeButton;
    private String musicURL = "";
    private Boolean touchEnabled = true;
    private Boolean clickEnabled = true;
    private String color = "yellow";
    private List<PostDto> posts;
    private String title = "";
    private String location = "";
    private String description = "";
    private String poster = "";
    private String musicTitle = "";
    private String musicArtist = "";
    private String savedUsername = "";
    private String savedPassword = "";
    private Double longitude = 0.0;
    private Double latitude = 0.0;
    private int likeCount = 0;


    View.OnClickListener postClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getApplicationContext(), PostActivity.class);
            intent.putExtra("source", "main");
            //mediaPlayer.stop();
            startActivity(intent);
            musicPlayButton.setImageResource(R.drawable.play);
            newMusicPlayButton.setImageResource(R.drawable.play);
            overridePendingTransition(R.anim.vertical_enter, R.anim.none);
        }
    };

    View.OnClickListener detailedClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (clickEnabled) {
                Intent intent = new Intent(getApplicationContext(), DetailedPostActivity.class);
                intent.putExtra("title", title);
                intent.putExtra("location", location);
                intent.putExtra("description", description);
                intent.putExtra("poster", poster);
                intent.putExtra("color", color);
                intent.putExtra("musicURL", musicURL);
                intent.putExtra("musicTitle", musicTitle);
                intent.putExtra("musicArtist", musicArtist);
                intent.putExtra("likeCount", likeCount);
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
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        getLocation();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                longitude = location.getLongitude();
                latitude = location.getLatitude();
            }
        };

        posts = new ArrayList<>();

        bindComponents(); // 화면에 있는 component 가져오기
        cropBackgroundToDevice();
        setEventListeners(); // 이벤트 리스너 설정

        String[] credentials = getCredentials();
        savedUsername = credentials[0];
        savedPassword = credentials[1];

        PostDto firstPost = new PostDto(1, new UserDto("yesiamok"), "과제하다가 같이 잠시 쉬어요", "과제를 하다가 잠시 쉴 때, 저는 이 음악을 들으면 스트레스가 풀리더라고요. 잠깐 스트레스 받는 과제를 내려두고 쉬었다가 다시 시작해요. 다 잘 마무리 할 수 있을 거예요", 5, new MusicDto("Claude Debussy, Alexis Weissenberg", "Claire de lune", "https://p.scdn.co/mp3-preview/b10ad4af310158240448e5a63985f0ef8a0deca1?cid=48ec963edf6147b49c54370210e3b278"), new PointDto(126.95785760879518, 37.50360217972531), new ArrayList<CommentDto>(), "서울특별시 동작구 흑석로 24", "중앙대학교 공과대학", new ArrayList<CommentDto>());
        PostDto secondPost = new PostDto(1, new UserDto("carbabyis"), "축제 기간 함께 즐겨요", "축제 기간동안 310관 바깥에 엄청 큰 푸앙이 인형보셨나요? 같이 사진 찍으면 엄청 잘 나와요! 여러분도 꼭 같이 찍어보세요~", 3, new MusicDto("NewJeans", "Hype Boy", "https://p.scdn.co/mp3-preview/7c55950057fc446dc2ce59671dff4fa6b3ef52a7?cid=48ec963edf6147b49c54370210e3b278"), new PointDto(126.95785760879518, 37.50360217972531), new ArrayList<CommentDto>(), "서울특별시 동작구 흑석로 84", "중앙대학교 서울캠퍼스 중앙마당", new ArrayList<CommentDto>());
        PostDto thirdPost = new PostDto(1, new UserDto("lim_chanhe"), "점심 산책을 즐길 때 어울리는 음악", "학교에서 점심시간에 산책하며 매일 듣는 노래입니다. 상쾌한 분위기와 함께 산책 도중에 듣기 좋은 멜로디와 가사가 있어서 좋아해요. 마음을 편안히 쉬고, 자연과 함께하는 시간을 같이 즐겨보아요~", 7, new MusicDto("The Beatles", "Here Comes the Sun", "https://p.scdn.co/mp3-preview/433dd3e00a82d231b060e2c7ab10f29249bf7942?cid=48ec963edf6147b49c54370210e3b278"), new PointDto(126.95785760879518, 37.50360217972531), new ArrayList<CommentDto>(), "서울특별시 동작구 흑석로 17", "중앙대학교 서울캠퍼스 후문", new ArrayList<CommentDto>());
        PostDto fourthPost = new PostDto(1, new UserDto("ppung.3.8"), "혼자여도 안 심심한 혼밥시간~", "The Lazy Song은 제가 제일 좋아하는 Bruno Mars 노래입니다. 혼자 밥을 먹으며 마음의 휴식을 취하고 싶을 때 저는 편안하고 부담없는 이 노래를 들어요", 2, new MusicDto("Bruno Mars", "The Lazy Song", "https://p.scdn.co/mp3-preview/8a6cd6679fc2a388b989a09b571194723d45cb71?cid=48ec963edf6147b49c54370210e3b278"), new PointDto(126.95785760879518, 37.50360217972531), new ArrayList<CommentDto>(), "서울특별시 동작구 흑석동 211-46", "고구동산", new ArrayList<CommentDto>());
        posts.add(firstPost);
        //posts.add(thirdPost);
        //posts.add(fourthPost);
        //posts.add(secondPost);
        setPost(0);
    }

    private String[] getCredentials() {
        SharedPreferences sharedPref = getSharedPreferences("Credentials", Context.MODE_PRIVATE);
        String username = sharedPref.getString("username", "");
        String password = sharedPref.getString("password", "");
        return new String[]{username, password};
    }

    public void bindComponents() {
        backgroundImage = (ImageView)findViewById(R.id.backgroundImage);
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
        locationChangeButton = (Button) findViewById(R.id.locationChangeButton);
    }

    public void cropBackgroundToDevice() {
        backgroundImage.setImageResource(R.drawable.mainview);
        backgroundImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
    }

    public void setEventListeners() {
        addPostButton.setOnClickListener(postClickListener);
        currentPostCard.setOnClickListener(detailedClickListener);
        currentPostCard.setOnTouchListener(cardFlipListener);
        musicPlayButton.setOnClickListener(musicPlay);
        newMusicPlayButton.setOnClickListener(musicPlay);
        locationChangeButton.setOnClickListener(locationChangeListener);
    }

    View.OnClickListener locationChangeListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            locationManager.removeUpdates(locationListener);
            longitude = 126.93537476435486;
            latitude = 37.52643919175901;
            posts.clear();
            getPosts();
            PostDto newPost1 = new PostDto(1, new UserDto("yesiamok"), "한강에서 여유롭게 친구들과 함께", "날씨가 좋아 이번주는 한강에 나왔습니다. 좋은 날씨와 편안한 분위기 덕분에 한강에서 즐거운 시간을 보냈습니다. 여러분도 함께 틀어두고 노래에 맞춰 즐거운 추억을 만들어보세요", 11, new MusicDto("The Weeknd", "Blinding Lights", "https://p.scdn.co/mp3-preview/1cce9eecdd36913ba1a15f27523550599ba995c3?cid=48ec963edf6147b49c54370210e3b278"), new PointDto(126.93537476435486, 37.52643919175901), new ArrayList<CommentDto>(), "서울 영등포구 여의동로 330", "여의도 한강공원", new ArrayList<CommentDto>());
            PostDto newPost4 = new PostDto(1, new UserDto("yesiamok"), "한강!", "정말 멋져요!", 8, new MusicDto("Quinn XCII", "Georgia Peach", "https://p.scdn.co/mp3-preview/b6cd84b94a981db0ab7512cf70a7fff94a2db943?cid=48ec963edf6147b49c54370210e3b278"), new PointDto(126.93537476435486, 37.52643919175901), new ArrayList<CommentDto>(), "서울 영등포구 여의동로 330", "여의도 한강공원", new ArrayList<CommentDto>());
            PostDto newPost2 = new PostDto(1, new UserDto("carbabyis"), "유람선 다녀감", "The Way You Look Tonight의 사랑스러운 가사와 멜로디를 들으면서 한강 유람선을 탔어요", 1, new MusicDto("Frank Sinatra", "The Way You Look Tonight", "https://p.scdn.co/mp3-preview/70d0f573438d33e0742480411e8af8f867a71afb?cid=48ec963edf6147b49c54370210e3b278"), new PointDto(126.93537476435486, 37.52643919175901), new ArrayList<CommentDto>(), "서울 영등포구 여의동로 338 한강아라호 선착장", "한강유람선 아라호", new ArrayList<CommentDto>());
            PostDto newPost3 = new PostDto(1, new UserDto("fitbathatba"), "야시장의 분위기를 한껏 뜨겁게", "어젯밤에 야시장에 다녀왔어요. 이 노래에 맞춰 다 같이 춤을 추던 어젯밤을 한동안 잊지 못할 것 같아요", 5, new MusicDto("ABBA", "Dancing Queen", "https://p.scdn.co/mp3-preview/1116076e3d1538852d6605ada1fd7130c8fc75a5?cid=48ec963edf6147b49c54370210e3b278"), new PointDto(126.93537476435486, 37.52643919175901), new ArrayList<CommentDto>(), "서울 영등포구 여의도동 84-9", "서울밤도깨비야시장", new ArrayList<CommentDto>());
            posts.add(newPost1);
            posts.add(newPost4);
            posts.add(newPost2);
            posts.add(newPost3);
            if(mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) mediaPlayer.release();
            }
            if(currentPostCard.getVisibility() == View.VISIBLE) setPost(0);
            else setPost(1);
        }
    };

    void playMusic() {
        //musicURL = "https://p.scdn.co/mp3-preview/c703198293891e3b276800ea6b187cf7951d3d7d?cid=48ec963edf6147b49c54370210e3b278"; // TODO: must remove!!
        mediaPlayer = new MediaPlayer();
        musicPlayButton.setImageResource(R.drawable.stop);
        newMusicPlayButton.setImageResource(R.drawable.stop);
        if (musicURL != "") {
            try {
                mediaPlayer.setDataSource(musicURL);
                mediaPlayer.setLooping(true);
                mediaPlayer.prepare();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    if (currentPostCard.getVisibility() == View.VISIBLE) musicPlayButton.setImageResource(R.drawable.play);
                    else newMusicPlayButton.setImageResource(R.drawable.play);
                }
            });
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

                        if (Math.abs(deltaX) > Math.abs(deltaY) && Math.abs(deltaX) > 10) {
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

                        if (distanceX < clickThreshold && distanceY < clickThreshold) {
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
                    if(mediaPlayer != null) mediaPlayer.release();
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
                    if(mediaPlayer != null) mediaPlayer.release();
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
                    if(mediaPlayer != null) mediaPlayer.release();
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
                    if(mediaPlayer != null) mediaPlayer.release();
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
                longitude = lastKnownLocation.getLongitude();
                latitude = lastKnownLocation.getLatitude();
                getPosts();
            } else {
                lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (lastKnownLocation != null) {
                    longitude = lastKnownLocation.getLongitude();
                    latitude = lastKnownLocation.getLatitude();
                    getPosts();
                }
            }
        }
    }

    private void getPosts() {
        String username = savedUsername;
        String password = savedPassword;
        String base = username + ":" + password;
        String authHeader = "Basic " + Base64.encodeToString(base.getBytes(), Base64.NO_WRAP);
        System.out.println(authHeader);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://music-post-backend-14235148.df.r.appspot.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        GetPostAPI getPostAPI = retrofit.create(GetPostAPI.class);
        Call<List<PostDto>> call = getPostAPI.getPosts(authHeader, 0, 10, longitude, latitude, 99999999); // TODO: 페이지 카운트 늘어나도록 변경해야함
        call.enqueue(new Callback<List<PostDto>>() {
            @Override
            public void onResponse(Call<List<PostDto>> call, Response<List<PostDto>> response) {
                if(response.body() != null && !response.body().isEmpty()){
                    for (PostDto post : response.body()) {
                        posts.add(post);
                        System.out.println("here: " + post);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<PostDto>> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(MainActivity.this, "주변에 포스트가 없습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setPost(int i) {
        if (posts.size() == 0) {
            clickEnabled = false;
            titleLabel.setText("모든 포스트를 읽었습니다");
            shortContentLabel.setText(":(");
            musicTitleLabel.setText("");
            musicArtistLabel.setText("");
            currentLocationLabel.setText("");
            currentAddressLabel.setText("");
            musicURL = "";
            title = "";
            description = "";
            location = "";
            poster = "";
            musicArtist = "";
            musicTitle = "";
            likeCount = 0;
        } else {
            clickEnabled = true;
            PostDto currentPost = posts.get(0);
            posts.remove(0);
            if (i == 0) {
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

            currentLocationLabel.setText(currentPost.getLocationName());
            currentAddressLabel.setText(currentPost.getAddress());
            musicURL = currentPost.getMusic().getMusicUrl();

            likeCount = currentPost.getLikeCount();
            title = currentPost.getTitle();
            description = currentPost.getDescription();
            location = currentPost.getLocationName();
            poster = currentPost.getOriginalPoster().getUsername();
            musicArtist = currentPost.getMusic().getArtist();
            musicTitle = currentPost.getMusic().getSongName();
        }
        /*
        if (mediaPlayer != null) {
            if(mediaPlayer.isLooping()) mediaPlayer.release();
            else playMusic();
        }  else playMusic();
         */

        if(posts.size() < 3) {
            getPosts();
        }
    }
}

