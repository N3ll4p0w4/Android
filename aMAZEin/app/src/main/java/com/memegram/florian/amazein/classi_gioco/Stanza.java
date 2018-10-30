package com.memegram.florian.amazein.classi_gioco;

import android.content.Context;
import android.os.Build;
import android.view.View;
import android.widget.RelativeLayout;

import com.memegram.florian.amazein.R;
import com.memegram.florian.amazein.ScenaGioco;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by pigro on 24/03/2018.
 */

public class Stanza {

    private Random random = new Random();

    private int riga, colonna;

    private boolean isPartenza = false, isArrivo = false;
    public boolean isVisited = false;

    private boolean sopra, sotto, destra, sinistra;

    private View viewStanza;
    private View viewStanzaMinimappa;

    private boolean hasBauli = false;
    private ArrayList<Baule> bauli = new ArrayList<>(0);
    private int bauliPerRiga = 7;
    private boolean[][] posBauli = new boolean[bauliPerRiga][bauliPerRiga];

    public Stanza(){}

    public int getRiga() {
        return riga;
    }

    public void setRiga(int riga){
        this.riga = riga;
    }

    public int getColonna() {
        return colonna;
    }

    public void setColonna(int colonna){
        this.colonna = colonna;
    }

    public boolean hasSopra() {
        return sopra;
    }

    public boolean isPartenza() {
        return isPartenza;
    }

    public void setPartenza(boolean partenza) {
        isPartenza = partenza;
    }

    public boolean isArrivo() {
        return isArrivo;
    }

    public void setArrivo(boolean arrivo) {
        isArrivo = arrivo;
    }

    public void setSopra(boolean sopra) {
        this.sopra = sopra;
    }

    public boolean hasSotto() {
        return sotto;
    }

    public void setSotto(boolean sotto) {
        this.sotto = sotto;
    }

    public boolean hasDestra() {
        return destra;
    }

    public void setDestra(boolean destra) {
        this.destra = destra;
    }

    public boolean hasSinistra() {
        return sinistra;
    }

    public void setSinistra(boolean sinistra) {
        this.sinistra = sinistra;
    }

    public void addBaule(Baule baule){
        bauli.add(baule);
        hasBauli = true;
        int riga, colonna;
        do {
            riga = random.nextInt(bauliPerRiga);
            colonna = random.nextInt(bauliPerRiga);
        } while (posBauli[riga][colonna]);
        posBauli[riga][colonna] = true;
    }

    public Baule getBaule(int pos){
        return bauli.get(pos);
    }

    public int getSizeBauli(){
        return bauli.size();
    }

    public boolean hasBauli(){
        return hasBauli;
    }

    public void setVisited(boolean visited) {
        isVisited = visited;
    }

    public View getViewStanza(int x, int y, int width, int height, int colorId, Context context){
        if(viewStanza == null) {
            RelativeLayout pareteLayout = (RelativeLayout) View.inflate(context, R.layout.parete_layout, null);

            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(width, height);
            lp.setMargins(x, y, 0, 0);
            pareteLayout.setLayoutParams(lp);

            viewStanza = pareteLayout;

            refreshView();

            /*
            for(int i=0; i<5; i++)
                ((ImageView)pareteLayout.getChildAt(i)).setColorFilter(GestoreColori.getColore(), PorterDuff.Mode.MULTIPLY);
                */
        }
        return viewStanza;
    }

    public View getViewStanza(){
        return viewStanza;
    }

