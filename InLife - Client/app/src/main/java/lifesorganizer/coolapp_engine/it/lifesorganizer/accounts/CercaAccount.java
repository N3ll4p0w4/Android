package lifesorganizer.coolapp_engine.it.lifesorganizer.accounts;

import android.graphics.Bitmap;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;

import lifesorganizer.coolapp_engine.it.lifesorganizer.R;
import lifesorganizer.coolapp_engine.it.lifesorganizer.custom_views.AmicoView;
import lifesorganizer.coolapp_engine.it.lifesorganizer.server.ServerAbstact;
import lifesorganizer.coolapp_engine.it.lifesorganizer.utils.Utils;

public class CercaAccount extends AppCompatActivity {

    private TextInputEditText editText;
    private ServerAbstact serverAbstact = new ServerAbstact(){};
    private ArrayList<Account> accountsResults = new ArrayList<>();
    private LinearLayout risultatiLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cerca_account);

        editText = findViewById(R.id.cerca_account_edit_text);
        risultatiLayout = findViewById(R.id.cerca_account_risultati_layout);

        editText.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                if(s.length() > 0){
                    editText.setEnabled(false);
                    refreshResults(s.toString(), 15);
                } else {
                    risultatiLayout.removeAllViews();
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
    }

    public void refreshResults(String ricerca, int maxRisultati){
        serverAbstact.interrupt();
        //Scambio dati col server
        serverAbstact = new ServerAbstact() {
            @Override
            public void run() {

                invia("Account");
                invia("getAccountsStartWith");
                invia(ricerca);
                invia(maxRisultati+"");

                String accountsString = ricevi();
                ArrayList<String> accountsId = Utils.getStringsFromStringWithPuntoEVirgola(accountsString);

                accountsResults.clear();

                for(int i=0; i<accountsId.size(); i++){
                    Account a = AccountManager.getAccountById(accountsId.get(i));
                    if(a == null){
                        a = AccountManager.creaAccount(accountsId.get(i));
                    }
                    if(a.getUsername() == null) {
                        invia("Account");
                        invia("getAccountMinimal");
                        invia(a.getId());
                        a.minimalLoad(ricevi());
                    }
                    accountsResults.add(a);
                }

                refreshResultsUI();

                runOnUiThread(() -> {
                    editText.setEnabled(true);
                    Utils.mostraTastiera(CercaAccount.this);
                });
            }
        };
        serverAbstact.start();
    }

    public void refreshResultsUI() {
        runOnUiThread(() -> {
            risultatiLayout.removeAllViews();
        });
        for(int i=0; i<accountsResults.size(); i++){
            AmicoView risultatoView = new AmicoView(CercaAccount.this, accountsResults.get(i));
            runOnUiThread(() -> {
                risultatiLayout.addView(risultatoView);
                System.out.println(risultatoView.getAccount().getUsername()+" aggiunto");
            });
        }
    }
}
