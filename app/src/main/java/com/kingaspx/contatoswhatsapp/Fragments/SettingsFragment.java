package com.kingaspx.contatoswhatsapp.Fragments;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.kingaspx.contatoswhatsapp.HomeActivity;
import com.kingaspx.contatoswhatsapp.LoginActivity;
import com.kingaspx.contatoswhatsapp.R;
import com.kingaspx.contatoswhatsapp.Util.Functions;

import static android.content.Context.MODE_PRIVATE;

public class SettingsFragment extends Fragment {
    private Button saveBtn;

    private SharedPreferences sharedPreferences;
    private EditText edtIpBase, edtIpBaseSocket;
    private Functions functions;
    private HomeActivity mainActivity;
    private TextView logoutBtn;

    public SettingsFragment() {
    }

    public SettingsFragment(HomeActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedPreferences = getActivity().getSharedPreferences("kingaspx-contacts", MODE_PRIVATE);
        functions = new Functions(sharedPreferences);

        edtIpBase = view.findViewById(R.id.ip_base);
        edtIpBaseSocket = view.findViewById(R.id.ip_base_socketio);

        edtIpBase.setText(functions.baseUrl());
        edtIpBaseSocket.setText(functions.baseUrlSocketIo());

        logoutBtn = view.findViewById(R.id.logout_btn);
        logoutBtn.setOnClickListener(a -> {
            SharedPreferences prefs = getActivity().getSharedPreferences("kingaspx-login", MODE_PRIVATE);
            SharedPreferences.Editor prefsEditor = prefs.edit();
            prefsEditor.clear();
            prefsEditor.putString("id-main", null);
            prefsEditor.commit();

            startActivity(new Intent(getActivity(), LoginActivity.class));
            getActivity().finish();
        });

        saveBtn = view.findViewById(R.id.save_btn);
        saveBtn.setOnClickListener(a -> {
            storeIpBase();
        });

    }

    private void storeIpBase() {
        SharedPreferences prefs = getActivity().getSharedPreferences("kingaspx-contacts", MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.clear();
        prefsEditor.putString("BASE_URL", edtIpBase.getText().toString());
        prefsEditor.putString("BASE_URL_SOCKET", edtIpBaseSocket.getText().toString());
        prefsEditor.commit();

        reloadApplication();
    }

    public void reloadApplication() {
        int mPendingIntentId = 123456;

        PendingIntent mPendingIntent = PendingIntent.getActivity(mainActivity, mPendingIntentId, new Intent(), PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);

        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
        System.exit(0);
    }

}