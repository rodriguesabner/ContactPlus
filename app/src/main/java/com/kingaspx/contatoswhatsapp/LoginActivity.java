package com.kingaspx.contatoswhatsapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kingaspx.contatoswhatsapp.Model.User;

import org.jetbrains.annotations.NotNull;

public class LoginActivity extends AppCompatActivity {

    private EditText editText, passText;
    private Button button;
    private TextView registerBtn, forgotBtn;
    private String username, password;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference usersRef = database.getReference("users");
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        button = findViewById(R.id.login_btn);

        editText = findViewById(R.id.edt_username);
        passText = findViewById(R.id.edt_password);
        forgotBtn = findViewById(R.id.btn_forgot);
        registerBtn = findViewById(R.id.btn_register);

        loadingDialog = new LoadingDialog(LoginActivity.this);

        button.setOnClickListener(v -> {
            if (editText.getText().toString().isEmpty() || passText.getText().toString().isEmpty()) {
                Toast.makeText(LoginActivity.this, "Preencha todos os campos.", Toast.LENGTH_SHORT).show();
            } else {
                loginUser();
            }
        });

        registerBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
            finish();
        });

    }

    private void loginUser() {
        loadingDialog.startLoadingDialog();
        String username = editText.getText().toString();
        String password = passText.getText().toString();

        usersRef.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User user = snapshot.getValue(User.class);
                        if (user.getUsername().equals(username)) {
                            if (user.getPassword().equals(password)) {
                                storeDataUser(user.getId());
                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                intent.putExtra("id", user.getId());
                                intent.putExtra("username", username);
                                loadingDialog.dismissDialog();
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this, "Invalid Password.", Toast.LENGTH_SHORT).show();
                                loadingDialog.dismissDialog();
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            loadingDialog.dismissDialog();
                        }
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "User does not exists.", Toast.LENGTH_SHORT).show();
                    loadingDialog.dismissDialog();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
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