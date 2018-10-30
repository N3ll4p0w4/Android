package tris.daminatoluca.it.tris;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by pigro on 30/01/2018.
 */

public class Locale1v1 extends AppCompatActivity {

    private TextView testo;
    private GridLayout gridLayout;

    private int giocatore1 = 1, giocatore2 = 2;
    private int turno;

    private Tris tris;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tris);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        tris = new Tris();

        testo = (TextView) findViewById(R.id.testoComputer);
        /*
        gridLayout = (GridLayout) findViewById(R.id.glComputer);
        for(int i=0; i<gridLayout.getRowCount(); i++){
            for(int j=0; j<gridLayout.getColumnCount(); j++){
                Button b = (Button)gridLayout.getChildAt(gridLayout.getColumnCount()*i+j);
                final int finalI = i;
                final int finalJ = j;
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottone(finalI, finalJ);
                    }
                });
            }
        }
        */

        turno = getIntent().getIntExtra("turno", 1);
        if(turno == giocatore1)
            testo.setText(getResources().getString(R.string.eTurno) + " Player1");
        else testo.setText(getResources().getString(R.string.eTurno) + " Player2");
    }

    public void checkVittoria(){
        if(tris.hasWin()){
            if(turno == giocatore2)
                testo.setText(getResources().getString(R.string.haVinto)+" Player1!");
            else
                testo.setText(getResources().getString(R.string.haVinto)+" Player2!");
            turno = 0;
        } else if(tris.getLiberi() <= 0){
            testo.setText(getResources().getString(R.string.pareggio));
            turno = 0;
        }
    }

    /*
    public void bottone(int row, int column){
        if (tris.isLibero(row, column)) {
            if(turno == giocatore1) {
                tris.setTurno(row, column, giocatore1);
                Button b = (Button) gridLayout.getChildAt(gridLayout.getColumnCount() * row + column);
                b.setBackground(getResources().getDrawable(R.drawable.croce));
                testo.setText(getResources().getString(R.string.eTurno) + " Player2");
                turno = 2;
                checkVittoria();
            } else if(turno == giocatore2) {
                tris.setTurno(row, column, giocatore2);
                Button b = (Button) gridLayout.getChildAt(gridLayout.getColumnCount() * row + column);
                b.setBackground(getResources().getDrawable(R.drawable.cerchio));
                testo.setText(getResources().getString(R.string.eTurno) + " Player1");
                turno = 1;
                checkVittoria();
            }
        } else
            Toast.makeText(this, getResources().getString(R.string.nonLibera), Toast.LENGTH_SHORT).show();
    }
    */

    public void bottone(View view){
        int row, column;
        String id = getResources().getResourceName(view.getId());
        id = id.substring(id.length()-3);
        row = Integer.parseInt(id.charAt(1)+"");
        column = Integer.parseInt(id.charAt(2)+"");
        if (tris.isLibero(row, column)) {
            if(turno == giocatore1) {
                tris.setTurno(row, column, giocatore1);
                Button b = (Button)view;
                b.setBackground(getResources().getDrawable(R.drawable.croce));
                testo.setText(getResources().getString(R.string.eTurno) + " Player2");
                turno = giocatore2;
                checkVittoria();
            } else if(turno == giocatore2) {
                tris.setTurno(row, column, giocatore2);
                Button b = (Button)view;
                b.setBackground(getResources().getDrawable(R.drawable.cerchio));
                testo.setText(getResources().getString(R.string.eTurno) + " Player1");
                turno = giocatore1;
                checkVittoria();
            }
        } else
            Toast.makeText(this, getResources().getString(R.string.nonLibera), Toast.LENGTH_SHORT).show();
    }

    public void restartButton(View w){
        Intent intent = new Intent(this, this.getClass());
        if(getIntent().getIntExtra("turno", giocatore1) == giocatore1)
            intent.putExtra("turno", giocatore2);
        else intent.putExtra("turno", giocatore1);
        startActivity(intent);
        finish();
    }
}
