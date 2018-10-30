package instasaver.coolapp_engine.it.instasaver.Activity;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.*;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import instasaver.coolapp_engine.it.instasaver.R;
import instasaver.coolapp_engine.it.instasaver.Utils.Account;
import instasaver.coolapp_engine.it.instasaver.Utils.WebViewPers;

public class AccountActivity extends AppCompatActivity {

    private Account account = new Account();

    private WebViewPers webViewPers;

    private LinearLayout contenitoreLayout;
    private RelativeLayout infoAccountLayout;
    private LinearLayout pffAccountLayout, privatoLayout, postPreviewContainerLayout;
    private TextView usernameTv, nomeTv, descrizioneTv, sitoTv;
    private CircleImageView immagineProfilo;
    private TextView nPostTv, nFollowersTv, nFollowingTv;
    private Button loadMorePosts;
    private ProgressBar caricamentoPagina;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        account.setUsername(getIntent().getStringExtra("username"));

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setTitle(account.getUsername());
        } else {
            getActionBar().setTitle(account.getUsername());
            getSupportActionBar().setTitle(account.getUsername());
        }

        contenitoreLayout = findViewById(R.id.account_layout_contenitore);
        infoAccountLayout = findViewById(R.id.account_info_layout);
        pffAccountLayout = findViewById(R.id.account_pff_layout);
        privatoLayout = findViewById(R.id.account_private_layout);
        postPreviewContainerLayout = findViewById(R.id.account_layout_post_preview);
        usernameTv = findViewById(R.id.account_username);
        nomeTv = findViewById(R.id.account_nome);
        descrizioneTv = findViewById(R.id.account_descrizione);
        sitoTv = findViewById(R.id.account_sito);
        immagineProfilo = findViewById(R.id.account_img_profilo);
        nPostTv = findViewById(R.id.account_n_post);
        nFollowersTv = findViewById(R.id.account_n_followers);
        nFollowingTv = findViewById(R.id.account_n_followings);
        loadMorePosts = findViewById(R.id.account_load_more_post_button);
        caricamentoPagina = findViewById(R.id.account_loading);

        infoAccountLayout.setVisibility(View.GONE);
        pffAccountLayout.setVisibility(View.GONE);
        privatoLayout.setVisibility(View.GONE);
        loadMorePosts.setVisibility(View.GONE);
        caricamentoPagina.setVisibility(View.VISIBLE);

        WebView webView = findViewById(R.id.web_view);

        webViewPers = new WebViewPers(webView);

        webViewPers.addJavascriptInterface(this, "AccountActivity");

        webViewPers.setOnFinish("javascript:window.AccountActivity.getDatiAccount("+getString(R.string.get_body)+");", this);

        webViewPers.loadUrl(getString(R.string.link_main_page)+account.getUsername());
    }

    @JavascriptInterface
    @SuppressWarnings("unused")
    public void getDatiAccount(String html){

        webViewPers.removeOnFinish();

        if(!account.setInformationFromAccountPage(html)){
            //System.out.println("AccountActivity: Pagina non caricata del tutto");
            try {
                Thread.sleep(50);
            } catch (InterruptedException e2) {}
            webViewPers.loadUrl("javascript:window.AccountActivity.getDatiAccount("+getString(R.string.get_body)+");");
            return;
        }

        contenitoreLayout.post(() -> {
            usernameTv.setText(account.getUsername());
            nomeTv.setText(account.getNome());
            descrizioneTv.setText(account.getDescrizione());
            sitoTv.setText(account.getSito());
            immagineProfilo.setImageBitmap(account.getImgProfilo());
            if(account.hasStories()) {
                immagineProfilo.setBorderColor(getResources().getColor(R.color.colorAccent));
                immagineProfilo.setBorderWidth(5);
            }
            nPostTv.setText(""+account.getnPost());
            nFollowersTv.setText(""+account.getnFollower());
            nFollowingTv.setText(""+account.getnFollowing());

            infoAccountLayout.setVisibility(View.VISIBLE);
            pffAccountLayout.setVisibility(View.VISIBLE);
        });

        //Privato
        if(account.isPrivate()) {
            String[] errors = account.getPrivateAccountText(html);
            TextView text1 = (TextView) privatoLayout.getChildAt(0);
            TextView text2 = (TextView) privatoLayout.getChildAt(1);
            contenitoreLayout.post(() -> {
                text1.setText(errors[0]);
                text2.setText(errors[1]);
                privatoLayout.setVisibility(View.VISIBLE);
                caricamentoPagina.setVisibility(View.GONE);
            });

            return;
        }

        webViewPers.loadUrl("javascript:window.AccountActivity.getPreviewPosts("+getString(R.string.get_body)+");");
    }

    @JavascriptInterface
    @SuppressWarnings("unused")
    public void getPreviewPosts(String html) {

        boolean result = account.getPreviewPostsFromHtml(html);

        System.out.println("Risultato = "+result);

        if (!result) {
            System.out.println("AccountActivity: Pagina non caricata del tutto");
            try {
                Thread.sleep(50);
            } catch (InterruptedException e2) {}
            webViewPers.scrollBy(0, 200);
            webViewPers.loadUrl("javascript:window.AccountActivity.getPreviewPosts(" + getString(R.string.get_body) + ");");
            return;
        }

        ArrayList<View> postPreviews = account.getPreviewPostsViews(this);

        for (int i = 0; i < postPreviews.size(); i++) {
            int finalI = i;
            postPreviewContainerLayout.post(() -> {
                try {
                    postPreviewContainerLayout.addView(postPreviews.get(finalI));
                } catch (Exception e){}
            });
        }

        caricamentoPagina.post(() -> {
            caricamentoPagina.setVisibility(View.GONE);
        });

        if(account.getnPost() <= 0 || account.getnPost() > account.getNPreviewPostsLoaded()){
            loadMorePosts.post(() -> {
                loadMorePosts.setVisibility(View.VISIBLE);
            });
        }
    }

    public void loadMorePreviewPosts(View view){
        loadMorePosts.setVisibility(View.GONE);
        caricamentoPagina.setVisibility(View.VISIBLE);
        webViewPers.scrollBy(0, 400);
        try {
            Thread.sleep(150);
        } catch (InterruptedException e2) {}
        webViewPers.loadUrl("javascript:window.AccountActivity.getPreviewPosts("+getString(R.string.get_body)+");");
    }
}
