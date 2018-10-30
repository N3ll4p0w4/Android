package it.CoolApp_Engine.freemusicdownloader.freemusicdownloader;

/**
 * Created by pigro on 26/12/2017.
 */

public class Datmusic {

    private String URL;

    Datmusic(){
        URL = "";
    }

    public void Cerca(String songName){
        URL = "https://datmusic.xyz/?q="+convertSpace(songName);
    }

    public String getURL(){
        return URL;
    }

    private String convertSpace(String s){
        s.replaceAll(" ", "%20");
        return s;
    }
}
