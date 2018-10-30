package nellaschat.coolapp_engine.it.nellaschat.hostedserver;

import java.util.ArrayList;

public class ClientsExecutorTask extends Thread {

    private HostedServer hostedServer;
    private ArrayList<Client> clients;
    private int startIndex, endIndex;

    private boolean interrupt;

    public ClientsExecutorTask(HostedServer hostedServer, ArrayList<Client> clients){
        this.hostedServer = hostedServer;
        this.clients = clients;
        interrupt = false;
    }

    @Override
    public void run() {
        while(!interrupt){
            try {
                Thread.sleep(20);
            } catch (InterruptedException ex) {}
            for(int i=startIndex; i<endIndex; i++){
                if(!clients.get(i).isRunning() && clients.get(i).isReady()){
                    clients.get(i).gestisci();
                }
            }
        }
    }

    public void setStartIndex(int startIndex){
        this.startIndex = startIndex;
    }

    public void setEndIndex(int endIndex){
        this.endIndex = endIndex;
    }

    public void interrupt(){
        this.interrupt = false;
    }

    public boolean getInterrupt(){
        return interrupt;
    }

}
