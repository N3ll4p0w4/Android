package tris.daminatoluca.it.tris.OnlineClasses.SocketUtils;

import android.os.AsyncTask;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by pigro on 13/02/2018.
 */

public class ReadFromSocket extends AsyncTask<Void, Void, String> {

    private Socket socket;
    private DataInputStream in;

    public ReadFromSocket(Socket socket){
        this.socket = socket;
    }

    @Override
    public String doInBackground(Void... voids) {
        try {
            in = new DataInputStream(socket.getInputStream());
            String read = in.readUTF();
            while(read == null)
                read = in.readUTF();
            return read;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public void onPostExecute(String result) {
        super.onPostExecute(result);
    }
}
