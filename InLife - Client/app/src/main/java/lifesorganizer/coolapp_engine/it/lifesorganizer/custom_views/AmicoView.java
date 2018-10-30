package lifesorganizer.coolapp_engine.it.lifesorganizer.custom_views;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import lifesorganizer.coolapp_engine.it.lifesorganizer.MyProfile;
import lifesorganizer.coolapp_engine.it.lifesorganizer.R;
import lifesorganizer.coolapp_engine.it.lifesorganizer.accounts.Account;
import lifesorganizer.coolapp_engine.it.lifesorganizer.accounts.AccountActivity;
import lifesorganizer.coolapp_engine.it.lifesorganizer.accounts.AccountManager;

public class AmicoView extends LinearLayout {

    View view;
    private Context context;
    private Account account;
    private MyProfile myProfile;

    public AmicoView(Context context) {
        super(context);
        this.context = context;
        inflater();
    }

    public AmicoView(Context context, Account account) {
        super(context);
        this.context = context;
        this.account = account;
        inflater();
    }

    public AmicoView(Context context, Account account, MyProfile myProfile) {
        super(context);
        this.context = context;
        this.account = account;
        this.myProfile = myProfile;
        inflater();
    }

    private void inflater(){
        LayoutInflater mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = mInflater.inflate(R.layout.cerca_account_layout, this, false);

        TextView username = view.findViewById(R.id.cerca_account_layout_username);
        TextView name = view.findViewById(R.id.cerca_account_layout_name);
        username.setText(account.getUsername());
        name.setText(account.getName());

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
