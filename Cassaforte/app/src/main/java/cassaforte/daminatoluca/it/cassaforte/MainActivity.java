package cassaforte.daminatoluca.it.cassaforte;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout fLayout;
    private TextView codice;
    private GridLayout gl;

    private String password;
    private int idColoreBg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        idColoreBg = GetColor.getColorPreference(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, idColoreBg));
        }
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(idColoreBg)));

        fLayout = (RelativeLayout) findViewById(R.id.layoutMainActivity);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            fLayout.setBackground(new ColorDrawable(getResources().getColor(idColoreBg)));
        }

        codice = (TextView) findViewById(R.id.codice);

        //Setto l'animazione dei bottoni
        gl = (GridLayout) findViewById(R.id.gridLayout);
        for(int i=0; i<gl.getChildCount(); i++){
            setButtonAnimation(gl.getChildAt(i));
        }

        password = getSharedPreferences("prefs", MODE_PRIVATE).getString("password", "1234");
    }

    public void bottoniNumeri(View w){
        int maxLenght = 8;
        if(codice.getText().toString().replaceAll(" ", "").length() < maxLenght) {
            if (!codice.getText().toString().isEmpty())
                codice.setText(codice.getText().toString() + " ");
            codice.setText(codice.getText().toString() + ((Button) w).getText());
            checkPassword();
        } else {
            Toast.makeText(this, "Lunghezza massima raggiunta", Toast.LENGTH_SHORT).show();
        }
    }

    public void bottoneCancellaTutto(View w){
        codice.setText("");
    }

    public void bottoneCancella(View w){
        if(!codice.getText().toString().isEmpty()){
            if(codice.getText().toString().length() == 1)
                codice.setText("");
            else {
                String old = codice.getText().toString();
                codice.setText(old.substring(0, old.lastIndexOf(" ")));
            }
        }
    }

    public void setButtonAnimation(View button){
        button.setOnTouchListener(new View.OnTouchListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    ((Button)v).setBackground(getResources().getDrawable(R.drawable.pulsante_cassaforte_piccolo));
                    //Se ritornassi true non farebbe onClick
                    //return true;
                } else if(event.getAction() == MotionEvent.ACTION_UP  || event.getAction() == MotionEvent.ACTION_CANCEL){
                    ((Button)v).setBackground(getResources().getDrawable(R.drawable.pulsante_cassaforte_grande));
                    //return true;
                }
                return false;
            }
        });
    }

    public void checkPassword(){
        if(password.equals(codice.getText().toString().replaceAll(" ", ""))){
            codice.setTextColor(getResources().getColor(R.color.verdeChiaro));
            for(int i=0; i<gl.getChildCount(); i++)
                gl.getChildAt(i).setEnabled(false);
            Toast.makeText(this, "Password trovata!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, CassaforteAperta.class));
            finish();
        } else {
            codice.setTextColor(getResources().getColor(R.color.rosso));
        }
    }
}
