package com.kingaspx.contatoswhatsapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kingaspx.contatoswhatsapp.Adapter.TabAdapter;
import com.kingaspx.contatoswhatsapp.Fragments.ContatosFragment;
import com.kingaspx.contatoswhatsapp.Fragments.SettingsFragment;
import com.kingaspx.contatoswhatsapp.Model.User;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private List<User> userList;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TabAdapter adapter;
    private TextView userText, idText;

    private Context context;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference usersRef = database.getReference("users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        context = this;
        userList = new ArrayList<>();

        userText = findViewById(R.id.text_username);
        idText = findViewById(R.id.text_id);

        getUserInfo();

        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);

        adapter = new TabAdapter(getSupportFragmentManager());
        adapter.addFragment(new ContatosFragment(this), "Contatos");
        adapter.addFragment(new SettingsFragment(this), "Configuração");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        MobileAds.initialize(this, (initializationStatus) -> {
        });

        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId("ca-app-pub-1508037510426777/7874007352");
        adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    private void getUserInfo() {
        Intent intent = getIntent();

        String id = intent.getStringExtra("id");
        String username = intent.getStringExtra("username");

        if (username != null) {
            idText.setText(id);
            userText.setText("@" + username);
        } else {
            usersRef.orderByChild("id").equalTo(id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User user = snapshot.getValue(User.class);

                        idText.setText(user.getId());
                        userText.setText("@" + user.getUsername());
                    }
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void storeDataUser(String id) {
        SharedPreferences prefs = getSharedPreferences("kingaspx-login", MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.clear();
        prefsEditor.putString("id-main", id);
        prefsEditor.commit();
    }

}