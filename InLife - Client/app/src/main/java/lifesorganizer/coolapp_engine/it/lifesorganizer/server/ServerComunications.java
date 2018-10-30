package lifesorganizer.coolapp_engine.it.lifesorganizer.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;

public class ServerComunications extends Thread {
    private static final ServerComunications ourInstance = new ServerComunications();

    public static Socket server;
    private static int timeoutConnection;
    public static String ipServer;
    public static int portServer;

    public static boolean started;

    private static ObjectOutputStream out;
    private static ObjectInputStream in;

    public static ServerComunications getInstance() {
        return ourInstance;
    }

    private ServerComunications() {
        ipServer = "lifesorganizer2.ddns.net";
        portServer = 9000;
        timeoutConnection = 1000;
        started = false;
    }

    @Override
    public void run(){
        started = true;
        while(true) {
            if (server == null || !server.isConnected()) {
                try {
                    server = new Socket();
                    server.connect(new InetSocketAddress(ipServer, portServer), timeoutConnection);
                    server.setTcpNoDelay(true);

                    out = new ObjectOutputStream(server.getOutputStream());
                    in = new ObjectInputStream(server.getInputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean isStarted(){
        return started;
    }

    public static boolean invia(String data){
        try {
            out.writeUTF(data);
            out.flush();
        }  catch(SocketException ex) {
            System.out.println("Errore nel Socket del Client");
            ex.printStackTrace();
            return false;
        }catch(IOException ex) {
            System.out.println("Errore nel DataOutputStream");
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    public static String ricevi(){
        try {
            String s = in.readUTF();
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

    public static void inviaObject(Object object){
        try {
            out.writeObject(object);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Object riceviObject(){
        try {
            return in.readObject();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
