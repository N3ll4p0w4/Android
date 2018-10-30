package tris.daminatoluca.it.tris.OnlineClasses.SocketUtils;

import android.os.AsyncTask;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by pigro on 13/02/2018.
 */

public class SendToSocket extends AsyncTask<String, Void, Void> {

    private Socket socket;
    private DataOutputStream out;

    public SendToSocket(Socket socket){
        this.socket = socket;
    }

    @Override
    public Void doInBackground(String... data) {
        try {
            out = new DataOutputStream(socket.getOutputStream());
            out.writeUTF(data[0]);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
