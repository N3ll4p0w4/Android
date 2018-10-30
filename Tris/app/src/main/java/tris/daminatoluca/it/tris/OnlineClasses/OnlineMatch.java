package tris.daminatoluca.it.tris.OnlineClasses;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import tris.daminatoluca.it.tris.R;
import tris.daminatoluca.it.tris.Tris;

public class OnlineMatch extends AppCompatActivity {

    private Tris tris;

    private String ipLobby;
    private int portLobby;

    private MatchController matchController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tris);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        tris = new Tris();

        ipLobby = getIntent().getStringExtra("ipLobby");
        portLobby = getIntent().getIntExtra("portLobby",9000);

    }

    public void bottone(View view){
        int row, column;
        String id = getResources().getResourceName(view.getId());
        id = id.substring(id.length()-3);
        row = Integer.parseInt(id.charAt(1)+"");
        column = Integer.parseInt(id.charAt(2)+"");
        matchController.faiMossa(view, row, column);
    }

    public void restartButton(View w){
        finish();
    }

    @Override
    public void onBackPressed() {
        matchController.interrupt();
        finish();
    }

    @Override
    protected void onPause() {
        matchController.interrupt();
        super.onPause();
    }

    @Override
    protected void onStop() {
        matchController.interrupt();
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        matchController = new MatchController(ipLobby, portLobby, this, tris);
        matchController.start();
    }
}
