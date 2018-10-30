package com.memegram.florian.amazein.classi_gioco;

import android.app.Activity;
import android.graphics.Point;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.memegram.florian.amazein.R;

/**
 * Created by pigro on 30/03/2018.
 */

public class Minimappa {

    private StanzeContainer stanzeContainer;

    private RelativeLayout layoutPrincipale;

    private Activity activity;

    private RelativeLayout relativeLayout;
    private RelativeLayout.LayoutParams lpRelative;
    private LinearLayout linearStanze;
    private RelativeLayout.LayoutParams lpLinear;

    private int screenWidth, screenHeight;

    private int dimStanza;

    private ImageView personaggio;
    private RelativeLayout.LayoutParams lpPersonaggio;

    private boolean isFullScreen = false;

    public Minimappa (StanzeContainer stanzeContainer, RelativeLayout layoutPrincipale, Activity activity){
        this.stanzeContainer = stanzeContainer;
        this.layoutPrincipale = layoutPrincipale;
        this.activity = activity;

        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size);
        screenWidth = size.x;
        screenHeight = size.y;

        personaggio = new ImageView(activity);
        personaggio.setImageDrawable(activity.getResources().getDrawable(R.drawable.minimappa_personaggio));
        lpPersonaggio = new RelativeLayout.LayoutParams(0, 0);
        lpPersonaggio.setMargins(0, 0, 0, 0);

        relativeLayout = new RelativeLayout(activity);
        relativeLayout.setOnClickListener(v -> {
            if(isFullScreen) {
                setLinearStanzeMinimappaCircle();
                setMinimappaCentrata();
            } else
                setLinearStanzeMinimappaFullScreen();
        });
        relativeLayout.addView(getLinearMinimappaStanze());
        relativeLayout.addView(personaggio);

        setLinearStanzeMinimappaCircle();
        setLinearAlpha(0.5f);

