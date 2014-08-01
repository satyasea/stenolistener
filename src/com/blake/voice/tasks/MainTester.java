package com.blake.voice.tasks;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by blake on 6/28/14.
 */
public class MainTester {

    static String USERNAME = "Bob";

    public static void main(String args[]){
        System.out.println("Booo");
      processVoiceInput("say my name bitch");
        System.out.println("");

        processVoiceInput("tell me my history");
    }

    public static void processVoiceInput(String cmd){
        String[] inputArray = cmd.toLowerCase().split(" ");
        String action = inputArray[0];
        // build recipient from first and last names
        StringBuilder sb = new StringBuilder();
        for(int i = 1; i < inputArray.length; i++){
            sb.append(inputArray[i]);
            sb.append(" ");
        }
        String rec = sb.toString().trim();
        List<String> emailActions = EmailActionCommandList.getEmailCommandList();
        List<String> phoneActions = PhoneActionCommandList.getPhoneCommandList();
        ActionCommand command;
        if(emailActions.contains(action)){
            command = new ActionCommand(new Date(), "email", rec);
            System.out.print(command.getActionTime() + " " + command.getAction() + " " + command.getRecipient());
        }
        else if(phoneActions.contains(action)){
            command = new ActionCommand(new Date(), "phone", rec);
            System.out.print(command.getActionTime() + " " + command.getAction() + " " + command.getRecipient());
        }else{
            //loose sentence checking....e
            Map<String, String> actionCommandMap = QuestionResponseMaps.getCommandMap();
            Map<String, String> actionResponseMap = QuestionResponseMaps.getResponseMap();
            Set<String> keys = actionCommandMap.keySet();
            for(String key: keys){
                if(cmd.equalsIgnoreCase(key)){
                    String value = actionCommandMap.get(key);
                    String response = actionResponseMap.get(value);
                    System.out.print("General Questions: " + key + "=" + response + ", " + USERNAME);
                    break;
                }
            }
        }
    }
}
