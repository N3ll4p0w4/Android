package it.coolappengine.musictagfinder;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.*;
import android.net.Uri;
import android.os.*;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.*;
import android.widget.*;

import org.jaudiotagger.audio.*;
import org.jaudiotagger.audio.exceptions.*;
import org.jaudiotagger.tag.*;
import org.jaudiotagger.tag.images.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;

import java.io.*;
import java.net.*;

public class EditSong extends AppCompatActivity {


    private EditText songName;
    private Button cerca;
    private WebView webView;
    private TextView testo;
    private ImageView copertina;

    private String currentURL = "";

    private File songFile;
    private String filePath;

    private Permessi permessi;

    @SuppressLint("JavascriptInterface")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_song);

        filePath = getIntent().getStringExtra("filePath");
        songFile = new File(filePath);

        songName = (EditText) findViewById(R.id.songName);
        cerca = (Button)findViewById(R.id.cerca);
        testo = (TextView)findViewById(R.id.testo);
        //webView = new WebView(this);
        webView = (WebView) findViewById(R.id.webView);
        copertina = (ImageView) findViewById(R.id.copertina);

        webView.getSettings().setUserAgentString("Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.4) Gecko/20100101 Firefox/4.0");
        webView.getSettings().setBlockNetworkImage(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(this, "HTMLOUT");

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageCommitVisible(WebView view, String url) {
                    String rimuoviScript = "var r = document.getElementsByTagName('script');for (var i = (r.length-1); i >= 0; i--) {r[i].parentNode.removeChild(r[i]);}";
                    webView.loadUrl("javascript:"+rimuoviScript+"window.HTMLOUT.getHtml(document.getElementById(\"a-page\").innerHTML);");
                }
            });
        } else{
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    String rimuoviScript = "var r = document.getElementsByTagName('script');for (var i = (r.length-1); i >= 0; i--) {r[i].parentNode.removeChild(r[i]);}";
                    webView.loadUrl("javascript:"+rimuoviScript+"window.HTMLOUT.getHtml(document.getElementById(\"a-page\").innerHTML);");
                }
            });
        }

        permessi = new Permessi(this);
        permessi.controlloPermessiScrittura();

        songName.setText(removeExtension(songFile.getName()));

        webView.loadUrl("https://www.amazon.com/s/ref=nb_sb_noss_2?url=search-alias%3Ddigital-music&field-keywords="+filter(songFile.getName()));
        currentURL = webView.getUrl();

        cerca.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                webView.stopLoading();
                webView.loadUrl("https://www.amazon.com/s/ref=nb_sb_noss_2?url=search-alias%3Ddigital-music&field-keywords="+songName.getText());
                currentURL = webView.getUrl();
                System.out.println("Apro ricerca");
            }
        });
    }

    @JavascriptInterface
    @SuppressWarnings("unused")
    public void getHtml(String html){
        if(html == null)
            html = "";
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                testo.setText("Caricamento...");
            }
        });
        System.out.println("Caricamento...");
        final Document document = Jsoup.parse(html);
        //se pagina risultati
        if(document.getElementById("resultsCol") != null) {
            //se ci sono risultati
            if(document.getElementsByClass("a-size-base a-link-normal a-text-bold a-text-normal").size() > 0) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        testo.setText(document.getElementsByClass("a-size-base a-link-normal a-text-bold a-text-normal").get(0).attr("href"));
                        webView.stopLoading();
                        webView.loadUrl(document.getElementsByClass("a-size-base a-link-normal a-text-bold a-text-normal").get(0).attr("href"));
                        currentURL = document.getElementsByClass("a-size-base a-link-normal a-text-bold a-text-normal").get(0).attr("href");
                        System.out.println("Apri primo risultato");
                    }
                });
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        testo.setText("Nessun risultato");
                    }
                });
            }
        } else
            //se pagina canzone
            if (document.getElementsByClass("feature").size() > 0) {
                System.out.println("Prendo info :D");
                String title = document.getElementById("title_feature_div").child(0).text();
                String artist = document.getElementById("artistLink_feature_div").child(0).child(0).text();
                String album = document.getElementById("fromAlbum").child(0).text();
                String releaseDate = document.getElementById("ProductInfoReleaseDate").text();
                String genres = "";
                if(document.getElementById("productDetailsTable") == null){
                    getHtml("");
                    return;
                }
                Element genresElem = document.getElementById("productDetailsTable").child(0).child(0).child(0).child(1).child(0);
                for(int i = 0; i<genresElem.children().size(); i++){
                    if(genresElem.child(i).child(0).text().equals("Genres:")){
                        genresElem = genresElem.child(i).child(1).child(0);
                        break;
                    }
                }
                for(int i = 0; i<genresElem.children().size()-1; i++){
                    genres += genresElem.child(i).child(genresElem.child(i).children().size()-1).text();
                    genres += "/";
                }
                genres += genresElem.child(genresElem.children().size()-1).child(genresElem.child(genresElem.children().size()-1).children().size()-1).text();
                genres = genres.replaceAll(" & ", "/");
                String s = title + "\n";
                s += artist + "\n";
                s += album + "\n";
                s += releaseDate + "\n";
                s += genres + "\n";
                s += document.getElementById("coverArt_feature_div").child(0).attr("src");
                final String fs = s;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        testo.setText(fs);
                    }
                });

                URL newurl = null;
                try {
                    newurl = new URL(document.getElementById("coverArt_feature_div").child(0).attr("src"));
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                Bitmap albumCover = null;
                while(albumCover == null) {
                    try {
                        albumCover = BitmapFactory.decodeStream(newurl.openConnection().getInputStream());
                        System.out.println(albumCover.getHeight());
                        final Bitmap ac = albumCover;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                copertina.setImageBitmap(ac);
                            }
                        });
                        System.out.println("Copertina caricata");
                    } catch (IOException e) {
                        System.out.println("Errore caricamento compertina");
                    }
                }

                permessi.controlloPermessiScrittura();
                Song song = new Song(songFile, this);
                song.setTitolo(title);
                song.setArtista(artist);
                song.setAlbum(album);
                song.setGenere(genres);
                song.setData(releaseDate);
                song.setCopertina(albumCover);
                song.salva();
            }
            //se non ha ancora caricato la pagina
            else {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                webView.loadUrl("javascript:window.HTMLOUT.getHtml(document.body.innerHTML);");
                                System.out.println("Aspetto caricamento pagina");
                            }
                        });
                    }
                }, 100);
            }
    }

    public String removeExtension(String s){
        return s.substring(0, s.lastIndexOf("."));
    }

    public String filter(String s){
        s = s.toLowerCase();
        s = removeExtension(s);
        //parentesi tonde
        while(s.contains("[") && s.contains("]")){
            s = s.substring(0, s.indexOf("["))+s.substring(s.indexOf("]")+1, s.length());
        }
        //parentesi tonde
        String t = s;
        s = "";
        while(t.contains("(") && t.contains(")")){
            if(t.substring(t.indexOf("("), t.indexOf(")")).contains("feat") || t.substring(t.indexOf("("), t.indexOf(")")).contains("ft")){
                s += t.substring(0, t.indexOf(")")+1);
                t = t.substring(t.indexOf(")")+1, t.length());
            } else {
                t = t.substring(0, t.indexOf("("))+t.substring(t.indexOf(")")+1, t.length());
            }
        }
        s += t;

        String[] inutili = new String[]{"&", ",", "-", "_", "#", "\"", "\\(", "\\)", "official video", "official music", "official audio", "official", "remix", "remixes", "vs", "extended mix", "original mix", "radio edit", "youtube", "lyrics", "high quality", "hd", "  "};
        for(int i=0; i<inutili.length; i++){
            s = s.replaceAll(inutili[i], " ");
        }

        return s;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        webView.destroy();
        finish();
    }
}