        layoutPrincipale.addView(relativeLayout);
    }

    public View getLinearMinimappaStanze(){
        if(linearStanze == null) {
            //Linear Layout
            linearStanze = new LinearLayout(activity);
            lpLinear = new RelativeLayout.LayoutParams(0, 0);
            lpLinear.setMargins(0, 0, 0, 0);
            linearStanze.setLayoutParams(lpLinear);
            linearStanze.setOrientation(LinearLayout.VERTICAL);

            for (int i = 0; i < stanzeContainer.getRighe(); i++) {
                //Linear Layout Orizzontale per tutte le stanze
                LinearLayout lno = new LinearLayout(activity);
                lno.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
                lno.setOrientation(LinearLayout.HORIZONTAL);

                for (int j = 0; j < stanzeContainer.getColonne(); j++) {
                    if (stanzeContainer.getStanza(i, j) != null)
                        lno.addView(stanzeContainer.getStanza(i, j).getViewMinimappaStanza(0, 0, 0, 0, activity));
                    else {
                        RelativeLayout pareteLayout = (RelativeLayout) View.inflate(activity, R.layout.parete_layout, null);
                        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(0, 0);
                        pareteLayout.setLayoutParams(lp2);
                        lno.addView(pareteLayout);
                    }
                }

                linearStanze.addView(lno);
            }
        }
        return linearStanze;
    }

    public void setLinearStanzeMinimappaFullScreen() {
        isFullScreen = true;
        lpRelative = new RelativeLayout.LayoutParams((screenHeight / 10 * 9)/stanzeContainer.getRighe()*stanzeContainer.getColonne(), screenHeight / 10 * 9);
        lpRelative.setMargins((screenWidth - lpRelative.width) / 2, (screenHeight - lpRelative.height) / 2, 0, 0);
        relativeLayout.setLayoutParams(lpRelative);
        relativeLayout.setBackground(activity.getResources().getDrawable(R.drawable.minimappa_layout_quadrato));

        dimStanza = lpRelative.height/stanzeContainer.getRighe();

        lpLinear = new RelativeLayout.LayoutParams(dimStanza*stanzeContainer.getColonne(), dimStanza*stanzeContainer.getRighe());
        lpLinear.setMargins(0, 0, 0, 0);
        linearStanze.setLayoutParams(lpLinear);
        linearStanze.setOrientation(LinearLayout.VERTICAL);

        for (int i = 0; i < linearStanze.getChildCount(); i++) {
            //Linear Layout Orizzontale per tutte le stanze
            LinearLayout lno = (LinearLayout) linearStanze.getChildAt(i);
            lno.setLayoutParams(new LinearLayout.LayoutParams(dimStanza*stanzeContainer.getColonne(), dimStanza));
            lno.setOrientation(LinearLayout.HORIZONTAL);

            for (int j = 0; j < lno.getChildCount(); j++) {
                RelativeLayout pareteStanza = (RelativeLayout) lno.getChildAt(j);
                LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(dimStanza, dimStanza);
                pareteStanza.setLayoutParams(lp2);
            }
        }

        setPersonaggioStanzaCorrente();
    }

    public void setPersonaggioStanzaCorrente(){
        if(isFullScreen){
            lpPersonaggio = new RelativeLayout.LayoutParams(dimStanza/4, dimStanza/4);
            lpPersonaggio.leftMargin = dimStanza*stanzeContainer.getCurrentStanza().getColonna()+dimStanza/2-lpPersonaggio.width/2;
            lpPersonaggio.topMargin = dimStanza*stanzeContainer.getCurrentStanza().getRiga()+dimStanza/2-lpPersonaggio.height/2;
            relativeLayout.post(new Runnable() {
                @Override
                public void run() {
                    personaggio.setLayoutParams(lpPersonaggio);
                }
            });
        }
    }

    public void setLinearStanzeMinimappaCircle() {
        isFullScreen = false;
        lpRelative = new RelativeLayout.LayoutParams(screenHeight / 10 * 4, screenHeight / 10 * 4);
        lpRelative.setMargins(screenHeight/20, screenHeight/20, 0, 0);
        relativeLayout.setLayoutParams(lpRelative);
        relativeLayout.setBackground(activity.getResources().getDrawable(R.drawable.minimappa_layout_tondo));

        dimStanza = screenHeight/10;

        lpLinear = new RelativeLayout.LayoutParams(dimStanza*stanzeContainer.getColonne(), dimStanza*stanzeContainer.getRighe());
        lpLinear.setMargins(0, 0, 0, 0);
        linearStanze.setLayoutParams(lpLinear);
        linearStanze.setOrientation(LinearLayout.VERTICAL);

        lpPersonaggio = new RelativeLayout.LayoutParams(dimStanza/4, dimStanza/4);
        lpPersonaggio.setMargins(lpRelative.width/2-lpPersonaggio.width/2,lpRelative.height/2-lpPersonaggio.height/2, 0, 0);
        personaggio.setLayoutParams(lpPersonaggio);

        for (int i = 0; i < linearStanze.getChildCount(); i++) {
            //Linear Layout Orizzontale per tutte le stanze
            LinearLayout lno = (LinearLayout) linearStanze.getChildAt(i);
            lno.setLayoutParams(new LinearLayout.LayoutParams(dimStanza*stanzeContainer.getColonne(), dimStanza));
            lno.setOrientation(LinearLayout.HORIZONTAL);

            for (int j = 0; j < lno.getChildCount(); j++) {
                RelativeLayout pareteStanza = (RelativeLayout) lno.getChildAt(j);
                LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(dimStanza, dimStanza);
                pareteStanza.setLayoutParams(lp2);
            }
        }

        setMinimappaCentrata();
    }

    public void setMinimappaCentrata(){
        if(!isFullScreen){
            int x = -dimStanza*stanzeContainer.getCurrentStanza().getColonna()+lpRelative.width/2-dimStanza/2;
            int y = -dimStanza*stanzeContainer.getCurrentStanza().getRiga()+lpRelative.height/2-dimStanza/2;

            lpLinear.setMargins(x, y, 0, 0);
            linearStanze.post(new Runnable() {
                @Override
                public void run() {
                    linearStanze.setLayoutParams(lpLinear);
                }
            });
        }
    }

    public void refreshMinimappa(){
        setMinimappaCentrata();
        setPersonaggioStanzaCorrente();
    }

    public void setLinearAlpha(float alpha){
        linearStanze.setAlpha(alpha);
    }
}