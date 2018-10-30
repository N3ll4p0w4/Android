package lifesorganizer.coolapp_engine.it.lifesorganizer.server;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public abstract class ServerAbstact extends Thread {

    @Override
    public void run(){
        //Code
    }

    public void invia(String s){
        boolean inviato = ServerComunications.invia(s);
        while(!inviato) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {e.printStackTrace();}
            inviato = ServerComunications.invia(s);
        }
    }

    public String ricevi(){
        String s = ServerComunications.ricevi();
        while(s == null) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {e.printStackTrace();}
            s = ServerComunications.ricevi();
        }
        return s;
    }

    public void inviaImmagine(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        ServerComunications.inviaObject(imageBytes);
    }

    public Bitmap riceviImmagine(){
        byte[] imageBytes = (byte[]) ServerComunications.riceviObject();
        Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        return decodedImage;
    }
}
