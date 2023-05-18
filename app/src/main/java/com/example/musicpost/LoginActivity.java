package com.example.musicpost;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.GsonBuilder;

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
    CheckBox saveID;
    private String savedUsername = "";
    private String savedPassword = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        bindComponents();
        setEventListeners();

        String[] credentials = getCredentials();
         savedUsername = credentials[0];
         savedPassword = credentials[1];
         if (savedUsername != "") {
             idField.setText(savedUsername);
             passwordField.setText(savedPassword);
             saveID.setChecked(true);
             //loginButton.callOnClick();
         }
    }

    View.OnClickListener loginListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://music-post-backend-14235148.df.r.appspot.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            LoginAPI loginAPI = retrofit.create(LoginAPI.class);
            String username = idField.getText().toString();
            String password = passwordField.getText().toString();
            SignUpRequestModel loginRequestModel = new SignUpRequestModel(username, password);
            Call<LoginResponseModel> call = loginAPI.login(loginRequestModel);

            call.enqueue(new Callback<LoginResponseModel>() {
                @Override
                public void onResponse(Call<LoginResponseModel> call, Response<LoginResponseModel> response) {
                    if(response.code() == 200) {
                        saveCredentials(idField.getText().toString(), passwordField.getText().toString());
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.horizontal_enter, R.anim.none);
                    } else {
                        System.out.println(response.code());
                    }
                }

                @Override
                public void onFailure(Call<LoginResponseModel> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        }
    };

    View.OnClickListener signupListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://music-post-backend-14235148.df.r.appspot.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            SignUpAPI signUpAPI = retrofit.create(SignUpAPI.class);
            SignUpRequestModel signUpRequestModel = new SignUpRequestModel(idField.getText().toString(), passwordField.getText().toString());
            Call<PostResponseModel> call = signUpAPI.signUp(signUpRequestModel);

            call.enqueue(new Callback<PostResponseModel>() {
                @Override
                public void onResponse(Call<PostResponseModel> call, Response<PostResponseModel> response) {
                    if(response.code() == 200) {
                        if(saveID.isChecked()) {
                            saveCredentials(idField.getText().toString(), passwordField.getText().toString());
                        }
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.horizontal_enter, R.anim.none);
                        Toast.makeText(LoginActivity.this, "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        System.out.println(response.code());
                    }
                }

                @Override
                public void onFailure(Call<PostResponseModel> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        }
    };

    private void saveCredentials(String username, String password) {
        SharedPreferences sharedPref = getSharedPreferences("Credentials", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("username", username);
        editor.putString("password", password);
        editor.apply();
    }

    private String[] getCredentials() {
        SharedPreferences sharedPref = getSharedPreferences("Credentials", Context.MODE_PRIVATE);
        String username = sharedPref.getString("username", "");
        String password = sharedPref.getString("password", "");
        return new String[]{username, password};
    }

    private void bindComponents() {
        idField = (EditText) findViewById(R.id.idField);
        passwordField = (EditText) findViewById(R.id.passwordField);
        loginButton = (Button) findViewById(R.id.loginButton);
        signupButton = (Button) findViewById(R.id.signupButton);
        saveID = (CheckBox) findViewById(R.id.saveID);
    }

    private void setEventListeners() {
        loginButton.setOnClickListener(loginListener);
        signupButton.setOnClickListener(signupListener);
    }
}
