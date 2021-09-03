package com.emineakduman.mzikoynatcm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class AkilliOynaticiActivity extends AppCompatActivity {
    private RelativeLayout anaRelativeLayout;
    private SpeechRecognizer konusmaTanimlayici;
    private Intent konusmaTanimlayiciIntent;
    private String tutucu="";

    private ImageView oynatDurdurBtn,sonrakiSarkiBtn,oncekiSarkiBtn;
    private TextView sarkiAdiText;
    private ImageView imageView;
    private RelativeLayout asagiRelativeLayout;
    private Button sesliKomutBtn;

    private String mode ="ON";

    private MediaPlayer myMediaPlayer;
    private int pozisyon;
    private ArrayList<File> benimSarkilarim;
    private String mSarkiAdi;

    private RewardedAd mRewardedAd;
    private final String TAG = "AkilliOynaticiActivity";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_akilli_oynatici);
        anaRelativeLayout=findViewById(R.id.anaRelativeLayout);

        sesliKomutİzniniKontrol();




     //   imageView.setBackgroundResource(R.drawable.logo);

        oynatDurdurBtn = findViewById(R.id.oynat_durdur_btn);
        sonrakiSarkiBtn= findViewById(R.id.sonraki_sarki_btn);
        oncekiSarkiBtn= findViewById(R.id.onceki_sarki_btn);
        sarkiAdiText=findViewById(R.id.sarkiAdi);
        asagiRelativeLayout=findViewById(R.id.asagi);
        sesliKomutBtn=findViewById(R.id.sesli_komut_btn);
        imageView=findViewById(R.id.logo);
        degerleriAlmayıDogrulaVeOynatmayaBasla();

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                loadRewardedAd();
            }
        });

        konusmaTanimlayici=SpeechRecognizer.createSpeechRecognizer(AkilliOynaticiActivity.this);
        konusmaTanimlayiciIntent= new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        konusmaTanimlayiciIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        konusmaTanimlayiciIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());



        konusmaTanimlayici.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int i) {

            }

            @Override
            public void onResults(Bundle bundle) {
                ArrayList<String> bulunanEslesmeler = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                if(bulunanEslesmeler != null){
                    if(mode.equals("ON")){
                        tutucu = bulunanEslesmeler.get(0);
                        if(tutucu.equals("şarkıyı durdur")){
                            sarkiyiOynatDurdur();
                            Toast.makeText(AkilliOynaticiActivity.this, "Komutunuz: "+tutucu, Toast.LENGTH_SHORT).show();
                        }
                        else if(tutucu.equals("şarkıyı oynat")){
                            sarkiyiOynatDurdur();
                            Toast.makeText(AkilliOynaticiActivity.this, "Komutunuz: "+tutucu, Toast.LENGTH_SHORT).show();
                        }
                        else if(tutucu.equals("sonraki şarkı")){
                            sonrakiSarkiyiOynat();
                            showRewardedAd();
                            Toast.makeText(AkilliOynaticiActivity.this, "Komutunuz: "+tutucu, Toast.LENGTH_SHORT).show();
                        }
                        else if(tutucu.equals("önceki şarkı")){
                            oncekiSarkiyiOynat();
                            showRewardedAd();
                            Toast.makeText(AkilliOynaticiActivity.this, "Komutunuz: "+tutucu, Toast.LENGTH_SHORT).show();
                        }
                    }
                   // Toast.makeText(AkilliOynaticiActivity.this, "Sonuç = "+tutucu, Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onPartialResults(Bundle bundle) {

            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });

        anaRelativeLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        konusmaTanimlayici.startListening(konusmaTanimlayiciIntent);
                        tutucu="";
                        break;

                    case MotionEvent.ACTION_UP:
                        konusmaTanimlayici.stopListening();
                        break;
                }
                return false;
            }
        });

        sesliKomutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mode.equals("ON")){
                    mode="OFF";
                    sesliKomutBtn.setText("SESLİ KOMUT MODU-OFF");
                    asagiRelativeLayout.setVisibility(View.VISIBLE);
                }else{
                    mode="ON";
                    sesliKomutBtn.setText("SESLİ KOMUT MODU-ON");
                    asagiRelativeLayout.setVisibility(View.GONE);
                }
            }
        });

        oynatDurdurBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sarkiyiOynatDurdur();
            }
        });

        sonrakiSarkiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               if(myMediaPlayer.getCurrentPosition()>0){
                   sonrakiSarkiyiOynat();
                   showRewardedAd();

               }
            }
        });

        oncekiSarkiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(myMediaPlayer.getCurrentPosition()>0){
                    oncekiSarkiyiOynat();
                    showRewardedAd();

                }
            }
        });

    }

    private void loadRewardedAd() {

        AdRequest adRequest = new AdRequest.Builder().build();

        RewardedAd.load(this, "ca-app-pub-5442804591703721/1465382184",
                adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error.
                        Log.d(TAG, loadAdError.getMessage());
                        mRewardedAd = null;
                        Log.d(TAG, "onAdFailedToLoad");

                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                        mRewardedAd = rewardedAd;
                        Log.d(TAG, "Ad was loaded.");

                        mRewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdShowedFullScreenContent() {
                                // Reklam gösterildiğinde çağrılır.
                                Log.d(TAG, "Ad was shown.");
                                sarkiyiOynatDurdur();
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                // Reklam gösterilemediğinde çağrılır.
                                Log.d(TAG, "Ad failed to show.");
                            }

                            @Override
                            public void onAdDismissedFullScreenContent() {
                                //İlan kapatıldığında çağrılır.
                                // Reklamı ikinci kez göstermemek için reklam referansını null olarak ayarlayın.
                                Log.d(TAG, "Ad was dismissed.");
                                mRewardedAd = null;
                                sarkiyiOynatDurdur();
                               /* Intent yemekDetayi= new Intent(YemeklerActivity.this,YemekDetayiActivity.class);
                                yemekDetayi.putExtra("YemekId",yemekId);
                                startActivity(yemekDetayi);*/



                            }
                        });
                    }
                });


    }


    private void degerleriAlmayıDogrulaVeOynatmayaBasla(){
        if(myMediaPlayer!=null){
            myMediaPlayer.stop();
            myMediaPlayer.release();
        }
         Intent intent= getIntent();
        Bundle bundle = intent.getExtras();

        benimSarkilarim= (ArrayList) bundle.getParcelableArrayList("şarkı");
        mSarkiAdi = benimSarkilarim.get(pozisyon).getName();
        String sarkiAdi = intent.getStringExtra("ad");

        sarkiAdiText.setText(sarkiAdi);
        sarkiAdiText.setSelected(true);

        pozisyon= bundle.getInt("pozisyon",0);
        Uri uri= Uri.parse(benimSarkilarim.get(pozisyon).toString());

        myMediaPlayer= MediaPlayer.create(AkilliOynaticiActivity.this,uri);
        myMediaPlayer.start();
    }
    private void sesliKomutİzniniKontrol(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(!(ContextCompat.checkSelfPermission(AkilliOynaticiActivity.this,
                    Manifest.permission.RECORD_AUDIO)== PackageManager.PERMISSION_GRANTED)){
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:"+getPackageName()));
                startActivity(intent);
                finish();



            }

        }
    }

    private void sarkiyiOynatDurdur(){
        if(myMediaPlayer.isPlaying()){
            oynatDurdurBtn.setImageResource(R.drawable.play);
            myMediaPlayer.pause();
        }else{
            oynatDurdurBtn.setImageResource(R.drawable.pause);
            myMediaPlayer.start();

        }

    }

    private void sonrakiSarkiyiOynat(){
        myMediaPlayer.pause();
        myMediaPlayer.start();
        myMediaPlayer.release();

        pozisyon=((pozisyon+1)%benimSarkilarim.size());

        Uri uri= Uri.parse(benimSarkilarim.get(pozisyon).toString());
         myMediaPlayer = MediaPlayer.create(AkilliOynaticiActivity.this,uri);

         mSarkiAdi = benimSarkilarim.get(pozisyon).toString();
         sarkiAdiText.setText(mSarkiAdi);
         myMediaPlayer.start();

         //şarkıyı oynat durdur

        if(myMediaPlayer.isPlaying()){
            oynatDurdurBtn.setImageResource(R.drawable.pause);


        }else{
            oynatDurdurBtn.setImageResource(R.drawable.play);



        }


    }

    private void oncekiSarkiyiOynat(){
        myMediaPlayer.pause();
        myMediaPlayer.start();
        myMediaPlayer.release();

        pozisyon= ((pozisyon-1)<0 ? (benimSarkilarim.size()-1) : (pozisyon));

        Uri uri = Uri.parse(benimSarkilarim.get(pozisyon).toString());
        myMediaPlayer= MediaPlayer.create(AkilliOynaticiActivity.this,uri);

        mSarkiAdi = benimSarkilarim.get(pozisyon).toString();
        sarkiAdiText.setText(mSarkiAdi);
        myMediaPlayer.start();

        if(myMediaPlayer.isPlaying()){
            oynatDurdurBtn.setImageResource(R.drawable.pause);


        }else{
            oynatDurdurBtn.setImageResource(R.drawable.play);



        }

    }

    private void showRewardedAd(){
        if (mRewardedAd != null) {

            mRewardedAd.show(this, new OnUserEarnedRewardListener() {
                @Override
                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                    // Handle the reward.
                    Log.d(TAG, "The user earned the reward.");
                    int rewardAmount = rewardItem.getAmount();
                    String rewardType = rewardItem.getType();
                }
            });
        } else {
            Log.d(TAG, "The rewarded ad wasn't ready yet.");
        }
    }
}

