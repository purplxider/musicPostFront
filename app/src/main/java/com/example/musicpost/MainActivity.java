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

public class MainActivity extends AppCompatActivity {
    TextView locationLabel;
    RelativeLayout currentPostCard;
    RelativeLayout nextPostCard;
    TextView titleLabel;
    TextView shortContentLabel;
    Button musicPlayButton;
    TextView musicTitleLabel;
    TextView musicArtistLabel;
    TextView postLocationLabel;
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
            intent.putExtra("source", "main");
            startActivity(intent);
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
                        if (deltaX < 0) startLeftCurlAnimation();
                        else startRightCurlAnimation();
                        return true;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    // Reset the dragging flag and touch event coordinates
                    isDragging = false;
                    startX = startY = currentX = currentY = 0f;
                    break;
            }
            return false;
        }
    };

    private void startLeftCurlAnimation() {
        currentPostCard.setPivotX(0);
        currentPostCard.setPivotY(0);
        // Create an ObjectAnimator to animate the page's curl
        ObjectAnimator animY = ObjectAnimator.ofFloat(currentPostCard, "rotationY", 0, -90);
        ObjectAnimator animX = ObjectAnimator.ofFloat(currentPostCard, "rotationX", 0, 60);
        AnimatorSet animSet = new AnimatorSet();
        animSet.playTogether(animX, animY);
        animSet.setDuration(650);

        animSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // remove the old view
                RelativeLayout mainLayout = (RelativeLayout)findViewById(R.id.mainLayout);
                mainLayout.removeView(currentPostCard);
                RelativeLayout newPostCard = createNewCard();
                currentPostCard = newPostCard;
                mainLayout.addView(currentPostCard);
                currentPostCard.setOnTouchListener(cardFlipListener);
            }
        });

        // Start the animation
        animSet.start();

    }

    private void startRightCurlAnimation() {
        currentPostCard.setPivotX(currentPostCard.getWidth());
        currentPostCard.setPivotY(0);
        // Create an ObjectAnimator to animate the page's curl
        ObjectAnimator animY = ObjectAnimator.ofFloat(currentPostCard, "rotationY", 0, 90);
        ObjectAnimator animX = ObjectAnimator.ofFloat(currentPostCard, "rotationX", 0, 60);
        AnimatorSet animSet = new AnimatorSet();
        animSet.playTogether(animX, animY);
        animSet.setDuration(650);

        animSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // remove the old view
                RelativeLayout mainLayout = (RelativeLayout)findViewById(R.id.mainLayout);
                mainLayout.removeView(currentPostCard);
                RelativeLayout newPostCard = createNewCard();
                currentPostCard = newPostCard;
                mainLayout.addView(currentPostCard);
                currentPostCard.setOnTouchListener(cardFlipListener);
            }
        });

        // Start the animation
        animSet.start();
    }

    private RelativeLayout createNewCard() {
        RelativeLayout newCard = new RelativeLayout(this);
        return newCard;
    }
}

