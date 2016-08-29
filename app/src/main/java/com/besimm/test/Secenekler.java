package com.besimm.test;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;


public class Secenekler extends AppCompatActivity implements View.OnClickListener {

    private Button secim1, secim2, secim3;
    private Button altSecim3_1, altSecim3_2;
    private Button secim4;
    private Button altSecim4_1, altSecim4_2, altSecim4_3;
    private Button secim5, secim6, secim7;
    private Button altSecim7_1, altSecim7_2, altSecim7_3;

    private int tSecim3 = 0, tSecim4 = 0, tSecim7 = 0;


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secenekler);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        secim1 = (Button) findViewById(R.id.secim1);
        secim2 = (Button) findViewById(R.id.secim2);
        secim3 = (Button) findViewById(R.id.secim3);
        altSecim3_1 = (Button) findViewById(R.id.altSecim3_1);
        altSecim3_2 = (Button) findViewById(R.id.altSecim3_2);
        secim4 = (Button) findViewById(R.id.secim4);
        altSecim4_1 = (Button) findViewById(R.id.altSecim4_1);
        altSecim4_2 = (Button) findViewById(R.id.altSecim4_2);
        altSecim4_3 = (Button) findViewById(R.id.altSecim4_3);
        secim5 = (Button) findViewById(R.id.secim5);
        secim6 = (Button) findViewById(R.id.secim6);
        secim7 = (Button) findViewById(R.id.secim7);
        altSecim7_1 = (Button) findViewById(R.id.altSecim7_1);
        altSecim7_2 = (Button) findViewById(R.id.altSecim7_2);
        altSecim7_3 = (Button) findViewById(R.id.altSecim7_3);

        secim1.setOnClickListener(this);
        secim2.setOnClickListener(this);
        secim3.setOnClickListener(this);
        altSecim3_1.setOnClickListener(this);
        altSecim3_2.setOnClickListener(this);
        secim4.setOnClickListener(this);
        secim7.setOnClickListener(this);

        altSecim3_1.setVisibility(View.GONE);
        altSecim3_2.setVisibility(View.GONE);

        altSecim4_1.setVisibility(View.GONE);
        altSecim4_2.setVisibility(View.GONE);
        altSecim4_3.setVisibility(View.GONE);
        altSecim7_1.setVisibility(View.GONE);
        altSecim7_2.setVisibility(View.GONE);
        altSecim7_3.setVisibility(View.GONE);


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.secim1: {
                Intent ıntent = new Intent(Secenekler.this, MainActivity.class);
                startActivity(ıntent);

                break;
            }
            case R.id.secim2: {
                // İlgili intent
                break;
            }
            case R.id.secim3: {
                tSecim3++;

                if (tSecim3 % 2 != 0) {

                    altSecim3_1.setVisibility(View.VISIBLE);
                    altSecim3_2.setVisibility(View.VISIBLE);
                } else {
                    altSecim3_2.setVisibility(View.GONE);
                    altSecim3_1.setVisibility(View.GONE);
                }
                break;
            }
            case R.id.altSecim3_1: {
                Intent ıntent = new Intent(Secenekler.this, MainActivity.class);
                startActivity(ıntent);
                break;
            }
            case R.id.altSecim3_2: {
                Intent ıntent = new Intent(Secenekler.this, MainActivity.class);
                startActivity(ıntent);
                break;
            }
            case R.id.secim4: {
                tSecim4++;
                if (tSecim4 % 2 != 0) {

                    altSecim4_1.setVisibility(View.VISIBLE);
                    altSecim4_2.setVisibility(View.VISIBLE);
                    altSecim4_3.setVisibility(View.VISIBLE);
                } else {
                    altSecim4_1.setVisibility(View.GONE);
                    altSecim4_2.setVisibility(View.GONE);
                    altSecim4_3.setVisibility(View.GONE);
                }
                break;
            }


            case R.id.secim7: {
                tSecim7++;
                if (tSecim7 % 2 != 0) {
                    altSecim7_1.setVisibility(View.VISIBLE);
                    altSecim7_2.setVisibility(View.VISIBLE);
                    altSecim7_3.setVisibility(View.VISIBLE);
                } else {
                    altSecim7_1.setVisibility(View.GONE);
                    altSecim7_2.setVisibility(View.GONE);
                    altSecim7_3.setVisibility(View.GONE);
                }
                break;
            }


        }

    }
}


