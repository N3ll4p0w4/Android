package com.memegram.florian.amazein.classi_gioco;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.view.Display;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.memegram.florian.amazein.R;

import java.util.ArrayList;

/**
 * Created by Florian on 24/03/2018.
 */

public class Personaggio implements Animabile{

    private int vita = 100;
    private int vel_movimento =10;
    private double angolo;

    private FrameLayout.LayoutParams parametri;
    private ImageView immagine;
    private float velocitaAttacco = 0f;

    private int screenWidth, screenHeight;
    private int minX, maxX;
    private int minY, maxY;
    private int centroX,centroY;
    private boolean inTransizione = false, isAttaccando = false;

    private Activity main;

    public Personaggio(Activity main, Drawable drawable) {
        this.main = main;
        immagine = new ImageView(this.main);
        immagine.setImageDrawable(drawable);
        parametri = new FrameLayout.LayoutParams(100, 100);
        immagine.setLayoutParams(parametri);

        Display display = this.main.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size);
        screenWidth = size.x;
        screenHeight = size.y;

        immagine.setScaleY(-1);

        centroX = screenWidth/2-parametri.width/2;
        centroY = screenHeight/2-parametri.height/2;

        immagine.setX(centroX);
        immagine.setY(centroY);
    }

    public ImageView getImmagine() {
        return immagine;
    }

    public int getVelocitaAttacco(){
        return vel_movimento;
    }

    public void setAngolo(float angolo) {
        this.angolo = angolo;
    }


    public ImageView attacca(StanzeContainer stanzeContainer, int dimStanza, int dimPareti, int dimPorte) {
        ImageView colpo = new ImageView(main);
        colpo.setImageResource(R.drawable.cassa);
        colpo.setX(immagine.getX());
        colpo.setY(immagine.getY());

        colpo.setScaleX(.2f);
        colpo.setScaleY(.2f);
        /*
        new Thread(()->{
            for (int i=0;i<100;i++){
                colpo.setX(colpo.getX()+i);
                colpo.setY(colpo.getY()-i);

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }

            }
        }).start();
        */

        return colpo;
    }

    public void avanza(StanzeContainer stanzeContainer, Minimappa minimappa, int dimStanza, int dimPareti, int dimPorte) {

        if (inTransizione) return;

        double angolo = this.angolo * Math.PI / 180;

        double endX = immagine.getX() + (velocitaAttacco * vel_movimento) * Math.sin(angolo);
        double endY = immagine.getY() - (velocitaAttacco * vel_movimento) * Math.cos(angolo);

        Stanza stanza = stanzeContainer.getCurrentStanza();

        int x = screenWidth / 2 - dimStanza / 2,
                y = screenHeight / 2 - dimStanza / 2;

        if (endY <= y) {
            esci(stanzeContainer,stanzeContainer.getStanzaSopra(),minimappa,0,+(dimStanza/2)-(dimPareti));//ESCE SOPRA
            return;
        } else if (endY + immagine.getHeight() >= y+dimStanza) {
            esci(stanzeContainer,stanzeContainer.getStanzaSotto(),minimappa,0,-(dimStanza/2)+dimPareti);// ESCE SOTTO
            return;
        } else if (endX + immagine.getWidth() >= x+dimStanza) {
            esci(stanzeContainer,stanzeContainer.getStanzaDestra(),minimappa,(dimPareti/2)-dimPareti*3,0);// ESCE A DESTRA
            return;
        } else if (endX <= x) {
            esci(stanzeContainer,stanzeContainer.getStanzaSinistra(),minimappa,-(dimPareti/2)+dimPareti*3,0); // ESCE A SINISTRA
            return;
        }

        int gap_ombra = 4;
        int dimTriangolo = (int) (dimPorte/1.5);

        //in entrata stanza sopra
        if (stanza.hasSopra() && x + (dimStanza-dimPorte)/2 - dimTriangolo < endX && x + (dimStanza+dimPorte)/2 + dimTriangolo > endX + immagine.getWidth() && endY < y + dimPareti) {
            int lim = (int) (y+dimPareti-endY);
            minY = y - immagine.getHeight() / 2;
            maxY = y + dimStanza - dimPareti;
            minX = x + (dimStanza - dimPorte)/2 - dimTriangolo + lim;
            maxX = x + (dimStanza + dimPorte)/2 + dimTriangolo - lim;

            //in entrata stanza sotto
        } else if (stanza.hasSotto() && x + (dimStanza-dimPorte)/2 - dimTriangolo < endX && x + (dimStanza + dimPorte)/2 + dimTriangolo > endX + immagine.getWidth() && endY + immagine.getHeight() > y + dimStanza - dimPareti) {
            int lim = (int) (endY+immagine.getHeight()-(y+dimStanza-dimPareti));
            minY = y + dimPareti;
            maxY = y + dimStanza + immagine.getHeight() / 2;
            minX = x + (dimStanza - dimPorte)/2 - dimTriangolo + lim;
            maxX = x + (dimStanza + dimPorte)/2 + dimTriangolo - lim;

            //in entrata stanza destra
        } else if (stanza.hasDestra() && y + (dimStanza-dimPorte)/2 - dimTriangolo < endY && y + (dimStanza+dimPorte)/2 + dimTriangolo > endY + immagine.getHeight() && endX + immagine.getWidth() > x + dimStanza - dimPareti) {
            int lim = (int) (endX+immagine.getWidth() - (x+dimStanza-dimPareti));
            minX = x + dimPareti;
            maxX = x + dimStanza + immagine.getWidth() / 2;
            minY = y + (dimStanza - dimPorte)/2 - dimTriangolo + lim;
            maxY = y + (dimStanza + dimPorte)/2 + dimTriangolo - lim;

            //in entrata stanza sinistra
        } else if (stanza.hasSinistra() && y+(dimStanza-dimPorte)/2 - dimTriangolo < endY && y+(dimStanza+dimPorte)/2 + dimTriangolo > endY + immagine.getHeight() && endX < x+dimPareti) {
            int lim = (int) (x+dimPareti-endX);
            minX = x - immagine.getWidth()/2;
            maxX = x + dimStanza - dimPareti;
            minY = y + (dimStanza-dimPorte)/2 - dimTriangolo + lim;
            maxY = y + (dimStanza+dimPorte)/2 + dimTriangolo - lim;

            //se dentro stranza
        } else if (endY >= y+dimPareti && endY + immagine.getHeight() <= y+dimStanza - dimPareti && endX >= x+dimPareti && endX + immagine.getWidth() <= x+dimStanza - dimPareti) {
            minX = x+dimPareti;
            maxX = x+dimStanza - dimPareti;
            minY = y+dimPareti;
            maxY = y+dimStanza - dimPareti;
        }

//        collisioniConOggetti(x,y,dimStanza,dimPareti,stanzeContainer.getCurrentStanza());

        //Bauli
        for(int i=0; i<stanza.getSizeBauli(); i++){
            Baule baule = stanza.getBaule(i);
            int xB = x+baule.getX(), yB = y+baule.getY();
            int wB = baule.getWidth(), hB = baule.getHeight();

            //Sopra o sotto
            if((endX <= xB && endX+immagine.getWidth() >= xB+wB)
                    || (endX >= xB && endX <= xB+wB)
                    || (endX+immagine.getWidth() >= xB && endX+immagine.getWidth() <= xB+wB)){
                if(endY+immagine.getHeight() <= yB+hB/2){ //Sopra
                    if(yB < maxY)
                        maxY = yB;
                } else if(endY > yB+hB/2){ //Sotto
                    if(yB+hB > minY)
                        minY = yB+hB;
                }
            }

            //Destra o sinistra
            if((endY <= yB && endY+immagine.getHeight() >= yB+hB)
                    || (endY >= yB && endY <= yB+hB)
                    || (endY+immagine.getHeight() >= yB && endY+immagine.getHeight() <= yB+hB)){
                if(endX+immagine.getWidth() <= xB+wB/2){ //Sinistra
                    if(xB < maxX)
                        maxX = xB;
                } else if(endX > xB+wB/2){ //Destra
                    if(xB+wB > minX)
                        minX = xB+wB;
                }
            }

            //Se collide
            if(((endX >= xB && endX <= xB+wB) || (endX+immagine.getHeight() >= xB && endX+immagine.getHeight() <= xB+wB)) &&
                ((endY >= yB && endY <= yB+hB) || (endY+immagine.getHeight() >= yB && endY+immagine.getHeight() <= yB+hB))){
                if(baule.isChiuso())
                    baule.apri();
            }
        }

        endX = Math.max(endX, minX);
        endX = Math.min(endX, maxX-immagine.getWidth());

        endY = Math.max(endY, minY);
        endY = Math.min(endY, maxY-immagine.getHeight());

        if (minX <= endX && maxX >= endX + immagine.getWidth())
            immagine.setX((float) endX);

        if (minY <= endY && maxY >= endY + immagine.getHeight())
            immagine.setY((float) endY);
    }
    

    private void esci(StanzeContainer stanzeContainer, Stanza stanza, Minimappa minimappa, int offset_x, int offset_y) {
        stanzeContainer.setCurrentStanza(stanza);
        stanzeContainer.getCurrentStanza().setVisited(true);
        stanzeContainer.getCurrentStanza().refreshViewMinimappa();
        minimappa.refreshMinimappa();
        centra(offset_x,offset_y);
        stanzeContainer.setAnimazioneStanzeToCurrentStanza();
        //stanzeContainer.setLinearStanzeToCurrentStanza();
    }

    public void setVelocita(float velocitaAttacco) {
        this.velocitaAttacco = velocitaAttacco;
    }

    /**
     * SETTA POS CON ANIMAZIONE
     * @param x quanto incrementare x dal centro
     * @param y quanto incrementare y dal centro
     */
    private void centra(final int x,final int y){

        inTransizione = true;

        final int centroX = screenWidth/2-parametri.width/2;
        final int centroY = screenHeight/2-parametri.height/2;

        ObjectAnimator animX = ObjectAnimator.ofFloat(immagine, "x", centroX+x);
        ObjectAnimator animY = ObjectAnimator.ofFloat(immagine, "y", centroY+y);

        AnimatorSet animSetXY = new AnimatorSet();
        animSetXY.playTogether(animX, animY);
        animSetXY.setInterpolator(new AccelerateDecelerateInterpolator());
        animSetXY.setDuration(TEMPO_ANIMAZIONE/2);

        animSetXY.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                immagine.setX(immagine.getX());
                immagine.setY(immagine.getY());


                inTransizione = false;
            }
        });

        new Thread(()-> main.runOnUiThread(new Runnable() {
            @Override
            public void run() {
               animSetXY.start();
            }
        })).start();



    }

    public void setStaAttaccando(boolean staAttaccando) {
        this.isAttaccando = staAttaccando;
    }

    public void ruota(float v) {
        immagine.setRotation(v);
    }

    public boolean isAttaccando() {
        return isAttaccando;
    }
}
