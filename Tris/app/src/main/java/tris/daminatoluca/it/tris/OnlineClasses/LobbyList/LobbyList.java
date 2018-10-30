package tris.daminatoluca.it.tris.OnlineClasses.LobbyList;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutionException;

import tris.daminatoluca.it.tris.OnlineClasses.SocketUtils.*;
import tris.daminatoluca.it.tris.R;

public class LobbyList extends AppCompatActivity {

    private EditText editSetIp;
    private Button buttonSetIp;
    private Button buttonRefresh;
    private EditText editNameLobby;
    private Button buttonCreateNewLobby;
    private LinearLayout layout;

    private SharedPreferences prefs;
    private SharedPreferences.Editor prefsEditor;

    private Socket server;
    private String ipServer;
    private int portServer;
    private int timeoutConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.online_lobby_list);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        prefsEditor = prefs.edit();

        ipServer = getStoredIp();
        portServer = 9000;
        timeoutConnection = 10000;

        editSetIp = (EditText) findViewById(R.id.editSetIp);
        buttonSetIp = (Button) findViewById(R.id.buttonSetIp);
        buttonRefresh = (Button) findViewById(R.id.buttonRefreshLobbies);
        editNameLobby = (EditText) findViewById(R.id.editNomeNewLobby);
        buttonCreateNewLobby = (Button) findViewById(R.id.buttonCreateNewLobbby);
        layout = (LinearLayout) findViewById(R.id.containerLobbiesLayout);

        editSetIp.setText(getStoredIp());
    }

    private void connectToServer(){
        if(server != null) {
            try {
                server.close();
            } catch (IOException e) {}
        }
        server = new Socket();
        GetSocket gs = new GetSocket(server, ipServer, portServer, timeoutConnection);
        gs.execute();
        System.out.println("Eseuito");
    }

    private String getStoredIp(){
        return prefs.getString("ServerIp", "192.168.1.0");
    }


    public void setIpButton(View view){
        prefsEditor.putString("ServerIp", editSetIp.getText().toString());
        prefsEditor.apply();
        ipServer = getStoredIp();
        editSetIp.setText(getStoredIp());
        connectToServer();
    }

    public void refreshLobbiesButton(View view){
        if(server == null)
            System.out.println("Null");
        else
            System.out.println(server.isConnected());
        //lobbyLoader.refreshLobbies();
    }

    public void createLobbyButton(View view){
        if(editNameLobby.getText().length() > 0){
        } else Toast.makeText(this, "Insert a name", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        try {
            server.close();
        } catch (IOException e) {}
        finish();
    }

    @Override
    protected void onPause() {
        try {
            server.close();
        } catch (IOException e) {}
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        connectToServer();
    }
}
