package tris.daminatoluca.it.tris.OnlineClasses;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.TimeUnit;

import tris.daminatoluca.it.tris.R;
import tris.daminatoluca.it.tris.Tris;

/**
 * Created by pigro on 03/02/2018.
 */

public class MatchController extends Thread{

    private Socket lobby;
    private int timeoutConnection;
    private String ipLobby;
    private int portLobby;

    private Activity activity;
    private TextView testo;
    private Button bottone;
    private int row, column;
    private Tris tris;

    private int locale, avversario;
    private int turno;

    public MatchController(String ipLobby, int portLobby, Activity activity, Tris tris){
        this.ipLobby = ipLobby;
        this.portLobby = portLobby;
        this.activity = activity;
        this.tris = tris;
        timeoutConnection = 5000;
        turno = 0;
        testo = (TextView) activity.findViewById(R.id.testoComputer);
    }

    @Override
    public void run(){
        if(lobby == null || !lobby.isConnected()){
            System.out.println("Lobby: Provo a connettermi!");
            try {
                lobby = new Socket();
                lobby.connect(new InetSocketAddress(ipLobby, portLobby), timeoutConnection);
                System.out.println("Lobby: Connesso!");
                makeToast("Connesso!");
            } catch (IOException e) {
                System.out.println("Errore Socket!");
                makeToast("Server non raggiungibile");
                e.printStackTrace();
                interrupt();
            }
        }
        setTesto("Attendere players...");
        locale = Integer.parseInt(ricevi(lobby));
        if(locale == 1) avversario = 2;
        else avversario = 1;
        turno = Integer.parseInt(ricevi(lobby));
        if(locale == turno)
            setTesto(activity.getResources().getString(R.string.eTurno) + " Locale");
        else setTesto(activity.getResources().getString(R.string.eTurno) + " Avversario");
        System.out.println(ricevi(lobby));
        while(!isInterrupted()){
            if(turno == avversario){
                String mess = ricevi(lobby);
                row = Integer.parseInt(mess.charAt(0)+"");
                column = Integer.parseInt(mess.charAt(1)+"");
                tris.setTurno(row, column, avversario);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Button b = (Button)activity.findViewById(activity.getResources().getIdentifier("b"+row+""+column, "id", activity.getPackageName()));
                        b.setBackground(activity.getResources().getDrawable(R.drawable.cerchio));
                    }
                });
                setTesto(activity.getResources().getString(R.string.eTurno) + " Locale");
                turno = locale;
                checkVittoria();
                bottone = null;
            } else if(bottone != null){
                if(turno == locale) {
                    if (tris.isLibero(row, column)) {
                        invia(lobby, row+""+column+"");
                        tris.setTurno(row, column, locale);
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                 Button b = (Button)activity.findViewById(activity.getResources().getIdentifier("b"+row+""+column, "id", activity.getPackageName()));
                                 b.setBackground(activity.getResources().getDrawable(R.drawable.croce));
                            }
                        });
                        setTesto(activity.getResources().getString(R.string.eTurno) + " Avversario");
                        turno = avversario;
                        checkVittoria();
                    } else
                        makeToast(activity.getResources().getString(R.string.nonLibera));
                } else
                    makeToast(activity.getResources().getString(R.string.nonTurno));
                bottone = null;
            }
        }
        try {
            lobby.close();
            System.out.println("Lobby: Disconnesso");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void checkVittoria(){
        if(tris.hasWin()){
            if(turno == avversario) {
                setTesto(activity.getResources().getString(R.string.haVinto) + " Locale!");
            } else {
                setTesto(activity.getResources().getString(R.string.haVinto) + " Avversario!");
            }
            turno = 0;
            invia(lobby, "Finish");
        } else if(tris.getLiberi() <= 0){
            setTesto(activity.getResources().getString(R.string.pareggio));
            turno = 0;
            invia(lobby, "Finish");
        }
    }

    public void faiMossa(View view, int row, int column){
        this.row = row;
        this.column = column;
        this.bottone = (Button)view;
    }

    private void setTesto(final String text){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                testo.setText(text);
            }
        });
    }

    private void makeToast(final String text){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity.getApplicationContext(), text, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void invia(Socket socket, String data){
        try {
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            out.writeUTF(data);
        }  catch(SocketException ex) {
            System.out.println("Errore nel Socket del Client");
            ex.printStackTrace();
        }catch(IOException ex) {
            System.out.println("Errore nel DataOutputStream");
            ex.printStackTrace();
        }
    }

    private String ricevi(Socket socket){
        try {
            DataInputStream input = new DataInputStream(lobby.getInputStream());
            String s = input.readUTF();
            while(s == null)
                s = input.readUTF();
            return s;
        }  catch(SocketException ex) {
            System.out.println("Errore nel Socket del Server");
            makeToast("Someone has left...");
            this.interrupt();
            activity.finish();
            ex.printStackTrace();
        } catch (IOException ex) {
            System.out.println("Errore nel DataInputStream");
            makeToast("Someone has left...");
            this.interrupt();
            activity.finish();
            ex.printStackTrace();
        }
        return null;
    }
}
