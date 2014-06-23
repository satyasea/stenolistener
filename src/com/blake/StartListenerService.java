package com.blake;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import com.blake.voice.tasks.ActionTask;
import com.blake.voice.tasks.DisplayMessageActivity;
import com.nuance.nmdp.sample.AppInfo;
import com.nuance.nmdp.sample.ListeningDialog;
import com.nuance.nmdp.sample.*;

import com.nuance.nmdp.sample.MainView2;
import com.nuance.nmdp.speechkit.*;


/**
 * Created by horse on 5/31/2014.
 */
public class StartListenerService extends Service {

    private static final String TAG = "StartListenerService";


    // private static final int LISTENING_DIALOG = 0;
    private Handler _handler = null;
    private final Recognizer.Listener _listener;
    private Recognizer _currentRecognizer;
    // private ListeningDialog _listeningDialog;
    private ArrayAdapter<String> _arrayAdapter;
    private boolean _destroyed;

    private static int listenTryCount;


    public StartListenerService() {
        super();
        _handler = new Handler();
        _listener = createListener();
        Log.d(TAG, "Constructor");

    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        _destroyed = true;
        if (_currentRecognizer != null) {
            _currentRecognizer.cancel();
            _currentRecognizer = null;
        }
        Toast.makeText(this, "Q Listener Service Stopped", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onDestroy");
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(TAG, "onStartCommand");
        System.out.println(TAG + " onstartcommand");

        //_currentRecognizer = null;

       // _currentRecognizer = MainView2.getSpeechKit().createRecognizer(Recognizer.RecognizerType.Dictation, Recognizer.EndOfSpeechDetection.Long, "en_US", _listener, _handler);
        initSpeechKit();
        _currentRecognizer = _speechKit.createRecognizer(Recognizer.RecognizerType.Dictation, Recognizer.EndOfSpeechDetection.Long, "en_US", _listener, _handler);


        //    _listeningDialog = null;
        _listener.onRecordingBegin(_currentRecognizer);
        _currentRecognizer.setListener(_listener);


        Toast.makeText(this, "Q Service Listener onStartCommand", Toast.LENGTH_LONG).show();
        _destroyed = false;
        //TODO: START THE LISTENER, we don't need to press no stinkin button
        if (_currentRecognizer != null) {
            _currentRecognizer.start();
        }else{
           // _currentRecognizer = MainView2.getSpeechKit().createRecognizer(Recognizer.RecognizerType.Dictation, Recognizer.EndOfSpeechDetection.Long, "en_US", _listener, _handler);
            _speechKit.createRecognizer(Recognizer.RecognizerType.Dictation, Recognizer.EndOfSpeechDetection.Long, "en_US", _listener, _handler);
            _listener.onRecordingBegin(_currentRecognizer);
            _currentRecognizer.setListener(_listener);
            _currentRecognizer.start();
        }

/*
        Toast.makeText(this, "Q Service Listener onStartCommand", Toast.LENGTH_LONG).show();
           Intent intents = new Intent(getBaseContext(), HelloActivity.class);
           intents.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
           startActivity(intents);
*/
      //  Log.d(TAG, "onStart");
        return START_STICKY;
    }



    private static SpeechKit _speechKit;
    private void initSpeechKit(){
        try {
            Thread.sleep(10000);
            boolean isNetWorkAvail = false;
            if(isNetworkAvailable(getApplicationContext())){
                isNetWorkAvail=true;
              //  Toast.makeText(this, "AAA InternetAvailable", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(this, "Q Voice app Msg: Internet Not Available", Toast.LENGTH_LONG).show();
            }
            boolean online = false;
            if(isOnline()){
                online=true;
             //   Toast.makeText(this, "AAA online", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(this, "Q Voice app Msg: Not online", Toast.LENGTH_LONG).show();
            }
            Log.d(TAG, "online=" + online);
            Log.d(TAG, "networked=" + isNetWorkAvail);
            Toast.makeText(this, "Q Voice App Service Initializing", Toast.LENGTH_LONG).show();
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        _speechKit = SpeechKit.initialize(getApplicationContext(), AppInfo.SpeechKitAppId, AppInfo.SpeechKitServer, AppInfo.SpeechKitPort, AppInfo.SpeechKitSsl, AppInfo.SpeechKitApplicationKey);
        _speechKit.connect();
        // TODO: Keep an eye out for audio prompts not working on the Droid 2 or other 2.2 devices.
        Prompt beep = _speechKit.defineAudioPrompt(R.raw.beep);
        _speechKit.setDefaultRecognizerPrompts(beep, Prompt.vibration(100), null, null);
    }




    private Recognizer.Listener createListener() {
        return new Recognizer.Listener() {
            @Override
            public void onRecordingBegin(Recognizer recognizer) {
                /*
                _listeningDialog.setText("Q Recording...");
                _listeningDialog.setStoppable(true);
                _listeningDialog.setRecording(true);
                */

                // Create a repeating task to update the audio level

                Runnable r = new Runnable() {
                    public void run() {
                        /*
                        if (_listeningDialog != null && _listeningDialog.isRecording() && _currentRecognizer != null)
                        {
                            _listeningDialog.setLevel(Float.toString(_currentRecognizer.getAudioLevel()));
                            _handler.postDelayed(this, 500);
                        }
*/

                        if (_currentRecognizer != null) {
                            _handler.postDelayed(this, 500);
                        }

                    }
                };
                r.run();
            }

            @Override
            public void onRecordingDone(Recognizer recognizer) {
                /*
                _listeningDialog.setText("Q Processing...");
                _listeningDialog.setLevel("");
                _listeningDialog.setRecording(false);
                _listeningDialog.setStoppable(false);
                */
            }






            @Override
            public void onError(Recognizer recognizer, SpeechError error) {
                if (recognizer != _currentRecognizer) return;
                // if (_listeningDialog.isShowing()) dismissDialog(LISTENING_DIALOG);
                _currentRecognizer = null;
                //  _listeningDialog.setRecording(false);

                // Display the error + suggestion in the edit box
                String detail = error.getErrorDetail();
                String suggestion = error.getSuggestion();

                if (suggestion == null) suggestion = "";
                // for debugging purpose: printing out the speechkit session id
                android.util.Log.d("Nuance SampleVoiceApp", "Recognizer.Listener.onError: session id ["
                //        + MainView2.getSpeechKit().getSessionId() + "]");
                        + _speechKit.getSessionId() + "]");
            }

            @Override
            public void onResults(Recognizer recognizer, Recognition results) {
                int count = results.getResultCount();
                Recognition.Result[] rs = new Recognition.Result[count];
                for (int i = 0; i < count; i++) {
                    rs[i] = results.getResult(i);
                }
                // for debugging purpose: printing out the speechkit session id
                android.util.Log.d("Nuance SampleVoiceApp", "Recognizer.Listener.onResults: session id ["
                 //       + MainView2.getSpeechKit().getSessionId() + "]");
                + _speechKit.getSessionId() + "]");
                //here we can capture the text...
                //TODO look through phrases from results
                System.out.println("bedbug********************************results 0-" + rs[0].getText());
                // openEmail(rs[0].getText());
                // ActionTask task = new ActionTask(DictationView.this);
                //  task.processVoiceInput(rs[0].getText());
                String START_WORD = "james bond";
                boolean found = false;
                for (int i = 0; i < rs.length; i++) {
                    String line = rs[i].getText().toLowerCase();
                    if (line.equals(START_WORD)) {
                        found = true;
                        listenTryCount = 0;
                        // if (_listeningDialog.isShowing()) dismissDialog(LISTENING_DIALOG);
                        _currentRecognizer = null;
                        // _listeningDialog.setRecording(false);
                        runSteno();
                        break;
                    } else if (-1 != line.indexOf(START_WORD)) {
                        found = true;
                        listenTryCount = 0;
                        // if (_listeningDialog.isShowing()) dismissDialog(LISTENING_DIALOG);
                        _currentRecognizer.stopRecording();
                        _currentRecognizer = null;
                        // _listeningDialog.setRecording(false);
                        runSteno();
                        break;
                    }
                }
                //TODO: if failed to find phrase yet, keep listening.
                if (!found) {
                    //TODO: if failed to find phrase three times, go back to main activity
                    if (listenTryCount > 3) {
                        listenTryCount = 0;
                        //  if (_listeningDialog.isShowing()) dismissDialog(LISTENING_DIALOG);
                        _currentRecognizer.stopRecording();
                        _currentRecognizer.cancel();
                        _currentRecognizer = null;
                        //  _listeningDialog.setRecording(false);
                        Intent intent = new Intent(StartListenerService.this, MainView2.class);
                        String message = "Q Listened three times!";
                        Toast.makeText(getApplicationContext(), "Q Listened three times!", Toast.LENGTH_LONG).show();
                        intent.putExtra(ActionTask.EXTRA_MESSAGE, message);
                        StartListenerService.this.startActivity(intent);
                    } else {
                        _currentRecognizer.cancel();
                        _currentRecognizer = null;
                        if (_currentRecognizer == null)
                       //     _currentRecognizer = MainView2.getSpeechKit().createRecognizer(Recognizer.RecognizerType.Dictation, Recognizer.EndOfSpeechDetection.Long, "en_US", _listener, _handler);
                            _currentRecognizer = _speechKit.createRecognizer(Recognizer.RecognizerType.Dictation, Recognizer.EndOfSpeechDetection.Long, "en_US", _listener, _handler);
                        listenTryCount++;
                        _currentRecognizer.start();
                    }
                }
            }
        };
    }


    private void runSteno() {
       // System.out.println("Blake Debug Logging runSteno from Activity Source: " + this.getClass() + " and starting " + DisplayMessageActivity.class);
        System.out.println("Blake Debug Logging runSteno from Activity Source: " + this.getClass() + " and starting " + MainView2.class);
        //Intent intent = new Intent(this, DisplayMessageActivity.class);
        Intent intent = new Intent(this, MainView2.class);
        String message = "Q says, Did somebody just say James Bond?";
        intent.putExtra(ActionTask.EXTRA_MESSAGE, message);
      //  intent.putExtra("caller", "StartListenerService");

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }




    public static boolean isNetworkAvailable(Context context)
    {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();

            if (info != null)
            {
                for (int i = 0; i < info.length; i++)
                {
                    Log.i("Class", info[i].getState().toString());
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }



    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }


}
