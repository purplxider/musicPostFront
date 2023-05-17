package com.example.musicpost;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {
    EditText idField;
    EditText passwordField;
    Button loginButton;
    Button signupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        bindComponents();
        setEventListeners();
    }

    View.OnClickListener loginListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://music_post_backend")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            LoginAPI loginAPI = retrofit.create(LoginAPI.class);
            String username = idField.getText().toString();
            String password = passwordField.getText().toString();
            String base = username + ":" + password;
            String authHeader = "Basic " + Base64.encodeToString(base.getBytes(), Base64.NO_WRAP);
            Call<PostResponseModel> call = loginAPI.login(authHeader);

            call.enqueue(new Callback<PostResponseModel>() {
                @Override
                public void onResponse(Call<PostResponseModel> call, Response<PostResponseModel> response) {
                    if(response.body().getStatusCode() == 200) {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.horizontal_enter, R.anim.none);
                    } else {
                        System.out.println(response.body().getMessage() + response.body().getStatusCode());
                    }
                }

                @Override
                public void onFailure(Call<PostResponseModel> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        }
    };

    View.OnClickListener signupListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://music_post_backend")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            SignUpAPI signUpAPI = retrofit.create(SignUpAPI.class);
            SignUpRequestModel signUpRequestModel = new SignUpRequestModel(idField.getText().toString(), passwordField.getText().toString());
            Call<PostResponseModel> call = signUpAPI.signUp(signUpRequestModel);

            call.enqueue(new Callback<PostResponseModel>() {
                @Override
                public void onResponse(Call<PostResponseModel> call, Response<PostResponseModel> response) {
                    if(response.body().getStatusCode() == 200) {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.horizontal_enter, R.anim.none);
                    } else {
                        System.out.println(response.body().getMessage() + response.body().getStatusCode());
                    }
                }

                @Override
                public void onFailure(Call<PostResponseModel> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        }
    };

    private void bindComponents() {
        idField = (EditText) findViewById(R.id.idField);
        passwordField = (EditText) findViewById(R.id.passwordField);
        loginButton = (Button) findViewById(R.id.loginButton);
        signupButton = (Button) findViewById(R.id.signupButton);
    }

    private void setEventListeners() {
        loginButton.setOnClickListener(loginListener);
        signupButton.setOnClickListener(signupListener);
    }
}
