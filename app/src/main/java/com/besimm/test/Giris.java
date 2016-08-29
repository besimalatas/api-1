package com.besimm.test;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.besimm.test.custom.design.CustomAlertDialog;

public class Giris extends AppCompatActivity {

    SharedPreferences uyeBilgileri;                            // Uye giris bilgilerini tutmak icin olusturulan sharedpreferences nesnesi
    SharedPreferences.Editor editor;                           // Sharedpre' duzenlemek icin  olusturulan editor

    private EditText kullaniciAdi, parola;                     // **
    private Button giris;                                      // Arayuz elemanlarımız
    private ProgressDialog progressDialog;
    // private PopupWindow pwindo;                                 Internet baglanti uyarisi icin popupWindow **iptal edilerek yerine AlertDialog lullanildi
    private CheckBox beniHatirla;                              // **
    String cikis = "hayir";                                    // Cikis isteği default olarak hayir olarak belirlendi

    CustomAlertDialog alert;                                   // Kendi olusturdugumuz özel AlertDialog sınıfından nesnemiz --Cikis ve internet baglantı uyarisinda kullanilacak
    Display display;                                           // **
    Point size = new Point();                                  // **
    int widht = 0;                                             // CustomAlertDialog' boyutunu ayarlamak icin cihazın genisliğini alıyoruz


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_giris);

        uyeBilgileri = getPreferences(MODE_PRIVATE);           // uyeBilgilerimizi private modda olusturuyoruz
        editor = uyeBilgileri.edit();                          // editor ile sharedPrefarence'si düzenleme ozelliği verdik

        alert = new CustomAlertDialog(getApplicationContext());    //uyari penceresi nesnemizi olusturarak constractor'ına context set ettik(
        display = getWindowManager().getDefaultDisplay();          // **
        display.getSize(size);                                     // **
        widht = size.x;                                            // ekranın genisliğini aldık

        Intent cikisMi = getIntent();                          // cikis istegi gelip gelmedigini kontrol ediyoruz
        cikis = cikisMi.getStringExtra("cikis");


        if (cikis != null) {                                   // cikis istegi geldiyse ilk activity(giris) sonlandırarak cikis sagliyoruz
            moveTaskToBack(true);
            finish();
        } else {


            kullaniciAdi = (EditText) findViewById(R.id.kullaniciAdi);      // **
            parola = (EditText) findViewById(R.id.parola);                  // **
            giris = (Button) findViewById(R.id.giris);                      // **
            beniHatirla = (CheckBox) findViewById(R.id.beniHatirla);        // Arayüz elemanlarinin tanımlanması


            if (getPreferences(MODE_PRIVATE).getString("kullaniciAdi", "bulunamadı").equals(" ")) {      // preferences'da kayit var mi diye sorguluyoruz
                beniHatirla.setChecked(false);                                                           // kayit yok ise
                kullaniciAdi.setText("");
                parola.setText("");
            } else {                                                                                     // kayit var ise
                kullaniciAdi.setText(getPreferences(MODE_PRIVATE).getString("kullaniciAdi", ""));
                parola.setText(getPreferences(MODE_PRIVATE).getString("parola", ""));
                beniHatirla.setChecked(true);
            }
            beniHatirla.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {                     // beni hatirla durumunun degistirimle isteği

                    if (isChecked) {                                                                // secilmis ise bilgiler editorle sheradprefarences'e set ediliyor
                        editor.putString("kullaniciAdi", kullaniciAdi.getText().toString());
                        editor.putString("parola", parola.getText().toString());
                    } else {                                                                        // secim kaidirilmis ise
                        editor.putString("kullaniciAdi", " ");
                        editor.putString("parola", " ");
                    }
                    editor.apply();                                                                 //kaydetme islemi yada -- editor.commit();

                }
            });

            giris.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View v) {                                                       // giris istegi

                                             if (!InternetBaglantiKontrol()) {                      // Internet baglantisi kontrolu false ise:

                                                 try {

                                                     alert.showDialog(Giris.this, "Lütfen internet bağlantınızı kontrol ediniz!", widht);       // custom alertdialog'un show metodunu calistiriyoruz
                                                     alert.ımageView.setBackgroundResource(R.drawable.error);                                    // paremetre olarak üyari mesaji ve ekran boyutunu gönderiyoruz, imageView'e uyari görüntüsünü ekliyorz
                                                     alert.dialogButton.setText("Tamam");                                                       // **
                                                     alert.dialogExit.setText("Çıkış");                                                         // butonlara seceneklerimizi yazıyoruz

                                                 } catch (Exception e) {
                                                     e.printStackTrace();
                                                 }

                                             } else {                                               // Baglanti true ise:

                                                 if (GirisKontrol()) {                              // Giris kontrolu yapiyoruz(kullanici adi ve parola sorgusu) true ise:

                                                     if (beniHatirla.isChecked()) {                                                           // beni hatirla durumunu giris butonuna
                                                         editor.putString("kullaniciAdi", kullaniciAdi.getText().toString());                 //tiklandiktan sonra bi daha kontrolünü yapıyoruz
                                                         editor.putString("parola", parola.getText().toString());                             //cunki kullaniici adi veya parola check olayından sonra tekrar degistirilmis olabilir
                                                         editor.apply();
                                                     }
                                                     progressDialog = new ProgressDialog(Giris.this, R.style.AppTheme_Dark_Dialog);          // kendi ozellestirdigimiz progres dialogu baslatiyoruz
                                                     progressDialog.setCancelable(false);                                                    // iptal edilme durumunu false belirledik
                                                     progressDialog.setMessage("Giris Yapılıyor, Lütfen biraz bekleyin...   ");              // mesajimiz
                                                     progressDialog.show();                                                                  // ekranda goruntuledik
                                                     progressDialog.getWindow().setLayout(widht - 32, ActionBar.LayoutParams.WRAP_CONTENT);  // boyutunu ayarlıyoruz

                                                     Thread thread = new Thread() {                                          // yeni bir thread olusturarak bi sure progressdialogu ekranda tutuyoruz
                                                         @Override
                                                         public void run() {
                                                             try {
                                                                 sleep(2500);
                                                             } catch (InterruptedException e) {
                                                                 e.printStackTrace();
                                                             } finally {

                                                                 progressDialog.dismiss();                                   // progressdialogu kapatıyoruz

                                                             }
                                                             Intent ıntent = new Intent(Giris.this, Secenekler.class);       // sonrasında secenekler sayfasina gecis yapiyoruz
                                                             startActivity(ıntent);
                                                             finish();                                                       // mevcut activity'i sonlandiriyoruz geri gelindiginde giris ekranı gorunmesin cikis yapisin diye
                                                         }
                                                     };
                                                     thread.start();

                                                 } else {                                                                   // giris bilgileri false ise:

                                                     CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.root);             // layout'da coordinatorLayout tanimladik

                                                     Snackbar snackbar = Snackbar.make(coordinatorLayout,                                           // Uyarimizi snackbar uzerinden vericez
                                                             "Lütfen giriş bilgilerinizi kontrol edip tekrar deneyiniz!", Snackbar.LENGTH_SHORT);   // uyari mesajimiz ve ekanda gorunme suresi
                                                     View view = snackbar.getView();                                                                // snackbar ornegi olusturuyoruz

                                                     TextView textView = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);   // TextView'e snackbarın text ozelligini atiyoruz
                                                     textView.setTextColor(Color.parseColor("#CC1C1C"));                                            //
                                                     textView.setTextSize(18);                                                                      // textview'in (snackbar') yazi rengini ve boyutunu degistiriyoruz
                                                     snackbar.setAction("Tamam", new View.OnClickListener() {                                       // setaction metodu ile yaziya tiklanma ozelligi kazandiriyoruz
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
    public void onBackPressed() {                                                     // cikis istegi gelirse:
        alert.showDialog(Giris.this, "Çıkmak istediğinize emin misiniz?", widht);     // dialogu baslatiyoruz ve uyari mesajlarimizi set ediyoruz
        alert.ımageView.setBackgroundResource(R.drawable.sure);                       // **
        alert.dialogButton.setText("Evet");
        alert.dialogExit.setText("Hayır");

    }

    private boolean GirisKontrol() {                                                    // giris paremetrelerinin dogrulugunu kontrol eden method

        String kulAdi = kullaniciAdi.getText().toString();                              // **
        String kulParola = parola.getText().toString();                                 // kullanici adi ve parolayi aliyoruz

        if (kulAdi.equals("") || kulAdi.equals(null) || !kulAdi.equals("deneme")) {     // kullanici adi dogruluk sorgusu
            //   kullaniciAdi.setError("Lürfen geçerli bir kullanıcı adı giriniz!");
            return false;
        } else {
            //   kullaniciAdi.setError(null);
        }
        if (kulParola.equals(null) || kulParola.equals("") || !kulParola.equals("123")) {  // parola sorgusu
            //    parola.setError("Lütfen geçerli bir parola giriniz!");
            return false;
        } else {
            //     parola.setError(null);
        }

        return true;
    }

    private boolean InternetBaglantiKontrol() {                                     // Internet baglantisini kontrol eden method

        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    /*   --Daha onceden olusturulmus ve ınternet uyarisi icin popupWİndow metodu--    */

    /*    private void BaglantiUyariPopup() {

        Button uyariTamam;
        TextView baglantiUyari;

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int widht = size.x;
        int height = size.y;


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
    */
}
