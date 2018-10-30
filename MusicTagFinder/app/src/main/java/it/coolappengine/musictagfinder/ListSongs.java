package it.coolappengine.musictagfinder;

import android.content.Intent;
import android.os.*;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.*;
import android.widget.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class ListSongs extends AppCompatActivity {

    private LinearLayout layout;

    private Permessi permessi;

    private ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_songs);

        layout = (LinearLayout) findViewById(R.id.layout);

        permessi = new Permessi(this);
        permessi.controlloPermessiScrittura();
        permessi.controlloPermessiLettura();

        refreshSongList();
    }

    public void refreshSongList(){
        if(songsList != null){
            for(int i=0;i<songsList.size();i++){
                layout.removeView(layout.findViewWithTag("Button"+i));
            }
        }
        songsList = getPlayList("/sdcard/Mp3MusicDownloader/");
        System.out.println("Lenght: "+songsList.size());
        if(songsList != null){
            for(int i=0;i<songsList.size();i++){
                String fileName = songsList.get(i).get("file_name");
                final String filePath = songsList.get(i).get("file_path");
                Button b = new Button(this);
                b.setText(fileName);
                b.setTag("Button"+i);
                b.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(ListSongs.this, EditSong.class);
                        intent.putExtra("filePath", filePath);
                        startActivity(intent);
                    }
                });
                layout.addView(b);
            }
        }
    }

    public ArrayList<HashMap<String,String>> getPlayList(String rootPath) {
        ArrayList<HashMap<String,String>> fileList = new ArrayList<>();
        try {
            File rootFolder = new File(rootPath);
            File[] files = rootFolder.listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    if (getPlayList(file.getAbsolutePath()) != null) {
                        fileList.addAll(getPlayList(file.getAbsolutePath()));
                    } else {
                        break;
                    }
                } else if (file.getName().endsWith(".mp3")) {
                    HashMap<String, String> song = new HashMap<>();
                    song.put("file_path", file.getAbsolutePath());
                    song.put("file_name", file.getName());
                    fileList.add(song);
                }
            }
            return fileList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
