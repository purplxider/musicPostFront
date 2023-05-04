package com.example.musicpost;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PostActivity extends AppCompatActivity {
    Button backButton;
    Button saveButton;
    EditText titleTextBox;
    EditText postContentTextBox;
    Button searchMusicButton;
    Button searchLocationButton;
    TextView selectedLocationLabel;
    TextView detailedLocationLabel;
    private String address = "";
    private String name = "위치를 설정해주세요";
    View.OnClickListener searchMusicListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getApplicationContext(), MusicSearchActivity.class);
            startActivity(intent);
        }
    };

    View.OnClickListener searchLocationListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getApplicationContext(), LocationSearchActivity.class);
            startActivity(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.vertical_enter, R.anim.none);
        setContentView(R.layout.activity_post);
        bindComponents();
        setEventListeners();

        Intent intent = getIntent();
        String address = intent.getStringExtra("address");
        String name = intent.getStringExtra("name");

        if (name == null) {
            if (address == null) name = "위치를 설정해주세요";
            name = address;
        }
        if (address == null) address = "";

        selectedLocationLabel.setText(name);
        detailedLocationLabel.setText(address);
    }

    public void bindComponents() {
        searchMusicButton = (Button)findViewById(R.id.searchMusicButton);
        searchLocationButton = (Button)findViewById(R.id.searchLocationButton);
        selectedLocationLabel = (TextView)findViewById(R.id.selectedLocationLabel);
        detailedLocationLabel = (TextView)findViewById(R.id.detailedLocationLabel);
        backButton = (Button)findViewById(R.id.backButton);
        saveButton = (Button)findViewById(R.id.saveButton);
        titleTextBox = (EditText)findViewById(R.id.titleTextBox);
        postContentTextBox = (EditText)findViewById(R.id.postContentTextBox);
    }

    public void setEventListeners() {
        searchMusicButton.setOnClickListener(searchMusicListener);
        searchLocationButton.setOnClickListener(searchLocationListener);
    }
}
