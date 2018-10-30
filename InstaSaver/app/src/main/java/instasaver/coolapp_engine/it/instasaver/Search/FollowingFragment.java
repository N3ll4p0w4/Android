package instasaver.coolapp_engine.it.instasaver.Search;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;

import instasaver.coolapp_engine.it.instasaver.Utils.Account;
import instasaver.coolapp_engine.it.instasaver.R;
import instasaver.coolapp_engine.it.instasaver.Utils.SuffixTree;
import instasaver.coolapp_engine.it.instasaver.Utils.WebViewPers;

import static android.content.Context.MODE_PRIVATE;

public class FollowingFragment extends Fragment {

    private WebViewPers webViewPers;

    private View view;

    private EditText inputUsername;
    private LinearLayout containerFollowings;

    private Account myProfile = new Account();

    private SuffixTree followingProfiles = new SuffixTree();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_following, container, false);

        inputUsername = view.findViewById(R.id.input_username_name);
        containerFollowings = view.findViewById(R.id.following_container);


        inputUsername.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                aggiornaRisultati();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        WebView webView = view.findViewById(R.id.web_view);

        webViewPers = new WebViewPers(webView);

        webViewPers.addJavascriptInterface(this, "Following");

        webViewPers.setOnFinish("javascript:window.Following.getDatiAccount("+getString(R.string.get_body)+");", getContext());

        webViewPers.loadUrl(getString(R.string.link_main_page)+
                getActivity().getApplicationContext().getSharedPreferences(getResources().getString(R.string.credenziali), MODE_PRIVATE).getString("username", ""));

        setFollowingCount(0);

        return view;
    }

    @JavascriptInterface
    @SuppressWarnings("unused")
    public void getDatiAccount(String html){

        webViewPers.removeOnFinish();

        if(!myProfile.setInformationFromAccountPage(html)){
            //System.out.println("Following Fragment: Pagina non caricata del tutto");
            try {
                Thread.sleep(50);
            } catch (InterruptedException e2) {}
            webViewPers.loadUrl("javascript:window.Following.getDatiAccount("+getString(R.string.get_body)+");");
            return;
        }

        setFollowingCount(0);

        webViewPers.setOnFinish("javascript:window.Following.getFollowers("+getString(R.string.get_body)+");", getContext());

        webViewPers.loadUrl("javascript:"+getString(R.string.show_following)+";");
    }

    private int lastLoaded = 0;
    @JavascriptInterface
    @SuppressWarnings("unused")
    public void getFollowers(String html){
        Document document = Jsoup.parse(html);
        webViewPers.removeOnFinish();

        Elements followingElements = document.getElementsByClass("_6e4x5");
        Elements followingUsernames = document.getElementsByClass("_2nunc");
        Elements followingNames = document.getElementsByClass("_9mmn5");
        Elements followingImages = document.getElementsByClass("_rewi8");

        if(followingElements.size() < myProfile.getnFollowing()-1){

            for(int i=lastLoaded; i<followingElements.size()-1; i++){
                try {
                    String username, nome, urlImmagine;
                    username = followingUsernames.get(i).child(0).text();
                    nome = followingNames.get(i).text();
                    urlImmagine = followingImages.get(i).attr("src");

                    Account following = new Account();
                    following.setUsername(username);
                    following.setNome(nome);
                    following.setImgProfilo(urlImmagine);

                    followingProfiles.insertByNome(following);
                    followingProfiles.insertByUsername(following);

                    setFollowingCount(i);
                    //aggiornaRisultati();

                    lastLoaded++;

                } catch (Exception e) {
                    break;
                }
            }

            //System.out.println("FollowingFragment: Pagina non caricata del tutto");
            webViewPers.scrollBy(0, 5000);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e2) {}
            webViewPers.loadUrl("javascript:window.Following.getFollowers("+getString(R.string.get_body)+");");
            return;
        }

        for(int i=lastLoaded; i<followingElements.size(); i++){
            try {
                String username, nome, urlImmagine;
                username = followingUsernames.get(i).child(0).text();
                nome = followingNames.get(i).text();
                urlImmagine = followingImages.get(i).attr("src");

                Account following = new Account();
                following.setUsername(username);
                following.setNome(nome);
                following.setImgProfilo(urlImmagine);
                followingProfiles.insertByUsername(following);

                setFollowingCount(i);
                //aggiornaRisultati();

                lastLoaded++;

            } catch (Exception e) {
                break;
            }
        }

        setFollowingCount(lastLoaded);

        ProgressBar loaderCounter = view.findViewById(R.id.loader_counter);
        view.post(() -> {
            loaderCounter.setVisibility(View.GONE);
        });
    }

    private void aggiornaRisultati(){
        TextView notShowed = containerFollowings.findViewById(R.id.following_not_showed);
        if(!inputUsername.getText().toString().isEmpty()) {
            containerFollowings.post(() -> {
                containerFollowings.removeViews(0, containerFollowings.getChildCount()-1);
            });
            ArrayList<Account> accounts = followingProfiles.getAccountStartWith(inputUsername.getText().toString());
            for (int i = 0; i < accounts.size(); i++) {
                //int finalI = i;
                View view = accounts.get(i).getSearchResultView(getActivity());
                containerFollowings.post(() -> {
                    try {
                        containerFollowings.addView(view, containerFollowings.getChildCount()-1);
                    } catch (Exception e){}
                });
            }
            containerFollowings.post(() -> {
                notShowed.setVisibility(View.VISIBLE);
            });
        } else {
            containerFollowings.post(() -> {
                containerFollowings.removeViews(0, containerFollowings.getChildCount()-1);
                notShowed.setVisibility(View.GONE);
            });
        }
    }

    @SuppressLint("SetTextI18n")
    private void setFollowingCount(int i){
        TextView loading = view.findViewById(R.id.counter_following);
        loading.post(() ->  {
            loading.setText(getString(R.string.following_loading_1)+" "+i+
                    " "+getString(R.string.following_loading_2)+" "+myProfile.getnFollowing());
        });
    }
}
