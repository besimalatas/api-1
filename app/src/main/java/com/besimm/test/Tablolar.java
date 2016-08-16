package com.besimm.test;

import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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

public class Tablolar extends AppCompatActivity {

    ListView listView;
    private String apiUrl = "http://androidapitest.somee.com/api/Tablolar/GetTablolar/?type=xml";
    private List<String> sonuclar = new ArrayList<String>();
    private ArrayAdapter<String> adapter;
    private HttpURLConnection baglanti;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tablolar);

        listView = (ListView) findViewById(R.id.listView);

        try {
            BilgileriDoldur();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void BilgileriDoldur() throws XmlPullParserException, IOException {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {


            URL url = new URL(apiUrl);
            baglanti = (HttpURLConnection) url.openConnection();

            int baglanti_durumu = baglanti.getResponseCode();

            if (baglanti_durumu == HttpURLConnection.HTTP_OK) {


                BufferedInputStream bufferedInputStream = new BufferedInputStream(baglanti.getInputStream());
                DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

                Document document = documentBuilder.parse(bufferedInputStream);

                NodeList nodeList = document.getElementsByTagName("Tablolar");

                for (int i = 0; i < nodeList.getLength(); i++) {

                    Element element = (Element) nodeList.item(i);
                    NodeList elementTabloAdi = element.getElementsByTagName("tabloAdi");

                    String tabloAdi = elementTabloAdi.item(0).getFirstChild().getNodeValue();

                    sonuclar.add((i + 1) + "--- " + tabloAdi);

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
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, sonuclar);
        listView.setAdapter(adapter);
    }
}
