package com.example.musicpost;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    TextView locationLabel;
    LinearLayout currentPostCard;
    TextView titleLabel;
    TextView shortContentLabel;
    Button musicPlayButton;
    TextView musicTitleLabel;
    TextView musicArtistLabel;
    TextView postLocationLabel;
    Button addPostButton;

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

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
        currentPostCard = (LinearLayout)findViewById(R.id.currentPostCard);
        titleLabel = (TextView)findViewById(R.id.titleLabel);
        shortContentLabel = (TextView)findViewById(R.id.shortContentLabel);
        musicPlayButton = (Button)findViewById(R.id.musicPlayButton);
        musicTitleLabel = (TextView)findViewById(R.id.musicTitleLabel);
        musicArtistLabel = (TextView)findViewById(R.id.musicArtistLabel);
        postLocationLabel = (TextView)findViewById(R.id.postLocationLabel);
        addPostButton = (Button)findViewById(R.id.addPostButton);
    }

    public void setEventListeners() {
        currentPostCard.setOnClickListener(clickListener);
        titleLabel.setOnClickListener(clickListener);
        shortContentLabel.setOnClickListener(clickListener);
    }
}

