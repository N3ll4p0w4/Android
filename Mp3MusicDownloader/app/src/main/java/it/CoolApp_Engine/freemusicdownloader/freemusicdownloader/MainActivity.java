package it.CoolApp_Engine.freemusicdownloader.freemusicdownloader;

import android.annotation.SuppressLint;
import android.content.*;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.*;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.*;
import android.view.*;
import android.view.inputmethod.*;
import android.webkit.*;
import android.widget.*;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.*;

import it.CoolApp_Engine.freemusicdownloader.settings.Permessi;
import it.CoolApp_Engine.freemusicdownloader.settings.Settings;

/**
 * Created by luca.daminato on 26/12/2017.
 */

public class MainActivity extends AppCompatActivity {

    private EditText songName;
    private Button cerca;
    private TextView testo;
    private WebView webView;

    private Permessi permessi;

    private LinearLayout layoutBottoniCanzoni;
    private MediaPlayer mediaPlayer;
    private MusicPlayerController musicPlayerController;

    private Datmusic datmusic = new Datmusic();

    private Document document;
    private String text = "";
    private Thread Loading = new Thread();

    private LayoutRisultatoCanzone[] layoutRisultatoCanzones = new LayoutRisultatoCanzone[0];
    private ArrayList<LayoutRisultatoCanzone> layoutRisultatoCanzonesDownload = new ArrayList<LayoutRisultatoCanzone>();

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    private AdView banner;
    private InterstitialAd interstitialAd;
    private int countCerca = 2;

    @SuppressLint("JavascriptInterface")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        MobileAds.initialize(this, getString(R.string.adsAppID));
        inizializeBanner();
        inizializeInterstitial();

        songName = (EditText) findViewById(R.id.songName);
        cerca = (Button)findViewById(R.id.cerca);
        testo = (TextView)findViewById(R.id.testo);
        webView = (WebView) findViewById(R.id.webView);
        layoutBottoniCanzoni = (LinearLayout) findViewById(R.id.layoutBottoniCanzoni);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView.getSettings().setAllowFileAccess(true);

