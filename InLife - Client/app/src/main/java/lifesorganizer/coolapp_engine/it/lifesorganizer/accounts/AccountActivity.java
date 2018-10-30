package lifesorganizer.coolapp_engine.it.lifesorganizer.accounts;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.graphics.Palette;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import lifesorganizer.coolapp_engine.it.lifesorganizer.R;
import lifesorganizer.coolapp_engine.it.lifesorganizer.custom_views.AmicoView;
import lifesorganizer.coolapp_engine.it.lifesorganizer.server.ServerAbstact;

public class AccountActivity extends Activity {

    //Collapsing Toolbar
    private CollapsingToolbarLayout ctl;
    private ImageView ctlImage;

    private Account account;

    private LinearLayout mainLinearLayout;
    private View amiciText, inviaRichiestaButton, richiestaInviataButton;
    private View informazioni, amiciGroup;
    private View username, name, email, id;
    private View amici;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        ctl = findViewById(R.id.account_collapsingtoolbarlayout);
        ctlImage = findViewById(R.id.account_collapsing_toolbar_image);

        mainLinearLayout = findViewById(R.id.account_mainlinearlayout);

        amiciText = findViewById(R.id.account_amici_text);
        inviaRichiestaButton = findViewById(R.id.account_invia_richiesta_button);
        inviaRichiestaButton.setOnClickListener((View view) -> {
            //Scambio dati col server
            ServerAbstact sa = new ServerAbstact() {
                @Override
                public void run() {
                invia("MyProfile");
                invia("sendRichiesta");
                invia(account.getId());

                refresh();
                }
            };
            sa.start();
        });

        richiestaInviataButton = findViewById(R.id.account_richiesta_inviata_button);
        richiestaInviataButton.setOnClickListener((View view) -> {
            //Scambio dati col server
            ServerAbstact sa = new ServerAbstact() {
                @Override
                public void run() {
                invia("MyProfile");
                invia("deleteRichiestaA");
                invia(account.getId());

                refresh();
                }
            };
            sa.start();
        });

        refreshToolbarColor(((BitmapDrawable)ctlImage.getDrawable()).getBitmap());

        String idAccount = getIntent().getStringExtra("idAccount");
        account = new Account();
        account.setId(idAccount);

        refresh();
    }

    private void refresh(){
        //Scambio dati col server
        ServerAbstact sa = new ServerAbstact() {
            @Override
            public void run() {
                invia("Account");
                invia("getAccount");
                invia(account.getId());
                account.load(ricevi());

                ArrayList<Account> amici = account.getAmici();
                for(int i=0; i<amici.size(); i++){
                    if(amici.get(i).getUsername() == null) {
                        invia("Account");
                        invia("getAccountMinimal");
                        invia(amici.get(i).getId());
                        amici.get(i).minimalLoad(ricevi());
                    }
                }

                refreshAccountInfo();

                invia("Account");
                invia("getAccountImmagineProfilo");
                invia(account.getId());
                Bitmap immagineProfilo = riceviImmagine();
                setImmagineProfiloClient(immagineProfilo);

                refreshToolbarColor(immagineProfilo);
            }
        };
        sa.start();
    }

    public void refreshAccountInfo(){
        runOnUiThread(()-> {
            ctl.setTitle(account.getUsername());

            mainLinearLayout.removeViews(3, mainLinearLayout.getChildCount()-3);

            if(account.getAmici().contains(AccountManager.mainAccount)){
                inviaRichiestaButton.setVisibility(View.GONE);
                richiestaInviataButton.setVisibility(View.GONE);
                amiciText.setVisibility(View.VISIBLE);
            } else if(account.getRichiesteDa().contains(AccountManager.mainAccount)){
                inviaRichiestaButton.setVisibility(View.GONE);
                amiciText.setVisibility(View.GONE);
                richiestaInviataButton.setVisibility(View.VISIBLE);
            } else
                //MANCANO RICHIESTE DA
                {
                amiciText.setVisibility(View.GONE);
                richiestaInviataButton.setVisibility(View.GONE);
                inviaRichiestaButton.setVisibility(View.VISIBLE);
            }

            informazioni = getLayoutInflater().inflate(R.layout.myprofile_expandable_group, mainLinearLayout, false);
            changeGroup(informazioni, "Informazioni");
            mainLinearLayout.addView(informazioni);

            name = getLayoutInflater().inflate(R.layout.myprofile_expandable_item, mainLinearLayout, false);
            changeItem(name, "Nome:", account.getName());
            mainLinearLayout.addView(name);

            username = getLayoutInflater().inflate(R.layout.myprofile_expandable_item, mainLinearLayout, false);
            changeItem(username, "Username:", account.getUsername());
            mainLinearLayout.addView(username);

            email = getLayoutInflater().inflate(R.layout.myprofile_expandable_item, mainLinearLayout, false);
            changeItem(email, "Email:", account.getEmail());
            mainLinearLayout.addView(email);

            id = getLayoutInflater().inflate(R.layout.myprofile_expandable_item, mainLinearLayout, false);
            changeItem(id, "Id:", account.getId());
            mainLinearLayout.addView(id);

            amiciGroup = getLayoutInflater().inflate(R.layout.myprofile_expandable_group, mainLinearLayout, false);
            changeGroup(amiciGroup, "Amici");
            mainLinearLayout.addView(amiciGroup);

            amici = getLayoutInflater().inflate(R.layout.myprofile_expandable_item, mainLinearLayout, false);
            changeItem(amici, "Amici:", account.getAmici().size()+"");
            mainLinearLayout.addView(amici);

            ArrayList<Account> amici = account.getAmici();
            for (int i=0; i<amici.size(); i++){
                AmicoView amicoView = new AmicoView(AccountActivity.this, amici.get(i));
                mainLinearLayout.addView(amicoView);
            }
        });
    }

    private void changeGroup(View group, String title){
        TextView tx = group.findViewById(R.id.myprofile_expandable_group_title);
        tx.setText(title);
    }

    private void changeItem(View item, String var, String value){
        TextView tx = item.findViewById(R.id.myprofile_expandable_item_var);
        tx.setText(var);
        tx = item.findViewById(R.id.myprofile_expandable_item_value);
        tx.setText(value);
    }

    private void refreshToolbarColor(Bitmap bitmap){
        Window window = getWindow();
        Palette p =  new Palette.Builder(bitmap).generate();
        runOnUiThread(()-> {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            ctl.setContentScrimColor(p.getMutedColor(getResources().getColor(R.color.flatui_alizarin)));
            window.setStatusBarColor(p.getDarkMutedColor(getResources().getColor(R.color.flatui_alizarin)));
        });
    }

    private void setImmagineProfiloClient(Bitmap bitmap){
        runOnUiThread(()-> {
            ctlImage.setImageBitmap(bitmap);
        });
        refreshToolbarColor(bitmap);
    }

}
