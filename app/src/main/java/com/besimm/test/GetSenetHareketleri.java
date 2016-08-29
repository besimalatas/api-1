package com.besimm.test;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
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

public class GetSenetHareketleri extends AppCompatActivity {

    int sayac = 0;
    ProgressDialog progressDialog;
    List<String> sonuclar;
    ArrayAdapter adapter;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_senet_hareketleri);

        sonuclar = new ArrayList<String>();
        listView = (ListView) findViewById(R.id.testList);
        Intent bilgiler = getIntent();
        String ilkTarih = bilgiler.getStringExtra("ilkTarih");
        String ikinciTarih = bilgiler.getStringExtra("ikinciTarih");

        VeriGetir(ilkTarih, ikinciTarih);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            startActivity(new Intent(this, MainActivity.class));
        }
        return true;
    }

    public void VeriGetir(final String ilkTarih, final String ikinciTarih) {


        showProgressDialog("", "bekleyiniz");

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://192.168.1.140:120/api/Hareketler/GetHareketler/?type=json";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {


            @Override
            public void onResponse(JSONArray response) {

                try {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);

                    Date date1 = simpleDateFormat.parse(ilkTarih);
                    Date date2 = simpleDateFormat.parse(ikinciTarih);

                    for (int j = 0; j < 8000; j++) {

                        for (int i = 0; i < response.length(); i++) {
                            JSONObject bilgiler = (JSONObject) response.get(i);
                            int id = Integer.parseInt(bilgiler.getString("ID"));
                            String tarih = bilgiler.getString("KesideTarihi");
                            String[] netTarih = tarih.split("-");
                            String[] netTarih2 = netTarih[2].split("T");
                            Date date = simpleDateFormat.parse(netTarih[0] + netTarih[1] + netTarih2[0]);


                            if ((date.after(date1) || date.equals(date1)) && (date.before(date2) || date.equals(date2))) {
                                sonuclar.add(String.valueOf(id) + "-" + String.valueOf(tarih) + "-" + "-" + String.valueOf(tarih) + "-" + "-" + String.valueOf(tarih) + "-"
                                        + "-" + String.valueOf(tarih) + "-" + "-" + String.valueOf(tarih) + "-" + "-" + String.valueOf(tarih) + "-" + "-" + String.valueOf(tarih) + "bitti-");
                            }


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

                Log.e("hata", error.toString());

            }

        }) {

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

        };

      /*  jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                */
        queue.add(jsonArrayRequest);


    }

    private void showProgressDialog(String title, String message) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(title); //title
        progressDialog.setMessage(message); // message
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public void EkranaBas() {

        adapter = new ArrayAdapter(GetSenetHareketleri.this, R.layout.custom_text_view, sonuclar);
        try {
            listView.setAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