        webView.addJavascriptInterface(this, "HTMLOUT");

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                AspettaJavascript(100);
            }
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Loading.interrupt();
                if(Internet.isConnected(MainActivity.this)) {
                    text = "Non connesso ad internet";
                    AggiornaTesto();
                } else {
                    text = "Error during loading page";
                    AggiornaTesto();
                    cerca.setText(getString(R.string.ricarica));
                }
            }
        });
        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                download(url);
            }
        });

        permessi = new Permessi(this);
        permessi.controlloPermessiScrittura();

        if(Internet.isConnected(MainActivity.this))
            testo.setText(getString(R.string.testoInizialeTextView));
        else
            testo.setText(getString(R.string.nonConnesso));

        songName.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                cerca.setText(getString(R.string.cerca));
            }
        });

        songName.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    cerca.performClick();
                    return true;
                }
                return false;
            }
        });

        musicPlayerController = new MusicPlayerController();

        pref = getApplicationContext().getSharedPreferences(getResources().getString(R.string.shredPrefsImpostazioni), MODE_PRIVATE);
        editor = pref.edit();
        if(!pref.contains(getResources().getString(R.string.pathDownloadFile))){
            editor.putString(getResources().getString(R.string.pathDownloadFile), new File(Environment.getExternalStorageDirectory(), "FreeMusicDownloader").getPath());
            editor.apply();
        }

        cerca.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(Internet.isConnected(MainActivity.this)) {
                    cerca.setText(getString(R.string.cerca));
                    if(songName.getText().toString().replaceAll(" ", "").isEmpty()){
                        Toast.makeText(MainActivity.this, getString(R.string.nessunNome), Toast.LENGTH_SHORT).show();
                        songName.setText("");
                    } else {
                        datmusic.Cerca(songName.getText().toString());
                        Loading.interrupt();
                        nascondiTastiera();
                        CaricaPagina(datmusic.getURL());
                        eseguiUnaVolta = true;
                        if(countCerca >= 3){
                            showInterstitialAfterTime(2000);
                            countCerca = 0;
                        } else countCerca++;
                    }
                } else
                    nonConnesso();
            }
        });
    }

    public void CaricaPagina(String url){
        webView.loadUrl(url);
        rimuoviBottoni();
        Loading = new Thread() {
            public void run() {
                int millisec = 200;
                try {
                    while (true) {
                        text = getString(R.string.caricamento)+".";
                        AggiornaTesto();
                        TimeUnit.MILLISECONDS.sleep(millisec);
                        for (int i = 0; i < 4; i++) {
                            text += ".";
                            AggiornaTesto();
                            TimeUnit.MILLISECONDS.sleep(millisec);
                        }
                    }
                } catch (InterruptedException e) {}
            }
        };
        Loading.start();
    }

    public void AspettaJavascript(int millisec){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                webView.post(new Runnable() {
                    public void run() {
                        webView.loadUrl("javascript:window.HTMLOUT.processHTML(document.getElementsByClassName(\"list-group\")[0].innerHTML);");
                    }
                });
            }
        }, millisec);
    }

    private boolean eseguiUnaVolta = false;
    @JavascriptInterface
    @SuppressWarnings("unused")
    public void processHTML(String html){
        document = Jsoup.parse(html);
        Elements elements = document.getElementsByClass("list-group-item list-group-item-danger");
        if(elements.size() > 0){
            text = elements.get(0).text();
            AggiornaTesto();
            Loading.interrupt();
            return;
        }
        elements = document.getElementsByClass("list-group-item");
        if(elements.size() == 0){
            AspettaJavascript(100);
            return;
        }
        if(eseguiUnaVolta) {
            eseguiUnaVolta = false;
            rimuoviBottoni();
            Loading.interrupt();
            layoutRisultatoCanzones = new LayoutRisultatoCanzone[elements.size()];
            for (int i = 0; i < elements.size(); i++) {
                Canzone c = new Canzone();
                c.setNome(elements.get(i).child(4).text());
                c.setDurata(elements.get(i).child(0).text());
                c.setDownload(elements.get(i).child(3).attr("href"));
                layoutRisultatoCanzones[i] = new LayoutRisultatoCanzone(c, "canzone" + i, this, webView, musicPlayerController);
            }

            layoutBottoniCanzoni.addView((View) getResources().getLayout(R.layout.risultato_ricerca_canzone));

            aggiungiBottoni();
            text = getString(R.string.premiPerScaricare);
            AggiornaTesto();
        } else {return;}
    }

    public static String linkCanzone;
    public void download (String url) {
        if(permessi.controlloPermessiScrittura()){
            for(int i=0; i<layoutRisultatoCanzones.length; i++) {
                if (layoutRisultatoCanzones[i].getCanzone().getDownload().equals(linkCanzone)) {
                    layoutRisultatoCanzones[i].startDownload(url);
                    break;
                }
            }
        } else {
           mostraToast(getString(R.string.permessoNegato), Toast.LENGTH_LONG);
        }
    }

    public void AggiornaTesto(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                testo.setText(text);
            }
        });
    }

    public void mostraToast(String string, int time){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, string, time).show();
            }
        });
    }

    public void aggiungiBottoni(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (int i = 0; i < layoutRisultatoCanzones.length; i++) {
                        layoutBottoniCanzoni.addView(layoutRisultatoCanzones[i].getLayout());
                    }
                } catch(NullPointerException e){}
                catch (IllegalArgumentException e){}
            }
        });
    }

    public void rimuoviBottoni(){
        final CountDownLatch latch = new CountDownLatch(1);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (int i = 0; i < layoutRisultatoCanzones.length; i++){
                        if(layoutRisultatoCanzones[i].canDelete()){
                            layoutBottoniCanzoni.removeView(layoutBottoniCanzoni.findViewWithTag(layoutRisultatoCanzones[i].getLayout().getTag()));
                        } else {
                            layoutRisultatoCanzonesDownload.add(layoutRisultatoCanzones[i]);
                        }
                    }
                    for (int i = 0; i < layoutRisultatoCanzonesDownload.size(); i++){
                        layoutRisultatoCanzonesDownload.get(i).setLayoutTag(i+"");
                    }
                    for (int i = 0; i < layoutRisultatoCanzonesDownload.size(); i++){
                        if(layoutRisultatoCanzonesDownload.get(i).canDelete()) {
                            layoutBottoniCanzoni.removeView(layoutBottoniCanzoni.findViewWithTag(layoutRisultatoCanzonesDownload.get(i).getLayout().getTag()));
                            layoutRisultatoCanzonesDownload.remove(i);
                            for (int j = 0; j < layoutRisultatoCanzonesDownload.size(); j++) {
                                layoutRisultatoCanzonesDownload.get(j).setLayoutTag(j + "");
                            }
                        }
                    }
                } catch (NullPointerException e){}
                latch.countDown();
            }
        });
        try {
            latch.await();
        } catch (InterruptedException e) {}
    }

    private void nascondiTastiera(){
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    public void nonConnesso(){
        text = getString(R.string.nonConnesso);
        AggiornaTesto();
        cerca.setText(getString(R.string.ricarica));
        rimuoviBottoni();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.settings) {
            musicPlayerController.stopLayoutRisultatoCanzone();
            MainActivity.this.startActivity(new Intent(this, Settings.class));
        }
        return super.onOptionsItemSelected(item);
    }

    private void inizializeBanner(){
        banner = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        banner.loadAd(adRequest);
    }

    private void inizializeInterstitial(){
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getString(R.string.adsInterstitialID));
        interstitialAd.loadAd(new AdRequest.Builder().build());
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                interstitialAd.loadAd(new AdRequest.Builder().build());
            }
        });
    }

    private void showInterstitialAfterTime(int millisec){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                if(interstitialAd.isLoaded()) {
                    interstitialAd.show();
                }
            }
        }, millisec);
    }

    @Override
    protected void onStop() {
        super.onStop();
        getDelegate().onStop();
        musicPlayerController.stopLayoutRisultatoCanzone();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getDelegate().onDestroy();
        musicPlayerController.stopLayoutRisultatoCanzone();
        finish();
    }

}