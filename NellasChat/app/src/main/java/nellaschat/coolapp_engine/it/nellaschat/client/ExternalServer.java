package nellaschat.coolapp_engine.it.nellaschat.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ExternalServer extends Thread {

    private Socket server;
    private int timeoutConnection;
    private String ipServer;
    private int portServer;

    private boolean started;

    private BufferedReader in;
    private PrintWriter out;

    private boolean running;

    public ExternalServer(int portServer) {
        ipServer = "localhost";
        this.portServer = portServer;
        timeoutConnection = 1000;
        started = false;
        running = false;
    }

    @Override
    public void run(){
        started = true;
        while(true) {
            if (server == null || !server.isConnected()) {
                try {
                    server = new Socket(ipServer, portServer);

                    in = new BufferedReader(new InputStreamReader(server.getInputStream()));
                    out = new PrintWriter(server.getOutputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else if(server.isClosed())
                break;
        }
    }

    public boolean isConnected(){
        if(server == null)
            return false;
        return server.isConnected();
    }

    public  boolean isStarted(){
        return started;
    }

    public int getPortServer(){
        return portServer;
    }

    public boolean isReady(){
        try {
            return in.ready();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public  void invia(String data){
        if(out == null) {
            try {
                out = new PrintWriter(server.getOutputStream());
            } catch (IOException e) {e.printStackTrace();}
        }
        data = data.replaceAll("\n", "<NEWLINE>");
        out.println(data);
        out.flush();
    }

    public String ricevi(){
        String s = null;
        try {
            if(in == null) {
                try {
                    in = new BufferedReader(new InputStreamReader(server.getInputStream()));
                } catch (IOException e) {e.printStackTrace();}
            }
            s = in.readLine();

            s = s.replaceAll("<NEWLINE>", "\n");

        } catch (IOException e) {
            e.printStackTrace();
        }
        return s;
    }

    public void disconnetti(){
        if(server != null) {
            try {
                invia("disconnect");
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running){
        this.running = running;
    }
}
