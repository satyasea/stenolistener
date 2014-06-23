package com.blake.voice.tasks;




import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by blake on 6/23/14.
 */
public class ActionCommand implements Serializable {

    private String action;
    private String recipient;
    private int category = 0;
    public static final int COMMAND_EMAIL = 1;
    public static final int COMMAND_PHONE = 2;
    public Date actionTime = null;

    public Date getActionTime() {
        return actionTime;
    }

    public void setActionTime(Date actionTime) {
        this.actionTime = actionTime;
    }

    public void setCommand(String action){
        this.action = action;
    }
    public void setRecipient(String recipient){
        this.recipient=recipient;
    }

    public String getRecipient() {
        return recipient;
    }

    public ActionCommand(){

    }

    public ActionCommand(Date time, String action, String recipient) {
        this.action = action;
        this.recipient=recipient;
        actionTime=time;
        if(action.equalsIgnoreCase("email")) category = COMMAND_EMAIL;
        if(action.equalsIgnoreCase("phone")) category = COMMAND_PHONE;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }



    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
