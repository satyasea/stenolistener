package com.nuance.nmdp.sample;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.blake.voice.tasks.ActionTask;
import com.nuance.nmdp.speechkit.Recognition;
import com.nuance.nmdp.speechkit.Recognizer;
import com.nuance.nmdp.speechkit.SpeechError;


public class DictationNameView extends Activity
{
    private static final int LISTENING_DIALOG = 0;
    private Handler _handler = null;
    private final Recognizer.Listener _listener;
    private Recognizer _currentRecognizer;
    private ListeningDialog _listeningDialog;
   // private ArrayAdapter<String> _arrayAdapter;
    private boolean _destroyed;

    private static int listenTryCount;

    public static String username;

    Button dictationButton = null;

    private class SavedState
    {
        String DialogText;
        String DialogLevel;
        boolean DialogRecording;
        Recognizer Recognizer;
        Handler Handler;
    }

    public DictationNameView()
    {
        super();
        //TODO start listener here automatically?
        _listener = createListener();
        _currentRecognizer = null;
        _listeningDialog = null;
        _destroyed = true;
    }

    @Override
    protected void onPrepareDialog(int id, final Dialog dialog) {
        switch(id)
        {
            case LISTENING_DIALOG:
                _listeningDialog.prepare(new Button.OnClickListener()
                {
                    @Override
                    public void onClick(View v) {
                        if (_currentRecognizer != null)
                        {
                            _currentRecognizer.stopRecording();
                        }
                    }
                });
                break;
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch(id)
        {
            case LISTENING_DIALOG:
                return _listeningDialog;
        }
        return null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setVolumeControlStream(AudioManager.STREAM_MUSIC); // So that the 'Media Volume' applies to this activity
        setContentView(R.layout.dictation_name_q);

        _destroyed = false;

        // Use the same handler for both buttons
        dictationButton = (Button)findViewById(R.id.btn_startDictation);
     //   Button websearchButton = (Button)findViewById(R.id.btn_startWebsearch);
        Button.OnClickListener startListener = new Button.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                _listeningDialog.setText("Q is Listening...");
                showDialog(LISTENING_DIALOG);
                _listeningDialog.setStoppable(false);
            //    setResults(new Recognition.Result[0]);
                _currentRecognizer = MainView2.getSpeechKit().createRecognizer(Recognizer.RecognizerType.Dictation, Recognizer.EndOfSpeechDetection.Long, "en_US", _listener, _handler);
                _currentRecognizer.start();
            }
        };
        dictationButton.setOnClickListener(startListener);
      //  websearchButton.setOnClickListener(startListener);

        // TTS button will switch to the TtsView for the displayed text
        /*
        Button button = (Button)findViewById(R.id.btn_startTts);
        button.setOnClickListener(new Button.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                EditText t = (EditText)findViewById(R.id.text_DictationResult);
                Intent intent = new Intent(v.getContext(), TtsView.class);
                intent.putExtra(TtsView.TTS_KEY, t.getText().toString());
                android.util.Log.d("Nuance SampleVoiceApp", "********************************Boooom-" + t.getText().toString());
                DictationNameView.this.startActivity(intent);
            }
        });
        */

        // Set up the list to display multiple results
        /*
        ListView list = (ListView)findViewById(R.id.list_results);
        _arrayAdapter = new ArrayAdapter<String>(list.getContext(), R.layout.listitem)
        {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                Button b = (Button)super.getView(position, convertView, parent);
                b.setBackgroundColor(Color.GREEN);
                b.setOnClickListener(new Button.OnClickListener()
                {
                    @Override
                    public void onClick(View v) {
                        Button b = (Button)v;
                        EditText t = (EditText)findViewById(R.id.text_DictationResult);

                        // Copy the text (without the [score]) into the edit box
                        String text = b.getText().toString();
                        int startIndex = text.indexOf("]: ");
                        t.setText(text.substring(startIndex > 0 ? (startIndex + 3) : 0));
                    }
                });
                return b;
            }
        };
        list.setAdapter(_arrayAdapter);
        */

        // Initialize the listening dialog
        createListeningDialog();

        SavedState savedState = (SavedState)getLastNonConfigurationInstance();
        if (savedState == null)
        {
            // Initialize the handler, for access to this application's message queue
            _handler = new Handler();
        } else
        {
            // There was a recognition in progress when the OS destroyed/
            // recreated this activity, so restore the existing recognition
            _currentRecognizer = savedState.Recognizer;
            _listeningDialog.setText(savedState.DialogText);
            _listeningDialog.setLevel(savedState.DialogLevel);
            _listeningDialog.setRecording(savedState.DialogRecording);
            _handler = savedState.Handler;

            if (savedState.DialogRecording)
            {
                // Simulate onRecordingBegin() to start animation
                _listener.onRecordingBegin(_currentRecognizer);
            }

            _currentRecognizer.setListener(_listener);
        }

