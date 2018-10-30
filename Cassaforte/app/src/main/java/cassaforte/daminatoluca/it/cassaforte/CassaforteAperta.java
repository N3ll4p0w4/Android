package cassaforte.daminatoluca.it.cassaforte;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class CassaforteAperta extends AppCompatActivity {

    private RelativeLayout fLayout;
    private TextView risparmi;
    private EditText nuovaPassword;
    private Spinner colorSpinner;
    private ArrayAdapter<CharSequence> adapter;

    private int minLength = 4, maxLength = 8;
    private int minRisparmi = 0, maxRisparmi = 10000000;
    private int diffRisparmi = 100000;

    private SharedPreferences prefs;
    private SharedPreferences.Editor prefsEditor;

    private int idColoreBg;

    private String color = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cassaforte_aperta);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        idColoreBg = GetColor.getColorPreference(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, idColoreBg));
        }
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(idColoreBg)));

        prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        prefsEditor = prefs.edit();
        if(!prefs.contains("risparmi")){
            prefsEditor.putInt("risparmi", diffRisparmi);
            prefsEditor.apply();
        }
        if(!prefs.contains("colore")){
            prefsEditor.putString("colore", "Default");
            prefsEditor.apply();
        }

        fLayout = (RelativeLayout) findViewById(R.id.layoutCassAperta);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            fLayout.setBackground(new ColorDrawable(getResources().getColor(idColoreBg)));
        }

        risparmi = (TextView) findViewById(R.id.risparmi);
        nuovaPassword = (EditText) findViewById(R.id.nuovaPass);
        colorSpinner = (Spinner) findViewById(R.id.selezionaColore);
        adapter = ArrayAdapter.createFromResource(this, R.array.array_colori, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        colorSpinner.setAdapter(adapter);
        colorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                color = parent.getItemAtPosition(position)+"";
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                color = "Default";
            }
        });

        aggiornaSoldi();

        setButtonAnimation(findViewById(R.id.bAumenta), R.drawable.pulsante_dentro_piccolo, R.drawable.pulsante_dentro_grande);
        setButtonAnimation(findViewById(R.id.bDiminuisci), R.drawable.pulsante_dentro_piccolo, R.drawable.pulsante_dentro_grande);
        setButtonAnimation(findViewById(R.id.salvaButton), R.drawable.pulsante_cassaforte_piccolo, R.drawable.pulsante_cassaforte_grande);
    }

    public void aggiornaSoldi(){
        String risparmiNum = ""+prefs.getInt("risparmi", diffRisparmi);
        System.out.println(risparmiNum);
        String s = "";
        for(int i=risparmiNum.length(); i >= 0;){
            if(i-3 > 0){
                s = "."+risparmiNum.substring(i-3, i)+s;
                risparmiNum = risparmiNum.substring(0, i-3);
            }
            i-=3;
        }
        s = risparmiNum+s;
        risparmi.setText("Soldi: "+s);
    }

    public void aumentaButton(View view){
        int tmp = prefs.getInt("risparmi", diffRisparmi);
        if(tmp < maxRisparmi){
            prefsEditor.putInt("risparmi", tmp+diffRisparmi);
            prefsEditor.apply();
        }
        aggiornaSoldi();
    }

    public void diminuisciButton(View view){
        int tmp = prefs.getInt("risparmi", diffRisparmi);
        if(tmp > minRisparmi){
            prefsEditor.putInt("risparmi", tmp-diffRisparmi);
            prefsEditor.apply();
        }
        aggiornaSoldi();
    }

    public void salvaButton(View view){
        if(nuovaPassword.getText().length() < minLength || nuovaPassword.getText().length() > maxLength){
            Toast.makeText(this, "La password deve essere lunga dai 4 agli 8 caratteri", Toast.LENGTH_SHORT).show();
        } else {
            prefsEditor.putString("password", nuovaPassword.getText().toString());
        }
        prefsEditor.putString("colore", color);
        prefsEditor.apply();
        Toast.makeText(this, "Salvato!", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, CassaforteAperta.class));
        finish();
    }

    public void setButtonAnimation(View button, final int drPiccolo, final int drGrande){
        button.setOnTouchListener(new View.OnTouchListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    ((Button)v).setBackground(getResources().getDrawable(drPiccolo));
                    //Se ritornassi true non farebbe onClick
                    //return true;
                } else if(event.getAction() == MotionEvent.ACTION_UP  || event.getAction() == MotionEvent.ACTION_CANCEL){
                    ((Button)v).setBackground(getResources().getDrawable(drGrande));
                    //return true;
                }
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
