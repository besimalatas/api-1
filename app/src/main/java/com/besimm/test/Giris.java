package com.besimm.test;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.HandlerThread;
import android.provider.Settings;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.besimm.test.custom.design.CustomAlertDialog;
import com.google.android.gms.auth.api.consent.GetConsentIntentRequest;

public class Giris extends AppCompatActivity {

    SharedPreferences uyeBilgileri;
    SharedPreferences.Editor editor;

    private EditText kullaniciAdi, parola;
    private Button btn_giris;
    private ProgressDialog progressDialog;
    private PopupWindow pwindo;
    private Context mContext;
    private CheckBox beniHatirla;
    String cikis = "hayir";

    CustomAlertDialog alert;
    Display display;
    Point size = new Point();
    int widht = 0;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_giris);

        uyeBilgileri = getPreferences(MODE_PRIVATE);
        editor = uyeBilgileri.edit();

        alert = new CustomAlertDialog(getApplicationContext());
        display = getWindowManager().getDefaultDisplay();
        display.getSize(size);
        widht = size.x;

        Intent cikisMi = getIntent();
        cikis = cikisMi.getStringExtra("cikis");


        if (cikis != null) {
            moveTaskToBack(true);
            finish();
            return;
        } else {


            kullaniciAdi = (EditText) findViewById(R.id.kullaniciAdi);
            parola = (EditText) findViewById(R.id.parola);
            btn_giris = (Button) findViewById(R.id.btn_giris);
            beniHatirla = (CheckBox) findViewById(R.id.beniHatirla);


            if (getPreferences(MODE_PRIVATE).getString("kullaniciAdi", "bulunamadı").toString() == " ") {
                beniHatirla.setChecked(false);
                kullaniciAdi.setText("");
                parola.setText("");
            } else {
                kullaniciAdi.setText(getPreferences(MODE_PRIVATE).getString("kullaniciAdi", "").toString());
                parola.setText(getPreferences(MODE_PRIVATE).getString("parola", "").toString());
                beniHatirla.setChecked(true);
            }
            beniHatirla.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if (isChecked) {
                        editor.putString("kullaniciAdi", kullaniciAdi.getText().toString());
                        editor.putString("parola", parola.getText().toString());
                        editor.commit();
                    } else {
                        editor.putString("kullaniciAdi", " ");
                        editor.putString("parola", " ");
                        editor.commit();

                    }

                }
            });

            btn_giris.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!InternetBaglantiKontrol()) {
                        // BaglantiUyariPopup(mContext);

                        //      new AlertDialog.Builder(Giris.this).
                        //              setTitle("İnternet Bağlantı Uyarısı").
                        //              setMessage("Lütfen internet bağlantınızı kontrol ediniz!").
                        //              setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                        //                  @Override
                        //                  public void onClick(DialogInterface dialog, int which) {
                        //                      startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));

                        //                  }
                        //              }).setNegativeButton("Çıkış", new DialogInterface.OnClickListener() {
                        //          @Override
                        //          public void onClick(DialogInterface dialog, int which) {
                        //              finish();
                        //          }
                        //      })
                        //              .setIcon(android.R.drawable.ic_dialog_alert).show();

                        try {


                            alert.showDialog(Giris.this, "Lütfen internet bağlantınızı kontrol ediniz!", widht);
                            alert.ımageView.setBackgroundResource(R.drawable.sure);
                            alert.dialogButton.setText("Tamam");
                            alert.dialogExit.setText("Çıkış");

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else {


                        if (GirisKontrol()) {

                            if (beniHatirla.isChecked()) {
                                editor.putString("kullaniciAdi", kullaniciAdi.getText().toString());
                                editor.putString("parola", parola.getText().toString());
                                editor.commit();
                            }
                                progressDialog = new ProgressDialog(Giris.this, R.style.AppTheme_Dark_Dialog);
                                progressDialog.setCancelable(false);
                                progressDialog.setMessage("Giris Yapılıyor...     ");
                                progressDialog.show();
                                progressDialog.getWindow().setLayout(widht - 32, ActionBar.LayoutParams.WRAP_CONTENT);

                                Thread thread = new Thread() {
                                    @Override
                                    public void run() {
                                        try {
                                            sleep(2000);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        } finally {

                                            progressDialog.dismiss();

                                        }
                                        Intent ıntent = new Intent(Giris.this, Secenekler.class);
                                        startActivity(ıntent);
                                        finish();
                                    }
                                };
                                thread.start();

                            } else {

                                CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.root);


                                Snackbar snackbar = Snackbar.make(coordinatorLayout, "Lütfen giriş bilgilerinizi kontrol edip tekrar deneyiniz!", Snackbar.LENGTH_SHORT);
                                View view = snackbar.getView();

                                TextView textView = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                                textView.setTextColor(Color.parseColor("#CC1C1C"));
                                textView.setTextSize(18);
                                snackbar.setAction("Tamam", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                    }
                                });
                                snackbar.show();


                            }
                        }
                    }
                }

                );
            }
        }


        @Override
        public void onBackPressed () {
            alert.showDialog(Giris.this, "Çıkmak istediğinize emin misiniz?", widht);
            alert.ımageView.setBackgroundResource(R.drawable.sure);
            alert.dialogButton.setText("Evet");
            alert.dialogExit.setText("Hayır");


        }

    private void BaglantiUyariPopup(Context context) {

        Button uyariTamam;
        TextView baglantiUyari;

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int widht = size.x;
        int height = size.y;

        mContext = context;

        try {
            LayoutInflater layoutInflater = (LayoutInflater) Giris.this.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = layoutInflater.inflate(R.layout.internet_baglanti_uyari, (ViewGroup) findViewById(R.id.uyari));
            pwindo = new PopupWindow(layout, widht - 100, height / 4, true);
            pwindo.showAtLocation(layout, Gravity.CENTER, 0, 0);

            uyariTamam = (Button) layout.findViewById(R.id.uyariTamam);
            uyariTamam.setOnClickListener(UyariTamamClick);
        } catch (Exception e) {

            e.printStackTrace();
        }


    }

    private View.OnClickListener UyariTamamClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            pwindo.dismiss();
            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
        }
    };

    private boolean GirisKontrol() {

        String kulAdi = kullaniciAdi.getText().toString();
        String kulParola = parola.getText().toString();

        if (kulAdi.equals(null) || !kulAdi.equals("deneme")) {
            //   kullaniciAdi.setError("Lürfen geçerli bir kullanıcı adı giriniz!");
            return false;
        } else {
            kullaniciAdi.setError(null);
        }
        if (kulParola.equals(null) || !kulParola.equals("123")) {
            //    parola.setError("Lütfen geçerli bir parola giriniz!");
            return false;
        } else {
            //     parola.setError(null);
        }

        return true;
    }

    private boolean InternetBaglantiKontrol() {


        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


}
