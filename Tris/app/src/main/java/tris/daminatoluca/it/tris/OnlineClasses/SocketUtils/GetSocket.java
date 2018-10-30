package tris.daminatoluca.it.tris.OnlineClasses.SocketUtils;

import android.os.AsyncTask;
import android.widget.Toast;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import tris.daminatoluca.it.tris.MainActivity;

/**
 * Created by pigro on 13/02/2018.
 */

public class GetSocket extends AsyncTask<Void, Void, Void> {

    private String ip;
    private int port;
    private int timeout;

    private Socket socket;

    public GetSocket(Socket socket, String ip, int port, int timeout){
        this.socket = socket;
        this.ip = ip;
        this.port = port;
        this.timeout = timeout;
    }

    @Override
    protected Void doInBackground(Void... strings) {
        try {
            socket.connect(new InetSocketAddress(ip, port), timeout);
            System.out.println("Server: Connesso!");
        } catch (IOException e) {
            System.out.println("Errore Socket!");
            e.printStackTrace();
        }
        return null;
    }
}
