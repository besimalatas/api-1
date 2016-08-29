package com.besimm.test;

import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class StartWifiSettings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_wifi_settings);


        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));

        finish();
    }
}
