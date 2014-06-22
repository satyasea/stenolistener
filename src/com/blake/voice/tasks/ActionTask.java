package com.blake.voice.tasks;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.nuance.nmdp.sample.MainView2;

/**
 * Created by horse on 5/3/2014.
 */
public class ActionTask {

    private Activity source;
    public final static String EXTRA_MESSAGE = "com.blake.voice.tasks.actiontask.MESSAGE";
    private static String LOG_TAG = "Logging ActionTask";


    public ActionTask(Activity src){
        this.source = src;
        //Supports phone call
        EndCallListener callListener = new EndCallListener();
        TelephonyManager mTM = (TelephonyManager) source.getSystemService(Context.TELEPHONY_SERVICE);
        mTM.listen(callListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    public void processVoiceInput(String cmd){
        String[] inputArray = cmd.toLowerCase().split(" ");
        String action = inputArray[0];
        // build recipient from first and last names
        StringBuilder sb = new StringBuilder();
        for(int i = 1; i < inputArray.length; i++){
            sb.append(inputArray[i]);
            sb.append(" ");
        }
        String rec = sb.toString().trim();
        //call action
        if(action.equals("email"))
            openEmail(rec);
        if(action.equals("telephone") ||
                action.equals("call") ||
                action.equals("cull") ||
                action.equals("phone") ||
                action.equals("called") )
            dialContactPhone(rec);
    }

    private void openEmail(String recip){
        if(recip.equals("james bond")){
            final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
            emailIntent.setType("plain/text");
            emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"jamesbond@gmail.com"});
            emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "confidential from james bond");
            emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "");
            source.startActivity(Intent.createChooser(emailIntent, "Mailing James Bond..."));
        }else if(recip.equals("batman")){
            final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
            emailIntent.setType("plain/text");
            emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"batman@gmail.com"});
            emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "confidential from the batcave");
            emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "");
            source.startActivity(Intent.createChooser(emailIntent, "Mailing Batman..."));
        }else{
            //Todo: integrate email with contacts here...
        }

    }


    //uses actual contacts info on phone
    private void dialContactPhone(String contactName){
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] projection    = new String[] {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER};
        Cursor people = source.getContentResolver().query(uri, projection, null, null, null);
        int indexName = people.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
        int indexNumber = people.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
        people.moveToFirst();
        do {
            String name   = people.getString(indexName);
            String number = people.getString(indexNumber);
            // Do work...
            if(name.equalsIgnoreCase(contactName)){

                Intent intent = new Intent(Intent.ACTION_CALL);
                Uri data = Uri.parse("tel:"+ number );
                intent.setData(data);
                Log.i(LOG_TAG, "Intent Data: " + intent.getDataString());
                source.startActivity(intent);
            }
        } while (people.moveToNext());
    }

    private class EndCallListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            if(TelephonyManager.CALL_STATE_RINGING == state) {
                Log.i(LOG_TAG, "RINGING, number: " + incomingNumber);
            }
            if(TelephonyManager.CALL_STATE_OFFHOOK == state) {
                //wait for phone to go offhook (probably set a boolean flag) so you know your app initiated the call.
                Log.i(LOG_TAG, "OFFHOOK");
            }
            if(TelephonyManager.CALL_STATE_IDLE == state) {
                //when this state occurs, and your flag is set, restart your app
                Log.i(LOG_TAG, "IDLE");
            }
        }
    }

}
