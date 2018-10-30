package it.coolappengine.musictagfinder;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;

import org.jaudiotagger.audio.*;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.tag.*;
import org.jaudiotagger.tag.images.*;

import java.io.ByteArrayOutputStream;
import java.io.File;

/**
 * Created by pigro on 26/01/2018.
 */

public class Song {

    private File songFile;
    private AudioFile audioFile;
    private Tag tags;
    private Activity activity;

    private Song(){
    }

    public Song(String path, Activity activity){
        songFile = new File(path);
        this.activity = activity;
        inizialize();
    }

    public Song(File songFile, Activity activity){
        this.songFile = songFile;
        this.activity = activity;
        inizialize();
    }

    private void inizialize(){
        try {
            audioFile = AudioFileIO.read(songFile);
            tags = audioFile.getTagOrCreateAndSetDefault();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setTitolo(String titolo){
        try {
            tags.setField(FieldKey.TITLE, titolo);
        } catch (FieldDataInvalidException e) {
            e.printStackTrace();
        }
    }

    public void setArtista(String artista){
        try {
            tags.setField(FieldKey.ARTIST, artista);
        } catch (FieldDataInvalidException e) {
            e.printStackTrace();
        }
    }

    public void setAlbum(String album){
        try {
            tags.setField(FieldKey.ALBUM, album);
        } catch (FieldDataInvalidException e) {
            e.printStackTrace();
        }
    }

    public void setGenere(String genere){
        try {
            tags.setField(FieldKey.GENRE, genere);
        } catch (FieldDataInvalidException e) {
            e.printStackTrace();
        }
    }

    public void setData(String data){
        try {
            tags.setField(FieldKey.YEAR, data);
        } catch (FieldDataInvalidException e) {
            e.printStackTrace();
        }
    }

    public void setCopertina(Bitmap copertina){
        tags.getArtworkList().clear();
        tags.deleteArtworkField();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        copertina.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        Artwork artwork = ArtworkFactory.getNew();
        artwork.setBinaryData(byteArray);
        try {
            tags.addField(artwork);
            System.out.println("Copertina settata");
        } catch (FieldDataInvalidException e) {
            e.printStackTrace();
        }
    }

    public void setField(FieldKey fieldKey, String info){
        try {
            tags.setField(fieldKey, info);
        } catch (FieldDataInvalidException e) {
            e.printStackTrace();
        }
    }

    public void salva(){
        audioFile.setTag(tags);
        try {
            audioFile.commit();
            System.out.println("Salvato");
        } catch (CannotWriteException e) {
            e.printStackTrace();
        }
        activity.getApplicationContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(songFile)));
    }

}