        //TODO: START THE LISTENER, we don't need to press no stinkin button
        //todo: To take the name, we let them press a button
     //   _currentRecognizer = MainView2.getSpeechKit().createRecognizer(Recognizer.RecognizerType.Dictation, Recognizer.EndOfSpeechDetection.Long, "en_US", _listener, _handler);
     //   _currentRecognizer.start();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        _destroyed = true;
        if (_currentRecognizer !=  null)
        {
            _currentRecognizer.cancel();
            _currentRecognizer = null;
        }
    }



    @Override
    protected void onStop(){
        super.onStop();
        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
        SharedPreferences settings = getSharedPreferences(MainView2.PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("username", username);
        // Commit the edits!
        editor.commit();

    }

    @Override
    public Object onRetainNonConfigurationInstance()
    {
        if (_listeningDialog.isShowing() && _currentRecognizer != null)
        {
            // If a recognition is in progress, save it, because the activity
            // is about to be destroyed and recreated
            SavedState savedState = new SavedState();
            savedState.Recognizer = _currentRecognizer;
            savedState.DialogText = _listeningDialog.getText();
            savedState.DialogLevel = _listeningDialog.getLevel();
            savedState.DialogRecording = _listeningDialog.isRecording();
            savedState.Handler = _handler;

            _currentRecognizer = null; // Prevent onDestroy() from canceling
            return savedState;
        }
        return null;
    }

    private Recognizer.Listener createListener()
    {
        return new Recognizer.Listener()
        {
            @Override
            public void onRecordingBegin(Recognizer recognizer)
            {
                _listeningDialog.setText("Q Recording...");
                _listeningDialog.setStoppable(true);
                _listeningDialog.setRecording(true);

                // Create a repeating task to update the audio level
                Runnable r = new Runnable()
                {
                    public void run()
                    {
                        if (_listeningDialog != null && _listeningDialog.isRecording() && _currentRecognizer != null)
                        {
                            _listeningDialog.setLevel(Float.toString(_currentRecognizer.getAudioLevel()));
                            _handler.postDelayed(this, 500);
                        }
                    }
                };
                r.run();
            }

            @Override
            public void onRecordingDone(Recognizer recognizer)
            {
                _listeningDialog.setText("Q Done Recording...");
                _listeningDialog.setLevel("");
                _listeningDialog.setRecording(false);
                _listeningDialog.setStoppable(false);
              //  if (_listeningDialog.isShowing()) dismissDialog(LISTENING_DIALOG);

            }

            @Override
            public void onError(Recognizer recognizer, SpeechError error)
            {
                if (recognizer != _currentRecognizer) return;
                if (_listeningDialog.isShowing()) dismissDialog(LISTENING_DIALOG);
                _currentRecognizer = null;
                _listeningDialog.setRecording(false);

                // Display the error + suggestion in the edit box
                String detail = error.getErrorDetail();
                String suggestion = error.getSuggestion();

                if (suggestion == null) suggestion = "";
                setResult(detail + "\n" + suggestion);
                // for debugging purpose: printing out the speechkit session id
                android.util.Log.d("Nuance SampleVoiceApp", "Recognizer.Listener.onError: session id ["
                        + MainView2.getSpeechKit().getSessionId() + "]");
            }

            @Override
            public void onResults(Recognizer recognizer, Recognition results) {
                if (_listeningDialog.isShowing()) dismissDialog(LISTENING_DIALOG);
                int count = results.getResultCount();
                Recognition.Result[] rs = new Recognition.Result[count];
                for (int i = 0; i < count; i++) {
                    rs[i] = results.getResult(i);
                }
               // setResults(rs);
                // for debugging purpose: printing out the speechkit session id
                android.util.Log.d("Nuance SampleVoiceApp", "Recognizer.Listener.onResults: session id ["
                        + MainView2.getSpeechKit().getSessionId() + "]");
                //here we can capture the text...
                System.out.println("bedbug********************************results 0-" + rs[0].getText());
                // openEmail(rs[0].getText());

                // ActionTask task = new ActionTask(DictationNameView.this);
               //  task.processVoiceInput(rs[0].getText());

                // We need an Editor object to make preference changes.
                // All objects are from android.context.Context
                username = rs[0].getText();

                DictationNameView.this.dictationButton.setVisibility(View.INVISIBLE);
                setResult(username);




                SharedPreferences settings = getSharedPreferences(MainView2.PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("username", username);
                // Commit the edits!
                editor.commit();
                Toast.makeText(DictationNameView.this, "AAA User saving [ " + username + " ]", Toast.LENGTH_LONG).show();

            //    Intent i = new Intent(DictationNameView.this, MainView2.class);
             //   startActivity(i);

            }
        };
    }



    private void setResult(String result)
    {
        EditText t = (EditText)findViewById(R.id.text_DictationResult);
        t.setVisibility(View.VISIBLE);
        if (t != null)
            t.setText(result);
    }
/*
    private void setResults(Recognition.Result[] results)
    {
        _arrayAdapter.clear();
        if (results.length > 0)
        {
            setResult(results[0].getText());

            for (int i = 0; i < results.length; i++)
                _arrayAdapter.add("[" + results[i].getScore() + "]: " + results[i].getText());
        }  else
        {
            setResult("");
        }
    }
*/
    private void createListeningDialog()
    {
        _listeningDialog = new ListeningDialog(this);
        _listeningDialog.setOnDismissListener(new OnDismissListener()
        {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (_currentRecognizer != null) // Cancel the current recognizer
                {
                    _currentRecognizer.cancel();
                    _currentRecognizer = null;
                }

                if (!_destroyed)
                {
                    // Remove the dialog so that it will be recreated next time.
                    // This is necessary to avoid a bug in Android >= 1.6 where the 
                    // animation stops working.
                    DictationNameView.this.removeDialog(LISTENING_DIALOG);
                    createListeningDialog();
                }
            }
        });
    }
}
