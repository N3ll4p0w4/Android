package it.test.coolapp_engine.scuoti;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.app.Activity;
import android.graphics.Color;
import android.hardware.*;
import android.os.CountDownTimer;
import android.view.*;
import android.widget.*;

public class Main extends Activity implements SensorEventListener {
    private SensorManager sensorManager;
    private View view;
    private SeekBar barra;
    private TextView barraValore;
    private MediaPlayer mPlayer;
    private ImageView immagine;
    private long lastUpdate;
    private int i;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        view = findViewById(R.id.barra);

        barra = (SeekBar) findViewById(R.id.barra);
        barraValore = (TextView) findViewById(R.id.barraValore);
        mPlayer = MediaPlayer.create(Main.this, R.raw.stay);
        immagine = (ImageView) findViewById(R.id.immagine);
        immagine.setVisibility(View.INVISIBLE);

        i=0;

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        lastUpdate = System.currentTimeMillis();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            getAccelerometer(event);
        }

    }

    private void getAccelerometer(SensorEvent event) {
        float[] values = event.values;
        // Movement
        float x = values[0];
        float y = values[1];
        float z = values[2];

        float accelationSquareRoot = (x * x + y * y + z * z)
                / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
        long actualTime = System.currentTimeMillis();
        System.out.println(accelationSquareRoot);

        barraValore.setText(""+barra.getProgress());

        long tempo = 200;

        if (accelationSquareRoot >= 1+barra.getProgress()/3) {
            if (actualTime - lastUpdate < tempo) {
                return;
            }
            lastUpdate = actualTime;
            mPlayer.start();
            immagine.setVisibility(View.VISIBLE);
        } else {
            if (actualTime - lastUpdate < tempo) {
                return;
            }
            if (mPlayer.isPlaying())
                mPlayer.pause();
            immagine.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        // register this class as a listener for the orientation and
        // accelerometer sensors
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        // unregister listener
        if (mPlayer.isPlaying())
            mPlayer.pause();
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    public void onDestroy() {
        mPlayer.stop();
        super.onDestroy();
    }
}