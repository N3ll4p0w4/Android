package instasaver.coolapp_engine.it.instasaver.Utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.arch.lifecycle.Lifecycle;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

import instasaver.coolapp_engine.it.instasaver.Activity.AccountActivity;
import instasaver.coolapp_engine.it.instasaver.R;

public class Account {

    private String username;
    private String nome;
    private String descrizione;
    private String sito;
    private Bitmap imgProfilo;

    private int nPost;
    private int nFollower;
    private int nFollowing;

    private boolean hasStories;

    private boolean isPrivate;

    private String lastUrl;
    private ArrayList<Bitmap> postPreviewImg = new ArrayList<>();
    private ArrayList<Integer> postPreviewImgVidOrSeq = new ArrayList<>();
    private ArrayList<View> postPreviewLayout = new ArrayList<>();

    private View searchResultView;

    public Account(){}

    public boolean setInformationFromAccountPage(String html){
        Document document = Jsoup.parse(html);

        String username, nome, descrizione, sito, nPost, nFollower, nFollowing;

        Element imgProfilo;

        try {
            username = document.getElementsByClass("_ienqf").get(0).child(0).text();

            int figlioHeader = 0;

            //Nome
            if(document.getElementsByClass("_tb97a").get(0).children().size() > figlioHeader &&
                    document.getElementsByClass("_tb97a").get(0).child(figlioHeader).className().compareTo("_kc4z2") == 0) {
                nome = document.getElementsByClass("_kc4z2").get(0).text();
                figlioHeader++;
            } else nome = "";
            //Descrizione
            if(document.getElementsByClass("_tb97a").get(0).children().size() > figlioHeader &&
                    document.getElementsByClass("_tb97a").get(0).child(figlioHeader).className().compareTo("") == 0) {
                descrizione = document.getElementsByClass("_tb97a").get(0).child(figlioHeader).child(0).text();
                figlioHeader++;
            } else descrizione = "";
            //Sito
            if(document.getElementsByClass("_ng0lj").size() > 0)
                sito = document.getElementsByClass("_ng0lj").get(0).text();
            else sito = "";

            //Post, Follower, Following
            nPost = document.getElementsByClass(" _573jb").get(0).child(0).child(0).text();
            nFollower = document.getElementsByClass(" _573jb").get(1).child(0).child(0).attr("title");
            nFollowing = document.getElementsByClass(" _573jb").get(2).child(0).child(0).text();

            if (document.getElementsByClass("_3xjwv").size() == 0) {
                imgProfilo = document.getElementsByClass("_mesn5").get(0).child(0).child(0).child(0);
                imgProfilo = imgProfilo.child(imgProfilo.children().size() - 1);
                imgProfilo = imgProfilo.child(0);
            } else {
                imgProfilo = document.getElementsByClass("_3xjwv").get(0).child(0);
            }
        } catch (Exception e) {
            //Errore
            return false;
        }

        this.setUsername(username);
        this.setNome(nome);
        this.setDescrizione(descrizione);

        if(sito != null)
            this.setSito(sito);
        else
            this.setSito("");

        this.setnPost(nPost);
        this.setnFollower(nFollower);
        this.setnFollowing(nFollowing);

        this.setImgProfilo(imgProfilo.attr("src"));

        if(document.getElementsByClass("_82odm _fu98p").size() <= 0)
            this.setHasStories(false);
        else this.setHasStories(true);

        if(document.getElementsByClass("_7r25s").size() > 0)
            this.setIsPrivate(true);
        else this.setIsPrivate(false);

        //Avvenuto con successo
        return true;
    }

    public String[] getPrivateAccountText(String html){
        Document document = Jsoup.parse(html);

        if(document.getElementsByClass("_7r25s").size() > 0){
            setIsPrivate(true);
        } else return null;

        String[] errors = null;

        Element privato = document.getElementsByClass("_7r25s").get(0);

        if(privato.children().size() > 0){
            errors = new String[privato.children().size()];
            for(int i=0; i<privato.children().size(); i++)
                errors[i] = privato.child(i).text();
        }

        return errors;
    }

