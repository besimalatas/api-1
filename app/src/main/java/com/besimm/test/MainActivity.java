package com.besimm.test;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.support.design.widget.Snackbar;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class MainActivity extends AppCompatActivity {

    ListView listView;

    Context mContext;


    String apiUrl = "http://androidapitest.somee.com/api/Hareketler/getName/?type=xml";
    String apiUrl2 = "http://androidapitest.somee.com/api/Hareketler/getName2/?type=xml";
    String apiUrl3 = "http://androidapitest.somee.com/api/Hareketler/GetSenetHareketleriDemo/?type=xml";
    Spinner spinner;

    BilgiGetir bilgiGetir = new BilgiGetir();

    private PopupWindow pwindo;                              //detayları görüntülemek için açılabilir pencere

    ArrayAdapter<String> adapter;
    ArrayAdapter<String> spinnerAdapter;

    // String apiUrl="http://www.tcmb.gov.tr/kurlar/today.xml";

    List<String> sonuclar = new ArrayList<String>();
    List<String> spinnerSonuclar = new ArrayList<String>();
    HttpURLConnection baglanti = null;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.listView);
        spinner = (Spinner) findViewById(R.id.spinner);

        Button baglan = (Button) findViewById(R.id.ınternetBaglan);

        if (!InternetBaglantiKontrol()) {

            baglan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                }
            });
        } else {


            //   final CoordinatorLayout bildiDetay = (CoordinatorLayout) findViewById(R.id.bilgiDetay);


            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                    Toast.makeText(MainActivity.this, sonuclar.get(position), Toast.LENGTH_SHORT).show();



/*
               Snackbar.make(bildiDetay, sonuclar.get(position), Snackbar.LENGTH_LONG)
                        .setAction(null, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        }).show();

*/

                    List<String> tümSonuclar = new ArrayList<String>();
                    String item = null;
                    try {
                        tümSonuclar = bilgiGetir.GetTumBilgiler();
                        item = tümSonuclar.get(position);
                    } catch (Exception e) {
                        e.getStackTrace();
                    }

                    String[] parts = item.split(",");
                    Toast.makeText(MainActivity.this, parts[0], Toast.LENGTH_SHORT).show();
                    PopUp(parts, mContext);


                }
            });


            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    Toast.makeText(MainActivity.this, spinnerSonuclar.get((int) id), Toast.LENGTH_SHORT).show();

                    switch (position) {

                        case (0): {

                            Toast.makeText(MainActivity.this, "Lütfen Seçim Yapınız...", Toast.LENGTH_SHORT).show();
                            break;
                        }
                        case (1): {
                            Toast.makeText(MainActivity.this, "1. Seçim", Toast.LENGTH_SHORT).show();
                            new GetInfos().execute(apiUrl, "ArayuzModel");
                            break;
                        }
                        case (2): {
                            Toast.makeText(MainActivity.this, "2. Seçim", Toast.LENGTH_SHORT).show();
                            new GetInfos().execute(apiUrl2, "ArayuzModel2");
                            break;
                        }
                        case (3): {
                            Toast.makeText(MainActivity.this, "Demo secim", Toast.LENGTH_SHORT).show();
                            new GetInfos().execute(apiUrl3, "SenetHareketleriDemo");
                            break;
                        }
                    }


                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                    return;

                }
            });

            spinnerSonuclar.add("Lütfen Seçim Yapınız");
            spinnerSonuclar.add("Seçim 1");
            spinnerSonuclar.add("Seçim 2");
            spinnerSonuclar.add("Demo secimi");
            spinnerSonuclar.add("Spinner2");
            spinnerSonuclar.add("Spinner3");
            spinnerSonuclar.add("Spinner1");
            spinnerSonuclar.add("Spinner2");


            spinnerAdapter = new ArrayAdapter<String>(MainActivity.this, R.layout.support_simple_spinner_dropdown_item, spinnerSonuclar);
            spinner.setAdapter(spinnerAdapter);


        }
    }

    private class GetInfos extends AsyncTask<String, Void, List<String>> {


        @Override
        protected void onPreExecute() {

            super.onPreExecute();

            showProgressDialog("", "Lütfen biraz bekleyin");


        }

        @Override
        protected List<String> doInBackground(String... params) {
            try {
                //  BilgileriDoldur();

                sonuclar = bilgiGetir.BilgileriDoldur(params[0], params[1], baglanti);
                adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_expandable_list_item_1, sonuclar);


            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return sonuclar;
        }

        @Override
        protected void onPostExecute(List<String> Result) {

            listView.setAdapter(adapter);
            // spinner.setAdapter(spinnerAdapter);
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }


        }

    }


    private void showProgressDialog(String title, String message) {
        progressDialog = new ProgressDialog(this);

        progressDialog.setTitle(title); //title

        progressDialog.setMessage(message); // message

        progressDialog.setCancelable(false);

        progressDialog.show();
    }

    private void PopUp(String parts[], Context context) {

        Button btnClosePopup;
        ListView listViewTumVeriler;
        ArrayAdapter adapterTumVeriler;
        List<String> tumSonuclar = new ArrayList<String>();

        mContext = context;

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int widht = size.x;
        int height = size.y;


        try {
            LayoutInflater layoutInflater = (LayoutInflater) MainActivity.this.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = layoutInflater.inflate(R.layout.screen_popup, (ViewGroup) findViewById(R.id.popup_element));
            pwindo = new PopupWindow(layout, widht - 60, height - 600, true);
            pwindo.showAtLocation(layout, Gravity.CENTER, 0, 0);

            btnClosePopup = (Button) layout.findViewById(R.id.btn_close_popup);
            listViewTumVeriler = (ListView) layout.findViewById(R.id.listViewTumVeriler);

            for (int j = 0; j < 5; j++) {
                for (int i = 0; i < parts.length; i++) {
                    tumSonuclar.add(parts[i]);
                }
            }


            adapterTumVeriler = new ArrayAdapter(MainActivity.this, R.layout.support_simple_spinner_dropdown_item, tumSonuclar);
            listViewTumVeriler.setAdapter(adapterTumVeriler);

            btnClosePopup.setOnClickListener(cancel_button_click_listener);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private View.OnClickListener cancel_button_click_listener = new View.OnClickListener() {
        public void onClick(View v) {
            pwindo.dismiss();

        }
    };


    private boolean InternetBaglantiKontrol() {

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return activeNetworkInfo != null && activeNetworkInfo.isConnected();

    }
}
