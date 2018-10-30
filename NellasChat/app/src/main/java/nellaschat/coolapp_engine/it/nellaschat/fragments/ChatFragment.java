package nellaschat.coolapp_engine.it.nellaschat.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import nellaschat.coolapp_engine.it.nellaschat.R;
import nellaschat.coolapp_engine.it.nellaschat.client.ExternalServer;
import nellaschat.coolapp_engine.it.nellaschat.client.ServerAbstact;
import nellaschat.coolapp_engine.it.nellaschat.chat.Messaggio;
import nellaschat.coolapp_engine.it.nellaschat.views.ChatAvviso;
import nellaschat.coolapp_engine.it.nellaschat.views.MessaggioView;
import nellaschat.coolapp_engine.it.nellaschat.views.RispostaMessaggioView;

public class ChatFragment extends Fragment {

    private View view;
    private Toolbar toolbar;
    private LinearLayout messaggiContainer, risposteContainter;
    private ScrollView scrollViewMessaggi;
    private ConstraintLayout constraintInvia, constraintRisposte;
    private ImageButton inviaMessaggioButton, inviaAltroButon;
    private EditText messaggioEditText;

    private ExternalServer server;

    private ArrayList<MessaggioView> messaggiViews = new ArrayList<>();
    private ArrayList<MessaggioView> messaggiViewsSelezionati = new ArrayList<>();
    private boolean selecting;
    private ArrayList<ChatAvviso> avvisi = new ArrayList<>();
    private ArrayList<RispostaMessaggioView> messaggiRispostaViews = new ArrayList<>();


    private TabFragment tabFragment;

    private ServerAbstact serverListener;

