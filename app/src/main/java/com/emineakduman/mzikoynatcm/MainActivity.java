package com.emineakduman.mzikoynatcm;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private String [] butunOgeler;
    private ListView mSarkiListesi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSarkiListesi=findViewById(R.id.sarkiListesi);
        uygulamaHariciDepolamaIzni();


    }

    public void uygulamaHariciDepolamaIzni(){
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override public void onPermissionGranted(PermissionGrantedResponse response)
                    {
                        sesliMedyaIsimleriniGoster();
                    }
                    @Override public void onPermissionDenied(PermissionDeniedResponse response)
                    {

                    }
                    @Override public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token)
                    {
                        token.continuePermissionRequest();
                    }
                }).check();

    }

    public ArrayList<File> sadeceSesliMedyayıOkuma(File file){
        ArrayList<File> arrayList= new ArrayList<>();

        File [] butunDosyalar =file.listFiles();
        for(File bireyselDosya : butunDosyalar){
            if(bireyselDosya.isDirectory() && !bireyselDosya.isHidden()){
                arrayList.addAll(sadeceSesliMedyayıOkuma(bireyselDosya));
            }else{
                if(bireyselDosya.getName().endsWith(".mp3")  || bireyselDosya.getName().endsWith(".aac")|| bireyselDosya.getName().endsWith(".wav") || bireyselDosya.getName().endsWith(".wma")){
                    arrayList.add(bireyselDosya);
                }
            }
        }
        return arrayList;
    }
    private void sesliMedyaIsimleriniGoster(){
        final ArrayList<File> sesliMedyalar = sadeceSesliMedyayıOkuma(Environment.getExternalStorageDirectory());

        butunOgeler = new String[sesliMedyalar.size()];
        for(int sarkiSayaci=0; sarkiSayaci<sesliMedyalar.size(); sarkiSayaci++){
            butunOgeler[sarkiSayaci]= sesliMedyalar.get(sarkiSayaci).getName();

        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1,butunOgeler);
         mSarkiListesi.setAdapter(arrayAdapter);

        mSarkiListesi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String sarkiAdi = mSarkiListesi.getItemAtPosition(i).toString();
                Intent intent = new Intent(MainActivity.this,AkilliOynaticiActivity.class);
                intent.putExtra("şarkı",sesliMedyalar);
                intent.putExtra("ad",sarkiAdi);
                intent.putExtra("pozisyon",i);
                startActivity(intent);
            }
        });
    }
}