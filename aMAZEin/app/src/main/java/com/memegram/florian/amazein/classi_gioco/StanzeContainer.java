package com.memegram.florian.amazein.classi_gioco;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.memegram.florian.amazein.R;

import java.util.Random;

public class StanzeContainer implements Animabile{

    private Activity context;

    private int righe, colonne, minStanze, nStanze;
    private Stanza[][] stanze;
    private boolean deveCollegarsi;

    private Random random = new Random();
    private int percSpawnBauli = 20;

    private Stanza stanzaInizio;
    private Stanza stanzaArrivo;

    private Stanza currentStanza;

    private LinearLayout linearStanze;
    private RelativeLayout.LayoutParams lp;

    public StanzeContainer(int righe, int colonne, Activity context){
        this.righe = righe;
        this.colonne = colonne;
        minStanze = righe*colonne/2;
        this.context = context;

        nStanze = 0;

        stanze = new Stanza[righe][colonne];

        generaStanze();
    }



    private void generaStanze(){
        deveCollegarsi = false;
        generaStanza(0, 0);
        for(int i=0; i<righe; i++){
            for(int j=0; j<colonne; j++){
                if(stanze[i][j] == null){
                    deveCollegarsi = true;
                    generaStanza(i, j);
                }
            }
        }

        int r, c;
        r = random.nextInt(righe);
        c = random.nextInt(colonne);
        stanze[r][c].setPartenza(true);
        stanzaInizio = stanze[r][c];

        r = random.nextInt(righe);
        c = random.nextInt(colonne);
        while(stanze[r][c].isPartenza()){
            r = random.nextInt(righe);
            c = random.nextInt(colonne);
        }
        stanze[r][c].setArrivo(true);
        stanzaArrivo = stanze[r][c];
    }

    private void generaStanza(int riga, int colonna) {
        Stanza stanza = new Stanza();
        stanze[riga][colonna] = stanza;
        stanza.setRiga(riga);
        stanza.setColonna(colonna);

        //Sopra
        if (riga - 1 >= 0) {
            if (stanze[riga - 1][colonna] != null) {
                stanza.setSopra(stanze[riga - 1][colonna].hasSotto());
            } else stanza.setSopra(random.nextBoolean());
        } else stanza.setSopra(false);
        //Sotto
        if (riga + 1 < righe) {
            if (stanze[riga + 1][colonna] != null) {
                stanza.setSotto(stanze[riga + 1][colonna].hasSopra());
            } else stanza.setSotto(random.nextBoolean());
        } else stanza.setSotto(false);
        //Destra
        if (colonna + 1 < colonne) {
            if (stanze[riga][colonna + 1] != null) {
                stanza.setDestra(stanze[riga][colonna + 1].hasSinistra());
            } else stanza.setDestra(random.nextBoolean());
        } else stanza.setDestra(false);
        //Sinistra
        if (colonna - 1 >= 0) {
            if (stanze[riga][colonna - 1] != null) {
                stanza.setSinistra(stanze[riga][colonna - 1].hasDestra());
            } else stanza.setSinistra(random.nextBoolean());
        } else stanza.setSinistra(false);

        if(deveCollegarsi){
            for (int nRand=0; nRand<4; nRand++) {
                //Sopra
                if (nRand == 0 && !stanza.hasSopra() && riga - 1 >= 0 && stanze[riga - 1][colonna] != null) {
                    stanza.setSopra(true);
                    stanze[riga - 1][colonna].setSotto(true);
                    deveCollegarsi = false;
                    break;
                    //Sotto
                } else if (nRand == 1 && !stanza.hasSotto() && riga + 1 < righe && stanze[riga + 1][colonna] != null) {
                    stanza.setSotto(true);
                    stanze[riga + 1][colonna].setSopra(true);
                    deveCollegarsi = false;
                    break;
                    //Destra
                } else if (nRand == 2 && !stanza.hasDestra() && colonna + 1 < colonne && stanze[riga][colonna + 1] != null) {
                    stanza.setDestra(true);
                    stanze[riga][colonna + 1].setSinistra(true);
                    deveCollegarsi = false;
                    break;
                    //Sinistra
                } else if (nRand == 3 && !stanza.hasSinistra() && colonna - 1 >= 0 && stanze[riga][colonna - 1] != null) {
                    stanza.setSinistra(true);
                    stanze[riga][colonna - 1].setDestra(true);
                    deveCollegarsi = false;
                    break;
                }
            }
        }

        if(stanza.hasSopra()){
            if(stanza.getRiga()-1 >= 0 && stanze[stanza.getRiga()-1][stanza.getColonna()] == null)
                generaStanza(stanza.getRiga()-1, stanza.getColonna());
        }
        if(stanza.hasSotto()){
            if(stanza.getRiga()+1 < righe && stanze[stanza.getRiga()+1][stanza.getColonna()] == null)
                generaStanza(stanza.getRiga()+1, stanza.getColonna());
        }
        if(stanza.hasDestra()){
            if(stanza.getColonna()+1 < colonne && stanze[stanza.getRiga()][stanza.getColonna()+1] == null)
                generaStanza(stanza.getRiga(), stanza.getColonna()+1);
        }
        if(stanza.hasSinistra()){
            if(stanza.getColonna()-1 >= 0 && stanze[stanza.getRiga()][stanza.getColonna()-1] == null)
                generaStanza(stanza.getRiga(), stanza.getColonna()-1);
        }

        //Bauli
        int intRand = random.nextInt(100);
        while(intRand < percSpawnBauli){
            Baule baule = new Baule();
            stanza.addBaule(baule);
            intRand = random.nextInt(100);
        }
    }

