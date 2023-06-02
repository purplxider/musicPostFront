package com.example.musicpost;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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
    TextView likeLabel;
    Button toggleCommentButton;
    RecyclerView commentRecyclerView;
    CommentAdapter commentAdapter;
    EditText commentEditText;
    ImageButton postCommentButton;
    List<Comment> comments = new ArrayList<>();
    Boolean liked = false;
    private int postId = 0;

    private String title = "";
    private String location = "";
    private String description = "";
    private String poster = "";
    private String musicTitle = "";
    private String musicArtist = "";
    private int likeCount = 0;
    private String savedUsername = "";
    private String savedPassword = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_post);

        bindComponents(); // 화면에 있는 component 가져오기
        String[] credentials = getCredentials();
        savedUsername = credentials[0];
        savedPassword = credentials[1];
        mediaPlayer = new MediaPlayer();
        postId = getIntent().getIntExtra("postId", 0);
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
        likeCount = getIntent().getIntExtra("likeCount", 0);
        comments = getIntent().getParcelableArrayListExtra("comments");
        likeLabel.setText(String.valueOf(likeCount));
        commentRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        if (comments != null) {
            commentAdapter = new CommentAdapter(comments);
            commentRecyclerView.setAdapter(commentAdapter);
        }


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
        likeLabel = (TextView) findViewById(R.id.likeLabel);
        toggleCommentButton = (Button)findViewById(R.id.commentToggleButton);
        commentRecyclerView = (RecyclerView) findViewById(R.id.commentRecyclerView);
        commentEditText = (EditText) findViewById(R.id.commentEditText);
        postCommentButton = (ImageButton) findViewById(R.id.postCommentButton);
    }

    public void setEventListeners() {
        backButton.setOnClickListener(backListener);
        likeButton.setOnClickListener(likeListener);
        toggleCommentButton.setOnClickListener(toggleCommentListener);
        postCommentButton.setOnClickListener(postCommentListener);
    }

    View.OnClickListener backListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mediaPlayer.release();
            DetailedPostActivity.super.onBackPressed();
        }
    };

    View.OnClickListener toggleCommentListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(commentRecyclerView.getVisibility() == View.VISIBLE) {
                commentRecyclerView.setVisibility(View.GONE);
                commentEditText.setVisibility(View.GONE);
                postCommentButton.setVisibility(View.GONE);
                toggleCommentButton.setText("댓글 펼치기");
            } else {
                if(comments != null) {
                    commentRecyclerView.setVisibility(View.VISIBLE);
                }
                commentEditText.setVisibility(View.VISIBLE);
                postCommentButton.setVisibility(View.VISIBLE);
                getComments();
                toggleCommentButton.setText("댓글 접기");
            }
        }
    };

    public void getComments() {
        String username = savedUsername;
        String password = savedPassword;
        String base = username + ":" + password;
        String authHeader = "Basic " + Base64.encodeToString(base.getBytes(), Base64.NO_WRAP);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://ec2-52-91-17-50.compute-1.amazonaws.com:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        GetCommentAPI getCommentAPI = retrofit.create(GetCommentAPI.class);
        Call<List<CommentDto>> call = getCommentAPI.getComments(authHeader, postId);

        call.enqueue(new Callback<List<CommentDto>>(){

            @Override
            public void onResponse(Call<List<CommentDto>> call, Response<List<CommentDto>> response) {
                if(response.body() != null && !response.body().isEmpty()){
                    for (CommentDto commentDto : response.body()) {
                        Comment comment = new Comment(commentDto.getCommenter().getUsername(), commentDto.getCommentText());
                        comments.add(comment);
                    }
                    commentAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<CommentDto>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    View.OnClickListener postCommentListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            UserDto commentUser = new UserDto(savedUsername);
            String commentText = commentEditText.getText().toString();

            String username = savedUsername;
            String password = savedPassword;
            String base = username + ":" + password;
            String authHeader = "Basic " + Base64.encodeToString(base.getBytes(), Base64.NO_WRAP);

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://ec2-52-91-17-50.compute-1.amazonaws.com:8080/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            PostCommentAPI postCommentAPI = retrofit.create(PostCommentAPI.class);
            CommentDto commentDto = new CommentDto(null, postId, commentUser, commentText);
            Call<PostResponseModel> call = postCommentAPI.commentPost(authHeader, commentDto);
            System.out.println(commentDto);

            call.enqueue(new Callback<PostResponseModel>() {
                @Override
                public void onResponse(Call<PostResponseModel> call, Response<PostResponseModel> response) {
                    System.out.println("here " + response.code());
                }

                @Override
                public void onFailure(Call<PostResponseModel> call, Throwable t) {
                    t.printStackTrace();
                }
            });

            if(comments == null) {
                commentAdapter = new CommentAdapter(comments);
                commentRecyclerView.setAdapter(commentAdapter);
            }
            Comment comment = new Comment(commentUser.getUsername(), commentText);
            commentEditText.setText("");
            getComments();
        }
    };

    View.OnClickListener likeListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (liked == false) {
                likeButton.setImageResource(R.drawable.favorite_fill);
                likeCount++;
                likeLabel.setText(String.valueOf(likeCount));
                liked = true;
            } else {
                likeButton.setImageResource(R.drawable.favorite);
                likeCount--;
                likeLabel.setText(String.valueOf(likeCount));
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

    private String[] getCredentials() {
        SharedPreferences sharedPref = getSharedPreferences("Credentials", Context.MODE_PRIVATE);
        String username = sharedPref.getString("username", "");
        String password = sharedPref.getString("password", "");
        return new String[]{username, password};
    }
}