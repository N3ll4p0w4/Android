package com.memegram.florian.amazein.classi_gioco;

import android.widget.ImageView;

/**
 * Created by Florian on 01/04/2018.
 */

public interface Collisionabile {

    /**
     * Reazione di ogni oggetto collisionabile
     * @param angoloColpo
     * @param velocitaColpo
     * @param x0
     * @param y0
     * @param dimStanza
     * @param dimParete
     */
    public void reagisci(double angoloColpo, double velocitaColpo, int x0, int y0, int dimStanza, int dimParete);

    /**
     * Dice se é in collisione con un altro rettangolo
     * @param x
     * @param y
     * @param w
     * @param h
     * @return
     */
    public boolean isCollisione(int x, int y, int w ,int h);


    public int getX();
    public int getY();

    public int getW();
    public int getH();

    public ImageView getImage();

}
