package it.CoolApp_Engine.freemusicdownloader.freemusicdownloader;

/**
 * Created by pigro on 27/12/2017.
 */

public class Canzone {

    private String nome = "";
    private String durata = "";
    private String download = "";
    private boolean isDownloading = false;

    Canzone(){}

    public void setNome(String nome) {
        this.nome = nome.replaceAll("/", "-");
    }

    public String getNome() {
        return nome;
    }

    public void setDurata(String durata) {
        this.durata = durata;
    }

    public String getDurata() {
        return durata;
    }

    public void setDownload(String download) {
        this.download = download;
    }

    public String getDownload() {
        return download;
    }

    public void startDownloading(){
        isDownloading = true;
    }

    public void finishDownloading(){
        isDownloading = false;
    }

    public boolean isDownloading(){
        return isDownloading;
    }
}
