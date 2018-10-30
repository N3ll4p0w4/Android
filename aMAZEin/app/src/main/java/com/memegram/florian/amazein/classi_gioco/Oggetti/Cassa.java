package com.memegram.florian.amazein.classi_gioco.Oggetti;

import android.widget.FrameLayout;
import android.widget.ImageView;

import com.memegram.florian.amazein.classi_gioco.Collisionabile;

/**
 * Created by Florian on 01/04/2018.
 */

public class Cassa implements Collisionabile {

    private int x,y,w,h;
    private ImageView immagine;
    private FrameLayout.LayoutParams parametri;

    public Cassa(int x, int y, int w, int h,ImageView immagine) {
        this.immagine = immagine;

        immagine.setX(x);
        immagine.setY(y);

        parametri = new FrameLayout.LayoutParams(w,h);

        this.immagine.setLayoutParams(parametri);
    }

    public Cassa(int w, int h, ImageView immagine) {
        this(w,h,w,h,immagine);
    }

    @Override
    public void reagisci(double angoloColpo, double velocitaColpo, int x0, int y0, int dimStanza, int dimParete) {

    }

    @Override
    public boolean isCollisione(int x, int y, int w, int h) {
        return false;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public int getW() {
        return w;
    }

    @Override
    public int getH() {
        return h;
    }

    @Override
    public ImageView getImage() {
        return immagine;
    }

}