    public boolean getPreviewPostsFromHtml(String html){
        Document document = Jsoup.parse(html);

        if(this.nPost == 0 || this.nPost <= postPreviewImg.size())
            return true;

        //Post Preview
        Elements previewPosts = document.getElementsByClass("_2di5p");

        int posNewPost = 0;
        if(lastUrl != null){
            for(posNewPost = 0; posNewPost<previewPosts.size(); posNewPost++)
                if (previewPosts.get(posNewPost).attr("src").equals(lastUrl))
                    break;
            posNewPost++;
        }

        if(previewPosts.size()-1 <= posNewPost)
            return false;

        //Post Elements
        Elements previewPostsExternal = document.getElementsByClass("_mck9w _gvoze  _tn0ps");

        for (int i=posNewPost; i<previewPosts.size(); i++){
            String url = previewPosts.get(i).attr("src");
            if(url.trim().isEmpty())
                return false;
            lastUrl = url;
            postPreviewImg.add(Utility.getBitmapFromUrl(url));

            //Video o sequenza immagini
            Element el = previewPostsExternal.get(i).child(0);
            if(el.children().size() > 1){
                //Video
                if(el.child(1).child(0).child(0).text().equals("Video"))
                    postPreviewImgVidOrSeq.add(1);
                    //Sequenza immagini
                else if(el.child(1).child(0).child(0).text().equals("Post"))
                    postPreviewImgVidOrSeq.add(2);
            } else postPreviewImgVidOrSeq.add(0);
        }

        return true;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getSito() {
        return sito;
    }

    public void setSito(String sito) {
        this.sito = sito;
    }

    public Bitmap getImgProfilo() {
        return imgProfilo;
    }

    public void setImgProfilo(Bitmap imgProfilo) {
        this.imgProfilo = imgProfilo;
    }

    public void setImgProfilo(String urlImgProfilo) {
        imgProfilo = Utility.getBitmapFromUrl(urlImgProfilo);
    }

    public int getnPost() {
        return nPost;
    }

    public void setnPost(int nPost) {
        this.nPost = nPost;
    }

    public void setnPost(String nPost) {
        nPost = nPost.replaceAll(",", "").replaceAll("\\.","");
        this.nPost = Integer.parseInt(nPost);
    }

    public int getnFollower() {
        return nFollower;
    }

    public void setnFollower(int nFollower) {
        this.nFollower = nFollower;
    }

    public void setnFollower(String nFollower) {
        nFollower = nFollower.replaceAll(",", "").replaceAll("\\.","");
        this.nFollower = Integer.parseInt(nFollower);
    }

    public int getnFollowing() {
        return nFollowing;
    }

    public void setnFollowing(int nFollowing) {
        this.nFollowing = nFollowing;
    }

    public void setnFollowing(String nFollowing) {
        nFollowing = nFollowing.replaceAll(",", "").replaceAll("\\.","");
        this.nFollowing = Integer.parseInt(nFollowing);
    }

    public boolean hasStories() {
        return hasStories;
    }

    public void setHasStories(boolean hasStories) {
        this.hasStories = hasStories;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setIsPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    public int getNPreviewPostsLoaded(){
        return postPreviewImg.size();
    }

    public View getSearchResultView(Activity activity){
        if(searchResultView == null){
            searchResultView = activity.getLayoutInflater().inflate(R.layout.search_result, null);
        }

        TextView username = searchResultView.findViewById(R.id.username);
        username.setText(this.getUsername());

        TextView nome = searchResultView.findViewById(R.id.nome);
        nome.setText(this.getNome());

        ImageView imgProfilo = searchResultView.findViewById(R.id.img_profilo);
        imgProfilo.setImageBitmap(this.getImgProfilo());

        searchResultView.setOnClickListener(View -> {
            Intent intent = new Intent(activity, AccountActivity.class);
            intent.putExtra("username", this.username);
            activity.startActivity(intent);
        });

        return searchResultView;
    }

    @SuppressLint("ClickableViewAccessibility")
    public ArrayList<View> getPreviewPostsViews(Activity activity){
        int nPostRiga = 3;
        int nLinearLayouts = postPreviewImg.size()%nPostRiga==0 ? postPreviewImg.size()/nPostRiga : postPreviewImg.size()/nPostRiga+1;
        for (int i=postPreviewLayout.size(); i<nLinearLayouts; i++)
            postPreviewLayout.add(activity.getLayoutInflater().inflate(R.layout.layout_post_preview_container, null));

        for (int i=0; i<postPreviewImg.size(); i++){
            LinearLayout linearLayout = (LinearLayout) postPreviewLayout.get(i/nPostRiga);
            RelativeLayout rl = (RelativeLayout) linearLayout.getChildAt(i%3);
            ImageView immagine = (ImageView) rl.getChildAt(0);
            immagine.setImageBitmap(postPreviewImg.get(i));

            //Video
            ImageView isVideo = (ImageView) rl.getChildAt(1);
            if(postPreviewImgVidOrSeq.get(i) == 1){
                isVideo.setVisibility(View.VISIBLE);
            } else isVideo.setVisibility(View.GONE);

            //Sequenza
            ImageView isSequence = (ImageView) rl.getChildAt(2);
            if(postPreviewImgVidOrSeq.get(i) == 2){
                isSequence.setVisibility(View.VISIBLE);
            } else isSequence.setVisibility(View.GONE);

            //Tocco scurisce
            immagine.setOnTouchListener((View view, MotionEvent event) -> {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        immagine.setColorFilter(activity.getResources().getColor(R.color.clickImage));
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_OUTSIDE:
                    case MotionEvent.ACTION_UP:
                        immagine.clearColorFilter();
                        break;
                }
                return true;
            });
        }

        return postPreviewLayout;
    }
}
