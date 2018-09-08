package com.example.hv12.ejercicio_2_audio;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

/**
 * Created by admin on 7/9/18.
 */

public class AudioAsincrono extends AsyncTask<Void,String,String> {

    Context context;
    TextView lblActual,lblFin;

    MediaPlayer reproductorMusica;


    boolean pausa=false;

    private String VIGILANTE = "vigilante";

    public AudioAsincrono(Context context, TextView lblActual, TextView lblFin) {
        this.context = context;
        this.lblActual = lblActual;
        this.lblFin = lblFin;


    }

    @Override
    protected String doInBackground(Void... voids) {
        reproductorMusica.start();

        while (reproductorMusica.isPlaying()){
            esperarUnSegundo();
            publishProgress(tiempo(reproductorMusica.getCurrentPosition()));
            if(pausa==true){
                synchronized (VIGILANTE){
                    try {
                        /**realiza pausa  en el hilo**/
                        reproductorMusica.pause();
                        VIGILANTE.wait();
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }/**sale del sincronized por lo que ya no hay pausa*/
                    pausa = false;
                    reproductorMusica.start();
                }
            }
        }
        return null;
    }


    public boolean esPausa(){
        return pausa;
    }

    public void pausarAudio(){
        pausa = true;
    }

    /** notifica a VIGILANTE en todas sus llamadas con syncronized**/
    public void reanudarAudio(){
        synchronized (VIGILANTE){
            VIGILANTE.notify();
        }
    }


    private void esperarUnSegundo() {
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException ignore) {}
    }

    @Override
    protected void onProgressUpdate(String... values) {
        lblActual.setText(values[0]);
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPreExecute() {
        reproductorMusica = MediaPlayer.create(context,R.raw.nokia_tune);
        long fin = reproductorMusica.getDuration();
        lblFin.setText(tiempo(fin));
        super.onPreExecute();
    }

    private String tiempo(long t){
        long fin_min = TimeUnit.MILLISECONDS.toMinutes(t);
        long fin_sec = TimeUnit.MILLISECONDS.toSeconds(t) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(t));
        return fin_min+":"+fin_sec;
    }


}
