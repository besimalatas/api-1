package com.besimm.test;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;

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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by besimm on 13.8.2016.
 */
public class BilgiGetir {

    List<String> sonuclar = new ArrayList<String>();
    List<String> tumBilgiler = new ArrayList<String>();

    public BilgiGetir() {
        List<String> sonuclar = new ArrayList<String>();
        List<String> tumBilgiler = new ArrayList<String>();

        this.tumBilgiler = tumBilgiler;
        this.sonuclar = sonuclar;

    }

    private ProgressDialog progressDialog;


    public List<String> BilgileriDoldur(String apiUrl, String etiketAdi, HttpURLConnection baglanti) throws XmlPullParserException, IOException {

        //    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        //   StrictMode.setThreadPolicy(policy);


        try {
            URL url = new URL(apiUrl);
            baglanti = (HttpURLConnection) url.openConnection();

            int baglanti_durumu = baglanti.getResponseCode();

            if (baglanti_durumu == HttpURLConnection.HTTP_OK) ;
            {
                BufferedInputStream bufferedInputStream = new BufferedInputStream(baglanti.getInputStream());
                DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

                Document document = documentBuilder.parse(bufferedInputStream);

                NodeList nodeList = document.getElementsByTagName(etiketAdi);


                for (int i = 0; i < nodeList.getLength(); i++) {

                    Element element = (Element) nodeList.item(i);

                    NodeList nodeListKod = element.getElementsByTagName("kod");
                    NodeList nodeListUnvan = element.getElementsByTagName("unvan");
                    NodeList nodeListKayitNo = element.getElementsByTagName("kayitNo");
                    NodeList nodeListVade = element.getElementsByTagName("vade");
                    NodeList nodeListTaksitTutari = element.getElementsByTagName("taksitTutari");
                    NodeList nodeListDonemi = element.getElementsByTagName("donemi");

                    String kod = nodeListKod.item(0).getFirstChild().getNodeValue();
                    String unvan = nodeListUnvan.item(0).getFirstChild().getNodeValue();
                    String kayitNo = nodeListKayitNo.item(0).getFirstChild().getNodeValue();
                    String vade = nodeListVade.item(0).getFirstChild().getNodeValue();
                    String taksitTutari = nodeListTaksitTutari.item(0).getFirstChild().getNodeValue();
                    String donemi = nodeListDonemi.item(0).getFirstChild().getNodeValue();

                    sonuclar.add(kod + " " + unvan + " " + kayitNo + " " + " " + vade);
                    tumBilgiler.add("Kodu: " + kod + "," + "Ünvanı" + unvan + "," + kayitNo +
                            "," + " " + vade + "," + taksitTutari + "," + donemi);


                }
            }


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } finally {
            baglanti.disconnect();
        }


        return sonuclar;
    }


    public List<String> GetTumBilgiler() {
        return tumBilgiler;
    }
}

