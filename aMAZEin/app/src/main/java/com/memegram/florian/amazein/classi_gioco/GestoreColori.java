package com.memegram.florian.amazein.classi_gioco;

import android.graphics.Color;

public class GestoreColori {

    private static int color;
    private static int[] colori;

    public static void setColore(int colore) {
        color = colore;
    }

    public static int getColore() {
        return color;
    }

    public static int opacizza(int color,float quanto){
        int a = Color.alpha((int) (color/quanto));
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);

        return Color.argb(a,
                Math.min(r,255),
                Math.min(g,255),
                Math.min(b,255));
    }


    public static int modifica(int color, float modifica) {
        int a = Color.alpha(color);
        int r = (int) (Color.red(color) * modifica);
        int g = (int) (Color.green(color) * modifica);
        int b = (int) (Color.blue(color) * modifica);

        return Color.argb(a,
                Math.min(r,255),
                Math.min(g,255),
                Math.min(b,255));
    }

    public static void genColoreRandom() {
        color = colori[(int) (Math.random()*colori.length)];
    }

    public static void addColori(int[] coloris){
        colori = coloris;
    }


}
