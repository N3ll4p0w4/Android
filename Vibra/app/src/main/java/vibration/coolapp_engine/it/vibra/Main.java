package vibration.coolapp_engine.it.vibra;

import android.content.*;
import android.app.Activity;
import android.hardware.*;
import android.os.*;
import android.view.*;
import android.widget.*;

public class Main extends Activity {
    private SensorManager sensorManager;
    private View view;
    private Button[] bottoniTempo = new Button[6];
    private Button start, infinito, stop;
    private EditText insNumeri;
    private TextView tempoRimanente;
    private CountDownTimer timer;
    private long tempo = 0;
    private boolean attivo = false;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);
        view = findViewById(R.id.testo1);

        bottoniTempo[0] = (Button)findViewById(R.id.button1);
        bottoniTempo[1] = (Button)findViewById(R.id.button2);
        bottoniTempo[2] = (Button)findViewById(R.id.button3);
        bottoniTempo[3] = (Button)findViewById(R.id.button4);
        bottoniTempo[4] = (Button)findViewById(R.id.button5);
        bottoniTempo[5] = (Button)findViewById(R.id.button6);

        insNumeri = (EditText)findViewById(R.id.insNumeri);
        start = (Button)findViewById(R.id.start);
        infinito = (Button)findViewById(R.id.infinito);
        tempoRimanente = (TextView) findViewById(R.id.tempoRimanente);
        stop = (Button)findViewById(R.id.stop);

        final Vibrator vb = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        schermoSpento(vb);

        for(int i=0; i<bottoniTempo.length; i++) {
            final Button bottone = bottoniTempo[i];
            bottone.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    System.out.println(bottone.getText().toString());
                    String testo = bottone.getText().toString();
                    int tempo = Integer.parseInt(testo);
                    vb.vibrate(tempo*1000);
                    disattivaBottoni();
                    impostaTimer(tempo*1000);
                }
            });
        }

        //Start
        start.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String testo = insNumeri.getText().toString();
                if(!testo.equals("")) {
                    int tempo = Integer.parseInt(testo);
                    vb.vibrate(tempo*1000);
                    disattivaBottoni();
                    impostaTimer(tempo*1000);
                }
            }
        });

        //Infinito
        infinito.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                vb.vibrate(999999999);
                disattivaBottoni();
                tempo = -1;
                tempoRimanente.setText("Tempo rimanente: INFINITO!");
            }
        });

        //Stop
        stop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                vb.cancel();
                if(timer != null){
                    timer.cancel();
                }
                attivaBottoni();
            }
        });
    }

    private void impostaTimer(long time){
        tempo = time;
        timer = new CountDownTimer(time, 1000) {
            public void onTick(long millisUntilFinished) {
                tempoRimanente.setText("Tempo rimanente: " + (millisUntilFinished / 1000) + "s");
                tempo -= 1000;
            }

            public void onFinish() {
                tempoRimanente.setText("Tempo rimanente: 0s");
                attivaBottoni();
            }
        }.start();
    }

    private void disattivaBottoni(){
        for(int i=0; i<bottoniTempo.length; i++)
            bottoniTempo[i].setEnabled(false);
        start.setEnabled(false);
        infinito.setEnabled(false);
        stop.setEnabled(true);
    }

    private void attivaBottoni(){
        for(int i=0; i<bottoniTempo.length; i++)
            bottoniTempo[i].setEnabled(true);
        start.setEnabled(true);
        infinito.setEnabled(true);
        stop.setEnabled(false);
    }

    private void schermoSpento(final Vibrator vb){
        BroadcastReceiver vibrateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                    if(stop.isEnabled()){
                        if(tempo == -1)
                            vb.vibrate(999999999);
                        else
                            vb.vibrate(tempo);
                    }
                }
            }
        };
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        registerReceiver(vibrateReceiver, filter);
    }


    @Override
    protected void onResume() {
        super.onResume();
        final Vibrator vb = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if(tempo == -1)
            vb.vibrate(999999999);
        else
            vb.vibrate(tempo);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void onDestroy() {
        super.onDestroy();
    }

}