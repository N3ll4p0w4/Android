package nellaschat.coolapp_engine.it.nellaschat.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import nellaschat.coolapp_engine.it.nellaschat.R;

public class ChatAvviso extends RelativeLayout {

    private View view;
    private LayoutInflater layoutInflater;
    private TextView testoView;

    private String testo;

    public ChatAvviso(Context context) {
        super(context);

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = layoutInflater.inflate(R.layout.chat_avviso, this);

        testoView = view.findViewById(R.id.testo);
    }

    public View getView() {
        return view;
    }
    public String getTesto() {
        return testo;
    }

    public void setTesto(String testo) {
        this.testo = testo;
        testoView.post(() -> {
            testoView.setText(testo);
        });
    }

}