    public int getRighe() {
        return righe;
    }

    public int getColonne() {
        return colonne;
    }

    public Stanza getCurrentStanza(){
        return currentStanza;
    }

    public void setCurrentStanza(Stanza currentStanza){
        this.currentStanza = currentStanza;
    }

    public Stanza getStanza(int riga, int colonna){
        return stanze[riga][colonna];
    }

    public Stanza getStanzaInizio(){
        return stanzaInizio;
    }

    public Stanza getStanzaArrivo(){
        return stanzaArrivo;
    }

    public Stanza getStanzaSopra() {
        return stanze[currentStanza.getRiga()-1][currentStanza.getColonna()];
    }

    public Stanza getStanzaSotto() {
        return stanze[currentStanza.getRiga()+1][currentStanza.getColonna()];
    }

    public Stanza getStanzaDestra() {
        return stanze[currentStanza.getRiga()][currentStanza.getColonna()+1];
    }

    public Stanza getStanzaSinistra() {
        return stanze[currentStanza.getRiga()][currentStanza.getColonna()-1];
    }

    public View getLinearStanze(int width, int height, int colorId, Context context){
        if(linearStanze == null) {
            //Linear Layout
            linearStanze = new LinearLayout(context);

            lp = new RelativeLayout.LayoutParams(width * colonne, height * colonne);
            lp.setMargins(0, 0, 0, 0);
            linearStanze.setLayoutParams(lp);
            linearStanze.setOrientation(LinearLayout.VERTICAL);

            for (int i = 0; i < righe; i++) {
                //Linear Layout Orizzontale per tutte le stanze
                LinearLayout lno = new LinearLayout(context);
                lno.setLayoutParams(new LinearLayout.LayoutParams(width * colonne, height));
                lno.setOrientation(LinearLayout.HORIZONTAL);

                for (int j = 0; j < colonne; j++) {
                    if (stanze[i][j] != null)
                        lno.addView(stanze[i][j].getViewStanza(0, 0, width, height, colorId, context));
                    else {
                        RelativeLayout pareteLayout = (RelativeLayout) View.inflate(context, R.layout.parete_layout, null);
                        RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(width, height);
                        pareteLayout.setLayoutParams(lp2);
                        lno.addView(pareteLayout);
                    }
                }

                linearStanze.addView(lno);
            }
        }
        return linearStanze;
    }

    public LinearLayout getLinearStanze() {
        return linearStanze;
    }

    public void setPositionLinearStanze(int x, int y){
        lp.setMargins(x, y, 0, 0);
        linearStanze.post(new Runnable() {
            @Override
            public void run() {
                linearStanze.setLayoutParams(lp);
            }
        });
    }

    public void moveLinearStanze(int deltaX, int deltaY){
        lp.setMargins(lp.leftMargin+deltaX, lp.topMargin+deltaY, 0, 0);
        linearStanze.post(new Runnable() {
            @Override
            public void run() {
                linearStanze.setLayoutParams(lp);
            }
        });
    }

    public void setLinearStanzeToCurrentStanza(){

        int width = getCurrentStanza().getViewStanza().getLayoutParams().width;
        int height = getCurrentStanza().getViewStanza().getLayoutParams().height;

        Display display = ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size);
        int screenWidth = size.x;
        int screenHeight = size.y;

        int x = -width*getCurrentStanza().getColonna()+screenWidth/2-width/2;
        int y = -height*getCurrentStanza().getRiga()+screenHeight/2-height/2;

        lp.setMargins(x, y, 0, 0);
        linearStanze.post(new Runnable() {
            @Override
            public void run() {
                linearStanze.setLayoutParams(lp);
            }
        });
    }

    public void setAnimazioneStanzeToCurrentStanza() {

        int width = getCurrentStanza().getViewStanza().getLayoutParams().width;
        int height = getCurrentStanza().getViewStanza().getLayoutParams().height;

        Display display = ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size);

        int screenWidth = size.x;
        int screenHeight = size.y;

        final int x = -width*getCurrentStanza().getColonna()+screenWidth/2-width/2;
        final int y = -height*getCurrentStanza().getRiga()+screenHeight/2-height/2;

        final int daX = lp.leftMargin;
        final int daY = lp.topMargin;

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float tempoInterpolazione, Transformation t) {
                lp.leftMargin = daX+(int)((x-daX) * tempoInterpolazione);
                lp.topMargin = daY+(int)((y-daY) * tempoInterpolazione);
                linearStanze.setLayoutParams(lp);
            }
        };

        a.setInterpolator(new AccelerateDecelerateInterpolator());
        a.setDuration(TEMPO_ANIMAZIONE); // in ms
        context.runOnUiThread(()-> linearStanze.startAnimation(a));

    }
}