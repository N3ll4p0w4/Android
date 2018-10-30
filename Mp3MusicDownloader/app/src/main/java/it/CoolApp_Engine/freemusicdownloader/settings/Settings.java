package it.CoolApp_Engine.freemusicdownloader.settings;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import it.CoolApp_Engine.freemusicdownloader.freemusicdownloader.R;

/**
 * Created by pigro on 02/01/2018.
 */

public class Settings extends AppCompatActivity {

    private static Activity activity;
    private static Context context;

    private static SharedPreferences pref;
    private static SharedPreferences.Editor editor;


    private static TextView textPath;
    private TextView scritta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFrafment()).commit();

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        activity = this;
        context = this.getApplicationContext();


        pref = context.getApplicationContext().getSharedPreferences(context.getResources().getString(R.string.shredPrefsImpostazioni), MODE_PRIVATE);
        editor = pref.edit();

        setContentView(R.layout.settings_chose_dir);
        textPath = (TextView)findViewById(R.id.path);
        scritta = (TextView)findViewById(R.id.scritta);
        scritta.setText(R.string.pathDownloadFileScritta);
        aggiorna();
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    public static void aggiorna(){
        String path = pref.getString(context.getResources().getString(R.string.pathDownloadFile), null);
        textPath.setText(path);
    }



    public static class SettingsFrafment extends PreferenceFragment {

        @Override
        public void onCreate(final Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferenze_impostazioni);

            Preference chooseDir = (Preference) findPreference("choseDir");
            chooseDir.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    startActivityForResult(Intent.createChooser(intent, "Select directory:"),99);
                    return true;
                }
            });

            Preference checkPermission = (Preference) findPreference("checkPermission");
            checkPermission.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    Permessi permessi = new Permessi(activity);
                    if(permessi.controlloPermessiScrittura()){
                        Toast.makeText(context, getString(R.string.permessoConcesso), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, getString(R.string.permessoNegato), Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
            });
        }

        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            if(resultCode != RESULT_OK) return;
            if(requestCode == 99){
                Uri uri = data.getData();
                if (uri != null){
                    String path = uri.getPath();
                    File dirDest = null;
                    //Memoria interna
                    if(path.contains("primary")){
                        path = path.substring(path.indexOf(":")+1);
                        dirDest = new File(Environment.getExternalStorageDirectory(), path);
                    }
                    //Scheda sd
                    else {
                        path = path.substring("/tree".length());
                        path = path.replace(":", "/");
                        path = "/storage"+path;
                        dirDest = new File(path);
                    }
                    editor.putString(getResources().getString(R.string.pathDownloadFile), dirDest.getPath());
                    editor.apply();
                    //Toast.makeText(context, uri.getPath(), Toast.LENGTH_SHORT).show();
                    aggiorna();
                }
            }
        }
    }
}
