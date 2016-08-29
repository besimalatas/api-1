package com.besimm.test;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GetOgrTaksitHareketleri extends AppCompatActivity {

    ProgressDialog progressDialog;
    List<String> sonuclar;
    ListView listView;
    ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_ogr_taksit_hareketleri);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sonuclar = new ArrayList<>();
        listView = (ListView) findViewById(R.id.sonuclarListView);
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
                        String[] vade = bilgiler.getString("Vade").split("T");
                        String[] yeniVade = bilgiler.getString("YeniVade").split("T");
                        String odenen = bilgiler.getString("Odenen");
                        String kalan = bilgiler.getString("Kalan");

                        String tarih = bilgiler.getString("YeniVade");
                        String[] netTarih = tarih.split("-");
                        String[] netTarih2 = netTarih[2].split("T");
                        Date date = simpleDateFormat.parse(netTarih[0] + netTarih[1] + netTarih2[0]);
                        if ((date.after(date1) || date.equals(date1)) && (date.before(date2) || date.equals(date2))) {
                            sonuclar.add("Ünvan        : " + unvan + "\n" +
                                    "Vade          : " + vade[0] + "\n" + "Yeni Vade : " + yeniVade[0] + "\n" +
                                    "Ödenen     : " + odenen + " ₺" + "\n" + "Kalan         : " + kalan + " ₺" + "\n");
                        }

                    }
                    if (progressDialog != null && progressDialog.isShowing()) {   //progresdialog sonlandırılıyor
                        progressDialog.dismiss();
                        EkranaBas();
                    }
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

        adapter = new ArrayAdapter(GetOgrTaksitHareketleri.this, R.layout.custom_text_view, sonuclar);
        listView.setAdapter(adapter);

    }

    private void showProgressDialog(String title, String message) {
        progressDialog = new ProgressDialog(this,R.style.AppTheme_Dark_Dialog);
        progressDialog.setTitle(title); //title
        progressDialog.setMessage(message); // message
        progressDialog.setCancelable(false);
        progressDialog.show();
    }
}
