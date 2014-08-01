package com.nuance.nmdp.sample;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.blake.StartListenerService;
import com.nuance.nmdp.speechkit.Prompt;
import com.nuance.nmdp.speechkit.SpeechKit;

public class MainView2 extends Activity {
    
    private static SpeechKit _speechKit;
    private static final String TAG = "MainView2";
    public static final String PREFS_NAME = "MyPrefsFile";
    public static String username;


    // Allow other activities to access the SpeechKit instance.
    public static SpeechKit getSpeechKit()
    {
        return _speechKit;
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main2);


        try {
            Thread.sleep(3000);
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        // If this Activity is being recreated due to a config change (e.g. 
        // screen rotation), check for the saved SpeechKit instance.
        _speechKit = (SpeechKit)getLastNonConfigurationInstance();
        boolean isNetWorkAvail = false;
        if(isNetworkAvailable(getApplicationContext())){
            isNetWorkAvail=true;
        //    Toast.makeText(this, "AAA InternetAvailable", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this, "XXX Internet Not Available", Toast.LENGTH_LONG).show();
        }
        boolean online = false;
        if(isOnline()){
            online=true;
          //  Toast.makeText(this, "AAA online", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this, "XXX Not online", Toast.LENGTH_LONG).show();
        }
        Log.d(TAG, "online=" + online);
        Log.d(TAG, "networked=" + isNetWorkAvail);
        //only initialize if speechkit is null....
        if (_speechKit == null && (online | isNetWorkAvail))
        {
        //    Toast.makeText(this, "Nuance Speech kit initializing...", Toast.LENGTH_LONG).show();
            //todo shared by all activities in app, static speechkit is initialized by singleton holder of static speechkit
            _speechKit = SpeechKitHolder.getSpeechKit(this);
            _speechKit.connect();
            // TODO: Keep an eye out for audio prompts not working on the Droid 2 or other 2.2 devices.
            Prompt beep = _speechKit.defineAudioPrompt(R.raw.beep);
            _speechKit.setDefaultRecognizerPrompts(beep, Prompt.vibration(100), null, null);
        }
        
        final Button dictationButton = (Button)findViewById(R.id.btn_dictation);
        final Button ttsButton = (Button)findViewById(R.id.btn_tts);

        Button.OnClickListener l = new Button.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                if (v == dictationButton)
                {
                    // TODO: here is the entry point for calling the dictationView. The voice listener will replace the button.onclicklistener, and create intent instead
                    Intent intent = null;
                    //    intent = new Intent(v.getContext(), DictationServiceView.class)
                       intent = new Intent(v.getContext(), CommandView.class);

                    MainView2.this.startActivity(intent);
                } else if (v == ttsButton)
                {
                    Intent intent = new Intent(v.getContext(), TtsNameView.class);
                    startActivity(intent);
                }
            }
        };
        
        dictationButton.setOnClickListener(l);
        ttsButton.setOnClickListener(l);


        Button buttonQ = (Button)findViewById(R.id.btn_quit);
        buttonQ.setOnClickListener(new Button.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                _speechKit=null;
                finish();

                //start listener again, if we are quitting.
                Intent service = new Intent(MainView2.this, StartListenerService.class);
                startService(service);

                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        Button buttonChg = (Button)findViewById(R.id.btn_change);
        buttonChg.setOnClickListener(new Button.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
               Intent intent = new Intent(v.getContext(), DictationNameView.class);
                startActivity(intent);
            }
        });



        Button buttonWhat = (Button)findViewById(R.id.btn_what);
        buttonWhat.setOnClickListener(new Button.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(v.getContext(), TtsCommandsHistoryView.class);
                startActivity(intent);
            }
        });




        // Restore preferences
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        username = settings.getString("username", "bob");
        //if first time through, we will name ourselves
        if(username.equals("bob")){
          //  Toast.makeText(this, "We have no user yet", Toast.LENGTH_LONG).show();
            //todo: we launch the dictationnameview here

           Intent intent = new Intent(this, DictationNameView.class);
            startActivity(intent);
           // username = "saraswati";

        }



    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (_speechKit != null)
        {
            _speechKit.release();
            _speechKit = null;
        }
    }


    @Override
    protected void onStop(){
        super.onStop();
        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("username", username);
        // Commit the edits!
        editor.commit();

    }
    
    @Override
    public Object onRetainNonConfigurationInstance()
    {
        // Save the SpeechKit instance, because we know the Activity will be
        // immediately recreated.
        SpeechKit sk = _speechKit;
        _speechKit = null; // Prevent onDestroy() from releasing SpeechKit
        return sk;
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