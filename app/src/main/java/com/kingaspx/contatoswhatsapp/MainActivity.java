package com.kingaspx.contatoswhatsapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Intent intent;

        sharedPreferences = getSharedPreferences("kingaspx-login", MODE_PRIVATE);
        String id = sharedPreferences.getString("id-main", null);
        if (id != null) {
            intent = new Intent(MainActivity.this, HomeActivity.class);
            intent.putExtra("id", id);
        } else {
            intent = new Intent(MainActivity.this, WelcomeActivity.class);
        }

        startActivity(intent);
        finish();
    }

}