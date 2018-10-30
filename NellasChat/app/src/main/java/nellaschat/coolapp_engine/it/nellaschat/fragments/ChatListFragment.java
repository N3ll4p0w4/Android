package nellaschat.coolapp_engine.it.nellaschat.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;

import nellaschat.coolapp_engine.it.nellaschat.R;
import nellaschat.coolapp_engine.it.nellaschat.views.MiniChatView;
import nellaschat.coolapp_engine.it.nellaschat.client.ExternalServer;
import nellaschat.coolapp_engine.it.nellaschat.client.ServerAbstact;

public class ChatListFragment extends Fragment {

    private String titolo;

    private View view;
    private SwipeRefreshLayout srl;
    private LinearLayout chatContainer;
    private Button disconnettitiButton;

    private ArrayList<MiniChatView> chats = new ArrayList<>();

    private ArrayList<ExternalServer> servers = new ArrayList<>();

    private TabFragment tabFragment;

    private boolean refreshing = false;

    public ChatListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(view == null) {
            view = inflater.inflate(R.layout.chat_list_layout, container, false);

            srl = view.findViewById(R.id.swiperefresh);
            srl.setOnRefreshListener(() -> {
                refresh();
            });

            disconnettitiButton = view.findViewById(R.id.disconnettiti_button);
            disconnettitiButton.setOnClickListener(view -> {
                disconnettiti();
            });

            chatContainer = view.findViewById(R.id.chat_container);
        }

        tabFragment = (TabFragment) getParentFragment();

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void disconnettiti(){
        tabFragment.disconnettiti();
    }

    public void setTitolo(String titolo){
        this.titolo = titolo;
    }

    public String getTitolo(){
        return titolo;
    }

    public TabFragment getTabFragment() {
        return tabFragment;
    }

    public void setTabFragment(TabFragment tabFragment) {
        this.tabFragment = tabFragment;
    }

    public void refresh(){
        if(refreshing)
            return;

        refreshing = true;
        srl.setRefreshing(true);

        int[] ports = tabFragment.getMainActivity().getPortServers();
        servers.clear();
        for(int i=0; i<ports.length; i++){
            ExternalServer es = new ExternalServer(ports[i]);
            servers.add(es);
            es.start();
        }

        for (int i=chatContainer.getChildCount()-1; i >= ports.length; i--) {
            chatContainer.removeViewAt(i);
        }
        for (int i=chats.size(); i < ports.length; i++) {
            MiniChatView mcf = new MiniChatView(getContext());
            mcf.setOnClickListener(view -> {
                tabFragment.entraChat(servers.get(chats.indexOf(mcf)).getPortServer());
            });
            chats.add(mcf);
        }
        for (int i=chatContainer.getChildCount(); i < ports.length; i++) {
            if(chats.get(i).getParent() != null)
                ((LinearLayout)chats.get(i).getParent()).removeAllViews();
            chatContainer.addView(chats.get(i));
        }

        new ServerAbstact() {
            @Override
            public void run() {
                for(int i=0; i<servers.size(); i++){
                    while(!servers.get(i).isConnected()){
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {e.printStackTrace();}
                    }
                    setExternalServer(servers.get(i));
                    invia("getNomeServer");
                    chats.get(i).setTitolo(ricevi());
                    invia("getClientCount");
                    chats.get(i).setClientConnessi(Integer.parseInt(ricevi()) - 1);
                    chats.get(i).refresh();
                    servers.get(i).disconnetti();
                }
                srl.setRefreshing(false);
                refreshing = false;
            }
        }.start();

    }
}
