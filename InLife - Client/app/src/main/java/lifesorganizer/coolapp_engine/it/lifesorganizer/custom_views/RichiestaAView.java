package lifesorganizer.coolapp_engine.it.lifesorganizer.custom_views;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import lifesorganizer.coolapp_engine.it.lifesorganizer.MyProfile;
import lifesorganizer.coolapp_engine.it.lifesorganizer.R;
import lifesorganizer.coolapp_engine.it.lifesorganizer.accounts.Account;
import lifesorganizer.coolapp_engine.it.lifesorganizer.accounts.AccountActivity;
import lifesorganizer.coolapp_engine.it.lifesorganizer.accounts.AccountManager;
import lifesorganizer.coolapp_engine.it.lifesorganizer.server.ServerAbstact;

public class RichiestaAView extends LinearLayout {

    View view;
    private Context context;
    private Account account;
    private MyProfile myProfile;

    public RichiestaAView(Context context) {
        super(context);
        this.context = context;
        inflater();
    }

    public RichiestaAView(Context context, Account account) {
        super(context);
        this.context = context;
        this.account = account;
        inflater();
    }

    public RichiestaAView(Context context, Account account, MyProfile myProfile) {
        super(context);
        this.context = context;
        this.account = account;
        this.myProfile = myProfile;
        inflater();
    }

    private void inflater(){
        LayoutInflater mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = mInflater.inflate(R.layout.account_richiesta_a, this, false);

        TextView username = view.findViewById(R.id.account_richiesta_a_layout_username);
        TextView name = view.findViewById(R.id.account_richiesta_a_layout_name);
        username.setText(account.getUsername());
        name.setText(account.getName());

        ImageButton decline = view.findViewById(R.id.account_richiesta_a_layout_delete_button);
        decline.setOnClickListener((View v) -> {
            //Scambio dati col server
            ServerAbstact sa = new ServerAbstact() {
                @Override
                public void run() {
                    invia("MyProfile");
                    invia("deleteRichiestaA");
                    invia(account.getId());
                    if(myProfile != null)
                        myProfile.refresh();
                }
            };
            sa.start();
        });

        view.setOnClickListener((View v) -> {
            Intent intent = null;
            if(account.getId().equals(AccountManager.mainAccount.getId())){
                intent = new Intent(context, MyProfile.class);
            } else {
                intent = new Intent(context, AccountActivity.class);
                intent.putExtra("idAccount", account.getId());
            }
            context.startActivity(intent);
        });

        this.removeAllViews();
        this.addView(view);
    }

    public void setAccount(Account account){
        this.account = account;
        inflater();
    }

    public Account getAccount(){
        return account;
    }
}
