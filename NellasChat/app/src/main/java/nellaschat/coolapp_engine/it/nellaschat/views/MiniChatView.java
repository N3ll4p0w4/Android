package nellaschat.coolapp_engine.it.nellaschat.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import nellaschat.coolapp_engine.it.nellaschat.R;
import nellaschat.coolapp_engine.it.nellaschat.fragments.TabFragment;

public class MiniChatView extends LinearLayout {

    private String titolo;
    private int clientConnessi;

    private View view;
    private LayoutInflater layoutInflater;
    private RelativeLayout relativeLayout;
    private TextView titoloText, clientConnessiText;

    private TabFragment tabFragment;

    public MiniChatView(Context context) {
        super(context);

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = layoutInflater.inflate(R.layout.mini_chat_layout, this);

        titoloText = view.findViewById(R.id.chat_title);
        clientConnessiText = view.findViewById(R.id.client_connessi);

        relativeLayout = view.findViewById(R.id.mini_chat_relative_layout);
    }

    public View getView() {
        return view;
    }

    public void setTitolo(String titolo){
        this.titolo = titolo;
    }

    public String getTitolo(){
        return titolo;
    }

    public void setClientConnessi(int clientConnessi){
        this.clientConnessi = clientConnessi;
    }

    public int getClientConnessi(){
        return clientConnessi;
    }

    public void refresh(){
        this.post(() -> {
            titoloText.setText(titolo);
            clientConnessiText.setText("Connessi: "+clientConnessi);
        });
    }

    public void setOnClickListener(OnClickListener onClickListener){
        relativeLayout.setOnClickListener(onClickListener);
    }

    public void setActivity(TabFragment tabFragment){
        this.tabFragment = tabFragment;
    }

}
