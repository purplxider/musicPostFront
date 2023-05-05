package com.example.musicpost;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
    ActivityResultLauncher<Intent> searchActivityResultLauncher;
    private String address = "";
    private String name = "위치를 설정해주세요";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        bindComponents();
        setEventListeners();
        searchActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            String address = data.getStringExtra("address");
                            String name = data.getStringExtra("name");

                            if (name == null) {
                                if (address == null) name = "위치를 설정해주세요";
                                name = address;
                            }
                            if (address == null) address = "";

                            selectedLocationLabel.setText(name);
                            detailedLocationLabel.setText(address);
                        }
                    }
                });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.none, R.anim.vertical_exit);
    }

    public void bindComponents() {
        searchMusicButton = (Button) findViewById(R.id.searchMusicButton);
        searchLocationButton = (Button) findViewById(R.id.searchLocationButton);
        selectedLocationLabel = (TextView) findViewById(R.id.selectedLocationLabel);
        detailedLocationLabel = (TextView) findViewById(R.id.detailedLocationLabel);
        backButton = (Button) findViewById(R.id.backButton);
        saveButton = (Button) findViewById(R.id.saveButton);
        titleTextBox = (EditText) findViewById(R.id.titleTextBox);
        postContentTextBox = (EditText) findViewById(R.id.postContentTextBox);
    }

    View.OnClickListener searchMusicListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getApplicationContext(), MusicSearchActivity.class);
            intent.putExtra("source", "post");
            startActivity(intent);
            overridePendingTransition(R.anim.horizontal_enter, R.anim.horizontal_exit);
        }
    };

    View.OnClickListener backListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            PostActivity.super.onBackPressed();
        }
    };

    View.OnClickListener searchLocationListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getApplicationContext(), LocationSearchActivity.class);
            intent.putExtra("source", "post");
            startActivity(intent);
            searchActivityResultLauncher.launch(intent);
            overridePendingTransition(R.anim.horizontal_enter, R.anim.none);
        }
    };

    public void setEventListeners() {
        searchMusicButton.setOnClickListener(searchMusicListener);
        searchLocationButton.setOnClickListener(searchLocationListener);
        backButton.setOnClickListener(backListener);
    }

}
