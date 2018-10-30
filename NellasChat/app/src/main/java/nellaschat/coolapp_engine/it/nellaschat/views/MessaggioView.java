package nellaschat.coolapp_engine.it.nellaschat.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

import nellaschat.coolapp_engine.it.nellaschat.R;
import nellaschat.coolapp_engine.it.nellaschat.chat.Messaggio;
import nellaschat.coolapp_engine.it.nellaschat.fragments.ChatFragment;
import nellaschat.coolapp_engine.it.nellaschat.pattern.Observable;
import nellaschat.coolapp_engine.it.nellaschat.pattern.Observer;

public class MessaggioView extends FrameLayout implements Observable {

    private View view;
    private LayoutInflater layoutInflater;

    private int tipo;
    public static final int MessaggioEntrata = 0, MessaggioUscita = 1;

    private FrameLayout frameLayout;
    private LinearLayout linearLayout, linearRisposte;
    private RelativeLayout relativeLayout;
    private TextView autoreTextView, messaggioTextView, dataTextView;

    private Messaggio messaggio;
    private ArrayList<Observer> messaggiRispostaObservers = new ArrayList<>();
    private ArrayList<RispostaMessaggioView> messaggiRispostaViews = new ArrayList<>();

    private Runnable onClickRunnable = null, onLongClickRunnable = null;

    private boolean selezionato;

    private ChatFragment chatFragment;

