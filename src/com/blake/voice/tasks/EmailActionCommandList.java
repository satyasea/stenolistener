package com.blake.voice.tasks;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by blake on 6/28/14.
 */
public class EmailActionCommandList {

    public static List<String> emailCommands;

    static {
         emailCommands = new ArrayList<String>();
         emailCommands.add("email");
         emailCommands.add("mail");
         emailCommands.add("message");
        emailCommands.add("emailed");
        emailCommands.add("mailed");
        emailCommands.add("messaged");
    }

    public static List getEmailCommandList(){
        return  emailCommands;
    }

}
