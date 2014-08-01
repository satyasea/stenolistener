package com.blake.voice.tasks;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by blake on 6/28/14.
 */
public class PhoneActionCommandList {

    public static List<String> phoneCommands;

    static {
        phoneCommands = new ArrayList<String>();
        phoneCommands.add("phone");
        phoneCommands.add("phoned");
        phoneCommands.add("telephone");
        phoneCommands.add("telephoned");
        phoneCommands.add("dial");
        phoneCommands.add("dialed");
        phoneCommands.add("call");
        phoneCommands.add("called");
        phoneCommands.add("cull");
        phoneCommands.add("culled");
    }

    public static List getPhoneCommandList(){
        return phoneCommands;
    }

}
