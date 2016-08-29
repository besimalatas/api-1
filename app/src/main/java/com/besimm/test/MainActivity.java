package com.besimm.test;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.support.design.widget.Snackbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

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
import java.util.Calendar;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class MainActivity extends AppCompatActivity {


    final String[] ilkTarih = new String[1];
    final String[] ikinciTarih = new String[1];

    final String[] dayOfMonthSon = new String[1];
    final String[] monthOfYearSon = new String[1];


    final int[] secilenGun = {0};
    final int[] secilenAy = {0};
    final int[] secilenYil = {0};

    final int[] secilenAySon = new int[1];
    final int[] secilenGunSon = new int[1];
    final int[] secilenYilSon = new int[1];


    Context mContext;


    DatePicker datePicker, datePicker2;
    DatePicker datePickerapi19, datePickerapi19_2;


    Button donemSec;

    String apiUrl = "http://10.0.2.2/api/Tablolar/GetSenetHareketleriDemo/?type=xml";
    String apiUrl2 = "http://10.0.2.2/api/Hareketler/GetSenetHareketleriDemo/?type=xml";
    String apiUrl3 = "http://10.0.2.2/api/Hareketler/GetSenetHareketleriDemo/?type=xml";
    Spinner spinner;


    Calendar calendar = Calendar.getInstance();

    BilgiGetir bilgiGetir = new BilgiGetir();                //secilen tablonun bilgilerinin cekileceği class (bundan her tablo için olmalı)

    private PopupWindow pwindo;                              //detayları görüntülemek için açılabilir pencere

    ArrayAdapter<String> adapter;                            //api den gelen sonucları set edeceğimiz adapter
    ArrayAdapter<String> spinnerAdapter;                     //api de bulunan tabloları set edeceğimiz adapter

    // String apiUrl="http://www.tcmb.gov.tr/kurlar/today.xml";

    List<String> sonuclar = new ArrayList<String>();               // apiden ilgili tablodan gelen sonuclar
    List<String> spinnerSonuclar = new ArrayList<String>();        //tablo isimleri (api'den değil android tarafından)
    HttpURLConnection baglanti = null;
    private ProgressDialog progressDialog;                    //gelen sonucların detaylarını göstermek için popup pencere


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        secilenAy[0]=calendar.get(Calendar.MONTH)+1;
        secilenGun[0]=calendar.get(Calendar.DAY_OF_MONTH);
        secilenYil[0]=calendar.get(Calendar.YEAR);

        secilenAySon[0]=secilenAy[0];
        secilenYilSon[0]=secilenYil[0];
        secilenGunSon[0]=secilenGun[0];

        datePickerapi19 = (DatePicker) findViewById(R.id.ilkTarihapi19);
        datePickerapi19_2 = (DatePicker) findViewById(R.id.ikinciTarihapi19);


        datePicker = (DatePicker) findViewById(R.id.ilkTarih);
        datePicker2 = (DatePicker) findViewById(R.id.ikinciTarih);


        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion < android.os.Build.VERSION_CODES.LOLLIPOP) {

            datePicker.setVisibility(View.GONE);
            datePicker2.setVisibility(View.GONE);
            datePickerapi19.setVisibility(View.VISIBLE);
            datePickerapi19_2.setVisibility(View.VISIBLE);


        } else {
            datePickerapi19.setVisibility(View.GONE);
            datePickerapi19_2.setVisibility(View.GONE);
            datePicker.setVisibility(View.VISIBLE);
            datePicker2.setVisibility(View.GONE);

        }


        spinner = (Spinner) findViewById(R.id.spinner);        //tabloları listeleyeceğimiz spinner

        donemSec = (Button) findViewById(R.id.donemSec);

        donemSec.setOnClickListener(DonemSec);
        datePickerapi19.init(datePickerapi19.getYear(), datePickerapi19.getMonth(), datePicker2.getDayOfMonth(), new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                secilenGun[0] = dayOfMonth;
                secilenAy[0] = monthOfYear;
                secilenYil[0] = year;

            }
        });

        datePickerapi19_2.init(datePickerapi19_2.getYear(), datePickerapi19_2.getMonth(), datePickerapi19_2.getDayOfMonth(), new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                secilenAySon[0] = monthOfYear;
                secilenGunSon[0] = dayOfMonth;
                secilenYilSon[0] = year;


            }
        });

        datePicker.init(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(), new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {


                secilenGun[0] = dayOfMonth;
                secilenAy[0] = monthOfYear;
                secilenYil[0] = year;


                datePicker.setVisibility(view.GONE);
                datePicker2.setVisibility(view.VISIBLE);

                InitalizeDatePicker2(secilenYil[0], secilenAy[0], secilenGun[0]);

            }
        });


        if (!InternetBaglantiKontrol()) {                      //internet bağlantısı kontrolu (burası daha stabil olmalı)

            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
        } else {


            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {  //bilgisi görüntülenmek istenen tablo'nun secimi
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                    switch (position) {

                        case (0): {
                            break;
                        }
                        case (1): {
                            new GetInfos().execute(apiUrl, "ArayuzModel");           //secilen tablonun url'si ve tagı yollanarak
                            break;                                                   //doInBackGround metodunda verilerin alınması sağlanıyor
                        }                                                            //apiUrl ve ArayuzModel gonderilir (metod içinda params olarak alınır)
                        case (2): {
                            new GetInfos().execute(apiUrl2, "ArayuzModel2");
                            break;
                        }
                        case (3): {
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

            spinnerSonuclar.add("Lütfen Seçim Yapınız");                     //spinner'a tablolar ekleniyor
            spinnerSonuclar.add("Seçim 1");
            spinnerSonuclar.add("Seçim 2");
            spinnerSonuclar.add("Senet Hareketleri");
            spinnerSonuclar.add("Spinner2");
            spinnerSonuclar.add("Spinner3");
            spinnerSonuclar.add("Spinner1");
            spinnerSonuclar.add("Spinner2");


            spinnerAdapter = new ArrayAdapter<String>(MainActivity.this, R.layout.support_simple_spinner_dropdown_item, spinnerSonuclar);   //olusturulan arraylist türünden spinnera aktivity icerisinde olusturuşuyor ve liste(spinner sonuclar )eklenıyor
            spinner.setAdapter(spinnerAdapter);                                                                                             //adaptere spinner set ediliyor


        }

    }

    private void InitalizeDatePicker2(final int secilenYil, final int secilenAy, final int secilenGun) {

        final int[] textAy = {0};
        final int[] textAy2 = {0};
        datePicker2.init(datePicker2.getYear(), secilenAy, secilenGun, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(final DatePicker view, final int year, final int monthOfYear, final int dayOfMonth) {

                textAy[0] = secilenAy + 1;


                secilenAySon[0] = monthOfYear;
                secilenGunSon[0] = dayOfMonth;
                secilenYilSon[0] = year;

                donemSec.setText(secilenGun + "/" + secilenAy + "/" + secilenYil + " --" + dayOfMonth + "/" + textAy[0] + "/" + year);
                datePicker.updateDate(secilenYil, secilenAy, secilenGun);
                datePicker.setVisibility(view.VISIBLE);
                if (secilenAy < monthOfYear || secilenAy > monthOfYear) {
                    datePicker.setVisibility(View.GONE);
                }

                datePicker2.init(year, monthOfYear, dayOfMonth, new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        if (secilenAy < monthOfYear || secilenAy > monthOfYear) {
                            datePicker.setVisibility(View.GONE);
                        }
                        textAy2[0] = monthOfYear + 1;
                        donemSec.setText(secilenGun + "/" + secilenAy + "/" + secilenYil + " --" + dayOfMonth + "/" + textAy2[0] + "/" + year);
                        secilenAySon[0] = monthOfYear;
                        secilenGunSon[0] = dayOfMonth;
                        secilenYilSon[0] = year;

                    }
                });


            }
        });
    }


    private class GetInfos extends AsyncTask<String, Void, List<String>> {      //verilerin arka planda getirilmesi için method
        //** AysnTasck'dan exdens ediliyor
        //**doInBacground bulunmak zorunda
        @Override
        //** aldıgı 3.parametre doInBackground için gerekli bilgiler
        protected void onPreExecute() {

            super.onPreExecute();
            showProgressDialog("", "Lütfen biraz bekleyin");                    //veriler getirilirken kullanıcıya bilgi vermek için method


        }

        @Override
        protected List<String> doInBackground(String... params) {              // gerekli bilgiler params ile alınıyor (apiUrl, etiketAdi)
            try {


                sonuclar = bilgiGetir.BilgileriDoldur(params[0], params[1], baglanti);          //apiUrl ve etkeTadi bilgiGetir class'ının BilgileriDoldur metoduna set ediliyor
                adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_expandable_list_item_1, sonuclar);   //gelen sonuclar adaptere ekleniyor


            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return sonuclar;
        }

        @Override
        protected void onPostExecute(List<String> Result) {               //doInBackground işlemi bittikten sonra calısacak method


            // spinner.setAdapter(spinnerAdapter);
            if (progressDialog != null && progressDialog.isShowing()) {   //progresdialog sonlandırılıyor
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


            for (int i = 0; i < parts.length; i++) {
                tumSonuclar.add(parts[i]);
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

    private View.OnClickListener DonemSec = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            secilenAy[0] += 1;
            secilenAySon[0] += 1;
            String castEdilenAy = String.valueOf(secilenAy[0]);
            String castEdilenAy2 = String.valueOf(secilenAySon[0]);
            String castEdilenGun = String.valueOf(secilenGun[0]);
            String castEdilenGun2 = String.valueOf(secilenGunSon[0]);

            if (secilenAy[0] < 10) {
                castEdilenAy = "0" + String.valueOf(secilenAy[0]);
            }
            if (secilenAySon[0] < 10) {
                castEdilenAy2 = "0" + String.valueOf(secilenAySon[0]);
            }
            if (secilenGun[0] < 10) {
                castEdilenGun = "0" + String.valueOf(secilenGun[0]);
            }

            if (secilenGunSon[0] < 10) {
                castEdilenGun2 = "0" + String.valueOf(secilenGunSon[0]);
            }

            ilkTarih[0] = String.valueOf(secilenYil[0] + "" + castEdilenAy + "" + castEdilenGun);
            ikinciTarih[0] = String.valueOf(secilenYilSon[0] + "" + castEdilenAy2 + "" + castEdilenGun2);

            donemSec.setText(ilkTarih[0] + ikinciTarih[0]);
            Intent ogrTaksitHareketleri = new Intent(MainActivity.this, GetOgrTaksitHareketleri.class);
            ogrTaksitHareketleri.putExtra("ilkTarih", ilkTarih[0]);
            ogrTaksitHareketleri.putExtra("ikinciTarih", ikinciTarih[0]);
            startActivity(ogrTaksitHareketleri);

            datePicker2.setVisibility(View.GONE);
            datePicker.setVisibility(View.GONE);
        }
    };
}
