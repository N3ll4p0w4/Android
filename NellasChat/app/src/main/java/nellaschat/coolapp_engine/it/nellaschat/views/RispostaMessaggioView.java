package nellaschat.coolapp_engine.it.nellaschat.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Date;

import nellaschat.coolapp_engine.it.nellaschat.R;
import nellaschat.coolapp_engine.it.nellaschat.chat.Messaggio;
import nellaschat.coolapp_engine.it.nellaschat.fragments.ChatFragment;
import nellaschat.coolapp_engine.it.nellaschat.pattern.Observer;

public class RispostaMessaggioView extends FrameLayout implements Observer {

    private View view;
    private LayoutInflater layoutInflater;

    private int tipoRisposta;
    public static final int RispostaInChat = 0, RispostaInMessaggio = 1;

    private FrameLayout frameLayout;
    private LinearLayout linearLayout, layoutColore;
    private RelativeLayout relativeLayout;
    private TextView autoreTextView, messaggioTextView;

    private MessaggioView messaggioView;

    private ChatFragment chatFragment;

    @SuppressLint("ClickableViewAccessibility")
    public RispostaMessaggioView(ChatFragment chatFragment, int tipoRisposta, MessaggioView messaggioView) {
        super(chatFragment.getContext());
        this.chatFragment = chatFragment;
        this.tipoRisposta = tipoRisposta;

        this.messaggioView = messaggioView;

        layoutInflater = (LayoutInflater) chatFragment.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(tipoRisposta == RispostaMessaggioView.RispostaInChat) {
            view = layoutInflater.inflate(R.layout.messaggio_risposta_inchat, this);

            ImageButton removeButton = view.findViewById(R.id.chat_risposta_inchat_rimuovi);
            removeButton.setOnClickListener(view -> chatFragment.removeRispostaMessaggioView(this));
        }
        else if(tipoRisposta == RispostaMessaggioView.RispostaInMessaggio){
            view = layoutInflater.inflate(R.layout.messaggio_risposta_inmessaggio, this);
        }
        else return;

        messaggioTextView = view.findViewById(R.id.testo_messaggio);

        frameLayout = view.findViewById(R.id.chat_framelayout);
        linearLayout = view.findViewById(R.id.chat_linearlayout);
        layoutColore = view.findViewById(R.id.messaggio_risposta_colore);
        relativeLayout = view.findViewById(R.id.chat_relativelayout);

        autoreTextView = view.findViewById(R.id.testo_autore_messaggio);
        if(messaggioView.getTipo() == MessaggioView.MessaggioEntrata)
            autoreTextView.post(() -> autoreTextView.setTextColor(chatFragment.getContext().getResources().getColor(R.color.verde_scuro)));


        if(messaggioView.getTipo() == MessaggioView.MessaggioEntrata) {
            GradientDrawable drawable = (GradientDrawable) frameLayout.getBackground();
            drawable.setColor(chatFragment.getContext().getResources().getColor(R.color.coloreMessaggioRispostaEntrata));
            frameLayout.setBackground(drawable);
        }
        else if(messaggioView.getTipo() == MessaggioView.MessaggioUscita){
            GradientDrawable drawable = (GradientDrawable) frameLayout.getBackground();
            drawable.setColor(chatFragment.getContext().getResources().getColor(R.color.coloreMessaggioRispostaUscita));
            frameLayout.setBackground(drawable);
        }
        else return;

        update();
    }

    public View getView() {
        return view;
    }

    public int getTipoRisposta(){
        return tipoRisposta;
    }

    public int getTipoMessaggio(){
        return messaggioView.getTipo();
    }

    public Messaggio getMessaggio() {
        return messaggioView.getMessaggio();
    }

    public MessaggioView getMessaggioView(){
        return messaggioView;
    }

    public void setMessaggioView(MessaggioView messaggioView) {
        this.messaggioView = messaggioView;
        setAutore(messaggioView.getMessaggio().getAutore());
        setTesto(messaggioView.getMessaggio().getTesto());
        setColore(messaggioView.getMessaggio().getColore());
    }

    public int getIdMessaggio() {
        return messaggioView.getMessaggio().getId();
    }

    private void setIdMessaggio(int id) {
        messaggioView.getMessaggio().setId(id);
    }

    public String getAutore() {
        return messaggioView.getMessaggio().getAutore();
    }

    private void setAutore(String autore) {
        if(messaggioView.getTipo() == MessaggioView.MessaggioUscita)
            autoreTextView.post(() -> autoreTextView.setText("Tu"));
        else autoreTextView.post(() -> autoreTextView.setText(messaggioView.getMessaggio().getAutore()));
    }

    public String getTesto() {
        return messaggioView.getMessaggio().getTesto();
    }

    private void setTesto(String testo) {
        messaggioTextView.post(() -> {
            messaggioTextView.setText(messaggioView.getMessaggio().getTesto());
        });
    }

    public String getData() {
        return messaggioView.getMessaggio().getData();
    }

    public int getColore(){
        return messaggioView.getMessaggio().getColore();
    }

    public void setColore(int colore){
        if(messaggioView.getTipo() == MessaggioView.MessaggioEntrata) {
            autoreTextView.post(() -> autoreTextView.setTextColor(messaggioView.getMessaggio().getColore()));

            GradientDrawable drawable = (GradientDrawable) layoutColore.getBackground();
            drawable.setColor(messaggioView.getMessaggio().getColore());
            layoutColore.post(() -> layoutColore.setBackground(drawable));
        }
        else if(messaggioView.getTipo() == MessaggioView.MessaggioUscita) {
            autoreTextView.post(() -> autoreTextView.setTextColor(chatFragment.getContext().getResources().getColor(R.color.verde_scuro)));

            GradientDrawable drawable = (GradientDrawable) layoutColore.getBackground();
            drawable.setColor(chatFragment.getContext().getResources().getColor(R.color.verde_scuro));
            layoutColore.post(() -> layoutColore.setBackground(drawable));
        }

    }

    @Override
    public void update() {
        setMessaggioView(messaggioView);
    }
}
