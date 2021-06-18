package com.kingaspx.contatoswhatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText editText, passText;
    private Button registerBtn;
    private TextView loginBtn;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference usersRef = database.getReference("users");
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        loginBtn = findViewById(R.id.login_btn);
        registerBtn = findViewById(R.id.btn_register);

        editText = findViewById(R.id.edt_username);
        passText = findViewById(R.id.edt_password);

        loadingDialog = new LoadingDialog(RegisterActivity.this);

        registerBtn.setOnClickListener(v -> {
            if (editText.getText().toString().equals("") || passText.getText().toString().equals("")) {
                Toast.makeText(RegisterActivity.this, "Preencha todos os campos.", Toast.LENGTH_SHORT).show();
            } else {
                checkUser();
            }
        });

        loginBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void checkUser() {
        loadingDialog.startLoadingDialog();

        usersRef.orderByChild("username").equalTo(editText.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    System.out.println(editText.getText().toString());
                    Toast.makeText(RegisterActivity.this, "User already exists.", Toast.LENGTH_SHORT).show();
                    loadingDialog.dismissDialog();
                } else {
                    registerUser();
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError databaseError) {

            }
        });
    }

    private void registerUser() {
        mAuth.signInAnonymously().addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = mAuth.getCurrentUser();
                insertUserInfo(user.getUid());
            } else {
                Toast.makeText(RegisterActivity.this, "Register failed.", Toast.LENGTH_SHORT).show();
                loadingDialog.dismissDialog();
            }
        });
    }

    private void insertUserInfo(String id) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("username", editText.getText().toString());
        map.put("password", passText.getText().toString());

        usersRef.child(id).updateChildren(map).addOnCompleteListener(task -> {
            storeDataUser(id);

            Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
            intent.putExtra("id", id);
            intent.putExtra("username", editText.getText().toString());
            startActivity(intent);
            finish();
            loadingDialog.dismissDialog();
        });
    }

    private void storeDataUser(String id) {
        SharedPreferences prefs = getSharedPreferences("kingaspx-login", MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.clear();
        prefsEditor.putString("id-main", id);
        prefsEditor.commit();
    }
}