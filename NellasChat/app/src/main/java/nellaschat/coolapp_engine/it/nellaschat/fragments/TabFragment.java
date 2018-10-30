package nellaschat.coolapp_engine.it.nellaschat.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import nellaschat.coolapp_engine.it.nellaschat.MainActivity;
import nellaschat.coolapp_engine.it.nellaschat.R;
import nellaschat.coolapp_engine.it.nellaschat.client.ExternalServer;
import nellaschat.coolapp_engine.it.nellaschat.hostedserver.HostedServer;

public class TabFragment extends Fragment {

    private String nomeClient;

    private View view;
    private FrameLayout frameLayout;

    private HostedServer hostedServer;

    private MainActivity mainActivity;

    private ChatListFragment chatListFragment;
    private ChatFragment chatFragment;

    public TabFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(view == null) {
            view = inflater.inflate(R.layout.tabfragment_layout, container, false);

            frameLayout = view.findViewById(R.id.framelayout);
            chatListFragment = new ChatListFragment();
            chatFragment = new ChatFragment();
            getChildFragmentManager().beginTransaction().replace(R.id.framelayout, chatListFragment).commit();
        }

        mainActivity = (MainActivity)getActivity();

        createHostServer();

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

    public void setNomeClient(String nomeClient){
        this.nomeClient = nomeClient;
    }

    public String getNomeClient(){
        return nomeClient;
    }

    public void createHostServer(){
        if(hostedServer == null || !hostedServer.isStarted()) {
            hostedServer = new HostedServer(mainActivity.getNewPortServer(), getContext());
            hostedServer.setNomeServer(nomeClient+"'s Server");
            hostedServer.start();
        }
    }

    public HostedServer getHostedServer(){
        createHostServer();
        return hostedServer;
    }

    public MainActivity getMainActivity() {
        return mainActivity;
    }

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public void disconnettiti(){
        mainActivity.removeTab(this);
        hostedServer.closeServer();
    }

    public void entraChat(int portServerChat){
        getChildFragmentManager().beginTransaction().replace(R.id.framelayout, chatFragment).commit();
        chatFragment.setExternalServerPort(portServerChat);
    }

    public void esciChat(){
        getChildFragmentManager().beginTransaction().replace(R.id.framelayout, chatListFragment).commit();
    }

    public void refresh() {
        chatListFragment.refresh();
    }
}
