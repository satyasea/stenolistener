package com.blake;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by horse on 5/31/2014.
 */
public class AutostartReceiver extends BroadcastReceiver
{
    public void onReceive(Context arg0, Intent arg1)
    {
    //   Intent intent = new Intent(arg0,StartService.class);
        Intent intent = new Intent(arg0, StartListenerService.class);
        Toast.makeText(arg0, "Q Listener Service Started", Toast.LENGTH_LONG).show();
        arg0.startService(intent);
        Log.i("Autostart", "started");
    }
}