    public ChatFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(view == null) {
            view = inflater.inflate(R.layout.chat_layout, container, false);

            messaggiContainer = view.findViewById(R.id.chat_linearlayout_messaggi);
            risposteContainter = view.findViewById(R.id.chat_risposte_container);
            scrollViewMessaggi = view.findViewById(R.id.scrollview_messaggi);
            constraintRisposte = view.findViewById(R.id.chat_container_risposta_messaggi);
            constraintInvia = view.findViewById(R.id.chat_invia_container2);
            inviaMessaggioButton = view.findViewById(R.id.chat_invia_messaggio);
            inviaAltroButon = view.findViewById(R.id.chat_invia_altro);
            messaggioEditText = view.findViewById(R.id.chat_edittext_messaggio);

            inviaMessaggioButton.setOnClickListener(view -> {
                inviaMessaggioClicked();
            });

            inviaAltroButon.setOnClickListener(view -> {
                inviaAltroClicked();
            });

            toolbar = view.findViewById(R.id.chat_toolbar);
            toolbar.setTitle("");
            //Pulsante indietro toolbar
            toolbar.setNavigationIcon(getResources().getDrawable(android.support.v7.appcompat.R.drawable.abc_ic_ab_back_material));
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    serverListener.interrupt();
                    new ServerAbstact(){
                        @Override
                        public void run() {
                            server.setRunning(true);
                            server.disconnetti();
                            server.setRunning(false);
                            tabFragment.esciChat();
                        }
                    }.start();
                }
            });

            selecting = false;
        }

        tabFragment = (TabFragment) getParentFragment();

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        messaggiViews.clear();
        messaggiViewsSelezionati.clear();
        avvisi.clear();
        messaggiRispostaViews.clear();
        if(messaggiContainer != null) {
            messaggiContainer.removeAllViews();

            ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) constraintRisposte.getLayoutParams();
            lp.height = (int) getContext().getResources().getDimension(R.dimen.contenitoreRisposteMessaggioInChatChiuso);
            constraintRisposte.setLayoutParams(lp);

            constraintInvia.setBackground(getContext().getResources().getDrawable(R.drawable.chat_input_complete_rounded));
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public ExternalServer getExternalServer() {
        return server;
    }

    public void setExternalServerPort(int portExternalServer){
        server = new ExternalServer(portExternalServer);
        server.start();

        startServerListener();
    }

    public void setTabFragment(TabFragment tabFragment){
        this.tabFragment = tabFragment;
    }

    public TabFragment getTabFragment(){
        return tabFragment;
    }

    public void startServerListener(){
        serverListener = new ServerAbstact(server) {
            @Override
            public void run() {
                while(!server.isConnected() || toolbar == null){
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {e.printStackTrace();}
                }
                server.setRunning(true);
                invia("getNomeServer");
                final String titolo = ricevi();
                toolbar.post(() -> toolbar.setTitle(titolo));
                invia("getClientCount");
                final String clientCounter = ricevi();
                toolbar.post(() -> toolbar.setSubtitle("Connessi: "+clientCounter));
                invia("setNomeClient");
                invia(tabFragment.getNomeClient());
                server.setRunning(false);
                while(!isInterrupted()){
                    if(isReady() && !server.isRunning()){
                        server.setRunning(true);
                        String mess = ricevi();
                        if(mess.equals("newMessaggio")){
                            Messaggio messaggio = new Messaggio();
                            messaggio.setId(Integer.parseInt(ricevi()));
                            messaggio.setAutore(ricevi());
                            messaggio.setTesto(ricevi());
                            messaggio.setData(ricevi());
                            int posColore = Integer.parseInt(ricevi());
                            int nRisposte = Integer.parseInt(ricevi());
                            for (int i=0; i<nRisposte; i++) {
                                Messaggio rispostaMessaggio = getMessaggioById(Integer.parseInt(ricevi()));
                                if(rispostaMessaggio != null)
                                    messaggio.addMessaggioRisposta(rispostaMessaggio);
                            }
                            aggiungiMessaggioRicevuto(messaggio, posColore);
                        }
                        if(mess.equals("removeMessaggio")){
                            int idMessaggio = Integer.parseInt(ricevi());
                            rimuoviMessaggioUI(idMessaggio);
                        }
                        if(mess.equals("setClientCount")){
                            final String clientCounter2 = ricevi();
                            toolbar.post(() -> toolbar.setSubtitle("Connessi: "+clientCounter2));
                        }
                        if(mess.equals("newAvviso")){
                            mess = ricevi();
                            if(mess.equals("clientConnected")){
                                mess = ricevi();
                                if(mess.equals(tabFragment.getNomeClient()))
                                    aggiungiAvvisoRicevuto("Ti sei connesso");
                                else aggiungiAvvisoRicevuto(mess+" si è connesso");
                            }
                            if(mess.equals("clientDisconnected")){
                                mess = ricevi();
                                aggiungiAvvisoRicevuto(mess+" si è disconnesso");
                            }
                        }
                        server.setRunning(false);
                    }
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {e.printStackTrace();}
                }
            }
        };
        serverListener.start();
    }

    public Messaggio getMessaggioById(int idMessaggio){
        for(int i=messaggiViews.size()-1; i>=0; i--){
            if(messaggiViews.get(i).getIdMessaggio() == idMessaggio) {
                return messaggiViews.get(i).getMessaggio();
            }
        }
        return null;
    }

    public MessaggioView getMessaggioViewById(int idMessaggio){
        for(int i=messaggiViews.size()-1; i>=0; i--){
            if(messaggiViews.get(i).getIdMessaggio() == idMessaggio) {
                return messaggiViews.get(i);
            }
        }
        return null;
    }

    public void inviaMessaggioClicked(){
        if(!messaggioEditText.getText().toString().trim().isEmpty()) {
            new ServerAbstact(server) {
                @Override
                public void run() {
                    while(!server.isConnected()){
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {e.printStackTrace();}
                    }
                    while(server.isRunning()){
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {e.printStackTrace();}
                    }
                    server.setRunning(true);

                    invia("newMessaggio");
                    invia(messaggioEditText.getText().toString());
                    invia(messaggiRispostaViews.size()+""); //Numero risposte ai messaggi
                    for(int i=0; i<messaggiRispostaViews.size(); i++)
                        invia(messaggiRispostaViews.get(i).getIdMessaggio()+""); //Id messaggio risposta

                    server.setRunning(false);
                    messaggioEditText.post(() -> {
                        messaggioEditText.setText("");
                    });
                    clearRispostaMessaggioViews();
                }
            }.start();
        }
    }

    public void inviaAltroClicked(){
        MaterialDialog md = new MaterialDialog.Builder(getContext())
            .items(new String[]{"Invia una foto", "Invia una canzone", "Invia un file"})
            .cancelable(true)
            .show();
    }

    public void aggiungiMessaggioRicevuto(Messaggio messaggio, int posColore) {
        MessaggioView messaggioView = null;
        if(messaggio.getAutore().equals(tabFragment.getNomeClient())){
            messaggioView = new MessaggioView(this, MessaggioView.MessaggioUscita);
            messaggioView.setMessaggio(messaggio);
            messaggiViews.add(messaggioView);
            MessaggioView finalMessaggioView1 = messaggioView;
            view.post(() -> {
                messaggiContainer.addView(finalMessaggioView1);
                messaggioEditText.setText("");
                finalMessaggioView1.requestFocus();
                messaggioEditText.requestFocus();
            });
        } else {
            messaggioView = new MessaggioView(this, MessaggioView.MessaggioEntrata);
            messaggioView.setMessaggio(messaggio);
            messaggiViews.add(messaggioView);
            MessaggioView finalMessaggioView = messaggioView;
            view.post(() -> {
                messaggiContainer.addView(finalMessaggioView);
            });

            int pos = messaggiViews.indexOf(messaggioView);
            if(pos > 0 && messaggiViews.get(pos-1).getAutore().equals(messaggioView.getAutore())){
                messaggioView.nascondiAutore();
            }
        }
        messaggioView.setColore(getResources().getIntArray(R.array.colori_nomi_utenti_messaggi)[posColore]);

        for(int i=0; i<messaggio.getMessaggiRisposta().size(); i++){
            MessaggioView messaggioView1 = getMessaggioViewById(messaggio.getMessaggiRisposta().get(i).getId());
            if(messaggioView1 != null) {
                RispostaMessaggioView rispostaMessaggioView = new RispostaMessaggioView(this,
                        RispostaMessaggioView.RispostaInMessaggio, messaggioView1);
                messaggioView.addMessaggiRispostaView(rispostaMessaggioView);
            }
        }
    }

    public void rimuoviMessaggioUI(int idMessaggio){
        MessaggioView messaggioView = null;

        for(int i=messaggiViews.size()-1; i>=0; i--){
            if(messaggiViews.get(i).getIdMessaggio() == idMessaggio) {
                messaggioView = messaggiViews.get(i);
                break;
            }
        }

        if(messaggioView == null)
            return;

        messaggioView.rimuoviMessaggio();
    }

    public void addMessaggioSelezionato(MessaggioView messaggioView){
        selecting = true;
        if(messaggiViewsSelezionati.size() == 0){
            setMenuToolbarWhenStartSelection();
        }

        messaggiViewsSelezionati.add(messaggioView);
    }

    public void removeMessaggioSelezionato(MessaggioView messaggioView){
        messaggiViewsSelezionati.remove(messaggioView);
        if(messaggiViewsSelezionati.size() == 0) {
            selecting = false;
            toolbar.getMenu().clear();
        }
    }

    public void deselectMessaggiSelezinati(){
        for(int i=0; i<messaggiViewsSelezionati.size(); i++){
            messaggiViewsSelezionati.get(i).deseleziona();
        }
        messaggiViewsSelezionati.clear();
        selecting = false;
    }

    public void addRispostaMessaggioView(RispostaMessaggioView rispostaMessaggioView){
        if(messaggiRispostaViews.size() == 0){
            ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) constraintRisposte.getLayoutParams();
            lp.height = (int) getContext().getResources().getDimension(R.dimen.contenitoreRisposteMessaggioInChatAperto);
            constraintRisposte.setLayoutParams(lp);

            constraintInvia.setBackground(getContext().getResources().getDrawable(R.drawable.chat_input_half_rounded));
        }

        messaggiRispostaViews.add(rispostaMessaggioView);
        risposteContainter.post(() -> risposteContainter.addView(rispostaMessaggioView));
    }

    public void removeRispostaMessaggioView(RispostaMessaggioView rispostaMessaggioView){
        rispostaMessaggioView.getMessaggioView().removeObservable(rispostaMessaggioView);
        messaggiRispostaViews.remove(rispostaMessaggioView);

        if(messaggiRispostaViews.size() == 0) {
            ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) constraintRisposte.getLayoutParams();
            lp.height = (int) getContext().getResources().getDimension(R.dimen.contenitoreRisposteMessaggioInChatChiuso);
            constraintRisposte.setLayoutParams(lp);

            constraintInvia.setBackground(getContext().getResources().getDrawable(R.drawable.chat_input_complete_rounded));
        }

        risposteContainter.post(() -> risposteContainter.removeView(rispostaMessaggioView));
    }

    public void clearRispostaMessaggioViews(){
        for(int i=messaggiRispostaViews.size()-1; i>=0; i--){
            messaggiRispostaViews.get(i).getMessaggioView().removeObservable(messaggiRispostaViews.get(i));
            messaggiRispostaViews.remove(i);
        }

        ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) constraintRisposte.getLayoutParams();
        lp.height = (int) getContext().getResources().getDimension(R.dimen.contenitoreRisposteMessaggioInChatChiuso);
        constraintRisposte.post(() -> constraintRisposte.setLayoutParams(lp));

        constraintInvia.post(() -> constraintInvia.setBackground(getContext().getResources().getDrawable(R.drawable.chat_input_complete_rounded)));

        risposteContainter.post(() -> risposteContainter.removeAllViews());
    }

    public void setMenuToolbarWhenStartSelection(){
        toolbar.inflateMenu(R.menu.chat_messaggi_selezionati_menu);
        toolbar.setOnMenuItemClickListener(menuItem -> {
            if(menuItem.getItemId() == R.id.menu_chat_deseleziona_messaggi) {
                deselectMessaggiSelezinati();
            }
            else if(menuItem.getItemId() == R.id.menu_chat_rispondi_messaggi){
                for(int i=0; i<messaggiViewsSelezionati.size(); i++){
                    RispostaMessaggioView rispostaMessaggioView = new RispostaMessaggioView(this,
                            RispostaMessaggioView.RispostaInChat, messaggiViewsSelezionati.get(i));
                    messaggiViewsSelezionati.get(i).addObservable(rispostaMessaggioView);
                    addRispostaMessaggioView(rispostaMessaggioView);
                }
                deselectMessaggiSelezinati();
            }
            else if(menuItem.getItemId() == R.id.menu_chat_rimuovi_messaggi) {
                ExecutorService executorService = Executors.newFixedThreadPool(1);
                ArrayList<MessaggioView> mv = new ArrayList<>();
                mv.addAll(messaggiViewsSelezionati);
                for(int i=0; i<mv.size(); i++){
                    if(mv.get(i).getTipo() == MessaggioView.MessaggioUscita){
                        int finalI = i;
                        ServerAbstact sa = new ServerAbstact(server){
                            @Override
                            public void run() {
                                while(server.isRunning()){
                                    try {
                                        Thread.sleep(50);
                                    } catch (InterruptedException e) {e.printStackTrace();}
                                }
                                server.setRunning(true);
                                invia("removeMessaggio");
                                invia(mv.get(finalI).getIdMessaggio()+"");
                                server.setRunning(false);
                            }
                        };
                        executorService.execute(sa);
                    }
                }
                deselectMessaggiSelezinati();
            }

            toolbar.setOnMenuItemClickListener(null);
            toolbar.getMenu().clear();

            return false;
        });
    }

    public boolean isSelecting() {
        return selecting;
    }

    public void aggiungiAvvisoRicevuto(String testoAvviso) {
        ChatAvviso chatAvviso = new ChatAvviso(getContext());
        chatAvviso.setTesto(testoAvviso);
        avvisi.add(chatAvviso);
        view.post(() -> {
            messaggiContainer.addView(chatAvviso);
        });
    }

}
