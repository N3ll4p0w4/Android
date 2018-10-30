package instasaver.coolapp_engine.it.instasaver.Search;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import instasaver.coolapp_engine.it.instasaver.Utils.Account;
import instasaver.coolapp_engine.it.instasaver.R;
import instasaver.coolapp_engine.it.instasaver.Utils.Utility;
import instasaver.coolapp_engine.it.instasaver.Utils.WebViewPers;

public class UsernameFragment extends Fragment {

    public WebViewPers webViewPers;
    private View view;

    private Account account = new Account();

    private LinearLayout risultatiContainer;
    private AutoCompleteTextView usernameEditText;
    private Button findButton;
    private TextView testoError;
    private ProgressBar caricamentoRisultati;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_username, container, false);

        WebView webView = view.findViewById(R.id.web_view);

        webViewPers = new WebViewPers(webView);

        webViewPers.addJavascriptInterface(this, "Username");

        risultatiContainer = view.findViewById(R.id.username_results_container);
        usernameEditText = view.findViewById(R.id.username_edit_username);
        findButton = view.findViewById(R.id.username_find_button);
        testoError = view.findViewById(R.id.username_text);
        caricamentoRisultati = view.findViewById(R.id.username_loading_results);

        testoError.setVisibility(View.GONE);
        caricamentoRisultati.setVisibility(View.GONE);

        findButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cercaUsername();
            }
        });

        return view;
    }

    public void cercaUsername(){
        if(!usernameEditText.getText().toString().trim().isEmpty()) {
            webViewPers.removeOnFinish();
            webViewPers.stopLoading();
            webViewPers.setOnFinish("javascript:window.Username.getDatiAccount(" + getString(R.string.get_body) + ");", getContext());
            webViewPers.loadUrl("https://www.instagram.com/" + usernameEditText.getText().toString().trim().replaceAll(" ", "") + "/");
            Utility.nascondiTastiera(getActivity());
            view.post(() -> {
                risultatiContainer.removeAllViews();
                caricamentoRisultati.setVisibility(View.VISIBLE);
                testoError.setVisibility(View.GONE);
            });
        }
    }

    @JavascriptInterface
    @SuppressWarnings("unused")
    public void getDatiAccount(String html){
        Document document = Jsoup.parse(html);

        if(document.getElementsByClass("error-container -cx-PRIVATE-ErrorPage__errorContainer").size() > 0){
            System.out.println("Errore!");
            view.post(() ->{
                testoError.setVisibility(View.VISIBLE);
                caricamentoRisultati.setVisibility(View.GONE);
            });
            return;
        }

        if(!account.setInformationFromAccountPage(html)){
            //System.out.println("Username Fragment: Pagina non caricata del tutto");
            try {
                Thread.sleep(50);
            } catch (InterruptedException e2) {}
            webViewPers.loadUrl("javascript:window.Username.getDatiAccount("+getString(R.string.get_body)+");");
            return;
        }

        aggiornaRisultati();

        view.post(() -> {
            caricamentoRisultati.setVisibility(View.GONE);
        });

    }

    private void aggiornaRisultati(){
        View view = account.getSearchResultView(getActivity());
        this.view.post(() -> {
            risultatiContainer.addView(view);
        });
    }

}
