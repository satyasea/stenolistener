package com.blake.voice.tasks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by blake on 6/28/14.
 */
public class QuestionResponseMaps {

    private static Map<String, String> commands;

    private static Map<String, String> response;
    static String WHOAMI_PROFANE = "who am I profane";
    static String WHOAMI = "who am I";
    static String HISTORY= "history";


    static {
         commands = new HashMap<String, String>();
        commands.put("say my name bitch", WHOAMI_PROFANE);
        commands.put("what's my name",WHOAMI );
        commands.put("who am I", WHOAMI );
        commands.put("what have I done", HISTORY);
        commands.put("show me my history", HISTORY);
        commands.put("give me my command history", HISTORY);
        commands.put("tell me my history", HISTORY);


        response = new HashMap<String, String>();
        response.put(WHOAMI_PROFANE, "No need to use such harsh language");
        response.put(WHOAMI,"I know about you" );
        response.put( HISTORY,"Your transaction history is");


    }

    public static Map<String, String> getCommandMap(){
        return commands;
    }

    public static Map<String, String> getResponseMap(){
        return response;
    }


}
