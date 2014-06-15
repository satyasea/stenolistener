package com.blake.voice.tasks;

import android.app.Activity;
import android.content.Intent;
import com.nuance.nmdp.sample.MainView2;

/**
 * Created by horse on 5/3/2014.
 */
public class ActionTask {

    private Activity source;

    public ActionTask(Activity src){
        this.source = src;
    }

    public final static String EXTRA_MESSAGE = "com.blake.voice.tasks.actiontask.MESSAGE";

    public void processVoiceInput(String cmd){
        String[] inputArray = cmd.toLowerCase().split(" ");
        StringBuilder sb = new StringBuilder();
        for(int i = 1; i < inputArray.length; i++){
            sb.append(inputArray[i]);
            sb.append(" ");
        }

        String rec = sb.toString().trim();
        if(inputArray[0].equals("email"))
            openEmail(rec);

        if(inputArray[0].equals("phone"))
            openPhone(rec);


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
        }

    }

    private void openPhone(String recip){
        throw new UnsupportedOperationException("phone contacts not available yet.");
    }





}
