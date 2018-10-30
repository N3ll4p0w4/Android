package tris.daminatoluca.it.tris.OnlineClasses;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import tris.daminatoluca.it.tris.R;

/**
 * Created by pigro on 31/01/2018.
 */

public class OnlineLobbyList extends AppCompatActivity {

    private EditText editSetIp;
    private Button buttonSetIp;
    private Button buttonRefresh;
    private EditText editNameLobby;
    private Button buttonCreateNewLobby;
    private LinearLayout layout;

    private SharedPreferences prefs;
    private SharedPreferences.Editor prefsEditor;

    private String ipServer;
    private int portServer;
    private LobbyLoader lobbyLoader;

    private Activity thisActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.online_lobby_list);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        prefsEditor = prefs.edit();

        ipServer = getStoredIp();
        portServer = 9000;
        thisActivity = this;

        editSetIp = (EditText) findViewById(R.id.editSetIp);
        buttonSetIp = (Button) findViewById(R.id.buttonSetIp);
        buttonRefresh = (Button) findViewById(R.id.buttonRefreshLobbies);
        editNameLobby = (EditText) findViewById(R.id.editNomeNewLobby);
        buttonCreateNewLobby = (Button) findViewById(R.id.buttonCreateNewLobbby);
        layout = (LinearLayout) findViewById(R.id.containerLobbiesLayout);

        editSetIp.setText(getStoredIp());
    }

    private String getStoredIp(){
        return prefs.getString("ServerIp", "192.168.1.0");
    }

    public void setIpButton(View view){
        prefsEditor.putString("ServerIp", editSetIp.getText().toString());
        prefsEditor.apply();
        ipServer = getStoredIp();
        lobbyLoader.interrupt();
        lobbyLoader = new LobbyLoader(ipServer, portServer, layout, thisActivity);
        lobbyLoader.start();
        editSetIp.setText(getStoredIp());
    }

    public void refreshLobbiesButton(View view){
        lobbyLoader.refreshLobbies();
    }

    public void createLobbyButton(View view){
        if(editNameLobby.getText().length() > 0){
            lobbyLoader.createNewLobby(editNameLobby.getText().toString());
            lobbyLoader.refreshLobbies();
        } else Toast.makeText(this, "Insert a name", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        lobbyLoader.interrupt();
        finish();
    }

    @Override
    protected void onPause() {
        lobbyLoader.interrupt();
        super.onPause();
    }

    @Override
    protected void onStop() {
        lobbyLoader.interrupt();
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        lobbyLoader = new LobbyLoader(ipServer, portServer, layout, this);
        lobbyLoader.start();
    }
}
