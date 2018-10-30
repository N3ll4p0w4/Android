package com.memegram.florian.amazein;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.memegram.florian.amazein.classi_gioco.Colorizzabile;
import com.memegram.florian.amazein.classi_gioco.GestoreColori;


public class MainActivity extends AppCompatActivity implements Colorizzabile{

    private RelativeLayout rl;

    @Override
    protected void onRestart() {
        super.onRestart();
        GestoreColori.genColoreRandom();
        settaColoriView(GestoreColori.getColore());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        int[] colori = getResources().getIntArray(R.array.colori);  // TUTTI I COLORI DALL XML
        GestoreColori.addColori(colori);
        GestoreColori.genColoreRandom();
        settaColoriView(GestoreColori.getColore());
    }

    public void gioca(View view) {
        startActivity(new Intent(this, ScenaGioco.class));
    }

    @Override
    public void settaColoriView(int colore) {

        rl = findViewById(R.id.scena_main);
        Button bottone = findViewById(R.id.gioca_button);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            Drawable drawableScena = rl.getBackground();
            drawableScena = DrawableCompat.wrap(drawableScena);
            DrawableCompat.setTint(drawableScena,colore);
            rl.setBackground(drawableScena);
        }

        bottone.setTextColor(GestoreColori.modifica(colore,2f));

    }
}
