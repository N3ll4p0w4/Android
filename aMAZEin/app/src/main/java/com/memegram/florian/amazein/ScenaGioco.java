package com.memegram.florian.amazein;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.memegram.florian.amazein.classi_gioco.Colorizzabile;
import com.memegram.florian.amazein.classi_gioco.GestoreColori;
import com.memegram.florian.amazein.classi_gioco.Minimappa;
import com.memegram.florian.amazein.classi_gioco.Personaggio;
import com.memegram.florian.amazein.classi_gioco.StanzeContainer;
import com.memegram.florian.amazein.controller.Joystick;
import com.memegram.florian.amazein.controller.JoystickListener;

public class ScenaGioco extends AppCompatActivity implements Colorizzabile {

    public static Context context;

    private Personaggio omino;

    private int righe = 7, colonne = 10;
    private static StanzeContainer stanzeContainer;
    private static Minimappa minimappa;
    public static int dimStanza, dimPareti, dimPorte;

    private RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scena);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        rimuoviBottoni();

        context = this;

        omino = new Personaggio(this, getResources().getDrawable(R.drawable.giallo_fermo));

        relativeLayout = findViewById(R.id.scena_gioco);

        settaColoriView(GestoreColori.getColore());

        stanzeContainer = new StanzeContainer(righe, colonne, this);
        stanzeContainer.setCurrentStanza(stanzeContainer.getStanzaInizio());
        stanzeContainer.getCurrentStanza().setVisited(true);

        dimStanza = pxFromDp(360);
        dimPareti = (int) (0.135*dimStanza);
        dimPorte = (int) (0.235*dimStanza);

        relativeLayout.addView(omino.getImmagine());

        minimappa = new Minimappa(stanzeContainer, relativeLayout, this);

        Joystick movimento = findViewById(R.id.joystick);
        movimento.setListener(new JoystickListener() {
            @Override
            public void onDown() {
                omino.setVelocita(0);
            }

            @Override
            public void onDrag(float degrees, float offset) {
                omino.setAngolo(-degrees+90);
                if(!omino.isAttaccando()) omino.ruota(-degrees+90);
                omino.setVelocita(offset);
            }

            @Override
            public void onUp() {
                omino.setVelocita(0);
            }
        });
        Joystick attacco = findViewById(R.id.attacco);
        attacco.setListener(new JoystickListener() {
            @Override
            public void onDown() {
                omino.setStaAttaccando(true);
            }

            @Override
            public void onDrag(float degrees, float offset) {
                omino.ruota(-degrees+90);
               //omino.setVelocita(offset);
            }

            @Override
            public void onUp() {
                omino.setStaAttaccando(false);
            }
        });

        new Thread(()->{
            final int FPS = 60;
            final long delay = 1000L /FPS;

            final int N_SPARI_PER_SECONDO = 1;
            final long FRX = FPS/N_SPARI_PER_SECONDO;

            int i = 0;

            while(true){
                omino.avanza(stanzeContainer, minimappa, dimStanza, dimPareti, dimPorte);
                /*
                if(omino.isAttaccando())
                if(i>=FRX){
                    runOnUiThread(()-> rlstanza.addView(omino.attacca(stanzeContainer, dimStanza, dimPareti, dimPorte)));
                    i=0;
                }i++;
                */
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                }
            }
        }).start();


        int idColore = GestoreColori.getColore();
        relativeLayout.addView(stanzeContainer.getLinearStanze(dimStanza, dimStanza, idColore, this), 0);
        stanzeContainer.setLinearStanzeToCurrentStanza();
    }

    @Override
    protected void onResume() {
        super.onResume();
        rimuoviBottoni();
    }


    public void rimuoviBottoni() {
        if(Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) {
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if(Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    public int dpFromPx(int px) {
        return (int) (px / this.getResources().getDisplayMetrics().density);
    }

    public int pxFromDp(int dp) {
        return (int) (dp * this.getResources().getDisplayMetrics().density);
    }

    @Override
    public void settaColoriView(int colore) {
        relativeLayout.setBackgroundColor(GestoreColori.modifica(GestoreColori.getColore(),1.2f));
    }
}
