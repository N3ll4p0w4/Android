package nellaschat.coolapp_engine.it.nellaschat.hostedserver;

import android.content.Context;
import android.support.constraint.ConstraintLayout;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import nellaschat.coolapp_engine.it.nellaschat.chat.Messaggio;

public class HostedServer extends Thread {

    private Context context;

    private ServerSocket server;
    private String nomeServer = "";
    private int portServer;

    private int nThread = 2;
    protected ArrayList<Client> clients = new ArrayList();
    protected ArrayList<Messaggio> messaggi = new ArrayList<>();
    private int lastIdMessaggi = -1;
    private ClientsExecutorTask[] clientsTasks = new ClientsExecutorTask[1];
    private ExecutorService executorService = Executors.newFixedThreadPool(nThread);

    private boolean started;

    public HostedServer(Context context) {
        this.context = context;
        portServer = 9000;
        started = false;
    }

    public HostedServer(int portServer, Context context) {
        this.context = context;
        this.portServer = portServer;
        started = false;
    }

    @Override
    public void run(){

        for(int i=0; i<clientsTasks.length; i++){
            clientsTasks[i] = new ClientsExecutorTask(this, clients);
            clientsTasks[i].start();
        }

        while(server == null || server.isClosed()){
            try {
                server = new ServerSocket(portServer);
            } catch (IOException e) {
                e.printStackTrace();
                portServer++;
            }
        }

        started = true;

        try{
            System.out.println("Aspetto alla porta "+portServer+"...");
            while(true){
                Socket client = server.accept();
                System.out.println("HostedServer"+portServer+": user connesso");
                addClient(new Client(this, client));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            started = false;
        }
    }

    private void updateIndexes(){
        int eachOne = clients.size()/clientsTasks.length;
        for(int i=0; i<clientsTasks.length-1; i++){
            clientsTasks[i].setStartIndex(i*eachOne);
            clientsTasks[i].setEndIndex(i*eachOne+eachOne);
        }
        clientsTasks[clientsTasks.length-1].setStartIndex((clientsTasks.length-1)*eachOne);
        clientsTasks[clientsTasks.length-1].setEndIndex(clients.size());
    }

    public void setNomeServer(String nomeServer){
        this.nomeServer = nomeServer;
    }

    public String getNomeServer(){
        return nomeServer;
    }

    public int getClientCount(){
        return clients.size();
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void addClient(Client client){
        clients.add(client);
        updateIndexes();
    }

    public void removeClient(Client client){
        clients.remove(client);
        updateIndexes();
    }

    public void updateClients(String[] comandi){
        new Thread(new UpdateClientsThread(clients, comandi)).start();
    }

    public Messaggio getMessaggioById(int idMessaggio){
        for(int i=messaggi.size()-1; i>=0; i--){
            if(messaggi.get(i).getId() == idMessaggio) {
                return messaggi.get(i);
            }
        }
        return null;
    }

    public void addMessaggio(Messaggio messaggio){
        messaggio.setId(++lastIdMessaggi);
        messaggi.add(messaggio);
        for (int i=0; i<clients.size(); i++){
            clients.get(i).invia("newMessaggio");
            clients.get(i).invia(messaggio.getId()+"");
            clients.get(i).invia(messaggio.getAutore());
            clients.get(i).invia(messaggio.getTesto());
            clients.get(i).invia(messaggio.getData());
            clients.get(i).invia(messaggio.getColore()+"");
            clients.get(i).invia(messaggio.getMessaggiRisposta().size()+""); //Numero risposte ai messaggi
            for(int j=0; j<messaggio.getMessaggiRisposta().size(); j++)
                clients.get(i).invia(messaggio.getMessaggiRisposta().get(j).getId()+""); //Id messaggio risposta
        }
    }
    public void removeMessaggio(int idMessaggio){
        Messaggio messaggio = null;

        messaggio = getMessaggioById(idMessaggio);

        if(messaggio == null)
            return;

        messaggio.remove();
        for (int i=0; i<clients.size(); i++){
            clients.get(i).invia("removeMessaggio");
            clients.get(i).invia(messaggio.getId()+"");
        }
    }

    public int getPortServer(){
        return portServer;
    }

    public void execute(Runnable task){
        executorService.execute(task);
    }

    public boolean isStarted(){
        return started;
    }

    public void closeServer(){
        for(int i=0; i<clientsTasks.length; i++){
            clientsTasks[i].interrupt();
        }
        try {
            server.close();
        } catch (IOException e) {e.printStackTrace();}
    }
}