    @SuppressLint("ClickableViewAccessibility")
    public MessaggioView(ChatFragment chatFragment, int tipo) {
        super(chatFragment.getContext());
        this.chatFragment = chatFragment;
        this.tipo = tipo;

        layoutInflater = (LayoutInflater) chatFragment.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(tipo == MessaggioView.MessaggioEntrata) {
            view = layoutInflater.inflate(R.layout.messaggio_entrata, this);
            autoreTextView = view.findViewById(R.id.testo_autore_messaggio);
        }
        else if(tipo == MessaggioView.MessaggioUscita){
            view = layoutInflater.inflate(R.layout.messaggio_uscita, this);
        }

        messaggioTextView = view.findViewById(R.id.testo_messaggio);
        dataTextView = view.findViewById(R.id.testo_data);

        messaggio = new Messaggio();

        frameLayout = view.findViewById(R.id.chat_framelayout);
        linearLayout = view.findViewById(R.id.chat_linearlayout);
        linearRisposte = view.findViewById(R.id.chat_linearlayout_risposte);
        relativeLayout = view.findViewById(R.id.chat_relativelayout);

        this.setClickable(true);
        this.setFocusable(true);
        this.setFocusableInTouchMode(true);
        selezionato = false;

        relativeLayout.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                if (chatFragment.isSelecting()) {
                    if (!this.selezionato) {
                        relativeLayout.performLongClick();
                        return true;
                    } else {
                        this.setForeground(null);
                        chatFragment.removeMessaggioSelezionato(this);
                        selezionato = false;
                        return true;
                    }
                } else {
                    if(tipo == MessaggioView.MessaggioEntrata)
                        frameLayout.setForeground(chatFragment.getContext().getResources().getDrawable(R.drawable.messaggio_entrata_rounded_selezionato));
                    else
                        frameLayout.setForeground(chatFragment.getContext().getResources().getDrawable(R.drawable.messaggio_uscita_rounded_selezionato));
                    if (onClickRunnable != null)
                        onClickRunnable.run();
                    return false;
                }

            }
            if (motionEvent.getAction() == MotionEvent.ACTION_UP || motionEvent.getAction() == MotionEvent.ACTION_CANCEL) {
                frameLayout.setForeground(null);
                return false;
            }
            return false;
        });

        relativeLayout.setOnLongClickListener(view -> {
            selezionato = true;
            chatFragment.addMessaggioSelezionato(this);
            frameLayout.setForeground(null);
            this.setForeground(new ColorDrawable(chatFragment.getContext().getResources().getColor(R.color.coloreMessaggioSelezionatoCompleto)));
            if(onLongClickRunnable != null)
                onLongClickRunnable.run();
            return true;
        });
    }

    public View getView() {
        return view;
    }

    public int getTipo(){
        return tipo;
    }

    public void setTipo(int tipo){
        this.tipo = tipo;
    }

    public Messaggio getMessaggio() {
        return messaggio;
    }

    public void setMessaggio(Messaggio messaggio) {
        this.messaggio = messaggio;
        setAutore(messaggio.getAutore());
        setTesto(messaggio.getTesto());
        setData(messaggio.getData());
        setColore(messaggio.getColore());
    }

    public int getIdMessaggio() {
        return messaggio.getId();
    }

    public void setIdMessaggio(int id) {
        messaggio.setId(id);
    }

    public String getAutore() {
        return messaggio.getAutore();
    }

    public void setAutore(String autore) {
        messaggio.setAutore(autore);
        if(tipo == MessaggioEntrata)
            autoreTextView.post(() -> autoreTextView.setText(messaggio.getAutore()));
    }

    public void mostraAutore(){
        if(tipo == MessaggioEntrata)
            autoreTextView.post(() -> autoreTextView.setVisibility(VISIBLE));
    }

    public void nascondiAutore(){
        if(tipo == MessaggioEntrata)
            autoreTextView.post(() -> autoreTextView.setVisibility(GONE));
    }

    public String getTesto() {
        return messaggio.getTesto();
    }

    public void setTesto(String testo) {
        messaggio.setTesto(testo);
        messaggioTextView.post(() -> {
            messaggioTextView.setText(messaggio.getTesto());
        });
    }

    public String getData() {
        return messaggio.getData();
    }

    public void setData(Date data) {
        messaggio.setData(data);
        dataTextView.post(() -> {
            dataTextView.setText(messaggio.getData());
        });
    }

    public void setData(String data) {
        messaggio.setData(data);
        dataTextView.post(() -> {
            dataTextView.setText(messaggio.getData());
        });
    }

    public int getColore(){
        return messaggio.getColore();
    }

    public void setColore(int colore){
        messaggio.setColore(colore);
        if(this.tipo == MessaggioEntrata)
            autoreTextView.post(() -> autoreTextView.setTextColor(messaggio.getColore()));
    }

    public void rimuoviMessaggio() {
        messaggio.remove();
        messaggioTextView.post(() -> {
            messaggioTextView.setTypeface(messaggioTextView.getTypeface(), Typeface.ITALIC);
            messaggioTextView.setTextColor(chatFragment.getContext().getResources().getColor(R.color.coloreMessaggioCancellatoTesto));
            messaggioTextView.setText("Il messaggio è stato cancellato.");
        });
    }

    public void setOnClickRunnable(Runnable runnable) {
        this.onClickRunnable = runnable;
    }

    public void setOnLongClickRunnable(Runnable runnable) {
        this.onLongClickRunnable = runnable;
    }

    public boolean isSelezionato() {
        return selezionato;
    }

    public void deseleziona() {
        selezionato = false;
        frameLayout.setForeground(null);
        this.setForeground(null);
    }

    public void addMessaggiRispostaView(RispostaMessaggioView rispostaMessaggioView) {
        if(messaggiRispostaViews.size() == 0)
            linearRisposte.post(() -> linearRisposte.setVisibility(VISIBLE));

        messaggiRispostaViews.add(rispostaMessaggioView);
        linearRisposte.post(() -> linearRisposte.addView(rispostaMessaggioView));
    }

    public void removeMessaggiRispostaView(RispostaMessaggioView rispostaMessaggioView) {
        messaggiRispostaViews.remove(rispostaMessaggioView);
        linearRisposte.post(() -> linearRisposte.removeView(rispostaMessaggioView));

        if(messaggiRispostaViews.size() == 0)
            linearRisposte.post(() -> linearRisposte.setVisibility(GONE));
    }

    public void clearMessaggiRispostaViews() {
        messaggiRispostaViews.clear();
        linearRisposte.post(() -> linearRisposte.removeAllViews());
        linearRisposte.post(() -> linearRisposte.setVisibility(GONE));
    }

    @Override
    public void addObservable(Observer observer) {
        messaggiRispostaObservers.add(observer);
    }

    @Override
    public void removeObservable(Observer observer) {
        messaggiRispostaObservers.remove(observer);
    }

    @Override
    public void clearObservables() {
        messaggiRispostaObservers.clear();
    }

    @Override
    public void notifyUpdateObservables() {
        for (int i=0; i<messaggiRispostaObservers.size(); i++)
            messaggiRispostaObservers.get(i).update();
    }
}