    private void refreshView(){
        RelativeLayout pareteLayout = (RelativeLayout) viewStanza;

        pareteLayout.getChildAt(0).setVisibility(View.VISIBLE);
        if (this.isPartenza() || this.isArrivo()) {
        }

        pareteLayout.post(new Runnable() {
            @Override
            public void run() {
                if (!hasSopra())
                    pareteLayout.getChildAt(1).setVisibility(View.VISIBLE);
                else pareteLayout.getChildAt(1).setVisibility(View.INVISIBLE);
                if (!hasSotto())
                    pareteLayout.getChildAt(2).setVisibility(View.VISIBLE);
                else pareteLayout.getChildAt(2).setVisibility(View.INVISIBLE);
                if (!hasDestra())
                    pareteLayout.getChildAt(3).setVisibility(View.VISIBLE);
                else pareteLayout.getChildAt(3).setVisibility(View.INVISIBLE);
                if (!hasSinistra())
                    pareteLayout.getChildAt(4).setVisibility(View.VISIBLE);
                else pareteLayout.getChildAt(4).setVisibility(View.INVISIBLE);
            }
        });

        //Bauli
        int dimBaule = (ScenaGioco.dimStanza-ScenaGioco.dimPareti*2)/bauliPerRiga;
        int nBaule = 0;
        for (int i=0; i<bauliPerRiga; i++){
            for (int j=0; j<bauliPerRiga; j++){
                if(posBauli[i][j]){
                    int finalJ = j;
                    int finalI = i;
                    int finalNBaule = nBaule;
                    pareteLayout.post(new Runnable() {
                        @Override
                        public void run() {
                            pareteLayout.addView(bauli.get(finalNBaule).getViewBaule(ScenaGioco.dimPareti+finalJ *dimBaule, ScenaGioco.dimPareti+ finalI *dimBaule, dimBaule, ScenaGioco.context));
                        }
                    });
                    nBaule++;
                }
            }
        }
    }

    public View getViewMinimappaStanza(int x, int y, int width, int height, Context context){
        if(viewStanzaMinimappa == null) {
            RelativeLayout pareteLayout = (RelativeLayout) View.inflate(context, R.layout.minimappa_stanza_layout, null);

            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(width, height);
            lp.setMargins(x, y, 0, 0);
            pareteLayout.setLayoutParams(lp);

            viewStanzaMinimappa = pareteLayout;

            refreshViewMinimappa();
        }
        return viewStanzaMinimappa;
    }

    public View getViewMinimappaStanza(){
        return viewStanzaMinimappa;
    }

    public void refreshViewMinimappa(){
        RelativeLayout pareteLayout = (RelativeLayout) viewStanzaMinimappa;

        if (this.isPartenza() || this.isArrivo()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //((ImageView)(pareteLayout.getChildAt(0))).setColorFilter(Color.YELLOW, PorterDuff.Mode.MULTIPLY);
            }
        }
        pareteLayout.post(new Runnable() {
            @Override
            public void run() {
                if (isVisited) {
                    if (hasSopra())
                        pareteLayout.getChildAt(0).setVisibility(View.VISIBLE);
                    else pareteLayout.getChildAt(0).setVisibility(View.INVISIBLE);
                    if (hasSotto())
                        pareteLayout.getChildAt(1).setVisibility(View.VISIBLE);
                    else pareteLayout.getChildAt(1).setVisibility(View.INVISIBLE);
                    if (hasDestra())
                        pareteLayout.getChildAt(2).setVisibility(View.VISIBLE);
                    else pareteLayout.getChildAt(2).setVisibility(View.INVISIBLE);
                    if (hasSinistra())
                        pareteLayout.getChildAt(3).setVisibility(View.VISIBLE);
                    else pareteLayout.getChildAt(3).setVisibility(View.INVISIBLE);

                    if (hasSopra() && hasDestra())
                        pareteLayout.getChildAt(4).setVisibility(View.VISIBLE);
                    else pareteLayout.getChildAt(4).setVisibility(View.INVISIBLE);
                    if (hasDestra() && hasSotto())
                        pareteLayout.getChildAt(5).setVisibility(View.VISIBLE);
                    else pareteLayout.getChildAt(5).setVisibility(View.INVISIBLE);
                    if (hasSotto() && hasSinistra())
                        pareteLayout.getChildAt(6).setVisibility(View.VISIBLE);
                    else pareteLayout.getChildAt(6).setVisibility(View.INVISIBLE);
                    if (hasSinistra() && hasSopra())
                        pareteLayout.getChildAt(7).setVisibility(View.VISIBLE);
                    else pareteLayout.getChildAt(7).setVisibility(View.INVISIBLE);

                    if ((hasSopra() && hasSotto()) || (hasDestra() && hasSinistra()))
                        pareteLayout.getChildAt(8).setVisibility(View.VISIBLE);
                    else pareteLayout.getChildAt(8).setVisibility(View.INVISIBLE);
                }
            }
        });
    }
}
