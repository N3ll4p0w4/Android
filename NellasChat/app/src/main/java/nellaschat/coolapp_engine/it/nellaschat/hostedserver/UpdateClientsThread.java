package nellaschat.coolapp_engine.it.nellaschat.hostedserver;

import android.support.v4.content.res.TypedArrayUtils;

import java.util.ArrayList;

public class UpdateClientsThread implements Runnable {

    private HostedServer hostedServer;
    private ArrayList<Client> clients = new ArrayList<>();
    private String[] comandi;

    public UpdateClientsThread(ArrayList<Client> clients, String[] comandi){
        this.clients.addAll(clients);
        this.comandi = comandi;
    }

    @Override
    public void run() {
        while(clients.size() > 0){
            for(int i=0; i<clients.size();){
                if(clients.get(i).isLogged() && !clients.get(i).isRunning()){
                    clients.get(i).setRunning(true);
                    for(int j=0; j<comandi.length; j++){
                        clients.get(i).invia(comandi[j]);
                    }
                    clients.get(i).setRunning(false);
                    clients.remove(i);
                } else i++;
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException ex) {}
        }
    }
}
