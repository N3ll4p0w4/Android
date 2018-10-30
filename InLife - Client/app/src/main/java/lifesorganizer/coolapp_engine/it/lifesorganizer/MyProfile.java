package lifesorganizer.coolapp_engine.it.lifesorganizer;

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
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;
import lifesorganizer.coolapp_engine.it.lifesorganizer.accounts.Account;
import lifesorganizer.coolapp_engine.it.lifesorganizer.accounts.AccountManager;
import lifesorganizer.coolapp_engine.it.lifesorganizer.accounts.CercaAccount;
import lifesorganizer.coolapp_engine.it.lifesorganizer.custom_views.AmicoView;
import lifesorganizer.coolapp_engine.it.lifesorganizer.custom_views.RichiestaAView;
import lifesorganizer.coolapp_engine.it.lifesorganizer.custom_views.RichiestaDaView;
import lifesorganizer.coolapp_engine.it.lifesorganizer.server.ServerAbstact;
import lifesorganizer.coolapp_engine.it.lifesorganizer.utils.CropImage;
import lifesorganizer.coolapp_engine.it.lifesorganizer.utils.Utils;

public class MyProfile extends Activity {

    //Collapsing Toolbar
    private CollapsingToolbarLayout ctl;
    private ImageView ctlImage;

    private Account account;

    private LinearLayout mainLinearLayout;
    private View informazioni, amiciGroup;
    private View username, name, email, id;
    private View amiciText, richiesteDaText, richiesteAText;
    private View amici, richiesteDa, richiesteA;
    private View cerca;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        ctl = findViewById(R.id.myprofile_collapsingtoolbarlayout);
        ctlImage = findViewById(R.id.myprofile_collapsing_toolbar_image);

        refreshToolbarColor(((BitmapDrawable)ctlImage.getDrawable()).getBitmap());

        mainLinearLayout = findViewById(R.id.myprofile_mainlinearlayout);

