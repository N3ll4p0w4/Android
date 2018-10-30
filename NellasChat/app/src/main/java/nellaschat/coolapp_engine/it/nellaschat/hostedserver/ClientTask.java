package nellaschat.coolapp_engine.it.nellaschat.hostedserver;

import java.util.Calendar;
import java.util.Random;

import nellaschat.coolapp_engine.it.nellaschat.R;
import nellaschat.coolapp_engine.it.nellaschat.chat.Messaggio;

public class ClientTask implements Runnable {

    private Client client;
    private HostedServer hostedServer;

    public ClientTask(HostedServer hostedServer, Client client){
        this.hostedServer = hostedServer;
        this.client = client;
    }

    @Override
    public void run() {
        String s = client.ricevi();
        if(s.equals("getNomeServer"))
            client.invia(hostedServer.getNomeServer());
        if(s.equals("getClientCount"))
            client.invia(hostedServer.getClientCount()+"");

        if(s.equals("setNomeClient")) {
            client.setNome(client.ricevi());
            client.setLogged(true);
            client.setColore(new Random().nextInt(hostedServer.getContext().getResources().getIntArray(R.array.colori_nomi_utenti_messaggi).length));
            hostedServer.updateClients(new String[]{"setClientCount", ""+hostedServer.getClientCount(),
                "newAvviso", "clientConnected", client.getNome()});
        }

        if(s.equals("newMessaggio")){
            String testo = client.ricevi();
            Messaggio messaggio = new Messaggio();
            messaggio.setAutore(client.getNome());
            messaggio.setTesto(testo);
            messaggio.setData(Calendar.getInstance().getTime());
            messaggio.setColore(client.getColore());
            int nRisposte = Integer.parseInt(client.ricevi());
            for (int i=0; i<nRisposte; i++)
                messaggio.addMessaggioRisposta(hostedServer.getMessaggioById(Integer.parseInt(client.ricevi())));
            hostedServer.addMessaggio(messaggio);
        }

        if(s.equals("removeMessaggio")){
            int idMessaggio = Integer.parseInt(client.ricevi());
            hostedServer.removeMessaggio(idMessaggio);
        }

        if(s.equals("disconnect")){
            client.disconnetti();
            if(client.isLogged()){
                hostedServer.updateClients(new String[]{"setClientCount", ""+hostedServer.getClientCount(),
                        "newAvviso", "clientDisconnected", client.getNome()});
                client.setLogged(false);
            }
        }

        client.setRunning(false);
    }
}
