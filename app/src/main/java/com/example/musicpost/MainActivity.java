package com.example.musicpost;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    TextView locationLabel;
    RelativeLayout currentPostCard;
    RelativeLayout newPostCard;
    TextView titleLabel;
    TextView newTitleLabel;
    TextView shortContentLabel;
    TextView newShortContentLabel;
    Button musicPlayButton;
    Button newMusicPlayButton;
    TextView musicTitleLabel;
    TextView newMusicTitleLabel;
    TextView musicArtistLabel;
    TextView newMusicArtistLabel;
    TextView postLocationLabel;
    TextView newPostLocationLabel;
    Button addPostButton;

    View.OnClickListener postClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getApplicationContext(), PostActivity.class);
            intent.putExtra("source", "main");
            startActivity(intent);
            overridePendingTransition(R.anim.vertical_enter, R.anim.none);
        }
    };

    View.OnClickListener detailedClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getApplicationContext(), DetailedPostActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bindComponents(); // 화면에 있는 component 가져오기
        setEventListeners(); // 이벤트 리스너 설정
    }

    public void bindComponents() {
        locationLabel = (TextView)findViewById(R.id.locationLabel);
        currentPostCard = (RelativeLayout)findViewById(R.id.currentPostCard);
        titleLabel = (TextView)findViewById(R.id.titleLabel);
        shortContentLabel = (TextView)findViewById(R.id.shortContentLabel);
        musicPlayButton = (Button)findViewById(R.id.musicPlayButton);
        musicTitleLabel = (TextView)findViewById(R.id.musicTitleLabel);
        musicArtistLabel = (TextView)findViewById(R.id.musicArtistLabel);
        postLocationLabel = (TextView)findViewById(R.id.postLocationLabel);
        addPostButton = (Button)findViewById(R.id.addPostButton);
        newPostCard = (RelativeLayout)findViewById(R.id.newPostCard);
        newTitleLabel = (TextView)findViewById(R.id.newTitleLabel);
        newShortContentLabel = (TextView)findViewById(R.id.newShortContentLabel);
        newMusicPlayButton = (Button)findViewById(R.id.newMusicPlayButton);
        newMusicTitleLabel = (TextView)findViewById(R.id.newMusicTitleLabel);
        newMusicArtistLabel = (TextView)findViewById(R.id.newMusicArtistLabel);
        newPostLocationLabel = (TextView)findViewById(R.id.newPostLocationLabel);
    }

    public void setEventListeners() {
        addPostButton.setOnClickListener(postClickListener);
        currentPostCard.setOnClickListener(detailedClickListener);
        currentPostCard.setOnTouchListener(cardFlipListener);
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

                    if (!isDragging && Math.abs(deltaX) > Math.abs(deltaY)) {
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
    };

    private void startLeftCurlAnimation(View postcard) {
        postcard.setPivotX(0);
        postcard.setPivotY(0);
        // Create an ObjectAnimator to animate the page's curl
        ObjectAnimator animY = ObjectAnimator.ofFloat(postcard, "rotationY", 0, -90);
        ObjectAnimator animX = ObjectAnimator.ofFloat(postcard, "rotationX", 0, 60);
        AnimatorSet animSet = new AnimatorSet();
        animSet.playTogether(animX, animY);
        animSet.setDuration(650);

        animSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // remove the old view
                if (currentPostCard.getVisibility() == View.VISIBLE) {
                    currentPostCard.setVisibility(View.GONE);
                    currentPostCard.setRotationX(0);
                    currentPostCard.setRotationY(0);
                    setColor(newPostCard);
                    newPostCard.setVisibility(View.VISIBLE);
                    newPostCard.setOnClickListener(detailedClickListener);
                    newPostCard.setOnTouchListener(cardFlipListener);
                } else {
                    newPostCard.setVisibility(View.GONE);
                    newPostCard.setRotationX(0);
                    newPostCard.setRotationY(0);
                    setColor(currentPostCard);
                    currentPostCard.setVisibility(View.VISIBLE);
                    currentPostCard.setOnClickListener(detailedClickListener);
                    currentPostCard.setOnTouchListener(cardFlipListener);
                }
            }
        });

        // Start the animation
        animSet.start();

    }

    private void startRightCurlAnimation(View postcard) {
        postcard.setPivotX(postcard.getWidth());
        postcard.setPivotY(0);
        // Create an ObjectAnimator to animate the page's curl
        ObjectAnimator animY = ObjectAnimator.ofFloat(postcard, "rotationY", 0, 90);
        ObjectAnimator animX = ObjectAnimator.ofFloat(postcard, "rotationX", 0, 60);
        AnimatorSet animSet = new AnimatorSet();
        animSet.playTogether(animX, animY);
        animSet.setDuration(650);

        animSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // remove the old view
                if (currentPostCard.getVisibility() == View.VISIBLE) {
                    currentPostCard.setVisibility(View.GONE);
                    currentPostCard.setRotationX(0);
                    currentPostCard.setRotationY(0);
                    setColor(newPostCard);
                    newPostCard.setVisibility(View.VISIBLE);
                    newPostCard.setOnClickListener(detailedClickListener);
                    newPostCard.setOnTouchListener(cardFlipListener);
                } else {
                    newPostCard.setVisibility(View.GONE);
                    newPostCard.setRotationX(0);
                    newPostCard.setRotationY(0);
                    setColor(currentPostCard);
                    currentPostCard.setVisibility(View.VISIBLE);
                    currentPostCard.setOnClickListener(detailedClickListener);
                    currentPostCard.setOnTouchListener(cardFlipListener);
                }
            }
        });

        // Start the animation
        animSet.start();
    }

    private void setColor(RelativeLayout postcard) {
        Random random = new Random();
        int randomNumber = random.nextInt(3);
        if (randomNumber == 0) postcard.setBackgroundColor(ContextCompat.getColor(this, R.color.yellow));
        else if (randomNumber == 1) postcard.setBackgroundColor(ContextCompat.getColor(this, R.color.blue));
        else if (randomNumber == 2) postcard.setBackgroundColor(ContextCompat.getColor(this, R.color.green));
    }
}

