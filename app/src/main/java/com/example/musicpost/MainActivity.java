package com.example.musicpost;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    TextView titleLabel;
    TextView shortContentLabel;
    TextView currentLocationLabel;
    Button addPostButton;
    Button settingsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        titleLabel = (TextView) findViewById(R.id.titleLabel);
        shortContentLabel = (TextView) findViewById(R.id.shortContentLabel);
        currentLocationLabel = (TextView) findViewById(R.id.currentLocationLabel);
        addPostButton = (Button) findViewById(R.id.addPostButton);
        settingsButton = (Button) findViewById(R.id.settingsButton);

        titleLabel.setText("Title");
        shortContentLabel.setText("short content");
        currentLocationLabel.setText("현재 위치");
        addPostButton.setText("+");
        settingsButton.setText("Settings");
    }
}