package com.besimm.test;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.os.StrictMode;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.appdatasearch.GetRecentContextCall;

import org.apache.http.impl.entity.EntityDeserializer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.FileNameMap;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by besimm on 29.8.2016.
 */
@SuppressWarnings("ResourceType")
public class GridViewTest extends AppCompatActivity {

    GridView gridView, gridView1;
    ProgressDialog progressDialog;
    String[] basliklar = {"Unvan", "Vade", "Ödenen", "Kalan"};
    List<String> sonuclar = new ArrayList<>();


    Display display;
    Point size = new Point();
    int widht = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.grid_view);
        display = getWindowManager().getDefaultDisplay();
        display.getSize(size);
        widht = size.x;

        gridView = (GridView) findViewById(R.id.gridView);
        gridView1 = (GridView) findViewById(R.id.gridView2);

        Intent bilgiler = getIntent();
        String ilkTarih = bilgiler.getStringExtra("ilkTarih");
        String ikinciTarih = bilgiler.getStringExtra("ikinciTarih");

        VeriGetir(ilkTarih, ikinciTarih);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            Intent ıntent = new Intent(this, Secenekler.class);
            NavUtils.navigateUpTo(this, ıntent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void VeriGetir(final String ilkTarih, final String ikinciTarih) {
        String url = "http://192.168.1.140:120/api/OgrTaksitHareketleri/GetTaksitHareketleri/?type=json";

        showProgressDialog("", "bekleyiniz");


        final RequestQueue queue = Volley.newRequestQueue(this);

        final JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                try {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);

                    Date date1 = simpleDateFormat.parse(ilkTarih);
                    Date date2 = simpleDateFormat.parse(ikinciTarih);
                    for (int i = 0; i < response.length(); i++) {


                        JSONObject bilgiler = (JSONObject) response.get(i);

                        String unvan = bilgiler.getString("Unvan");
                        String borc = bilgiler.getString("TaksitTutari");
                        String[] vade = bilgiler.getString("Vade").split("T");
                        String odenen = bilgiler.getString("Odenen");
                        String kalan = bilgiler.getString("Kalan");

                        String tarih = bilgiler.getString("YeniVade");
                        String[] netTarih = tarih.split("-");
                        String[] netTarih2 = netTarih[2].split("T");
                        Date date = simpleDateFormat.parse(netTarih[0] + netTarih[1] + netTarih2[0]);
                        if ((date.after(date1) || date.equals(date1)) && (date.before(date2) || date.equals(date2))) {
                            sonuclar.add(unvan);
                            sonuclar.add(vade[0]);
                            sonuclar.add(borc);
                            sonuclar.add(odenen);
                            sonuclar.add(kalan);
                        }

                    }

                    EkranaBas2();

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                error.printStackTrace();

            }
        }) {

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

        };
        queue.add(jsonArrayRequest);
    }

    private void EkranaBas() {

        GridViewTest.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        gridView.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, basliklar) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                TextView tv1, tv2, tv3, tv4;
                LinearLayout linearLayout;

                if (convertView == null) {
                    tv1 = new TextView(getContext());
                    tv1.setLayoutParams(new LinearLayout.LayoutParams(700, 700, 0.5f));
                    tv2 = new TextView(getContext());
                    tv2.setLayoutParams(new LinearLayout.LayoutParams(700, 700, 0.3f));
                    tv3 = new TextView(getContext());
                    tv3.setLayoutParams(new LinearLayout.LayoutParams(700, 700, 0.1f));
                    tv4 = new TextView(getContext());
                    tv4.setLayoutParams(new LinearLayout.LayoutParams(700, 700, 0.1f));

                    linearLayout = new LinearLayout(getContext());
                    linearLayout.setOrientation(LinearLayout.HORIZONTAL);


                    tv1.setText("unvan");
                    tv2.setText("vade");
                    tv3.setText("odenen");
                    tv4.setText("kalan");

                    linearLayout.addView(tv1);
                    linearLayout.addView(tv2);
                    linearLayout.addView(tv3);
                    linearLayout.addView(tv4);


                } else {
                    linearLayout = (LinearLayout) convertView;
                    tv1 = (TextView) linearLayout.getChildAt(0);
                    tv2 = (TextView) linearLayout.getChildAt(1);
                    tv3 = (TextView) linearLayout.getChildAt(2);
                    tv4 = (TextView) linearLayout.getChildAt(3);


                }
                return linearLayout;
            }
        });
        gridView1.setAdapter(new ArrayAdapter<String>(GridViewTest.this, android.R.layout.simple_list_item_1, sonuclar) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                View view = super.getView(position, convertView, parent);
                TextView text = (TextView) view.findViewById(android.R.id.text1);
                text.setTextColor(Color.parseColor("#E14036"));
                return view;
            }
        });


    }


    private void EkranaBas2() {

        GridViewTest.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);        //Ekranın yatay olarak durmasını saglayan paremetre

        float toplamAlacak = 0.0f;                                                                   //**
        float toplamAlınan = 0.0f;
        float toplamkalan = 0.0f;                                                                    // Verilerin sonuclari

        LinearLayout enDis = new LinearLayout(this);                                                 // Parent View
        LinearLayout ustBilgi = new LinearLayout(this);                                              // Ust bilgiler(unvani,vadesi,borcu..)
        ScrollView scrollView = new ScrollView(this);
        RelativeLayout icContext = new RelativeLayout(this);                                         // Verilerin tutulacagi dis context
        LinearLayout tumVeriler = new LinearLayout(this);                                            // verileri alt alta listelemek icin liner layout
        LinearLayout altVeriler;                                                                     // veriler icin horizontal layout

        scrollView.setLayoutParams(new ViewGroup.LayoutParams(                                       //
                ViewGroup.LayoutParams.MATCH_PARENT,                                                 // **
                ViewGroup.LayoutParams.MATCH_PARENT));                                               // ScrolView 'e height ve widht özellikleri


        ViewGroup.LayoutParams paramsTumEkran = new LinearLayout.LayoutParams(                       // Ekran yerlesimi
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        ViewGroup.LayoutParams yatayParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(                           // veriler icin genislik tanımlanması(TextView'ler icin)
                widht / 3,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        params1.weight = 3.0f;
        params1.setMargins(3, 0, 0, 0);
        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(                           // veriler icin genislik tanımlanması(TextView'ler icin)
                widht / 6, LinearLayout.LayoutParams.WRAP_CONTENT);
        params2.weight = 2.0f;
        params2.setMargins(0, 4, 0, 0);
        LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams                            // veriler icin genislik tanımlanması(TextView'ler icin)
                (widht / 10,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
        params3.weight = 1.0f;

        enDis.setOrientation(LinearLayout.VERTICAL);                                                 // dikey oriantation verdik
        enDis.setBackgroundColor(Color.parseColor("#212121"));                                       // renk atamasi
        enDis.setLayoutParams(paramsTumEkran);                                                       // ekran ozelligini ekledik

        ustBilgi.setWeightSum(10);                                                                   // toplam agirligi 10 olarak belirledik
        ustBilgi.setOrientation(LinearLayout.HORIZONTAL);
        ustBilgi.setLayoutParams(yatayParams);                                                       // ustBilgi layoutu icin params özelligi atadık
        ustBilgi.setBackgroundColor(Color.parseColor("#212121"));                                    // Renk atamasi
        ustBilgi.setPadding(4, 4, 2, 0);                                                             // Padding (kenarlik)

        icContext.setLayoutParams(paramsTumEkran);                                                   // Params atamasi
        icContext.setBackgroundColor(Color.parseColor("#212121"));                                   // Renk atamasi

        tumVeriler.setOrientation(LinearLayout.VERTICAL);                                            // Verileri koyacagimiz dis context dikey olarak ayarlaniyor
        tumVeriler.setLayoutParams(yatayParams);                                                     // Params atamasi
        tumVeriler.setBackgroundColor(Color.parseColor("#212121"));                                  // Renk atamasi


        TextView unvan, sinif, vade, alacak, odenen, kalan;                                          // Ust bilgiler icin textview'lar
        unvan = new TextView(GridViewTest.this);                                                     // **
        sinif = new TextView(GridViewTest.this);                                                     // **
        vade = new TextView(GridViewTest.this);                                                      // **
        alacak = new TextView(GridViewTest.this);                                                    // **
        odenen = new TextView(GridViewTest.this);                                                    // **
        kalan = new TextView(GridViewTest.this);                                                     // Arayuz nesnelerinin olusturulmasi
        unvan.setTextColor(Color.parseColor("#E14036"));                                             // **
        sinif.setTextColor(Color.parseColor("#E14036"));                                             // **
        vade.setTextColor(Color.parseColor("#E14036"));                                              // **
        alacak.setTextColor(Color.parseColor("#E14036"));                                            // **
        odenen.setTextColor(Color.parseColor("#E14036"));                                            // **
        kalan.setTextColor(Color.parseColor("#E14036"));                                             //  textview'lara renk atamasi

        unvan.setLayoutParams(params1);                                                              // yatay ekranın 3/10' u
        sinif.setLayoutParams(params2);                                                              // yatay ekranın 2/10'u
        vade.setLayoutParams(params2);                                                               // yatay ekranın 2/10'u
        alacak.setLayoutParams(params2);
        odenen.setLayoutParams(params2);
        kalan.setLayoutParams(params3);                                                              // ekranın 1/10'u yerlesim atanmasi
        unvan.setText("Unvanı");
        sinif.setText("Sınıfı");
        vade.setText("Vadesi");
        alacak.setText("Alacak");
        odenen.setText("Ödenen");
        kalan.setText("Kalan");

        unvan.setTextSize(24);
        sinif.setTextSize(24);
        vade.setTextSize(24);
        alacak.setTextSize(24);
        odenen.setTextSize(24);
        kalan.setTextSize(24);

        unvan.setGravity(Gravity.CENTER);
        sinif.setGravity(Gravity.CENTER);
        vade.setGravity(Gravity.CENTER);
        alacak.setGravity(Gravity.CENTER);
        odenen.setGravity(Gravity.CENTER);
        kalan.setGravity(Gravity.RIGHT);

        ustBilgi.addView(unvan);                                                                      // **
        ustBilgi.addView(sinif);                                                                      // **
        ustBilgi.addView(vade);                                                                       // **
        ustBilgi.addView(alacak);                                                                     // **
        ustBilgi.addView(odenen);                                                                     // **
        ustBilgi.addView(kalan);                                                                      // layout'a textviewlarin eklenmesi


        TextView unvanVeri, sinifVeri, vadeVeri, alacakVeri, odenenVeri, kalanVeri;                   // Sonuclarin goruntulenecegi textview'lar

        enDis.addView(ustBilgi);                                                                      // ustBilgi layout'u enDis layoutuna ekleniyor
        scrollView.addView(icContext);                                                                // scrollView'e ic context ekleniyor(verilerin olacagi context)
        icContext.addView(tumVeriler);                                                                // icContext'e veriler layoutu ekleniyor

        for (int i = 0; i <= sonuclar.size(); i += 5) {

            unvanVeri = new TextView(GridViewTest.this);                                              // **
            sinifVeri = new TextView(GridViewTest.this);                                              // **
            vadeVeri = new TextView(GridViewTest.this);                                               // **
            alacakVeri = new TextView(GridViewTest.this);                                             // **
            odenenVeri = new TextView(GridViewTest.this);                                             // **
            kalanVeri = new TextView(GridViewTest.this);                                              // Arayuz nesnelerinin olusturulmasi
            unvanVeri.setTextColor(Color.parseColor("#E14036"));                                      // **
            sinifVeri.setTextColor(Color.parseColor("#E14036"));                                      // **
            vadeVeri.setTextColor(Color.parseColor("#E14036"));                                       // **
            alacakVeri.setTextColor(Color.parseColor("#E14036"));                                     // **
            odenenVeri.setTextColor(Color.parseColor("#E14036"));                                     // **
            kalanVeri.setTextColor(Color.parseColor("#E14036"));                                      // Arayuz elemanlarina renk atamasi
            altVeriler = new LinearLayout(this);                                                      // Verilerin eklenmesi icin layout tanimlanmasi
            altVeriler.setOrientation(LinearLayout.HORIZONTAL);                                       // orientation'u yatay belirledik( veri1-veri2-veri3-veri4-veri5)
            altVeriler.setLayoutParams(yatayParams);                                                  // layouta params ozelligi eklenmesi
            altVeriler.setWeightSum(10);                                                              // layoutun toplam agirligi 10 olarak belirlendi
            altVeriler.setPadding(4, 4, 0, 0);                                                        // kenarlik belirlenmesi

            altVeriler.addView(unvanVeri);                                                            // **
            altVeriler.addView(sinifVeri);                                                            // **
            altVeriler.addView(vadeVeri);                                                             // **
            altVeriler.addView(alacakVeri);                                                           // **
            altVeriler.addView(odenenVeri);                                                           // **
            altVeriler.addView(kalanVeri);                                                            // veriler layouta ekleniyor

            unvanVeri.setLayoutParams(params1);                                                       // yatay ekranin 3/10' u
            sinifVeri.setLayoutParams(params2);                                                       // yatay ekranin 2/10' u
            vadeVeri.setLayoutParams(params2);                                                        // **
            alacakVeri.setLayoutParams(params2);                                                      // **
            odenenVeri.setLayoutParams(params2);                                                      // **
            kalanVeri.setLayoutParams(params3);                                                       // yatay ekranin 1/10' u

            unvanVeri.setTextSize(16);                                                                // yazi boyutu
            sinifVeri.setTextSize(18);                                                                // **
            vadeVeri.setTextSize(18);                                                                 // **
            alacakVeri.setTextSize(18);                                                               // **
            odenenVeri.setTextSize(18);                                                               // **
            kalanVeri.setTextSize(18);                                                                // **

            unvanVeri.setGravity(Gravity.LEFT);                                                       // **
            sinifVeri.setGravity(Gravity.CENTER);                                                     // **
            vadeVeri.setGravity(Gravity.LEFT);                                                        // **
            alacakVeri.setGravity(Gravity.CENTER);                                                    // **
            odenenVeri.setGravity(Gravity.CENTER);                                                    // **
            kalanVeri.setGravity(Gravity.CENTER);                                                     // yazinin textview icindekiyerinin belirlenmesi

            if (i != sonuclar.size()) {                                                               // sona gelene dek veriler ekrana basilacak


                unvanVeri.setText(sonuclar.get(i));
                sinifVeri.setText("x sınıf");
                vadeVeri.setText(sonuclar.get(i + 1));
                alacakVeri.setText(sonuclar.get(i + 2));
                odenenVeri.setText(sonuclar.get(i + 3));
                kalanVeri.setText(sonuclar.get(i + 4));

                toplamAlacak += Float.parseFloat(sonuclar.get(i + 2));
                toplamAlınan += Float.parseFloat(sonuclar.get(i + 3));
                toplamkalan += Float.parseFloat(sonuclar.get(i + 4));

            } else {                                                                                  // sona geldiyse toplam verilerin hesaplanmasi

                unvanVeri.setText("        ");
                sinifVeri.setText("        ");
                vadeVeri.setText("Genel Toplam :");
                alacakVeri.setText(String.valueOf(toplamAlacak));
                odenenVeri.setText(String.valueOf(toplamAlınan));
                kalanVeri.setText(String.valueOf(toplamkalan));
            }
            tumVeriler.addView(altVeriler);                                                           // tumVerilre sonuclar ekleniyor
            tumVeriler.setPadding(6, 0, 0, 0);

        }


        enDis.addView(scrollView);                                                                    //Endisa'a scrollView ekleniyor
        setContentView(enDis);                                                                        // enDis layouta set ediliyor
        if (progressDialog != null && progressDialog.isShowing()) {                                   //progresdialog sonlandırılıyor
            progressDialog.dismiss();
        }
    }

    private void showProgressDialog(String title, String message) {
        progressDialog = new ProgressDialog(this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setTitle(title); //title
        progressDialog.setMessage(message); // message
        progressDialog.setCancelable(false);
        progressDialog.show();
    }


}

