package nellaschat.coolapp_engine.it.nellaschat.chat;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Messaggio {

    private int id;
    private String autore = "";
    private String testo  = "";
    private String data = "";

    private int colore;

    private ArrayList<Messaggio> messaggiRisposta = new ArrayList<>();

    private boolean removed;

    public Messaggio() {
        removed = false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAutore() {
        return autore;
    }

    public void setAutore(String autore) {
        this.autore = autore;
    }

    public String getTesto() {
        return testo;
    }

    public void setTesto(String testo) {
        this.testo = testo;
    }

    public String getData() {
        return data;
    }

    public void setData(Date data) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(data);

        String ora = calendar.get(Calendar.HOUR_OF_DAY)+"";
        if(ora.length() == 1)
            ora = "0"+ora;

        String minuto = calendar.get(Calendar.MINUTE)+"";
        if(minuto.length() == 1)
            minuto = "0"+minuto;

        this.data = ora + ":" + minuto;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getColore() {
        return colore;
    }

    public void setColore(int colore) {
        this.colore = colore;
    }

    public ArrayList<Messaggio> getMessaggiRisposta() {
        return messaggiRisposta;
    }

    public void addMessaggioRisposta(Messaggio messaggio){
        messaggiRisposta.add(messaggio);
    }

    public void removeMessaggioRisposta(Messaggio messaggio){
        messaggiRisposta.remove(messaggio);
    }

    public void clearMessaggiRisposta(){
        messaggiRisposta.clear();
    }

    public boolean isRemoved() {
        return removed;
    }

    public void remove(){
        removed = true;
    }
}
