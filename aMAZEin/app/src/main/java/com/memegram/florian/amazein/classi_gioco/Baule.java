package com.memegram.florian.amazein.classi_gioco;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.memegram.florian.amazein.R;
import com.memegram.florian.amazein.ScenaGioco;

import java.util.Random;

/**
 * Created by pigro on 31/03/2018.
 */

public class Baule {

    private static final int[] PERC_BAULI = new int[]{60, 30, 10}; //legno, argento, oro
    private static final int[] DRAWABLE_BAULI = new int[]{R.drawable.baule_legno, R.drawable.baule_argento, R.drawable.baule_oro};
    private static final int[] DRAWABLE_BAULI_APERTI = new int[]{R.drawable.baule_legno_aperto, R.drawable.baule_argento_aperto, R.drawable.baule_oro_aperto};
    private Random random = new Random();

    private int tipoBaule;

    private ImageView immagine;

    private int width, height;

    private boolean isChiuso = true;

    private Context context;

    public Baule(){
        this.context = ScenaGioco.context;

        int maxPerc = 0;
        for (int i=0; i<PERC_BAULI.length; i++)
            maxPerc += PERC_BAULI[i];

        int n = random.nextInt(maxPerc);

        immagine = new ImageView(context);

        for (int i=0, percPrec = 0; i<PERC_BAULI.length; i++) {
            if (n < PERC_BAULI[i]+percPrec) {
                tipoBaule = i;
                break;
            }
            percPrec += PERC_BAULI[i];
        }
    }

    public void setX(int x) {
        immagine.setX(x);
    }

    public void setY(int y) {
        immagine.setY(y);
    }

    public int getX(){
        return (int) immagine.getX()+immagine.getWidth()*20/95;
    }

    public int getY(){
        if(!isChiuso) //Se aperto
            return (int) immagine.getY();
        else
            return (int) immagine.getY()+immagine.getHeight()-immagine.getHeight()*70/115;
    }

    public int getWidth(){
        return (int) immagine.getWidth()*65/95;
    }

    public int getHeight(){
        if(!isChiuso) //Se aperto
            return (int) immagine.getHeight();
        else
            return (int) immagine.getHeight()*60/115;
    }

    public void chiudi(){
        isChiuso = true;
        immagine.setImageDrawable(context.getResources().getDrawable(DRAWABLE_BAULI[tipoBaule]));
    }

    public void apri(){
        isChiuso = false;
        immagine.setImageDrawable(context.getResources().getDrawable(DRAWABLE_BAULI_APERTI[tipoBaule]));
    }

    public boolean isChiuso(){
        return isChiuso;
    }

    public int getTipoBaule(){
        return tipoBaule;
    }

    public View getViewBaule(int x, int y, int width, Context context){
        this.width = width;
        height = width*115/95;

        immagine.setLayoutParams(new RelativeLayout.LayoutParams(width, height));
        immagine.setX(x);
        immagine.setY(y);
        chiudi();

        return immagine;
    }

    public View getViewBaule(){
        return immagine;
    }
}
