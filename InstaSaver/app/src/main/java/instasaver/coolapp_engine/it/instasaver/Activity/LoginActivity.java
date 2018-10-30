package instasaver.coolapp_engine.it.instasaver.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import instasaver.coolapp_engine.it.instasaver.R;
import instasaver.coolapp_engine.it.instasaver.Utils.Internet;
import instasaver.coolapp_engine.it.instasaver.Utils.Utility;

public class LoginActivity extends AppCompatActivity {

    private TextView errorText;
    private AutoCompleteTextView usernameView;
    private EditText passView;
    private Button loginButton;
    private ProgressBar progressBar;

    private WebView webView;

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    @SuppressLint("JavascriptInterface")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        errorText = findViewById(R.id.error_text);
        usernameView = findViewById(R.id.username);
        passView = findViewById(R.id.password);
        loginButton = findViewById(R.id.email_sign_in_button);
        progressBar = findViewById(R.id.login_progress);

        webView = findViewById(R.id.web_view);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setSavePassword(false);

        webView.getSettings().setUserAgentString("Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.4) Gecko/20100101 Firefox/4.0");
        webView.getSettings().setBlockNetworkImage(true);

        webView.addJavascriptInterface(this, "HTMLOUT");

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                webView.loadUrl("javascript:window.HTMLOUT.processHTML("+getString(R.string.get_body)+");");
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                if(!Internet.isConnected(LoginActivity.this))
                    errorText.setText(getString(R.string.error_no_connection));
                else
                    errorText.setText(getString(R.string.error_load_failed));

                errorText.setVisibility(View.VISIBLE);
            }
        });

        webView.loadUrl(getString(R.string.link_accedi));
        usernameView.setEnabled(false);
        passView.setEnabled(false);

        pref = getApplicationContext().getSharedPreferences(getResources().getString(R.string.credenziali), MODE_PRIVATE);
        usernameView.setText(pref.getString("username", ""));

        Utility.nascondiTastiera(this);
    }

    public void loginButtonPressed(View view){
        errorText.setVisibility(View.GONE);
        errorText.setText("");
        if(usernameView.getText().toString().trim().isEmpty())
            errorText.append(getString(R.string.error_invalid_username)+". ");
        if(passView.getText().toString().trim().isEmpty())
            errorText.append(getString(R.string.error_invalid_password)+". ");

        if(!Internet.isConnected(LoginActivity.this))
            errorText.setText(getString(R.string.error_no_connection));

        if(!errorText.getText().toString().isEmpty())
            errorText.setVisibility(View.VISIBLE);
        else {
            webView.loadUrl("javascript:" + getString(R.string.set_username) + "\"" + usernameView.getText() + "\";"+
                getString(R.string.set_password) + "\"" + passView.getText() + "\";"+
                getString(R.string.accedi));

            loginButton.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);

            usernameView.setEnabled(false);
            passView.setEnabled(false);

            Utility.nascondiTastiera(this);
        }
    }

    boolean caricata = false;
    @JavascriptInterface
    @SuppressWarnings("unused")
    public void processHTML(String html){
        Document document = Jsoup.parse(html);

        //Se caricata pagina principale
        if(document.getElementById("id_username") == null){

            //Salvo username
            editor = pref.edit();
            editor.putString("username", usernameView.getText().toString());
            editor.apply();

            if(!caricata) {
                caricata = true;

                Intent searchActivity = new Intent(this, SearchAccount.class);
                startActivity(searchActivity);

                webView.post(() -> {
                    webView.destroy();
                });

                this.finish();
            }
            return;
        }

        loginButton.post(() ->{
            loginButton.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            usernameView.setEnabled(true);
            passView.setEnabled(true);
        });
        //Errore login
        if(document.getElementById("alerts") != null){
            errorText.post(() -> {
                errorText.setText(document.getElementById("alerts").child(0).text());
                errorText.setVisibility(View.VISIBLE);
            });
        }
    }
}

