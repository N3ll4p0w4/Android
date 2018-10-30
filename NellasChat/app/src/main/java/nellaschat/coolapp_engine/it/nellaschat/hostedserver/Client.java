package nellaschat.coolapp_engine.it.nellaschat.hostedserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {

    private Socket client;
    private HostedServer hostedServer;

    private String nome = "";

    private BufferedReader in;
    private PrintWriter out;

    private int colore;

    private boolean logged;
    private boolean running;

    public Client(HostedServer hostedServer, Socket client){
        this.hostedServer = hostedServer;
        this.client = client;
        running = false;
        logged = false;

        try {
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        try {
            out = new PrintWriter(client.getOutputStream());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void gestisci() {
        running = true;

        hostedServer.execute(new ClientTask(hostedServer, this));
    }

    public boolean isReady(){
        try {
            return in.ready();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void invia(String data) {
        data = data.replaceAll("\n", "<NEWLINE>");
        out.println(data);
        out.flush();
    }

    public String ricevi() {
        String s = null;
        try {
            s = in.readLine();

            s = s.replaceAll("<NEWLINE>", "\n");

        } catch (IOException e) {
            e.printStackTrace();
        }
        return s;
    }

    public void disconnetti(){
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        hostedServer.removeClient(this);
    }

    public Socket getSocket() {
        return client;
    }

    public void setRunning(boolean running){
        this.running = running;
    }

    public boolean isRunning(){
        return running;
    }

    public void setLogged(boolean logged){
        this.logged = logged;
    }

    public boolean isLogged(){
        return logged;
    }

    public int getColore() {
        return colore;
    }

    public void setColore(int colore) {
        this.colore = colore;
    }
}
