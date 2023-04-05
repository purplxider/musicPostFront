package com.example.musicpost;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    LinearLayout currentPostCard;
    TextView titleLabel;
    TextView shortContentLabel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        currentPostCard = (LinearLayout)findViewById(R.id.currentPostCard);
        titleLabel = (TextView)findViewById(R.id.titleLabel);
        shortContentLabel = (TextView)findViewById(R.id.shortContentLabel);

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        };
        View.OnDragListener dragListener = new View.OnDragListener() {
            @Override
            public boolean onDrag(View view, DragEvent dragEvent) {
                System.out.println("abcdefg");
                titleLabel.setText("즐거운 봄 산책 되세요!");
                shortContentLabel.setText("해가 떠오르고 날씨가 좋을 때 듣기 좋은 곡입니다. 밝고 경쾌한 멜로디와 가사가 기분을 좋게해줍니다...더보기");
                return true;
            }
        };

        currentPostCard.setOnClickListener(clickListener);
        titleLabel.setOnClickListener(clickListener);
        shortContentLabel.setOnClickListener(clickListener);

        currentPostCard.setOnDragListener(dragListener);
        titleLabel.setOnDragListener(dragListener);
        shortContentLabel.setOnDragListener(dragListener);
    }
}