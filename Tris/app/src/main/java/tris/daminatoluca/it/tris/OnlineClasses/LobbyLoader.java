package tris.daminatoluca.it.tris.OnlineClasses;

import android.app.Activity;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.text.Layout;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import tris.daminatoluca.it.tris.R;

/**
 * Created by pigro on 01/02/2018.
 */

public class LobbyLoader extends Thread {

    private Socket server;
    private int timeoutConnection;
    private String ipServer;
    private int portServer;

    private Activity activity;

    private LinearLayout layout;

    private boolean refresh;

    private boolean newLobby;
    private String nomeLobby;

    public LobbyLoader(String ipServer, int portServer, LinearLayout layout, Activity activity){
        this.ipServer = ipServer;
        this.portServer = portServer;
        this.layout = layout;
        this.activity = activity;
        timeoutConnection = 1000;
        refresh = true;
        newLobby = false;
    }

    @Override
    public void run(){
        if(server == null || !server.isConnected()){
            System.out.println("Server: Provo a connettermi!");
            try {
                server = new Socket();
                server.connect(new InetSocketAddress(ipServer, portServer), timeoutConnection);
                System.out.println("Server: Connesso!");
                makeToast("Connesso al server!");
            } catch (IOException e) {
                System.out.println("Errore Socket!");
                makeToast("Server non raggiungibile");
                e.printStackTrace();
                interrupt();
            }
        }
        while(!isInterrupted()){
            if(refresh) {
                refresh = false;
                invia(server, "Lobbies");
                String listaLobby = ricevi(server);
                creaBottoni(layout, listaLobby);
            }
            if(newLobby){
                newLobby = false;
                invia(server, "NewLobby");
                invia(server, nomeLobby);
                int portLobby = Integer.parseInt(ricevi(server));
                enterLobby(ipServer, portLobby);
            }
        }
        try {
            server.close();
            System.out.println("Server: Disconnesso");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void creaBottoni(final LinearLayout layout, String listaLobby){
        Scanner scanner = new Scanner(listaLobby);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                layout.removeAllViews();
            }
        });
        if(listaLobby.equals("")){
            final TextView noLobby = new TextView(activity.getApplicationContext());
            noLobby.setText("No match available");
            noLobby.setTextColor(activity.getResources().getColor(android.R.color.black));
            noLobby.setTextSize(20);
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    layout.addView(noLobby);
                }
            });
        } else {
            while (scanner.hasNextLine()) {
                String riga = scanner.nextLine();
                String nomeLobby = riga.substring(0, riga.indexOf(";"));
                riga = riga.substring(riga.indexOf(";")+1);
                final int portLobby = Integer.parseInt(riga.substring(0, riga.indexOf(";")));
                riga = riga.substring(riga.indexOf(";")+1);
                final int nPlayer = Integer.parseInt(riga.substring(0, riga.indexOf(";")));
                riga = riga.substring(riga.indexOf(";")+1);
                final int maxPlayer = Integer.parseInt(riga.substring(0, riga.indexOf(";")));
                System.out.println(nomeLobby+" "+portLobby+" "+nPlayer+" "+maxPlayer);
                RelativeLayout lobbyLayout = (RelativeLayout) View.inflate(activity.getApplicationContext(), R.layout.bottone_lobby, null);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) (60 * activity.getApplicationContext().getResources().getDisplayMetrics().density));
                lp.setMargins(0, (int) (5 * activity.getApplicationContext().getResources().getDisplayMetrics().density), 0, 0);
                lobbyLayout.setLayoutParams(lp);
                Button joinButton = (Button) lobbyLayout.getChildAt(0);
                joinButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(nPlayer < maxPlayer)
                            enterLobby(ipServer, portLobby);
                        else
                            makeToast("Lobby piena!");
                    }
                });
                TextView nomeLobbyTextView = (TextView) lobbyLayout.getChildAt(1);
                nomeLobbyTextView.setText(nomeLobby);
                TextView nPlayerTextView = (TextView) lobbyLayout.getChildAt(2);
                nPlayerTextView.setText(nPlayer+"/"+maxPlayer);
                final RelativeLayout finalLobbyLayout = lobbyLayout;
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        layout.addView(finalLobbyLayout);
                    }
                });
            }
            scanner.close();
        }
    }

    private void makeToast(final String text){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, text, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void refreshLobbies(){
        refresh = true;
    }

    public void createNewLobby(String nomeLobby){
        newLobby = true;
        this.nomeLobby = nomeLobby;
    }

    public void enterLobby(String ipLobby, int portLobby){
        Intent intent = new Intent(activity, OnlineMatch.class);
        intent.putExtra("ipLobby", ipLobby);
        intent.putExtra("portLobby", portLobby);
        activity.startActivity(intent);
        LobbyLoader.this.interrupt();
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
            DataInputStream input = new DataInputStream(server.getInputStream());
            String s = input.readUTF();
            while(s == null)
                s = input.readUTF();
            return s;
        }  catch(SocketException ex) {
            System.out.println("Errore nel Socket del Server");
            ex.printStackTrace();
        } catch (IOException ex) {
            System.out.println("Errore nel DataInputStream");
            ex.printStackTrace();
        }
        return null;
    }
}
