package com.nuance.nmdp.sample;

import android.app.Activity;
import android.content.Context;
import com.nuance.nmdp.speechkit.SpeechKit;

/**
 * Created by blake on 8/1/14.
 */
public class SpeechKitHolder {
    private static SpeechKit speechKit;
    private SpeechKitHolder(){
    }
    public static SpeechKit getSpeechKit(Context context){
        if(speechKit == null){
            speechKit = SpeechKit.initialize(context, AppInfo.SpeechKitAppId, AppInfo.SpeechKitServer, AppInfo.SpeechKitPort, AppInfo.SpeechKitSsl, AppInfo.SpeechKitApplicationKey);
        }
        return speechKit;
    }
}
