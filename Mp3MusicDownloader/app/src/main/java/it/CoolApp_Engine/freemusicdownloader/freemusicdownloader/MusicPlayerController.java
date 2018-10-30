package it.CoolApp_Engine.freemusicdownloader.freemusicdownloader;

import android.media.MediaPlayer;

/**
 * Created by pigro on 03/01/2018.
 */

public class MusicPlayerController {

    private LayoutRisultatoCanzone current;
    private MediaPlayer mediaPlayer;

    public MusicPlayerController(){}

    public void startLayoutRisultatoCanzone(LayoutRisultatoCanzone layoutRisultatoCanzone){
        if(current == null){
            mediaPlayer = layoutRisultatoCanzone.getMediaPlayer();
        } else {
            stopLayoutRisultatoCanzone();
        }
        current = layoutRisultatoCanzone;
        mediaPlayer = current.getMediaPlayer();
        current.setPauseIcon();
        mediaPlayer.start();
    }

    public void stopLayoutRisultatoCanzone(){
        if (mediaPlayer != null)
            mediaPlayer.reset();
        if(current != null)
            current.setPlayIcon();
    }

}
