package com.kingaspx.contatoswhatsapp.Util;

import android.content.SharedPreferences;

public class Functions {
    private SharedPreferences sharedPreferences;

    public Functions(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public String baseUrl() {
        return sharedPreferences.getString("BASE_URL", "http://192.168.15.46:4747/api/");
    }

    public String baseUrlSocketIo() {
        return sharedPreferences.getString("BASE_URL_SOCKET", "http://192.168.15.46:4747/");
    }

}
