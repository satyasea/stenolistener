package com.blake;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import com.nuance.nmdp.sample.MainView2;
import com.nuance.nmdp.speechkit.Recognizer;

/**
 * Created by horse on 5/31/2014.
 */
public class StartService extends Service
{
    private static final String TAG = "StartService";
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    public void onDestroy() {
        Toast.makeText(this, "Q Service Stopped", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onDestroy");
    }
    private Recognizer _currentRecognizer;
    //@Override
    //public void onStart(Intent intent, int startid)
    private Handler _handler = null;
   // private final Recognizer.Listener _listener;

    public StartService(){
        _handler = new Handler();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        try {
            Toast.makeText(this, "Start Service Sleeping", Toast.LENGTH_LONG).show();
            Thread.sleep(15000);

           // _currentRecognizer = MainView2.getSpeechKit().createRecognizer(Recognizer.RecognizerType.Dictation, Recognizer.EndOfSpeechDetection.Long, "en_US", _listener, _handler);
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        Intent intents = new Intent(getBaseContext(),HelloActivity.class);
       // Intent intents = new Intent(getBaseContext(),MainView2.class);
        intents.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intents);
        Toast.makeText(this, "Q Service Started", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onStart");
        return START_STICKY;
    }
}