        refresh();
    }

    public void refresh(){
        //Scambio dati col server
        ServerAbstact sa = new ServerAbstact() {
            @Override
            public void run() {
                invia("MyProfile");
                invia("getMyAccount");
                AccountManager.mainAccount = new Account();
                AccountManager.mainAccount.load(ricevi());
                account = AccountManager.mainAccount;

                ArrayList<Account> amici = account.getAmici();
                for(int i=0; i<amici.size(); i++){
                    if(amici.get(i).getUsername() == null) {
                        invia("Account");
                        invia("getAccountMinimal");
                        invia(amici.get(i).getId());
                        amici.get(i).minimalLoad(ricevi());
                    }
                }

                ArrayList<Account> richiesteDa = account.getRichiesteDa();
                for(int i=0; i<richiesteDa.size(); i++){
                    if(richiesteDa.get(i).getUsername() == null) {
                        invia("Account");
                        invia("getAccountMinimal");
                        invia(richiesteDa.get(i).getId());
                        richiesteDa.get(i).minimalLoad(ricevi());
                    }
                }

                ArrayList<Account> richiesteA = account.getRichiesteA();
                for(int i=0; i<richiesteA.size(); i++){
                    if(richiesteA.get(i).getUsername() == null) {
                        invia("Account");
                        invia("getAccountMinimal");
                        invia(richiesteA.get(i).getId());
                        richiesteA.get(i).minimalLoad(ricevi());
                    }
                }

                refreshAccountInfo();

                invia("MyProfile");
                invia("getMyImmagineProfilo");
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

            mainLinearLayout.removeAllViews();

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

            amiciText = getLayoutInflater().inflate(R.layout.myprofile_expandable_item, mainLinearLayout, false);
            changeItem(amiciText, "Amici:", account.getAmici().size()+"");
            mainLinearLayout.addView(amiciText);

            richiesteDaText = getLayoutInflater().inflate(R.layout.myprofile_expandable_item, mainLinearLayout, false);
            changeItem(richiesteDaText, "Richieste:", account.getRichiesteDa().size()+"");
            mainLinearLayout.addView(richiesteDaText);

            richiesteAText = getLayoutInflater().inflate(R.layout.myprofile_expandable_item, mainLinearLayout, false);
            changeItem(richiesteAText, "Richieste fatte:", account.getRichiesteA().size()+"");
            mainLinearLayout.addView(richiesteAText);

            amici = getLayoutInflater().inflate(R.layout.myprofile_expandable_group, mainLinearLayout, false);
            changeGroup(amici, "Amici");
            mainLinearLayout.addView(amici);

            ArrayList<Account> amici = account.getAmici();
            for (int i=0; i<amici.size(); i++){
                View amicoView = new AmicoView(MyProfile.this, amici.get(i));
                mainLinearLayout.addView(amicoView);
            }

            richiesteDa = getLayoutInflater().inflate(R.layout.myprofile_expandable_group, mainLinearLayout, false);
            changeGroup(richiesteDa, "Richieste");
            mainLinearLayout.addView(richiesteDa);

            ArrayList<Account> richiesteDa = account.getRichiesteDa();
            for (int i=0; i<richiesteDa.size(); i++){
                View richiestaDaView = new RichiestaDaView(MyProfile.this, richiesteDa.get(i), this);
                mainLinearLayout.addView(richiestaDaView);
            }

            richiesteA = getLayoutInflater().inflate(R.layout.myprofile_expandable_group, mainLinearLayout, false);
            changeGroup(richiesteA, "Richieste Fatte");
            mainLinearLayout.addView(richiesteA);

            ArrayList<Account> richiesteA = account.getRichiesteA();
            for (int i=0; i<richiesteA.size(); i++){
                View richiestaAView = new RichiestaAView(MyProfile.this, richiesteA.get(i), this);
                mainLinearLayout.addView(richiestaAView);
            }

            cerca = getLayoutInflater().inflate(R.layout.myprofile_expandable_group, mainLinearLayout, false);
            changeGroup(cerca, "Cerca");
            mainLinearLayout.addView(cerca);
            cerca.setOnClickListener((View v) -> {
                Intent intent = new Intent(MyProfile.this, CercaAccount.class);
                startActivity(intent);
            });
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

    private void setImmagineProfiloServer(Bitmap bitmap){
        //Scambio dati col server
        ServerAbstact sa = new ServerAbstact() {
            @Override
            public void run() {
                invia("MyProfile");
                invia("setMyImmagineProfilo");

                MaterialDialog.Builder mdb = new MaterialDialog.Builder(MyProfile.this)
                        .title(R.string.myprofile_uploading_picture)
                        .content(R.string.aspetta)
                        .progress(true, 0)
                        .cancelable(false)
                        .progressIndeterminateStyle(false);

                final MaterialDialog[] md = new MaterialDialog[1];

                runOnUiThread(()->{
                    md[0] = mdb.build();
                });

                runOnUiThread(()->{
                    md[0].show();
                });
                inviaImmagine(bitmap);
                runOnUiThread(()->{
                    md[0].dismiss();
                    Toasty.success(MyProfile.this, "Picture changed!", Toast.LENGTH_SHORT, true).show();
                });
            }
        };
        sa.start();
    }

    private void removeImmagineProfiloClient(){
        ctlImage.setImageDrawable(getDrawable(R.drawable.myprofile_immagine_profilo_default));
        refreshToolbarColor(((BitmapDrawable)ctlImage.getDrawable()).getBitmap());
    }

    private void removeImmagineProfiloServer(){
        //Scambio dati col server
        ServerAbstact sa = new ServerAbstact() {
            @Override
            public void run() {
                invia("MyProfile");
                invia("removeMyImmagineProfilo");
                runOnUiThread(()->{
                    Toasty.success(getApplicationContext(), "Picture removed...", Toast.LENGTH_SHORT, true).show();
                });
            }
        };
        sa.start();
    }

    public void cltImageClick(View view){
        new MaterialDialog.Builder(this)
            .title(R.string.myprofile_immagine_profilo)
            .items(R.array.myprofile_immagine_profilo_azioni)
            .negativeColor(getResources().getColor(R.color.flatui_pumpkin))
            .negativeText(R.string.annulla)
            .itemsCallback(new MaterialDialog.ListCallback() {
                @Override
                public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                    switch (which){
                        case 0:
                            getCropImage();
                            break;
                        case 1:
                            removeImmagineProfiloServer();
                            removeImmagineProfiloClient();
                            break;
                    }
                }
            })
            .show();
    }

    private void getCropImage(){
        Intent intent = new Intent(this, CropImage.class);
        startActivityForResult(intent, 100);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK)
            switch (requestCode){
                case 100:
                    String croppedImagePath = data.getStringExtra("croppedImagePath");
                    Bitmap croppedImage = Utils.tempFileToBitmap(croppedImagePath);
                    setImmagineProfiloServer(croppedImage);
                    setImmagineProfiloClient(croppedImage);
                    break;
            }
    }

}
