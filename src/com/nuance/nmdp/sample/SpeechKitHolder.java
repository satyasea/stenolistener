package com.nuance.nmdp.sample;

import android.app.Activity;
import com.nuance.nmdp.speechkit.SpeechKit;

/**
 * Created by blake on 8/1/14.
 */
public class SpeechKitHolder {
    private static SpeechKit speechKit;
    private SpeechKitHolder(){
    }
    public static SpeechKit getSpeechKit(Activity owner){
        if(speechKit == null){
            speechKit = SpeechKit.initialize(owner.getApplication().getApplicationContext(), AppInfo.SpeechKitAppId, AppInfo.SpeechKitServer, AppInfo.SpeechKitPort, AppInfo.SpeechKitSsl, AppInfo.SpeechKitApplicationKey);
        }
        return speechKit;
    }
}
