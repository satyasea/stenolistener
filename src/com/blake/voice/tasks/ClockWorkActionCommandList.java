package com.blake.voice.tasks;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by blake on 6/28/14.
 */
public class ClockWorkActionCommandList {

    public static List<String> clockCommands;

    static {
        clockCommands = new ArrayList<String>();
        clockCommands.add("clock");
        clockCommands.add("punch");
        clockCommands.add("check");
        clockCommands.add("log");
    }

    public static List getClockCommandList(){
        return  clockCommands;
    }

}
