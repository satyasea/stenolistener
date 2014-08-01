package com.nuance.nmdp.sample;

import android.content.SharedPreferences;
import android.drm.DrmStore;
import com.blake.db.MySQLiteHelper;
import com.blake.voice.tasks.*;
import com.nuance.nmdp.speechkit.Recognizer;
import com.nuance.nmdp.speechkit.Recognition;
import com.nuance.nmdp.speechkit.SpeechError;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.io.Serializable;
import java.util.*;


public class CommandView extends Activity
{
    private static final int LISTENING_DIALOG = 0;
    private Handler _handler = null;
    private final Recognizer.Listener _listener;
    private Recognizer _currentRecognizer;
    private ListeningDialog _listeningDialog;
    private ArrayAdapter<String> _arrayAdapter;
    private boolean _destroyed;

    private static int listenTryCount;

  //  private static List<ActionCommand> actionCommandList = new ArrayList<ActionCommand>();
    // Allow other activities to access the SpeechKit instance.
  //  public static List getActionCommandList()
   // {
   //     return actionCommandList;
   // }

    //todo currently Q stores things locally, so for storing commands history
    //todo put into db on server
    MySQLiteHelper db = new MySQLiteHelper(this);

    public void processVoiceInput(String cmd){
        String[] inputArray = cmd.toLowerCase().split(" ");
        String cmdStr = inputArray[0];
        // build recipient from first and last names
        StringBuilder sb = new StringBuilder();
        for(int i = 1; i < inputArray.length; i++){
            sb.append(inputArray[i]);
            sb.append(" ");
        }
        String recip = sb.toString().trim();
        ActionTask task = new ActionTask(this);
        /*
        make system smart part I here, whoo hoo
         */
        ActionCommand actionCmd;
        List<String> emailActions = EmailActionCommandList.getEmailCommandList();
        List<String> phoneActions = PhoneActionCommandList.getPhoneCommandList();
        List<String> clockActions = ClockWorkActionCommandList.getClockCommandList();
        if(emailActions.contains(cmdStr)) {
            actionCmd = new ActionCommand(new Date(), "email", recip);
            db.addCommand(actionCmd);
            task.sendTestEmail(recip);
        }else if(phoneActions.contains(cmdStr) ) {
            actionCmd = new ActionCommand(new Date(), "phone", recip);
            db.addCommand(actionCmd);
            task.dialContactPhone(recip);
        }else if(clockActions.contains(cmdStr) ) {
            //todo: handle clock actions, placeholder for integration
            actionCmd = new ActionCommand(new Date(), cmdStr, cmdStr + "-" + recip);
            db.addCommand(actionCmd);
            Intent intent = new Intent(this, TtsResponseView.class);
            intent.putExtra(ActionTask.EXTRA_MESSAGE, cmdStr + "-" + recip);
            startActivity(intent);
        }else {
            /*
            make system smart part II here, whoo hoo
             */
            Map<String, String> actionCommandMap = QuestionResponseMaps.getCommandMap();
            Map<String, String> actionResponseMap = QuestionResponseMaps.getResponseMap();
            Set<String> keys = actionCommandMap.keySet();
            for(String key: keys){
                if(cmd.equalsIgnoreCase(key)){
                    String value = actionCommandMap.get(key);
                    String response = actionResponseMap.get(value);
                    System.out.print("General Questions: " + key + "=" + response + ", ");
                    Intent intent = new Intent(this, TtsResponseView.class);
                    intent.putExtra(ActionTask.EXTRA_MESSAGE, response);
                    startActivity(intent);
                    break;
                }
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }


    private class SavedState
    {
        String DialogText;
        String DialogLevel;
        boolean DialogRecording;
        Recognizer Recognizer;
        Handler Handler;
    }

    public CommandView()
    {
        super();
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
        setContentView(R.layout.command);

        _destroyed = false;


        Button buttonMain = (Button)findViewById(R.id.btn_main);
        buttonMain.setOnClickListener(new Button.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(CommandView.this, MainView2.class);
                startActivity(i);
            }
        });

        // Use the same handler for both buttons

        final Button dictationButton = (Button)findViewById(R.id.btn_startDictation);
        Button websearchButton = (Button)findViewById(R.id.btn_startWebsearch);

        Button.OnClickListener startListener = new Button.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                _listeningDialog.setText("Initializing...");
                showDialog(LISTENING_DIALOG);
                _listeningDialog.setStoppable(false);
                setResults(new Recognition.Result[0]);

                if (v == dictationButton)
                    _currentRecognizer = MainView2.getSpeechKit().createRecognizer(Recognizer.RecognizerType.Dictation, Recognizer.EndOfSpeechDetection.Long, "en_US", _listener, _handler);
                else
                    _currentRecognizer = MainView2.getSpeechKit().createRecognizer(Recognizer.RecognizerType.Search, Recognizer.EndOfSpeechDetection.Short, "en_US", _listener, _handler);
                _currentRecognizer.start();
            }
        };
        dictationButton.setOnClickListener(startListener);
        websearchButton.setOnClickListener(startListener);

        // TTS button will switch to the TtsView for the displayed text
        Button button = (Button)findViewById(R.id.btn_startTts);
        button.setOnClickListener(new Button.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                EditText t = (EditText)findViewById(R.id.text_DictationResult);
                Intent intent = new Intent(v.getContext(), TtsView.class);
                intent.putExtra(TtsView.TTS_KEY, t.getText().toString());
                android.util.Log.d("Nuance SampleVoiceApp", "********************************Boooom-" + t.getText().toString());
                CommandView.this.startActivity(intent);
            }
        });

        // Set up the list to display multiple results
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
        _currentRecognizer = MainView2.getSpeechKit().createRecognizer(Recognizer.RecognizerType.Dictation, Recognizer.EndOfSpeechDetection.Long, "en_US", _listener, _handler);
        _currentRecognizer.start();

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
                _listeningDialog.setText("Q Processing...");
                _listeningDialog.setLevel("");
                _listeningDialog.setRecording(false);
                _listeningDialog.setStoppable(false);
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
                int count = results.getResultCount();
                Recognition.Result[] rs = new Recognition.Result[count];
                for (int i = 0; i < count; i++) {
                    rs[i] = results.getResult(i);
                }
                setResults(rs);
                // for debugging purpose: printing out the speechkit session id
                android.util.Log.d("Nuance SampleVoiceApp", "Recognizer.Listener.onResults: session id ["
                        + MainView2.getSpeechKit().getSessionId() + "]");
                //here we can capture the text...
                System.out.println("bedbug********************************results 0-" + rs[0].getText());
                // openEmail(rs[0].getText());


                processVoiceInput(rs[0].getText());

            }
        };
    }



    private void setResult(String result)
    {
        EditText t = (EditText)findViewById(R.id.text_DictationResult);
        if (t != null)
            t.setText(result);
    }

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
                    CommandView.this.removeDialog(LISTENING_DIALOG);
                    createListeningDialog();
                }
            }
        });
    }
}