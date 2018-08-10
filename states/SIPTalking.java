package states;

import java.io.IOException;
import java.io.PrintWriter;

import controller.AudioPlayer;
import controller.MessageReceiverAndTranslator;

public class SIPTalking extends SIPEngaged{
	
    public SIPTalking(){
    	System.out.println("in talking");
    	AudioPlayer.startPlayer();
    }

    @Override
    public SIPState receiveBYE(PrintWriter out) {
    	AudioPlayer.close();
		out.println(MessageTranslator.TranslateToSend(SIPMessages.SEND_OK));    		
    	out.flush();

    	MessageReceiverAndTranslator.closeEveryThing();
    	
    	return new SIPWaiting();
    }

    @Override
    public  SIPState receiveINTERPTION(PrintWriter out) {
    	out.println(MessageTranslator.TranslateToSend(SIPMessages.SEND_BUSY));
    	out.flush();
    	return this;
	}
    
    @Override
    public SIPState sendBYE(PrintWriter out) {
    	AudioPlayer.close();

		out.println(MessageTranslator.TranslateToSend(SIPMessages.SEND_BYE)); 
		out.flush();
        return new SIPDisconnect();
    }
    

}
