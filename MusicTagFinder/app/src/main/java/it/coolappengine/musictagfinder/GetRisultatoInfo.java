package it.coolappengine.musictagfinder;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by pigro on 26/01/2018.
 */

public class GetRisultatoInfo extends Thread {

    private WebView webView;
    private String linkRisultato;

    private Activity activity;

    private String titolo;
    private String artista;
    private String album;
    private String genere;
    private String data;
    private Bitmap albumCover;

    private boolean loaded = false;

    public GetRisultatoInfo(String linkRisultato, Activity activity){
        this.linkRisultato = linkRisultato;
        this.activity = activity;
    }

    @SuppressLint("JavascriptInterface")
    @Override
    public void run() {

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

        webView.loadUrl(linkRisultato);
    }

    @JavascriptInterface
    @SuppressWarnings("unused")
    public void getHtml(String html){
        if(html == null)
            html = "";
        System.out.println("Caricamento...");
        final Document document = Jsoup.parse(html);
            //se pagina canzone
        if (document.getElementsByClass("feature").size() > 0) {
            System.out.println("Prendo info :D");
            titolo = document.getElementById("title_feature_div").child(0).text();
            artista = document.getElementById("artistLink_feature_div").child(0).child(0).text();
            album = document.getElementById("fromAlbum").child(0).text();
            data = document.getElementById("ProductInfoReleaseDate").text();
            genere = "";
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
                genere += genresElem.child(i).child(genresElem.child(i).children().size()-1).text();
                genere += "/";
            }
            genere += genresElem.child(genresElem.children().size()-1).child(genresElem.child(genresElem.children().size()-1).children().size()-1).text();
            genere = genere.replaceAll(" & ", "/");

            URL newurl = null;
            try {
                newurl = new URL(document.getElementById("coverArt_feature_div").child(0).attr("src"));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            albumCover = null;
            while(albumCover == null) {
                try {
                    albumCover = BitmapFactory.decodeStream(newurl.openConnection().getInputStream());
                    System.out.println(albumCover.getHeight());
                    final Bitmap ac = albumCover;
                    System.out.println("Copertina caricata");
                } catch (IOException e) {
                    System.out.println("Errore caricamento compertina");
                }
            }

            loaded = true;
        }
        //se non ha ancora caricato la pagina
        else {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                activity.runOnUiThread(new Runnable() {
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

    public boolean hasLoaded(){
        return loaded;
    }

    public String getTitolo() {
        return titolo;
    }

    public String getArtista(){
        return artista;
    }

    public String getAlbum() {
        return album;
    }

    public String getGenere() {
        return genere;
    }

    public String getData() {
        return data;
    }

    public Bitmap getAlbumCover() {
        return albumCover;
    }
}
